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
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.resources.Images;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;
import org.drools.guvnor.client.rulelist.OpenItemCommand;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
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
 * This provides a popup for creating a new rule/asset from scratch.
 * reuses a few other widgets.
 */
public class NewAssetWizard extends FormStylePopup {

    private Constants                        constants               = GWT.create( Constants.class );
    private static Images                    images                  = GWT.create( Images.class );

    private TextBox                          name                    = new TextBox();
    private TextArea                         description             = new TextArea();
    private String                           initialCategory;

    private ListBox                          formatChooser           = getFormatChooser();
    RadioButton                              createInPackageButton   = new RadioButton( "creatinpackagegroup", constants.CreateInPackage() );
    RadioButton                              createInGlobalButton    = new RadioButton( "creatinpackagegroup", constants.CreateInGlobalArea() );

    private RulePackageSelector              packageSelector         = new RulePackageSelector();
    private RulePackageSelector              importedPackageSelector = new RulePackageSelector();
    private GlobalAreaAssetSelector          globalAreaAssetSelector;
    private OpenItemCommand                  afterCreate;
    private boolean                          showCategories;
    private String                           format;

    private final NewAssetFormStyleLayout    newAssetLayout          = new NewAssetFormStyleLayout();
    private final ImportAssetFormStyleLayout importAssetLayout       = new ImportAssetFormStyleLayout();

    /** This is used when creating a new rule. */
    public NewAssetWizard(OpenItemCommand afterCreate, boolean showCategories, String format, String title) {
        super( images.newWiz(), title );
        this.showCategories = showCategories;
        this.format = format;

        this.afterCreate = afterCreate;

        RadioButton newPackage = new RadioButton( "layoutgroup", constants.CreateNewAsset() ); // NON-NLS
        newPackage.setValue( true );
        RadioButton importPackage = new RadioButton( "layoutgroup", constants.CreateLinkedAsset() ); // NON-NLS

        newAssetLayout.setVisible( true );

        createClickHandlerForNewPackageButton( newPackage );
        importAssetLayout.setVisible( false );
        createClickHandlerForImportPackageButton( importPackage );
        addAttribute( "", createVerticalPanelFor( newPackage, importPackage ) );
        addRow( newAssetLayout );
        addRow( importAssetLayout );

        newAssetLayout.addAttribute( constants.NameColon(), name );

        setAfterShowCommand();

        if ( showCategories ) {
            newAssetLayout.addAttribute( constants.InitialCategory(), getCatChooser() );
        }

        handleLayoutForFormat( format );

        createInPackageButton.setValue( true );
        newAssetLayout.buildNewAssetFormStyleLayout();

        globalAreaAssetSelector = new GlobalAreaAssetSelector( format );
        importAssetLayout.buildImportAssetLayout();
    }

    private class ImportAssetFormStyleLayout extends FormStyleLayout {
        protected void buildImportAssetLayout() {
            this.addAttribute( constants.AssetToImport(), globalAreaAssetSelector );
            this.addAttribute( constants.Package() + ":", importedPackageSelector );
            this.addAttribute( "", createLinkedAssetOkButtonAndClickHandler() );
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
            this.addAttribute( "", createHorizontalePanelFor() );
            this.addAttribute( "", createInGlobalButton );
            buildDescriptionTextArea( format );
            this.addAttribute( constants.InitialDescription(), description );
            this.addAttribute( "", createOkButtonAndClickHandler() );
        }

        private void buildDescriptionTextArea(String format) {
            description.setVisibleLines( 4 );
            description.setWidth( "100%" );
            if ( AssetFormats.DSL_TEMPLATE_RULE.equals(format) ) {
                description.setText( constants.DSLMappingTip() );
            } else if ( AssetFormats.ENUMERATION.equals(format) ) {
                description.setText( constants.NewEnumDoco() );
            } else if ( format == AssetFormats.SPRING_CONTEXT ) {
                description.setText( constants.DescSpringContext());
            } else if ( format == AssetFormats.WORKITEM_DEFINITION ) {
                description.setText( constants.DeskWorkItemDefinition());
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
            newAssetLayout.addAttribute( constants.TypeFormatOfRule(), this.formatChooser );
        } else if ( "*".equals( format ) ) { //NON-NLS
            final TextBox fmt = new TextBox();
            newAssetLayout.addAttribute( constants.FileExtensionTypeFormat(), fmt );
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

    private VerticalPanel createVerticalPanelFor(RadioButton newPackage, RadioButton importPackage) {
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
        scroll.setSize( "300px", "130px" ); //NON-NLS
        return scroll;

    }

    private ListBox getFormatChooser() {

        ListBox box = new ListBox();

        box.addItem( constants.BusinessRuleGuidedEditor(), AssetFormats.BUSINESS_RULE );
        box.addItem( constants.DSLBusinessRuleTextEditor(), AssetFormats.DSL_TEMPLATE_RULE );
        box.addItem( constants.DRLRuleTechnicalRuleTextEditor(), AssetFormats.DRL );
        box.addItem( constants.DecisionTableSpreadsheet(), AssetFormats.DECISION_SPREADSHEET_XLS );
        box.addItem( constants.DecisionTableWebGuidedEditor(), AssetFormats.DECISION_TABLE_GUIDED );

        box.setSelectedIndex( 0 );

        return box;
    }

    /**
     * When OK is pressed, it will update the repository with the new rule.
     */
    void ok() {

        if ( this.showCategories && this.initialCategory == null ) {
            Window.alert( constants.YouHaveToPickAnInitialCategory() );
            return;
        }

        if ( !validatePathPerJSR170( this.name.getText() ) ) {
            return;
        }

        if ( "*".equals( getFormat() ) ) {
            Window.alert( constants.PleaseEnterAFormatFileType() );
            return;
        }

        String selectedPackage;
        if ( createInGlobalButton.getValue() ) {
            selectedPackage = "globalArea";
        } else {
            selectedPackage = packageSelector.getSelectedPackage();
        }

        LoadingPopup.showMessage( constants.PleaseWaitDotDotDot() );
        RepositoryServiceFactory.getService().createNewRule( name.getText(), description.getText(), initialCategory, selectedPackage, getFormat(), createGenericCallbackForOk() );
    }

    /**
     * When Import OK is pressed, it will update the repository with the imported asset.
     */
    void importOK() {
        LoadingPopup.showMessage( constants.PleaseWaitDotDotDot() );
        RepositoryServiceFactory.getService().createNewImportedRule( globalAreaAssetSelector.getSelectedAsset(), importedPackageSelector.getSelectedPackage(), createGenericCallbackForOk() );
    }

    private GenericCallback<String> createGenericCallbackForOk() {
        GenericCallback<String> cb = new GenericCallback<String>() {
            public void onSuccess(String uuid) {
                if ( uuid.startsWith( "DUPLICATE" ) ) { // NON-NLS
                    LoadingPopup.close();
                    Window.alert( constants.AssetNameAlreadyExistsPickAnother() );
                } else {
                    openEditor( uuid );
                    hide();
                }
            }
        };
        return cb;
    }

    private String getFormat() {
        if ( format != null ) {
            return format;
        }
        return formatChooser.getValue( formatChooser.getSelectedIndex() );
    }

    /**
     * After creating the item we open it in the editor.
     * @param uuid
     */
    protected void openEditor(String uuid) {
        afterCreate.open( uuid );
    }

    /**
     * Validate name per JSR-170. Only following characters are valid: char ::=
     * nonspace | ' ' nonspace ::= (* Any Unicode character except: '/', ':',
     * '[', ']', '*', ''', '"', '|' or any whitespace character *)
     *
     * @param jsrPath
     */
    public static boolean validatePathPerJSR170(String jsrPath) {
        int len = jsrPath == null ? 0 : jsrPath.trim().length();
        if ( len == 0 ) {
            Window.alert( GWT.<Constants> create( Constants.class ).emptyNameIsNotAllowed() );
            return false;
        }

        int pos = 0;

        while ( pos < len ) {
            char c = jsrPath.charAt( pos );
            pos++;

            switch ( c ) {
                case '/' :
                case ':' :
                case '[' :
                case ']' :
                case '*' :
                case '\'' :
                case '\"' :
                    Window.alert( GWT.<Constants> create( Constants.class ).NonValidJCRName(jsrPath, c) );
                    return false;
                default :
            }
        }

        return true;
    }

}
