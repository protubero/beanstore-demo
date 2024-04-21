package de.protubero.beanstoredemo.migrations;

import de.protubero.beanstore.builder.MigrationTransaction;
import de.protubero.beanstoredemo.framework.BeanStoreMigration;
import de.protubero.beanstoredemo.framework.Migration;

// @Migration(name="task-deleted-not-null", order=2)
public class TaskDeletedNotNullMigration implements BeanStoreMigration {


	@Override
	public void accept(MigrationTransaction tx) {
		replaceNullValues(tx, "task", "deleted", Boolean.FALSE);
	}


}
