package org.drools.guvnor.client.explorer.navigation.browse;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.drools.guvnor.client.explorer.ClientFactory;

public class RulesNewMenu implements RulesNewMenuView.Presenter, IsWidget {

    private RulesNewMenuView view;
    private ClientFactory clientFactory;

    public RulesNewMenu( ClientFactory clientFactory ) {
        this.view = clientFactory.getNavigationViewFactory().getRulesNewMenuView();
        this.clientFactory = clientFactory;
        view.setPresenter( this );
    }

    public void onOpenWizard( String format, boolean showCategories ) {
        view.launchWizard( format, showCategories, clientFactory );
    }

    public Widget asWidget() {
        return view.asWidget();
    }
}
