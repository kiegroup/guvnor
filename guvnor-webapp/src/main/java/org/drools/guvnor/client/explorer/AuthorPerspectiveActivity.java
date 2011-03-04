package org.drools.guvnor.client.explorer;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import org.drools.guvnor.client.explorer.AuthorPerspectiveView.Presenter;

public class AuthorPerspectiveActivity extends AbstractActivity implements Presenter {

    private ClientFactory clientFactory;

    public AuthorPerspectiveActivity(ClientFactory clientFactory) {
        this.clientFactory = clientFactory;
    }

    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        AuthorPerspectiveView authorPerspectiveView = clientFactory.getAuthorPerspectiveView();

        authorPerspectiveView.setPresenter(this);

        panel.setWidget(authorPerspectiveView);
    }

    public void goTo(Place place) {
        clientFactory.getPlaceController().goTo(place);
    }

}
