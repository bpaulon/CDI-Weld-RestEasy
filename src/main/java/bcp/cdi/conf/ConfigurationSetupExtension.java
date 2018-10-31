package bcp.cdi.conf;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.InjectionTarget;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Named;
import javax.inject.Singleton;

import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
@Slf4j
public class ConfigurationSetupExtension implements Extension {

	Map<String, String> configMap;

	public ConfigurationSetupExtension() {
		configMap = new HashMap<>();
		// This is a dummy initialization, do something constructive here
		configMap.put("string.value", "This is a test value");
	}

	// Add the ConfigMap values to the global bean scope
	void afterBeanDiscovery(@Observes AfterBeanDiscovery abd, BeanManager bm) {

		// Loop through each entry registering the strings.
		for (Entry<String, String> configEntry : configMap.entrySet()) {
			final String configKey = configEntry.getKey();
			final String configValue = configEntry.getValue();

			log.debug("registering the configuration {}:{}", configKey, configValue);

			AnnotatedType<String> at = bm.createAnnotatedType(String.class);
			final InjectionTarget<String> it = bm.createInjectionTarget(at);

			/**
			 * All of this is necessary so WELD knows where to find the string, what it's
			 * named, and what scope (singleton) it is.
			 */
			Bean<String> si = new Bean<String>() {

				public Set<Type> getTypes() {
					Set<Type> types = new HashSet<>();
					types.add(String.class);
					types.add(Object.class);
					return types;
				}

				public Set<Annotation> getQualifiers() {
					Set<Annotation> qualifiers = new HashSet<>();
					qualifiers.add(new NamedAnnotationImpl(configKey));
					return qualifiers;

				}

				public Class<? extends Annotation> getScope() {
					return Singleton.class;
				}

				public String getName() {
					return configKey;
				}

				public Set<Class<? extends Annotation>> getStereotypes() {
					return Collections.emptySet();
				}

				public Class<?> getBeanClass() {
					return String.class;
				}

				public boolean isAlternative() {
					return false;
				}

				public boolean isNullable() {
					return true;
				}

				public Set<InjectionPoint> getInjectionPoints() {
					return it.getInjectionPoints();
				}

				@Override
				public String create(CreationalContext<String> ctx) {
					return configValue;

				}

				@Override
				public void destroy(String instance, CreationalContext<String> ctx) {
					// Strings can't be destroyed, so don't do anything
				}
			};
			abd.addBean(si);
		}
	}

	/**
	 * This is just so we can create a @Named annotation at runtime.
	 */
	class NamedAnnotationImpl extends AnnotationLiteral<Named> implements Named {

		private static final long serialVersionUID = 1L;

		final String nameValue;

		NamedAnnotationImpl(String nameValue) {
			this.nameValue = nameValue;
		}

		public String value() {
			return nameValue;
		}

	}
}