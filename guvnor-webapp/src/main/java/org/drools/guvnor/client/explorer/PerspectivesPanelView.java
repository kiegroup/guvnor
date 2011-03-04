package org.drools.guvnor.client.explorer;

import com.google.gwt.user.client.ui.IsWidget;

interface PerspectivesPanelView extends IsWidget {

    interface Presenter {
        void onPerspectiveChange(String perspectiveId) throws UnknownPerspective;
    }

    void setPresenter(Presenter presenter);

    void setUserName(String userName);

    void setWidget(IsWidget widget);

    void addPerspectiveToList(String perspectiveId, String perspectiveName);
}
