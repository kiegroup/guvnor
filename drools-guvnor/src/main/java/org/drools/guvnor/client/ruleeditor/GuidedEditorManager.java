package org.drools.guvnor.client.ruleeditor;

import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.modeldriven.ui.RuleModeller;
import org.drools.guvnor.client.modeldriven.ui.RuleModellerWidgetFactory;
import org.drools.guvnor.client.packages.SuggestionCompletionCache;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;
import org.drools.guvnor.client.rpc.RuleAsset;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Panel;

/**
 * Class used to manage the stand-alone version of the Guided Editor (RuleModeller)
 * @author esteban.aliverti@gmail.com
 *
 */
public class GuidedEditorManager {

	private DockLayoutPanel	mainPanel;
	
	public Panel getBaseLayout() {
		mainPanel = new DockLayoutPanel( Unit.EM );
		
		//Just a POC: open a hardcoded rule asset.
		RepositoryServiceFactory.getService().loadRuleAsset("968c9b3c-bc19-40ba-bb38-44435956ccee", new GenericCallback<RuleAsset>() {
            public void onSuccess(final RuleAsset asset) {
            	SuggestionCompletionCache.getInstance().loadPackage("mortgages", new Command() {
					
					public void execute() {
						RuleModeller modeller = new RuleModeller(asset, new RuleModellerWidgetFactory());
		            	mainPanel.add(modeller);
					}
				});
            	
            }			
        } );	
		
		return mainPanel;
	}

}
