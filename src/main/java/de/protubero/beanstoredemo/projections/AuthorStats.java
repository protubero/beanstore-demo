package de.protubero.beanstoredemo.projections;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.protubero.beanstore.api.BeanStore;
import de.protubero.beanstoredemo.beans.Book;
import jakarta.annotation.PostConstruct;

@Service
public class AuthorStats {

	@Autowired
	private BeanStore store;
	
	private Map<String, Integer> authorBookCountMap = new HashMap<>();
	
	@PostConstruct
	public void onInit() {
		System.out.println("Version is " + store.snapshot().version());

		
		// initial count
		store.snapshot().entity(Book.class).stream()
			.forEach(book -> {
				inc(book.getAuthor());
			});
		
		store.callbacks().onChangeInstance(Book.class, evt -> {
			switch (evt.type()) {
			case Create:
				if (evt.newInstance().getAuthor() != null) {
					inc(evt.newInstance().getAuthor());
				}
				break;
			case Update:
				if (!Objects.equals(evt.newInstance().getAuthor(), evt.replacedInstance().getAuthor())) {
					dec(evt.replacedInstance().getAuthor());
					inc(evt.newInstance().getAuthor());
				}
				break;
			case Delete:
				if (evt.replacedInstance().getAuthor() != null) {
					dec(evt.replacedInstance().getAuthor());
				}
				break;
			}
		});
	}

	private void dec(String author) {
		changeCount(author, -1);
	}

	private void changeCount(String author, int countDiff) {
		if (author == null) {
			author = "_null";
		}
		Integer currentCount = authorBookCountMap.get(author);
		if (currentCount == null) {
			authorBookCountMap.put(author, countDiff);
		} else {
			authorBookCountMap.put(author, currentCount + countDiff);
			
		}
	}

	private void inc(String author) {
		changeCount(author, 1);
	}

	public Map<String, Integer> getAuthorBookCountMap() {
		return authorBookCountMap;
	}


	
	
}
