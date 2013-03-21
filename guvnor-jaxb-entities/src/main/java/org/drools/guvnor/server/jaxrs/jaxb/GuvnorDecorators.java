package org.drools.guvnor.server.jaxrs.jaxb;

import org.jboss.resteasy.annotations.Decorator;

import javax.xml.bind.Marshaller;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 10 19 2012
 *
 * @author <a href="mailto:l.weinan@gmail.com">Weinan Li</a>
 */
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Decorator(processor = GuvnorAtomProcessor.class, target = Marshaller.class)
public @interface GuvnorDecorators {
}
