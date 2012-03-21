package org.drools.guvnor.client.layout;

import org.drools.guvnor.client.place.NameTokens;

import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.ContentSlot;
import com.gwtplatform.mvp.client.annotations.ProxyStandard;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.gwtplatform.mvp.client.proxy.RevealContentHandler;
import com.gwtplatform.mvp.client.proxy.RevealRootLayoutContentEvent;

public class PerspectivesPanelPresenter extends
		Presenter<PerspectivesPanelPresenter.MyView, PerspectivesPanelPresenter.MyProxy> {
    
    @ContentSlot
    public static final GwtEvent.Type<RevealContentHandler<?>> TYPE_MainContent = new GwtEvent.Type<RevealContentHandler<?>>();
    boolean revealDefault = true;

	public interface MyView extends View {
	    void setUserName(String userName);
	}

	@ProxyStandard
	@NameToken(NameTokens.perspective)
	public interface MyProxy extends ProxyPlace<PerspectivesPanelPresenter> {
	}

	@Inject
	public PerspectivesPanelPresenter(final EventBus eventBus, final MyView view,
			final MyProxy proxy, final PlaceManager placeManager) {
		super(eventBus, view, proxy);
	}

    @Override
    protected void revealInParent() {
        RevealRootLayoutContentEvent.fire(this, this);
    }
    
    @Override
    public void prepareFromRequest(PlaceRequest request) {
        super.prepareFromRequest(request);
/*        if(revealDefault && request.getNameToken().equals(NameTokens.perspective))  {
            revealDefault = false;
            placeManager.revealPlace(new PlaceRequest(NameTokens.author));
        }*/
    }
    
	@Override
	protected void onBind() {
		super.onBind();
		setCurrentSeller();
	}

	@Override
	protected void onReset() {
		super.onReset();
		//getView().resetAndFocus();
	}
	
    public void setCurrentSeller() {

    }	
}
