package org.drools.guvnor.client.ruleeditor;
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



import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.common.DirtyableComposite;
import org.drools.guvnor.client.common.ErrorPopup;
import org.drools.guvnor.client.common.FormStylePopup;
import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.common.LoadingPopup;
import org.drools.guvnor.client.packages.SuggestionCompletionCache;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;
import org.drools.guvnor.client.rpc.RuleAsset;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * The main layout parent/controller the rule viewer.
 *
 * @author Michael Neale
 */
public class RuleViewer extends Composite {

    private Command           closeCommand;
    protected RuleAsset       asset;

    private boolean readOnly;

    private MetaDataWidget metaWidget;
    private RuleDocumentWidget doco;
    private Widget editor;

    private ActionToolbar toolbar;
	private VerticalPanel layout;
	private HorizontalPanel hsp;


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

        this.layout = new VerticalPanel();

        layout.setWidth("100%");
        layout.setHeight("100%");

        initWidget( layout );

        doWidgets();

        LoadingPopup.close();
    }



	/**
     * This will actually load up the data (this is called by the callback)
     * when we get the data back from the server,
     * also determines what widgets to load up).
     */
    private void doWidgets() {
    	layout.clear();


    	editor = EditorLauncher.getEditorViewer(asset, this);


        //the action widgets (checkin/close etc).
        toolbar = new ActionToolbar( asset,
                                     new ActionToolbar.CheckinAction() {
                public void doCheckin(String comment) {
                	if (editor instanceof SaveEventListener) {
                		((SaveEventListener) editor).onSave();
                	}
                    performCheckIn(comment);
                    if (editor instanceof SaveEventListener) {
                    	((SaveEventListener) editor).onAfterSave();
                    }
                }
                },
                new ActionToolbar.CheckinAction() {
                    public void doCheckin(String comment) {
                        doArchive(comment);
                    }

                },

                new Command() {
                    public void execute() {
                        doDelete();
                    }
                },
        readOnly);

        //layout.add(toolbar, DockPanel.NORTH);
        layout.add(toolbar);
        layout.setCellHeight(toolbar, "30px");
        layout.setCellHorizontalAlignment(toolbar, HasHorizontalAlignment.ALIGN_LEFT);
        layout.setCellWidth(toolbar, "100%");

        doMetaWidget();


        hsp = new HorizontalPanel();


        layout.add(hsp);




        //the document widget
        doco = new RuleDocumentWidget(asset.metaData);



        VerticalPanel vert = new VerticalPanel();
        vert.add(editor);
        editor.setHeight("100%");
        vert.add(doco);

        vert.setWidth("100%");
        vert.setHeight("100%");

        hsp.add(vert);
        //hsp.addStyleName("HorizontalSplitPanel");

        hsp.add(metaWidget);



        hsp.setCellWidth(metaWidget, "25%");

        //hsp.setSplitPosition("80%");
        hsp.setHeight("100%");

    }

    public long getLastSavedTime() {
    	return toolbar.getLastSavedTime();
    }

	private void doMetaWidget() {
		metaWidget = new MetaDataWidget( this.asset.metaData, readOnly, this.asset.uuid, new Command() {
            public void execute() {
                refreshMetaWidgetOnly();
            }
        });
	}



    protected boolean hasDirty() {
    	//not sure how to implement this now.
		return false;
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

    private void doArchive(String comment) {
        this.asset.archived = true;
        this.performCheckIn(comment);
        this.closeCommand.execute();
    }

    private void performCheckIn(String comment) {
        //layout.clear();
    	this.asset.metaData.checkinComment = comment;
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

                doco.resetDirty();

                refreshMetaWidgetOnly();

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
    	LoadingPopup.showMessage("Refreshing item...");
        RepositoryServiceFactory.getService().loadRuleAsset( asset.uuid, new GenericCallback() {
            public void onSuccess(Object a) {
                asset = (RuleAsset) a;
                doWidgets();
                LoadingPopup.close();
            }
        });
    }

    /**
     * This will only
     */
    public void refreshMetaWidgetOnly() {
    	LoadingPopup.showMessage("Refreshing item...");
        RepositoryServiceFactory.getService().loadRuleAsset( asset.uuid, new GenericCallback() {
            public void onSuccess(Object a) {
                RuleAsset asset_ = (RuleAsset) a;
                asset.metaData = asset_.metaData;
                hsp.remove(metaWidget);
                doMetaWidget();
                hsp.add(metaWidget);
                hsp.setCellWidth(metaWidget, "25%");
                LoadingPopup.close();
            }
        });
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


		pop.show();
    }

}