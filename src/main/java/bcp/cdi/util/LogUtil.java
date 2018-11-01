package bcp.cdi.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.ContextNotActiveException;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.spi.AlterableContext;
import javax.enterprise.context.spi.Context;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.CDI;
import javax.enterprise.util.AnnotationLiteral;

import org.apache.log4j.Level;
import org.slf4j.Logger;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import static java.util.stream.Collectors.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class LogUtil {

	public static final String DISPOSED_MSG = "--> DISPOSED {}";
	public static final String CREATED_MSG = "--> CREATED {}";
	public static final String PREDESTROY_MSG = "/// PreDestroy: {}";
	public static final String POSTCONSTRUCT_MSG = "/// PostConstruct: {}";
	public static final String CONSTRUCTOR_MSG = "/// Constructor {}";
	
	public static String identity(Object obj) {
		return obj.getClass().getSimpleName() + " [" + System.identityHashCode(obj) + "]";
	}

	public static String currentContextIdentity() {
		BeanManager bm = CDI.current().getBeanManager();
		AlterableContext context = (AlterableContext) bm.getContext(RequestScoped.class);
		
		return identity(context);
	}
	
	public static void logConstructorEvent(Logger log, Object obj) {
		log.debug("/// Constructed {} in scope {} ", identity(obj), currentContextIdentity());
		logIfFoundInContext(LogUtil.class, RequestScoped.class, obj);
		logIfFoundInContext(LogUtil.class, ApplicationScoped.class, obj);
	}

	public static void logBeansInAllScopes() {
		String delimiter = "\n\t\t\t\t\t\t";
		BeanManager bm = CDI.current().getBeanManager();

		AlterableContext ctxt = (AlterableContext) bm.getContext(ApplicationScoped.class);
		String identitiesList = listBeansInContext(ctxt).stream().collect(joining(delimiter));
		log.debug("In Application Scope {} " + delimiter + "{}", identity(ctxt), identitiesList);
		
		ctxt = (AlterableContext) bm.getContext(RequestScoped.class);
		identitiesList = listBeansInContext(ctxt).stream().collect(joining(delimiter));
		log.debug("In Request Scope {} " + delimiter + "{}", identity(ctxt), identitiesList);
	}
	
	@SuppressWarnings("serial")
	private static void logIfFoundInContext(Class<?> source, Class<? extends Annotation> scope, Object obj) {

		// Use the log4j logger directly to make this class a wrapper. The logger
		// will filter from the stack-trace the lines corresponding to this class
		org.apache.log4j.Logger callerLog = org.apache.log4j.Logger.getLogger(source);

		BeanManager bm = CDI.current().getBeanManager();
		Set<Bean<?>> beans = bm.getBeans(Object.class, new AnnotationLiteral<Any>() {
		});
		try {
			AlterableContext ctxt = (AlterableContext) bm.getContext(scope);
			boolean found = beans.stream()
					.map(ctxt::get)
					.anyMatch(instance -> instance == obj);
			if (found) {
				callerLog.log(LogUtil.class.getCanonicalName(), Level.DEBUG,
				        "--> DISPOSED " + identity(obj) + " in context: " + identity(ctxt), (Throwable) null);
			}
		} catch (ContextNotActiveException e) {
			// In case we don't have an active request scope. The application scope should
			// be always active
		}
	}
	
	@SuppressWarnings("serial")
	private static List<String> listBeansInContext(Context ctxt) {
		BeanManager bm = CDI.current().getBeanManager();
		Set<Bean<?>> beans = bm.getBeans(Object.class, new AnnotationLiteral<Any>() {});
		return beans.stream().map(ctxt::get).filter(Objects::nonNull).map(LogUtil::identity)
		        .collect(Collectors.toList());
	}
	
	
	public static void logDestroyEvent(Object source, Object obj) {
		logIfFoundInContext(source.getClass(), RequestScoped.class, obj);
		logIfFoundInContext(source.getClass(), ApplicationScoped.class, obj);
	}
	
	public static void inspectThreadLocal() {
		try {
			Field field = Thread.class.getDeclaredField("threadLocals");
			field.setAccessible(true);
			Object map = field.get(Thread.currentThread());
			
			Field table = Class.forName("java.lang.ThreadLocal$ThreadLocalMap").getDeclaredField("table");
			table.setAccessible(true);
			Object tbl = table.get(map);
			int length = Array.getLength(tbl);
			for (int i = 0; i < length; i++) {
				Object entry = Array.get(tbl, i);
				Object value = null;
				String valueClass = null;
				if (entry != null) {
					Field valueField = Class.forName("java.lang.ThreadLocal$ThreadLocalMap$Entry")
					        .getDeclaredField("value");
					valueField.setAccessible(true);
					value = valueField.get(entry);
					if (value != null) {
						valueClass = value.getClass().getName();
					}
					log.info("[" + i + "] type[" + valueClass + "] " + value);
				}
			}
		} catch (Exception e) {
			log.error("Could not inspect ThreadLocal of" + Thread.currentThread(), e);
		}
	}
}
