package de.protubero.beanstoredemo.beans;

import de.protubero.beanstore.entity.AbstractEntity;
import de.protubero.beanstore.entity.Entity;
import jakarta.validation.constraints.NotNull;

@Entity(alias="employee")
public class Employee extends AbstractEntity {

	@NotNull(message = "first name must not be null")
	private String firstName;
	private String lastName;
	private Integer age;
	
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public Integer getAge() {
		return age;
	}
	public void setAge(Integer age) {
		this.age = age;
	}
	
}
