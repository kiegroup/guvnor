package org.drools.guvnor.client.rpc;

import com.google.gwt.user.client.rpc.SerializationException;


/**
 * This is for more detailed reports to send back to the client.
 * Typically there is a short message and longer message. The longer one is used by support. The shorter one displayed by default.
 */
public class DetailedSerializationException extends SerializationException {
    private static final long serialVersionUID = 3971826310143149345L;

    private String longDescription;

    private BuilderResultLine[] errs;
    public DetailedSerializationException() {}

    public DetailedSerializationException(String shortDescription, String longDescription) {
        super(shortDescription);
        this.longDescription = longDescription;
    }
    public DetailedSerializationException(String shortDescription, BuilderResultLine[] errs) {
        super(shortDescription);
        this.errs = errs;
    }

    public String getLongDescription() {
        return this.longDescription;
    }
    public BuilderResultLine[] getErrs(){
    	return errs;
    }

}
