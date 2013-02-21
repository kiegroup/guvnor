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

package org.kie.guvnor.m2repo.client.editor;

import org.kie.guvnor.m2repo.model.HTMLFileManagerFields;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;


/**
 * This wraps a file uploader utility
 */

public class AttachmentFileWidget extends Composite {

    private FormPanel                form;

    public AttachmentFileWidget() {
        initWidgets();
        initSubmitCompleteHandler();
    }

    protected void initWidgets() {
        form = new FormPanel();
        form.setAction( GWT.getModuleBaseURL() + "file" );
        form.setEncoding( FormPanel.ENCODING_MULTIPART );
        form.setMethod( FormPanel.METHOD_POST );

        FileUpload up = new FileUpload();
        up.setName( HTMLFileManagerFields.UPLOAD_FIELD_NAME_ATTACH );
        HorizontalPanel fields = new HorizontalPanel();
        fields.add( getHiddenField( HTMLFileManagerFields.FORM_FIELD_PATH,
                                    "uuid" ) );

        Button ok = new Button( "upload");
        ok.addClickHandler( new ClickHandler() {
            public void onClick(ClickEvent event) {
                showUploadingBusy();
                submitUpload();
            }
        } );
        
        fields.add( up );
        fields.add( ok );
        
/*        Button dl = new Button( "Download" );
        //dl.setEnabled( this.asset.getVersionNumber() > 0 );
        dl.addClickHandler( new ClickHandler() {
            public void onClick(ClickEvent event) {
                Window.open( GWT.getModuleBaseURL() + "file?" + HTMLFileManagerFields.FORM_FIELD_UUID + "=" + "uuid",
                             "downloading",
                             "resizable=no,scrollbars=yes,status=no" );
            }
        } );
*/
        form.add( fields );
        
        initWidget( form );
    }

    void initSubmitCompleteHandler() {
        form.addSubmitCompleteHandler( new SubmitCompleteHandler() {

            public void onSubmitComplete(SubmitCompleteEvent event) {
                //LoadingPopup.close();
/*
                if ( asset.getFormat().equals( AssetFormats.MODEL ) ) {
                    eventBus.fireEvent( new RefreshModuleEditorEvent( asset.getUuid() ) );
                }

                if ( event.getResults().indexOf( "OK" ) > -1 ) {
                    //Raise an Event to show an information message
                    eventBus.fireEvent( new ShowMessageEvent( ConstantsCore.INSTANCE.FileWasUploadedSuccessfully(),
                                                              MessageType.INFO ) );

                    //Reload asset as the upload operation commits the asset's content. If we don't 
                    //reload the asset we receive a optimistic lock error appearing as "Unable to save 
                    //this asset, as it has been recently updated" message to users
                    eventBus.fireEvent( new RefreshAssetEditorEvent(asset.getMetaData().getModuleName(),  asset.getUuid() ) );
                } else {
                    ErrorPopup.showMessage( ConstantsCore.INSTANCE.UnableToUploadTheFile() );
                }*/
            }

        } );
    }

    protected void submitUpload() {
        form.submit();
    }

    protected void showUploadingBusy() {
        //LoadingPopup.showMessage( "Uploading..." );
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

}
