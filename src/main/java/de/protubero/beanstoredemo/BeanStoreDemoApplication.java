package de.protubero.beanstoredemo;

import java.time.LocalDateTime;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import de.protubero.beanstore.plugins.history.BeanStoreHistoryPlugin;
import de.protubero.beanstore.plugins.search.BeanStoreSearchPlugin;
import de.protubero.beanstore.plugins.txlog.BeanStoreTransactionLogPlugin;
import de.protubero.beanstore.plugins.validate.BeanValidationPlugin;
import de.protubero.beanstoredemo.beans.Address;
import de.protubero.beanstoredemo.beans.Priority;
import de.protubero.beanstoredemo.beans.Task;
import de.protubero.beanstoredemo.beans.TeamMember;
import de.protubero.beanstoredemo.framework.BeanStoreInitializer;

@SpringBootApplication
public class BeanStoreDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(BeanStoreDemoApplication.class, args);
	}
	
	@Bean
	public BeanStoreInitializer storeInitializer() {
		return tx -> {
			Task newTask = tx.create(Task.class);
			newTask.setText2("Eat lunch");
			newTask.setCreatedAt(LocalDateTime.now());
			newTask.setPriority(Priority.Today);
			
			TeamMember teamMember = tx.create(TeamMember.class);
			teamMember.setAddress(new Address("Koenigsallee", "Berlin"));
		};
	}
	
	@Bean
	public BeanStoreSearchPlugin searchPlugin() {
		BeanStoreSearchPlugin plugin = new BeanStoreSearchPlugin();
		
		plugin.register(Task.class, task -> {
			return task.getText2() + " elkos";
		});
		
		return plugin;
	}
	
	@Bean
	public BeanValidationPlugin beanValidation() {
		return new BeanValidationPlugin();
	}
	
	@Bean
	public BeanStoreHistoryPlugin beanStoreHistory() {
		BeanStoreHistoryPlugin result = new BeanStoreHistoryPlugin();
		result.register("task", "employee");
		return result;
	}

	@Bean
	public BeanStoreTransactionLogPlugin txLogPlugin() {
		return new BeanStoreTransactionLogPlugin();
	}
	
}
