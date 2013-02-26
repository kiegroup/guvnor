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
import org.uberfire.client.common.FormStyleLayout;

import javax.annotation.PostConstruct;

public class ScoreCardXLSEditorViewImpl
        extends Composite
        implements ScoreCardXLSEditorView {

    private boolean isDirty;
    
    private VerticalPanel       layout;
    private Path fullPath;
    
    FormStyleLayout ts;
    
    @PostConstruct
    public void init() {
        layout = new VerticalPanel();
        layout.setWidth( "100%" );     
        
        ts = new FormStyleLayout(getIcon(), ScoreCardXLSEditorConstants.INSTANCE.ScoreCard());
        layout.add(ts);
        
        initWidget( layout );
        setWidth( "100%" );
    }
    
    public void setPath(Path path) {
        this.fullPath = path;
        //ts.clear();
        ts.addAttribute( ScoreCardXLSEditorConstants.INSTANCE.UploadNewVersion() + ":", new AttachmentFileWidget(fullPath, new Command() {
            @Override
            public void execute() {
            }
            
        }));       
        
        Button dl = new Button("Download");
        // dl.setEnabled( this.asset.getVersionNumber() > 0 );
        dl.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                Window.open(GWT.getModuleBaseURL() + "scorecardxls/file?"
                        + HTMLFileManagerFields.FORM_FIELD_PATH + "="
                        + fullPath.toURI(), "downloading",
                        "resizable=no,scrollbars=yes,status=no");
            }
        });
        ts.addAttribute(ScoreCardXLSEditorConstants.INSTANCE.DownloadCurrentVersion() + ":", dl);
    }

    @Override
    public boolean isDirty() {
        return isDirty;
    }

    @Override
    public void setNotDirty() {
        this.isDirty = false;
    }

    @Override
    public boolean confirmClose() {
        return Window.confirm( CommonConstants.INSTANCE.DiscardUnsavedData() );
    }

    @Override
    public void makeReadOnly() {
    }
    
    public Image getIcon() {
        Image image = new Image(ImageResources.INSTANCE.decisionTable());
        image.setAltText(ScoreCardXLSEditorConstants.INSTANCE.ScoreCard());
        return image;
    }
}
