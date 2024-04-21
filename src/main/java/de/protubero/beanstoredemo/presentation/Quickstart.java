package de.protubero.beanstoredemo.presentation;

import java.io.File;
import java.util.stream.Collectors;

import de.protubero.beanstore.api.BeanStore;
import de.protubero.beanstore.builder.BeanStoreBuilder;
import de.protubero.beanstore.entity.Keys;
import de.protubero.beanstore.persistence.kryo.KryoConfiguration;
import de.protubero.beanstore.persistence.kryo.KryoPersistence;
import de.protubero.beanstore.persistence.kryo.PropertyBeanSerializer;
import de.protubero.beanstore.plugins.txlog.BeanStoreTransactionLogPlugin;
import de.protubero.beanstore.tx.TransactionFailure;

public class Quickstart {

	private static void exec() {
		KryoConfiguration kryoConfig = KryoConfiguration.create();
		kryoConfig.register(User.class, PropertyBeanSerializer.class, 345);
		
		KryoPersistence persistence = KryoPersistence.of(new File("c:\\demo\\demo.bst"), kryoConfig);
		
		BeanStoreBuilder builder = BeanStoreBuilder.init(persistence);
		
		builder.registerEntity(ToDo.class);
		// builder.addMigration(null, null);
		// builder.addPlugin(new BeanStoreTransactionLogPlugin());
		// builder.initNewStore(null);
		// builder.registerMapEntity("todo");
		// builder.setAutoCreateEntities(true);

		BeanStore store = builder.build();
		store.callbacks().onChangeInstance(ToDo.class, evt -> {
			System.out.println("Old: " + evt.replacedInstance());
			System.out.println("New: " + evt.newInstance());
		});

		
//		store.locked(ctx -> {
//			var tx = store.transaction();
//			tx.update(Keys.key("todo", 0)).put("text", "xyz");
//			tx.execute();
//		});

		var tx = store.transaction();
		tx.update(Keys.key(ToDo.class, 0)).setText("345678");
		tx.execute();
		
		System.out.println("snapshot version = " + store.snapshot().version());
		store.snapshot().entity("todo").stream().forEach(System.out::println);
	}

	public static void main(String[] args) {
		try {
			exec();
		} catch (TransactionFailure txf) {
			System.out.println("Transaction Failure of type " + txf.getType());
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
		System.exit(0);
	}
	
}
