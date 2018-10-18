package bcp.cdi.service;

import static bcp.cdi.util.LogUtil.identity;

import java.util.Arrays;

import javax.annotation.PreDestroy;
import javax.annotation.Priority;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import lombok.extern.slf4j.Slf4j;

@Logged
@Interceptor
@Priority(Interceptor.Priority.APPLICATION)
@Slf4j
public class LogInterceptor {

  public LogInterceptor() {
    log.debug("/// Constructor {}", identity(this));
  }

  @AroundInvoke
  public Object auditMethod(InvocationContext ctx) throws Exception {
    log.debug("Intercepted START obj:{} method:{} with parameters: {}", identity(ctx.getTarget()), ctx.getMethod(),
        Arrays.toString(ctx.getParameters()));
    Object result = ctx.proceed();
    log.debug("Intercepted END result: {}", result);
    return result;
  }

  @PreDestroy
  public void destroy() {
    log.debug("/// PreDestroy: {}", identity(this));
  }
}