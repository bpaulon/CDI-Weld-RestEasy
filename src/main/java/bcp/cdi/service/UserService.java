package bcp.cdi.service;

import static bcp.cdi.util.LogUtil.*;

import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import bcp.cdi.conf.RequestProduced;
import lombok.extern.slf4j.Slf4j;

@Dependent
@Slf4j
public class UserService {

	@Inject
	@RequestProduced
	private CloseableResource resource;

	public UserService() {
		log.debug(CONSTRUCTOR_MSG, identity(this));
	}

	public void close() {
	}

	public void doSomething() {
		log.debug("doSomething called: {}", identity(this));
		resource.doSomething();
	}

	@PreDestroy
	public void destroy() {
		log.debug(PREDESTROY_MSG, identity(this));
	}

}
