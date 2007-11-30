package org.drools.brms.client.ruleeditor;
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



import org.drools.brms.client.common.AssetFormats;
import org.drools.brms.client.common.DirtyableComposite;
import org.drools.brms.client.common.DirtyableFlexTable;
import org.drools.brms.client.common.ErrorPopup;
import org.drools.brms.client.common.FormStylePopup;
import org.drools.brms.client.common.GenericCallback;
import org.drools.brms.client.common.LoadingPopup;
import org.drools.brms.client.packages.SuggestionCompletionCache;
import org.drools.brms.client.rpc.RepositoryServiceFactory;
import org.drools.brms.client.rpc.RuleAsset;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;

/**
 * The main layout parent/controller the rule viewer.
 *
 * @author Michael Neale
 */
public class RuleViewer extends Composite {

    private Command           closeCommand;
    protected RuleAsset       asset;
    private final DirtyableFlexTable layout;

    private boolean readOnly;

    private MetaDataWidget metaWidget;
    private RuleDocumentWidget doco;
    private Widget editor;

    private ActionToolbar toolbar;


    public RuleViewer(RuleAsset asset) {
        this(asset, false);
    }

    /**
     * @param UUID The resource to open.
     * @param format The type of resource (may determine what editor is used).
     * @param name The name to be displayed.
     * @param historicalReadOnly true if this is a read only view for historical purposes.
     */
    public RuleViewer(RuleAsset asset, boolean historicalReadOnly) {
        this.asset = asset;
        this.readOnly = historicalReadOnly;
        layout = new DirtyableFlexTable();

        doWidgets();
        initWidget( this.layout );

        LoadingPopup.close();
    }

    /**
     * This will actually load up the data (this is called by the callback
     * when we get the data back from the server,
     * also determines what widgets to load up).
     */
    private void doWidgets() {
        this.layout.clear();

        FlexCellFormatter formatter = layout.getFlexCellFormatter();


        //the action widgets (checkin/close etc).
        toolbar = new ActionToolbar( asset,
                                     new Command() {
                public void execute() {
                    doCheckin();
                }
                },
                new Command() {
                    public void execute() {
                        doArchive();
                    }
                },
                new Command() {
                    public void execute() {
                        zoomIntoAsset();
                    }
                },
                new Command() {
                    public void execute() {
                        doDelete();
                    }
                },
        readOnly);


        layout.setWidget( 0, 0, toolbar );
        formatter.setAlignment( 0, 0, HasHorizontalAlignment.ALIGN_RIGHT, HasVerticalAlignment.ALIGN_TOP );


        metaWidget = new MetaDataWidget( this.asset.metaData, readOnly, this.asset.uuid, new Command() {
            public void execute() {
                refreshDataAndView();
            }

        });

        //now the main layout table
        layout.setWidget( 0, 1, metaWidget );

        formatter.setRowSpan( 0, 1, 3 );
        formatter.setVerticalAlignment( 0, 1, HasVerticalAlignment.ALIGN_TOP );
        formatter.setWidth( 0, 1, "30%" );


        //REMEMBER: subsequent rows have only one column, doh that is confusing !
        //GAAAAAAAAAAAAAAAAAAAAAAAAAAH

        editor = EditorLauncher.getEditorViewer(asset, this);
        toolbar.setCloseCommand( new Command() {
            public void execute() {
                if (layout.hasDirty()) {
                    doCloseUnsavedWarning( );
                } else {

                closeCommand.execute();
                }
            }
        } );

        layout.setWidget( 1, 0, editor);
        formatter.setVerticalAlignment(1, 0, HasVerticalAlignment.ALIGN_TOP);


        //the document widget
        doco = new RuleDocumentWidget(asset.metaData);
        layout.setWidget( 2, 0, doco );
        formatter.setVerticalAlignment(2, 0, HasVerticalAlignment.ALIGN_TOP);
    }

    void doDelete() {
        RepositoryServiceFactory.getService().deleteUncheckedRule( this.asset.uuid , this.asset.metaData.packageName, new GenericCallback() {
          public void onSuccess(Object o) {
              closeCommand.execute();
          }
       });
    }

    /**
     * This responds to the checkin command.
     */

    void doArchive() {
        this.asset.archived = true;
        this.doCheckin();
        this.closeCommand.execute();
    }

    void doCheckin() {
        this.layout.clear();

        LoadingPopup.showMessage( "Saving, please wait..." );
        RepositoryServiceFactory.getService().checkinVersion( this.asset, new GenericCallback() {


            public void onSuccess(Object o) {

                String uuid = (String)o;
                if (uuid == null) {
                    ErrorPopup.showMessage( "Failed to check in the item. Please contact your system administrator." );
                    return;
                }

                if (uuid.startsWith("ERR")) {
                	ErrorPopup.showMessage(uuid.substring(5));
                	return;
                }

                flushSuggestionCompletionCache();


                if ( editor instanceof DirtyableComposite ) {
                    ((DirtyableComposite) editor).resetDirty();
                }

                metaWidget.resetDirty();
                doco.resetDirty();

                refreshDataAndView( );
            }
        });
    }


    /**
     * In some cases we will want to flush the package dependency stuff for suggestion completions.
     * The user will still need to reload the asset editor though.
     */
    public void flushSuggestionCompletionCache() {
        if (AssetFormats.isPackageDependency( this.asset.metaData.format) ) {
            LoadingPopup.showMessage( "Refreshing content assistance..." );
            SuggestionCompletionCache.getInstance().refreshPackage( this.asset.metaData.packageName, new Command() {
                public void execute() {
                    LoadingPopup.close();
                }
            });
        }
    }

    /**
     * This will reload the contents from the database, and refresh the widgets.
     */
    public void refreshDataAndView() {

        RepositoryServiceFactory.getService().loadRuleAsset( asset.uuid, new GenericCallback() {
            public void onSuccess(Object a) {
                asset = (RuleAsset) a;
                doWidgets();
                LoadingPopup.close();
            }
        });
    }

    /**
     * Calling this will toggle the visibility of the meta-data widget (effectively zooming
     * in the rule asset).
     */
    public void zoomIntoAsset() {

       boolean vis = !layout.getFlexCellFormatter().isVisible( 2, 0 );
       this.layout.getFlexCellFormatter().setVisible( 0, 1, vis );
       this.layout.getFlexCellFormatter().setVisible( 2, 0, vis );
    }


    /**
     * This needs to be called to allow the opened viewer to close itself.
     * @param c
     */
    public void setCloseCommand(Command c) {
        this.closeCommand = c;
    }

    /**
     * Called when user wants to close, but there is "dirtyness".
     */
    protected void doCloseUnsavedWarning() {
        final FormStylePopup pop = new FormStylePopup("images/warning-large.png", "WARNING: Un-committed changes.");
        Button dis = new Button("Discard");
        Button can = new Button("Cancel");
        HorizontalPanel hor =  new HorizontalPanel();

        hor.add( dis );
        hor.add( can );

        pop.addRow( new HTML("Are you sure you want to discard changes?") );
        pop.addRow( hor );


        dis.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                closeCommand.execute();
                pop.hide();
            }
        });

        can.addClickListener( new ClickListener () {
            public void onClick(Widget w) {
                pop.hide();
            }
        });

        pop.setStyleName( "warning-Popup" );

		pop.setPopupPosition((DirtyableComposite.getWidth() - pop.getOffsetWidth()) / 2, 100);
		pop.show();
    }

}