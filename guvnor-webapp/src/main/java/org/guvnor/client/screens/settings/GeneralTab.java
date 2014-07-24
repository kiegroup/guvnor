package org.guvnor.client.screens.settings;

import com.github.gwtbootstrap.client.ui.Tab;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class GeneralTab
        extends Tab implements IsWidget, RequiresResize {

    interface Binder
            extends
            UiBinder<Widget, GeneralTab> {

    }

    private static Binder uiBinder = GWT.create(Binder.class);

    @UiField VerticalPanel base;

    public GeneralTab() {
        setHeading("General");
        add(uiBinder.createAndBindUi(this));
    }

    @Override
    public void onResize() {
        int height = asWidget().getParent().getOffsetHeight();
        int width = asWidget().getParent().getOffsetWidth();
        base.setPixelSize(width, height);

    }

}
