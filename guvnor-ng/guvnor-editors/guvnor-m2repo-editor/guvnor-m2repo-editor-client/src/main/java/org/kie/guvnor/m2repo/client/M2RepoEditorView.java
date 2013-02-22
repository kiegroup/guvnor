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
package org.kie.guvnor.m2repo.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import org.jboss.errai.ioc.client.api.Caller;
import org.kie.guvnor.m2repo.client.editor.JarListEditor;
import org.kie.guvnor.m2repo.model.HTMLFileManagerFields;
import org.kie.guvnor.m2repo.service.M2RepoService;
import org.uberfire.client.common.FormStyleLayout;
import org.uberfire.client.common.LoadingPopup;

import javax.inject.Inject;


public class M2RepoEditorView
        extends Composite
        implements M2RepoEditorPresenter.View {

    private VerticalPanel       layout;
    private FormPanel           form;
    //private FormStyleLayout       emptyGAVPanel = new FormStyleLayout();
    private TextBox hiddenGroupIdField = new TextBox();
    private TextBox hiddenArtifactIdField = new TextBox();
    private TextBox hiddenVersionIdField = new TextBox();
    
    FormStyleLayout hiddenFieldsPanel = new FormStyleLayout();
    final SimplePanel resultsP = new SimplePanel();
    
/*    @Inject*/
    private Caller<M2RepoService> m2RepoService;
    
    @Inject
    public M2RepoEditorView(Caller<M2RepoService> s) {
        this.m2RepoService = s;

        layout = new VerticalPanel();
        doSearch();
        layout.setWidth( "100%" );
        initWidget( layout );
        setWidth( "100%" );
    }
    
    private void doSearch() {
        VerticalPanel container = new VerticalPanel();
        VerticalPanel criteria = new VerticalPanel();

        FormStyleLayout ts = new FormStyleLayout();
        
        ts.addAttribute( "Upload new Jar:", doUploadForm() );       
        
        final TextBox searchTextBox = new TextBox();
        //tx.setWidth("100px");
        ts.addAttribute( "Find items with a name matching:", searchTextBox );

        Button go = new Button();
        go.setText( "Search" );
        ts.addAttribute( "",
                         go );

        
        ts.setWidth( "100%" );

        final ClickHandler cl = new ClickHandler() {

            public void onClick(ClickEvent arg0) {
                resultsP.clear();             
                if ( searchTextBox.getText() ==null || searchTextBox.getText().equals( "" ) ) {
                    JarListEditor table = new JarListEditor(m2RepoService);
                    resultsP.add( table );                    
                } else {
                    JarListEditor table = new JarListEditor(m2RepoService, searchTextBox.getText());
                    resultsP.add( table );
                }
            }

        };

        go.addClickHandler( cl );
        searchTextBox.addKeyPressHandler( new KeyPressHandler() {
            public void onKeyPress(KeyPressEvent event) {
                if ( event.getCharCode() == KeyCodes.KEY_ENTER ) {
                    cl.onClick( null );
                }
            }
        } );

        criteria.add( ts );
        container.add( criteria );
        container.add( resultsP );
        
        resultsP.clear();
        JarListEditor table = new JarListEditor(m2RepoService);
        resultsP.add( table );
        
        layout.add(container);
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
                    hiddenFieldsPanel.setVisible(false);
                    hiddenArtifactIdField.setText(null);
                    hiddenGroupIdField.setText(null);
                    hiddenVersionIdField.setText(null);   
                    
                    resultsP.clear();             
                    JarListEditor table = new JarListEditor(m2RepoService);
                    resultsP.add( table );                  
                        
                } else if("NO VALID POM".equalsIgnoreCase(event.getResults())) {
                    LoadingPopup.close();
                    Window.alert("The Jar does not contain a valid POM file. Please specify GAV info manually.");
                    hiddenFieldsPanel.setVisible(true);
                } else {
                    LoadingPopup.close();
                    Window.alert("Upload failed:" + event.getResults()); 
                    
                    hiddenFieldsPanel.setVisible(false);
                    hiddenArtifactIdField.setText(null);
                    hiddenGroupIdField.setText(null);
                    hiddenVersionIdField.setText(null);  
                }

            }
        });
        
        HorizontalPanel fields = new HorizontalPanel();
        fields.add(up);
        fields.add(ok);

        hiddenGroupIdField.setName(HTMLFileManagerFields.GROUP_ID);
        hiddenGroupIdField.setText(null);
        //hiddenGroupIdField.setVisible(false);

        hiddenArtifactIdField.setName(HTMLFileManagerFields.ARTIFACT_ID);
        hiddenArtifactIdField.setText(null);
        //hiddenArtifactIdField.setVisible(false);

        hiddenVersionIdField.setName(HTMLFileManagerFields.VERSION_ID);
        hiddenVersionIdField.setText(null);
        //hiddenVersionIdField.setVisible(false);
        
        hiddenFieldsPanel.setVisible(false);
        hiddenFieldsPanel.addAttribute("GroupID:", hiddenGroupIdField);
        hiddenFieldsPanel.addAttribute("ArtifactID:", hiddenArtifactIdField);
        hiddenFieldsPanel.addAttribute("VersionID:", hiddenVersionIdField);
        
        VerticalPanel allFields = new VerticalPanel();
        allFields.add(fields);
        allFields.add(hiddenFieldsPanel);
        
        form.add(allFields);

        return form;
    }

    protected void submitUpload() {
        form.submit();
    }

    protected void showUploadingBusy() {
        // LoadingPopup.showMessage( "Uploading...");
    }
}
