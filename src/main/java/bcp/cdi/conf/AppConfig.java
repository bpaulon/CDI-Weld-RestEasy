package bcp.cdi.conf;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("rest")
public class AppConfig extends Application {

  public AppConfig() {
    System.out.println("///" + AppConfig.class + " constructor");
  }
}