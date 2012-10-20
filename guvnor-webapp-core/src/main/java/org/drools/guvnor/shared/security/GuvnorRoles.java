package org.drools.guvnor.shared.security;


import org.uberfire.security.annotations.RolesType;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@RolesType
@Retention(RUNTIME)
@Target({TYPE, METHOD})
public @interface GuvnorRoles {

    AppRoles[] value();

}
