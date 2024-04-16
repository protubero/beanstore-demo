package de.protubero.beanstoredemo.migrations;

import de.protubero.beanstore.builder.MigrationTransaction;
import de.protubero.beanstoredemo.framework.BeanStoreMigration;
import de.protubero.beanstoredemo.framework.Migration;

// @Migration(name="firstname-not-null", order=1)
public class FirstNameNotNullMigration implements BeanStoreMigration {


	@Override
	public void accept(MigrationTransaction tx) {
		tx.snapshot().mapEntity("employee").forEach(employee ->  {
			if (employee.get("firstName") == null) {
				var upd = tx.update(employee);
				upd.set("firstName", "unknown");
			}
		});
	}

}
