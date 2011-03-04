package org.drools.guvnor.client.explorer;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Label;
import org.drools.guvnor.client.explorer.RuntimePerspectiveView;
import org.drools.guvnor.client.messages.Constants;

public class RuntimePerspectiveViewImpl extends Composite implements RuntimePerspectiveView {

    private Frame frame = new Frame("http://localhost:8080/gwt-console");

    public RuntimePerspectiveViewImpl() {
        initWidget(frame);
    }

    public String getName() {
        return Constants.INSTANCE.Runtime();
    }

    public void setPresenter(Presenter presenter) {
        //TODO: Generated code -Rikkola-
    }
}
