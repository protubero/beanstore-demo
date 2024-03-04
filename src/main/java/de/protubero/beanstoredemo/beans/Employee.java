package de.protubero.beanstoredemo.beans;

import de.protubero.beanstore.entity.AbstractEntity;
import de.protubero.beanstore.entity.Entity;
import jakarta.validation.constraints.NotNull;

@Entity(alias="employee")
public class Employee extends AbstractEntity {

	@NotNull(message = "first name must not be null")
	private String firstName;
	private String lastName;
	
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
	
}
