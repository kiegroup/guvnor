package org.drools.guvnor.client.explorer;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.IsWidget;
import org.drools.guvnor.client.explorer.PerspectivesPanelView.Presenter;

import java.util.HashMap;
import java.util.Map;


public class PerspectivesPanel implements Presenter, AcceptsOneWidget {

    private final PerspectivesPanelView view;
    private final PlaceController placeController;

    private Map<String, Place> perspectives = new HashMap<String, Place>();

    public PerspectivesPanel(PerspectivesPanelView view, PlaceController placeController) {
        this.view = view;
        this.view.setPresenter(this);
        this.placeController = placeController;

    }

    public void setWidget(IsWidget widget) {
        if (widget != null) {
            view.setWidget(widget);
        }
    }

    public PerspectivesPanelView getView() {
        return view;
    }

    public void setUserName(String userName) {
        view.setUserName(userName);
    }

    public void addPerspective(Perspective perspective) {
        String name = perspective.getName();
        perspectives.put(name, perspective);
        view.addPerspectiveToList(name, name);
    }

    public void onPerspectiveChange(String perspectiveId) throws UnknownPerspective {
        placeController.goTo(perspectives.get(perspectiveId));
    }
    }
