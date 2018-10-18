package bcp.cdi.service;

import static bcp.cdi.util.LogUtil.identity;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;

import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
@Slf4j
public class ApplicationResources {

  final static String CREATED = "--> CREATED {}";
  final static String DISPOSED = "--> DISPOSED {}";
  
  @Produces
  @RequestProduced
  public UserService createUserService(Instance<UserService> instance) {
    UserService us = instance.select(UserService.class).get();
    log.debug(CREATED, identity(us));

    return us; 
  }

  public void disposeUserService(@RequestProduced @Disposes UserService us) {
    us.close();
    log.debug(DISPOSED, identity(us));
  }
  
  @Produces
  @RequestProduced
  public CloseableResource createCloseableResource() {
    CloseableResource resource= new CloseableResource();
    log.debug(CREATED, identity(resource));
    return resource;
  }
  
  public void destroyCloseableResource(@Disposes @RequestProduced CloseableResource resource)  {
    try {
      resource.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    log.debug(DISPOSED, identity(resource));
  }
  
  @Produces
  @ApplicationScoped
  public ExecutorService createExecutorService() {
    ExecutorService es = Executors.newCachedThreadPool();
    log.debug(CREATED, identity(es));
    return es;
  }
  
  public void destroyExecutorService(@Disposes ExecutorService es) {
    es.shutdown();
    log.debug(DISPOSED, identity(es));
  }
  
  @PostConstruct
  public void postConstruct() {
    log.debug("/// PostConstruct {}", identity(this));
  }

  @PreDestroy
  public void destroy() {
    log.debug("/// PreDestroy {}", identity(this));
  }

}
