package org.drools.brms.client.admin;

import org.drools.brms.client.common.FormStyleLayout;
import org.drools.brms.client.common.GenericCallback;
import org.drools.brms.client.common.LoadingPopup;
import org.drools.brms.client.rpc.RepositoryServiceFactory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormHandler;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormSubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormSubmitEvent;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;

public class BackupManager extends Composite {

    public static final String FORM_FIELD_REPOSITORY = "repo";

    public BackupManager() {

        FormStyleLayout widtab = new FormStyleLayout( "images/backup_large.png",
                                                      "Manage Backups" );

        widtab.addAttribute( "",
                             new HTML( "<i>Import and Export rules repository 'drools:repository'</i>" ) );
        widtab.addRow( new HTML( "<hr/>" ) );

        widtab.addAttribute( "Import 'drools:repository' from xml file",
                             newImportWidget() );
        widtab.addAttribute( "Export 'drools:repository'",
                             newExportWidget() );
        widtab.addRow( new HTML( "<hr/>" ) );

        widtab.addAttribute( "Delete rules repository",
                             cleanRepository() );

        initWidget( widtab );

    }

    private Widget newExportWidget() {
        HorizontalPanel horiz = new HorizontalPanel();

        Button create = new Button( "Export" );
        create.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                exportRepository();
            }
        } );

        horiz.add( create );
        return horiz;
    }
    
    private Widget cleanRepository() {
        HorizontalPanel horiz = new HorizontalPanel();

        Button delete = new Button( "Execute" );
        delete.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                if ( Window.confirm( "Are you really sure about delete your entry drools repository database?" ) ) {  
                    RepositoryServiceFactory.getService().clearRulesRepository( new GenericCallback() {
                        public void onSuccess(Object data) {
                            Window.alert( "Rules repository deleted." );
                        }
                    });
                } else {
                    Window.alert( "Operation cancelled" );
                }
            }
        } );
        
        horiz.add( delete );
        return horiz;
    }

    

    private Widget newImportWidget() {

        final FormPanel uploadFormPanel = new FormPanel();
        uploadFormPanel.setAction( GWT.getModuleBaseURL() + "fileManager" );
        uploadFormPanel.setEncoding( FormPanel.ENCODING_MULTIPART );
        uploadFormPanel.setMethod( FormPanel.METHOD_POST );

        HorizontalPanel panel = new HorizontalPanel();
        uploadFormPanel.setWidget( panel );

        final FileUpload upload = new FileUpload();
        upload.setName( "uploadFormElement" );
        panel.add( upload );

        panel.add( new Button( "Submit",
                               new ClickListener() {
                                   public void onClick(Widget sender) {
                                       LoadingPopup.showMessage( "Importing 'drools:repository' file" );
                                       uploadFormPanel.submit();
                                   }
                               } ) );

        uploadFormPanel.addFormHandler( new FormHandler() {
            public void onSubmitComplete(FormSubmitCompleteEvent event) {
                if ( event.getResults() != null ) Window.alert( event.getResults() );
                else Window.alert( "Rules repository imported successful, execute a browser refresh (F5) to update static content." );                
                LoadingPopup.close();
            }

            public void onSubmit(FormSubmitEvent event) {
                if ( upload.getFilename().length() == 0 ) {
                    Window.alert( "You did not specify an exported repository filename!" );
                    event.setCancelled( true );
                } else if ( !upload.getFilename().endsWith( ".xml" ) ) {
                    Window.alert( "Please specify a valid repository xml file." );
                    event.setCancelled( true );
                }

            }
        } );

        return uploadFormPanel;
    }

    private void exportRepository() {
        byte[] returnfromfunction = null;
        LoadingPopup.showMessage( "Exporting 'drools:repository' file" );
        Window.open( GWT.getModuleBaseURL() + "fileManager?" + FORM_FIELD_REPOSITORY + "=true",
                     "downloading...",
                     "" );
        LoadingPopup.close();
    }

}
