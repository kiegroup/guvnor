
package org.drools.guvnor.client.editor;

import org.drools.guvnor.client.event.RefreshAdminAreaEvent;
import org.drools.guvnor.client.layout.PerspectivesPanelPresenter;
import org.drools.guvnor.client.place.NameTokens;
import org.drools.guvnor.shared.CreateModuleAction;
import org.drools.guvnor.shared.CreateModuleResult;

import com.google.web.bindery.event.shared.EventBus;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.gwtplatform.dispatch.shared.DispatchAsync;
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
        void setModuleUUID(String uuid);
    }
    
    private final DispatchAsync dispatcher;

    @Inject
    public AdminAreaPresenter(final EventBus eventBus, final MyView view, final MyProxy proxy, DispatchAsync dispatcher) {
        super(eventBus, view, proxy);
        this.dispatcher = dispatcher;

    }

    @Override
    protected void revealInParent() {
        RevealContentEvent.fire(this, PerspectivesPanelPresenter.TYPE_MainContent, this);
    }
    
    @Override
    protected void onBind() {
        super.onBind();
    }
    
    @Override
    protected void onReset() {
      super.onReset();

      dispatcher.execute(new CreateModuleAction("defaultPackage"),
          new AsyncCallback<CreateModuleResult>() {
            @Override
            public void onFailure(Throwable caught) {
              //TODO: 
            }

            @Override
            public void onSuccess(CreateModuleResult result) {
              getView().setModuleUUID(result.getUUID());
            }
          });
    }
    
    private void fireRefreshEvent() {
    	RefreshAdminAreaEvent.fire(this);
    }
}