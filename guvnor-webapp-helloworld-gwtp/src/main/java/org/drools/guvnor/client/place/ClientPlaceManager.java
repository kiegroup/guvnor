package org.drools.guvnor.client.place;

import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.place.DefaultPlace;

import com.gwtplatform.mvp.client.proxy.PlaceManagerImpl;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.google.gwt.core.client.GWT;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.proxy.TokenFormatter;

public class ClientPlaceManager extends PlaceManagerImpl {

	private final PlaceRequest defaultPlaceRequest;
    private static Constants constants = GWT.create(Constants.class);

	@Inject
	public ClientPlaceManager(final EventBus eventBus,
			final TokenFormatter tokenFormatter,
			@DefaultPlace final String defaultPlaceNameToken) {
		super(eventBus, tokenFormatter);
		this.defaultPlaceRequest = new PlaceRequest(defaultPlaceNameToken);
	}

	@Override
	public void revealDefaultPlace() {
        PlaceRequest placeRequest = new PlaceRequest(NameTokens.helloWorld);
        placeRequest = placeRequest.with("tabName", constants.helloWorld());
        revealPlace(placeRequest, false);

		//revealPlace(defaultPlaceRequest, false);
	}
}
