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

package org.drools.guvnor.client.asseteditor;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;

import org.drools.guvnor.client.common.*;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.explorer.RefreshModuleEditorEvent;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.rpc.Asset;

/**
 * This wraps a file uploader utility for model packages.
 * Model packages are jar files.
 */

public abstract class AssetAttachmentFileWidget extends Composite
        implements
        EditorWidget {

    private Constants constants = GWT.create(Constants.class);

    private FormPanel form;
    private RuleViewer viewer;
    protected FormStyleLayout layout;
    protected Asset asset;
    private final EventBus eventBus;

    public AssetAttachmentFileWidget(final Asset asset,
                                     final RuleViewer viewer,
                                     ClientFactory clientFactory,
                                     EventBus eventBus) {
        this.viewer = viewer;
        this.eventBus = eventBus;
        this.asset = asset;
        initWidgets(asset.getUuid(),
                asset.getName());
        initAssetHandlers();
    }

    protected void initWidgets(final String uuid,
                               String formName) {
        form = new FormPanel();
        form.setAction(GWT.getModuleBaseURL() + "asset");
        form.setEncoding(FormPanel.ENCODING_MULTIPART);
        form.setMethod(FormPanel.METHOD_POST);

        FileUpload up = new FileUpload();
        up.setName(HTMLFileManagerFields.UPLOAD_FIELD_NAME_ATTACH);
        HorizontalPanel fields = new HorizontalPanel();
        fields.add(getHiddenField(HTMLFileManagerFields.FORM_FIELD_UUID,
                uuid));

        Button ok = new Button(constants.Upload());

        fields.add(up);
        fields.add(ok);

        form.add(fields);

        layout = new FormStyleLayout(getIcon(),
                formName);

        if (!this.asset.isReadonly()) layout.addAttribute(constants.UploadNewVersion(),
                form);

        Button dl = new Button(constants.Download());
        dl.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                Window.open(GWT.getModuleBaseURL() + "asset?" + HTMLFileManagerFields.FORM_FIELD_UUID + "=" + uuid,
                        "downloading",
                        "resizable=no,scrollbars=yes,status=no");
            }
        });
        layout.addAttribute(constants.DownloadCurrentVersion(),
                dl);

        ok.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                showUploadingBusy();
                submitUpload();
            }
        });

        initWidget(layout);
        layout.setWidth("100%");
        this.setStyleName(getOverallStyleName());
    }

    /**
     * @return The path to the icon to use.
     */
    public abstract ImageResource getIcon();

    /**
     * return the overall style name to use.
     */
    public abstract String getOverallStyleName();

    void initAssetHandlers() {
        form.addSubmitCompleteHandler(new SubmitCompleteHandler() {

            public void onSubmitComplete(SubmitCompleteEvent event) {
                LoadingPopup.close();

                if (asset.getFormat().equals(AssetFormats.MODEL)) {
                    eventBus.fireEvent(new RefreshModuleEditorEvent(asset.getUuid()));
                }

                if (event.getResults().indexOf("OK") > -1) {
                    viewer.showInfoMessage(constants.FileWasUploadedSuccessfully());
                    
                    //Reload asset as the upload operation commits the asset's content. If we don't 
                    //reload the asset we receive a optimistic lock error appearing as "Unable to save 
                    //this asset, as it has been recently updated" message to users
                    eventBus.fireEvent(new RefreshAssetEditorEvent(asset.getUuid()));
                } else {
                    ErrorPopup.showMessage(constants.UnableToUploadTheFile());
                }
            }

        });
    }

    protected void submitUpload() {
        form.submit();
    }

    protected void showUploadingBusy() {
        LoadingPopup.showMessage(constants.Uploading());
    }

    private TextBox getHiddenField(String name,
                                   String value) {
        TextBox t = new TextBox();
        t.setName(name);
        t.setText(value);
        t.setVisible(false);
        return t;
    }

    public void addDescription(Widget d) {
        this.layout.addRow(d);
    }

}
