package bcp.cdi.conf;

import static bcp.cdi.util.LogUtil.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;
import javax.servlet.ServletContext;

import bcp.cdi.service.CloseableResource;
import bcp.cdi.service.UserService;
import bcp.cdi.util.LogUtil;
import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
@Slf4j
public class ApplicationResources {

	@Inject
	ServletContext sc;

	@Produces
	@RequestProduced
	public UserService createUserService(Instance<UserService> instance) {
		UserService us = instance.select(UserService.class).get();
		log.debug(LogUtil.CREATED_MSG, identity(us));

		return us;
	}

	public void disposeUserService(@RequestProduced @Disposes UserService us) {
		us.close();
		log.debug(LogUtil.DISPOSED_MSG, identity(us));
	}

	@Produces
	@RequestProduced
	public CloseableResource createCloseableResource() {
		CloseableResource resource = new CloseableResource();
		log.debug(LogUtil.CREATED_MSG, identity(resource));
		return resource;
	}

	public void destroyCloseableResource(@Disposes @RequestProduced CloseableResource resource) throws Exception {
		try {
			resource.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		log.debug(LogUtil.DISPOSED_MSG, identity(resource));
	}

	@Produces
	@ApplicationScoped
	public ExecutorService createExecutorService() {
		ExecutorService es = Executors.newCachedThreadPool();
		log.debug(LogUtil.CREATED_MSG, identity(es));
		return es;
	}

	public void destroyExecutorService(@Disposes ExecutorService es) {
		es.shutdown();
		log.debug(LogUtil.DISPOSED_MSG, identity(es));
	}

	
	@Produces
    @ConfigValue("")
	@Dependent
	public String configValueProducer(InjectionPoint ip) {
        // We know this annotation WILL be present as WELD won't call us otherwise, so no null checking is required.
        ConfigValue configValue = ip.getAnnotated().getAnnotation(ConfigValue.class);
        // This could potentially return a null, so the function is annotated @Dependent to avoid a WELD error.
        Map<String, String> configMap = new HashMap<>();
		// This is a dummy initialization, do something constructive here
		configMap.put("string.value", "This is a test value");
		
        return configMap.get(configValue.value());
    }
	
	@PostConstruct
	public void postConstruct() {
		log.debug("/// PostConstruct {}", identity(this));
	}

	@PreDestroy
	public void destroy() {
		log.debug(PREDESTROY_MSG, identity(this));
	}

}
