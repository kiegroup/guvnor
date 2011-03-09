package org.drools.guvnor.client.explorer;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import org.drools.guvnor.client.explorer.IFramePerspectiveView.Presenter;

public class IFramePerspectiveActivity extends AbstractActivity implements Presenter {

    private ClientFactory clientFactory;

    public IFramePerspectiveActivity(ClientFactory clientFactory) {
        this.clientFactory = clientFactory;
    }

    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        IFramePerspectiveView view = clientFactory.getIFramePerspectiveView();

        view.setPresenter(this);

        panel.setWidget(view);
    }

    public void goTo(Place place) {
        clientFactory.getPlaceController().goTo(place);
    }
}
