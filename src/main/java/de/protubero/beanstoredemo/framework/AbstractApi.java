package de.protubero.beanstoredemo.framework;

import java.beans.PropertyDescriptor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.protubero.beanstore.api.BeanStore;
import de.protubero.beanstore.api.EntityStoreSnapshot;
import de.protubero.beanstore.entity.AbstractEntity;
import de.protubero.beanstore.entity.EntityCompanion;
import de.protubero.beanstore.store.InstanceNotFoundException;
import jakarta.servlet.http.HttpServletResponse;

public abstract class AbstractApi<T extends AbstractEntity> {

	@Autowired
	protected BeanStore store;

	@Autowired
	protected ObjectMapper objectMapper;
	
	protected Class<T> beanClass;

	public AbstractApi(Class<T> beanClass) {
		this.beanClass = Objects.requireNonNull(beanClass);
	}
	
	protected EntityStoreSnapshot<T> entityStore() {
		return store.snapshot().entity(beanClass);
	}
	
	@GetMapping
	public List<T> tasks() {
		return entityStore().asList();
	}
	
	@GetMapping(value = "/{id}")
	public T findById(@PathVariable("id") Long id) {
		try {
			return entityStore().find(id);
		} catch (InstanceNotFoundException instanceNotFoundException) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND,  beanClass.getSimpleName() + " not found");
		}
	}
	
	@DeleteMapping(value = "/{id}")
	@ResponseStatus(HttpStatus.OK)
	public void delete(@PathVariable("id") Long id) {
		try {
			store.delete(beanClass, id);
		} catch (InstanceNotFoundException instanceNotFoundException) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND,  beanClass.getSimpleName() + " not found");
		}
	}
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public void create(@RequestBody T instance, HttpServletResponse response) {
		try {
			store.create(instance);
			
			// TODO: complete URI
			response.addHeader("Location", String.valueOf(instance.id()));
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

	@PutMapping(value = "/{id}")
	@ResponseStatus(HttpStatus.OK)
	public void update(@PathVariable("id") Long id, @RequestBody JsonNode jsonNode) {
		if (!jsonNode.isObject()) {
			throw new RuntimeException("json has to be an object");
		}
		
		EntityCompanion<T> companion = (EntityCompanion<T>) entityStore().meta();
		Map<String, Object> updatedFields = new HashMap<>();
		jsonNode.fields().forEachRemaining(field -> {
			PropertyDescriptor propDesc = companion.propertyDescriptorOf(field.getKey());
			if (propDesc == null) {
				throw new RuntimeException("Invalid field name: " + field.getKey());
			}
			Object value;
			try {
				value = objectMapper.treeToValue(field.getValue(), propDesc.getPropertyType());
			} catch (JsonProcessingException | IllegalArgumentException e) {
				throw new RuntimeException("Error reading json", e);
			}
			
			updatedFields.put(field.getKey(), value);
		});	
		
		store.update(beanClass, id, updatedFields);
	}
	
	
}
