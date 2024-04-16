package de.protubero.beanstoredemo.api;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.protubero.beanstoredemo.beans.Employee;
import de.protubero.beanstoredemo.beans.Priority;
import de.protubero.beanstoredemo.beans.Task;
import de.protubero.beanstoredemo.callbacks.TaskPriorityStats;
import de.protubero.beanstoredemo.framework.AbstractApi;

@RestController
@RequestMapping("/tasks")
public class TaskApi extends AbstractApi<Task> {


	@Autowired
	private TaskPriorityStats counter;

	public TaskApi() {
		super(Task.class);
	}

	@GetMapping(value = "/stats")
	public Map<Priority, Integer> priorityStats() {
		return counter.getPriorityCountMap();
	}

}
