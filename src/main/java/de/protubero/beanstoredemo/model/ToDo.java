package de.protubero.beanstoredemo.model;

import java.time.Instant;

import de.protubero.beanstore.base.AbstractEntity;
import de.protubero.beanstore.base.Entity;

@Entity(alias = "todo")
public class ToDo extends AbstractEntity {

	private String text;
	private boolean done;
	private Instant createdAt;

	public Instant getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
	}

	public boolean isDone() {
		return done;
	}

	public void setDone(boolean done) {
		this.done = done;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}


}
