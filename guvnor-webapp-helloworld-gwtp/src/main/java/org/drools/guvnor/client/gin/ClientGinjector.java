package org.drools.guvnor.client.gin;

import org.drools.guvnor.client.editor.AdminAreaPresenter;
import org.drools.guvnor.client.editor.AdminAreaPresenter2;
import org.drools.guvnor.client.editor.PackagePresenter;
import org.drools.guvnor.client.editor.QueryPresenter;
import org.drools.guvnor.client.gin.ClientModule;
import org.drools.guvnor.client.layout.PerspectivesPanelPresenter;

import com.google.gwt.inject.client.GinModules;
import com.gwtplatform.dispatch.client.gin.DispatchAsyncModule;
import com.google.gwt.inject.client.Ginjector;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.google.inject.Provider;

@GinModules({ DispatchAsyncModule.class, ClientModule.class })
public interface ClientGinjector extends Ginjector {

	EventBus getEventBus();

	PlaceManager getPlaceManager();

	Provider<PerspectivesPanelPresenter> getPerspectivesPanelPresenter();
	Provider<AdminAreaPresenter> getAdminAreaPresenter();
	Provider<AdminAreaPresenter2> getAdminAreaPresenter2();
    Provider<QueryPresenter> getQueryPresenter();
    Provider<PackagePresenter> getPackagePresenter();
}
