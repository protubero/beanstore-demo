package de.protubero.beanstoredemo.presentation;

import java.io.File;
import java.util.stream.Collectors;

import de.protubero.beanstore.api.BeanStore;
import de.protubero.beanstore.builder.BeanStoreBuilder;
import de.protubero.beanstore.entity.Keys;
import de.protubero.beanstore.persistence.kryo.KryoConfiguration;
import de.protubero.beanstore.persistence.kryo.KryoPersistence;
import de.protubero.beanstore.tx.TransactionFailure;

public class Quickstart {

	public static void main(String[] args) {
		KryoConfiguration kryoConfig = KryoConfiguration.create();
		KryoPersistence persistence = KryoPersistence.of(new File("c:\\work\\demo.bst"), kryoConfig);
		BeanStoreBuilder builder = BeanStoreBuilder.init(persistence);
		builder.registerEntity(ToDo.class);

		BeanStore store = builder.build();
		
		
//		var tx = store.transaction();		
//		ToDo updToDo = tx.update(Keys.versionKey(ToDo.class, 1, 1));
//		updToDo.setText("Hello   Manfred");
		try {
//			tx.execute();
			
			System.out.println("snapshot version = " + store.snapshot().version());
			var allToDos = store.snapshot().entity(ToDo.class).stream().collect(Collectors.toList());
			allToDos.forEach(System.out::println);		
		} catch (TransactionFailure txf) {
			System.out.println(txf.getType());
		}
		
	}

}
