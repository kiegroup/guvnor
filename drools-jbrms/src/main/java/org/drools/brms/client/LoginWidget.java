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



import org.drools.brms.client.common.ErrorPopup;
import org.drools.brms.client.common.FormStyleLayout;
import org.drools.brms.client.common.GenericCallback;
import org.drools.brms.client.common.LoadingPopup;
import org.drools.brms.client.rpc.RepositoryServiceFactory;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.core.Ext;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.ButtonConfig;
import com.gwtext.client.widgets.LayoutDialog;
import com.gwtext.client.widgets.LayoutDialogConfig;
import com.gwtext.client.widgets.Toolbar;
import com.gwtext.client.widgets.ToolbarButton;
import com.gwtext.client.widgets.ToolbarTextItem;
import com.gwtext.client.widgets.event.ButtonListener;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.form.Form;
import com.gwtext.client.widgets.form.FormConfig;
import com.gwtext.client.widgets.layout.BorderLayout;
import com.gwtext.client.widgets.layout.ContentPanel;
import com.gwtext.client.widgets.layout.ContentPanelConfig;
import com.gwtext.client.widgets.layout.LayoutRegionConfig;

/**
 * Used for logging in, obviously !
 *
 * @author Michael Neale
 */
public class LoginWidget {


    private TextBox userName;
    private PasswordTextBox password;
    private Command loggedInEvent;
	private LayoutDialog dialog;



	public void show() {
		LayoutRegionConfig center = new LayoutRegionConfig() {
    	    {
    	        setAutoScroll(true);
    	        setTabPosition("top");
    	        setCloseOnTab(true);
    	        setAlwaysShowTabs(true);
    	    }
    	};

    	dialog = new LayoutDialog(new LayoutDialogConfig() {
    	    {
    	        setModal(true);
    	        setWidth(500);
    	        setHeight(350);
    	        setShadow(true);
    	        setResizable(false);
    	        setClosable(false);
    	        setProxyDrag(true);
    	        setTitle("Sign in");
    	    }
    	}, center);



    	final BorderLayout layout = dialog.getLayout();
    	layout.beginUpdate();

    	ContentPanel signInPanel = new ContentPanel(Ext.generateId(), "Sign In");
    	final Widget signInForm = getSignInForm();

    	VerticalPanel signInWrapper = new VerticalPanel() {
    	    {
    	        setSpacing(30);
    	        setWidth("100%");
    	        setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);
    	    }
    	};
    	signInWrapper.add(signInForm);

    	signInPanel.add(signInWrapper);
    	layout.add(LayoutRegionConfig.CENTER, signInPanel);


    	final Toolbar tb = new Toolbar("my-tb");
    	tb.addButton(new ToolbarButton("About", new ButtonConfig()));
    	tb.addSeparator();
    	tb.addItem(new ToolbarTextItem("Copyright (c) 2006 JBoss, a division of Red Hat."));

    	ContentPanel infoPanel = new ContentPanel(Ext.generateId(), new ContentPanelConfig() {
    	    {
    	        setTitle("Info");
    	        setClosable(true);
    	        setBackground(true);
    	        setToolbar(tb);
    	    }
    	});
    	infoPanel.setContent("Drools Business Rule Management System (BRMS). See http://labs.jboss.com/drools/ for more information.");

    	layout.add(LayoutRegionConfig.CENTER, infoPanel);
    	layout.endUpdate();


        Button login = dialog.addButton("Sign in");


        login.addButtonListener( new ButtonListenerAdapter() {
        	public void onClick(Button button, EventObject e) {
                LoadingPopup.showMessage( "Logging in..." );

                DeferredCommand.addCommand( new Command() {
                    public void execute() {
                        doLogin( loggedInEvent, userName, password );
                    }
                });
            }

        });
        dialog.show();
        userName.setFocus( true );
	}

    private void doLogin(final Command loggedInEvent, final TextBox userName, final PasswordTextBox password) {
        RepositoryServiceFactory.login( userName.getText(), password.getText(), new GenericCallback() {
            public void onSuccess(Object o) {
                LoadingPopup.close();
                Boolean success = (Boolean) o;
                if (!success.booleanValue()) {
                    Window.alert( "Incorrect username or password." );
                } else {
                    loggedInEvent.execute();
                    dialog.destroy();
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