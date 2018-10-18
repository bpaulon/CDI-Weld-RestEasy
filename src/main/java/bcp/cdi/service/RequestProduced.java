package bcp.cdi.service;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Stereotype;
import javax.inject.Qualifier;

@Qualifier
@Stereotype
@RequestScoped
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD,
        ElementType.TYPE, ElementType.PARAMETER})
public @interface RequestProduced {}