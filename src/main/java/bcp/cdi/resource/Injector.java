package bcp.cdi.resource;

import javax.ws.rs.container.PreMatching;
import javax.ws.rs.ext.Provider;

@Provider
@PreMatching
public class Injector {

  public Injector(String arg) {
    
  }
}
