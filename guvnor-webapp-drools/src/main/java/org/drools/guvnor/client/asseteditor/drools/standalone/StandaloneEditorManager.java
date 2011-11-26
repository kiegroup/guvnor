package org.drools.guvnor.client.asseteditor.drools.standalone;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;

import org.drools.guvnor.client.asseteditor.MultiViewEditor;
import org.drools.guvnor.client.asseteditor.MultiViewEditorMenuBarCreator;
import org.drools.guvnor.client.asseteditor.drools.OryxMultiViewEditorMenuBarCreator;
import org.drools.guvnor.client.asseteditor.drools.modeldriven.ui.RuleModellerConfiguration;
import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.common.LoadingPopup;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.moduleeditor.drools.WorkingSetManager;
import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.guvnor.client.rpc.StandaloneEditorInvocationParameters;
import org.drools.guvnor.client.rpc.StandaloneEditorService;
import org.drools.guvnor.client.rpc.StandaloneEditorServiceAsync;
import org.drools.guvnor.client.widgets.toolbar.StandaloneEditorIndividualActionToolbarButtonsConfigurationProvider;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Class used to manage the stand-alone version of Guvnor's Editors
 */
public class StandaloneEditorManager {

    private final ClientFactory clientFactory;
    private Constants constants = GWT.create(Constants.class);
    private MultiViewEditor editor;
    private StandaloneEditorServiceAsync standaloneEditorService = GWT.create(StandaloneEditorService.class);
    private RuleAsset[] assets;
    private final EventBus eventBus;

    public StandaloneEditorManager(ClientFactory clientFactory, EventBus eventBus) {
        this.clientFactory = clientFactory;
        this.eventBus = eventBus;
    }

    public Panel getBaseLayout() {

        String parametersUUID = Window.Location.getParameter("pUUID");
        if (parametersUUID == null || parametersUUID.trim().equals("")) {
            return null;
        }

        //init JS hooks
        this.setHooks(this);

        DockLayoutPanel mainLayout = new DockLayoutPanel(Unit.EM);

        final ScrollPanel mainPanel = new ScrollPanel();

        mainLayout.add(mainPanel);

        //The package must exist (because we need at least a model to work with)
        //To make things easier (to me), the category must exist too.
        standaloneEditorService.getInvocationParameters(parametersUUID, new GenericCallback<StandaloneEditorInvocationParameters>() {

            public void onSuccess(final StandaloneEditorInvocationParameters parameters) {

                //no assets? This is an error!
                if (parameters.getAssetsToBeEdited().length == 0) {
                    Window.alert(constants.NoRulesFound());
                    return;
                }

                //we need to store the assets.
                StandaloneEditorManager.this.assets = parameters.getAssetsToBeEdited();

                //Load SCE and create a MultiViewEditor for the assets.
                //We take the package from the first asset (because all the assets
                //must belong to the same package)


                Set<String> validFacts = null;
                if (parameters.getValidFactTypes() != null) {
                    validFacts = new HashSet<String>();
                    validFacts.addAll(Arrays.asList(parameters.getValidFactTypes()));
                }
                
                Command afterWorkingSetsAreAppliedCommand = new Command() {

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
                                                  //"Done" buton command

                                                  public void execute() {
                                                      afterCancelButtonCallbackFunction();
                                                  }
                                              }
                                              );
                                          } else if (parameters.getClientName().equalsIgnoreCase("oryx")) {
                                              editorMenuBarCreator = new OryxMultiViewEditorMenuBarCreator(new Command() {
                                                  // "Close" button command
                                                  public void execute() {
                                                      afterCloseButtonCallbackFunction();
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

                                          editor = new MultiViewEditor(
                                                  parameters.getAssetsToBeEdited(),
                                                  clientFactory,
                                                  eventBus,
                                                  new StandaloneEditorIndividualActionToolbarButtonsConfigurationProvider(),
                                                  editorMenuBarCreator);

                                          editor.setCloseCommand(new Command() {

                                              public void execute() {
                                                  afterSaveAndClose();
                                              }
                                          });

                                          //Add the editor to main panel
                                          mainPanel.add(editor);
                                      }
                                  };
                
                //If any Working-Set UUID was provided, use it instead "valid facts"
                if (parameters.getActiveWorkingSets() != null && parameters.getActiveWorkingSets().length >0){
                    WorkingSetManager.getInstance().applyWorkingSets(assets[0].getMetaData().getPackageName(), parameters.getActiveWorkingSets(), afterWorkingSetsAreAppliedCommand);
                }else{
                    //use a temporal working-set
                    WorkingSetManager.getInstance().applyTemporalWorkingSetForFactTypes(assets[0].getMetaData().getPackageName(), validFacts, afterWorkingSetsAreAppliedCommand);
                }
                
                
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

        standaloneEditorService.getAsstesDRL(assets, new GenericCallback<String[]>() {

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

        standaloneEditorService.getAsstesBRL(assets, new GenericCallback<String[]>() {

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
     * Returns the uuids of the assets that are being edited in JSON format.
     *
     * @return
     */
    public String getAssetsUUIDs() {
        StringBuilder uuids = new StringBuilder("[");
        String separator = "";
        for (int i = 0; i < this.assets.length; i++) {
            uuids.append(separator);
            uuids.append("'");
            uuids.append(this.assets[i].getUuid());
            uuids.append("'");
            if (separator.equals("")) {
                separator = ",";
            }
        }
        uuids.append("]");

        return uuids.toString();
    }

    /**
     * Creates 2 JS functions in window object: getDRLs() and getBRLs(). These
     * functions are used to retrieve the source code of the assets this component
     * is handling.
     *
     * @param app
     */
    public native void setHooks(StandaloneEditorManager app)/*-{

        var guvnorEditorObject = {
            drlCallbackFunction: null,
            brlCallbackFunction: null,

            //close function listener. The function you register here will be called
            //after the "Save and Close" button is pressed
            afterSaveAndCloseButtonCallbackFunction: null,

            afterCancelButtonCallbackFunction: null,

            getDRL: function (callbackFunction) {
                this.drlCallbackFunction = callbackFunction;
                app.@org.drools.guvnor.client.asseteditor.drools.standalone.StandaloneEditorManager::getDRLs()();
            },

            getBRL: function (callbackFunction) {
                this.brlCallbackFunction = callbackFunction;
                app.@org.drools.guvnor.client.asseteditor.drools.standalone.StandaloneEditorManager::getBRLs()();
            },

            registerAfterSaveAndCloseButtonCallbackFunction: function (callbackFunction) {
                this.afterSaveAndCloseButtonCallbackFunction = callbackFunction;
            },

            registerAfterCancelButtonCallbackFunction: function (callbackFunction) {
                this.afterCancelButtonCallbackFunction = callbackFunction;
            },

            getAssetsUUIDs: function() {
                return app.@org.drools.guvnor.client.asseteditor.drools.standalone.StandaloneEditorManager::getAssetsUUIDs()();
            }
        }
        $wnd.guvnorEditorObject = guvnorEditorObject;

    }-*/;

    /**
     * Callback method invoked from getDRLs().
     *
     * @param drl
     */
    public native void returnDRL(String drl)/*-{
        if ($wnd.guvnorEditorObject.drlCallbackFunction) {
            $wnd.guvnorEditorObject.drlCallbackFunction(drl);
        }
    }-*/;

    /**
     * Callback method invoked from getDRLs().
     *
     * @param drl
     */
    public native void returnBRL(String brl)/*-{
        if ($wnd.guvnorEditorObject.brlCallbackFunction) {
            $wnd.guvnorEditorObject.brlCallbackFunction(brl);
        }
    }-*/;

    /**
     * Method invoked after the "Save an Close" button is pressed.
     */
    public native void afterSaveAndClose()/*-{
        if ($wnd.guvnorEditorObject.afterSaveAndCloseButtonCallbackFunction) {
            $wnd.guvnorEditorObject.afterSaveAndCloseButtonCallbackFunction();
        }
    }-*/;

    public native void afterCancelButtonCallbackFunction()/*-{
        if ($wnd.guvnorEditorObject.afterCancelButtonCallbackFunction) {
            $wnd.guvnorEditorObject.afterCancelButtonCallbackFunction();
        }
    }-*/;

    public native void afterCloseButtonCallbackFunction()/*-{
        $wnd.opener.location.reload();
        if (confirm("Are you sure you want to close this window?")) {
            $wnd.close();
        }
    }-*/;
}
