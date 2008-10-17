package org.drools.atom;

import javax.xml.ws.WebFault;

@WebFault
public class PackageNotFoundFault extends Exception {
    private PackageNotFoundDetails details;

    public PackageNotFoundFault(PackageNotFoundDetails details) {
        super();
        this.details = details;
    }

    public PackageNotFoundDetails getFaultInfo() {
        return details;
    }
}
