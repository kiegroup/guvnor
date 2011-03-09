package org.drools.guvnor.client.explorer;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.drools.guvnor.client.messages.Constants;

public class AuthorPerspectiveViewImpl extends Composite implements AuthorPerspectiveView {

    private Constants constants = GWT.create( Constants.class );

    interface AuthorPerspectiveViewImplBinder
            extends
            UiBinder<Widget, AuthorPerspectiveViewImpl> {
    }

    private static AuthorPerspectiveViewImplBinder uiBinder = GWT.create(AuthorPerspectiveViewImplBinder.class);

    private Presenter presenter;

    public AuthorPerspectiveViewImpl() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    public String getName() {
        return constants.AuthorPerspective();
    }
}
