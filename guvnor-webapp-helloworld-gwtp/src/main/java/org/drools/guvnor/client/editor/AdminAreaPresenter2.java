
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

public class AdminAreaPresenter2
    extends Presenter<AdminAreaPresenter2.MyView, AdminAreaPresenter2.MyProxy>  {
    @ProxyStandard
    @NameToken(NameTokens.helloWorld2)
    public interface MyProxy extends ProxyPlace<AdminAreaPresenter2> {
    }

    public interface MyView extends View {
    }

    @Inject
    public AdminAreaPresenter2(final EventBus eventBus, final MyView view, final MyProxy proxy) {
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