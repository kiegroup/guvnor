
package org.drools.guvnor.client.editor;

import org.drools.guvnor.client.layout.PerspectivesPanelPresenter;
import org.drools.guvnor.client.place.NameTokens;

import com.google.web.bindery.event.shared.EventBus;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyStandard;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;

public class AdminAreaPresenter
    extends Presenter<AdminAreaPresenter.MyView, AdminAreaPresenter.MyProxy>  {
    @ProxyStandard
    @NameToken(NameTokens.helloWorld)
    public interface MyProxy extends ProxyPlace<AdminAreaPresenter> {
    }

    public interface MyView extends View {
    }

    @Inject
    public AdminAreaPresenter(final EventBus eventBus, final MyView view, final MyProxy proxy) {
        super(eventBus, view, proxy);
    }

    @Override
    protected void revealInParent() {
        RevealContentEvent.fire(this, PerspectivesPanelPresenter.TYPE_MainContent, this);
    }
    
    @Override
    protected void onBind() {
        super.onBind();
    }
}