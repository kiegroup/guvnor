package org.drools.guvnor.client.common;

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

import org.drools.guvnor.client.rpc.DetailedSerializableException;
import org.drools.guvnor.client.rpc.SessionExpiredException;
import org.drools.guvnor.client.messages.Constants;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.gwtext.client.util.Format;

/**
 * This is a generic call back that handles errors (very simply).
 * 
 * @author Michael Neale
 */
public abstract class GenericCallback<T> implements AsyncCallback<T> {

	public void onFailure(Throwable t) {
		LoadingPopup.close();
		if (t instanceof SessionExpiredException) {
			showSessionExpiry();
		} else if (t instanceof DetailedSerializableException) {
			ErrorPopup.showMessage((DetailedSerializableException) t);
		} else {
			ErrorPopup.showMessage(t.getMessage());
		}
	}

	public static void showSessionExpiry() {
		String url = GWT.getModuleBaseURL();
		url = url.substring(0, url.lastIndexOf('/'));
		url = url.substring(0, url.lastIndexOf('/'));

		FormStylePopup pop = new FormStylePopup();
        String m = Format.format(((Constants) GWT.create(Constants.class)).SessionExpiredMessage(), url);
		pop.addRow(new HTML(m));
		pop.show();
		LoadingPopup.close();

	}
}