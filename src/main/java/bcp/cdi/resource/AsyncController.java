package bcp.cdi.resource;

import static bcp.cdi.util.LogUtil.*;

import java.time.LocalDateTime;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.annotation.PreDestroy;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.spi.AlterableContext;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.CDI;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;

import org.jboss.weld.module.web.context.http.HttpRequestContextImpl;

import bcp.cdi.conf.RequestProduced;
import bcp.cdi.service.UserService;
import lombok.extern.slf4j.Slf4j;

@RequestScoped
@Slf4j
@Path("")
public class AsyncController {

	@Inject
	@RequestProduced
	// if we remove the requestProduced we have the same instance
	// injected in asyncValidation
	private UserService userService;

	@Inject
	private AsyncProcessor<String> asyncProcessor;

	@Inject
	private BeanManager beanManager;

	@GET
	@Path("blocking")
	public String asyncValidation() throws InterruptedException, ExecutionException {
		userService.doSomething();

		Callable<String> callable = () -> {
			sleep(10000);
			userService.doSomething();
			return "DONE " + LocalDateTime.now();
		};

		Future<String> future = asyncProcessor.doit(callable);
		return future.get();
	}

	@GET
	@Path("suspended")
	public void aboutAsync(@Suspended AsyncResponse response) {
		userService.doSomething();

		Callable<String> callable = () -> {
			sleep(10000);
			AlterableContext context = (AlterableContext) beanManager.getContext(RequestScoped.class);
			log.debug("Current context: {} ", identity(context));

			UserService usc = CDI.current().select(UserService.class, () -> RequestProduced.class).get();
			usc.doSomething();

			response.resume("DONE " + LocalDateTime.now());
			return null;
		};
		asyncProcessor.doit(callable);

		// destroyRequestContext();
	}

	private void destroyRequestContext() {
		HttpRequestContextImpl context = (HttpRequestContextImpl) beanManager.getContext(RequestScoped.class);
		log.debug("Deactivating context {}", identity(context));
		context.invalidate();
		context.deactivate();
		context.cleanup();
	}

	private void sleep(long howLong) {
		try {
			log.debug("-- going to sleep");
			Thread.sleep(howLong);
			log.debug("-- woke up");
		} catch (InterruptedException e) {
			log.error("interrupted", e);
			Thread.currentThread().interrupt();
		}
	}

	@PreDestroy
	public void destroy() {
		log.debug(PREDESTROY_MSG, identity(this));
	}
}
