package bcp.cdi.resource;

import static bcp.cdi.conf.ConfigurationKeys.KEY_NAME_01;
import static bcp.cdi.util.LogUtil.CONSTRUCTOR_MSG;
import static bcp.cdi.util.LogUtil.identity;
import static bcp.cdi.util.LogUtil.logConstructorEvent;
import static bcp.cdi.util.LogUtil.logDestroyEvent;

import java.time.LocalDateTime;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import bcp.cdi.conf.ConfigValue;
import bcp.cdi.conf.RequestProduced;
import bcp.cdi.service.Logged;
import bcp.cdi.service.UserService;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Sample controller which uses the same service bean with different scopes
 * 
 * @author bogdan_paulon
 */
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

	// this will be injected in the constructor. Dependent beans 
	// aren't tied to a context. As far as CDI is concerned after being 
	// injected they are ordinary (strongly reachable) Java objects.
	private UserService usDependent;

	@Inject
	public UserController(UserService us, @ConfigValue(KEY_NAME_01) String configValue) {
		this.usDependent = us;
		log.debug(CONSTRUCTOR_MSG, identity(this));
		logConstructorEvent(log, this);
	}

	/**
	 * The UserService injected bean has this scope (ApplicationScoped)
	 * @return
	 */
	@Path("/dependent")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String callDependentScopedService() {
		usDependent.doSomething();
		return "DONE " + LocalDateTime.now();
	}

	/**
	 * The UserService injected bean has request scope (RequestScoped)
	 * even if this controller is ApplicationScoped
	 * 
	 * @return
	 */
	@Path("/request")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Inject
	public String callRequestScopedService(@ConfigValue(KEY_NAME_01) String configValue) {
		usRequest.doSomething();
		return "DONE " + LocalDateTime.now();
	}

	@PostConstruct
	public void postConstruct() {
		logConstructorEvent(log, this);
	}
	
	@PreDestroy
	public void destroy() {
		logDestroyEvent(this, this);
	}
}
