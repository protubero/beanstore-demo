package de.protubero.beanstoredemo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import de.protubero.beanstore.api.BeanStore;
import de.protubero.beanstore.api.EntityStoreSnapshot;
import de.protubero.beanstoredemo.beans.Task;

@RestController
@RequestMapping("/tasks")
public class TaskApi {

	@Autowired
	private BeanStore store;

	private EntityStoreSnapshot<Task> entityStore() {
		return store.snapshot().entity(Task.class);
	}
	
	@GetMapping
	public List<Task> tasks() {
		return entityStore().asList();
	}
	
	@GetMapping(value = "/{id}")
    public Task findById(@PathVariable("id") Long id) {
        return entityStore().find(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Long create(@RequestBody Task task) {
    	var tx = store.transaction();
    	tx.create(task);
    	tx.execute();
        return task.id();
    }

    @PutMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void update(@PathVariable( "id" ) Long id, @RequestBody Task task) {
    	var tx = store.transaction();
    	tx.update(task);
    	tx.execute();
    }

    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable("id") Long id) {
    	var tx = store.transaction();
    	tx.delete(Task.class, id);
    	tx.execute();
    }	
}
