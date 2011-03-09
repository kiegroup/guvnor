package org.drools.guvnor.client.explorer;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.place.shared.PlaceController;

public class ClientFactoryImpl implements ClientFactory {

    private final EventBus eventBus = new SimpleEventBus();
    private final PlaceController placeController = new PlaceController(eventBus);
    private final AuthorPerspectiveView authorPerspectiveView = new AuthorPerspectiveViewImpl();
    private PerspectivesPanelView perspectivesPanelView;

    public PlaceController getPlaceController() {
        return placeController;
    }

    public AuthorPerspectiveView getAuthorPerspectiveView() {
        return authorPerspectiveView;
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public PerspectivesPanelView getPerspectivesPanelView(boolean showTitle) {
        if (perspectivesPanelView == null) {
            perspectivesPanelView = new PerspectivesPanelViewImpl(showTitle);
        }
        return perspectivesPanelView;
    }

    public IFramePerspectiveView getIFramePerspectiveView() {
return new IFramePerspectiveViewImpl();
    }
}
