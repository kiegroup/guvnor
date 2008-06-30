package org.drools.guvnor.client.packages;
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


import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.guvnor.client.ruleeditor.Editor;
import org.drools.guvnor.client.ruleeditor.RuleViewer;

/**
 * This wraps a file uploader utility for model packages.
 * Model packages are jar files. 
 * 
 * @author Michael Neale
 * @author Fernando Meyer
 */

public class ModelAttachmentFileWidget extends AssetAttachmentFileWidget implements Editor {

    
    public ModelAttachmentFileWidget(RuleAsset asset, RuleViewer viewer) {
        super( asset, viewer );
    }


    public String getIcon() {
        return "images/model_large.png";
    }
    
    public String getOverallStyleName() {
        return "editable-Surface";
    }

    public String getWrapperClass() {
        return null;
    }

    public String getAssetFormat() {
        return null;
    }
}

/*Composite {


    private FormPanel form;
    private Button ok;
    private HorizontalPanel busy;
    private RuleViewer viewer;
    

    public ModelAttachmentFileWidget(final RuleAsset asset, final RuleViewer viewer) {
        this.viewer = viewer;
        initWidgets(asset.uuid, asset.metaData.name);
        initAssetHandlers();
    }
    
    protected void initWidgets(final String uuid, String formName) {
        form = new FormPanel();
        form.setAction( GWT.getModuleBaseURL() + "asset" );
        form.setEncoding( FormPanel.ENCODING_MULTIPART );
        form.setMethod( FormPanel.METHOD_POST );
        
        FileUpload up = new FileUpload();
        up.setName( HTMLFileManagerFields.UPLOAD_FIELD_NAME_ATTACH );        
        HorizontalPanel fields = new HorizontalPanel();
        fields.add( getHiddenField(HTMLFileManagerFields.FORM_FIELD_UUID, uuid) );
  
        ok = new Button("Upload");
                
        fields.add( up );
        fields.add( ok );
        
        form.add( fields );
        
        FormStyleLayout layout = new FormStyleLayout("images/model_large.png", 
                                                     formName);

        
        layout.addAttribute( "Upload new version:", form );
        Button dl = new Button("Download");
        dl.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                Window.open( GWT.getModuleBaseURL() + "asset?" +  HTMLFileManagerFields.FORM_FIELD_UUID + "=" + uuid, 
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
                
        initWidget( layout );
        
        this.setStyleName( "editable-Surface" );        
    }
    
    void initAssetHandlers( ) {
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

*/