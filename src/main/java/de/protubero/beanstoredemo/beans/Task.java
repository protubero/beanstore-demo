package de.protubero.beanstoredemo.beans;

import java.time.LocalDateTime;

import de.protubero.beanstore.entity.AbstractEntity;
import de.protubero.beanstore.entity.Entity;
import de.protubero.beanstoredemo.framework.History;
import de.protubero.beanstoredemo.framework.Searchable;
import jakarta.validation.constraints.NotBlank;

@Entity(alias = "task")
@History
public class Task extends AbstractEntity implements Searchable {

	private Priority priority;
	private Boolean completed;
	
	@NotBlank(message="task text can not be empty")
	private String text;
	
	private LocalDateTime createdAt;
	private LocalDateTime completedAt;
	private Boolean deleted;
	private LocalDateTime deletedAt;

	public Priority getPriority() {
		return priority;
	}

	public void setPriority(Priority priority) {
		this.priority = priority;
	}

	public Boolean getCompleted() {
		return completed;
	}

	public void setCompleted(Boolean completed) {
		this.completed = completed;
	}

	

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getCompletedAt() {
		return completedAt;
	}

	public void setCompletedAt(LocalDateTime completedAt) {
		this.completedAt = completedAt;
	}

	public Boolean getDeleted() {
		return deleted;
	}

	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}

	public LocalDateTime getDeletedAt() {
		return deletedAt;
	}

	public void setDeletedAt(LocalDateTime deletedAt) {
		this.deletedAt = deletedAt;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	@Override
	public String toSearchString() {
		return text;
	}

}
