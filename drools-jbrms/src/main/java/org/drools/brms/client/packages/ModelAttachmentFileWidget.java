package org.drools.brms.client.packages;

import org.drools.brms.client.common.ErrorPopup;
import org.drools.brms.client.common.FormStyleLayout;
import org.drools.brms.client.common.HTMLFileManagerFields;
import org.drools.brms.client.common.ImportWidget;
import org.drools.brms.client.common.LoadingPopup;
import org.drools.brms.client.rpc.RuleAsset;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FormHandler;
import com.google.gwt.user.client.ui.FormSubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormSubmitEvent;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * This wraps a file uploader utility for model packages.
 * Model packages are jar files. 
 * 
 * @author Michael Neale
 * @author Fernando Meyer
 */

public class ModelAttachmentFileWidget extends Composite {


    private Button ok;
    private HorizontalPanel busy;
    

    public ModelAttachmentFileWidget(final RuleAsset asset ) {
        initWidgets(asset.uuid, asset.metaData.name);
    }
    
    private Widget newImportWidget(final String uuid) {

        final ImportWidget impWidget = new ImportWidget( HTMLFileManagerFields.UPLOAD_FIELD_NAME_ATTACH, uuid);

        impWidget.add( new Button( "Import", new ClickListener() {
           public void onClick(Widget sender) {
               doImportFile();
           }
        
           private void doImportFile() {
               if ( Window.confirm( "Are you sure you want to import? this will erase the previous file?" ) ) {  
                   LoadingPopup.showMessage( "Importing file to asset" );
                   impWidget.submit();
               }
           }
           
        } ) );

        impWidget.addFormHandler( new FormHandler() {
            public void onSubmitComplete(FormSubmitCompleteEvent event) {
                if (event.getResults().indexOf( "OK" ) > -1) {
                    Window.alert( "File imported successfully. Please refresh your browser (F5) to show the new content. ");
                } else {
                    ErrorPopup.showMessage( "Unable to import into the repository. Consult the server logs for error messages." );
                }                
                LoadingPopup.close();
            }

            public void onSubmit(FormSubmitEvent event) {

            }
        } );
        
        return impWidget;
    }
    
    
    protected void initWidgets(final String uuid, String formName) {
        FormStyleLayout layout = new FormStyleLayout("images/model_large.png", 
                                                     formName);
        layout.addAttribute( "Upload new version:", newImportWidget(uuid) );
        Button dl = new Button("Download");
        dl.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                Window.open( GWT.getModuleBaseURL() + "fileManager?" +  HTMLFileManagerFields.FORM_FIELD_UUID + "=" + uuid, 
                             "downloading...", "" );
            }            
        });
        layout.addAttribute( "Download current version:", dl );
        
        busy = new HorizontalPanel();
        busy.setVisible( false );
        busy.add( new Label("Uploading file...") );
        busy.add( new Image("images/spinner.gif") );
        
        layout.addRow( busy );
        initWidget( layout );
        
        this.setStyleName( "editable-Surface" );        
    }

    protected void showUploadingBusy() {
        this.ok.setVisible( false );
//        this.form.setVisible( false );
        this.busy.setVisible( true );
    }

    
}
