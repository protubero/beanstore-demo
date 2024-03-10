package de.protubero.beanstoredemo.callbacks;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.protubero.beanstore.api.BeanStore;
import de.protubero.beanstoredemo.beans.Priority;
import de.protubero.beanstoredemo.beans.Task;
import jakarta.annotation.PostConstruct;

@Service
public class TaskPriorityStats {

	@Autowired
	private BeanStore store;
	
	private Map<Priority, Integer> priorityCountMap = new HashMap<>();
	
	@PostConstruct
	public void onInit() {
		System.out.println("Version is " + store.snapshot().version());

		for (Priority prio : Priority.values()) {
			priorityCountMap.put(prio, 0);		
		}
		
		// initial count
		store.snapshot().entity(Task.class).stream()
			.forEach(task -> {
				if (task.getPriority() != null) {
					inc(task.getPriority());
				}	
			});
		
		store.callbacks().onChangeInstance(Task.class, evt -> {
			switch (evt.type()) {
			case Create:
				if (evt.newInstance().getPriority() != null) {
					inc(evt.newInstance().getPriority());
				}
				break;
			case Update:
				if (!Objects.equals(evt.newInstance().getPriority(), evt.replacedInstance().getPriority())) {
					dec(evt.replacedInstance().getPriority());
					inc(evt.newInstance().getPriority());
				}
				break;
			case Delete:
				if (evt.replacedInstance().getPriority() != null) {
					dec(evt.replacedInstance().getPriority());
				}
				break;
			}
		});
	}

	private void dec(Priority priority) {
		priorityCountMap.put(priority,  priorityCountMap.get(priority).intValue() - 1);
	}

	private void inc(Priority priority) {
		priorityCountMap.put(priority,  priorityCountMap.get(priority).intValue() + 1);
	}

	public Map<Priority, Integer> getPriorityCountMap() {
		return priorityCountMap;
	}

	
	
}
