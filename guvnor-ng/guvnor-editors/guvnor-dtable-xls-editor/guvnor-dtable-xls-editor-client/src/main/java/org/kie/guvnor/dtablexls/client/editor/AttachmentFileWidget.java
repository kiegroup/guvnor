/*
 * Copyright 2010 JBoss Inc
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

package org.kie.guvnor.dtablexls.client.editor;

import org.kie.guvnor.dtablexls.service.HTMLFileManagerFields;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.common.LoadingPopup;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;


/**
 * This wraps a file uploader utility
 */

public class AttachmentFileWidget extends Composite {

    private FormPanel                form;
    private Path contextPath;
    private String fileName;
    private Command createdCallback;
    private Path fullPath = null;
   
    public AttachmentFileWidget(Path contextPath, String fileName, Command createdCallback) {
        this.contextPath = contextPath;
        this.fileName = fileName;
        this.createdCallback = createdCallback;
        
        initWidgets();
        initSubmitCompleteHandler();
    }
    
    public AttachmentFileWidget(Path fullPath, Command createdCallback) {
        this.fullPath = fullPath;
        this.createdCallback = createdCallback;
        
        initWidgets();
        initSubmitCompleteHandler();
    }

    protected void initWidgets() {
        form = new FormPanel();
        form.setAction( GWT.getModuleBaseURL() + "dtablexls/file" );
        form.setEncoding( FormPanel.ENCODING_MULTIPART );
        form.setMethod( FormPanel.METHOD_POST );

        FileUpload up = new FileUpload();
        up.setName( HTMLFileManagerFields.UPLOAD_FIELD_NAME_ATTACH );
        HorizontalPanel fields = new HorizontalPanel();
        if(fullPath == null) {
        fields.add( getHiddenField( HTMLFileManagerFields.FORM_FIELD_PATH,
                                    contextPath.toURI() ) );
        fields.add( getHiddenField( HTMLFileManagerFields.FORM_FIELD_NAME,
                                    fileName ) );
        } else {
            fields.add( getHiddenField( HTMLFileManagerFields.FORM_FIELD_FULL_PATH,
                                        fullPath.toURI() ) );           
        }
        Button ok = new Button( "upload");
        ok.addClickHandler( new ClickHandler() {
            public void onClick(ClickEvent event) {
                showUploadingBusy();
                submitUpload();
            }
        } );
        
        fields.add( up );
        fields.add( ok );
        

        form.add( fields );       
        
        initWidget( form );
    }

    void initSubmitCompleteHandler() {
        form.addSubmitCompleteHandler( new SubmitCompleteHandler() {

            public void onSubmitComplete(SubmitCompleteEvent event) {
                LoadingPopup.close();

                if("OK".equalsIgnoreCase(event.getResults())) {
                    Window.alert("Uploaded successfully");
                    
                    createdCallback.execute();                           
                } else {
                    Window.alert("Upload failed:" + event.getResults()); 
                }
            }

        } );
    }

    protected void submitUpload() {
        form.submit();
    }

    protected void showUploadingBusy() {
        LoadingPopup.showMessage( "Uploading..." );
    }

    private TextBox getHiddenField(String name,
                                   String value) {
        TextBox t = new TextBox();
        t.setName( name );
        t.setText( value );
        t.setVisible( false );
        return t;
    }
/*
    public void addSupplementaryWidget(Widget d) {
        this.layout.addRow( d );
    }*/
    
    public void hide() {
        this.hide();
    }

}
