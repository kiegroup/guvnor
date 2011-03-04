package org.drools.guvnor.client.explorer;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class AuthorPerspectiveActivityTemp extends AbstractActivity {

    private ClientFactory clientFactory;

    public AuthorPerspectiveActivityTemp(AuthorPerspectivePlace authorPerspectivePlace, ClientFactory clientFactory) {
        this.clientFactory = clientFactory;
    }

    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        AuthorPerspectiveView authorPerspectiveView = clientFactory.getAuthorPerspectiveView();

                panel.setWidget(authorPerspectiveView);
            }
}
