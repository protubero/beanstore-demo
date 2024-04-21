package de.protubero.beanstoredemo.beans;

import de.protubero.beanstore.entity.AbstractEntity;
import de.protubero.beanstore.entity.Entity;
import de.protubero.beanstoredemo.framework.History;
import de.protubero.beanstoredemo.framework.Searchable;

@Entity(alias="book")
@History
public class Book extends AbstractEntity implements Searchable {

	private String mainTitle;
	private String author;

	public String getMainTitle() {
		return mainTitle;
	}

	public void setMainTitle(String title) {
		this.mainTitle = title;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	@Override
	public String toSearchString() {
		return String.valueOf(author) +  " " + String.valueOf(mainTitle);
	}
}
