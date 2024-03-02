package de.protubero.beanstoredemo.framework;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import de.protubero.beanstore.api.BeanStore;
import de.protubero.beanstore.builder.BeanStoreBuilder;
import de.protubero.beanstore.entity.Entity;
import de.protubero.beanstore.persistence.api.KryoConfig;
import de.protubero.beanstore.persistence.kryo.KryoConfiguration;
import de.protubero.beanstore.persistence.kryo.KryoPersistence;
import de.protubero.beanstore.pluginapi.BeanStorePlugin;

@Configuration
public class BeanStoreConfiguration {

	public static Logger log = LoggerFactory.getLogger(BeanStoreConfiguration.class);

	@Value("${beanstore.file}")
	private String filename;

	@Autowired
	private BeanStoreInitializer storeInitializer;

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private ClassScanner classScanner;
	
	private List<Runnable> initInvocations = new ArrayList<>();

	private static final String[] SCANNED_PACKAGES = { "de.protubero.beanstoredemo" };

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Bean
	public BeanStore createBeanStore() {
		log.info("Build Kryo Configuration");

		KryoConfiguration kryoConfig = KryoConfiguration.create();
		Set<String> kryoConfClassSet = classScanner.findAnnotatedClasses(KryoConfig.class, SCANNED_PACKAGES);
		kryoConfClassSet.forEach(cls -> {
			Class<?> clazz = classByName(cls);
			kryoConfig.register(clazz);
		});

		log.info("Build Kryo Persistence");
		File dataFile = new File(filename);
		KryoPersistence persistence = KryoPersistence.of(dataFile, kryoConfig);

		log.info("Build Bean Store Builder");
		BeanStoreBuilder builder = BeanStoreBuilder.init(persistence);
		Set<String> dataBeanClassSet = classScanner.findAnnotatedClasses(Entity.class, SCANNED_PACKAGES);
		dataBeanClassSet.forEach(cls -> {
			Class<?> clazz = classByName(cls);
			builder.registerEntity((Class) clazz);
		});

		builder.initNewStore(storeInitializer);

		// Migrations
		List<MigrationNode> migrationList = new ArrayList<>();
		Set<String> migrationBeanClassSet = classScanner.findAnnotatedClasses(Migration.class, SCANNED_PACKAGES);
		migrationBeanClassSet.forEach(cls -> {
			Class<?> clazz = classByName(cls);
			Migration annotation = clazz.getAnnotation(Migration.class);
			BeanStoreMigration migration = null;
			try {
				Constructor<?> noArgsConstructor = clazz.getConstructor();
				migration = (BeanStoreMigration) noArgsConstructor.newInstance();
			} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException
					| IllegalArgumentException | InvocationTargetException e) {
				throw new RuntimeException(e);
			}

			migrationList.add(new MigrationNode(annotation.name(), annotation.order(), migration));
		});
		migrationList.sort(Comparator.comparingInt(m -> m.getOrder()));
		migrationList.forEach(m -> {
			builder.addMigration(m.getName(), m.getMigration());
		});

		
		// Callbacks
//		builder.addPlugin(new BeanStorePlugin() {
//			@Override
//			public void onEndCreate(BeanStore beanStore) {
//				initMethods.forEach(m -> {
//					try {
//						m.invoke();
//					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
//						throw new RuntimeException(e);
//					}
//				});
//			};
//		});
		Collection<BeanStorePlugin> plugins = applicationContext.getBeansOfType(BeanStorePlugin.class).values();
		plugins.forEach(plugin -> {
			log.info("registering plugin " + plugin);
			builder.addPlugin(plugin);
//			for(Method method : co.getClass().getDeclaredMethods()) {
//				BeanStoreInitialized initAnnotation = method.getAnnotation(BeanStoreInitialized.class);
//				if (initAnnotation != null) {
//					Class<?>[] paramTypes = method.getParameterTypes();
//					if (paramTypes.length != 1 || !BeanStore.class.isAssignableFrom(paramTypes[0])) {
//						throw new RuntimeException("Invalid parameters of 'BeanStoreInitialized' annotated method " + method.getName());
//					}
//					initMethods.add(method);
//				}
//			}
		});
		
		return builder.build();
	}

	private Class<?> classByName(String cls) {
		Class<?> clazz;
		try {
			clazz = Class.forName(cls);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
		return clazz;
	}

}
