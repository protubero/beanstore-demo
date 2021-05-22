package de.protubero.beanstoredemo.commands;

import de.protubero.beanstore.init.BeanStore;
import de.protubero.beanstoredemo.model.ToDo;

public class UpperCaseTodoText extends AbstractCommand {

	@Override
	public void accept(BeanStore store) {
		store.executeDeferred(ctx -> {
			var tx = ctx.transaction();

			store.reader().objects(ToDo.class).forEach(todo -> {
				if (todo.getText() != null) {
					var updToDo = tx.update(todo);
					updToDo.setText(updToDo.getText().toUpperCase());
				}
			});
			
			tx.execute();
		});
	}

}
