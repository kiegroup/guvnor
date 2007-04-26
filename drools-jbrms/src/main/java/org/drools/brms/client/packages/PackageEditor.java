package org.drools.brms.client.packages;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.drools.brms.client.common.ErrorPopup;
import org.drools.brms.client.common.FormStyleLayout;
import org.drools.brms.client.common.FormStylePopup;
import org.drools.brms.client.common.GenericCallback;
import org.drools.brms.client.common.ImageButton;
import org.drools.brms.client.common.InfoPopup;
import org.drools.brms.client.common.LoadingPopup;
import org.drools.brms.client.common.StatusChangePopup;
import org.drools.brms.client.common.ValidationMessageWidget;
import org.drools.brms.client.common.YesNoDialog;
import org.drools.brms.client.rpc.BuilderResult;
import org.drools.brms.client.rpc.PackageConfigData;
import org.drools.brms.client.rpc.RepositoryServiceFactory;
import org.drools.brms.client.rpc.SnapshotInfo;
import org.drools.brms.client.rpc.ValidatedResponse;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FormHandler;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormSubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormSubmitEvent;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * This is the package editor and viewer for package configuration.
 * 
 * @author Michael Neale
 */
public class PackageEditor extends FormStyleLayout {
    
   

    private PackageConfigData conf;
    private HTML status;
    protected ValidatedResponse previousResponse;

    public PackageEditor(PackageConfigData data) {
        this.conf = data;
        
        setStyleName( "package-Editor" );

        setWidth( "100%" );
        
        refreshWidgets();
    }

    private void refreshWidgets() {
        clear();
        addHeader( "images/package_large.png", this.conf.name );
        

        addRow( warnings() );

        //build stuff
        final SimplePanel buildResults = new SimplePanel();
        buildResults.setStyleName( "build-Results" );
        addAttribute( "Build binary package", buildButton(buildResults));
        addRow( buildResults );
        
      
        
        addAttribute( "Description:", description() );
        addAttribute( "Header:", header() );
        addAttribute( "External URI:", externalURI() );
        addRow(new HTML("<hr/>"));
        addAttribute( "Last modified:", new Label(this.conf.lastModified.toLocaleString())  );
        addAttribute( "Last contributor:", new Label(this.conf.lasContributor));
        addRow(new HTML("<hr/>"));
        
        status = new HTML();
        HorizontalPanel statusBar = new HorizontalPanel();
        Image editState = new ImageButton("images/edit.gif");
        editState.setTitle( "Change status." );
        editState.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                showStatusChanger(w);
            }

        } );
        statusBar.add( status );
        
        statusBar.add( editState );
        
        setState(conf.state);
        addAttribute("Status:", statusBar);

        addRow( saveWidgets() );
        addRow(new HTML("<hr/>"));

        
    }
    
    private Widget buildButton(final Panel buildResults) {
        final Button build = new Button("Build");
        InfoPopup info = new InfoPopup("Building", "Building the package will collate, " +
                "validate, and compile all the rules into a binary package, ready for deployment." +
                "If successful, a package will be downloaded. If not, a list of errors will show what needs to be addressed.");
        final AbsolutePanel ab = new AbsolutePanel();
        ab.add( build ); ab.add( info );
        build.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                build.setEnabled( false );
                doBuild(buildResults);
                build.setEnabled( true );
            }
        });          
        return ab;
    }

    /**
     * Actually do the building.
     * @param buildResults The panel to stuff the results in.
     */
    private void doBuild(final Panel buildResults) {
        buildResults.clear();
        
        final HorizontalPanel busy = new HorizontalPanel();
        busy.add( new Label("Validating and building package...") );
        busy.add( new Image("images/spinner.gif") );
        
        buildResults.add( busy );
        
        DeferredCommand.add( new Command() {
            public void execute() {
                RepositoryServiceFactory.getService().buildPackage( conf.uuid, new AsyncCallback() {
                    public void onSuccess(Object data) {
                        if (data == null) {
                            showSuccessfulBuild(buildResults);
                            
                        } else {
                            BuilderResult[] results = (BuilderResult[]) data;
                            showBuilderErrors(results, buildResults);
                        }
                    }
                    public void onFailure(Throwable t) {
                        ErrorPopup.showMessage( t.getMessage() );
                        buildResults.clear();
                    }
                });
            }
        });
        

        
    }
    
    /**
     * This is called to display the success (and a download option).
     * @param buildResults
     */
    private void showSuccessfulBuild(Panel buildResults) {
        buildResults.clear();
        buildResults.add( new Label("Package build successfully.") );
    }
    
    /**
     * This is called in the unhappy event of there being errors.  
     */
    private void showBuilderErrors(BuilderResult[] results, Panel buildResults) {
        buildResults.clear();
        
        FlexTable errTable = new FlexTable();
        errTable.setStyleName( "error-List" );
        errTable.setText( 0, 1, "Format" );
        errTable.setText( 0, 2, "Name" );
        errTable.setText( 0, 3, "Message" );
        
        for ( int i = 0; i < results.length; i++ ) {
            int row = i+1;
            BuilderResult res = results[i];
            errTable.setWidget( row, 0, new Image("images/error.gif"));
            errTable.setText( row, 1, res.assetFormat );
            errTable.setText( row, 2, res.assetName );
            errTable.setText( row, 3, res.message );
            
            errTable.setWidget( row, 4, new Button("show") );
        }
        
        buildResults.add( errTable );
        
    }    

    private Widget warnings() {
        if (this.previousResponse != null && this.previousResponse.hasErrors) {
            Image img = new Image("images/warning.gif");
            HorizontalPanel h = new HorizontalPanel();
            h.add( img );
            HTML msg = new HTML("<b>There were errors validating this package configuration.");
            h.add( msg );
            Button show = new Button("View errors");
            show.addClickListener( new ClickListener() {
                public void onClick(Widget w) {
                    ValidationMessageWidget wid = new ValidationMessageWidget(previousResponse.errorHeader, previousResponse.errorMessage);
                    wid.setPopupPosition( Window.getClientWidth()/4, w.getAbsoluteTop()  );
                    wid.show();
                }
            } );
            h.add( show );
            return h;
        } else {
            return new SimplePanel();
        }
    }

    protected void showStatusChanger(Widget w) {
        final StatusChangePopup pop = new StatusChangePopup(conf.uuid, true);
        pop.setChangeStatusEvent(new Command() {
            public void execute() {
                setState( pop.getState() );
            }                    
        });
        pop.setPopupPosition( w.getAbsoluteLeft(), w.getAbsoluteTop() );
        pop.show();
        
    }

    private void setState(String state) {
        status.setHTML( "<b>" + state + "</b>" );
        
    }

    /**
     * This will get the save widgets.
     */
    private Widget saveWidgets() {
        
        HorizontalPanel horiz = new HorizontalPanel();
        
        Button save = new Button("Save configuration changes");
        
        save.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                doSaveAction();                
            }
        } );
        
        horiz.add( save );
        
        Button snap = new Button("Create snapshot for deployment");
        snap.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                showSnapshotDialog(w);
            }
        } );
        horiz.add( snap );
        
        Button archive = new Button("Archive");
        archive.addClickListener(new ClickListener() {
            public void onClick(Widget w) {
                YesNoDialog diag = new YesNoDialog("Are you sure you want to archive (remove) this package?", new Command() {
                    public void execute() {
                        conf.archived = true;
                        doSaveAction();
                        PackageExplorerWidget local = (PackageExplorerWidget) getParent().getParent();
                        local.refreshTreeView();
                    }                        
                });
                diag.setPopupPosition(Window.getClientWidth() / 2, Window.getClientHeight() / 2);
                diag.show();
            }
        });
        horiz.add(archive);
                
        return horiz;
    }

    /**
     * This will display a dialog for creating a snapshot.
     */
    private void showSnapshotDialog(Widget w) {
        LoadingPopup.showMessage( "Loading existing snapshots..." );
        final FormStylePopup form = new FormStylePopup("images/snapshot.png", "Create a snapshot for deployment.");
        form.addRow( new HTML("<i>A package snapshot is essentially a " +
                "read only 'locked in' and labelled view of a package at a point in time, which can be used for deployment.</i>") );
        
        final VerticalPanel vert = new VerticalPanel();
        form.addAttribute( "Choose or create snapshot name:",  vert);
        final List radioList = new ArrayList();
        final TextBox newName = new TextBox();
        final String newSnapshotText = "NEW: ";
        
        RepositoryServiceFactory.getService().listSnapshots( conf.name, new GenericCallback() {
            public void onSuccess(Object data) {
                SnapshotInfo[] result = (SnapshotInfo[]) data;
                for ( int i = 0; i < result.length; i++ ) {
                    RadioButton existing = new RadioButton("snapshotNameGroup", result[i].name);
                    radioList.add( existing );
                    vert.add( existing );    
                }
                HorizontalPanel newSnap = new HorizontalPanel();
                
                final RadioButton newSnapRadio = new RadioButton("snapshotNameGroup", newSnapshotText);                
                newSnap.add( newSnapRadio );
                newName.setEnabled( false );
                newSnapRadio.addClickListener( new ClickListener() {

                    public void onClick(Widget w) {
                        newName.setEnabled( true );
                    }
                    
                });
                
                newSnap.add( newName );
                radioList.add( newSnapRadio );
                vert.add( newSnap );
                
                LoadingPopup.close();
            }
        });
        
        final TextBox comment = new TextBox();
        form.addAttribute( "Comment:", comment );
        
        Button create = new Button("Create new snapshot");
        form.addAttribute( "", create );
        
        create.addClickListener( new ClickListener() {
            String name = "";
            public void onClick(Widget w) {
                boolean replace = false;
                for ( Iterator iter = radioList.iterator(); iter.hasNext(); ) {
                    RadioButton but = (RadioButton) iter.next();
                    if (but.isChecked()) {
                        name = but.getText();
                        if (!but.getText().equals( newSnapshotText )) {
                            replace = true;
                        }
                        break;
                    }
                }
                if (name.equals( newSnapshotText )) {
                    name = newName.getText();
                }
                
                if (name.equals( "" )) {
                    Window.alert( "You have to enter or chose a label (name) for the snapshot." );
                    return;
                }
                
                
                RepositoryServiceFactory.getService().createPackageSnapshot( conf.name, name, replace, comment.getText(), new GenericCallback() {
                    public void onSuccess(Object data) {
                        Window.alert( "The snapshot called: " + name + " was successfully created." );
                        form.hide();
                    }
                });
            }
        } );
        
        form.setWidth( "50%" );
        form.setPopupPosition( Window.getClientWidth() / 3, Window.getClientHeight() / 3 );
        form.show();
        
        
    }

    private void doSaveAction() {
        LoadingPopup.showMessage( "Saving package configuration. Please wait ..." );
        RepositoryServiceFactory.getService().savePackage( this.conf, new GenericCallback() {
            public void onSuccess(Object data) {
                previousResponse = (ValidatedResponse) data;
                
                reload();
                SuggestionCompletionCache.getInstance().removePackage( conf.name );
                LoadingPopup.showMessage( "Package configuration updated successfully" );
                Timer t = new Timer() {
                    public void run() {
                        LoadingPopup.close();
                    }
                    
                };
                t.schedule( 2000 );
                
            }            
        });
        
    }



    /**
     * Will refresh all the data.
     */
    private void reload() {
        LoadingPopup.showMessage( "Refreshing package data..." );
        RepositoryServiceFactory.getService().loadPackageConfig( this.conf.uuid, new GenericCallback() {
            public void onSuccess(Object data) {
                LoadingPopup.close();
                conf = (PackageConfigData) data;
                refreshWidgets();
            }
        });
    }

    private Widget externalURI() {
        final TextBox box = new TextBox();
        box.setWidth( "100%" );
        box.setText( this.conf.externalURI );
        box.addChangeListener( new ChangeListener() {
            public void onChange(Widget w) {
                conf.externalURI = box.getText();
            }            
        });
        return box;
    }

    private Widget header() {
        
        final TextArea area = new TextArea();
        area.setWidth( "100%" );
        area.setVisibleLines( 8 );
        
        area.setCharacterWidth( 100 );
        
        area.setText( this.conf.header );
        area.addChangeListener( new ChangeListener() {
            public void onChange(Widget w) {
                 conf.header = area.getText();         
            }            
        });
        
        
        
        HorizontalPanel panel = new HorizontalPanel();
        panel.add( area );

        VerticalPanel vert = new VerticalPanel();

        Image max = new Image("images/max_min.gif");
        max.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                if (area.getVisibleLines() != 32) {
                    area.setVisibleLines( 32 );
                } else {
                    area.setVisibleLines( 8 );
                }
            }
        } );
        max.setTitle( "Increase view area." );
        vert.add( max );

        Image newImport = new Image("images/new_import.gif");
        newImport.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                area.setText( area.getText(  ) + "\n" + 
                              "import <your class here>");
                conf.header = area.getText();
            }
        });
        vert.add( newImport );
        newImport.setTitle( "Add a new Type/Class import to the package." );
        
        Image newGlobal = new Image("images/new_global.gif");
        newGlobal.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                area.setText( area.getText() + "\n" + 
                              "global <your class here> <variable name>");
                conf.header = area.getText();
            }
        });
        newGlobal.setTitle( "Add a new global variable declaration." );
        vert.add( newGlobal );
        
        Image newFactTemplate = new Image("images/fact_template.gif");
        newFactTemplate.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                final FactTemplateWizard wiz = new FactTemplateWizard();
                wiz.setPopupPosition( w.getAbsoluteLeft() - 400, w.getAbsoluteTop() - 250 );
                wiz.setOKClick( new Command() {
                    public void execute() {
                        area.setText( area.getText() + "\n" +
                                      wiz.getTemplateText() );
                        conf.header = area.getText();
                        
                    }
                } );
                wiz.show();
            }
        });
        newFactTemplate.setTitle( "Add a new fact template." );
        vert.add( newFactTemplate );
        
        panel.setWidth( "100%" );
        
        panel.add( vert );
        return panel;
    }


    private HorizontalPanel expandableTextArea(final TextArea area) {
        HorizontalPanel panel = new HorizontalPanel();
        panel.add( area );
        
        Image max = new Image("images/max_min.gif");
        max.setTitle( "Increase view area" );
        
        panel.add( max );
        max.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                if (area.getVisibleLines() != 32) {
                    area.setVisibleLines( 32 );                    
                } else {
                    area.setVisibleLines( 8 );
                }
            }
        } );
        return panel;
    }

    private Widget description() {
        final TextArea area = new TextArea();
        area.setWidth( "100%" );
        area.setVisibleLines( 8 );
        area.setText( conf.description );
        
        area.addChangeListener( new ChangeListener() {
            public void onChange(Widget w) {
                conf.description = area.getText();                 
            }            
        });
        
        area.setCharacterWidth( 100 );
        
        return expandableTextArea( area );
    }
    
}
