package org.drools.guvnor.client;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class HelloWorldViewPart implements ViewPart {

    @Override
    public Widget asWidget() {
        return new Label("Hello world");
    }
}
