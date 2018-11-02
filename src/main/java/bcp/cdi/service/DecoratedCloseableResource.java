package bcp.cdi.service;

import javax.annotation.PostConstruct;
import javax.decorator.Decorator;
import javax.decorator.Delegate;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Any;
import javax.inject.Inject;

import static bcp.cdi.util.LogUtil.*;
import lombok.extern.slf4j.Slf4j;

@Dependent
@Decorator
@Slf4j
public abstract class DecoratedCloseableResource extends CloseableResource {

	@Inject
    @Delegate
    @Any
    private CloseableResource resource;
	
	@Override
	public void doSomething() {
		log.debug(">>>> do something in resource");
	}
	
	@PostConstruct
	public void postConstruct() {
		log.debug(POSTCONSTRUCT_MSG, this);
		log.debug("resource:{}", resource);
	}
}
