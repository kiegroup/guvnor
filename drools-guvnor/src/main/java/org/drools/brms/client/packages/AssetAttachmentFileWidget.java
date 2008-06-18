package org.drools.brms.client.packages;
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

import org.drools.brms.client.common.ErrorPopup;
import org.drools.brms.client.common.FormStyleLayout;
import org.drools.brms.client.common.HTMLFileManagerFields;
import org.drools.brms.client.common.ImageButton;
import org.drools.brms.client.common.LoadingPopup;
import org.drools.brms.client.rpc.RuleAsset;
import org.drools.brms.client.ruleeditor.RuleViewer;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormHandler;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormSubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormSubmitEvent;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * This wraps a file uploader utility for model packages.
 * Model packages are jar files.
 *
 * @author Michael Neale
 * @author Fernando Meyer
 */

public abstract class AssetAttachmentFileWidget extends Composite {


    private FormPanel form;
    private ImageButton ok;
    private RuleViewer viewer;
    private FormStyleLayout layout;
    private RuleAsset asset;


    public AssetAttachmentFileWidget(final RuleAsset asset, final RuleViewer viewer) {
        this.viewer = viewer;
        this.asset = asset;
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

        ok = new ImageButton("images/upload.gif", "Upload");

        fields.add( up );
        fields.add(new Label("upload:"));
        fields.add( ok );

        form.add( fields );

        layout = new FormStyleLayout(getIcon(),
                                                     formName);

        if ( !this.asset.isreadonly )
            layout.addAttribute( "Upload new version:", form );

        Button dl = new Button("Download");
        dl.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                Window.open( GWT.getModuleBaseURL() + "asset?" +  HTMLFileManagerFields.FORM_FIELD_UUID + "=" + uuid,
                             "downloading", "resizable=no,scrollbars=yes,status=no" );
            }
        });
        layout.addAttribute( "Download current version:", dl );

        ok.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                showUploadingBusy();
                submitUpload();
            }
        });

        initWidget( layout );
        layout.setWidth( "100%" );
        this.setStyleName( getOverallStyleName() );
    }

    /**
     * @return The path to the icon to use.
     */
    public abstract String getIcon();

    /**
     * return the overall style name to use.
     */
    public abstract String getOverallStyleName();

    void initAssetHandlers( ) {
        form.addFormHandler( new FormHandler() {

            public void onSubmit(FormSubmitEvent ev) {
            }

            public void onSubmitComplete(FormSubmitCompleteEvent ev) {
            		LoadingPopup.close();
                    if (ev.getResults().indexOf( "OK" ) > -1) {
                    	Window.alert("File was uploaded successfully.");
                        viewer.refreshDataAndView();
                    } else {
                        ErrorPopup.showMessage( "Unable to upload the file." );
                    }
            }

        });
    }

    protected void submitUpload() {
                form.submit();

    }

    protected void showUploadingBusy() {
    	LoadingPopup.showMessage("Uploading...");
    }

    private TextBox getHiddenField(String name, String value) {
        TextBox t = new TextBox();
        t.setName( name );
        t.setText( value );
        t.setVisible( false );
        return t;
    }

    public void addDescription(Widget d) {
        this.layout.addRow( d );

    }

}