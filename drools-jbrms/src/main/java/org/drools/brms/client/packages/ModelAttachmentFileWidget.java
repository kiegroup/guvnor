package org.drools.brms.client.packages;

import org.drools.brms.client.common.ErrorPopup;
import org.drools.brms.client.common.FormStyleLayout;
import org.drools.brms.client.rpc.RuleAsset;
import org.drools.brms.client.ruleeditor.RuleViewer;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
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
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * This wraps a file uploader utility for model packages.
 * Model packages are jar files. 
 * 
 * @author Michael Neale
 */
public class ModelAttachmentFileWidget extends Composite {



    public static final String FORM_FIELD_UUID = "uuid";
    private FormPanel form;
    private Button ok;
    private HorizontalPanel busy;

    public ModelAttachmentFileWidget(final RuleAsset asset, final RuleViewer viewer) {
        
        form = new FormPanel();
        form.setAction( GWT.getModuleBaseURL() + "fileManager"   );
        form.setEncoding( FormPanel.ENCODING_MULTIPART );
        form.setMethod( FormPanel.METHOD_POST );
        
        FileUpload up = new FileUpload();
        up.setName( "fileUploadElement" );        
        HorizontalPanel fields = new HorizontalPanel();
        fields.add( getHiddenField(FORM_FIELD_UUID, asset.uuid) );
  
        ok = new Button("Upload");
                
        fields.add( up );
        fields.add( ok );
        
        form.add( fields );
        
        FormStyleLayout layout = new FormStyleLayout("images/model_large.png", 
                                                     asset.metaData.name);

        
        layout.addAttribute( "Upload new version:", form );
        Button dl = new Button("Download");
        dl.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                Window.open( GWT.getModuleBaseURL() + "fileManager?" +  FORM_FIELD_UUID + "=" + asset.uuid, 
                             "downloading...", "" );
            }            
        });
        layout.addAttribute( "Download current version:", dl );
        
        busy = new HorizontalPanel();
        busy.setVisible( false );
        busy.add( new Label("Uploading file...") );
        busy.add( new Image("images/spinner.gif") );
        
        
        
        layout.addRow( busy );
        ok.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                showUploadingBusy();
                submitUpload();              
            }            
        });
        
        form.addFormHandler( new FormHandler() {

            public void onSubmit(FormSubmitEvent ev) {                
            }

            public void onSubmitComplete(FormSubmitCompleteEvent ev) {  
                    if (ev.getResults().indexOf( "OK" ) > -1) {                        
                        viewer.refreshDataAndView();
                    } else {
                        ErrorPopup.showMessage( "Unable to upload the file." );
                    }
            }
            
        });
        
        initWidget( layout );
        
        this.setStyleName( "editable-Surface" );
        
    }

    protected void submitUpload() {
        DeferredCommand.add( new Command() {
            public void execute() {
                form.submit();
            }            
        });
    }

    protected void showUploadingBusy() {
        this.ok.setVisible( false );
        this.form.setVisible( false );
        this.busy.setVisible( true );
    }

    private TextBox getHiddenField(String name, String value) {
        TextBox t = new TextBox();
        t.setName( name );
        t.setText( value );
        t.setVisible( false );
        return t;
    }
    
}
