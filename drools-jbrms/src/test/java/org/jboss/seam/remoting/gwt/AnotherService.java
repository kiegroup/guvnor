package org.jboss.seam.remoting.gwt;

import org.jboss.seam.annotations.WebRemote;

public class AnotherService {

    public boolean called = false;
    
    @WebRemote
    public void doSomething() {
        called = true;
    }
    
    
}
