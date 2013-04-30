package org.kie.guvnor.datamodel.backend.server.testclasses.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface SmurfDescriptor {

    String gender();

    String colour() default "blue";

    String description();

}
