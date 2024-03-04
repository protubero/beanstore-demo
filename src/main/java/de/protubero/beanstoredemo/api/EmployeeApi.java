package de.protubero.beanstoredemo.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.protubero.beanstoredemo.beans.Employee;
import de.protubero.beanstoredemo.framework.AbstractApi;

@RestController
@RequestMapping("/employee")
public class EmployeeApi extends AbstractApi<Employee> {

	public EmployeeApi() {
		super(Employee.class);
	}

}
