package de.protubero.beanstoredemo.presentation;

import de.protubero.beanstore.entity.AbstractEntity;
import de.protubero.beanstore.entity.Entity;

@Entity(alias = "todo")
public class ToDo extends AbstractEntity {

	private String text;
	private User user;

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
	@Override
	public String toString() {
		return "id " + id().toString() + "/version " + super.version() + ": " + text;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}
