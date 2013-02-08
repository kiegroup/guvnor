package org.kie.guvnor.project.backend.server;


import com.thoughtworks.xstream.XStream;
import org.kie.guvnor.project.model.PackageConfiguration;

import javax.enterprise.context.Dependent;

@Dependent
public class PackageConfigurationContentHandler {

    public PackageConfigurationContentHandler() {
        // Weld needs this for proxying.
    }

    public String toString(PackageConfiguration configuration) {
        return createXStream().toXML(configuration);
    }

    public PackageConfiguration toModel(String text) {
        return (PackageConfiguration) createXStream().fromXML(text);
    }

    private XStream createXStream() {
        return new XStream();
    }
}
