package bcp.cdi.service;

import static bcp.cdi.conf.ConfigurationKeys.KEY_NAME_01;
import static bcp.cdi.util.LogUtil.POSTCONSTRUCT_MSG;
import static bcp.cdi.util.LogUtil.identity;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Destroyed;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import bcp.cdi.conf.ConfigValue;
import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
@Slf4j
public class StartupBean {

	@Inject
	@ConfigValue(KEY_NAME_01)
	String stringValue;

	public void init(@Observes @Initialized(ApplicationScoped.class) Object init) {
		log.debug("ApplicationScoped initialized with {}:{} - init obj:{}", KEY_NAME_01, stringValue, identity(init));
	}

	public void destroy(@Observes @Destroyed(ApplicationScoped.class) Object init) {
		log.debug("ApplicationScoped destroyed event {}", init);
	}
	
	@PostConstruct
	public void postConstruct() {
		log.debug(POSTCONSTRUCT_MSG, identity(this));
	}

}
