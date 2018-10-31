package bcp.cdi.util;

import java.lang.reflect.Array;
import java.lang.reflect.Field;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class LogUtil {

	public static final String DISPOSED_MSG = "--> DISPOSED {}";
	public static final String CREATED_MSG = "--> CREATED {}";
	public static final String PREDESTROY_MSG = "/// PreDestroy: {}";
	public static final String CONSTRUCTOR_MSG = "/// Constructor {}";
	
	public static String identity(Object obj) {
		return obj.getClass().getSimpleName() + " [" + System.identityHashCode(obj) + "]";
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
			log.error("Could not inspect ThreadLocal of {}", Thread.currentThread(), e);
		}
	}
}
