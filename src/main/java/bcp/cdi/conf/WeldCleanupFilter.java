package bcp.cdi.conf;

import static bcp.cdi.util.LogUtil.*;
import static bcp.cdi.util.LogUtil.inspectThreadLocal;

import java.io.IOException;
import java.lang.reflect.Field;

import javax.enterprise.context.ContextNotActiveException;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.context.spi.AlterableContext;
import javax.enterprise.context.spi.Context;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;

import org.apache.catalina.core.ApplicationContext;
import org.apache.catalina.core.ApplicationContextFacade;
import org.apache.catalina.core.StandardContext;
import org.jboss.weld.contexts.AbstractBoundContext;
import org.jboss.weld.proxy.WeldClientProxy;
import org.jboss.weld.util.ForwardingContext;

import lombok.extern.slf4j.Slf4j;

@WebFilter(urlPatterns = { "/*" }, asyncSupported = true)
@Slf4j
public class WeldCleanupFilter implements Filter {

	@Inject
	private BeanManager beanManager;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
	        throws IOException, ServletException {

		log.debug("Weld cleanup filter BEGIN {}", request);

		AlterableContext context = (AlterableContext) beanManager.getContext(RequestScoped.class);
		log.debug("Current context: {} ", identity(context));

		try {
			chain.doFilter(request, response);
		} finally {
			logBeansInAllScopes();
			if (request.isAsyncStarted()) {
				// FIX Tomcat issue: http://weld.cdi-spec.org/documentation/#8
				// It will forcedly invalidate the context to cleanup
				// inspectThreadLocal();
				destroyContexts();
				//inspectThreadLocal();
			}
			log.debug("Weld cleanup filter END {} isAsync:{}", request, request.isAsyncStarted());
		}
	}

	private void destroyContexts() {
		try {
			Context requestScope = beanManager.getContext(RequestScoped.class);
			destroyContext(requestScope);

			Context conversationContext = beanManager.getContext(ConversationScoped.class);
			if (conversationContext instanceof ForwardingContext) {
				Context delegate = ForwardingContext.unwrap((ForwardingContext) conversationContext);
				AbstractBoundContext<?> ctxt = (AbstractBoundContext<?>) delegate;
				ctxt.cleanup();
			}

			Context sessionContext = beanManager.getContext(SessionScoped.class);
			if (sessionContext instanceof ForwardingContext) {
				Context delegate = ForwardingContext.unwrap((ForwardingContext) sessionContext);
				AbstractBoundContext<?> ctxt = (AbstractBoundContext<?>) delegate;
				ctxt.cleanup();
			}

		} catch (ContextNotActiveException e) {
			log.warn("No Request Scoped context is active", e);
		}
	}

	private void destroyContext(Context delegate) {
		AbstractBoundContext<?> ctxt = (AbstractBoundContext<?>) delegate;
		ctxt.invalidate();
		ctxt.deactivate();
		ctxt.cleanup();
		log.debug("Context {} destroyed", identity(ctxt));
	}

	@Inject
	ServletContext sc;

	/** 
	 * Sending a RequestDestroy Event here does not invalidate the contexts. 
	 * Lifecycle methods don't get called - only the ThreadLocalMap gets
	 * cleaned up.
	 * 
	 * @param request
	 * @throws Exception
	 */
	private void fireRequestDestroyed(ServletRequest request) throws Exception {

		Object obj = ((WeldClientProxy) sc).getMetadata().getContextualInstance();

		Field field = ApplicationContextFacade.class.getDeclaredField("context");
		field.setAccessible(true);
		ApplicationContext appCtxt = (ApplicationContext) field.get(obj);

		field = ApplicationContext.class.getDeclaredField("context");
		field.setAccessible(true);
		StandardContext stdctxt = (StandardContext) field.get(appCtxt);

		stdctxt.fireRequestDestroyEvent(request);
	}


	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// NOP
	}
	
	@Override
	public void destroy() {
		// NOP
	}

}
