package de.protubero.beanstoredemo.commands;

import de.protubero.beanstore.api.BeanStore;
import de.protubero.beanstoredemo.model.ToDo;

public class LowerCaseTodoText extends AbstractCommand {

	@Override
	public void accept(BeanStore store) {
		store.locked(ctx -> {
			var tx = ctx.transaction();

			store.read().entity(ToDo.class).forEach(todo -> {
				if (todo.getText() != null) {
					var updToDo = tx.update(todo);
					updToDo.setText(updToDo.getText().toLowerCase());
				}
			});
			
			tx.execute();
		});
	}

}
