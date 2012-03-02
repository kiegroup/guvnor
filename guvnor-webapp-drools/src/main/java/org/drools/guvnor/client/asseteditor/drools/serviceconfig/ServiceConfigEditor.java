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

/*Every That is commented in relate to de attribute data is because a NEP*/
package org.drools.guvnor.client.asseteditor.drools.serviceconfig;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;
import org.drools.guvnor.client.asseteditor.EditorWidget;
import org.drools.guvnor.client.asseteditor.RuleViewer;
import org.drools.guvnor.client.asseteditor.SaveEventListener;
import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.common.DirtyableComposite;
import org.drools.guvnor.client.common.ErrorPopup;
import org.drools.guvnor.client.common.FormStylePopup;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.resources.Images;
import org.drools.guvnor.client.rpc.ArtifactDependenciesService;
import org.drools.guvnor.client.rpc.ArtifactDependenciesServiceAsync;
import org.drools.guvnor.client.rpc.Asset;
import org.drools.guvnor.client.rpc.MavenArtifact;
import org.drools.guvnor.client.rpc.RuleContentText;
import org.drools.guvnor.client.widgets.drools.explorer.ArtifactDependenciesExplorerWidget;
import org.drools.guvnor.client.widgets.drools.explorer.ArtifactDependenciesReadyCommand;
import org.drools.guvnor.client.widgets.drools.explorer.AssetResourceExplorerWidget;
import org.drools.guvnor.client.widgets.drools.explorer.ResourceElementReadyCommand;
import org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52;

import static com.google.gwt.safehtml.shared.SafeHtmlUtils.*;
import static com.google.gwt.user.client.ui.AbstractImagePrototype.*;
import static org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceConfig.Protocol.*;
import static org.drools.guvnor.client.common.AssetFormats.*;
import static org.drools.guvnor.client.widgets.drools.explorer.AssetDownloadLinkUtil.*;
import static org.drools.guvnor.client.widgets.drools.explorer.ExplorerRenderMode.*;

public class ServiceConfigEditor extends DirtyableComposite
        implements
        EditorWidget, SaveEventListener {

    // UI
    interface ServiceConfigEditorBinder
            extends
            UiBinder<Widget, ServiceConfigEditor> {

    }

    private static ServiceConfigEditorBinder uiBinder = GWT.create(ServiceConfigEditorBinder.class);
    ArtifactDependenciesServiceAsync mavenArtifactsAsync = (ArtifactDependenciesServiceAsync) GWT.create(ArtifactDependenciesService.class);

    private static Images images = GWT.create(Images.class);

    public static final Map<String, ImageResource> FORMAT_IMAGES = new HashMap<String, ImageResource>() {{
        put(BUSINESS_RULE, images.ruleAsset());
        put(DRL, images.technicalRuleAssets());
        put(DSL, images.dsl());
        put(BPMN2_PROCESS, images.ruleflowSmall());
        put(DECISION_TABLE_GUIDED, images.gdst());
        put(CHANGE_SET, images.enumeration());
        put(MODEL, images.modelAsset());
    }};

    @UiField
    protected ListBox listProtocol;

    @UiField
    protected TextBox pollingFrequency;

    @UiField
    protected Button btnDownloadWar;

    @UiField
    protected Button btnAssetResource;

    @UiField
    protected Button btnRemoveSelected;

    @UiField
    protected Button btnArtifacts;

    @UiField
    protected Tree resourceTree;

    final private Asset asset;
    final private ClientFactory clientFactory;
    final private String assetPackageName;
    final private String assetPackageUUID;
    final private String assetUUID;
    final private String assetName;
    private ServiceConfig config;
    private final TreeItem root;

    private Collection<MavenArtifact> serviceArtifacts = null;

    private final Map<String, Map<String, TreeItem>> resourcesIndex = new HashMap<String, Map<String, TreeItem>>();
    private final Map<String, TreeItem> packageIndex = new HashMap<String, TreeItem>();

    public ServiceConfigEditor(final Asset a, final RuleViewer v, final ClientFactory clientFactory, final EventBus eventBus) {
        this(a, clientFactory);
    }

    public ServiceConfigEditor(final Asset asset, final ClientFactory clientFactory) {

        this.initWidget(uiBinder.createAndBindUi(this));

        this.asset = asset;
        this.clientFactory = clientFactory;
        this.assetUUID = asset.getUuid();
        this.assetPackageUUID = asset.getMetaData().getModuleUUID();
        this.assetPackageName = asset.getMetaData().getModuleName();
        this.assetName = asset.getName();
        this.config = (ServiceConfig) asset.getContent();

        this.customizeUIElements();

        this.root = resourceTree.addItem(treeItemFormat(asset.getName(), images.enumeration()));

        this.loadContent();
    }

    private void loadContent() {

        this.pollingFrequency.setText(String.valueOf(config.getPollingFrequency()));

        if (config.getProtocol().equals(REST)) {
            this.listProtocol.setSelectedIndex(0);
        } else {
            this.listProtocol.setSelectedIndex(1);
        }

        for (ServiceConfig.AssetReference assetReference : config.getResources()) {
            addResource(assetReference);
        }

        for (ServiceConfig.AssetReference modelReference : config.getModels()) {
            addResource(modelReference);
        }

        mavenArtifactsAsync.getDependencies(new AsyncCallback<Collection<MavenArtifact>>() {
            public void onFailure(final Throwable e) {
                ErrorPopup.showMessage(e.getMessage());
            }

            public void onSuccess(Collection<MavenArtifact> result) {
                serviceArtifacts = new ArrayList<MavenArtifact>(result);
            }
        });
    }

    private void customizeUIElements() {
        listProtocol.addItem("REST", REST.toString());
        listProtocol.addItem("WebService", WEB_SERVICE.toString());

        listProtocol.addChangeHandler(new ChangeHandler() {
            public void onChange(ChangeEvent event) {
                makeDirty();
            }
        });

        pollingFrequency.addKeyPressHandler(new KeyPressHandler() {
            public void onKeyPress(final KeyPressEvent event) {
                final TextBox sender = (TextBox) event.getSource();
                final int keyCode = event.getNativeEvent().getKeyCode();

                if (!(Character.isDigit(event.getCharCode()))
                        && !(keyCode == KeyCodes.KEY_TAB) && !(keyCode == KeyCodes.KEY_DELETE)
                        && !(keyCode == KeyCodes.KEY_ENTER) && !(keyCode == KeyCodes.KEY_HOME)
                        && !(keyCode == KeyCodes.KEY_END) && !(keyCode == KeyCodes.KEY_BACKSPACE)
                        && !(keyCode == KeyCodes.KEY_UP) && !(keyCode == KeyCodes.KEY_DOWN)
                        && !(keyCode == KeyCodes.KEY_LEFT) && !(keyCode == KeyCodes.KEY_RIGHT)) {
                    sender.cancelKey();
                }
                makeDirty();
            }
        });
    }

    public void onSave() {
        final String pollingFrequency = this.pollingFrequency.getText();
        final String protocol = listProtocol.getValue(listProtocol.getSelectedIndex());
        final Collection<ServiceConfig.AssetReference> resources = new ArrayList<ServiceConfig.AssetReference>();
        final Collection<ServiceConfig.AssetReference> models = new ArrayList<ServiceConfig.AssetReference>();
        final Collection<MavenArtifact> excludedArtifacts = new ArrayList<MavenArtifact>();

        final Iterator<TreeItem> iterator = resourceTree.treeItemIterator();
        while (iterator.hasNext()) {
            final TreeItem item = iterator.next();
            if (item.getUserObject() != null) {
                final ServiceConfig.AssetReference assetReference = (ServiceConfig.AssetReference) item.getUserObject();
                if (assetReference.getFormat().equals(AssetFormats.MODEL)){
                    models.add(assetReference);
                } else {
                    resources.add(assetReference);
                }
            }
        }

        excludedArtifacts.addAll(this.config.getExcludedArtifacts());

        this.config = new ServiceConfig(pollingFrequency, protocol, resources, models, excludedArtifacts);

        asset.setContent(config);
    }

    public void onAfterSave() {
    }

    @UiHandler("btnRemoveSelected")
    public void removeSelectedElements(final ClickEvent e) {
        final List<TreeItem> result = new ArrayList<TreeItem>();
        buildExcludedList(root, result);
        for (final TreeItem item : result) {
            if (item.getUserObject() != null) {
                removeFromIndexes((ServiceConfig.AssetReference) item.getUserObject());
            }
            if (item.getUserObject() != null) {
                item.remove();
            }
        }
    }

    private void removeFromIndexes(final ServiceConfig.AssetReference userObject) {
        removeFromIndex(userObject, packageIndex);
        for (final Map<String, TreeItem> pkgItem : resourcesIndex.values()) {
            removeFromIndex(userObject, pkgItem);
        }
    }

    private void removeFromIndex(ServiceConfig.AssetReference userObject, Map<String, TreeItem> index) {
        for (final Map.Entry<String, TreeItem> element : index.entrySet()) {
            if (element.getValue().getUserObject() != null) {
                final ServiceConfig.AssetReference activeAsset = (ServiceConfig.AssetReference) element.getValue().getUserObject();
                if (activeAsset.getUuid().equals(userObject.getUuid())) {
                    index.remove(element.getKey());
                    break;
                }
            }
        }
    }

    private void buildExcludedList(final TreeItem item, final List<TreeItem> result) {
        if (item.getWidget() != null) {
            if (((CheckBox) item.getWidget()).getValue()) {
                result.add(item);
            }
        }

        if (item.getChildCount() > 0) {
            for (int i = 0; i < item.getChildCount(); i++) {
                buildExcludedList(item.getChild(i), result);
            }
        }
    }

    @UiHandler("btnAssetResource")
    public void addNewAssetResource(final ClickEvent e) {
        final AssetResourceExplorerWidget widget = new AssetResourceExplorerWidget(assetPackageUUID,
                assetPackageName, clientFactory, SERVICE_CONFIG_RESOURCE, HIDE_NAME_AND_DESCRIPTION);

        final NewResourcePopup popup = new NewResourcePopup(widget.asWidget());

        popup.addOkButtonClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                try {
                    widget.processSelectedResources(new ResourceElementReadyCommand() {

                        public void onSuccess(String packageRef, Asset[] result, String name, String description) {
                            for (final Asset asset : result) {
                                final ServiceConfig.AssetReference reference = new ServiceConfig.AssetReference(packageRef,
                                        asset.getName(),
                                        asset.getFormat(),
                                        buildDownloadLink(asset, packageRef),
                                        asset.getUuid());
                                addResource(reference);
                            }
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

    @UiHandler("btnArtifacts")
    public void setupMavenArtifacts(final ClickEvent e) {

        final ArtifactDependenciesExplorerWidget widget =
                new ArtifactDependenciesExplorerWidget(assetName, serviceArtifacts, config.getExcludedArtifacts());

        final NewResourcePopup popup = new NewResourcePopup(widget.asWidget());

        popup.addOkButtonClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                try {
                    widget.processExcludedArtifacts(new ArtifactDependenciesReadyCommand() {
                        public void onSuccess(Collection<MavenArtifact> excludedItems) {
                            config.setExcludedArtifacts(excludedItems);
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

    @UiHandler("btnDownloadWar")
    public void downloadWarFile(ClickEvent e) {
        Window.open(GWT.getModuleBaseURL() + "serviceWarBuilderAndDownloadHandler?uuid=" + assetUUID, "service download", "");
    }

    private void addResource(final ServiceConfig.AssetReference asset) {

        if (!resourcesIndex.containsKey(asset.getPkg())) {
            packageIndex.put(asset.getPkg(), buildTreeItem(root, asset.getPkg(), images.packageImage(), null));
            resourcesIndex.put(asset.getPkg(), new HashMap<String, TreeItem>());
        }

        final TreeItem pkg = packageIndex.get(asset.getPkg());

        if (!resourcesIndex.get(asset.getPkg()).containsKey(asset.getFormat())) {
            final TreeItem newFormat = buildTreeItem(pkg, asset.getFormat(), FORMAT_IMAGES.get(asset.getFormat()), null);
            resourcesIndex.get(asset.getPkg()).put(asset.getFormat(), newFormat);
        }

        final TreeItem parent = resourcesIndex.get(asset.getPkg()).get(asset.getFormat());

        buildTreeItem(parent, asset.getName(), images.rules(), asset);

        makeDirty();
    }

    private TreeItem buildTreeItem(final TreeItem parent, final String text, final ImageResource image, final ServiceConfig.AssetReference asset) {

        if (asset != null) {
            for (int i = 0; i < parent.getChildCount(); i++) {
                if (parent.getChild(i).getUserObject() == null) {
                    continue;
                }
                final ServiceConfig.AssetReference currentAsset = (ServiceConfig.AssetReference) parent.getChild(i).getUserObject();
                if (currentAsset.getName().equals(text)) {
                    return parent.getChild(i);
                }
            }
        }

        final CheckBox checkBox = new CheckBox(treeItemFormat(text, image));
        final TreeItem newTreeItem = parent.addItem(checkBox);
        newTreeItem.setUserObject(asset);

        checkBox.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                final CheckBox me = ((CheckBox) event.getSource());
                boolean checked = me.getValue();
                if (newTreeItem.getChildCount() > 0) {
                    for (int i = 0; i < newTreeItem.getChildCount(); i++) {
                        defineState(newTreeItem.getChild(i), checked);
                    }
                }
            }

            private void defineState(TreeItem currentItem, boolean checked) {
                ((CheckBox) currentItem.getWidget()).setValue(checked);
                if (currentItem.getChildCount() > 0) {
                    for (int i = 0; i < currentItem.getChildCount(); i++) {
                        defineState(currentItem.getChild(i), checked);
                    }
                }
            }
        });

        parent.setState(true, false);
        newTreeItem.setState(true, false);

        return newTreeItem;
    }

    private SafeHtml treeItemFormat(final String text, final ImageResource image) {
        return new SafeHtmlBuilder()
                .append(fromTrustedString(create(image).getHTML()))
                .appendEscaped(" ")
                .appendEscaped(text).toSafeHtml();
    }

    private class NewResourcePopup extends FormStylePopup {

        private final Button ok = new Button(Constants.INSTANCE.OK());

        public NewResourcePopup(Widget content) {
            setTitle(Constants.INSTANCE.NewResource());

            final HorizontalPanel hor = new HorizontalPanel();
            final Button cancel = new Button(Constants.INSTANCE.Cancel());

            hor.add(ok);
            hor.add(cancel);

            addRow(content);
            addRow(hor);

            cancel.addClickHandler(new ClickHandler() {
                public void onClick(final ClickEvent event) {
                    hide();
                }
            });
        }

        public void addOkButtonClickHandler(final ClickHandler clickHandler) {
            ok.addClickHandler(clickHandler);
        }
    }
}