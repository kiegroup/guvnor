/*
 * Copyright 2010 JBoss Inc
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

package org.drools.guvnor.client.ruleeditor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.common.SmallLabel;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.resources.Images;
import org.drools.guvnor.client.rpc.DiscussionRecord;
import org.drools.guvnor.client.rpc.PushClient;
import org.drools.guvnor.client.rpc.PushResponse;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;
import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.guvnor.client.rpc.ServerPushNotification;
import org.drools.guvnor.client.security.Capabilities;
import org.drools.guvnor.client.security.CapabilitiesManager;
import org.drools.guvnor.client.util.Format;
import org.drools.guvnor.client.util.Util;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * Does the discussion panel for assets.
 * 
 * @author Michael Neale
 */
public class DiscussionWidget extends Composite {

    private static Constants       constants        = GWT.create( Constants.class );
    private static Images          images           = GWT.create( Images.class );

    private VerticalPanel          commentList      = new VerticalPanel();
    private VerticalPanel          newCommentLayout = new VerticalPanel();
    private RuleAsset              asset;
    private ServerPushNotification pushNotify;
    private int                    lastCount        = 0;

    @Override
    protected void onUnload() {
        super.onUnload(); //To change body of overridden methods use File | Settings | File Templates.
        PushClient.instance().unsubscribe( pushNotify );
    }

    public DiscussionWidget(final RuleAsset asset) {
        this.asset = asset;

        DisclosurePanel discussionPanel = new DisclosurePanel( constants.Discussion() );
        discussionPanel.setAnimationEnabled( true );
        discussionPanel.setWidth( "100%" );

        commentList.setWidth( "100%" );
        VerticalPanel discussionLayout = new VerticalPanel();
        discussionLayout.setWidth( "90%" );
        discussionLayout.add( commentList );

        newCommentLayout.setWidth( "100%" );
        refreshDiscussion();
        discussionLayout.add( newCommentLayout );
        showNewCommentButton();

        discussionPanel.setContent( discussionLayout );

        pushNotify = new ServerPushNotification() {
            public void messageReceived(PushResponse response) {
                if ( "discussion".equals( response.messageType ) && asset.uuid.equals( response.message ) ) {
                    System.err.println( "Refreshing discussion..." );
                    refreshDiscussion();
                }
            }
        };

        PushClient.instance().subscribe( pushNotify );

        initWidget( discussionPanel );
    }

    /** Hit up the server */
    public void refreshDiscussion() {
        RepositoryServiceFactory.getService().loadDiscussionForAsset( asset.uuid,
                                                                      new GenericCallback<List<DiscussionRecord>>() {
                                                                          public void onSuccess(List<DiscussionRecord> result) {
                                                                              updateCommentList( result );
                                                                          }
                                                                      } );
    }

    private void updateCommentList(List<DiscussionRecord> ls) {
        if ( ls.size() == lastCount ) return; //don't want to over do it boys...
        commentList.clear();
        for ( DiscussionRecord dr : ls ) {
            appendComment( dr );
        }
        lastCount = ls.size();
    }

    private Widget appendComment(DiscussionRecord r) {
        SmallLabel hrd = new SmallLabel( Format.format( constants.smallCommentBy0On1Small(),
                                                        r.author,
                                                        new Date( r.timestamp ).toString() ) );
        hrd.addStyleName( "discussion-header" );
        commentList.add( hrd );

        String[] parts = r.note.split( "\n" );

        if ( parts.length > 0 ) {
            String txt = "";
            for ( int i = 0; i < parts.length; i++ ) {
                txt += parts[i];
                if ( i != parts.length - 1 ) {
                    txt += "<br/>";
                }
            }
            HTML hth = new HTML( txt );
            hth.setStyleName( "x-form-field" );
            commentList.add( hth );
        } else {
            Label lbl = new Label( r.note );
            lbl.setStyleName( "x-form-field" );
            commentList.add( lbl );
        }

        commentList.add( new HTML( "<br/>" ) );
        return hrd;
    }

    private void showNewCommentButton() {
        newCommentLayout.clear();

        HorizontalPanel hp = new HorizontalPanel();

        Button createNewComment = new Button( constants.AddADiscussionComment() );
        hp.add( createNewComment );

        if ( CapabilitiesManager.getInstance().shouldShow( Capabilities.SHOW_ADMIN ) ) {
            Button adminClearAll = new Button( constants.EraseAllComments() );
            hp.add( adminClearAll );
            adminClearAll.addClickHandler( new ClickHandler() {
                public void onClick(ClickEvent sender) {
                    if ( Window.confirm( constants.EraseAllCommentsWarning() ) ) {
                        RepositoryServiceFactory.getService().clearAllDiscussionsForAsset( asset.uuid,
                                                                                           new GenericCallback<java.lang.Void>() {
                                                                                               public void onSuccess(Void v) {
                                                                                                   updateCommentList( new ArrayList<DiscussionRecord>() );
                                                                                               }
                                                                                           } );
                    }
                }
            } );
        }

        String feedURL = Format.format( "{0}feed/discussion?package={1}&assetName={2}&viewUrl={3}",
                                        GWT.getModuleBaseURL(),
                                        asset.metaData.packageName,
                                        URL.encode( asset.metaData.name ),
                                        Util.getSelfURL() );
        hp.add( new HTML( Format.format( "<a href='{0}' target='_blank'><img src='{1}'/></a>",
                                         feedURL,
                                         new Image( images.feed() ).getUrl() ) ) );

        newCommentLayout.add( hp );

        newCommentLayout.setCellHorizontalAlignment( hp,
                                                     HasHorizontalAlignment.ALIGN_RIGHT );
        createNewComment.addClickHandler( new ClickHandler() {
            public void onClick(ClickEvent sender) {
                showAddNewComment();
            }
        } );
    }

    private void showAddNewComment() {
        newCommentLayout.clear();
        final TextArea comment = new TextArea();
        comment.setWidth( "100%" );
        newCommentLayout.add( comment );

        Button ok = new Button( constants.OK() );
        Button cancel = new Button( constants.Cancel() );

        ok.addClickHandler( new ClickHandler() {
            public void onClick(ClickEvent sender) {
                sendNewComment( comment.getText() );
            }
        } );

        cancel.addClickHandler( new ClickHandler() {
            public void onClick(ClickEvent sender) {
                showNewCommentButton();
            }
        } );

        HorizontalPanel hp = new HorizontalPanel();
        hp.add( ok );
        hp.add( cancel );

        newCommentLayout.add( hp );

        comment.setFocus( true );
    }

    private void sendNewComment(String text) {
        newCommentLayout.clear();
        newCommentLayout.add( new Image( images.spinner() ) );
        RepositoryServiceFactory.getService().addToDiscussionForAsset( asset.uuid,
                                                                       text,
                                                                       new GenericCallback<List<DiscussionRecord>>() {
                                                                           public void onSuccess(List<DiscussionRecord> result) {
                                                                               showNewCommentButton();
                                                                               updateCommentList( result );
                                                                           }
                                                                       } );
    }
}
