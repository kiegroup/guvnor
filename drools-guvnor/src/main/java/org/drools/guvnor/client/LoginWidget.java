package org.drools.guvnor.client;
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



import org.drools.guvnor.client.common.FormStylePopup;
import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.common.LoadingPopup;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;
import org.drools.guvnor.client.messages.Constants;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.*;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.widgets.event.KeyListener;
import com.gwtext.client.widgets.form.TextField;
import com.google.gwt.core.client.GWT;

/**
 * Used for logging in, obviously !
 *
 * @author Michael Neale
 */
public class LoginWidget {

    Constants messages = GWT.create(Constants.class);

	private Command loggedInEvent;
	private String userNameLoggedIn;

	public void show() {
		final FormStylePopup pop = new FormStylePopup("images/login.gif", messages.Login());

		final TextField userName = new TextField("username");
		pop.addAttribute(messages.UserName(), userName);

		final TextField password = new TextField("password" );
		password.setPassword(true);
		pop.addAttribute(messages.Password(), password);

        KeyListener kl = new KeyListener() {
          	public void onKey(int key, EventObject e) {
				if (key == KeyboardListener.KEY_ENTER) {
                    doLogin(userName, password, pop);
                }
				
            }
        };

        userName.addKeyListener(KeyboardListener.KEY_ENTER, kl);
        password.addKeyListener(KeyboardListener.KEY_ENTER, kl);

		Button b = new Button(messages.OK());

		b.addClickListener(new ClickListener() {
			public void onClick(Widget arg0) {
                doLogin(userName, password, pop);
			}
		});
		
		pop.addAttribute("", b);
    //

        pop.setAfterShow(new Command() {
            public void execute() {
                userName.focus(true,100);                    
            	
            }
        });
		pop.show();
		
	}

    private void doLogin(final TextField userName, TextField password, final FormStylePopup pop) {
        LoadingPopup.showMessage(messages.Authenticating());
        RepositoryServiceFactory.login( userName.getText(), password.getText(), new GenericCallback() {
            public void onSuccess(Object o) {
                userNameLoggedIn = userName.getText();
                LoadingPopup.close();
                Boolean success = (Boolean) o;
                if (!success.booleanValue()) {
                    com.google.gwt.user.client.Window.alert(messages.IncorrectUsernameOrPassword());
                } else {
                    loggedInEvent.execute();
                    pop.hide();
                }
            }
        });
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