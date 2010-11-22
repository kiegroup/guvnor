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

package org.drools.guvnor.client.rulelist;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.DateCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.ImageCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.cellview.client.TextHeader;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.ProvidesKey;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.rpc.AssetPageRequest;
import org.drools.guvnor.client.rpc.AssetPageResponse;
import org.drools.guvnor.client.rpc.RepositoryServiceAsync;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;
import org.drools.guvnor.client.rpc.AssetPageRow;
import org.drools.guvnor.client.ruleeditor.EditorLauncher;
import org.drools.guvnor.client.ruleeditor.MultiViewRow;
import org.drools.guvnor.client.table.ColumnPicker;
import org.drools.guvnor.client.table.SelectionColumn;
import org.drools.guvnor.client.table.SortableHeader;
import org.drools.guvnor.client.table.SortableHeaderGroup;

/**
 * Widget with a table of assets.
 * @author Geoffrey De Smet
 */
public class AssetTable extends Composite {

    private static final Constants constants = GWT.create(Constants.class);
    private RepositoryServiceAsync repositoryService = RepositoryServiceFactory.getService(); // TODO use (C)DI

    private final String packageUuid;
    private final List<String> formatInList;
    private final EditItemEvent editEvent;
    private int pageSize = 50; // TODO might need to be configurable, or a constant
    private String feedURL;
    private Set<Command> unloadListenerSet = new HashSet<Command>();

    private VerticalPanel layout;
    private CellTable<AssetPageRow> cellTable;
    private AsyncDataProvider<AssetPageRow> dataProvider;
    private SimplePager pager;
    private MultiSelectionModel<AssetPageRow> selectionModel;

    public AssetTable(String packageUuid, List<String> formatInList, EditItemEvent event) {
        this(packageUuid, formatInList, event, null);
    }

    public AssetTable(String packageUuid, List<String> formatInList, final EditItemEvent event, String feedURL) {
        this.packageUuid = packageUuid;
        this.formatInList = formatInList;
        this.editEvent = event;
        this.feedURL = feedURL;
        this.layout = new VerticalPanel();
        doCellTable();
        initWidget(layout);
    }

    private void doCellTable() {
        ProvidesKey<AssetPageRow> providesKey = new ProvidesKey<AssetPageRow>() {
            public Object getKey(AssetPageRow row) {
                return row.getUuid();
            }
        };
        cellTable = new CellTable<AssetPageRow>(providesKey);
        selectionModel = new MultiSelectionModel<AssetPageRow>(providesKey);
        cellTable.setSelectionModel(selectionModel);
        SelectionColumn.createAndAddSelectionColumn(cellTable);

        ColumnPicker<AssetPageRow> columnPicker = new ColumnPicker<AssetPageRow>(cellTable);
        SortableHeaderGroup sortableHeaderGroup = new SortableHeaderGroup<AssetPageRow>(cellTable);

        final TextColumn<AssetPageRow> uuidNumberColumn = new TextColumn<AssetPageRow>() {
            public String getValue(AssetPageRow row) {
                return row.getUuid();
            }
        };
        columnPicker.addColumn(uuidNumberColumn, new SortableHeader<AssetPageRow, String>(
                sortableHeaderGroup, "uuid TODO", uuidNumberColumn), false); // TODO i18n

        Column<AssetPageRow, String> formatColumn = new Column<AssetPageRow, String>(new ImageCell()) {
            public String getValue(AssetPageRow row) {
                return "images/" + EditorLauncher.getAssetFormatIcon(row.getFormat());
            }
        };
        columnPicker.addColumn(formatColumn, new SortableHeader<AssetPageRow, String>(
                sortableHeaderGroup, "format TODO", formatColumn), true); // TODO i18n

        TextColumn<AssetPageRow> packageNameColumn = new TextColumn<AssetPageRow>() {
            public String getValue(AssetPageRow row) {
                return row.getPackageName();
            }
        };
        columnPicker.addColumn(packageNameColumn, new SortableHeader<AssetPageRow, String>(
                sortableHeaderGroup, "packageName TODO", packageNameColumn), false);

        TextColumn<AssetPageRow> nameColumn = new TextColumn<AssetPageRow>() {
            public String getValue(AssetPageRow row) {
                return row.getName();
            }
        };
        columnPicker.addColumn(nameColumn, new SortableHeader<AssetPageRow, String>(
                sortableHeaderGroup, constants.name(), nameColumn), true);

        TextColumn<AssetPageRow> descriptionColumn = new TextColumn<AssetPageRow>() {
            public String getValue(AssetPageRow row) {
                return row.getDescription();
            }
        };
        columnPicker.addColumn(descriptionColumn, new SortableHeader<AssetPageRow, String>(
                sortableHeaderGroup, "description TODO", descriptionColumn), true);

        TextColumn<AssetPageRow> stateNameColumn = new TextColumn<AssetPageRow>() {
            public String getValue(AssetPageRow row) {
                return row.getStateName();
            }
        };
        columnPicker.addColumn(stateNameColumn, new SortableHeader<AssetPageRow, String>(
                sortableHeaderGroup, "stateName TODO", stateNameColumn), true);

        TextColumn<AssetPageRow> creatorColumn = new TextColumn<AssetPageRow>() {
            public String getValue(AssetPageRow row) {
                return row.getCreator();
            }
        };
        columnPicker.addColumn(creatorColumn, new SortableHeader<AssetPageRow, String>(
                sortableHeaderGroup, "creator TODO", creatorColumn), false); // TODO i18n

        Column<AssetPageRow, Date> createdDateColumn = new Column<AssetPageRow, Date>(new DateCell(
                DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_TIME_MEDIUM))) {
            public Date getValue(AssetPageRow row) {
                return row.getCreatedDate();
            }
        };
        columnPicker.addColumn(createdDateColumn, new SortableHeader<AssetPageRow, Date>(
                sortableHeaderGroup, "createdDate TODO", createdDateColumn), false); // TODO i18n

        TextColumn<AssetPageRow> lastContributorColumn = new TextColumn<AssetPageRow>() {
            public String getValue(AssetPageRow row) {
                return row.getLastContributor();
            }
        };
        columnPicker.addColumn(lastContributorColumn, new SortableHeader<AssetPageRow, String>(
                sortableHeaderGroup, "lastContributor TODO", lastContributorColumn), false); // TODO i18n

        Column<AssetPageRow, Date> lastModifiedColumn = new Column<AssetPageRow, Date>(new DateCell(
                DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_TIME_MEDIUM))) {
            public Date getValue(AssetPageRow row) {
                return row.getLastModified();
            }
        };
        columnPicker.addColumn(lastModifiedColumn, new SortableHeader<AssetPageRow, Date>(
                sortableHeaderGroup, "lastModified TODO", lastModifiedColumn), true); // TODO i18n

        TextColumn<AssetPageRow> categorySummaryColumn = new TextColumn<AssetPageRow>() {
            public String getValue(AssetPageRow row) {
                return row.getCategorySummary();
            }
        };
        columnPicker.addColumn(categorySummaryColumn, new SortableHeader<AssetPageRow, String>(
                sortableHeaderGroup, "categorySummary TODO", categorySummaryColumn), false); // TODO i18n

        TextColumn<AssetPageRow> externalSourceColumn = new TextColumn<AssetPageRow>() {
            public String getValue(AssetPageRow row) {
                return row.getExternalSource();
            }
        };
        columnPicker.addColumn(externalSourceColumn, new SortableHeader<AssetPageRow, String>(
                sortableHeaderGroup, "externalSource TODO", externalSourceColumn), false); // TODO i18n

        Column<AssetPageRow, String> openColumn = new Column<AssetPageRow, String>(new ButtonCell()) {
            public String getValue(AssetPageRow row) {
                return "Open"; // TODO i18n
            }
        };
        openColumn.setFieldUpdater(new FieldUpdater<AssetPageRow, String>() {
            public void update(int index, AssetPageRow row, String value) {
                editEvent.open(row.getUuid());
            }
        });
        cellTable.addColumn(openColumn, new TextHeader("Open")); // TODO i18n

        cellTable.setPageSize(pageSize);
        cellTable.setWidth("100%");

        pager = new SimplePager();
        pager.setDisplay(cellTable);

        dataProvider = new AsyncDataProvider<AssetPageRow>() {
            protected void onRangeChanged(HasData<AssetPageRow> display) {
                AssetPageRequest request = new AssetPageRequest();
                request.setPackageUuid(packageUuid);
                request.setFormatInList(formatInList);
                request.setStartRowIndex(pager.getPageStart());
                request.setPageSize(pageSize);
                repositoryService.findAssetPage(request,
                        new GenericCallback<AssetPageResponse>() {
                            public void onSuccess(AssetPageResponse response) {
                                updateRowCount(response.getTotalRowSize(), true);
                                updateRowData(response.getStartRowIndex(), response.getAssetPageRowList());
                            }
                        });
            }
        };
        dataProvider.addDataDisplay(cellTable);

        HorizontalPanel tableHeaderPanel = new HorizontalPanel();
        Button refreshButton = new Button(constants.refreshList());
        refreshButton.addListener(new ButtonListenerAdapter() {
            @Override
            public void onClick(Button button, EventObject e) {
                refresh();
            }
        });
        tableHeaderPanel.add(refreshButton);
        Button openSelectedButton = new Button(constants.openSelected());
        openSelectedButton.addListener(new ButtonListenerAdapter() {
            @Override
            public void onClick(Button button, EventObject e) {
                Set<AssetPageRow> selectedSet = selectionModel.getSelectedSet();
                for (AssetPageRow selected : selectedSet) {
                    // TODO directly push the selected AssetPageRow
                    editEvent.open(selected.getUuid());
                }
            }
        });
        tableHeaderPanel.add(openSelectedButton);
        Button openSelectedToSingleTabButton = new Button(constants.openSelectedToSingleTab());
        openSelectedToSingleTabButton.addListener(new ButtonListenerAdapter() {
            @Override
            public void onClick(Button button, EventObject e) {
                Set<AssetPageRow> selectedSet = selectionModel.getSelectedSet();
                // TODO directly push the selected AssetPageRows
                List<MultiViewRow> multiViewRowList = new ArrayList<MultiViewRow>(selectedSet.size());
                for (AssetPageRow selected : selectedSet) {
                    MultiViewRow row = new MultiViewRow();
                    row.uuid = selected.getUuid();
                    row.format = selected.getFormat();
                    row.name = selected.getName();
                    multiViewRowList.add(row);
                }
                editEvent.open(multiViewRowList.toArray(new MultiViewRow[multiViewRowList.size()]));
            }
        });
        tableHeaderPanel.add(openSelectedToSingleTabButton);
        tableHeaderPanel.add(columnPicker.createToggleButton());

        if (feedURL != null) {
            // TODO use image resources to get the feed.png and use image button or something like that
            HTML feedButton = new HTML("<a href='" + feedURL + "' target='_blank'><img src='images/feed.png'/></a>");
            tableHeaderPanel.add(feedButton);
        }
        layout.add(tableHeaderPanel);
        layout.add(cellTable);
        layout.add(pager);
    }

    /**
     * Refreshes the data. Does not rebuild the GUI.
     */
    public void refresh() {
        cellTable.setVisibleRangeAndClearData(cellTable.getVisibleRange(), true);
    }

    /**
     * @param unloadListener never null
     */
    public void addUnloadListener(Command unloadListener) {
        unloadListenerSet.add(unloadListener);
    }

    @Override
    protected void onUnload() {
        super.onUnload();
        for (Command unloadListener : unloadListenerSet) {
            unloadListener.execute();
        }
    }

}
