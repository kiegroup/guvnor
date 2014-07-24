package org.guvnor.client.screens.clients;

import javax.enterprise.context.Dependent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;

@Dependent
@WorkbenchScreen(identifier = "oauthClientSettingsScreen")
public class OAuthClientSettingsScreenPresenter
        extends Composite {

    interface Binder
            extends
            UiBinder<Widget, OAuthClientSettingsScreenPresenter> {

    }

    private static Binder uiBinder = GWT.create(Binder.class);

    public OAuthClientSettingsScreenPresenter() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    @WorkbenchPartView
    public Widget getWidget() {
        return this;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Settings";
    }

}
