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

package org.kie.guvnor.scorecardxls.client.editor;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextBox;
import org.kie.guvnor.scorecardxls.client.resources.i18n.ScoreCardXLSEditorConstants;
import org.kie.guvnor.scorecardxls.service.HTMLFileManagerFields;
import org.uberfire.backend.vfs.Path;

/**
 * This wraps a file uploader utility
 */
public class AttachmentFileWidget extends Composite {

    private final FormPanel form = new FormPanel();
    private final HorizontalPanel fields = new HorizontalPanel();

    private Command successCallback;

    public AttachmentFileWidget() {
        form.setAction( GWT.getModuleBaseURL() + "scorecardxls/file" );
        form.setEncoding( FormPanel.ENCODING_MULTIPART );
        form.setMethod( FormPanel.METHOD_POST );

        form.addSubmitCompleteHandler( new FormPanel.SubmitCompleteHandler() {

            @Override
            public void onSubmitComplete( final FormPanel.SubmitCompleteEvent event ) {
                if ( "OK".equalsIgnoreCase( event.getResults() ) ) {
                    Window.alert( ScoreCardXLSEditorConstants.INSTANCE.UploadSuccess() );
                    onSuccessCallback();
                } else {
                    Window.alert( ScoreCardXLSEditorConstants.INSTANCE.UploadFailure0( event.getResults() ) );
                }
            }

        } );

        final FileUpload up = new FileUpload();
        up.setName( HTMLFileManagerFields.UPLOAD_FIELD_NAME_ATTACH );

        fields.add( up );
        form.add( fields );

        initWidget( form );
    }

    private void onSuccessCallback() {
        if ( this.successCallback == null ) {
            return;
        }
        this.successCallback.execute();
    }

    public void submit( final Path context,
                        final String fileName,
                        final Command successCallback ) {
        this.successCallback = successCallback;
        fields.add( getHiddenField( HTMLFileManagerFields.FORM_FIELD_PATH,
                                    context.toURI() ) );
        fields.add( getHiddenField( HTMLFileManagerFields.FORM_FIELD_NAME,
                                    fileName ) );
        form.submit();
    }

    public void submit( final Path path,
                        final Command successCallback ) {
        this.successCallback = successCallback;
        fields.add( getHiddenField( HTMLFileManagerFields.FORM_FIELD_FULL_PATH,
                                    path.toURI() ) );
        form.submit();
    }

    private TextBox getHiddenField( final String name,
                                    final String value ) {
        final TextBox t = new TextBox();
        t.setName( name );
        t.setText( value );
        t.setVisible( false );
        return t;
    }

}
