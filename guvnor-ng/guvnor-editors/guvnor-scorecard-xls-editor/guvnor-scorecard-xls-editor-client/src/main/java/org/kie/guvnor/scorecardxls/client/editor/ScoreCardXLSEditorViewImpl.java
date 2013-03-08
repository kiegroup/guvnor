/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.guvnor.scorecardxls.client.editor;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;
import org.kie.guvnor.commons.ui.client.resources.i18n.CommonConstants;
import org.kie.guvnor.scorecardxls.client.resources.i18n.ScoreCardXLSEditorConstants;
import org.kie.guvnor.scorecardxls.client.resources.images.ImageResources;
import org.kie.guvnor.scorecardxls.service.HTMLFileManagerFields;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.common.BusyPopup;
import org.uberfire.client.common.FormStyleLayout;
import org.uberfire.client.workbench.widgets.events.NotificationEvent;

public class ScoreCardXLSEditorViewImpl
        extends Composite
        implements ScoreCardXLSEditorView {

    private final Button uploadButton = new Button( ScoreCardXLSEditorConstants.INSTANCE.Upload() );
    private final Button downloadButton = new Button( ScoreCardXLSEditorConstants.INSTANCE.Download() );

    private final VerticalPanel layout = new VerticalPanel();
    private final FormStyleLayout ts = new FormStyleLayout( getIcon(),
                                                            ScoreCardXLSEditorConstants.INSTANCE.ScoreCard() );

    @Inject
    private AttachmentFileWidget uploadWidget;

    @Inject
    private Event<NotificationEvent> notificationEvent;

    @PostConstruct
    public void init() {
        layout.setWidth( "100%" );
        layout.add( ts );
        initWidget( layout );
        setWidth( "100%" );
    }

    public void setPath( final Path path ) {
        ts.addAttribute( ScoreCardXLSEditorConstants.INSTANCE.UploadNewVersion() + ":",
                         uploadWidget );
        uploadButton.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( final ClickEvent event ) {
                BusyPopup.showMessage( ScoreCardXLSEditorConstants.INSTANCE.Uploading() );
                uploadWidget.submit( path,
                                     new Command() {

                                         @Override
                                         public void execute() {
                                             BusyPopup.close();
                                             notifySuccess();
                                         }

                                     } );
            }
        } );
        ts.addRow( uploadButton );

        downloadButton.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( final ClickEvent event ) {
                Window.open( GWT.getModuleBaseURL() + "scorecardxls/file?"
                                     + HTMLFileManagerFields.FORM_FIELD_PATH + "="
                                     + path.toURI(),
                             "downloading",
                             "resizable=no,scrollbars=yes,status=no" );
            }
        } );
        ts.addAttribute( ScoreCardXLSEditorConstants.INSTANCE.DownloadCurrentVersion() + ":",
                         downloadButton );
    }

    @Override
    public void setReadOnly( final boolean isReadOnly ) {
        uploadButton.setEnabled( !isReadOnly );
    }

    private Image getIcon() {
        Image image = new Image( ImageResources.INSTANCE.scoreCard() );
        image.setAltText( ScoreCardXLSEditorConstants.INSTANCE.ScoreCard() );
        return image;
    }

    private void notifySuccess() {
        notificationEvent.fire( new NotificationEvent( CommonConstants.INSTANCE.ItemCreatedSuccessfully() ) );
    }

}
