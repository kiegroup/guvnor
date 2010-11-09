package org.drools.guvnor.client.ruleeditor;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window.ClosingEvent;
import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.common.LoadingPopup;
import org.drools.guvnor.client.packages.SuggestionCompletionCache;
import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.guvnor.client.rulelist.EditItemEvent;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;
import java.util.ArrayList;
import java.util.List;
import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.modeldriven.ui.RuleModellerConfiguration;
import org.drools.guvnor.client.rpc.StandaloneGuidedEditorService;
import org.drools.guvnor.client.rpc.StandaloneGuidedEditorServiceAsync;
import org.drools.guvnor.client.ruleeditor.standalone.StandaloneGuidedEditorInvocationParameters;
import org.drools.guvnor.client.ruleeditor.toolbar.StandaloneGuidedEditorIndividualActionToolbarButtonsConfigurationProvider;
import org.drools.ide.common.client.modeldriven.brl.RuleModel;

/**
 * Class used to manage the stand-alone version of the Guided Editor (RuleModeller)
 * @author esteban.aliverti@gmail.com
 *
 */
public class GuidedEditorManager {

    private DockLayoutPanel mainLayout;

    private Constants constants = GWT.create(Constants.class);
    
    private MultiViewEditor editor;
    
    private StandaloneGuidedEditorServiceAsync standaloneGuidedEditorService = GWT.create( StandaloneGuidedEditorService.class );
    
    private String[] assetsUids;
    
    private Window.ClosingHandler windowCloseingHandler = new Window.ClosingHandler() {

                public void onWindowClosing(ClosingEvent event) {
                    removeAssets();
                }
            };
    
    public Panel getBaseLayout() {
        
        //init JS hoocks
        this.setHooks(this);
        
        //remove assets on close
        if (Boolean.parseBoolean(Window.Location.getParameter("removeAssetsOnClose"))){
            Window.addWindowClosingHandler(windowCloseingHandler);
        }
        
        mainLayout = new DockLayoutPanel(Unit.EM);
        
        final ScrollPanel mainPanel = new ScrollPanel(); 
        
        mainLayout.add(mainPanel);

        //The package must exist (because we need at least a model to work with)
        //To make things easier (to me), the category must exist too.
        standaloneGuidedEditorService.getInvocationParameters(new GenericCallback<StandaloneGuidedEditorInvocationParameters>() {

            public void onSuccess(final StandaloneGuidedEditorInvocationParameters parameters) {
                GuidedEditorManager.this.assetsUids = new String[parameters.getAssetsToBeEdited().length];
                
                //no assets? This is an error!
                if (parameters.getAssetsToBeEdited().length == 0){
                    Window.alert(constants.NoRulesFound());
                    return;
                }
               
                //we need to store the uids of each asset.
                for (int i = 0; i < parameters.getAssetsToBeEdited().length; i++) {
                    RuleAsset ruleAsset = parameters.getAssetsToBeEdited()[i];
                    GuidedEditorManager.this.assetsUids[i] = ruleAsset.uuid;
                }
                
                //Load SCE and create a MultiViewEditor for the assets.
                //We take the package from the first asset (because all the assets
                //must belong to the same package)
                SuggestionCompletionCache.getInstance().loadPackage(parameters.getAssetsToBeEdited()[0].metaData.packageName, new Command() {

                    public void execute() {
                        
//                        Set<String> validFacts = new HashSet<String>();
//                        validFacts.add("LoanApplication");
//                        
//                        SuggestionCompletionCache.getInstance().applyFactFilter(assets[0].metaData.packageName, new SetFactTypeFilter(validFacts), new Command() {
//
//                            public void execute() {
//                                throw new UnsupportedOperationException("Not supported yet.");
//                            }
//                        });
                        
                        LoadingPopup.close();

                        //Configure RuleModeller
                        RuleModellerConfiguration ruleModellerConfiguration = RuleModellerConfiguration.getInstance();
                        ruleModellerConfiguration.setHideLHS(parameters.isHideLHS());
                        ruleModellerConfiguration.setHideRHS(parameters.isHideRHS());
                        ruleModellerConfiguration.setHideAttributes(parameters.isHideAttributes());
                        
                        //Create the editor
                        editor = new MultiViewEditor(parameters.getAssetsToBeEdited(), new EditItemEvent() {

                            public void open(MultiViewRow[] rows) {
                                // TODO Auto-generated method stub
                            }

                            public void open(String key) {
                                // TODO Auto-generated method stub
                            }
                        }, new StandaloneGuidedEditorIndividualActionToolbarButtonsConfigurationProvider());

                        editor.setCloseCommand(new Command() {

                            public void execute() {
                                afterSaveAndClose();
                            }
                        });
                        
                        //Add the editor to main panel
                        mainPanel.add(editor);
                    }
                });
            }

        });

        
        return mainLayout;
    }
    
    /**
     * Remove the assets used by this Guided Editor instance
     */
    public void removeAssets(){
        standaloneGuidedEditorService.removeAssets(assetsUids, new AsyncCallback<Void>() {

            public void onFailure(Throwable caught) {
                removeAssetsCallback(false,caught.getMessage());
            }

            public void onSuccess(Void result) {
                LoadingPopup.showMessage("Assets removed");
                removeAssetsCallback(true,"Assets removed");
            }
        });
    }
    
    /**
     * This method should be invoked from JS using window.getEditorDRL().
     * Returns the DRL of the assets we are editing. Because this method is 
     * asynchronous, the DRL code is passed to a callback function specified
     * in the JS invocation.
     */
    public void getDRLs(){        
        if (assetsUids == null || assetsUids.length == 0){
            returnDRL("");
        }
        
        standaloneGuidedEditorService.getAsstesDRL(assetsUids, new GenericCallback<String[]>() {

            public void onSuccess(String[] drls) {
                String result = "";
                if (drls != null){
                    for (String drl : drls) {
                        result+=drl+"\n\n";
                    }
                }

                returnDRL(result);
            }
        });
    }
    
    /**
     * This method should be invoked from JS using window.getEditorBRL().
     * Returns the BRL of the assets we are editing. Because this method is 
     * asynchronous, the BRL code is passed to a callback function specified
     * in the JS invocation.
     */
    public void getBRLs(){        
        if (assetsUids == null || assetsUids.length == 0){
            returnDRL("");
        }
        
        standaloneGuidedEditorService.getAsstesBRL(assetsUids, new GenericCallback<String[]>() {

            public void onSuccess(String[] drls) {
                String result = "";
                if (drls != null){
                    for (String drl : drls) {
                        result+=drl+"\n\n";
                    }
                }

                returnBRL(result);
            }
        });
    }
    
    /**
     * Creates 2 JS functions in window object: getDRLs() and getBRLs(). These
     * functions are used to retrieve the source code of the assets this component
     * is handling.
     * @param app
     */
    public native void setHooks(GuidedEditorManager app)/*-{
        
        $wnd.getEditorDRL = function (callbackFunction) {
            $wnd.guvnorGuidedEditorDRLCallbackFunction = callbackFunction;
            app.@org.drools.guvnor.client.ruleeditor.GuidedEditorManager::getDRLs()();
        };
                                                          
        $wnd.getEditorBRL = function (callbackFunction) {
            $wnd.guvnorGuidedEditorBRLCallbackFunction = callbackFunction;
            app.@org.drools.guvnor.client.ruleeditor.GuidedEditorManager::getBRLs()();
        };
                                                          
                                                         
        $wnd.removeAssets = function (callbackFunction) {
            $wnd.guvnorGuidedEditorRemoveAssetsCallbackFunction = callbackFunction;
            app.@org.drools.guvnor.client.ruleeditor.GuidedEditorManager::removeAssets()();
        };           
       
        //close function listener. The function you register here will be called
        //after the "Save and Close" button is pressed                                                                                                                 
        $wnd.guvnorGuidedEditorOnSaveAndCloseFunction=null;
                                                         
                                                         
    }-*/;
    
    /**
     * Callback method invoked from getDRLs().
     * @param drl
     */
    public native void returnDRL(String drl)/*-{
        if ($wnd.guvnorGuidedEditorDRLCallbackFunction){
            $wnd.guvnorGuidedEditorDRLCallbackFunction(drl);
        }
    }-*/;
    
    /**
     * Callback method invoked from getDRLs().
     * @param drl
     */
    public native void returnBRL(String brl)/*-{
        if ($wnd.guvnorGuidedEditorBRLCallbackFunction){
            $wnd.guvnorGuidedEditorBRLCallbackFunction(brl);
        }
    }-*/;
    
    /**
     * Callback method invoked from removeAssets().
     * @param success
     * @param message
     */
    public native void removeAssetsCallback(boolean success, String message)/*-{
        if ($wnd.guvnorGuidedEditorRemoveAssetsCallbackFunction){
            $wnd.guvnorGuidedEditorRemoveAssetsCallbackFunction(success, message);
        }
    }-*/;
    
    /**
     * Method invoked after the "Save an Close" button is pressed. 
     */
    public native void afterSaveAndClose()/*-{
        if ($wnd.guvnorGuidedEditorOnSaveAndCloseFunction){
            $wnd.guvnorGuidedEditorOnSaveAndCloseFunction();
        }
    }-*/;
}
