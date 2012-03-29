
package org.drools.guvnor.client.editor;

import org.drools.guvnor.client.layout.PerspectivesPanelPresenter;
import org.drools.guvnor.client.place.NameTokens;
import org.drools.guvnor.shared.QueryFullTextAction;
import org.drools.guvnor.shared.QueryFullTextResult;

import com.google.web.bindery.event.shared.EventBus;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.inject.Inject;
import com.gwtplatform.dispatch.shared.DispatchAsync;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyStandard;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;

public class QueryPresenter
    extends Presenter<QueryPresenter.MyView, QueryPresenter.MyProxy>  {
    @ProxyStandard
    @NameToken(NameTokens.find)
    public interface MyProxy extends ProxyPlace<QueryPresenter> {
    }

    public interface MyView extends View {
        Button getTextSearchButton();
        String getSearchText();   
    }
    
    private final DispatchAsync dispatcher;

    @Inject
    public QueryPresenter(final EventBus eventBus, final MyView view, final MyProxy proxy, DispatchAsync dispatcher) {
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
        registerHandler(getView().getTextSearchButton().addClickHandler(
                new ClickHandler() {
                    public void onClick(ClickEvent arg0) {
                        dispatcher.execute(new QueryFullTextAction(getView().getSearchText()),
                            new AsyncCallback<QueryFullTextResult>() {
                                  @Override
                                  public void onFailure(Throwable caught) {
                                  }

                                  @Override
                                  public void onSuccess(QueryFullTextResult result) {
                                      //TODO:
                                  }
                        });
                        
                    }
                }));
    }
    
    @Override
    protected void onReset() {
      super.onReset();
    }

}