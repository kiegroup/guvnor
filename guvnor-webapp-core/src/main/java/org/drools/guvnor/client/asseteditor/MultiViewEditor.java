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
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.drools.guvnor.client.common.AssetFormats;
import org.kie.uberfirebootstrap.client.widgets.ErrorPopup;
import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.common.LoadingPopup;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.explorer.RefreshModuleEditorEvent;
import org.drools.guvnor.client.messages.ConstantsCore;
import org.drools.guvnor.client.rpc.Asset;
import org.drools.guvnor.client.rpc.AssetService;
import org.drools.guvnor.client.rpc.AssetServiceAsync;
import org.drools.guvnor.client.util.LazyStackPanel;
import org.drools.guvnor.client.util.LoadContentCommand;
import org.drools.guvnor.client.widgets.CheckinPopup;
import org.drools.guvnor.client.widgets.toolbar.ActionToolbarButtonsConfigurationProvider;
import org.drools.guvnor.client.rpc.Path;
import org.drools.guvnor.client.rpc.PathImpl;

import java.util.*;

public class MultiViewEditor extends GuvnorEditor {

    private ConstantsCore constants = GWT.create(ConstantsCore.class);

    private final ClientFactory clientFactory;
    private VerticalPanel viewsPanel = new VerticalPanel();
    private Command closeCommand;
    private final Set<MultiViewRow> rows = new HashSet<MultiViewRow>();
    private Map<String, RuleViewer> ruleViews = new HashMap<String, RuleViewer>();
    private ActionToolbarButtonsConfigurationProvider individualActionToolbarButtonsConfigurationProvider;

    private Map<String, Asset> assets = new HashMap<String, Asset>();

    private MultiViewEditorMenuBarCreator menuBarCreator;
    private final EventBus eventBus;

    public MultiViewEditor(MultiViewRow[] rows,
                           ClientFactory clientFactory,
                           EventBus eventBus) {
        this(rows,
                clientFactory,
                eventBus,
                null);
    }

    public MultiViewEditor(MultiViewRow[] rows,
                           ClientFactory clientFactory,
                           EventBus eventBus,
                           ActionToolbarButtonsConfigurationProvider individualActionToolbarButtonsConfigurationProvider) {
        this(Arrays.asList(rows),
                clientFactory,
                eventBus,
                individualActionToolbarButtonsConfigurationProvider);
    }

    public MultiViewEditor(List<MultiViewRow> rows,
                           ClientFactory clientFactory,
                           EventBus eventBus,
                           ActionToolbarButtonsConfigurationProvider individualActionToolbarButtonsConfigurationProvider) {
        this.rows.addAll(rows);
        this.individualActionToolbarButtonsConfigurationProvider = individualActionToolbarButtonsConfigurationProvider;
        this.clientFactory = clientFactory;
        this.eventBus = eventBus;

        init();
    }

    public MultiViewEditor(Asset[] assets,
                           ClientFactory clientFactory,
                           EventBus eventBus,
                           ActionToolbarButtonsConfigurationProvider individualActionToolbarButtonsConfigurationProvider) {
        this(assets,
                clientFactory,
                eventBus,
                individualActionToolbarButtonsConfigurationProvider,
                null);
    }

    public MultiViewEditor(Asset[] assets,
                           ClientFactory clientFactory,
                           EventBus eventBus,
                           ActionToolbarButtonsConfigurationProvider individualActionToolbarButtonsConfigurationProvider,
                           MultiViewEditorMenuBarCreator menuBarCreator) {
        this.rows.addAll(createRows(assets));
        this.clientFactory = clientFactory;
        this.eventBus = eventBus;
        this.individualActionToolbarButtonsConfigurationProvider = individualActionToolbarButtonsConfigurationProvider;
        this.menuBarCreator = menuBarCreator;
        addAssets(assets);
        init();
    }

    private void addAssets(Asset[] assets) {
        for (Asset ruleAsset : assets) {
            this.assets.put(ruleAsset.getUuid(),
                    ruleAsset);
        }
    }

    private static List<MultiViewRow> createRows(Asset[] assets) {
        List<MultiViewRow> rows = new ArrayList<MultiViewRow>();
        for (Asset ruleAsset : assets) {
            MultiViewRow row = new MultiViewRow(
                    ruleAsset.getUuid(),
                    ruleAsset.getName(),
                    AssetFormats.BUSINESS_RULE);
            rows.add(row);
        }
        return rows;
    }

    private void init() {
        VerticalPanel rootPanel = new VerticalPanel();

        rootPanel.setWidth("100%");

        rootPanel.add(createToolbar());

        viewsPanel.setWidth("100%");
        rootPanel.add(viewsPanel);

        doViews();

        initWidget(rootPanel);
    }

    private MenuBar createToolbar() {

        //if no MultiViewEditorMenuBarCreator is set, then use the Default
        //implementation.
        if (this.menuBarCreator == null) {
            this.menuBarCreator = new DefaultMultiViewEditorMenuBarCreator();
        }

        return this.menuBarCreator.createMenuBar(this, eventBus);
    }

    private void doViews() {

        viewsPanel.clear();
        ruleViews.clear();
        final LazyStackPanel panel = new LazyStackPanel();

        //the first row will be expanded
        int rowNumber = 1;
        for (final MultiViewRow row : rows) {

            panel.add(row.getName(),
                    new LoadContentCommand() {

                        public Widget load() {
                            final SimplePanel content = new SimplePanel();

                            if (assets.containsKey(row.getUuid())) {
                                addRuleViewInToSimplePanel(row,
                                        content,
                                        assets.get(row.getUuid()));
                            } else {
                                AssetServiceAsync assetService = GWT.create(AssetService.class);
                            	Path path = new PathImpl();
                            	path.setUUID(row.getUuid());
                                assetService.loadRuleAsset(path,
                                        new GenericCallback<Asset>() {

                                            public void onSuccess(final Asset asset) {
                                                assets.put(asset.getUuid(),
                                                        asset);

                                                addRuleViewInToSimplePanel(row,
                                                        content,
                                                        asset);
                                            }

                                        });

                            }
                            return content;
                        }
                    },
                    rowNumber == 1);

            rowNumber++;
        }

        viewsPanel.add(panel);

    }

    private void addRuleViewInToSimplePanel(final MultiViewRow row,
                                            final SimplePanel content,
                                            final Asset asset) {

    }

    public void checkin(final boolean closeAfter) {
        final CheckinPopup pop = new CheckinPopup(constants.CheckInChanges());
        pop.setCommand(new Command() {

            public void execute() {
                String comment = pop.getCheckinComment();
                for (RuleViewer ruleViewer : ruleViews.values()) {
                    doCheckin(ruleViewer.getAssetEditor(), ruleViewer.asset, comment, false);
                }
                if (closeAfter) {
                    close();
                }
            }
        });
        pop.show();

    }

    public void doCheckin(final Widget editor, final Asset asset, final String comment, final boolean closeAfter) {
        if (editor instanceof SaveEventListener) {
            ((SaveEventListener) editor).onSave(new SaveCommand() {
                @Override
                public void save() {
                    MultiViewEditor.this.save(comment, closeAfter, asset, editor);
                }

                @Override
                public void cancel() {
                }
            });
        } else {
            save(comment, closeAfter, asset, editor);
        }
    }

    private void save(String comment, boolean closeAfter, Asset asset, Widget editor) {
        performCheckIn(comment, closeAfter, asset);
        if (editor instanceof SaveEventListener) {
            ((SaveEventListener) editor).onAfterSave();
        }

        eventBus.fireEvent(new RefreshModuleEditorEvent(asset.getMetaData()
                .getModuleUUID()));
    }

    private void performCheckIn(String comment,
                                final boolean closeAfter, final Asset asset) {
        asset.setCheckinComment(comment);
        final boolean[] saved = {false};

        if (!saved[0]) LoadingPopup.showMessage(constants.SavingPleaseWait());
        AssetServiceAsync assetService = GWT.create(AssetService.class);
        assetService.checkinVersion(asset,
                new GenericCallback<String>() {

                    public void onSuccess(String uuid) {
                        if (uuid == null) {
                            ErrorPopup.showMessage(constants.FailedToCheckInTheItemPleaseContactYourSystemAdministrator());
                            return;
                        }

                        if (uuid.startsWith("ERR")) { // NON-NLS
                            ErrorPopup.showMessage(uuid.substring(5));
                            return;
                        }

                        flushSuggestionCompletionCache(asset.getMetaData().getModuleName(), asset);
/*                        if ( editor instanceof DirtyableComposite ) {
                            ((DirtyableComposite) editor).resetDirty();
                        }*/

                        LoadingPopup.close();
                        saved[0] = true;


                        //fire after check-in event
                        eventBus.fireEvent(new AfterAssetEditorCheckInEvent(uuid, MultiViewEditor.this));
                    }
                });
    }

    /**
     * In some cases we will want to flush the package dependency stuff for
     * suggestion completions. The user will still need to reload the asset
     * editor though.
     */
    public void flushSuggestionCompletionCache(final String packageName, Asset asset) {

    }

    public void close() {

    }

    @Override
    public boolean isDirty() {
        return false;
    }

    @Override
    public void makeDirty() {
    }

    @Override
    public void resetDirty() {
    }

    public void setCloseCommand(Command command) {
        closeCommand = command;
    }
}
