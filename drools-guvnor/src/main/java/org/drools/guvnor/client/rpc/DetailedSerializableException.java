package org.drools.guvnor.client.rpc;

import com.google.gwt.user.client.rpc.SerializableException;

/**
 * This is for more detailed reports to send back to the client.
 * Typically there is a short message and longer message. The longer one is used by support. The shorter one displayed by default.
 */
public class DetailedSerializableException extends SerializableException {


    private String longDescription;

    public DetailedSerializableException() {}

    public DetailedSerializableException(String shortDescription, String longDescription) {
        super(shortDescription);
        this.longDescription = longDescription;
    }


    public String getLongDescription() {
        return this.longDescription;
    }

}
