package de.protubero.beanstoredemo.app;

import static io.javalin.apibuilder.ApiBuilder.crud;
import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.post;

import java.io.File;
import java.time.Instant;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import de.protubero.beanstore.builder.BeanStoreBuilder;
import de.protubero.beanstore.entity.AbstractPersistentObject;
import de.protubero.beanstore.entity.BeanStoreInstanceSerializer;
import de.protubero.beanstore.plugins.history.BeanStoreHistoryPlugin;
import de.protubero.beanstore.plugins.search.BeanStoreSearchPlugin;
import de.protubero.beanstore.plugins.txlog.BeanStoreTransactionLogPlugin;
import de.protubero.beanstore.store.InstanceNotFoundException;
import de.protubero.beanstore.tx.TransactionFailure;
import de.protubero.beanstoredemo.commands.AbstractCommand;
import de.protubero.beanstoredemo.model.ToDo;
import de.protubero.beanstoredemo.model.TodoCount;
import io.javalin.Javalin;
import io.javalin.plugin.json.JavalinJackson;

public class Application {

	private static long todoCount;

	public static void main(String[] args) {
		start(new File("c:/work/app.kryo"), 7000);
	}
		
	public static void start(File file, int port) {
        // Init bean store
        BeanStoreBuilder factory = BeanStoreBuilder.init(file);
        
        // log transactions to slf4j logger
        factory.addPlugin(new BeanStoreTransactionLogPlugin());
        
        // tx history
        var historyPlugin = new BeanStoreHistoryPlugin();
        factory.addPlugin(historyPlugin);
        
        // register entities
        var todoEntity = factory.registerEntity(ToDo.class);
        
        // configure full text search
        BeanStoreSearchPlugin searchPlugin = new BeanStoreSearchPlugin();
        factory.addPlugin(searchPlugin);
        searchPlugin.register(todoEntity, todo -> {
        	return todo.getText();
        });
                
        // Initialize a newly created store
        factory.initNewStore(tx -> {
        	var newTodo = tx.create(ToDo.class);
        	newTodo.setCreatedAt(Instant.now());
        	newTodo.setText("Read BeanStore docs");
        	newTodo.setDone(false);
        });
        
        // add migrations - sample code
        /*
        factory.addMigration("myTextRename", tx -> {
        	if (tx.read().entityOptional("todo").isPresent()) {
	        	tx.read().entity("todo").forEach(obj -> {
	        		var upd = tx.update(obj);        		
	        		upd.put("myText", obj.get("text"));
	        		var origText = upd.remove("text");
	        		System.out.println("text=" + origText);
	        	});
        	}	
        });
        factory.addMigration("myTextRename2", tx -> {
        	if (tx.read().entityOptional("todo").isPresent()) {
	        	tx.read().entity("todo").forEach(obj -> {
	        		var upd = tx.update(obj);        		
	        		upd.put("text", obj.get("myText"));
	        		var origText = upd.remove("myText");
	        		System.out.println("text=" + origText);
	        	});
        	}	
        });
        */
        
        var store = factory.create();        
        
        // total todo count
        todoCount = store.state().entity(ToDo.class).count();		
		store.callbacks().onChangeInstanceAsync(ToDo.class, sit -> {
			switch (sit.type()) {
			case Create:
				todoCount++;
				break;
			case Delete:
				todoCount--;
				break;
			case Update:
				// ignore
				break;
			}
		});        
        
        
        // check invariant
        store.callbacks().verifyInstance(ToDo.class, bc -> {
        	if (bc.newInstance().getText() != null) {
        		if (bc.newInstance().getText().contains("invalid")) {
        			throw new RuntimeException("Invalid todo text: " + bc.newInstance().getText());
        		}
        	}
        });
        
        // start server
        Javalin app = Javalin.create(cfg -> {
        	cfg.enableCorsForAllOrigins();
        	cfg.showJavalinBanner = false;
        	cfg.enableDevLogging();
        }).start(port);

        // register default serializer for beanstore beans 
        SimpleModule module = new SimpleModule();
        module.addSerializer(AbstractPersistentObject.class, new BeanStoreInstanceSerializer());        
        JavalinJackson.getObjectMapper().registerModule(module);        
        JavalinJackson.getObjectMapper().registerModule(new JavaTimeModule());        
        
        app.exception(TransactionFailure.class, (e, ctx) -> {
        	switch (e.getType()) {
        	case INSTANCE_NOT_FOUND:
        		ctx.status(404);
			default:
				throw e;
        	}
        });

        app.exception(InstanceNotFoundException.class, (e, ctx) -> {
      		ctx.status(404);
        });
        
        app.routes(() -> {
        	// REST interface
            crud("todos/:todo-id", new RestController(store));
            
            // Fulltext Search
            get("search", ctx -> {
        		String queryParam = ctx.queryParam("query");
        		System.out.println(queryParam);
				ctx.json(searchPlugin.search(queryParam));            	
            });
            
            get("count", ctx -> {
            	ctx.json(new TodoCount(todoCount));            	
            });
            
            // CQRS command interface
            post("command", ctx -> {
            	ctx.bodyAsClass(AbstractCommand.class).accept(store);            	
            });
            
            // Todo history
            get("history/:todo-id", ctx -> {
        		Long id = Long.valueOf(ctx.pathParam("todo-id"));
        		
        		ctx.json(historyPlugin.changes("todo", id.longValue()));
            });
        });        
        
        
    }	
}
