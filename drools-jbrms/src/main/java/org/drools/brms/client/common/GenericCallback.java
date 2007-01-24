package org.drools.brms.client.common;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * This is a generic call back that handles errors (very simply).
 * 
 * @author Michael Neale
 */
public abstract class GenericCallback
    implements
    AsyncCallback {

    public void onFailure(Throwable t) {
        ErrorPopup.showMessage( t.getMessage() );
    }

    public abstract void onSuccess(Object data);
}
