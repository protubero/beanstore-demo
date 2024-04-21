package de.protubero.beanstoredemo.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.protubero.beanstoredemo.beans.Book;
import de.protubero.beanstoredemo.beans.Priority;
import de.protubero.beanstoredemo.framework.AbstractService;
import de.protubero.beanstoredemo.projections.AuthorStats;

@RestController
@RequestMapping("/book")
public class BookService extends AbstractService<Book> {



	public BookService() {
		super(Book.class);
	}


	
	



}
