package de.protubero.beanstoredemo.app;

import java.time.Instant;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.node.ObjectNode;

import de.protubero.beanstore.api.BeanStore;
import de.protubero.beanstore.api.EntityState;
import de.protubero.beanstoredemo.model.ToDo;
import io.javalin.apibuilder.CrudHandler;
import io.javalin.http.Context;

public class RestController implements CrudHandler {

	private BeanStore store;
	
	public RestController(BeanStore store) {
		this.store = store;
	}

	@Override
	public void create(Context ctx) {
		var tx = store.transaction();
		
		ToDo newToDo = tx.create(ToDo.class);
		ObjectNode node = ctx.bodyAsClass(ObjectNode.class);
		String text = node.get("text").asText();
		if (node.get("done") != null) {
			boolean done = node.get("done").asBoolean();
			newToDo.setDone(done);
		}
			
		newToDo.setText(text);
		newToDo.setCreatedAt(Instant.now());
		
		tx.executeBlocking();
		
		// return id of newly created instance
		ctx.header("newid", newToDo.id().toString());
	}

	@Override
	public void delete(Context ctx, String id) {
		var tx = store.transaction();
		
		tx.delete(ToDo.class, Long.valueOf(id));
		tx.executeBlocking();
	}

	@Override
	public void getAll(Context ctx) {
		ctx.json(todoStore().stream().collect(Collectors.toList()));
	}

	private EntityState<ToDo> todoStore() {
		return store.state().entity(ToDo.class);
	}

	@Override
	public void getOne(Context ctx, String id) {
		ctx.json(todoStore().find(Long.valueOf(id)));
	}

	@Override
	public void update(Context ctx, String id) {
		ToDo todo = todoStore().find(Long.valueOf(id));
		var tx = store.transaction();
		
		ObjectNode node = ctx.bodyAsClass(ObjectNode.class);
		ToDo upd = tx.update(todo);
		if (node.get("text") != null) {
			String text = node.get("text").asText();
			upd.setText(text);
		}
		if (node.get("done") != null) {
			boolean done = node.get("done").asBoolean();
			upd.setDone(done);
		}

		tx.executeBlocking();
	}

}
