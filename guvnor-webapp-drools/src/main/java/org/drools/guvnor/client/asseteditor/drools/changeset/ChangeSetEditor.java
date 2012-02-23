/*
 * Copyright 2011 JBoss by Red Hat.
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

/*Every That is commented in relate to de attribute data is because a NEP*/
package org.drools.guvnor.client.asseteditor.drools.changeset;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;
import org.drools.guvnor.client.asseteditor.EditorWidget;
import org.drools.guvnor.client.asseteditor.RuleViewer;
import org.drools.guvnor.client.asseteditor.SaveEventListener;
import org.drools.guvnor.client.common.DirtyableComposite;
import org.drools.guvnor.client.common.ErrorPopup;
import org.drools.guvnor.client.common.FormStylePopup;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.rpc.Asset;
import org.drools.guvnor.client.rpc.RuleContentText;
import org.drools.guvnor.client.widgets.RESTUtil;
import org.drools.guvnor.client.widgets.drools.explorer.AssetResourceExplorerWidget;
import org.drools.guvnor.client.widgets.drools.explorer.PackageReadyCommand;
import org.drools.guvnor.client.widgets.drools.explorer.PackageResourceExplorerWidget;
import org.drools.guvnor.client.widgets.drools.explorer.ResourceElementReadyCommand;

import static org.drools.guvnor.client.common.AssetFormats.*;
import static org.drools.guvnor.client.widgets.drools.explorer.AssetDownloadLinkUtil.*;
import static org.drools.guvnor.client.widgets.drools.explorer.ExplorerRenderMode.*;

/**
 * This is the default Change Set editor widget - more to come later.
 */
public class ChangeSetEditor extends DirtyableComposite
        implements
        EditorWidget,
        SaveEventListener {

    // UI
    interface ChangeSetEditorBinder
            extends
            UiBinder<Widget, ChangeSetEditor> {

    }

    private static final String resourceXMLElementTemplate = "<resource {name} {description} source='{source}' type='{type}' />";

    private static ChangeSetEditorBinder uiBinder = GWT.create(ChangeSetEditorBinder.class);
    @UiField
    protected TextArea editorArea;
    @UiField
    protected Button btnAssetResource;
    @UiField
    protected Button btnPackageResource;
    @UiField
    protected HorizontalPanel pnlURL;

    private ClientFactory clientFactory;
    final private RuleContentText data;
    final private String assetPackageName;
    final private String assetPackageUUID;
    final private String assetName;
    private final int visibleLines;

    public ChangeSetEditor(Asset a,
            RuleViewer v,
            ClientFactory clientFactory,
            EventBus eventBus) {
        this(a,
                clientFactory);
    }

    public ChangeSetEditor(Asset a,
            ClientFactory clientFactory) {
        this(a,
                clientFactory,
                -1);
    }

    public ChangeSetEditor(Asset asset,
            ClientFactory clientFactory,
            int visibleLines) {

        this.initWidget(uiBinder.createAndBindUi(this));

        this.clientFactory = clientFactory;

        assetPackageUUID = asset.getMetaData().getModuleUUID();
        assetPackageName = asset.getMetaData().getModuleName();
        assetName = asset.getName();

        data = (RuleContentText) asset.getContent();

        if (data.content == null) {
            data.content = "Empty!";
        }

        this.visibleLines = visibleLines;

        this.customizeUIElements();
    }

    private void customizeUIElements() {

        pnlURL.add(this.createChangeSetLink());

        editorArea.setStyleName("default-text-Area"); //NON-NLS
        editorArea.setVisibleLines((visibleLines == -1) ? 25 : visibleLines);
        editorArea.setText(data.content);
        editorArea.getElement().setAttribute("spellcheck",
                "false"); //NON-NLS

        editorArea.addChangeHandler(new ChangeHandler() {

            public void onChange(ChangeEvent event) {
                data.content = editorArea.getText();
                makeDirty();
            }
        });

        editorArea.addKeyDownHandler(new KeyDownHandler() {

            public void onKeyDown(KeyDownEvent event) {
                if (event.getNativeKeyCode() == KeyCodes.KEY_TAB) {
                    event.preventDefault();
                    event.stopPropagation();
                    int pos = editorArea.getCursorPos();
                    insertText("\t",
                            false);
                    editorArea.setCursorPos(pos + 1);
                    editorArea.cancelKey();
                    editorArea.setFocus(true);
                }
            }
        });

    }

    void insertText(String ins,
            boolean isSpecialPaste) {

        editorArea.setFocus(true);

        int i = editorArea.getCursorPos();
        String left = editorArea.getText().substring(0,
                i);
        String right = editorArea.getText().substring(i,
                editorArea.getText().length());
        int cursorPosition = left.toCharArray().length;
        if (isSpecialPaste) {
            int p = ins.indexOf("|");
            if (p > -1) {
                cursorPosition += p;
                ins = ins.replaceAll("\\|",
                        "");
            }

        }

        editorArea.setText(left + ins + right);
        this.data.content = editorArea.getText();

        editorArea.setCursorPos(cursorPosition);
    }

    public void onSave() {
        //data.content = text.getText();
        //asset.content = data;
    }

    public void onAfterSave() {
    }

    @UiHandler("btnPackageResource")
    public void addNewPackageResource(ClickEvent e) {
        addNewResourcePackage(new PackageResourceExplorerWidget(assetPackageUUID,
                assetPackageName,
                clientFactory, DISPLAY_NAME_AND_DESCRIPTION));
    }

    private void addNewResourcePackage(final PackageResourceExplorerWidget widget) {
        final NewResourcePopup popup = new NewResourcePopup(widget.asWidget());

        popup.addOkButtonClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                try {
                    widget.processSelectedPackage(new PackageReadyCommand() {

                        public void onSuccess(String moduleName, String label, String link, String name, String description) {
                            String resourceXMLElementTemplate = "<resource {name} {description} source='{source}' type='{type}' />";
                            String result = resourceXMLElementTemplate;

                            String nameString = "";
                            if (name.length() != 0) {
                                nameString = "name=\"" + name.trim() + "\"";
                            }
                            result = result.replace("{name}",
                                    nameString);

                            String descriptionString = "";
                            if (description.length() != 0) {
                                descriptionString = "description=\"" + description.trim() + "\"";
                            }

                            result = result.replace("{description}", descriptionString);
                            result = result.replace("{type}", "PKG");
                            result = result.replace("{source}", link);

                            insertText(result.toString(), false);
                        }

                        public void onFailure(Throwable cause) {
                            ErrorPopup.showMessage(cause.getMessage());
                        }
                    });

                } catch (Exception e) {
                    ErrorPopup.showMessage(e.getMessage());
                }
                popup.hide();
            }
        });
        popup.show();
    }

    @UiHandler("btnAssetResource")
    public void addNewAssetResource(ClickEvent e) {
        addNewResource(new AssetResourceExplorerWidget(assetPackageUUID,
                assetPackageName,
                clientFactory,
                CHANGE_SET_RESOURCE,
                DISPLAY_NAME_AND_DESCRIPTION));
    }

    private void addNewResource(final AssetResourceExplorerWidget widget) {

        final NewResourcePopup popup = new NewResourcePopup(widget.asWidget());

        popup.addOkButtonClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                try {
                    widget.processSelectedResources(new ResourceElementReadyCommand() {

                        public void onSuccess(final String packageRef, final Asset[] assets, final String name, final String description) {
                            //for each selcted resource we are going to add a xml entry
                            final StringBuilder result = new StringBuilder("");

                            for (int i = 0; i < assets.length; i++) {
                                final Asset asset = assets[i];

                                String partialResult = resourceXMLElementTemplate;

                                String nameString = "";
                                if (name.length() != 0) {
                                    if (assets.length == 1) {
                                        nameString = "name=\"" + name.trim() + "\"";
                                    } else {
                                        //add index to the name to avoid duplication
                                        nameString = "name=\"" + name.trim() + i + "\"";
                                    }
                                }
                                partialResult = partialResult.replace("{name}",
                                        nameString);

                                String descriptionString = "";
                                if (description.length() != 0) {
                                    descriptionString = "description=\"" + description.trim() + "\"";
                                }
                                partialResult = partialResult.replace("{description}",
                                        descriptionString);

                                final String type = convertAssetFormatToResourceType(asset.getFormat());
                                if (type == null) {
                                    throw new IllegalArgumentException(Constants.INSTANCE.UnknownResourceFormat(asset.getFormat()));
                                }

                                partialResult = partialResult.replace("{type}", type);

                                partialResult = partialResult.replace("{source}", buildDownloadLink(asset, packageRef));

                                result.append(partialResult).append('\n');
                            }
                            insertText(result.toString(), false);
                        }

                        public void onFailure(Throwable cause) {
                            ErrorPopup.showMessage(cause.getMessage());
                        }
                    });

                } catch (Exception e) {
                    ErrorPopup.showMessage(e.getMessage());
                }
                popup.hide();
            }
        });
        popup.show();
    }

    private Widget createChangeSetLink() {
        String url = RESTUtil.getRESTBaseURL();
        url += "packages/";
        url += this.assetPackageName;
        url += "/assets/";
        url += this.assetName;
        url += "/source";

        return new HTML(Constants.INSTANCE.Url() + ":&nbsp;<a href='" + url + "' target='_blank'>" + url + "</a>");
    }
}

class NewResourcePopup extends FormStylePopup {

    private Constants constants = GWT.create(Constants.class);

    public Button ok = new Button(constants.OK());
    public Button cancel = new Button(constants.Cancel());

    public NewResourcePopup(Widget content) {
        setTitle(constants.NewResource());

        HorizontalPanel hor = new HorizontalPanel();
        hor.add(ok);
        hor.add(cancel);

        addRow(content);
        addRow(hor);

        cancel.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                hide();
            }
        });

    }

    public void addOkButtonClickHandler(ClickHandler okClickHandler) {
        ok.addClickHandler(okClickHandler);
    }

}