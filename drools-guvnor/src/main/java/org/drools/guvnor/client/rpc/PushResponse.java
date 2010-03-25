package org.drools.guvnor.client.rpc;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * A response packet that is sent back to the client.
 * messageType is to allow filtering on the client, message data itself contains the message.
 * @author Michael Neale
 */
public class PushResponse implements IsSerializable {

    public PushResponse() {
    	this("", "");
    }
    public PushResponse(String key, String message) {
        this.messageType = key;
        this.message = message;
    }
    public String messageType;
    public String message;

}
