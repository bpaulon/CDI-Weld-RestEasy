package bcp.cdi.service;

import static bcp.cdi.util.LogUtil.CONSTRUCTOR_MSG;
import static bcp.cdi.util.LogUtil.identity;
import static bcp.cdi.util.LogUtil.logConstructorEvent;
import static bcp.cdi.util.LogUtil.logDestroyEvent;

import java.io.Closeable;
import java.io.IOException;

import javax.annotation.PreDestroy;
import javax.enterprise.context.RequestScoped;

import lombok.extern.slf4j.Slf4j;

@RequestScoped
@Slf4j
public class CloseableResource implements Closeable {

	public CloseableResource() {
		log.debug(CONSTRUCTOR_MSG, identity(this));
	}

	public void doSomething() {
		// NOP
	}

	@Override
	public void close() throws IOException {
	}
	
	@PreDestroy
	public void destroy() {
		logDestroyEvent(this, this);
	}

}
