package de.protubero.beanstoredemo.framework;

import java.util.function.Consumer;

import de.protubero.beanstore.api.BeanStoreTransaction;

public interface BeanStoreInitializer extends Consumer<BeanStoreTransaction> {

}
