package de.protubero.beanstoredemo.app;

import static io.javalin.apibuilder.ApiBuilder.crud;
import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.post;

import java.io.File;
import java.time.Instant;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import de.protubero.beanstore.base.AbstractPersistentObject;
import de.protubero.beanstore.base.BeanStoreInstanceSerializer;
import de.protubero.beanstore.init.BeanStoreFactory;
import de.protubero.beanstore.plugins.history.BeanStoreHistoryPlugin;
import de.protubero.beanstore.plugins.search.BeanStoreSearchPlugin;
import de.protubero.beanstore.plugins.txlog.BeanStoreTransactionLogPlugin;
import de.protubero.beanstore.store.InstanceNotFoundException;
import de.protubero.beanstore.writer.TransactionFailure;
import de.protubero.beanstoredemo.commands.AbstractCommand;
import de.protubero.beanstoredemo.model.ToDo;
import io.javalin.Javalin;
import io.javalin.plugin.json.JavalinJackson;

public class Application {

	private static long todoCount;

	public static void main(String[] args) {
        // Init bean store
        BeanStoreFactory factory = BeanStoreFactory.of(new File("c:/work/app.kryo"));
        
        // log transactions to slf4j logger
        factory.addPlugin(new BeanStoreTransactionLogPlugin());
        
        // tx history
        var historyPlugin = new BeanStoreHistoryPlugin();
        factory.addPlugin(historyPlugin);
        
        // register entities
        var todoEntity = factory.registerType(ToDo.class);
        
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
        });
        
        // add migrations
        factory.addMigration("myTextRename", tx -> {
        	if (tx.dataStore().exists("todo")) {
	        	tx.dataStore().objects("todo").forEach(obj -> {
	        		var upd = tx.update(obj);        		
	        		upd.put("myText", obj.get("text"));
	        		var origText = upd.remove("text");
	        		System.out.println("text=" + origText);
	        	});
        	}	
        });
        factory.addMigration("myTextRename2", tx -> {
        	if (tx.dataStore().exists("todo")) {
	        	tx.dataStore().objects("todo").forEach(obj -> {
	        		var upd = tx.update(obj);        		
	        		upd.put("text", obj.get("myText"));
	        		var origText = upd.remove("myText");
	        		System.out.println("text=" + origText);
	        	});
        	}	
        });
        
        var store = factory.create();        
        
        // total todo count
        todoCount = store.objects(ToDo.class).count();		
		store.onChangeInstanceAsync(ToDo.class, sit -> {
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
        store.verifyInstance(ToDo.class, bc -> {
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
        }).start(7000);

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
        		ctx.json(searchPlugin.search(ctx.queryParam("query")));            	
            });
            
            get("count", ctx -> {
            	ctx.json(todoCount);            	
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
