package bcp.cdi.service;

import static bcp.cdi.conf.ConfigurationKeys.KEY_NAME_01;
import static bcp.cdi.util.LogUtil.*;

import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import bcp.cdi.conf.ConfigValue;
import bcp.cdi.conf.RequestProduced;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Dependent
@Slf4j
@NoArgsConstructor
public class UserService {

	@Inject
	@RequestProduced
	private CloseableResource resource;

	@Inject
	public UserService(@ConfigValue(KEY_NAME_01) String configValue) {
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
		logDestroyEvent(this, this);
		log.debug(PREDESTROY_MSG, identity(this));;
	}

}
