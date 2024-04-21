package de.protubero.beanstoredemo.service;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.protubero.beanstoredemo.beans.Book;
import de.protubero.beanstorespring.AbstractService;

@RestController
@RequestMapping("/book")
public class BookService extends AbstractService<Book> {



	public BookService() {
		super(Book.class);
	}


	
	



}
