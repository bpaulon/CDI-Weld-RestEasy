package bcp.cdi.conf;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.enterprise.util.Nonbinding;
import javax.inject.Qualifier;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

@Qualifier
@Retention(RUNTIME)
@Target({METHOD, FIELD, PARAMETER, TYPE})
public @interface ConfigValue {
    // Excludes this value from being considered for injection point matching
    @Nonbinding 
    // Avoid specifying a default value, since it can encourage programmer error.
    // We WANT a value every time.
    String value();
}
