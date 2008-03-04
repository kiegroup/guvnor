package org.drools.brms.client;
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



import org.drools.brms.client.common.FormStylePopup;
import org.drools.brms.client.common.GenericCallback;
import org.drools.brms.client.common.LoadingPopup;
import org.drools.brms.client.rpc.RepositoryServiceFactory;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.gwtext.client.core.EventObject;

import com.gwtext.client.widgets.Window;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.layout.FitLayout;

/**
 * Used for logging in, obviously !
 *
 * @author Michael Neale
 */
public class LoginWidget {







	private Command loggedInEvent;
	private String userNameLoggedIn;

	public void show() {

		final FormStylePopup pop = new FormStylePopup("images/login.gif", "BRMS login");


		final TextBox userName = new TextBox();
		pop.addAttribute("User name:", userName);


		final PasswordTextBox password = new PasswordTextBox();
		pop.addAttribute("Password: ", password);

		Button b = new Button("OK");


		b.addClickListener(new ClickListener() {
			public void onClick(Widget arg0) {
				LoadingPopup.showMessage("Authenticating...");
		        RepositoryServiceFactory.login( userName.getText(), password.getText(), new GenericCallback() {
					public void onSuccess(Object o) {
		            	userNameLoggedIn = userName.getText();
		                LoadingPopup.close();
		                Boolean success = (Boolean) o;
		                if (!success.booleanValue()) {
		                    com.google.gwt.user.client.Window.alert( "Incorrect username or password." );
		                } else {
		                    loggedInEvent.execute();
		                    pop.hide();
		                }
		            }
		        });
			}

		});


		pop.addAttribute("", b);

		pop.show();





	}



    /**
     * Return the name that was entered.
     */
    public String getUserName() {
        return userNameLoggedIn;
    }

    /**
     * This is required for a callback after a successful login.
     */
    public void setLoggedInEvent(Command loggedInEvent) {
        this.loggedInEvent = loggedInEvent;
    }


}