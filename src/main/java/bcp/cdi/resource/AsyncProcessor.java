package bcp.cdi.resource;

import static bcp.cdi.util.LogUtil.PREDESTROY_MSG;
import static bcp.cdi.util.LogUtil.identity;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.weld.context.bound.BoundRequestContext;
import org.jboss.weld.contexts.AbstractManagedContext;

import com.google.common.collect.Maps;

import lombok.extern.slf4j.Slf4j;

@Dependent
@Slf4j
public class AsyncProcessor<T> {

	@Inject
	private ExecutorService executor;

	@Inject
	private BoundRequestContext requestContext;

	public Future<T> doit(Callable<T> command) {

		return executor.submit(() -> {
			log.debug("Submitting command");
			// activate RequestContext for this thread
			requestContext.associate(Maps.newHashMap());
			requestContext.activate();
			try {
				return command.call();
			} finally {
				// deactivate the context to destroy or dispose the beans. Cleaning
				// up ensures the request scoped beans are created new
				log.debug("Deactivating context: {}", identity(requestContext));
				requestContext.invalidate();
				requestContext.deactivate();
				((AbstractManagedContext) requestContext).cleanup();
			}
		});
	}

	@PreDestroy
	public void destroy() {
		log.debug(PREDESTROY_MSG, identity(this));
	}
}
