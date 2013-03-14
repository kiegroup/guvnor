package org.drools.guvnor.models.commons.backend;

import org.kie.builder.impl.FormatConverter;

public abstract class BaseConverter implements FormatConverter {

    protected String getPackageDeclaration(String name) {
        if ( name.startsWith("/") ) {
            name = name.substring(1);
        }
        int fileNameStart = name.lastIndexOf('/');
        if (fileNameStart < 0) {
            return "";
        } else {
            return "package " + name.substring(0, fileNameStart).replace('/', '.') + "\n";
        }
    }

    protected String getDestinationName(String name) {
        return getDestinationName(name, false);
    }

    protected String getDestinationName(String name, boolean hasDsl) {
        return name.substring(0, name.lastIndexOf('.')) + (hasDsl ? ".dslr" : ".drl");
    }
}
