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

package org.drools.guvnor.client;

import org.drools.guvnor.client.common.FormStylePopup;
import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.common.LoadingPopup;
import org.drools.guvnor.client.resources.Images;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;
import org.drools.guvnor.client.messages.Constants;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;

/**
 * Used for logging in, obviously !
 */
public class LoginWidget {

    private Constants     constants = GWT.create( Constants.class );
    private static Images images    = GWT.create( Images.class );

    private Command       loggedInEvent;
    private String        userNameLoggedIn;

    public void show() {
        final FormStylePopup pop = new FormStylePopup( images.login(),
                                                       constants.Login() );

        final TextBox userName = new TextBox();
        pop.addAttribute( constants.UserName(),
                          userName );
        final PasswordTextBox password = new PasswordTextBox();
        pop.addAttribute( constants.Password(),
                          password );

        KeyPressHandler kph = new KeyPressHandler() {
            public void onKeyPress(KeyPressEvent event) {
                if ( KeyCodes.KEY_ENTER == event.getNativeEvent().getKeyCode() ) {
                    doLogin( userName,
                             password,
                             pop );
                }
            }
        };
        userName.addKeyPressHandler( kph );
        password.addKeyPressHandler( kph );

        Button b = new Button( constants.OK() );
        b.addClickHandler( new ClickHandler() {
            public void onClick(ClickEvent event) {
                doLogin( userName,
                         password,
                         pop );
            }
        } );

        pop.addAttribute( "",
                          b );
       
        pop.setAfterShow( new Command() {
            public void execute() {
                Scheduler scheduler = Scheduler.get();
                scheduler.scheduleDeferred( new Command() {
                    public void execute() {
                        userName.setFocus( true );
                    }
                });
            }
        } );

        pop.show();
    }

    private void doLogin(final TextBox userName,
                         PasswordTextBox password,
                         final FormStylePopup pop) {
        LoadingPopup.showMessage( constants.Authenticating() );
        RepositoryServiceFactory.login( userName.getText(),
                                        password.getText(),
                                        new GenericCallback() {
                                            public void onSuccess(Object o) {
                                                userNameLoggedIn = userName.getText();
                                                LoadingPopup.close();
                                                Boolean success = (Boolean) o;
                                                if ( !success.booleanValue() ) {
                                                    com.google.gwt.user.client.Window.alert( constants.IncorrectUsernameOrPassword() );
                                                } else {
                                                    loggedInEvent.execute();
                                                    pop.hide();
                                                }
                                            }
                                        } );
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
