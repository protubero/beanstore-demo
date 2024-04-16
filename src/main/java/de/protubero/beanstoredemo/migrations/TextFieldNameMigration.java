package de.protubero.beanstoredemo.migrations;

import de.protubero.beanstore.builder.MigrationTransaction;
import de.protubero.beanstoredemo.framework.BeanStoreMigration;
import de.protubero.beanstoredemo.framework.Migration;

// @Migration(name="rename-text-property", order=1)
public class TextFieldNameMigration implements BeanStoreMigration {


	@Override
	public void accept(MigrationTransaction tx) {
		tx.snapshot().mapEntity("task").forEach(task ->  {
			var upd = tx.update(task);
			upd.set("text", task.get("text2"));
			upd.remove("text2");
		});
	}

}
