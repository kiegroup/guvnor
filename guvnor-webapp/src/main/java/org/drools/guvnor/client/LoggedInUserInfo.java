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

import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.resources.GuvnorResources;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;
import org.drools.guvnor.client.rpc.UserSecurityContext;
import org.drools.guvnor.client.util.Format;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;

/**
 * Simple widget to show who is logged in, and a link to logout.
 */
public class LoggedInUserInfo extends Composite {

    private Constants constants = ((Constants) GWT.create( Constants.class ));

    private HTML      widgetcontent;

    public LoggedInUserInfo() {
        widgetcontent = new HTML();
        initWidget( widgetcontent );
    }

    public void setUserName(String userName) {
        StringBuilder content = new StringBuilder();
        content.append( Format.format( "<div class='{0}'>",
                                       GuvnorResources.INSTANCE.headerCss().headerUserInfoClass() ) );

        String m = Format.format( constants.WelcomeUser(),
                                  userName );
        content.append( "<small>" + m ); //NON-NLS
        content.append( "&nbsp;&nbsp;&nbsp;<a href='logout.jsp'>[" + constants.SignOut() + "]</a></small>" ); //NON-NLS
        content.append( "</div>" ); //NON-NLS
        widgetcontent.setHTML( content.toString() );

        //we have the timer to keep the session alive.
        Timer timer = new Timer() {

            public void run() {
                RepositoryServiceFactory.getSecurityService().getCurrentUser( new AsyncCallback<UserSecurityContext>() {
                    public void onFailure(Throwable t) {
                    }

                    public void onSuccess(UserSecurityContext ctx) {
                        if ( ctx.userName == null ) {
                            GenericCallback.showSessionExpiry();
                        }
                    }

                } );
            }

        };

        timer.scheduleRepeating( 300000 );

    }

}
