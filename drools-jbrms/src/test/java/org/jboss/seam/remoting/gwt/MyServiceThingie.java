package org.jboss.seam.remoting.gwt;

import org.jboss.seam.annotations.WebRemote;

public abstract class MyServiceThingie {

    public String something;
    
    @WebRemote
    public abstract void doSomething(String x);
    
    public String notMe() {
        return "Not allowed as doesn't have WebRemote";
    }
    
    @WebRemote
    public String yeahYeah() {
        return "whee";
    }
    
}
