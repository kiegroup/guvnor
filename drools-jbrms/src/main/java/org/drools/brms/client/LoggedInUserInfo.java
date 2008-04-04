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




import org.drools.brms.client.common.GenericCallback;
import org.drools.brms.client.rpc.RepositoryServiceFactory;
import org.drools.brms.client.rpc.UserSecurityContext;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;

/**
 * Simple widget to show who is logged in, and a link to logout.
 * @author Fernando Meyer
 */
public class LoggedInUserInfo extends Composite{
    private HTML widgetcontent;


    public LoggedInUserInfo() {
        widgetcontent = new HTML();
        initWidget( widgetcontent );
    }

    public void setUserName(String userName) {
        StringBuffer content = new StringBuffer();
        content.append( "<div class='headerUserInfo'>" );
        content.append( "<small>Welcome: &nbsp;" + userName );
        content.append( "&nbsp;&nbsp;&nbsp;<a href='logout.jsp'>[Sign Out]</a></small>" );
        content.append( "</div>" );
        widgetcontent.setHTML( content.toString() );

        //we have the timer to keep the session alive.
        Timer timer = new Timer() {

            public void run() {
                RepositoryServiceFactory.getSecurityService().getCurrentUser( new AsyncCallback() {

                    public void onFailure(Throwable t) {
                    }

                    public void onSuccess(Object o) {
                        	UserSecurityContext ctx = (UserSecurityContext) o;
                        	if (ctx.userName == null) {
                                GenericCallback.showSessionExpiry();
                        	}
                     }

                });
            }

        };

        timer.scheduleRepeating( 300000 );


    }



}