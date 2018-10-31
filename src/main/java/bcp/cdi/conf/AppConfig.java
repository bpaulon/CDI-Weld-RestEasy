package bcp.cdi.conf;

import javax.ws.rs.ApplicationPath;

import javax.ws.rs.core.Application;

import lombok.extern.slf4j.Slf4j;
import static bcp.cdi.util.LogUtil.identity;
import static bcp.cdi.util.LogUtil.*;

@ApplicationPath("rest")
@Slf4j
public class AppConfig extends Application {

	public AppConfig() {
		log.debug(CONSTRUCTOR_MSG, identity(this));
	}
}