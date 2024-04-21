package de.protubero.beanstoredemo.service;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.protubero.beanstoredemo.beans.TeamMember;
import de.protubero.beanstoredemo.framework.AbstractService;

@RestController
@RequestMapping("/member")
public class TeamMemberService extends AbstractService<TeamMember> {

	public TeamMemberService() {
		super(TeamMember.class);
	}

	
}
