package org.drools.guvnor.server.jaxrs.jaxb;

import org.drools.guvnor.server.jaxrs.providers.atom.Entry;
import org.jboss.resteasy.spi.interception.DecoratorProcessor;

import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.lang.annotation.Annotation;

/**
 * 10 19 2012
 *
 * @author <a href="mailto:l.weinan@gmail.com">Weinan Li</a>
 */
public class GuvnorAtomProcessor implements DecoratorProcessor<Marshaller, GuvnorDecorators> {

    @Override
    public Marshaller decorate(Marshaller target, GuvnorDecorators annotation, Class type, Annotation[] annotations, MediaType mediaType) {
        Class[] classes = new Class[]{AtomAssetMetadata.class, AtomPackageMetadata.class, Entry.class};
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(classes);
            return jaxbContext.createMarshaller();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
