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

package org.drools.guvnor.client.packages;


import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.drools.guvnor.client.categorynav.CategoryExplorerWidget;
import org.drools.guvnor.client.categorynav.CategorySelectHandler;
import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.common.FormStylePopup;
import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.common.ImageButton;
import org.drools.guvnor.client.common.InfoPopup;
import org.drools.guvnor.client.common.LoadingPopup;
import org.drools.guvnor.client.common.PrettyFormLayout;
import org.drools.guvnor.client.common.SmallLabel;
import org.drools.guvnor.client.common.StatusChangePopup;
import org.drools.guvnor.client.common.ValidationMessageWidget;
import org.drools.guvnor.client.explorer.ExplorerNodeConfig;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.resources.Images;
import org.drools.guvnor.client.rpc.PackageConfigData;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;
import org.drools.guvnor.client.rpc.TableDataResult;
import org.drools.guvnor.client.rpc.ValidatedResponse;
import org.drools.guvnor.client.util.Format;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * This is the package editor and viewer for package configuration.
 */
public class PackageEditor extends PrettyFormLayout {

    private Constants           constants = GWT.create( Constants.class );
    private static Images       images    = GWT.create( Images.class );

    private PackageConfigData   conf;
    private HTML                status;
    protected ValidatedResponse previousResponse;
    private Command             close;
    private Command             refreshPackageList;

    public PackageEditor(PackageConfigData data,
                         Command close,
                         Command refreshPackageList) {
        this.conf = data;
        this.close = close;
        this.refreshPackageList = refreshPackageList;

        setWidth( "100%" );
        refreshWidgets();
    }

    private void refreshWidgets() {
        clear();

        FlexTable headerWidgets = new FlexTable();
        headerWidgets.setWidget( 0,
                                 0,
                                 new HTML( "<b>" + constants.PackageName() + ":</b>" ) ); //NON-NLS
        headerWidgets.setWidget( 0,
                                 1,
                                 new Label( this.conf.name ) );
        if ( !conf.isSnapshot ) {

            headerWidgets.setWidget( 1,
                                     0,
                                     modifyWidgets() );
            headerWidgets.getFlexCellFormatter().setColSpan( 1,
                                                             0,
                                                             2 );
        }

        addHeader( images.packageLarge(),
                   headerWidgets );

        startSection( constants.ConfigurationSection() );

        addRow( warnings() );
        addAttribute( constants.Configuration(),
                      header() );
        addAttribute( constants.DescriptionColon(),
                      description() );
        addAttribute( constants.CategoryRules(),
                      getAddCatRules() );
        addAttribute( "",
                      getShowCatRules() );

        if ( !conf.isSnapshot ) {
            Button save = new Button( constants.SaveAndValidateConfiguration() );
            save.addClickHandler( new ClickHandler() {

                public void onClick(ClickEvent event) {
                    doSaveAction( null );
                }
            } );
            addAttribute( "",
                          save );
        }

        endSection();

        startSection(constants.BuildAndValidate());
        addRow(new DependencyWidget(this.conf));
        endSection();
        
        if ( !conf.isSnapshot ) {
            startSection( constants.BuildAndValidate() );
            addRow( new PackageBuilderWidget( this.conf ) );
            endSection();
        }

        startSection( constants.InformationAndImportantURLs() );
        if ( !conf.isSnapshot ) {
            addAttribute( constants.LastModified() + ":",
                          new Label( getDateString( conf.lastModified ) ) );
        }

        addAttribute( constants.LastContributor() + ":",
                      new Label( this.conf.lasContributor ) );

        addAttribute( constants.DateCreated(),
                      new Label( getDateString( this.conf.dateCreated ) ) );
        Button buildSource = new Button( constants.ShowPackageSource() );
        buildSource.addClickHandler( new ClickHandler() {

            public void onClick(ClickEvent event) {
                PackageBuilderWidget.doBuildSource( conf.uuid,
                                                    conf.name );
            }
        } );

        addAttribute( constants.ShowPackageSource() + ":",
                      buildSource );

        HTML html0 = new HTML( "<a href='" + getDocumentationDownload( this.conf ) + "' target='_blank'>" + getDocumentationDownload( this.conf ) + "</a>" );
        addAttribute( constants.URLForDocumention(),
                      createHPanel( html0,
                         constants.URLDocumentionDescription() ) );

        HTML html = new HTML( "<a href='" + getSourceDownload( this.conf ) + "' target='_blank'>" + getSourceDownload( this.conf ) + "</a>" );
        addAttribute( constants.URLForPackageSource(),
                      createHPanel( html,
                         constants.URLSourceDescription() ) );

        HTML html2 = new HTML( "<a href='" + getBinaryDownload( this.conf ) + "' target='_blank'>" + getBinaryDownload( this.conf ) + "</a>" );
        addAttribute( constants.URLForPackageBinary(),
                      createHPanel( html2,
                         constants.UseThisUrlInTheRuntimeAgentToFetchAPreCompiledBinary() ) );

        HTML html3 = new HTML( "<a href='" + getScenarios( this.conf ) + "' target='_blank'>" + getScenarios( this.conf ) + "</a>" );
        addAttribute( constants.URLForRunningTests(),
                      createHPanel( html3,
                         constants.URLRunTestsRemote() ) );

        HTML html4 = new HTML( "<a href='" + getChangeset( this.conf ) + "' target='_blank'>" + getChangeset( this.conf ) + "</a>" );

        addAttribute( constants.ChangeSet(),
                      createHPanel( html4,
                         constants.URLToChangeSetForDeploymentAgents() ) );

        HTML html5 = new HTML( "<a href='" + getModelDownload( this.conf ) + "' target='_blank'>" + getModelDownload( this.conf ) + "</a>" );

        addAttribute( constants.ModelSet(),
                      createHPanel( html5,
                         constants.URLToDownloadModelSet()) );

        final Tree springContextTree = new Tree();
        final TreeItem rootItem = new TreeItem("");
        
        springContextTree.addItem(rootItem);
        
        final int rowNumber = addAttribute(constants.SpringContext() + ":", springContextTree);
        
        GenericCallback<TableDataResult> callBack = new GenericCallback<TableDataResult>() {

            public void onSuccess(TableDataResult resultTable) {

                if (resultTable.data.length == 0) {
                    removeRow(rowNumber);
                }

                for (int i = 0; i < resultTable.data.length; i++) {

                    String url = getSpringContextDownload(conf, resultTable.data[i].getDisplayName());
                    HTML html = new HTML( "<a href='" + url + "' target='_blank'>" + url + "</a>" );
                    rootItem.addItem(html);
                }
            }
        };
        
        RepositoryServiceFactory.getAssetService().listAssetsWithPackageName(this.conf.name, new String[]{AssetFormats.SPRING_CONTEXT}, 0,
                                                                        -1, ExplorerNodeConfig.RULE_LIST_TABLE_ID, callBack);
        
        status = new HTML();
        HorizontalPanel statusBar = new HorizontalPanel();
        Image editState = new ImageButton( images.edit() );
        editState.setTitle( constants.ChangeStatusDot() );
        editState.addClickHandler( new ClickHandler() {

            public void onClick(ClickEvent event) {
                showStatusChanger( (Widget) event.getSource() );
            }

        } );
        statusBar.add( status );

        if ( !this.conf.isSnapshot ) {
            statusBar.add( editState );
        }

        setState( conf.state );
        addAttribute( constants.Status() + ":",
                      statusBar );

        endSection();

    }

    private Widget createHPanel(Widget widget, String popUpText) {
        HorizontalPanel hPanel = new HorizontalPanel();
        hPanel.add( widget );
        hPanel.add( new InfoPopup( constants.Tip(), popUpText ) );
        return hPanel;
    }

    private Widget getShowCatRules() {

        if ( conf.catRules != null && conf.catRules.size() > 0 ) {
            VerticalPanel vp = new VerticalPanel();

            for ( Iterator<Entry<String, String>> iterator = conf.catRules.entrySet().iterator(); iterator.hasNext(); ) {
                Entry<String, String> entry = iterator.next();
                HorizontalPanel hp = new HorizontalPanel();
                String m = Format.format( constants.AllRulesForCategory0WillNowExtendTheRule1(),
                                          (String) entry.getValue(),
                                          (String) entry.getKey() );
                hp.add( new SmallLabel( m ) );
                hp.add( getRemoveCatRulesIcon( (String) entry.getKey() ) );
                vp.add( hp );
            }
            return (vp);
        }
        return new HTML( "&nbsp;&nbsp;" ); //NON-NLS

    }

    private Image getRemoveCatRulesIcon(final String rule) {
        Image remove = new Image( images.deleteItemSmall() );
        remove.addClickHandler( new ClickHandler() {

            public void onClick(ClickEvent event) {
                if ( Window.confirm( constants.RemoveThisCategoryRule() ) ) {
                    conf.catRules.remove( rule );
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
            if ( conf.catRules == null ) {
                conf.catRules = new HashMap<String, String>();
            }
            conf.catRules.put( rule,
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

    private String getDateString(Date d) {
        if ( d != null ) {
            return DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_TIME_SHORT).format(d);
        } else {
            return "";
        }
    }

    private Widget warnings() {
        if ( this.previousResponse != null && this.previousResponse.hasErrors ) {
            Image img = new Image( images.warning() );
            HorizontalPanel h = new HorizontalPanel();
            h.add( img );
            HTML msg = new HTML( "<b>" + constants.ThereWereErrorsValidatingThisPackageConfiguration() + "</b>" ); //NON-NLS
            h.add( msg );
            Button show = new Button( constants.ViewErrors() );
            show.addClickHandler( new ClickHandler() {

                public void onClick(ClickEvent event) {
                    ValidationMessageWidget wid = new ValidationMessageWidget( previousResponse.errorHeader,
                                                                               previousResponse.errorMessage );
                    wid.show();
                }
            } );
            h.add( show );
            return h;
        } else {
            return new SimplePanel();
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
    
    static String getSpringContextDownload(PackageConfigData conf, String name) {
        return makeLink( conf ) + "/SpringContext/" + name;
    }
    
    /**
     * Get a download link for the binary package.
     */
    public static String makeLink(PackageConfigData conf) {
        String hurl = GWT.getModuleBaseURL() + "package/" + conf.name;
        if ( !conf.isSnapshot ) {
            hurl = hurl + "/" + SnapshotView.LATEST_SNAPSHOT;
        } else {
            hurl = hurl + "/" + conf.snapshotName;
        }
        final String uri = hurl;
        return uri;
    }

    protected void showStatusChanger(Widget w) {
        final StatusChangePopup pop = new StatusChangePopup( conf.uuid,
                                                             true );
        pop.setChangeStatusEvent( new Command() {
            public void execute() {
                setState( pop.getState() );
            }
        } );

        pop.show();

    }

    private void setState(String state) {
        status.setHTML( "<b>" + state + "</b>" );
    }

    /**
     * This will get the save widgets.
     */
    private Widget modifyWidgets() {

        HorizontalPanel horiz = new HorizontalPanel();

        Button copy = new Button( constants.Copy() );
        copy.addClickHandler( new ClickHandler() {
            public void onClick(ClickEvent event) {
                showCopyDialog();
            }
        } );
        horiz.add( copy );

        Button rename = new Button( constants.Rename() );
        rename.addClickHandler( new ClickHandler() {
            public void onClick(ClickEvent event) {
                showRenameDialog();
            }
        } );
        horiz.add( rename );

        Button archive = new Button( constants.Archive() );
        archive.addClickHandler( new ClickHandler() {
            public void onClick(ClickEvent event) {
                if ( Window.confirm( constants.AreYouSureYouWantToArchiveRemoveThisPackage() ) ) {
                    conf.archived = true;
                    Command ref = new Command() {
                        public void execute() {
                            close.execute();
                            refreshPackageList.execute();
                        }
                    };
                    doSaveAction( ref );
                }
            }
        } );
        horiz.add( archive );

        return horiz;
    }

    private void showRenameDialog() {
        final FormStylePopup pop = new FormStylePopup( images.newWiz(),
                                                       constants.RenameThePackage() );
        pop.addRow( new HTML( constants.RenamePackageTip() ) );
        final TextBox name = new TextBox();
        pop.addAttribute( constants.NewPackageNameIs(),
                          name );
        Button ok = new Button( constants.OK() );
        pop.addAttribute( "",
                          ok );

        ok.addClickHandler( new ClickHandler() {

            public void onClick(ClickEvent event) {
                RepositoryServiceFactory.getService().renamePackage( conf.uuid,
                                                                     name.getText(),
                                                                     new GenericCallback<String>() {
                                                                         public void onSuccess(String data) {
                                                                             refreshPackageList.execute();
                                                                             conf.name = name.getText();
                                                                             refreshWidgets();
                                                                             Window.alert( constants.PackageRenamedSuccessfully() );
                                                                             pop.hide();
                                                                         }
                                                                     } );
            }
        } );

        pop.show();
    }

    /**
     * Will show a copy dialog for copying the whole package.
     */
    private void showCopyDialog() {
        final FormStylePopup pop = new FormStylePopup( images.newWiz(),
                                                       constants.CopyThePackage() );
        pop.addRow( new HTML( constants.CopyThePackageTip() ) );
        final TextBox name = new TextBox();
        pop.addAttribute( constants.NewPackageNameIs(),
                          name );
        Button ok = new Button( constants.OK() );
        pop.addAttribute( "",
                          ok );

        ok.addClickHandler( new ClickHandler() {

            public void onClick(ClickEvent event) {
                if ( !PackageNameValidator.validatePackageName( name.getText() ) ) {
                    Window.alert( constants.NotAValidPackageName() );
                    return;
                }
                LoadingPopup.showMessage( constants.PleaseWaitDotDotDot() );
                RepositoryServiceFactory.getService().copyPackage( conf.name,
                                                                   name.getText(),
                                                                   new GenericCallback<Void>() {
                                                                       public void onSuccess(Void data) {
                                                                           refreshPackageList.execute();
                                                                           Window.alert( constants.PackageCopiedSuccessfully() );
                                                                           pop.hide();
                                                                           LoadingPopup.close();
                                                                       }
                                                                   } );
            }
        } );

        pop.show();

    }

    private void doSaveAction(final Command refresh) {
        LoadingPopup.showMessage( constants.SavingPackageConfigurationPleaseWait() );

        RepositoryServiceFactory.getService().savePackage( this.conf,
                                                           new GenericCallback<ValidatedResponse>() {
                                                               public void onSuccess(ValidatedResponse data) {
                                                                   previousResponse = data;
                                                                   reload();
                                                                   LoadingPopup.showMessage( constants.PackageConfigurationUpdatedSuccessfullyRefreshingContentCache() );

                                                                   SuggestionCompletionCache.getInstance().refreshPackage( conf.name,
                                                                                                                           new Command() {
                                                                                                                               public void execute() {
                                                                                                                                   if ( refresh != null ) {
                                                                                                                                       refresh.execute();
                                                                                                                                   }
                                                                                                                                   LoadingPopup.close();
                                                                                                                               }
                                                                                                                           } );
                                                               }
                                                           } );
    }

    /**
     * Will refresh all the data.
     */
    public void reload() {
        LoadingPopup.showMessage( constants.RefreshingPackageData() );
        RepositoryServiceFactory.getService().loadPackageConfig( this.conf.uuid,
                                                                 new GenericCallback<PackageConfigData>() {
                                                                     public void onSuccess(PackageConfigData data) {
                                                                         LoadingPopup.close();
                                                                         conf = data;
                                                                         refreshWidgets();
                                                                     }
                                                                 } );
    }

    private Widget header() {

        return new PackageHeaderWidget( this.conf );

    }

    private Widget description() {

        final TextArea box = new TextArea();
        box.setText( conf.description );
        box.addChangeHandler( new ChangeHandler() {

            public void onChange(ChangeEvent event) {
                conf.description = box.getText();
            }
        } );
        box.setWidth( "400px" );

        return box;
    }

}
