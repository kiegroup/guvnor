package org.drools.guvnor.client.explorer;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import org.drools.guvnor.client.common.ErrorPopup;
import org.drools.guvnor.client.messages.Constants;

public class PerspectivesPanelViewImpl extends Composite implements PerspectivesPanelView {

    interface PerspectivesPanelViewImplBinder
            extends
            UiBinder<Widget, PerspectivesPanelViewImpl> {
    }

    private static PerspectivesPanelViewImplBinder uiBinder = GWT.create(PerspectivesPanelViewImplBinder.class);

    private Presenter presenter;

    @UiField()
    FlowPanel perspective;

    @UiField
    ListBox perspectives;

    @UiField
    SpanElement userName;

    @UiField
    HTMLPanel titlePanel;

    public PerspectivesPanelViewImpl(boolean showTitle) {
        showTitle(showTitle);

        initWidget(uiBinder.createAndBindUi(this));

        titlePanel.setVisible(showTitle);
    }

    private void showTitle(boolean showTitle) {
        if (showTitle) {
            TitlePanelHeight.show();
        } else {
            TitlePanelHeight.hide();
        }
    }

    public void setUserName(String userName) {
        this.userName.setInnerText(userName);
    }

    public void setWidget(IsWidget widget) {
        perspective.clear();
        Widget w = widget.asWidget();
        w.setHeight("100%");
        w.setWidth("100%");
        perspective.add(w);
    }


    public void addPerspectiveToList(String perspectiveId, String perspectiveName) {
        perspectives.addItem(perspectiveName, perspectiveId);
    }

    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @UiHandler("perspectives")
    public void handleChange(ChangeEvent event) {
        String perspectiveId = perspectives.getValue(perspectives.getSelectedIndex());
        try {
            presenter.onPerspectiveChange(perspectiveId);
        } catch (UnknownPerspective unknownPerspective) {
            ErrorPopup.showMessage(Constants.INSTANCE.FailedToLoadPerspectiveUnknownId0(perspectiveId));
        }
    }

    public static class TitlePanelHeight {

        private static final int DEFAULT_HEIGHT = 4;
        private static int height = DEFAULT_HEIGHT;

        public int getHeight() {
            return height;
        }

        public static void show() {
            height = DEFAULT_HEIGHT;
        }

        public static void hide() {
            height = 0;
        }
    }
}
