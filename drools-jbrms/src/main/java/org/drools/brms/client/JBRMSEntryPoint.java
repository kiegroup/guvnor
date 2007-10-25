
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


package org.drools.brms.client;

import org.drools.brms.client.JBRMSFeature.ComponentInfo;
import org.drools.brms.client.common.GenericCallback;
import org.drools.brms.client.rpc.RepositoryServiceFactory;
import org.drools.brms.client.rpc.UserSecurityContext;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.HistoryListener;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * This is the main launching/entry point for the JBRMS web console.
 * It essentially sets the initial layout.
 *
 * If you hadn't noticed, this is using GWT from google. Refer to GWT docs
 * if GWT is new to you (it is quite a different way of building web apps).
 */
public class JBRMSEntryPoint implements EntryPoint, HistoryListener {

  private ComponentInfo curInfo;
  private JBRMSFeature curSink;
  private HTML description = new HTML();
  private JBRMSFeatureList list = new JBRMSFeatureList();
  private DockPanel panel = new DockPanel();
  private DockPanel sinkContainer;
  private LoginWidget loginWidget;
  private LoggedInUserInfo loggedInUserInfo;

  public void onHistoryChanged(String token) {
    // Find the SinkInfo associated with the history context. If one is
    // found, show it (It may not be found, for example, when the user mis-
    // types a URL, or on startup, when the first context will be "").
    ComponentInfo info = list.find(token);
    if (info == null) {
      showInfo();
      return;
    }
    show(info, false);
  }

  public void onModuleLoad() {

    // Load all the sinks.
    JBRMSFeatureConfigurator.configure(list);

    // Put the sink list on the left, and add the outer dock panel to the
    // root.
    sinkContainer = new DockPanel();
    sinkContainer.setStyleName("ks-Sink");

    VerticalPanel vp = new VerticalPanel();
    vp.setWidth("100%");
    vp.add(description);
    vp.add(sinkContainer);

    description.setStyleName("ks-Info");

    panel.add(list, DockPanel.WEST);
    panel.add(vp, DockPanel.CENTER);

    panel.setCellVerticalAlignment(list, HasAlignment.ALIGN_TOP);
    panel.setCellWidth(vp, "100%");

    History.addHistoryListener(this);

    loggedInUserInfo = new LoggedInUserInfo();
    loginWidget = new LoginWidget();

    RootPanel.get().add( loggedInUserInfo );
    RootPanel.get().add(panel);
    RootPanel.get().add( loginWidget );
    loginWidget.setWidth( "100%" );

    loggedInUserInfo.setVisible( false );
    panel.setVisible( false );
    loginWidget.setVisible( false );

    checkLoggedIn();

    // Show the initial screen.
    String initToken = History.getToken();
    if (initToken.length() > 0)
      onHistoryChanged(initToken);
    else
      showInfo();
  }

  /**
   * Check if user is logged in, if not, then show prompt.
   * If it is, then we show the app, in all its glory !
   */
  private void checkLoggedIn() {

      RepositoryServiceFactory.getSecurityService().getCurrentUser( new GenericCallback() {

        public void onSuccess(Object data) {
        	UserSecurityContext ctx  = (UserSecurityContext) data;
            if (ctx.userName != null) {

	                loggedInUserInfo.setUserName( ctx.userName );
	                loggedInUserInfo.setVisible( true );
	                list.disableFeatures(ctx);
	                panel.setVisible( true );
	                loginWidget.setVisible( false );


            } else {

                loginWidget.setVisible( true );
                loginWidget.setLoggedInEvent( new Command() {
                    public void execute() {
                        loggedInUserInfo.setUserName( loginWidget.getUserName() );
                        loggedInUserInfo.setVisible( true );
                        loginWidget.setVisible( false );
                        panel.setVisible( true );
                    }
                } );

            }
        }

      });





  }



public void show(ComponentInfo info, boolean affectHistory) {
    // Don't bother re-displaying the existing sink. This can be an issue
    // in practice, because when the history context is set, our
    // onHistoryChanged() handler will attempt to show the currently-visible
    // sink.
    if (info == curInfo)
      return;
    curInfo = info;

    // Remove the old sink from the display area.
    if (curSink != null) {
      curSink.onHide();
      sinkContainer.remove(curSink);
    }

    // Get the new sink instance, and display its description in the
    // sink list.
    curSink = info.getInstance();
    list.setSinkSelection(info.getName());
    description.setHTML(info.getDescription());

    // If affectHistory is set, create a new item on the history stack. This
    // will ultimately result in onHistoryChanged() being called. It will call
    // show() again, but nothing will happen because it will request the exact
    // same sink we're already showing.
    if (affectHistory)
      History.newItem(info.getName());

    // Display the new sink.
    sinkContainer.add(curSink, DockPanel.CENTER);
    sinkContainer.setCellWidth(curSink, "100%");
    //sinkContainer.setCellHeight(curSink, "100%");
    sinkContainer.setCellVerticalAlignment(curSink, DockPanel.ALIGN_TOP);
    curSink.onShow();

  }


  private void showInfo() {
    show(list.find("Info"), false);
  }
}