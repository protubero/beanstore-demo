package de.protubero.beanstoredemo.beans;

import de.protubero.beanstore.entity.AbstractEntity;
import de.protubero.beanstore.entity.Entity;
import jakarta.validation.constraints.Positive;

@Entity(alias = "team-member")
public class TeamMember extends AbstractEntity {

	private Address address;
	
	@Positive
	private Integer age;

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

}
