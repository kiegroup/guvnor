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

package org.kie.guvnor.dtablexls.client.editor;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import org.kie.guvnor.commons.ui.client.resources.i18n.CommonConstants;
import org.kie.guvnor.dtablexls.client.resources.i18n.DecisionTableXLSEditorConstants;
import org.kie.guvnor.dtablexls.client.resources.images.ImageResources;
import org.kie.guvnor.dtablexls.service.HTMLFileManagerFields;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.common.FormStyleLayout;
import org.uberfire.client.common.LoadingPopup;

import javax.annotation.PostConstruct;

public class DecisionTableXLSEditorViewImpl
        extends Composite
        implements DecisionTableXLSEditorView {

    private boolean isDirty;
    
    private VerticalPanel       layout;
    private FormPanel           form;
    private Path contextPath;
    
    final SimplePanel resultsP = new SimplePanel();
    FormStyleLayout ts = new FormStyleLayout(getIcon(), DecisionTableXLSEditorConstants.INSTANCE.DecisionTable());
    
    @PostConstruct
    public void init() {
        layout = new VerticalPanel();
        layout.setWidth( "100%" );       
        layout.add(ts);
        
        initWidget( layout );
        setWidth( "100%" );
    }
    
    public void setPath(Path path) {
        this.contextPath = path;
        //ts.clear();
        ts.addAttribute( "", new AttachmentFileWidget(contextPath, null, new Command() {
            @Override
            public void execute() {
            }
            
        }));         
    }
        
    public FormPanel doUploadForm() {
        form = new FormPanel();
        form.setAction(GWT.getModuleBaseURL() + "file");
        form.setEncoding(FormPanel.ENCODING_MULTIPART);
        form.setMethod(FormPanel.METHOD_POST);

        FileUpload up = new FileUpload();
        //up.setWidth("100px");
        up.setName(HTMLFileManagerFields.UPLOAD_FIELD_NAME_ATTACH);
         
       
        Button ok = new Button("upload");
        ok.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                showUploadingBusy();
                submitUpload();
            }
        });

       
        //form.add(fields);
        
        form.addSubmitCompleteHandler(new SubmitCompleteHandler() {
            public void onSubmitComplete(SubmitCompleteEvent event) {
                if("OK".equalsIgnoreCase(event.getResults())) {
                    LoadingPopup.close();
                    Window.alert("Uploaded successfully");
                    
                    resultsP.clear();             
                    //JarListEditor table = new JarListEditor(m2RepoService);
                    //resultsP.add( table );                  
                        
                } else {
                    LoadingPopup.close();
                    Window.alert("Upload failed:" + event.getResults()); 
                }

            }
        });
        
        HorizontalPanel fields = new HorizontalPanel();
        fields.add(up);
        fields.add(ok);
        
        VerticalPanel allFields = new VerticalPanel();
        allFields.add(fields);
        //allFields.add(hiddenFieldsPanel);
        
        form.add(allFields);

        return form;
    }

    protected void submitUpload() {
        form.submit();
    }

    protected void showUploadingBusy() {
        // LoadingPopup.showMessage( "Uploading...");
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
        image.setAltText(DecisionTableXLSEditorConstants.INSTANCE.DecisionTable());
        return image;
    }
}
