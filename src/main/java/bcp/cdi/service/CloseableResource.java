package bcp.cdi.service;

import java.io.Closeable;
import static bcp.cdi.util.LogUtil.*;
import java.io.IOException;

import javax.enterprise.context.RequestScoped;

import lombok.extern.slf4j.Slf4j;

@RequestScoped
@Slf4j
public class CloseableResource implements Closeable {

  public CloseableResource () {
    log.debug("/// Constructor {} ", identity(this));
  }
  
  public void doSomething() {
    //log.debug("doSomething()");
  }
  
  @Override
  public void close() throws IOException {
    //log.debug("--> Closed: {}", identity(this));
  }

}
