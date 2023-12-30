package de.protubero.beanstoredemo.commands;

import de.protubero.beanstore.api.BeanStore;
import de.protubero.beanstoredemo.model.ToDo;

public class LowerCaseTodoText extends AbstractCommand {

	@Override
	public void accept(BeanStore store) {
		store.locked(ctx -> {
			var tx = ctx.get();

			tx.lockedStoreState().entity(ToDo.class).forEach(todo -> {
				if (todo.getText() != null) {
					var updToDo = tx.update(todo);
					updToDo.setText(todo.getText().toLowerCase());
				}
			});
			
			tx.execute();
		});
	}

}
