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

package org.drools.guvnor.client.moduleeditor.drools;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;

import org.drools.guvnor.client.common.*;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.explorer.ExplorerNodeConfig;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.moduleeditor.AbstractModuleEditor;
import org.drools.guvnor.client.moduleeditor.DependencyWidget;
import org.drools.guvnor.client.resources.Images;
import org.drools.guvnor.client.rpc.PackageConfigData;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;
import org.drools.guvnor.client.rpc.TableDataResult;
import org.drools.guvnor.client.rpc.ValidatedResponse;
import org.drools.guvnor.client.widgets.categorynav.CategoryExplorerWidget;
import org.drools.guvnor.client.widgets.categorynav.CategorySelectHandler;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

/**
 * This is the module editor for Drools Package.
 */
public class PackageEditor extends AbstractModuleEditor {
    private Constants constants = GWT.create( Constants.class );
    private static Images images = GWT.create( Images.class );

    private final PackageConfigData packageConfigData;
    private boolean isHistoricalReadOnly = false;
    private Command refreshCommand;

    private HorizontalPanel packageConfigurationValidationResult = new HorizontalPanel();
    private final ClientFactory clientFactory;
    private final EventBus eventBus;

    public PackageEditor(PackageConfigData data,
                         ClientFactory clientFactory,
                         EventBus eventBus,
                         Command refreshCommand) {
        this( data,
                clientFactory,
                eventBus,
                false,
                refreshCommand );
    }

    public PackageEditor(PackageConfigData data,
                         ClientFactory clientFactory,
                         EventBus eventBus,
                         boolean historicalReadOnly,
                         Command refreshCommand) {
        this.packageConfigData = data;
        this.clientFactory = clientFactory;
        this.eventBus = eventBus;
        this.isHistoricalReadOnly = historicalReadOnly;
        this.refreshCommand = refreshCommand;

        setWidth( "100%" );
        refreshWidgets();
    }

    private void refreshWidgets() {
        clear();

        startSection( constants.ConfigurationSection() );

        packageConfigurationValidationResult.clear();
        addRow( packageConfigurationValidationResult );

        addAttribute( constants.Configuration(),
                header() );

        if ( !isHistoricalReadOnly ) {
            addAttribute( constants.CategoryRules(),
                    getAddCatRules() );
        }
        addAttribute( "",
                getShowCatRules() );

        if ( !packageConfigData.isSnapshot() && !isHistoricalReadOnly ) {
            Button save = new Button( constants.ValidateConfiguration() );
            save.addClickHandler( new ClickHandler() {

                public void onClick(ClickEvent event) {
                    doValidatePackageConfiguration( null );
                }
            } );
            addAttribute( "",
                    save );
        }

        endSection();

        if ( isHistoricalReadOnly ) {
            startSection( constants.Dependencies() );
            addRow( new DependencyWidget(
                    clientFactory,
                    eventBus,
                    this.packageConfigData,
                    isHistoricalReadOnly ) );
            endSection();
        }

        if ( !packageConfigData.isSnapshot() && !isHistoricalReadOnly ) {
            startSection( constants.BuildAndValidate() );
            addRow( new PackageBuilderWidget(
                    this.packageConfigData,
                    clientFactory ) );
            endSection();
        }

        startSection( constants.InformationAndImportantURLs() );

        Button buildSource = new Button( constants.ShowPackageSource() );
        buildSource.addClickHandler( new ClickHandler() {

            public void onClick(ClickEvent event) {
                PackageBuilderWidget.doBuildSource( packageConfigData.getUuid(),
                        packageConfigData.getName() );
            }
        } );

        HTML html0 = new HTML( "<a href='" + getDocumentationDownload( this.packageConfigData ) + "' target='_blank'>" + getDocumentationDownload( this.packageConfigData ) + "</a>" );
        addAttribute( constants.URLForDocumention(),
                createHPanel( html0,
                        constants.URLDocumentionDescription() ) );

        HTML html = new HTML( "<a href='" + getPackageSourceURL( this.packageConfigData ) + "' target='_blank'>" + getPackageSourceURL( this.packageConfigData ) + "</a>" );
        addAttribute( constants.URLForPackageSource(),
                createHPanel( html,
                        constants.URLSourceDescription() ) );

        HTML html2 = new HTML( "<a href='" + getPackageBinaryURL( this.packageConfigData ) + "' target='_blank'>" + getPackageBinaryURL( this.packageConfigData ) + "</a>" );
        addAttribute( constants.URLForPackageBinary(),
                createHPanel( html2,
                        constants.UseThisUrlInTheRuntimeAgentToFetchAPreCompiledBinary() ) );

        HTML html3 = new HTML( "<a href='" + getScenarios( this.packageConfigData ) + "' target='_blank'>" + getScenarios( this.packageConfigData ) + "</a>" );
        addAttribute( constants.URLForRunningTests(),
                createHPanel( html3,
                        constants.URLRunTestsRemote() ) );

        HTML html4 = new HTML( "<a href='" + getChangeset( this.packageConfigData ) + "' target='_blank'>" + getChangeset( this.packageConfigData ) + "</a>" );

        addAttribute( constants.ChangeSet(),
                createHPanel( html4,
                        constants.URLToChangeSetForDeploymentAgents() ) );

        HTML html5 = new HTML( "<a href='" + getModelDownload( this.packageConfigData ) + "' target='_blank'>" + getModelDownload( this.packageConfigData ) + "</a>" );

        addAttribute( constants.ModelSet(),
                createHPanel( html5,
                        constants.URLToDownloadModelSet() ) );

        final Tree springContextTree = new Tree();
        final TreeItem rootItem = new TreeItem( "" );

        springContextTree.addItem( rootItem );

        final int rowNumber = addAttribute( constants.SpringContext() + ":",
                springContextTree );

        GenericCallback<TableDataResult> callBack = new GenericCallback<TableDataResult>() {

            public void onSuccess(TableDataResult resultTable) {

                if ( resultTable.data.length == 0 ) {
                    removeRow( rowNumber );
                }

                for (int i = 0; i < resultTable.data.length; i++) {

                    String url = getSpringContextDownload( packageConfigData,
                            resultTable.data[i].getDisplayName() );
                    HTML html = new HTML( "<a href='" + url + "' target='_blank'>" + url + "</a>" );
                    rootItem.addItem( html );
                }
            }
        };

        RepositoryServiceFactory.getAssetService().listAssetsWithPackageName( this.packageConfigData.getName(),
                new String[]{AssetFormats.SPRING_CONTEXT},
                0,
                -1,
                ExplorerNodeConfig.RULE_LIST_TABLE_ID,
                callBack );

        endSection();
    }

    //TODO: move this to PackageEditorActionToolbar
    private void doValidatePackageConfiguration(final Command refresh) {
        final HorizontalPanel busy = new HorizontalPanel();
        busy.add( new Label( constants.ValidatingAndBuildingPackagePleaseWait() ) );
        busy.add( new Image( images.redAnime() ) );

        packageConfigurationValidationResult.add( busy );

        RepositoryServiceFactory.getPackageService().validatePackageConfiguration( this.packageConfigData,
                new GenericCallback<ValidatedResponse>() {
                    public void onSuccess(ValidatedResponse data) {
                        showValidatePackageConfigurationResult( data );
                    }
                } );
    }
    
    private Widget createHPanel(Widget widget,
                                String popUpText) {
        HorizontalPanel hPanel = new HorizontalPanel();
        hPanel.add( widget );
        hPanel.add( new InfoPopup( constants.Tip(),
                popUpText ) );
        return hPanel;
    }

    private Widget getShowCatRules() {
        if ( packageConfigData.getCatRules() != null && packageConfigData.getCatRules().size() > 0 ) {
            VerticalPanel vp = new VerticalPanel();

            for (Iterator<Entry<String, String>> iterator = packageConfigData.getCatRules().entrySet().iterator(); iterator.hasNext(); ) {
                Entry<String, String> entry = iterator.next();
                HorizontalPanel hp = new HorizontalPanel();
                String m = constants.AllRulesForCategory0WillNowExtendTheRule1(
                        (String) entry.getValue(),
                        (String) entry.getKey() );
                hp.add( new SmallLabel( m ) );
                hp.add( getRemoveCatRulesIcon( (String) entry.getKey() ) );
                vp.add( hp );
            }
            return (vp);
        }
        return new HTML( "&nbsp;&nbsp;" );
    }

    private Image getRemoveCatRulesIcon(final String rule) {
        Image remove = new Image( images.deleteItemSmall() );
        remove.addClickHandler( new ClickHandler() {

            public void onClick(ClickEvent event) {
                if ( Window.confirm( constants.RemoveThisCategoryRule() ) ) {
                    packageConfigData.getCatRules().remove( rule );
                    refreshWidgets();
                }
            }
        } );
        return remove;
    }

    private Widget getAddCatRules() {
        Image add = new ImageButton( images.edit() );
        add.setTitle( constants.AddCatRuleToThePackage() );

        add.addClickHandler( new ClickHandler() {

            public void onClick(ClickEvent event) {
                showCatRuleSelector( (Widget) event.getSource() );
            }
        } );

        HorizontalPanel hp = new HorizontalPanel();
        hp.add( add );
        hp.add( new InfoPopup( constants.CategoryParentRules(),
                constants.CatRulesInfo() ) );
        return hp;
    }

    private void addToCatRules(String category,
                               String rule) {
        if ( null != category && null != rule ) {
            if ( packageConfigData.getCatRules() == null ) {
                packageConfigData.setCatRules( new HashMap<String, String>() );
            }
            packageConfigData.getCatRules().put( rule,
                    category );
        }
    }

    protected void showCatRuleSelector(Widget w) {
        final FormStylePopup pop = new FormStylePopup( images.config(),
                constants.AddACategoryRuleToThePackage() );
        final Button addbutton = new Button( constants.OK() );
        final TextBox ruleName = new TextBox();

        final CategoryExplorerWidget exw = new CategoryExplorerWidget( new CategorySelectHandler() {
            public void selected(String selectedPath) { //not needed
            }
        } );

        ruleName.setVisibleLength( 15 );

        addbutton.setTitle( constants.CreateCategoryRule() );

        addbutton.addClickHandler( new ClickHandler() {

            public void onClick(ClickEvent event) {
                if ( exw.getSelectedPath().length() > 0 && ruleName.getText().trim().length() > 0 ) {
                    addToCatRules( exw.getSelectedPath(),
                            ruleName.getText() );
                }
                refreshWidgets();
                pop.hide();
            }
        } );

        pop.addAttribute( constants.AllTheRulesInFollowingCategory(),
                exw );
        pop.addAttribute( constants.WillExtendTheFollowingRuleCalled(),
                ruleName );
        pop.addAttribute( "",
                addbutton );

        pop.show();
    }

    private void showValidatePackageConfigurationResult(final ValidatedResponse validatedResponse) {
        packageConfigurationValidationResult.clear();

        if ( validatedResponse != null && validatedResponse.hasErrors && !validatedResponse.errorMessage.startsWith( "Class" ) ) {
            Image img = new Image( images.warning() );
            packageConfigurationValidationResult.add( img );
            HTML msg = new HTML( "<b>" + constants.ThereWereErrorsValidatingThisPackageConfiguration() + "</b>" ); //NON-NLS
            packageConfigurationValidationResult.add( msg );
            Button show = new Button( constants.ViewErrors() );
            show.addClickHandler( new ClickHandler() {
                public void onClick(ClickEvent event) {
                    ValidationMessageWidget wid = new ValidationMessageWidget( validatedResponse.errorHeader,
                            validatedResponse.errorMessage );
                    wid.show();
                }
            } );
            packageConfigurationValidationResult.add( show );
        } else {
            Image img = new Image( images.greenTick() );
            packageConfigurationValidationResult.add( img );
            HTML msg = new HTML( "<b>" + constants.PackageValidatedSuccessfully() + "</b>" ); //NON-NLS
            packageConfigurationValidationResult.add( msg );
        }
    }

    static String getDocumentationDownload(PackageConfigData conf) {
        return makeLink( conf ) + "/documentation.pdf"; //NON-NLS
    }

    static String getSourceDownload(PackageConfigData conf) {
        return makeLink( conf ) + ".drl"; //NON-NLS
    }

    static String getBinaryDownload(PackageConfigData conf) {
        return makeLink( conf );
    }

    static String getScenarios(PackageConfigData conf) {
        return makeLink( conf ) + "/SCENARIOS"; //NON-NLS
    }

    static String getChangeset(PackageConfigData conf) {
        return makeLink( conf ) + "/ChangeSet.xml"; //NON-NLS
    }

    public static String getModelDownload(PackageConfigData conf) {
        return makeLink( conf ) + "/MODEL"; //NON-NLS
    }

    static String getSpringContextDownload(PackageConfigData conf,
                                           String name) {
        return makeLink( conf ) + "/SpringContext/" + name;
    }

    static String getVersionFeed(PackageConfigData conf) {
        String hurl = getRESTBaseURL() + "packages/" + conf.getName() + "/versions";
        return hurl;
    }

    String getPackageSourceURL(PackageConfigData conf) {
        String url;
        if ( isHistoricalReadOnly ) {
            url = getRESTBaseURL() + "packages/" + conf.getName() +
                    "/versions/" + conf.getVersionNumber() + "/source";
        } else {
            url = getRESTBaseURL() + "packages/" + conf.getName() + "/source";
        }
        return url;
    }

    String getPackageBinaryURL(PackageConfigData conf) {
        String url;
        if ( isHistoricalReadOnly ) {
            url = getRESTBaseURL() + "packages/" + conf.getName() +
                    "/versions/" + conf.getVersionNumber() + "/binary";
        } else {
            url = getRESTBaseURL() + "packages/" + conf.getName() + "/binary";
        }
        return url;
    }

    static String getRESTBaseURL() {
        String url = GWT.getModuleBaseURL();
        return url.replaceFirst( "org.drools.guvnor.Guvnor",
                "rest" );
    }

    /**
     * Get a download link for the binary package.
     */
    public static String makeLink(PackageConfigData conf) {
        String hurl = GWT.getModuleBaseURL() + "package/" + conf.getName();
        if ( !conf.isSnapshot() ) {
            hurl = hurl + "/" + SnapshotView.LATEST_SNAPSHOT;
        } else {
            hurl = hurl + "/" + conf.getSnapshotName();
        }
        final String uri = hurl;
        return uri;
    }

    private Widget header() {
        return new PackageHeaderWidget( this.packageConfigData,
        		isHistoricalReadOnly );
    }

    /*
        private void setState(String state) {
            status.setHTML( "<b>" + state + "</b>" );
        }
    */
}
