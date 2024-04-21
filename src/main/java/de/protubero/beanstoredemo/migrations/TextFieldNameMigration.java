package de.protubero.beanstoredemo.migrations;

import de.protubero.beanstore.builder.MigrationTransaction;
import de.protubero.beanstorespring.BeanStoreMigration;
import de.protubero.beanstorespring.Migration;

@Migration(name="rename-text-property", order=1)
public class TextFieldNameMigration implements BeanStoreMigration {


	@Override
	public void accept(MigrationTransaction tx) {
		renameField(tx, "book", "title", "mainTitle");
	}


}
