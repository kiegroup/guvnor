package org.drools.guvnor.client.gin;

import org.drools.guvnor.client.editor.AdminAreaPresenter;
import org.drools.guvnor.client.editor.AdminAreaPresenter2;
import org.drools.guvnor.client.editor.AdminAreaView;
import org.drools.guvnor.client.editor.AdminAreaView2;
import org.drools.guvnor.client.editor.PackagePresenter;
import org.drools.guvnor.client.editor.PackageView;
import org.drools.guvnor.client.editor.QueryPresenter;
import org.drools.guvnor.client.editor.QueryView;
import org.drools.guvnor.client.explorer.BrowseTree;
import org.drools.guvnor.client.explorer.KnowledgeTree;
import org.drools.guvnor.client.layout.ExplorerViewCenterPanel;
import org.drools.guvnor.client.layout.PerspectivesPanelPresenter;
import org.drools.guvnor.client.layout.PerspectivesPanelViewImpl;
import org.drools.guvnor.client.place.ClientPlaceManager;
import org.drools.guvnor.client.place.DefaultPlace;
import org.drools.guvnor.client.place.NameTokens;

import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;
import com.gwtplatform.mvp.client.gin.DefaultModule;


public class ClientModule extends AbstractPresenterModule {

	@Override
	protected void configure() {
		install(new DefaultModule(ClientPlaceManager.class));

		bindConstant().annotatedWith(DefaultPlace.class).to(NameTokens.find);
		
		bindPresenter(PerspectivesPanelPresenter.class, PerspectivesPanelPresenter.MyView.class,
				PerspectivesPanelViewImpl.class, PerspectivesPanelPresenter.MyProxy.class);
		
		bindPresenter(AdminAreaPresenter.class, AdminAreaPresenter.MyView.class,
				AdminAreaView.class, AdminAreaPresenter.MyProxy.class);
		
		bindPresenter(AdminAreaPresenter2.class, AdminAreaPresenter2.MyView.class,
				AdminAreaView2.class, AdminAreaPresenter2.MyProxy.class);
              
        bindPresenter(QueryPresenter.class, QueryPresenter.MyView.class,
                QueryView.class, QueryPresenter.MyProxy.class);

        bindPresenter(PackagePresenter.class, PackagePresenter.MyView.class,
                PackageView.class, PackagePresenter.MyProxy.class);
  
        requestStaticInjection(ExplorerViewCenterPanel.class); 
        requestStaticInjection(BrowseTree.class); 
        requestStaticInjection(KnowledgeTree.class); 
	}
}
