package org.guvnor.client.screens;

import com.github.gwtbootstrap.client.ui.Heading;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class Empty
        extends Composite {

    interface Binder
            extends
            UiBinder<Widget, Empty> {

    }

    private static Binder uiBinder = GWT.create(Binder.class);

    @UiField
    Heading title;

    public Empty(String title) {

        initWidget(uiBinder.createAndBindUi(this));

        this.title.setText(title);

    }
}
