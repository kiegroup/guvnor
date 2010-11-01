package org.drools.guvnor.client.ruleeditor;

import com.google.gwt.core.client.GWT;
import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.common.LoadingPopup;
import org.drools.guvnor.client.packages.SuggestionCompletionCache;
import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.guvnor.client.rulelist.EditItemEvent;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;
import java.util.ArrayList;
import java.util.List;
import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.rpc.StandaloneGuidedEditorService;
import org.drools.guvnor.client.rpc.StandaloneGuidedEditorServiceAsync;
import org.drools.ide.common.client.modeldriven.brl.RuleModel;

/**
 * Class used to manage the stand-alone version of the Guided Editor (RuleModeller)
 * @author esteban.aliverti@gmail.com
 *
 */
public class GuidedEditorManager {

    private DockLayoutPanel mainLayout;

    private Constants constants = GWT.create(Constants.class);
    
    public Panel getBaseLayout() {
        
        mainLayout = new DockLayoutPanel(Unit.EM);
        
        final ScrollPanel mainPanel = new ScrollPanel(); 
        
        mainLayout.add(mainPanel);

        //The package must exist (because we need at least a model to work with)
        //To make things easier (to me), the category must exist too.
        StandaloneGuidedEditorServiceAsync standaloneGuidedEditorService = GWT.create( StandaloneGuidedEditorService.class );
        
        standaloneGuidedEditorService.loadRuleAssetsFromSession(new GenericCallback<RuleAsset[]>() {

            public void onSuccess(final RuleAsset[] assets) {
                //no assets? This is an error!
                if (assets.length == 0){
                    Window.alert(constants.NoRulesFound());
                    return;
                }
                
                SuggestionCompletionCache.getInstance().loadPackage(assets[0].metaData.packageName, new Command() {

                    public void execute() {
                        LoadingPopup.close();

                        List<MultiViewRow> rows = new ArrayList<MultiViewRow>();
                        for (RuleAsset ruleAsset : assets) {
                            MultiViewRow row = new MultiViewRow();
                            row.uuid = ruleAsset.uuid;
                            row.name = ((RuleModel)ruleAsset.content).name;
                            row.format = AssetFormats.BUSINESS_RULE;
                            rows.add(row);
                        }
                        
                        MultiViewEditor viewer = new MultiViewEditor(rows.toArray(new MultiViewRow[rows.size()]), new EditItemEvent() {

                            public void open(MultiViewRow[] rows) {
                                // TODO Auto-generated method stub
                            }

                            public void open(String key) {
                                // TODO Auto-generated method stub
                            }
                        });

                        mainPanel.add(viewer);
                    }
                });
            }

        });


        return mainLayout;
    }

}
