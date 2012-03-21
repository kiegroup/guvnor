package org.drools.guvnor.client.common;

import org.drools.guvnor.client.messages.Constants;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;

/**
 * This is a generic call back that handles errors (very simply).
 */
public abstract class GenericCallback<T> implements AsyncCallback<T> {
	
	//It will only get the rpc invocation error in this method.
    public void onFailure(Throwable t) {
        LoadingPopup.close();
        ErrorPopup.showMessage(t.getMessage());
    }

    public static void showSessionExpiry() {
        String url = GWT.getModuleBaseURL();
        url = url.substring(0, url.lastIndexOf('/'));
        url = url.substring(0, url.lastIndexOf('/'));

        FormStylePopup pop = new FormStylePopup();
        String m = ((Constants) GWT.create(Constants.class)).SessionExpiredMessage(url);
        pop.addRow(new HTML(m));
        pop.show();
        LoadingPopup.close();

    }
}
