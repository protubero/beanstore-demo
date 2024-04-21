package de.protubero.beanstoredemo.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.protubero.beanstoredemo.beans.Employee;
import de.protubero.beanstoredemo.beans.Priority;
import de.protubero.beanstoredemo.beans.Task;
import de.protubero.beanstoredemo.framework.AbstractService;
import de.protubero.beanstoredemo.projections.TaskPriorityStats;

@RestController
@RequestMapping("/task")
public class TaskService extends AbstractService<Task> {


	@Autowired
	private TaskPriorityStats counter;

	public TaskService() {
		super(Task.class);
	}

	@GetMapping(value = "/stats")
	public Map<Priority, Integer> priorityStats() {
		return counter.getPriorityCountMap();
	}

}
