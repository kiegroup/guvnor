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



import org.drools.brms.client.common.FormStyleLayout;
import org.drools.brms.client.common.GenericCallback;
import org.drools.brms.client.common.LoadingPopup;
import org.drools.brms.client.rpc.RepositoryServiceFactory;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.Window;
import com.gwtext.client.widgets.event.ButtonListener;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.layout.FitLayout;

/**
 * Used for logging in, obviously !
 *
 * @author Michael Neale
 */
public class LoginWidget {


    private TextBox userName;
    private PasswordTextBox password;
    private Command loggedInEvent;
	private Window w;




	public void show() {

    	w = new Window();
    	w.setWidth(400);
    	//w.setHeight(200);
    	w.setModal(true);
    	w.setShadow(false);
    	w.setClosable(false);

    	final Widget signInForm = getSignInForm();

//    	VerticalPanel signInWrapper = new VerticalPanel() {
//    	    {
//    	        setSpacing(30);
//    	        setWidth("100%");
//    	        setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);
//    	    }
//    	};

    	Panel p = new Panel();
    	p.add(signInForm);
    	p.setLayout(new FitLayout());
//    	signInWrapper.add(signInForm);

    	w.setTitle("Sign In");

    	w.add(p);







        w.show();
        userName.setFocus( true );
	}

    private void doLogin(final Command loggedInEvent, final TextBox userName, final PasswordTextBox password) {
        RepositoryServiceFactory.login( userName.getText(), password.getText(), new GenericCallback() {
            public void onSuccess(Object o) {
                LoadingPopup.close();
                Boolean success = (Boolean) o;
                if (!success.booleanValue()) {
                    com.google.gwt.user.client.Window.alert( "Incorrect username or password." );
                } else {
                    loggedInEvent.execute();
                    w.hide();
                    w.destroy();
                }
            }
        });
    }

    private Widget getSignInForm() {

        FormStyleLayout layout = new FormStyleLayout("images/login.gif", "BRMS Login");

        userName = new TextBox();
        userName.setTabIndex( 1 );
        layout.addAttribute( "User name:", userName );

        password = new PasswordTextBox();
        password.setTabIndex( 2 );
        layout.addAttribute( "Password:", password );
    	com.google.gwt.user.client.ui.Button b2 = new com.google.gwt.user.client.ui.Button("Sign in");

    	b2.addClickListener(new ClickListener() {

			public void onClick(Widget arg0) {
                LoadingPopup.showMessage( "Logging in..." );

                DeferredCommand.addCommand( new Command() {
                    public void execute() {
                        doLogin( loggedInEvent, userName, password );
                    }
                });
			}

    	});
        layout.addAttribute("", b2);

        return layout;

    }

    /**
     * Return the name that was entered.
     */
    public String getUserName() {
        return userName.getText();
    }

    /**
     * This is required for a callback after a successful login.
     */
    public void setLoggedInEvent(Command loggedInEvent) {
        this.loggedInEvent = loggedInEvent;
    }


}