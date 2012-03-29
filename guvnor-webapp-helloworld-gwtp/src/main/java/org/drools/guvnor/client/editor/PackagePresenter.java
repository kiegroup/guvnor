
package org.drools.guvnor.client.editor;

import org.drools.guvnor.client.layout.PerspectivesPanelPresenter;
import org.drools.guvnor.client.place.NameTokens;
import org.drools.guvnor.shared.LoadModuleAction;
import org.drools.guvnor.shared.LoadModuleResult;
import org.drools.guvnor.shared.Module;

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

public class PackagePresenter
    extends Presenter<PackagePresenter.MyView, PackagePresenter.MyProxy>  {
    @ProxyStandard
    @NameToken(NameTokens.packageEditor)
    public interface MyProxy extends ProxyPlace<PackagePresenter> {
    }

    public interface MyView extends View {
        void setModule(Module module);
    }
    
    private final DispatchAsync dispatcher;

    @Inject
    public PackagePresenter(final EventBus eventBus, final MyView view, final MyProxy proxy, DispatchAsync dispatcher) {
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

        dispatcher.execute(new LoadModuleAction("defaultPackage"),
            new AsyncCallback<LoadModuleResult>() {
              @Override
              public void onFailure(Throwable caught) {
                //TODO: 
              }

              @Override
              public void onSuccess(LoadModuleResult result) {
                getView().setModule(result.getModule());
              }
            });
    }
    
    @Override
    protected void onReset() {
      super.onReset();
    }
}