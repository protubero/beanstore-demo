package de.protubero.beanstoredemo.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.protubero.beanstoredemo.beans.TeamMember;
import de.protubero.beanstoredemo.framework.AbstractApi;

@RestController
@RequestMapping("/member")
public class TeamMemberApi extends AbstractApi<TeamMember> {

	public TeamMemberApi() {
		super(TeamMember.class);
	}

	
}
