package de.protubero.beanstoredemo.callbacks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.protubero.beanstore.api.BeanStore;
import de.protubero.beanstoredemo.beans.Task;
import jakarta.annotation.PostConstruct;

@Service
public class TaskCounter {

	@Autowired
	private BeanStore store;
	
	private long count;
	
	@PostConstruct
	public void onInit() {
		System.out.println("Version is " + store.snapshot().version());
		
		// initial count
		count = store.snapshot().entity(Task.class).stream().count();
		System.out.println("Initial count: " + count);
		
		store.callbacks().onChangeInstance(Task.class, evt -> {
			switch (evt.type()) {
			case Create:
				count++;
				break;
			case Update:
				// NOP
				break;
			case Delete:
				count--;
				break;
			}
		});
	}

	public long getCount() {
		return count;
	}
	
	
}
