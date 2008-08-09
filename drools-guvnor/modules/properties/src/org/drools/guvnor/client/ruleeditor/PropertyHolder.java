package org.drools.guvnor.client.ruleeditor;

import org.drools.guvnor.client.modeldriven.brl.PortableObject;

/**
 *
 */
public class PropertyHolder implements PortableObject {
    public String name;
    public String value;

    public PropertyHolder() {
    }

    public PropertyHolder(String name, String value) {
        this.name = name;
        this.value = value;
    }
}