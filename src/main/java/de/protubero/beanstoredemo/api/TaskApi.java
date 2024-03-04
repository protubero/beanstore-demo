package de.protubero.beanstoredemo.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.protubero.beanstoredemo.beans.Employee;
import de.protubero.beanstoredemo.beans.Task;
import de.protubero.beanstoredemo.callbacks.TaskCounter;
import de.protubero.beanstoredemo.framework.AbstractApi;

@RestController
@RequestMapping("/tasks")
public class TaskApi extends AbstractApi<Task> {


	@Autowired
	private TaskCounter counter;

	public TaskApi() {
		super(Task.class);
	}

	@GetMapping(value = "/count")
	public Long count() {
		return counter.getCount();
	}

	@GetMapping(value = "/test")
	public void test(@RequestBody Employee employee) {
		System.out.println(employee);
	}
}
