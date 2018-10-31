package bcp.cdi.service;

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.annotation.WebListener;

import lombok.extern.slf4j.Slf4j;

@WebListener
@Slf4j
public class ServletRequestLogger implements ServletRequestListener {

	@Override
	public void requestInitialized(ServletRequestEvent event) {
		log.debug("ServletRequest initialized {} ", event.getServletRequest());
	}

	@Override
	public void requestDestroyed(ServletRequestEvent event) {
		log.debug("ServletRequest destroyed {}", event.getServletRequest());
		// destroying the context will not work here
	}

}