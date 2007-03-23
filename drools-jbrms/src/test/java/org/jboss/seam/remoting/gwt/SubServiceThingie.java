package org.jboss.seam.remoting.gwt;

import org.jboss.seam.annotations.WebRemote;

public class SubServiceThingie extends MyServiceThingie {

    
    public void doSomething(String x) {
        something = x;
    }    
    
}
