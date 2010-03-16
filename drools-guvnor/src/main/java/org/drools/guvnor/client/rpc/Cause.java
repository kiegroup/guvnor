package org.drools.guvnor.client.rpc;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * 
 * @author trikkola
 *
 */
public class Cause
    implements
    IsSerializable {

    private String  cause;
    private Cause[] causes;

    public Cause() {
        this.cause = "";
        this.causes = new Cause[0];
    }

    public Cause(String cause,
                 Cause[] causes) {
        this.cause = cause;
        this.causes = causes;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }

    public String getCause() {
        return cause;
    }

    public void setCauses(Cause[] causes) {
        this.causes = causes;
    }

    public Cause[] getCauses() {
        return causes;
    }

}
