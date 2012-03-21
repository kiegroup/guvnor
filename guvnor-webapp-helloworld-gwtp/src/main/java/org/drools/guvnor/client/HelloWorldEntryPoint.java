package org.drools.guvnor.client;

import org.drools.guvnor.client.gin.ClientGinjector;
import org.drools.guvnor.client.resources.GuvnorResources;
import org.drools.guvnor.client.resources.RoundedCornersResource;


import com.google.gwt.animation.client.Animation;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.user.client.ui.RootPanel;
import com.gwtplatform.mvp.client.DelayedBindRegistry;

public class HelloWorldEntryPoint implements EntryPoint {
	private final ClientGinjector ginjector = GWT.create(ClientGinjector.class);

    public void onModuleLoad() {
   		DelayedBindRegistry.bind(ginjector);
        loadStyles();
        hideLoadingPopup();
        ginjector.getPlaceManager().revealDefaultPlace();
    }

    private void loadStyles() {
        GuvnorResources.INSTANCE.headerCss().ensureInjected();
        RoundedCornersResource.INSTANCE.roundCornersCss().ensureInjected();
    }
    
    //Fade out the "Loading application" pop-up
    private void hideLoadingPopup() {
        final Element e = RootPanel.get( "loading" ).getElement();

        Animation r = new Animation() {

            @Override
            protected void onUpdate(double progress) {
                e.getStyle().setOpacity( 1.0 - progress );
            }

            @Override
            protected void onComplete() {
                e.getStyle().setVisibility( Visibility.HIDDEN );
                //showSyncTBDataPopUp();
                //createSellerIfNotExist();
            }

        };

        r.run( 500 );
    }


}
