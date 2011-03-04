package org.drools.guvnor.client.explorer;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import org.drools.guvnor.client.explorer.RuntimePerspectiveView.Presenter;

public class RuntimePerspectiveActivity extends AbstractActivity implements Presenter {

    private ClientFactory clientFactory;

    public RuntimePerspectiveActivity(ClientFactory clientFactory) {
        this.clientFactory = clientFactory;
    }

    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        RuntimePerspectiveView view = clientFactory.getRuntimePerspectiveView();

        view.setPresenter(this);

        panel.setWidget(view);
    }

    public void goTo(Place place) {
        clientFactory.getPlaceController().goTo(place);
    }
}
