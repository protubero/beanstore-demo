package de.protubero.beanstoredemo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import de.protubero.beanstore.api.BeanStore;
import de.protubero.beanstore.api.EntityStoreSnapshot;
import de.protubero.beanstoredemo.beans.Task;
import de.protubero.beanstoredemo.beans.TeamMember;

@RestController
@RequestMapping("/member")
public class TeamMemberApi {

	@Autowired
	private BeanStore store;

	private EntityStoreSnapshot<TeamMember> entityStore() {
		return store.snapshot().entity(TeamMember.class);
	}
	
	@GetMapping
	public List<TeamMember> members() {
		return entityStore().asList();
	}
	
}
