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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.ImageResourceCell;
import com.google.gwt.cell.client.SelectionCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.common.DirtyableComposite;
import org.drools.guvnor.client.common.ErrorPopup;
import org.drools.guvnor.client.common.FormStylePopup;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.resources.DroolsGuvnorImages;
import org.drools.guvnor.client.rpc.Asset;
import org.drools.guvnor.client.widgets.drools.explorer.AssetResourceExplorerWidget;
import org.drools.guvnor.client.widgets.drools.explorer.ResourceElementReadyCommand;

import static com.google.gwt.safehtml.shared.SafeHtmlUtils.*;
import static com.google.gwt.user.client.ui.AbstractImagePrototype.*;
import static org.drools.guvnor.client.common.AssetFormats.*;
import static org.drools.guvnor.client.util.Preconditions.*;
import static org.drools.guvnor.client.widgets.drools.explorer.AssetDownloadLinkUtil.*;
import static org.drools.guvnor.client.widgets.drools.explorer.ExplorerRenderMode.*;
import static org.drools.guvnor.client.widgets.drools.explorer.PackageDisplayMode.*;

public class KBaseConfigPanel extends DirtyableComposite {

    // UI
    interface KBaseConfigEditorBinder extends UiBinder<Widget, KBaseConfigPanel> {

    }

    private static DroolsGuvnorImages images = GWT.create(DroolsGuvnorImages.class);

    private static KBaseConfigEditorBinder uiBinder = GWT.create(KBaseConfigEditorBinder.class);

    public static final Map<String, ImageResource> FORMAT_IMAGES = new HashMap<String, ImageResource>() {{
        put(BUSINESS_RULE, images.ruleAsset());
        put(DRL, images.technicalRuleAssets());
        put(DSL, images.dsl());
        put(BPMN2_PROCESS, images.ruleflowSmall());
        put(DECISION_TABLE_GUIDED, images.gdst());
        put(CHANGE_SET, images.enumeration());
        put(MODEL, images.modelAsset());
    }};

    final private String assetPackageUUID;
    final private String assetPackageName;
    final private ClientFactory clientFactory;

    @UiField
    protected Button btnAssetResource;

    @UiField
    protected Button btnRemoveSelected;

    @UiField
    protected Button btnRename;

    @UiField
    protected Button btnAdvancedOptions;

    @UiField
    protected Tree resourceTree;

    @UiField(provided = true)
    final CellTable<ServiceKSessionConfig> cellTable;

    @UiField(provided = true)
    final SimplePager pager;

    private final ListDataProvider<ServiceKSessionConfig> dataProvider = new ListDataProvider<ServiceKSessionConfig>();

    private final ServiceConfig config;

    private final ServiceConfigEditor.UpdateTabEvent updateTab;

    private ServiceKBaseConfig kbase;

    private final TreeItem root;

    private final Map<String, Map<String, TreeItem>> resourcesIndex = new HashMap<String, Map<String, TreeItem>>();
    private final Map<String, TreeItem> packageIndex = new HashMap<String, TreeItem>();

    public static final ProvidesKey<ServiceKSessionConfig> KEY_PROVIDER = new ProvidesKey<ServiceKSessionConfig>() {
        public Object getKey(final ServiceKSessionConfig item) {
            return item == null ? null : item.getName();
        }
    };

    public KBaseConfigPanel(final ServiceConfig config, final ServiceKBaseConfig kbase,
            final ServiceConfigEditor.UpdateTabEvent updateTab,
            final String assetPackageUUID, final String assetPackageName, final ClientFactory clientFactory) {
        this.kbase = checkNotNull("kbase", kbase);
        this.config = checkNotNull("config", config);
        this.updateTab = checkNotNull("updateTab", updateTab);
        this.clientFactory = clientFactory;
        this.assetPackageUUID = assetPackageUUID;
        this.assetPackageName = assetPackageName;

        //INICIO table
        cellTable = new CellTable<ServiceKSessionConfig>(KEY_PROVIDER);
        cellTable.setWidth("100%", false);

        final SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
        pager = new SimplePager(SimplePager.TextLocation.CENTER, pagerResources, false, 0, true);
        pager.setDisplay(cellTable);

        initTableColumns(cellTable);
        //FIM table

        // Add the CellList to the adapter in the database.
        dataProvider.addDataDisplay(cellTable);

        dataProvider.getList().addAll(kbase.getKsessions());

        this.initWidget(uiBinder.createAndBindUi(this));

        this.root = resourceTree.addItem(treeItemFormat(kbase.getName(), images.enumeration()));

        this.loadContent();
    }

    private void initTableColumns(final CellTable<ServiceKSessionConfig> cellTable) {

        // Add new row
        MyClickableImageCell addRowCell = new MyClickableImageCell(new MyClickableImageCell.ImageCellClickHandler() {
            public void onClick(final Cell.Context context) {
                final ServiceKSessionConfig newKsession = new ServiceKSessionConfig(kbase.getNextKSessionName());
                kbase.addKsession(newKsession);

                dataProvider.getList().add(newKsession);
                dataProvider.refresh();
                pager.lastPage();
            }
        });

        final Column<ServiceKSessionConfig, ImageResource> imageColumn = new Column<ServiceKSessionConfig, ImageResource>(addRowCell) {
            @Override
            public ImageResource getValue(final ServiceKSessionConfig object) {
                return images.itemImages().newItem();
            }
        };
        cellTable.addColumn(imageColumn, SafeHtmlUtils.fromSafeConstant("<br/>"));
        cellTable.setColumnWidth(imageColumn, "16px");

        // Remove active row
        MyClickableImageCell removeRowCell = new MyClickableImageCell(new MyClickableImageCell.ImageCellClickHandler() {
            public void onClick(final Cell.Context context) {
                if (dataProvider.getList().size() == 1) {
                    Window.alert(Constants.INSTANCE.KBaseNeedsOneKsession());
                } else {
                    dataProvider.getList().remove(context.getIndex());
                    kbase.removeKsession((String) context.getKey());
                    dataProvider.refresh();
                }
            }
        });

        final Column<ServiceKSessionConfig, ImageResource> imageColumn2 = new Column<ServiceKSessionConfig, ImageResource>(removeRowCell) {
            @Override
            public ImageResource getValue(final ServiceKSessionConfig object) {
                return images.removeItem();
            }
        };
        cellTable.addColumn(imageColumn2, SafeHtmlUtils.fromSafeConstant("<br/>"));
        cellTable.setColumnWidth(imageColumn, "16px");

        // KSession Name
        final EditTextCell textCell = new EditTextCell();
        final Column<ServiceKSessionConfig, String> nameColumn = new Column<ServiceKSessionConfig, String>(textCell) {
            @Override
            public String getValue(final ServiceKSessionConfig object) {
                return object.getName();
            }
        };
        cellTable.addColumn(nameColumn, Constants.INSTANCE.Name());
        nameColumn.setFieldUpdater(new FieldUpdater<ServiceKSessionConfig, String>() {
            public void update(int index, ServiceKSessionConfig object, String value) {
                // Called when the user changes the value.
                if (object.getName().equals(value)) {
                    return;
                }

                if (kbase.getKsession(value) != null) {
                    Window.alert(Constants.INSTANCE.KSessionNameAlreadyExists());
                    textCell.clearViewData(KEY_PROVIDER.getKey(object));
                    dataProvider.flush();
                    dataProvider.refresh();
                    cellTable.redraw();
                } else {
                    final ServiceKSessionConfig updatedKsession = new ServiceKSessionConfig(value, object);
                    kbase.removeKsession(object.getName());
                    kbase.addKsession(updatedKsession);
                    dataProvider.getList().set(index, updatedKsession);
                    dataProvider.refresh();
                }
            }
        });
        cellTable.setColumnWidth(nameColumn, "40%");

        // Type
        final List<String> sessionTypes = new ArrayList<String>(SessionType.values().length);
        for (final SessionType activeType : SessionType.values()) {
            sessionTypes.add(activeType.toString().toLowerCase());
        }

        final SelectionCell typeCell = new SelectionCell(sessionTypes);
        final Column<ServiceKSessionConfig, String> typeColumn = new Column<ServiceKSessionConfig, String>(typeCell) {
            @Override
            public String getValue(ServiceKSessionConfig object) {
                return object.getType().toString().toLowerCase();
            }
        };
        cellTable.addColumn(typeColumn, Constants.INSTANCE.Type());
        typeColumn.setFieldUpdater(new FieldUpdater<ServiceKSessionConfig, String>() {
            public void update(int index, ServiceKSessionConfig object, String value) {
                // Called when the user changes the value.
                object.setType(SessionType.valueOf(value.toUpperCase()));
                dataProvider.refresh();
            }
        });
        cellTable.setColumnWidth(typeColumn, "40%");

        //Advanced config
        final Column<ServiceKSessionConfig, String> configAdvanced = new Column<ServiceKSessionConfig, String>(new ButtonCell()) {
            @Override
            public String getValue(ServiceKSessionConfig object) {
                return "...";
            }
        };
        cellTable.addColumn(configAdvanced, Constants.INSTANCE.Config());
        configAdvanced.setFieldUpdater(new FieldUpdater<ServiceKSessionConfig, String>() {
            public void update(int index, ServiceKSessionConfig object, String value) {

                final AdvancedKSessionConfigWidget widget = new AdvancedKSessionConfigWidget(object);
                final InternalPopup popup = new InternalPopup(widget.asWidget(), Constants.INSTANCE.KSessionConfiguration());
                popup.addOkButtonClickHandler(new ClickHandler() {

                    public void onClick(ClickEvent event) {
                        widget.updateKSession();
                        popup.hide();
                    }
                });
                popup.show();
            }
        });
        cellTable.setColumnWidth(configAdvanced, "20%");
    }

    private void loadContent() {
        for (AssetReference assetReference : kbase.getResources()) {
            addResource(assetReference);
        }

        for (AssetReference modelReference : kbase.getModels()) {
            addResource(modelReference);
        }
    }

    @UiHandler("btnRemoveSelected")
    public void removeSelectedElements(final ClickEvent e) {
        final List<TreeItem> result = new ArrayList<TreeItem>();
        buildExcludedList(root, result);
        for (final TreeItem item : result) {
            if (item.getUserObject() != null) {
                removeFromIndexes((AssetReference) item.getUserObject());
            }
            if (item.getUserObject() != null) {
                item.remove();
            }
        }
    }

    private void removeFromIndexes(final AssetReference userObject) {
        removeFromIndex(userObject, packageIndex);
        for (final Map<String, TreeItem> pkgItem : resourcesIndex.values()) {
            removeFromIndex(userObject, pkgItem);
        }
    }

    private void removeFromIndex(AssetReference userObject, Map<String, TreeItem> index) {
        for (final Map.Entry<String, TreeItem> element : index.entrySet()) {
            if (element.getValue().getUserObject() != null) {
                final AssetReference activeAsset = (AssetReference) element.getValue().getUserObject();
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
                assetPackageName, clientFactory,
                SERVICE_CONFIG_RESOURCE,
                HIDE_NAME_AND_DESCRIPTION,
                ALL_PACKAGES);

        final InternalPopup popup = new InternalPopup(widget.asWidget(), Constants.INSTANCE.AddNewAsset());

        popup.addOkButtonClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                try {
                    widget.processSelectedResources(new ResourceElementReadyCommand() {

                        public void onSuccess(String packageRef, Asset[] result, String name, String description) {
                            for (final Asset asset : result) {
                                final AssetReference reference = new AssetReference(packageRef,
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

    private void addResource(final AssetReference asset) {

        if (!resourcesIndex.containsKey(asset.getPackageRef())) {
            packageIndex.put(asset.getPackageRef(), buildTreeItem(root, asset.getPackageRef(), images.packageImage(), null));
            resourcesIndex.put(asset.getPackageRef(), new HashMap<String, TreeItem>());
        }

        final TreeItem pkg = packageIndex.get(asset.getPackageRef());

        if (!resourcesIndex.get(asset.getPackageRef()).containsKey(asset.getFormat())) {
            final TreeItem newFormat = buildTreeItem(pkg, asset.getFormat(), FORMAT_IMAGES.get(asset.getFormat()), null);
            resourcesIndex.get(asset.getPackageRef()).put(asset.getFormat(), newFormat);
        }

        final TreeItem parent = resourcesIndex.get(asset.getPackageRef()).get(asset.getFormat());

        buildTreeItem(parent, asset.getName(), images.rules(), asset);

        makeDirty();
    }

    private TreeItem buildTreeItem(final TreeItem parent, final String text, final ImageResource image, final AssetReference asset) {

        if (asset != null) {
            for (int i = 0; i < parent.getChildCount(); i++) {
                if (parent.getChild(i).getUserObject() == null) {
                    continue;
                }
                final AssetReference currentAsset = (AssetReference) parent.getChild(i).getUserObject();
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

    @UiHandler("btnRename")
    public void doRename(final ClickEvent e) {
        final FormStylePopup pop = new FormStylePopup(DroolsGuvnorImages.INSTANCE.packageLarge(), Constants.INSTANCE.RenameThisKBase());
        final TextBox box = new TextBox();
        box.setText(kbase.getName());
        pop.addAttribute(Constants.INSTANCE.NewNameAsset(), box);
        final Button ok = new Button(Constants.INSTANCE.RenameKBase());
        pop.addAttribute("", ok);
        ok.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent w) {
                final String newName = box.getText().trim();
                if (kbase.getName().equals(newName)) {
                    pop.hide();
                    return;
                }

                if (config.getKbase(newName) != null) {
                    Window.alert(Constants.INSTANCE.KBaseNameAlreadyExists());
                    pop.hide();
                    return;
                }

                config.removeKBase(kbase.getName());
                kbase = new ServiceKBaseConfig(newName, kbase);
                config.addKBase(kbase);
                updateTab.onUpdate(newName);
                root.setHTML(treeItemFormat(kbase.getName(), images.enumeration()));
                pop.hide();
            }
        });

        pop.show();
    }

    public void onSave() {
        final Collection<AssetReference> resources = new ArrayList<AssetReference>();
        final Collection<AssetReference> models = new ArrayList<AssetReference>();

        final Iterator<TreeItem> iterator = resourceTree.treeItemIterator();
        while (iterator.hasNext()) {
            final TreeItem item = iterator.next();
            if (item.getUserObject() != null) {
                final AssetReference assetReference = (AssetReference) item.getUserObject();
                if (assetReference.getFormat().equals(AssetFormats.MODEL)) {
                    models.add(assetReference);
                } else {
                    resources.add(assetReference);
                }
            }
        }

        kbase.setModels(models);
        kbase.setResources(resources);
    }

    @UiHandler("btnAdvancedOptions")
    public void advancedOptions(final ClickEvent e) {
        final AdvancedKBaseConfigWidget widget = new AdvancedKBaseConfigWidget(kbase);

        final InternalPopup popup = new InternalPopup(widget.asWidget(), Constants.INSTANCE.KBaseAdvancedOptions());
        popup.addOkButtonClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                widget.updateKBase();
                popup.hide();
            }
        });
        popup.show();
    }

    public ServiceKBaseConfig getKBase() {
        return kbase;
    }

    private class InternalPopup extends FormStylePopup {

        private final Button ok = new Button(Constants.INSTANCE.OK());

        public InternalPopup(final Widget content, final String title) {
            setTitle(title);

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

    private static class MyClickableImageCell extends ImageResourceCell {

        private final ImageCellClickHandler clickHandler;

        public MyClickableImageCell(final ImageCellClickHandler handler) {
            this.clickHandler = checkNotNull("handler", handler);
        }

        @Override
        public Set<String> getConsumedEvents() {
            Set<String> consumedEvents = new HashSet<String>();
            consumedEvents.add("click");
            return consumedEvents;
        }

        @Override
        public void onBrowserEvent(final Context context, final Element parent, final ImageResource value, final NativeEvent event, final ValueUpdater<ImageResource> valueUpdater) {
            switch (DOM.eventGetType((Event) event)) {
                case Event.ONCLICK:
                    clickHandler.onClick(context);
                    break;

                default:
                    break;
            }
        }

        private static interface ImageCellClickHandler {

            public void onClick(final Context context);
        }
    }
}