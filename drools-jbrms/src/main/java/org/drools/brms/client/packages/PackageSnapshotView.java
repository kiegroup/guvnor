package org.drools.brms.client.packages;
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



import org.drools.brms.client.common.DirtyableComposite;
import org.drools.brms.client.common.FormStyleLayout;
import org.drools.brms.client.common.FormStylePopup;
import org.drools.brms.client.common.GenericCallback;
import org.drools.brms.client.common.ImageButton;
import org.drools.brms.client.common.LoadingPopup;
import org.drools.brms.client.rpc.PackageConfigData;
import org.drools.brms.client.rpc.RepositoryServiceAsync;
import org.drools.brms.client.rpc.RepositoryServiceFactory;
import org.drools.brms.client.rpc.SnapshotInfo;
import org.drools.brms.client.table.SortableTable;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.TreeListener;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;

/**
 * This contains a list of packages and their deployment snapshots.
 * @deprecated Use SnapshotView instead.
 * @author Michael Neale
 */
public class PackageSnapshotView extends Composite {

    private RepositoryServiceAsync service;
    private TabPanel tab;
    private FlexTable layout;

    public static final String LATEST_SNAPSHOT = "LATEST";

    public PackageSnapshotView() {

        layout = new FlexTable();
        tab = new TabPanel();
        tab.setWidth( "100%" );
        tab.setHeight( "100%" );

        VerticalPanel vert = new VerticalPanel();
        vert.add( layout );

        Button rebuild = new Button("Rebuild snapshot binaries");
        rebuild.setTitle( "Rebuilding the binaries may be needed if the BRMS software was updated. Otherwise it should not be needed." );
        rebuild.addClickListener( new ClickListener() {

            public void onClick(Widget arg0) {
                if (Window.confirm( "Rebuilding the snapshot binaries will take some time, and only needs to be done if" +
                        " the BRMS itself has been updated recently. This will also cause the rule agents to load the rules anew." +
                        " Are you sure you want to do this?" )) {
	                LoadingPopup.showMessage( "Rebuilding snapshots. Please wait, this may take some time..." );
	                RepositoryServiceFactory.getService().rebuildSnapshots( new GenericCallback() {
	                    public void onSuccess(Object data) {
	                        LoadingPopup.close();
	                        Window.alert( "Snapshots were rebuilt successfully." );
	                    }
	                });
                }
            }
        });
        vert.add( rebuild );

        tab.add( vert, "<img src='images/package_snapshot.gif'>Snapshots</a>", true );
        layout.getCellFormatter().setWidth( 0, 0, "28%" );

        service = RepositoryServiceFactory.getService();

        refreshPackageList();

        layout.setWidth( "100%" );

        initWidget( tab );

        tab.selectTab( 0 );

    }

    private void refreshPackageList() {
        LoadingPopup.showMessage( "Loading package list..." );
        service.listPackages( new GenericCallback() {
            public void onSuccess(Object data) {
                PackageConfigData[] list = (PackageConfigData[]) data;
                addPackages(list);
                LoadingPopup.close();
            }
        });
    }

    private void addPackages(final PackageConfigData[] list) {

        Tree snapTree = new Tree();

        VerticalPanel packages = new VerticalPanel();
        for ( int i = 0; i < list.length; i++ ) {
            final String pkgName = list[i].name;
            TreeItem item  = makeItem( pkgName, "images/package_snapshot.gif", new Command() {
                public void execute() {
                    showPackage(pkgName);
                }
            } );


            snapTree.addItem( item );

        }

        packages.add( snapTree );

        HTML refresh = new HTML("Refresh list:&nbsp;<img src='images/refresh.gif'/>");

        //Image refresh = new Image("images/refresh.gif");
        refresh.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                refreshPackageList();
            }
        });

        snapTree.addTreeListener( new TreeListener() {
            public void onTreeItemSelected(TreeItem item) {
                DeferredCommand.add( (Command) item.getUserObject() );
            }
            public void onTreeItemStateChanged(TreeItem a) {}
        });
        packages.setVerticalAlignment( HasVerticalAlignment.ALIGN_TOP );
        packages.setHorizontalAlignment( HasHorizontalAlignment.ALIGN_LEFT );
        packages.add( refresh );
        packages.setStyleName( "snapshot-List" );
        layout.setWidget( 0, 0, packages );
        layout.getCellFormatter().setVerticalAlignment( 0, 0, HasVerticalAlignment.ALIGN_TOP );
    }

    /**
     * This will load up the list of snapshots for a package.
     */
    private void showPackage(final String pkgName) {
        LoadingPopup.showMessage( "Loading snapshots..." );
        service.listSnapshots( pkgName, new GenericCallback() {
            public void onSuccess(Object data) {
                SnapshotInfo[] list = (SnapshotInfo[]) data;

                renderListOfSnapshots(pkgName, list);
                LoadingPopup.close();
            }
        });
    }

    /**
     * This will render the snapshot list.
     */
    protected void renderListOfSnapshots(String pkgName, SnapshotInfo[] list) {

        FormStyleLayout right = new FormStyleLayout("images/snapshot.png", "Labelled snapshots for package: " + pkgName);

        FlexTable table = new FlexTable();
        table.setText( 0, 1, "Name" );
        table.setText( 0, 2, "Comment" );
        table.getRowFormatter().setStyleName( 0, SortableTable.styleListHeader );

        for ( int i = 0; i < list.length; i++ ) {
            int row = i + 1;
            Label name = new Label( list[i].name );
            table.setWidget( row, 0,  new Image("images/package_snapshot_item.gif"));
            table.setWidget( row, 1, name );
            table.setWidget( row, 2, new Label(list[i].comment) );
            table.setWidget( row, 3, getOpenSnapshotButton(pkgName, name.getText(), list[i].uuid) );
            table.setWidget( row, 4, getCopyButton(pkgName, name.getText() ) );
            table.setWidget( row, 5, getDeleteButton(name.getText(), pkgName) );

            if (i%2 == 0) {
                table.getRowFormatter().setStyleName( i + 1, SortableTable.styleEvenRow );
            }
        }

        right.setWidth( "100%" );
        //right.setHeight( "100%" );
        right.addRow( table );
        table.setWidth( "100%" );
        right.setStyleName( SortableTable.styleList );



        layout.setWidget( 0, 1, right);
        layout.getFlexCellFormatter().setVerticalAlignment( 0, 1, HasVerticalAlignment.ALIGN_TOP );

    }

    private Button getCopyButton(final String packageName, final String snapshotName) {
        final FormStylePopup copy = new FormStylePopup("images/snapshot.png", "Copy snapshot " + snapshotName);
        final TextBox box = new TextBox();
        copy.addAttribute( "New label:", box );
        Button ok = new Button("OK");
        copy.addAttribute( "", ok );

        ok.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                service.copyOrRemoveSnapshot( packageName, snapshotName, false, box.getText(), new GenericCallback() {
                    public void onSuccess(Object data) {
                        showPackage( packageName );
                        copy.hide();
                    }
                });
            }
        } );


        Button btn = new Button("Copy");
        btn.addClickListener( new ClickListener() {
            public void onClick(Widget w) {

    		  copy.show();
            }
        });

        return btn;
    }

    private Button getOpenSnapshotButton(final String pkgName, final String snapshotName, final String uuid) {


        Button but = new Button("Open");
        but.addClickListener( new ClickListener() {
            public void onClick(Widget w) {

                openPackageSnapshot( pkgName,
                                     snapshotName,
                                     uuid );
            }
        });

        return but;
    }

    private Button getDeleteButton(final String snapshotName, final String pkgName) {
        Button btn = new Button("Delete");
        btn.addClickListener( new ClickListener() {

            public void onClick(Widget w) {
                boolean confirm = Window.confirm( "Are you sure you want to delete the snapshot labelled [" + snapshotName +
                                 "] from the package [" + pkgName + "] ?");

                if (!confirm)
                {
                    return;
                } else {
                    service.copyOrRemoveSnapshot( pkgName, snapshotName, true, null, new GenericCallback() {
                        public void onSuccess(Object data) {
                            showPackage( pkgName );
                        }
                    });
                }
            }

        });
        return btn;
    }

    private TreeItem makeItem(String name, String icon, Object command) {
        TreeItem item = new TreeItem();
        item.setHTML( "<img src=\""+ icon + "\">" + name + "</a>" );
        item.setUserObject( command );
        return item;
    }

    /**
     * This opens the package viewer, showing the contents of that snapshot.
     */
    private void openPackageSnapshot(final String pkgName,
                                     final String snapshotName,
                                     final String uuid) {
        FlexTable viewLayout = new FlexTable();
        String msg = "<b>Viewing snapshot labelled: </b>" + snapshotName +
            " for package " + pkgName + ". This should not be edited.";
        HorizontalPanel horiz = new HorizontalPanel();
        horiz.add( new HTML(msg) );
        Image close = new ImageButton("images/close.gif");
        close.setTitle( "Close this view" );
        close.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                tab.remove( 1 );
                tab.selectTab( 0 );
            }
        } );
        horiz.add( close );
        viewLayout.setWidget( 0, 0, horiz );
        FlexCellFormatter formatter = viewLayout.getFlexCellFormatter();
        formatter.setStyleName( 0, 0, "editable-Surface" );

        viewLayout.setWidget( 1, 0, new PackageManagerView(uuid, snapshotName) );

        viewLayout.setWidth( "100%" );
        viewLayout.setHeight( "100%" );

        if (tab.getWidgetCount() > 1) {
            tab.remove( 1 );
        }
        tab.add( viewLayout, "<img src='images/package_snapshot_item.gif'> " + pkgName + " [" + snapshotName + "]", true );
        tab.selectTab( 1 );
    }

}