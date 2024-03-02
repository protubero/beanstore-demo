package de.protubero.beanstoredemo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import de.protubero.beanstore.entity.AbstractPersistentObject;
import de.protubero.beanstore.plugins.search.BeanStoreSearchPlugin;
import de.protubero.beanstoredemo.beans.Task;

@RestController
public class SearchApi {

	@Autowired
	private BeanStoreSearchPlugin searchPlugin;
	
	
	@GetMapping(value = "/search")
    public List<AbstractPersistentObject> findById(@RequestParam("text") String text) {
        return searchPlugin.search(text);
    }
	
}
