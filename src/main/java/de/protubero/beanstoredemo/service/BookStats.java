package de.protubero.beanstoredemo.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import de.protubero.beanstoredemo.projections.AuthorStats;

@RestController
public class BookStats {


	@Autowired
	private AuthorStats counter;

	
	@GetMapping(value = "/stats")
	public Map<String, Integer> authorStats() {
		return counter.getAuthorBookCountMap();
	}	
}
