package bcp.cdi.service;

import static bcp.cdi.util.LogUtil.identity;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Destroyed;
import javax.enterprise.context.Initialized;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Observes;
import javax.servlet.ServletRequest;

import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
@Slf4j
public class RequestScopeListener {

	public void processRequestScopedInit(@Observes @Initialized(RequestScoped.class) ServletRequest payload) {
		log.debug("RequestScoped initialized {}", identity(payload));
	}

	public void processRequestScopedDestroyed(@Observes @Destroyed(RequestScoped.class) ServletRequest payload) {
		log.debug("RequestScoped destroyed {}", identity(payload));
	}
	
}
