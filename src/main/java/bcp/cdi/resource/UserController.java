package bcp.cdi.resource;

import static bcp.cdi.util.LogUtil.CONSTRUCTOR_MSG;
import static bcp.cdi.util.LogUtil.identity;

import java.time.LocalDateTime;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import bcp.cdi.conf.RequestProduced;
import bcp.cdi.service.Logged;
import bcp.cdi.service.UserService;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Path("users")
@ApplicationScoped
@Logged
@NoArgsConstructor
@Slf4j
public class UserController {

	@Inject
	@RequestProduced
	// request scoped bean
	private UserService usRequest;

	// this will be injected in the constructor with
	// this controller scope (application)
	private UserService usDependent;
	
	@Inject
	public UserController(UserService us) {
		this.usDependent = us;
		log.debug(CONSTRUCTOR_MSG, identity(this));
	}
	
	@Path("/dependent")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String method01() {
		usDependent.doSomething();
		return "DONE " + LocalDateTime.now();
	}
	
	@Path("/request")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String method02() {
		usRequest.doSomething();
		return "DONE " + LocalDateTime.now();
	}

}
