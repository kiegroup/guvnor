package org.drools.guvnor.client.explorer.navigation.browse;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.IsWidget;
import org.drools.guvnor.client.explorer.ClientFactory;

public interface RulesNewMenuView extends IsWidget {

    interface Presenter {

        void onOpenWizard( String assetType, boolean showCategories );
    }

    void setPresenter( Presenter presenter );

    void launchWizard( String assetType, boolean showCategories, ClientFactory clientFactory, EventBus eventBus );
}
