package org.drools.guvnor.client.ruleeditor;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.Window;
import com.google.gwt.http.client.URL;
import com.gwtext.client.util.Format;
import com.gwtext.client.widgets.Panel;
import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.common.SmallLabel;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.rpc.DiscussionRecord;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;
import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.guvnor.client.rpc.PushClient;
import org.drools.guvnor.client.rpc.ServerPushNotification;
import org.drools.guvnor.client.rpc.PushResponse;
import org.drools.guvnor.client.explorer.ExplorerLayoutManager;
import org.drools.guvnor.client.explorer.CategoriesPanel;
import org.drools.guvnor.client.security.Capabilities;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;

/**
 * 
 * Does the discussion panel for assets.
 * 
 * @author Michael Neale
 */
public class DiscussionWidget extends Composite {

    private static Constants constants = ((Constants) GWT.create(Constants.class));

    private VerticalPanel commentList = new VerticalPanel();
    private VerticalPanel newCommentLayout = new VerticalPanel();
    private RuleAsset asset;
    private ServerPushNotification pushNotify;
    private int lastCount = 0;

    @Override
    protected void onUnload() {
        super.onUnload();    //To change body of overridden methods use File | Settings | File Templates.
        PushClient.instance().unsubscribe(pushNotify);
    }

    public DiscussionWidget(final RuleAsset asset) {
        this.asset = asset;
        final Panel discussionPanel = new Panel();
        discussionPanel.setCollapsible( true );
        discussionPanel.setTitle(constants.Discussion() + ":" );
        discussionPanel.setBodyBorder(false);

        commentList.setWidth("100%");
        VerticalPanel discussionLayout = new VerticalPanel();
        discussionLayout.setWidth("100%");

        discussionPanel.add(discussionLayout);

        discussionLayout.add(commentList);
        newCommentLayout.setWidth("100%");

        refreshDiscussion();


        discussionLayout.add(newCommentLayout);
        showNewCommentButton();

        pushNotify = new ServerPushNotification() {
            public void messageReceived(PushResponse response) {
                if ("discussion".equals(response.messageType) &&
                        asset.uuid.equals(response.message)) {
                    System.err.println("Refreshing discussion...");
                    refreshDiscussion();
                }
            }
        };

        PushClient.instance().subscribe(pushNotify);

        initWidget(discussionPanel);
    }



    /** Hit up the server */
    public void refreshDiscussion() {
        RepositoryServiceFactory.getService().loadDiscussionForAsset(asset.uuid, new GenericCallback<List<DiscussionRecord>>() {
            public void onSuccess(List<DiscussionRecord> result) {

                updateCommentList(result);
            }
        });
    }

    private void updateCommentList(List<DiscussionRecord> ls) {
        if (ls.size() == lastCount) return; //don't want to over do it boys...
        commentList.clear();
        for(DiscussionRecord dr: ls) {
            appendComment(dr);
        }
        lastCount = ls.size();
    }

    private Widget appendComment(DiscussionRecord r) {
        SmallLabel hrd = new SmallLabel(Format.format(constants.smallCommentBy0On1Small(), r.author, new Date(r.timestamp).toString()));
        hrd.addStyleName("discussion-header");
        commentList.add(hrd);
        Label lbl = new Label(r.note);
        lbl.setStyleName("x-form-field");
        commentList.add(lbl);
        commentList.add(new HTML("<br/>"));
        return hrd;
    }


    private void showNewCommentButton() {
        newCommentLayout.clear();

        HorizontalPanel hp = new HorizontalPanel();

        Button createNewComment = new Button(constants.AddADiscussionComment());
        hp.add(createNewComment);



        if (ExplorerLayoutManager.shouldShow(Capabilities.SHOW_ADMIN)) {
            Button adminClearAll = new Button(constants.EraseAllComments());
            hp.add(adminClearAll);
            adminClearAll.addClickListener(new ClickListener() {
                public void onClick(Widget sender) {
                    if (Window.confirm(constants.EraseAllCommentsWarning())) {
                        RepositoryServiceFactory.getService().clearAllDiscussionsForAsset(asset.uuid, new GenericCallback() {
                            public void onSuccess(Object result) {
                                updateCommentList(new ArrayList<DiscussionRecord>());
                            }
                        });
                    }
                }
            });
            
        }


        String feedURL = GWT.getModuleBaseURL() + "feed/discussion?package=" + asset.metaData.packageName+
                "&assetName=" + URL.encode(asset.metaData.name) + "&viewUrl=" + CategoriesPanel.getSelfURL();
        hp.add(new HTML("<a href='" + feedURL + "' target='_blank'><img src='images/feed.png'/></a>"));

        newCommentLayout.add(hp);
        
        newCommentLayout.setCellHorizontalAlignment(hp, HasHorizontalAlignment.ALIGN_RIGHT);
        createNewComment.addClickListener(new ClickListener() {
            public void onClick(Widget sender) {
                showAddNewComment();
            }
        });
    }

    private void showAddNewComment() {
        newCommentLayout.clear();
        final TextArea comment = new TextArea();
        comment.setWidth("100%");
        newCommentLayout.add(comment);

        Button ok = new Button(constants.OK());
        Button cancel = new Button(constants.Cancel());

        ok.addClickListener(new ClickListener() {
            public void onClick(Widget sender) {
                sendNewComment(comment.getText());
            }
        });

        cancel.addClickListener(new ClickListener() {
            public void onClick(Widget sender) {
                showNewCommentButton();
            }
        });

        HorizontalPanel hp = new HorizontalPanel();
        hp.add(ok); hp.add(cancel);

        newCommentLayout.add(hp);
        
        comment.setFocus(true);

    }

    private void sendNewComment(String text) {
        newCommentLayout.clear();
        newCommentLayout.add(new Image("images/spinner.gif"));
        RepositoryServiceFactory.getService().addToDiscussionForAsset(asset.uuid, text, new GenericCallback<List<DiscussionRecord>>() {
            public void onSuccess(List<DiscussionRecord> result) {
                showNewCommentButton();
                updateCommentList(result);
            }
        });

    }


}
