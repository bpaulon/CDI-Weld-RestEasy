package bcp.cdi.service;

import static bcp.cdi.util.LogUtil.identity;

import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;

@Dependent
@Slf4j
public class UserService {

  @Inject
  @RequestProduced
  private CloseableResource resource;
  
  public UserService() {
    log.debug("/// Constructor {}", identity(this));
  }
  
  public void close() {
  }

  public void doSomething() {
   log.debug("doSomething called: {}", identity(this));
   resource.doSomething();
  }
  
  @PreDestroy
  public void destroy() { 
    log.debug("/// PreDestroy {}", identity(this));
  }

}
