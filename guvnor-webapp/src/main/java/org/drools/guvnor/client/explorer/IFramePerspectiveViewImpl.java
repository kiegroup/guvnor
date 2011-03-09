package org.drools.guvnor.client.explorer;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Frame;

public class IFramePerspectiveViewImpl extends Composite implements IFramePerspectiveView {

    private Frame frame = new Frame("http://localhost:8080/gwt-console");

    public IFramePerspectiveViewImpl() {
        initWidget(frame);
    }

    public String getName() {
        return "CCCCCC";
    }

    public void setPresenter(Presenter presenter) {
        //TODO: Generated code -Rikkola-
    }
}
