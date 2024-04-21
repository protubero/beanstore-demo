package de.protubero.beanstoredemo.service;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.protubero.beanstoredemo.beans.Employee;
import de.protubero.beanstoredemo.framework.AbstractService;

@RestController
@RequestMapping("/employee")
public class EmployeeService extends AbstractService<Employee> {

	public EmployeeService() {
		super(Employee.class);
	}

	@GetMapping(value = "/by-age-range/{from}/{to}")
	public List<Employee> employeesByAgeRange(@PathVariable("from") int fromAge, @PathVariable("to") int toAge) {
		return entityStore().stream().filter(
				e -> e.getAge() != null && (
						e.getAge().intValue() >= fromAge &&
						e.getAge().intValue() < toAge)).toList();
	}
	

	
}
