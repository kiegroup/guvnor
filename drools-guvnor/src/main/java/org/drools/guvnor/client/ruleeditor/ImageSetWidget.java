package org.drools.guvnor.client.ruleeditor;
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

import org.drools.guvnor.client.common.*;
import org.drools.guvnor.client.rpc.RuleAsset;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.ui.impl.ClippedImagePrototype;
import com.google.gwt.user.client.Window;
import com.yesmail.gwt.rolodex.client.RolodexPanel;
import com.yesmail.gwt.rolodex.client.RolodexCardBundle;
import com.yesmail.gwt.rolodex.client.RolodexCard;

/**
 * ImageSetWidget - allows to use images as assets. Can upload images one by one and display them as a deck of cards (using rolodex).
 *
 * TODO: support in content handler to enable multiple images and carry out graphic routines
 * TODO: refactor initWidgets (as it was copypasted from AssetAttachmentFileWidget
 * TODO: The "Loading.." popup doesn't close
 * TODO: functional features ?
 *
 */
public class ImageSetWidget extends DirtyableComposite {

    private FormPanel form;
    private ImageButton ok;
    private RuleViewer viewer;
    private FormStyleLayout layout;
    private RuleAsset asset;

    public ImageSetWidget(RuleAsset asset, RuleViewer viewer) {
        this.asset = asset;
        this.viewer = viewer;
        initWidgets(asset.uuid, asset.metaData.name);
    }

    private void initWidgets(final String uuid, String formName) {
        form = new FormPanel();
        form.setAction(GWT.getModuleBaseURL() + "asset");
        form.setEncoding(FormPanel.ENCODING_MULTIPART);
        form.setMethod(FormPanel.METHOD_POST);

        FileUpload up = new FileUpload();
        up.setName(HTMLFileManagerFields.UPLOAD_FIELD_NAME_ATTACH);
        HorizontalPanel fields = new HorizontalPanel();
        fields.add(getHiddenField(HTMLFileManagerFields.FORM_FIELD_UUID, uuid));

        ok = new ImageButton("images/upload.gif", "Upload");

        fields.add(up);
        fields.add(new Label("upload:"));
        fields.add(ok);

        form.add(fields);

        layout = new FormStyleLayout("images/upload.gif", formName);

        createRolodexPanel();

        if (!this.asset.isreadonly)
            layout.addAttribute("Upload new version:", form);

        Button dl = new Button("Download");
        dl.addClickListener(new ClickListener() {
            public void onClick(Widget w) {
                Window.open(GWT.getModuleBaseURL() + "asset?" + HTMLFileManagerFields.FORM_FIELD_UUID + "=" + uuid,
                        "downloading", "resizable=no,scrollbars=yes,status=no");
            }
        });
        layout.addAttribute("Download current version:", dl);

        ok.addClickListener(new ClickListener() {
            public void onClick(Widget w) {
                showUploadingBusy();
                submitUpload();
            }
        });

        initWidget(layout);
        layout.setWidth("100%");
        this.setStyleName(getOverallStyleName());
    }

    private void createRolodexPanel() {
        RolodexCardBundle images = getImagesFromAsset();
        RolodexCard[] rolodexCards = images.getRolodexCards();
        if (rolodexCards.length > 0) {
            final RolodexPanel rolodex = new RolodexPanel(images, 3, rolodexCards[0], true);
            rolodex.setHeight("200px");
            layout.addRow(rolodex);
        }
    }

    protected void submitUpload() {
        form.submit();
    }

    protected void showUploadingBusy() {
        LoadingPopup.showMessage("Uploading...");
    }

    void initAssetHandlers() {
        form.addFormHandler(new FormHandler() {
            public void onSubmit(FormSubmitEvent ev) {
            }

            public void onSubmitComplete(FormSubmitCompleteEvent ev) {
                LoadingPopup.close();
                if (ev.getResults().indexOf("OK") > -1) {
                    Window.alert("File was uploaded successfully.");
                    viewer.refreshDataAndView();
                } else {
                    ErrorPopup.showMessage("Unable to upload the file.");
                }
            }
        });
    }

    private TextBox getHiddenField(String name, String value) {
        TextBox t = new TextBox();
        t.setName(name);
        t.setText(value);
        t.setVisible(false);
        return t;
    }

    //TODO: new icon
    public String getIcon() {
        return "images/decision_table.png";
    }

    //TODO: ?
    public String getOverallStyleName() {
        return "decision-Table-upload";
    }

    /**
     * TODO: create a bundle of images using asset.content
     */
    public RolodexCardBundle getImagesFromAsset() {
        return new RolodexCardBundle() {
            public int getMaxHeight() {
                return 80;
            }

            ClippedImagePrototype clip = new ClippedImagePrototype(
                    GWT.getModuleBaseURL() + "asset?" + HTMLFileManagerFields.FORM_FIELD_UUID + "=" + asset.uuid,
                    0, 0, 300, 200
            );

            RolodexCard card = new RolodexCard(clip, clip, clip, 300, 100, 10);

            public RolodexCard[] getRolodexCards() {
                return new RolodexCard[]{card};
            }
        };

    }
}
