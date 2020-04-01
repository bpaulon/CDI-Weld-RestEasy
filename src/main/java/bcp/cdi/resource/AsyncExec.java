package bcp.cdi.resource;


import java.util.concurrent.ExecutorService;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.weld.context.bound.BoundRequestContext;
import org.jboss.weld.contexts.AbstractManagedContext;

import com.google.common.collect.Maps;

import lombok.extern.slf4j.Slf4j;

@Dependent
@Slf4j
public class AsyncExec {


  @Inject
  private ExecutorService executor;

  @Inject
  private BoundRequestContext requestContext;

  public void execute(Runnable command) {

    executor.submit(() -> {
      // activate RequestContext for this thread
      requestContext.associate(Maps.newHashMap());
      requestContext.activate();
      try {
        command.run();
      } finally {
        log.debug("Deactivating context: {}", requestContext);
        
        // deactivates the context to destroy or dispose the beans. Cleaning
        // up ensures that any data associated with the thread is deleted. The threads
        // are typically pooled - if we don't cleanup the created beans might not be removed from 
        // the thread context
        requestContext.invalidate();
        requestContext.deactivate();
        ((AbstractManagedContext)requestContext).cleanup();
      }
    });
  }
}

