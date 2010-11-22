package org.drools.guvnor.client.ruleeditor;

import com.google.gwt.core.client.GWT;
import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.common.LoadingPopup;
import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.guvnor.client.rulelist.EditItemEvent;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.modeldriven.ui.RuleModellerConfiguration;
import org.drools.guvnor.client.packages.WorkingSetManager;
import org.drools.guvnor.client.rpc.StandaloneGuidedEditorService;
import org.drools.guvnor.client.rpc.StandaloneGuidedEditorServiceAsync;
import org.drools.guvnor.client.ruleeditor.standalone.RealAssetsMultiViewEditorMenuBarCreator;
import org.drools.guvnor.client.ruleeditor.standalone.StandaloneGuidedEditorInvocationParameters;
import org.drools.guvnor.client.ruleeditor.standalone.TemporalAssetsMultiViewEditorMenuBarCreator;
import org.drools.guvnor.client.ruleeditor.toolbar.StandaloneGuidedEditorIndividualActionToolbarButtonsConfigurationProvider;

/**
 * Class used to manage the stand-alone version of the Guided Editor (RuleModeller)
 * @author esteban.aliverti@gmail.com
 *
 */
public class GuidedEditorManager {

    private DockLayoutPanel mainLayout;
    private Constants constants = GWT.create(Constants.class);
    private MultiViewEditor editor;
    private StandaloneGuidedEditorServiceAsync standaloneGuidedEditorService = GWT.create(StandaloneGuidedEditorService.class);
    private RuleAsset[] assets;

    public Panel getBaseLayout() {

        String parametersUUID = Window.Location.getParameter("pUUID");
        if (parametersUUID == null || parametersUUID.trim().equals("")) {
            return null;
        }

        //init JS hooks
        this.setHooks(this);

        mainLayout = new DockLayoutPanel(Unit.EM);

        final ScrollPanel mainPanel = new ScrollPanel();

        mainLayout.add(mainPanel);

        //The package must exist (because we need at least a model to work with)
        //To make things easier (to me), the category must exist too.
        standaloneGuidedEditorService.getInvocationParameters(parametersUUID, new GenericCallback<StandaloneGuidedEditorInvocationParameters>() {

            public void onSuccess(final StandaloneGuidedEditorInvocationParameters parameters) {

                //no assets? This is an error!
                if (parameters.getAssetsToBeEdited().length == 0) {
                    Window.alert(constants.NoRulesFound());
                    return;
                }

                //we need to store the assets.
                GuidedEditorManager.this.assets = parameters.getAssetsToBeEdited();

                //Load SCE and create a MultiViewEditor for the assets.
                //We take the package from the first asset (because all the assets
                //must belong to the same package)


                Set<String> validFacts = null;
                if (parameters.getValidFactTypes() != null){
                    validFacts = new HashSet<String>();
                    validFacts.addAll(Arrays.asList(parameters.getValidFactTypes()));
                }
                
                WorkingSetManager.getInstance().applyTemporalWorkingSetForFactTypes(assets[0].metaData.packageName, validFacts, new Command() {

                    public void execute() {
                        LoadingPopup.close();

                        //Configure RuleModeller
                        RuleModellerConfiguration ruleModellerConfiguration = RuleModellerConfiguration.getInstance();
                        ruleModellerConfiguration.setHideLHS(parameters.isHideLHS());
                        ruleModellerConfiguration.setHideRHS(parameters.isHideRHS());
                        ruleModellerConfiguration.setHideAttributes(parameters.isHideAttributes());

                        //Create the editor
                        MultiViewEditorMenuBarCreator editorMenuBarCreator;
                        if (parameters.isTemporalAssets()) {
                            editorMenuBarCreator = new TemporalAssetsMultiViewEditorMenuBarCreator(new Command() {
                                //"Done" buton command

                                public void execute() {
                                    afterSaveAndClose();
                                }
                            }, new Command() {
                                //"Cancel button command

                                public void execute() {
                                    afterCancelButtonCallbackFunction();
                                }
                            });
                        } else {
                            editorMenuBarCreator = new RealAssetsMultiViewEditorMenuBarCreator(new Command() {
                                //"Cancel" button command

                                public void execute() {
                                    afterCancelButtonCallbackFunction();
                                }
                            });
                        }

                        editor = new MultiViewEditor(parameters.getAssetsToBeEdited(), new EditItemEvent() {

                            public void open(MultiViewRow[] rows) {
                                // TODO Auto-generated method stub
                            }

                            public void open(String key) {
                                // TODO Auto-generated method stub
                            }
                        }, new StandaloneGuidedEditorIndividualActionToolbarButtonsConfigurationProvider(),
                                editorMenuBarCreator);

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
     * This method should be invoked from JS using window.getEditorDRL().
     * Returns the DRL of the assets we are editing. Because this method is 
     * asynchronous, the DRL code is passed to a callback function specified
     * in the JS invocation.
     */
    public void getDRLs() {
        if (assets == null || assets.length == 0) {
            returnDRL("");
        }

        standaloneGuidedEditorService.getAsstesDRL(assets, new GenericCallback<String[]>() {

            public void onSuccess(String[] drls) {
                String result = "";
                if (drls != null) {
                    for (String drl : drls) {
                        result += drl + "\n\n";
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
    public void getBRLs() {
        if (assets == null || assets.length == 0) {
            returnDRL("");
        }

        standaloneGuidedEditorService.getAsstesBRL(assets, new GenericCallback<String[]>() {

            public void onSuccess(String[] drls) {
                String result = "";
                if (drls != null) {
                    for (String drl : drls) {
                        result += drl + "\n\n";
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
    
    var guidedEditorObject = {
    drlCallbackFunction: null,
    brlCallbackFunction: null,
    
    //close function listener. The function you register here will be called
    //after the "Save and Close" button is pressed                                                                                                                 
    afterSaveAndCloseButtonCallbackFunction: null,
    
    afterCancelButtonCallbackFunction: null,
    
    getDRL: function (callbackFunction){
    this.drlCallbackFunction = callbackFunction;
    app.@org.drools.guvnor.client.ruleeditor.GuidedEditorManager::getDRLs()();
    },
    
    getBRL: function (callbackFunction){
    this.brlCallbackFunction = callbackFunction;
    app.@org.drools.guvnor.client.ruleeditor.GuidedEditorManager::getBRLs()();
    },
    
    registerAfterSaveAndCloseButtonCallbackFunction: function (callbackFunction){
    this.afterSaveAndCloseButtonCallbackFunction = callbackFunction;
    },
    
    registerAfterCancelButtonCallbackFunction: function (callbackFunction){
    this.afterCancelButtonCallbackFunction = callbackFunction;
    }
    }    
    $wnd.guidedEditorObject = guidedEditorObject;                                                                                                      
    
    }-*/;

    /**
     * Callback method invoked from getDRLs().
     * @param drl
     */
    public native void returnDRL(String drl)/*-{
    if ($wnd.guidedEditorObject.drlCallbackFunction){
    $wnd.guidedEditorObject.drlCallbackFunction(drl);
    }
    }-*/;

    /**
     * Callback method invoked from getDRLs().
     * @param drl
     */
    public native void returnBRL(String brl)/*-{
    if ($wnd.guidedEditorObject.brlCallbackFunction){
    $wnd.guidedEditorObject.brlCallbackFunction(brl);
    }
    }-*/;

    /**
     * Method invoked after the "Save an Close" button is pressed. 
     */
    public native void afterSaveAndClose()/*-{
    if ($wnd.guidedEditorObject.afterSaveAndCloseButtonCallbackFunction){
    $wnd.guidedEditorObject.afterSaveAndCloseButtonCallbackFunction();
    }
    }-*/;

    public native void afterCancelButtonCallbackFunction()/*-{
    if ($wnd.guidedEditorObject.afterCancelButtonCallbackFunction){
    $wnd.guidedEditorObject.afterCancelButtonCallbackFunction();
    }
    }-*/;
}
