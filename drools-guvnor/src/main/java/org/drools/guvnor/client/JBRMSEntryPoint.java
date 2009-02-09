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
import org.drools.guvnor.client.explorer.ExplorerLayoutManager;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;
import org.drools.guvnor.client.rpc.UserSecurityContext;
import org.drools.guvnor.client.security.Capabilities;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.HistoryListener;
import com.google.gwt.user.client.Window;
import com.gwtext.client.util.CSS;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.QuickTips;
import com.gwtext.client.widgets.Viewport;
import com.gwtext.client.widgets.form.Field;

/**
 * This is the main launching/entry point for the JBRMS web console.
 * It essentially sets the initial layout.
 *
 * If you hadn't noticed, this is using GWT from google. Refer to GWT docs
 * if GWT is new to you (it is quite a different way of building web apps).
 */
public class JBRMSEntryPoint
    implements
    EntryPoint, HistoryListener {

    private LoggedInUserInfo loggedInUserInfo;

    public void onModuleLoad() {
        Field.setMsgTarget("side");
        QuickTips.init();
        CSS.swapStyleSheet("theme", "js/ext/resources/css/xtheme-gray.css");
        loggedInUserInfo = new LoggedInUserInfo();
        loggedInUserInfo.setVisible(false);
        checkLoggedIn();
        History.addHistoryListener(this);
    }

	private Panel createMain(Capabilities caps) {
		return (new ExplorerLayoutManager(loggedInUserInfo, caps)).getBaseLayout();
	}


    /**
     * Check if user is logged in, if not, then show prompt.
     * If it is, then we show the app, in all its glory !
     */
    private void checkLoggedIn() {
        RepositoryServiceFactory.getSecurityService().getCurrentUser( new GenericCallback<UserSecurityContext>() {
            public void onSuccess(UserSecurityContext ctx) {
                if ( ctx.userName != null ) {
                	showMain();
                    loggedInUserInfo.setUserName( ctx.userName );
                    loggedInUserInfo.setVisible( true );

                } else {
                	final LoginWidget lw = new LoginWidget();
                	lw.setLoggedInEvent(new Command() {
                        public void execute() {
                        	showMain();
                            loggedInUserInfo.setUserName( lw.getUserName() );
                            loggedInUserInfo.setVisible( true );
                        }
                    } );
                	lw.show();
                }
            }


        } );
    }

	private void showMain() {
		Window.setStatus("Loading user permissions...");
		RepositoryServiceFactory.getSecurityService().getUserCapabilities(new GenericCallback<Capabilities>() {
			public void onSuccess(Capabilities cp) {
				Window.setStatus(" ");
				new Viewport(createMain(cp));

			}
		});
	}

	public void onHistoryChanged(String a) {

	}




}
