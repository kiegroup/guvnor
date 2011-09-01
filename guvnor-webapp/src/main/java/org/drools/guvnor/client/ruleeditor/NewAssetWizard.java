/*
 * Copyright 2005 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.guvnor.client.ruleeditor;

import org.drools.guvnor.client.categorynav.CategoryExplorerWidget;
import org.drools.guvnor.client.categorynav.CategorySelectHandler;
import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.common.FormStyleLayout;
import org.drools.guvnor.client.common.FormStylePopup;
import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.common.GlobalAreaAssetSelector;
import org.drools.guvnor.client.common.LoadingPopup;
import org.drools.guvnor.client.common.RulePackageSelector;
import org.drools.guvnor.client.explorer.AssetEditorPlace;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.explorer.RefreshModuleEditorEvent;
import org.drools.guvnor.client.explorer.RefreshSuggestionCompletionEngineEvent;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.packages.SuggestionCompletionCache;
import org.drools.guvnor.client.resources.Images;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;
import org.drools.guvnor.client.widgets.wizards.WizardPlace;
import org.drools.guvnor.client.widgets.wizards.assets.NewAssetWizardContext;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * This provides a popup for creating a new rule/asset from scratch. reuses a
 * few other widgets.
 */
public class NewAssetWizard extends FormStylePopup {

    private static Constants                 constants               = GWT.create( Constants.class );
    private static Images                    images                  = GWT.create( Images.class );

    private TextBox                          name                    = new TextBox();
    private TextArea                         description             = new TextArea();
    private CheckBox                         chkUseWizard            = null;
    private String                           initialCategory;

    private ListBox                          formatChooser           = getFormatChooser();
    private RadioButton                      createInPackageButton   = new RadioButton( "creatinpackagegroup",
                                                                                        constants.CreateInPackage() );
    private RadioButton                      createInGlobalButton    = new RadioButton( "creatinpackagegroup",
                                                                                        constants.CreateInGlobalArea() );

    private RulePackageSelector              packageSelector         = new RulePackageSelector();
    private RulePackageSelector              importedPackageSelector = new RulePackageSelector();
    private GlobalAreaAssetSelector          globalAreaAssetSelector;
    private String                           format;

    private final NewAssetFormStyleLayout    newAssetLayout          = new NewAssetFormStyleLayout();
    private final ImportAssetFormStyleLayout importAssetLayout       = new ImportAssetFormStyleLayout();
    private final ClientFactory              clientFactory;
    private final EventBus                   eventBus;

    private static String getTitle(String format) {
        if ( format == null ) return constants.NewRule();
        else if ( format.equals( AssetFormats.SPRING_CONTEXT ) ) return constants.NewSpringContext();
        else if ( format.equals( AssetFormats.WORKING_SET ) ) return constants.NewWorkingSet();
        else if ( format.equals( AssetFormats.RULE_TEMPLATE ) ) return constants.NewRuleTemplate();
        else if ( format.equals( AssetFormats.MODEL ) ) return constants.NewModelArchiveJar();
        else if ( format.equals( AssetFormats.DRL_MODEL ) ) return constants.NewDeclarativeModelUsingGuidedEditor();
        else if ( format.equals( AssetFormats.BPEL_PACKAGE ) ) return constants.CreateANewBPELPackage();
        else if ( format.equals( AssetFormats.FUNCTION ) ) return constants.CreateANewFunction();
        else if ( format.equals( AssetFormats.DSL ) ) return constants.CreateANewDSLConfiguration();
        else if ( format.equals( AssetFormats.RULE_FLOW_RF ) ) return constants.CreateANewRuleFlow();
        else if ( format.equals( AssetFormats.BPMN2_PROCESS ) ) return constants.CreateANewBPMN2Process();
        else if ( format.equals( AssetFormats.WORKITEM_DEFINITION ) ) return constants.NewWorkitemDefinition();
        else if ( format.equals( AssetFormats.ENUMERATION ) ) return constants.CreateANewEnumerationDropDownMapping();
        else if ( format.equals( AssetFormats.TEST_SCENARIO ) ) return constants.CreateATestScenario();
        else if ( format.equals( "*" ) ) return constants.CreateAFile();

        return "";
    }

    /**
     * This is used when creating a new rule.
     */
    public NewAssetWizard(boolean showCategories,
                           String format,
                           ClientFactory clientFactory,
                           EventBus eventBus) {
        super( images.newWiz(),
                getTitle( format ) );
        this.format = format;
        this.clientFactory = clientFactory;
        this.eventBus = eventBus;

        RadioButton newPackage = new RadioButton( "layoutgroup",
                                                  constants.CreateNewAsset() ); // NON-NLS
        newPackage.setValue( true );
        RadioButton importPackage = new RadioButton( "layoutgroup",
                                                     constants.CreateLinkedAsset() ); // NON-NLS

        newAssetLayout.setVisible( true );

        createClickHandlerForNewPackageButton( newPackage );
        importAssetLayout.setVisible( false );
        createClickHandlerForImportPackageButton( importPackage );
        addAttribute( "",
                      createVerticalPanelFor( newPackage,
                                              importPackage ) );
        addRow( newAssetLayout );
        addRow( importAssetLayout );

        newAssetLayout.addAttribute( constants.NameColon(),
                                     name );

        setAfterShowCommand();

        if ( showCategories ) {
            newAssetLayout.addAttribute( constants.InitialCategory(),
                                         getCatChooser() );
        }

        handleLayoutForFormat( format );

        createInPackageButton.setValue( true );
        newAssetLayout.buildNewAssetFormStyleLayout();

        globalAreaAssetSelector = new GlobalAreaAssetSelector( format );
        importAssetLayout.buildImportAssetLayout();

    }

    private class ImportAssetFormStyleLayout extends FormStyleLayout {
        protected void buildImportAssetLayout() {
            this.addAttribute( constants.AssetToImport(),
                               globalAreaAssetSelector );
            this.addAttribute( constants.Package() + ":",
                               importedPackageSelector );
            this.addAttribute( "",
                               createLinkedAssetOkButtonAndClickHandler() );
            this.addRow( new HTML( "<br/><b>" + constants.NoteNewLinkedAsset() + "</b>" ) );
            this.addRow( new HTML( constants.NewLinkedAssetDesc1() ) );
        }

        private Button createLinkedAssetOkButtonAndClickHandler() {
            Button linkedAssetOKButton = new Button( constants.OK() );
            linkedAssetOKButton.addClickHandler( new ClickHandler() {
                public void onClick(ClickEvent event) {
                    importOK();
                }
            } );
            return linkedAssetOKButton;
        }

    }

    private class NewAssetFormStyleLayout extends FormStyleLayout {
        protected void buildNewAssetFormStyleLayout() {
            this.addAttribute( "",
                               createHorizontalePanelFor() );
            this.addAttribute( "",
                               createInGlobalButton );
            buildDescriptionTextArea( format );
            this.addAttribute( constants.InitialDescription(),
                               description );
            this.addAttribute( "",
                               createOkButtonAndClickHandler() );
        }

        private void buildDescriptionTextArea(String format) {
            description.setVisibleLines( 4 );
            description.setWidth( "100%" );
            if ( AssetFormats.DSL_TEMPLATE_RULE.equals( format ) ) {
                description.setText( constants.DSLMappingTip() );
            } else if ( AssetFormats.ENUMERATION.equals( format ) ) {
                description.setText( constants.NewEnumDoco() );
            } else if ( format == AssetFormats.SPRING_CONTEXT ) {
                description.setText( constants.DescSpringContext() );
            } else if ( format == AssetFormats.WORKITEM_DEFINITION ) {
                description.setText( constants.DeskWorkItemDefinition() );
            }
        }
    }

    private Button createOkButtonAndClickHandler() {
        Button ok = new Button( constants.OK() );
        ok.addClickHandler( new ClickHandler() {
            public void onClick(ClickEvent event) {
                ok();
            }
        } );
        return ok;
    }

    private HorizontalPanel createHorizontalePanelFor() {
        HorizontalPanel hp = new HorizontalPanel();
        hp.add( createInPackageButton );
        hp.add( packageSelector );
        return hp;
    }

    private void handleLayoutForFormat(String format) {
        if ( format == null ) {
            newAssetLayout.addAttribute( constants.TypeFormatOfRule(),
                                         this.formatChooser );

            //Add additional widget (when creating a new rule) to allow for use of a Wizard
            chkUseWizard = new CheckBox();
            final int useWizardRowIndex = newAssetLayout.addAttribute( constants.UseWizardToBuildAsset(),
                                                                       chkUseWizard );
            newAssetLayout.setAttributeVisibility( useWizardRowIndex,
                                                   false );

            //TODO Removed until the Wizard has been completed
            //            this.formatChooser.addChangeHandler( new ChangeHandler() {
            //
            //                public void onChange(ChangeEvent event) {
            //                    boolean isVisible = false;
            //                    int selectedIndex = formatChooser.getSelectedIndex();
            //                    if ( selectedIndex >= 0 ) {
            //                        String value = formatChooser.getValue( selectedIndex );
            //                        isVisible = AssetFormats.DECISION_TABLE_GUIDED.equals( value );
            //                    }
            //                    newAssetLayout.setAttributeVisibility( useWizardRowIndex,
            //                                                           isVisible );
            //                    if ( chkUseWizard != null ) {
            //                        chkUseWizard.setValue( false );
            //                    }
            //                }
            //
            //            } );

        } else if ( "*".equals( format ) ) { //NON-NLS
            final TextBox fmt = new TextBox();
            newAssetLayout.addAttribute( constants.FileExtensionTypeFormat(),
                                         fmt );
            fmt.addChangeHandler( new ChangeHandler() {
                public void onChange(ChangeEvent event) {
                    NewAssetWizard.this.format = fmt.getText();
                }
            } );
        }
    }

    private void setAfterShowCommand() {
        this.setAfterShow( new Command() {
            public void execute() {
                name.setFocus( true );
            }
        } );
    }

    private VerticalPanel createVerticalPanelFor(RadioButton newPackage,
                                                  RadioButton importPackage) {
        VerticalPanel ab = new VerticalPanel();
        ab.add( newPackage );
        ab.add( importPackage );
        return ab;
    }

    private void createClickHandlerForImportPackageButton(RadioButton importPackage) {
        importPackage.addClickHandler( new ClickHandler() {
            public void onClick(ClickEvent event) {
                newAssetLayout.setVisible( false );
                importAssetLayout.setVisible( true );
            }
        } );
    }

    private void createClickHandlerForNewPackageButton(RadioButton newPackage) {
        newPackage.addClickHandler( new ClickHandler() {
            public void onClick(ClickEvent event) {
                newAssetLayout.setVisible( true );
                importAssetLayout.setVisible( false );
            }
        } );
    }

    private Widget getCatChooser() {

        Widget w = new CategoryExplorerWidget( new CategorySelectHandler() {
            public void selected(String selectedPath) {
                initialCategory = selectedPath;
            }
        } );
        ScrollPanel scroll = new ScrollPanel( w );
        scroll.setAlwaysShowScrollBars( true );
        scroll.setSize( "300px",
                        "130px" ); //NON-NLS
        return scroll;

    }

    private ListBox getFormatChooser() {

        final ListBox box = new ListBox();

        box.addItem( constants.BusinessRuleGuidedEditor(),
                     AssetFormats.BUSINESS_RULE );
        box.addItem( constants.DSLBusinessRuleTextEditor(),
                     AssetFormats.DSL_TEMPLATE_RULE );
        box.addItem( constants.DRLRuleTechnicalRuleTextEditor(),
                     AssetFormats.DRL );
        box.addItem( constants.DecisionTableSpreadsheet(),
                     AssetFormats.DECISION_SPREADSHEET_XLS );
        box.addItem( constants.DecisionTableWebGuidedEditor(),
                     AssetFormats.DECISION_TABLE_GUIDED );

        box.setSelectedIndex( 0 );

        return box;
    }

    /**
     * When OK is pressed, it will update the repository with the new rule.
     */
    void ok() {

        if ( "*".equals( getFormat() ) ) {
            Window.alert( constants.PleaseEnterAFormatFileType() );
            return;
        }

        String assetName = name.getText();
        if ( "".equals( assetName ) ) {
            Window.alert( constants.InvalidModelName( assetName ) );
            return;
        }

        final String packageName;
        if ( createInGlobalButton.getValue() ) {
            packageName = "globalArea";
        } else {
            packageName = packageSelector.getSelectedPackage();
        }

        //If using a Wizard we don't attempt to create and save the asset until the Wizard is completed. Using commands make this simpler
        Command cmd = null;
        if ( usingWizard() ) {
            cmd = makeWizardSaveCommand( assetName,
                                         packageName,
                                         packageSelector.getSelectedPackageUUID(),
                                         description.getText(),
                                         initialCategory,
                                         getFormat() );
        } else {
            cmd = makeSaveCommand( assetName,
                                   packageName,
                                   packageSelector.getSelectedPackageUUID(),
                                   description.getText(),
                                   initialCategory,
                                   getFormat() );
        }
        cmd.execute();

    }

    //Construct a chain of commands to handle saving the new asset, depending on whether the asset is constructed with a Wizard or not
    private Command makeWizardSaveCommand(final String assetName,
                                          final String packageName,
                                          final String packageUUID,
                                          final String description,
                                          final String initialCategory,
                                          final String format) {

        final Command cmdInvokeWizard = new Command() {

            public void execute() {
                NewAssetWizardContext config = new NewAssetWizardContext( assetName,
                                                                        packageName,
                                                                        packageUUID,
                                                                        description,
                                                                        initialCategory,
                                                                        format );
                clientFactory.getPlaceController().goTo( new WizardPlace<NewAssetWizardContext>( config ) );
            }
        };
        final Command cmdCheckBeforeInvokingWizard = new Command() {

            public void execute() {
                LoadingPopup.showMessage( constants.PleaseWaitDotDotDot() );
                RepositoryServiceFactory.getService().doesAssetExistInPackage( assetName,
                                                                               packageName,
                                                                               createGenericCallBackForCheckingIfExists( cmdInvokeWizard ) );
            }

        };
        return cmdCheckBeforeInvokingWizard;
    }

    private Command makeSaveCommand(final String assetName,
                                    final String packageName,
                                    final String packageUUID,
                                    final String description,
                                    final String initialCategory,
                                    final String format) {

        final Command cmdCheckBeforeSaving = new Command() {

            public void execute() {
                LoadingPopup.showMessage( constants.PleaseWaitDotDotDot() );
                RepositoryServiceFactory.getService().doesAssetExistInPackage( assetName,
                                                                                   packageName,
                                                                                   createGenericCallBackForCheckingIfExists( makeSaveCommand( assetName,
                                                                                                                                              packageName,
                                                                                                                                              description,
                                                                                                                                              initialCategory,
                                                                                                                                              format ) ) );
            }

        };
        return cmdCheckBeforeSaving;
    }

    private Command makeSaveCommand(final String assetName,
                                    final String packageName,
                                    final String description,
                                    final String initialCategory,
                                    final String format) {
        final Command cmdSave = new Command() {

            public void execute() {
                RepositoryServiceFactory.getService().createNewRule( assetName,
                                                                     description,
                                                                     initialCategory,
                                                                     packageName,
                                                                     format,
                                                                     createGenericCallbackForOk() );
            }
        };
        return cmdSave;
    }

    private boolean usingWizard() {
        return chkUseWizard == null ? false : chkUseWizard.getValue();
    }

    /**
     * When Import OK is pressed, it will update the repository with the
     * imported asset.
     */
    void importOK() {
        LoadingPopup.showMessage( constants.PleaseWaitDotDotDot() );
        RepositoryServiceFactory.getService().createNewImportedRule( globalAreaAssetSelector.getSelectedAsset(),
                                                                     importedPackageSelector.getSelectedPackage(),
                                                                     createGenericCallbackForImportOk() );
    }

    private GenericCallback<String> createGenericCallbackForOk() {
        GenericCallback<String> cb = new GenericCallback<String>() {
            public void onSuccess(String uuid) {
                LoadingPopup.close();
                if ( uuid.startsWith( "DUPLICATE" ) ) { // NON-NLS
                    Window.alert( constants.AssetNameAlreadyExistsPickAnother() );
                } else {
                    eventBus.fireEvent( new RefreshModuleEditorEvent( packageSelector.getSelectedPackageUUID() ) );
                    openEditor( uuid );
                    hide();
                }
            }
        };
        return cb;
    }

    private GenericCallback<String> createGenericCallbackForImportOk() {
        GenericCallback<String> cb = new GenericCallback<String>() {
            public void onSuccess(String uuid) {
                if ( uuid.startsWith( "DUPLICATE" ) ) { // NON-NLS
                    LoadingPopup.close();
                    Window.alert( constants.AssetNameAlreadyExistsPickAnother() );
                } else {
                    eventBus.fireEvent( new RefreshModuleEditorEvent( importedPackageSelector.getSelectedPackageUUID() ) );
                    flushSuggestionCompletionCache();
                    openEditor( uuid );
                    hide();
                }
            }
        };
        return cb;
    }

    private GenericCallback<Boolean> createGenericCallBackForCheckingIfExists(final Command cmd) {
        GenericCallback<Boolean> cb = new GenericCallback<Boolean>() {
            public void onSuccess(Boolean result) {
                LoadingPopup.close();
                if ( result == true ) {
                    Window.alert( constants.AssetNameAlreadyExistsPickAnother() );
                } else {
                    hide();
                    cmd.execute();
                }
            }
        };
        return cb;
    }

    /**
     * In some cases we will want to flush the package dependency stuff for
     * suggestion completions. The user will still need to reload the asset
     * editor though.
     */
    public void flushSuggestionCompletionCache() {
        if ( AssetFormats.isPackageDependency( format ) ) {
            LoadingPopup.showMessage( constants.RefreshingContentAssistance() );
            SuggestionCompletionCache.getInstance().refreshPackage( importedPackageSelector.getSelectedPackage(),
                                                                    new Command() {
                                                                        public void execute() {
                                                                            //Some assets depend on the SuggestionCompletionEngine. This event is to notify them that the 
                                                                            //SuggestionCompletionEngine has been changed, they need to refresh their UI to represent the changes.
                                                                            eventBus.fireEvent( new RefreshSuggestionCompletionEngineEvent( importedPackageSelector.getSelectedPackage() ) );
                                                                            LoadingPopup.close();
                                                                        }
                                                                    } );
        }
    }

    private String getFormat() {
        if ( format != null ) {
            return format;
        }
        return formatChooser.getValue( formatChooser.getSelectedIndex() );
    }

    /**
     * After creating the item we open it in the editor.
     * 
     * @param uuid
     */
    protected void openEditor(String uuid) {
        clientFactory.getPlaceController().goTo( new AssetEditorPlace( uuid ) );
    }

}
