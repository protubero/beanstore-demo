package de.protubero.beanstoredemo.framework;

import java.util.function.Consumer;

import de.protubero.beanstore.builder.MigrationTransaction;

public interface BeanStoreMigration extends Consumer<MigrationTransaction> {

}
