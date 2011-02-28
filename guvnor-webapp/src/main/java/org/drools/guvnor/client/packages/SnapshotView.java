/*
 * Copyright 2011 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.drools.guvnor.client.packages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.guvnor.client.common.FormStylePopup;
import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.common.LoadingPopup;
import org.drools.guvnor.client.common.PrettyFormLayout;
import org.drools.guvnor.client.common.RulePackageSelector;
import org.drools.guvnor.client.explorer.ExplorerNodeConfig;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.resources.Images;
import org.drools.guvnor.client.rpc.PackageConfigData;
import org.drools.guvnor.client.rpc.RepositoryServiceAsync;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;
import org.drools.guvnor.client.rpc.SnapshotInfo;
import org.drools.guvnor.client.ruleeditor.MultiViewRow;
import org.drools.guvnor.client.rulelist.OpenItemCommand;
import org.drools.guvnor.client.util.Format;
import org.drools.guvnor.client.util.TabOpener;
import org.drools.guvnor.client.widgets.tables.SnapshotComparisonPagedTable;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * This is the new snapshot view.
 */
public class SnapshotView extends Composite {

    private static Constants             constants       = GWT.create( Constants.class );
    private static Images                images          = GWT.create( Images.class );

    public static final String           LATEST_SNAPSHOT = "LATEST";

    private PackageConfigData            parentConf;
    private SnapshotInfo                 snapInfo;

    private Command                      close;

    private ListBox                      box             = new ListBox();

    private VerticalPanel                vert;
    private SnapshotComparisonPagedTable table;
    private final OpenItemCommand        openCommand     = new OpenItemCommand() {

                                                             public void open(String uuid) {
                                                                 TabOpener tabOpener = TabOpener.getInstance();
                                                                 tabOpener.openAsset( uuid );
                                                             }

                                                             public void open(MultiViewRow[] rows) {
                                                                 // Do nothing,
                                                                 // unsupported
                                                             }
                                                         };

    public SnapshotView(SnapshotInfo snapInfo,
                        PackageConfigData parentPackage,
                        Command closeSnap) {

        vert = new VerticalPanel();
        this.snapInfo = snapInfo;
        this.parentConf = parentPackage;
        this.close = closeSnap;
        PrettyFormLayout head = new PrettyFormLayout();

        head.addHeader( images.snapshot(),
                        header() );

        vert.add( head );
        vert.add( infoPanel() );
        vert.setWidth( "100%" );
        initWidget( vert );

    }

    private Widget header() {
        FlexTable ft = new FlexTable();

        ft.setWidget( 0,
                      0,
                      new Label( constants.ViewingSnapshot() ) );
        ft.setWidget( 0,
                      1,
                      new HTML( "<b>"
                                + this.snapInfo.name
                                + "</b>" ) );
        ft.getFlexCellFormatter().setHorizontalAlignment( 0,
                                                          0,
                                                          HasHorizontalAlignment.ALIGN_RIGHT );

        ft.setWidget( 1,
                      0,
                      new Label( constants.ForPackage() ) );
        ft.setWidget( 1,
                      1,
                      new Label( this.parentConf.name ) );
        ft.getFlexCellFormatter().setHorizontalAlignment( 1,
                                                          0,
                                                          HasHorizontalAlignment.ALIGN_RIGHT );

        HTML dLink = new HTML( "<a href='"
                               + PackageBuilderWidget.getDownloadLink( this.parentConf )
                               + "' target='_blank'>"
                               + constants.clickHereToDownloadBinaryOrCopyURLForDeploymentAgent()
                               + "</a>" );
        ft.setWidget( 2,
                      0,
                      new Label( constants.DeploymentURL() ) );
        ft.setWidget( 2,
                      1,
                      dLink );
        ft.getFlexCellFormatter().setHorizontalAlignment( 2,
                                                          0,
                                                          HasHorizontalAlignment.ALIGN_RIGHT );

        ft.setWidget( 3,
                      0,
                      new Label( constants.SnapshotCreatedOn() ) );
        ft.setWidget( 3,
                      1,
                      new Label( DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_TIME_SHORT).format(parentConf.lastModified)));
        ft.getFlexCellFormatter().setHorizontalAlignment( 4,
                                                          0,
                                                          HasHorizontalAlignment.ALIGN_RIGHT );

        ft.setWidget( 4,
                      0,
                      new Label( constants.CommentColon() ) );
        ft.setWidget( 4,
                      1,
                      new Label( parentConf.checkinComment ) );
        ft.getFlexCellFormatter().setHorizontalAlignment( 4,
                                                          0,
                                                          HasHorizontalAlignment.ALIGN_RIGHT );

        HorizontalPanel actions = new HorizontalPanel();

        actions.add( getDeleteButton( this.snapInfo.name,
                                      this.parentConf.name ) );
        actions.add( getCopyButton( this.snapInfo.name,
                                    this.parentConf.name ) );

        ft.setWidget( 5,
                      0,
                      actions );

        ft.setWidget( 6,
                      0,
                      getCompareWidget( this.parentConf.name,
                                        this.snapInfo.name ) );
        ft.getFlexCellFormatter().setHorizontalAlignment( 4,
                                                          0,
                                                          HasHorizontalAlignment.ALIGN_RIGHT );

        ft.getFlexCellFormatter().setColSpan( 5,
                                              0,
                                              2 );

        return ft;
    }

    private Widget getCompareWidget(final String packageName,
                                    final String snapshotName) {
        HorizontalPanel hPanel = new HorizontalPanel();
        hPanel.add( new Label( "Compare to:" ) );

        RepositoryServiceFactory.getService().listSnapshots( this.parentConf.name,
                                                             new GenericCallback<SnapshotInfo[]>() {
                                                                 public void onSuccess(SnapshotInfo[] info) {
                                                                     for ( int i = 0; i < info.length; i++ ) {
                                                                         if ( !snapshotName.equals( info[i].name ) ) {
                                                                             box.addItem( info[i].name );
                                                                         }
                                                                     }
                                                                 }
                                                             } );
        hPanel.add( box );

        Button button = new Button( "Compare" );
        button.addClickHandler( new ClickHandler() {
            public void onClick(ClickEvent event) {
                if ( table != null ) {
                    vert.remove( table );
                }
                table = new SnapshotComparisonPagedTable( packageName,
                                                          snapshotName,
                                                          box.getItemText( box.getSelectedIndex() ),
                                                          openCommand );
                vert.add( table );
            }
        } );

        hPanel.add( button );

        return hPanel;
    }

    private Button getDeleteButton(final String snapshotName,
                                   final String pkgName) {
        Button btn = new Button( constants.Delete() );
        btn.addClickHandler( new ClickHandler() {
            public void onClick(ClickEvent event) {
                if ( Window.confirm( Format.format( constants.SnapshotDeleteConfirm(),
                                                    snapshotName,
                                                    pkgName ) ) ) {
                    RepositoryServiceFactory.getService().copyOrRemoveSnapshot( pkgName,
                                                                                snapshotName,
                                                                                true,
                                                                                null,
                                                                                new GenericCallback<java.lang.Void>() {
                                                                                    public void onSuccess(Void v) {
                                                                                        close.execute();
                                                                                        Window.alert( constants.SnapshotWasDeleted() );

                                                                                    }
                                                                                } );
                }
            }

        } );
        return btn;
    }

    private Button getCopyButton(final String snapshotName,
                                 final String packageName) {
        final RepositoryServiceAsync serv = RepositoryServiceFactory.getService();
        Button btn = new Button( constants.Copy() );
        btn.addClickHandler( new ClickHandler() {
            public void onClick(ClickEvent event) {
                serv.listSnapshots( packageName,
                                    createGenericCallback( snapshotName,
                                                           packageName,
                                                           serv ) );
            }
        } );
        return btn;
    }

    private GenericCallback<SnapshotInfo[]> createGenericCallback(final String snapshotName,
                                                                  final String packageName,
                                                                  final RepositoryServiceAsync serv) {
        return new GenericCallback<SnapshotInfo[]>() {
            public void onSuccess(final SnapshotInfo[] snaps) {
                final FormStylePopup copy = new FormStylePopup( images.snapshot(),
                                                                Format.format( constants.CopySnapshotText(),
                                                                               snapshotName ) );
                final List<RadioButton> options = new ArrayList<RadioButton>();
                VerticalPanel vert = new VerticalPanel();
                for ( int i = 0; i < snaps.length; i++ ) {
                    // cant copy onto to itself...
                    if ( !snaps[i].name.equals( snapshotName ) ) {
                        RadioButton existing = new RadioButton( "snapshotNameGroup",
                                                                snaps[i].name ); // NON-NLS
                        options.add( existing );
                        vert.add( existing );
                    }
                }

                HorizontalPanel newNameHorizontalPanel = new HorizontalPanel();
                final TextBox newNameTextBox = new TextBox();
                final String newNameText = constants.NEW()
                                           + ": ";

                final RadioButton newNameRadioButton = new RadioButton( "snapshotNameGroup",
                                                                        newNameText );
                newNameHorizontalPanel.add( newNameRadioButton );
                newNameTextBox.setEnabled( false );
                newNameRadioButton.addClickHandler( new ClickHandler() {
                    public void onClick(ClickEvent event) {
                        newNameTextBox.setEnabled( true );
                    }
                } );

                newNameHorizontalPanel.add( newNameTextBox );
                options.add( newNameRadioButton );
                vert.add( newNameHorizontalPanel );

                copy.addAttribute( constants.ExistingSnapshots(),
                                   vert );

                Button ok = new Button( constants.OK() );
                copy.addAttribute( "",
                                   ok );
                ok.addClickHandler( new ClickHandler() {
                    public void onClick(ClickEvent event) {
                        if ( !isOneButtonSelected( options ) ) {
                            Window.alert( constants.YouHaveToEnterOrChoseALabelNameForTheSnapshot() );
                            return;
                        }

                        if ( newNameRadioButton.getValue() ) {
                            if ( checkUnique( snaps,
                                              newNameTextBox.getText() ) ) {
                                serv.copyOrRemoveSnapshot( packageName,
                                                           snapshotName,
                                                           false,
                                                           newNameTextBox.getText(),
                                                           new GenericCallback<java.lang.Void>() {
                                                               public void onSuccess(Void v) {
                                                                   copy.hide();
                                                                   Window.alert( Format.format( constants.CreatedSnapshot0ForPackage1(),
                                                                                                newNameTextBox.getText(),
                                                                                                packageName ) );
                                                               }
                                                           } );
                            }
                        } else {
                            for ( RadioButton rb : options ) {
                                if ( rb.getValue() ) {
                                    final String newName = rb.getText();
                                    serv.copyOrRemoveSnapshot( packageName,
                                                               snapshotName,
                                                               false,
                                                               newName,
                                                               new GenericCallback<java.lang.Void>() {
                                                                   public void onSuccess(Void v) {
                                                                       copy.hide();
                                                                       Window.alert( Format.format( constants.Snapshot0ForPackage1WasCopiedFrom2(),
                                                                                                    newName,
                                                                                                    packageName,
                                                                                                    snapshotName ) );
                                                                   }
                                                               } );
                                }
                            }
                        }
                    }

                    private boolean isOneButtonSelected(final List<RadioButton> options) {
                        boolean oneButtonIsSelected = false;
                        for ( RadioButton rb : options ) {
                            if ( rb.getValue() ) {
                                oneButtonIsSelected = true;
                                break;
                            }
                        }
                        return oneButtonIsSelected;
                    }

                    private boolean checkUnique(SnapshotInfo[] snaps,
                                                String name) {
                        for ( SnapshotInfo sn : snaps ) {
                            if ( sn.name.equals( name ) ) {
                                Window.alert( constants.PleaseEnterANonExistingSnapshotName() );
                                return false;
                            }
                        }
                        return true;
                    }
                } );
                copy.show();
            }
        };
    }

    private Widget infoPanel() {
        return packageTree();
    }

    protected Widget packageTree() {
        Map<TreeItem, String> itemWidgets = new HashMap<TreeItem, String>();
        Tree root = new Tree();
        root.setAnimationEnabled( true );

        TreeItem pkg = ExplorerNodeConfig.getPackageItemStructure( parentConf.name,
                                                                   snapInfo.uuid,
                                                                   itemWidgets );
        pkg.setUserObject( snapInfo );
        root.addItem( pkg );

        ScrollPanel packagesTreeItemPanel = new ScrollPanel( root );
        root.addSelectionHandler( new SelectionHandler<TreeItem>() {
            public void onSelection(SelectionEvent<TreeItem> event) {
                Object uo = event.getSelectedItem().getUserObject();
                if ( uo instanceof Object[] ) {
                    Object o = ((Object[]) uo)[0];
                    showAssetList( new String[]{(String) o} );
                } else if ( uo instanceof SnapshotInfo ) {
                    SnapshotInfo s = (SnapshotInfo) uo;
                    TabOpener tabOpener = TabOpener.getInstance();
                    tabOpener.openPackageEditor( s.uuid,
                                                 null );
                }
            }
        } );

        return packagesTreeItemPanel;
    }

    protected void showAssetList(final String[] assetTypes) {

        StringBuilder keyBuilder = new StringBuilder( this.snapInfo.uuid );
        for ( String assetType : assetTypes ) {
            keyBuilder.append( assetType );
        }

        TabOpener tabOpener = TabOpener.getInstance();
        tabOpener.openSnapshotAssetList( snapInfo.name,
                                         snapInfo.uuid,
                                         assetTypes,
                                         keyBuilder.toString() );
    }

    public static void showNewSnapshot(final Command refreshCmd) {
        final FormStylePopup pop = new FormStylePopup( images.snapshot(),
                                                       constants.NewSnapshot() );
        final RulePackageSelector sel = new RulePackageSelector();

        pop.addAttribute( constants.ForPackage(),
                          sel );
        Button ok = new Button( constants.OK() );
        pop.addAttribute( "",
                          ok );
        pop.show();

        ok.addClickHandler( new ClickHandler() {
            public void onClick(ClickEvent event) {
                pop.hide();
                String pkg = sel.getSelectedPackage();
                PackageBuilderWidget.showSnapshotDialog( pkg,
                                                         refreshCmd );
            }
        } );

    }

    public static void rebuildBinaries() {
        if ( Window.confirm( constants.SnapshotRebuildWarning() ) ) {
            LoadingPopup.showMessage( constants.RebuildingSnapshotsPleaseWaitThisMayTakeSomeTime() );
            RepositoryServiceFactory.getService().rebuildSnapshots( new GenericCallback<java.lang.Void>() {
                public void onSuccess(Void v) {
                    LoadingPopup.close();
                    Window.alert( constants.SnapshotsWereRebuiltSuccessfully() );
                }
            } );
        }
    }

}
