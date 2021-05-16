package de.protubero.beanstoredemo.app;

import java.time.Instant;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.node.ObjectNode;

import de.protubero.beanstore.init.BeanStore;
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
		newToDo.setText(text);
		newToDo.setCreatedAt(Instant.now());
		
		tx.execute();
		
		// return id of newly created instance
		ctx.header("newid", newToDo.id().toString());
	}

	@Override
	public void delete(Context ctx, String id) {
		var tx = store.transaction();
		
		tx.delete(ToDo.class, Long.valueOf(id));
		tx.execute();
	}

	@Override
	public void getAll(Context ctx) {
		ctx.json(store.objects(ToDo.class).collect(Collectors.toList()));
	}

	@Override
	public void getOne(Context ctx, String id) {
		ctx.json(store.find(ToDo.class, Long.valueOf(id)));
	}

	@Override
	public void update(Context ctx, String id) {
		ToDo todo = store.find(ToDo.class, Long.valueOf(id));
		var tx = store.transaction();
		
		ObjectNode node = ctx.bodyAsClass(ObjectNode.class);
		String text = node.get("text").asText();
		tx.update(todo).setText(text);

		tx.execute();
	}

}
