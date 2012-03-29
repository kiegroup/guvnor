package org.drools.guvnor.client.editor;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.drools.guvnor.client.common.DatePickerTextBox;
import org.drools.guvnor.client.common.DecoratedDisclosurePanel;
import org.drools.guvnor.client.common.FormStyleLayout;
import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.common.InfoPopup;
import org.drools.guvnor.client.common.PrettyFormLayout;
import org.drools.guvnor.client.common.SmallLabel;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.resources.Images;
import org.drools.guvnor.shared.MetaDataQuery;
import org.drools.guvnor.shared.Module;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.gwtplatform.mvp.client.ViewImpl;


public class PackageView extends ViewImpl implements PackagePresenter.MyView {
    private static Images          images    = (Images) GWT.create( Images.class );
    private Constants              constants = ((Constants) GWT.create( Constants.class ));
  
    private Module packageConfigData = new Module();
    private boolean isHistoricalReadOnly = false;
    
    private PrettyFormLayout layout = new PrettyFormLayout();
    private HorizontalPanel packageConfigurationValidationResult = new HorizontalPanel();
    
    public PackageView() {
        render();
    }

    @Override
    public Widget asWidget() {
        return layout;
    }

    private void render() {
        layout.clear();

        layout.startSection( constants.ConfigurationSection() );

        packageConfigurationValidationResult.clear();
        layout.addRow( packageConfigurationValidationResult );

/*        layout.addAttribute( constants.Configuration(),
                header() );*/

        if ( !isHistoricalReadOnly ) {
/*            addAttribute( constants.CategoryRules(),
                    getAddCatRules() );*/
        }
/*        layout.addAttribute( "",
                getShowCatRules() );*/

        if ( !packageConfigData.isSnapshot() && !isHistoricalReadOnly ) {
            Button save = new Button(constants.ValidateConfiguration() );
            save.addClickHandler( new ClickHandler() {

                public void onClick(ClickEvent event) {
                    //doValidatePackageConfiguration( null );
                }
            } );
            layout.addAttribute( "",
                    save );
        }

        layout.endSection();

        if ( isHistoricalReadOnly ) {
/*            layout.startSection( constants.Dependencies() );
            layout.addRow( new DependencyWidget(
                    clientFactory,
                    eventBus,
                    this.packageConfigData,
                    isHistoricalReadOnly ) );
            layout.endSection();*/
        }

        if ( !packageConfigData.isSnapshot() && !isHistoricalReadOnly ) {
/*            layout.startSection( constants.BuildAndValidate() );
            layout.addRow( new PackageBuilderWidget(
                    this.packageConfigData,
                    clientFactory ) );
            layout.endSection();*/
        }

        layout.startSection( constants.InformationAndImportantURLs() );

        Button buildSource = new Button( constants.ShowPackageSource() );
        buildSource.addClickHandler( new ClickHandler() {

            public void onClick(ClickEvent event) {
/*                PackageBuilderWidget.doBuildSource( packageConfigData.getUuid(),
                        packageConfigData.getName() );*/
            }
        } );

        HTML html0 = new HTML( "<a href='" + getDocumentationDownload( this.packageConfigData ) + "' target='_blank'>" + getDocumentationDownload( this.packageConfigData ) + "</a>" );
        layout.addAttribute( constants.URLForDocumention(),
                createHPanel( html0,
                        constants.URLDocumentionDescription() ) );

        HTML html = new HTML( "<a href='" + getPackageSourceURL( this.packageConfigData ) + "' target='_blank'>" + getPackageSourceURL( this.packageConfigData ) + "</a>" );
        layout.addAttribute( constants.URLForPackageSource(),
                createHPanel( html,
                        constants.URLSourceDescription() ) );

        HTML html2 = new HTML( "<a href='" + getPackageBinaryURL( this.packageConfigData ) + "' target='_blank'>" + getPackageBinaryURL( this.packageConfigData ) + "</a>" );
        layout.addAttribute( constants.URLForPackageBinary(),
                createHPanel( html2,
                        constants.UseThisUrlInTheRuntimeAgentToFetchAPreCompiledBinary() ) );

        HTML html3 = new HTML( "<a href='" + getScenarios( this.packageConfigData ) + "' target='_blank'>" + getScenarios( this.packageConfigData ) + "</a>" );
        layout.addAttribute( constants.URLForRunningTests(),
                createHPanel( html3,
                        constants.URLRunTestsRemote() ) );

        HTML html4 = new HTML( "<a href='" + getChangeset( this.packageConfigData ) + "' target='_blank'>" + getChangeset( this.packageConfigData ) + "</a>" );

        layout.addAttribute( constants.ChangeSet(),
                createHPanel( html4,
                        constants.URLToChangeSetForDeploymentAgents() ) );

        HTML html5 = new HTML( "<a href='" + getModelDownload( this.packageConfigData ) + "' target='_blank'>" + getModelDownload( this.packageConfigData ) + "</a>" );

        layout.addAttribute( constants.ModelSet(),
                createHPanel( html5,
                        constants.URLToDownloadModelSet() ) );
/*
        final Tree springContextTree = new Tree();
        final TreeItem rootItem = new TreeItem( "" );

        springContextTree.addItem( rootItem );

        final int rowNumber = layout.addAttribute( constants.SpringContext() + ":",
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
                callBack );*/

        layout.endSection();

    }
    
    private Widget createHPanel(Widget widget,
                                String popUpText) {
        HorizontalPanel hPanel = new HorizontalPanel();
        hPanel.add( widget );
        hPanel.add( new InfoPopup( constants.Tip(),
                popUpText ) );
        return hPanel;
    }

    static String getDocumentationDownload(Module conf) {
        return makeLink( conf ) + "/documentation.pdf"; //NON-NLS
    }

    static String getSourceDownload(Module conf) {
        return makeLink( conf ) + ".drl"; //NON-NLS
    }

    static String getBinaryDownload(Module conf) {
        return makeLink( conf );
    }

    static String getScenarios(Module conf) {
        return makeLink( conf ) + "/SCENARIOS"; //NON-NLS
    }

    static String getChangeset(Module conf) {
        return makeLink( conf ) + "/ChangeSet.xml"; //NON-NLS
    }

    public static String getModelDownload(Module conf) {
        return makeLink( conf ) + "/MODEL"; //NON-NLS
    }

    static String getSpringContextDownload(Module conf,
                                           String name) {
        return makeLink( conf ) + "/SpringContext/" + name;
    }

    static String getVersionFeed(Module conf) {
        String hurl = RESTUtil.getRESTBaseURL() + "packages/" + conf.getName() + "/versions";
        return hurl;
    }

    String getPackageSourceURL(Module conf) {
        String url;
        if ( isHistoricalReadOnly ) {
            url = RESTUtil.getRESTBaseURL() + "packages/" + conf.getName() +
                    "/versions/" + conf.getVersionNumber() + "/source";
        } else {
            url = RESTUtil.getRESTBaseURL() + "packages/" + conf.getName() + "/source";
        }
        return url;
    }

    String getPackageBinaryURL(Module conf) {
        String url;
        if ( isHistoricalReadOnly ) {
            url = RESTUtil.getRESTBaseURL() + "packages/" + conf.getName() +
                    "/versions/" + conf.getVersionNumber() + "/binary";
        } else {
            url = RESTUtil.getRESTBaseURL() + "packages/" + conf.getName() + "/binary";
        }
        return url;
    }

    /**
     * Get a download link for the binary package.
     */
    public static String makeLink(Module conf) {
        String hurl = GWT.getModuleBaseURL() + "package/" + conf.getName();
        if ( !conf.isSnapshot() ) {
            hurl = hurl + "/" + "LATEST";
        } else {
            hurl = hurl + "/" + conf.getSnapshotName();
        }
        final String uri = hurl;
        return uri;
    }
    
    public void setModule(Module module) {
        this.packageConfigData = module;
        render();
    }
 
}