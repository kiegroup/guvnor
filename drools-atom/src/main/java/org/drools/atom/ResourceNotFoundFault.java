package org.drools.atom;

import javax.xml.ws.WebFault;

@WebFault
public class ResourceNotFoundFault extends Exception {
    private ResourceNotFoundDetails details;

    public ResourceNotFoundFault(ResourceNotFoundDetails details) {
        super();
        this.details = details;
    }

    public ResourceNotFoundDetails getFaultInfo() {
        return details;
    }
}
