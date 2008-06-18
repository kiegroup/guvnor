package org.drools.brms.client.common;
/*
 * Copyright 2005 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



import org.drools.brms.client.rpc.DetailedSerializableException;
import org.drools.brms.client.rpc.SessionExpiredException;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;

/**
 * This is a generic call back that handles errors (very simply).
 *
 * @author Michael Neale
 */
public abstract class GenericCallback
    implements
    AsyncCallback {

    public void onFailure(Throwable t) {
    	LoadingPopup.close();
        if (t instanceof SessionExpiredException) {
            showSessionExpiry();
        } else if (t instanceof DetailedSerializableException){
            ErrorPopup.showMessage( (DetailedSerializableException) t );
        } else {
            ErrorPopup.showMessage( t.getMessage() );
        }
    }



    public static void showSessionExpiry() {
    	FormStylePopup pop = new FormStylePopup();
    	pop.addRow(new HTML("<i><strong>Your session expired due to inactivity.</strong></i><p/>" +
        "Please <a href='/drools-jbrms/'>[Log in].</a>"));
    	pop.show();
        LoadingPopup.close();

    }

    public abstract void onSuccess(Object data);
}