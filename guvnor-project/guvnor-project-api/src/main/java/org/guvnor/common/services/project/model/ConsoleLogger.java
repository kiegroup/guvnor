package org.guvnor.common.services.project.model;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class ConsoleLogger
        implements KSessionLogger {

    private String name;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
