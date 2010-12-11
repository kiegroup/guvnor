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

import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.resources.RuleFormatImageResource;
import org.drools.guvnor.client.rpc.AssetPageRequest;
import org.drools.guvnor.client.rpc.AssetPageResponse;
import org.drools.guvnor.client.rpc.AssetPageRow;
import org.drools.guvnor.client.rpc.RepositoryServiceAsync;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;
import org.drools.guvnor.client.ruleeditor.EditorLauncher;
import org.drools.guvnor.client.ruleeditor.MultiViewRow;
import org.drools.guvnor.client.table.ColumnPicker;
import org.drools.guvnor.client.table.SelectionColumn;
import org.drools.guvnor.client.table.SortableHeader;
import org.drools.guvnor.client.table.SortableHeaderGroup;

import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.DateCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.cellview.client.TextHeader;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.ProvidesKey;

/**
 * Widget with a table of assets.
 * @author Geoffrey De Smet
 */
public class AssetTable extends Composite {

    private static final Constants constants = GWT.create(Constants.class);

    interface AssetTableBinder extends UiBinder<Widget, AssetTable> {}

    private static AssetTableBinder uiBinder = GWT.create(AssetTableBinder.class);

    private RepositoryServiceAsync repositoryService = RepositoryServiceFactory.getService(); // TODO use (C)DI

    private final String packageUuid;
    private final List<String> formatInList;
    private final Boolean formatIsRegistered;
    private final EditItemEvent editEvent;
    private int pageSize = 25; // TODO might need to be configurable, or a constant
    private String feedURL;
    private Set<Command> unloadListenerSet = new HashSet<Command>();

    @UiField(provided=true)
    ToggleButton columnPickerButton;

    @UiField()
    Image feedImage;

    @UiField(provided = true)
    CellTable<AssetPageRow> cellTable;
    private AsyncDataProvider<AssetPageRow> dataProvider;
    @UiField(provided=true)
    SimplePager pager;
    private MultiSelectionModel<AssetPageRow> selectionModel;

    public AssetTable(String packageUuid, List<String> formatInList, Boolean formatIsRegistered, EditItemEvent event) {
        this(packageUuid, formatInList, formatIsRegistered, event, null);
    }

    public AssetTable(String packageUuid, List<String> formatInList, Boolean formatIsRegistered,
            final EditItemEvent event, String feedURL) {
        this.packageUuid = packageUuid;
        this.formatInList = formatInList;
        this.formatIsRegistered = formatIsRegistered;
        this.editEvent = event;
        this.feedURL = feedURL;
        doCellTable();
        initWidget(uiBinder.createAndBindUi(this));
        if (feedURL == null) {
            feedImage.setVisible(false);
        }
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
                sortableHeaderGroup, constants.uuid(), uuidNumberColumn), false);

        Column<AssetPageRow, RuleFormatImageResource> formatColumn = new Column<AssetPageRow, RuleFormatImageResource>(new RuleFormatImageResourceCell()) {

            public RuleFormatImageResource getValue(AssetPageRow row) {
                return EditorLauncher.getAssetFormatIcon(row.getFormat()); 
            }
        };
        columnPicker.addColumn(formatColumn, new SortableHeader<AssetPageRow, RuleFormatImageResource>(
                sortableHeaderGroup, constants.Format(), formatColumn), true);

        TextColumn<AssetPageRow> packageNameColumn = new TextColumn<AssetPageRow>() {
            public String getValue(AssetPageRow row) {
                return row.getPackageName();
            }
        };
        columnPicker.addColumn(packageNameColumn, new SortableHeader<AssetPageRow, String>(
                sortableHeaderGroup, constants.PackageName(), packageNameColumn), false);

        TextColumn<AssetPageRow> nameColumn = new TextColumn<AssetPageRow>() {
            public String getValue(AssetPageRow row) {
                return row.getName();
            }
        };
        columnPicker.addColumn(nameColumn, new SortableHeader<AssetPageRow, String>(
                sortableHeaderGroup, constants.Name(), nameColumn), true);

        TextColumn<AssetPageRow> descriptionColumn = new TextColumn<AssetPageRow>() {
            public String getValue(AssetPageRow row) {
                return row.getDescription();
            }
        };
        columnPicker.addColumn(descriptionColumn, new SortableHeader<AssetPageRow, String>(
                sortableHeaderGroup, constants.Description(), descriptionColumn), true);

        TextColumn<AssetPageRow> stateNameColumn = new TextColumn<AssetPageRow>() {
            public String getValue(AssetPageRow row) {
                return row.getStateName();
            }
        };
        columnPicker.addColumn(stateNameColumn, new SortableHeader<AssetPageRow, String>(
                sortableHeaderGroup, constants.Status(), stateNameColumn), true);

        TextColumn<AssetPageRow> creatorColumn = new TextColumn<AssetPageRow>() {
            public String getValue(AssetPageRow row) {
                return row.getCreator();
            }
        };
        columnPicker.addColumn(creatorColumn, new SortableHeader<AssetPageRow, String>(
                sortableHeaderGroup, constants.Creator(), creatorColumn), false);

        Column<AssetPageRow, Date> createdDateColumn = new Column<AssetPageRow, Date>(new DateCell(
                DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_TIME_MEDIUM))) {
            public Date getValue(AssetPageRow row) {
                return row.getCreatedDate();
            }
        };
        columnPicker.addColumn(createdDateColumn, new SortableHeader<AssetPageRow, Date>(
                sortableHeaderGroup, constants.CreatedDate(), createdDateColumn), false);

        TextColumn<AssetPageRow> lastContributorColumn = new TextColumn<AssetPageRow>() {
            public String getValue(AssetPageRow row) {
                return row.getLastContributor();
            }
        };
        columnPicker.addColumn(lastContributorColumn, new SortableHeader<AssetPageRow, String>(
                sortableHeaderGroup, constants.LastContributor(), lastContributorColumn), false);

        Column<AssetPageRow, Date> lastModifiedColumn = new Column<AssetPageRow, Date>(new DateCell(
                DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_TIME_MEDIUM))) {
            public Date getValue(AssetPageRow row) {
                return row.getLastModified();
            }
        };
        columnPicker.addColumn(lastModifiedColumn, new SortableHeader<AssetPageRow, Date>(
                sortableHeaderGroup, constants.LastModified(), lastModifiedColumn), true);

        TextColumn<AssetPageRow> categorySummaryColumn = new TextColumn<AssetPageRow>() {
            public String getValue(AssetPageRow row) {
                return row.getCategorySummary();
            }
        };
        columnPicker.addColumn(categorySummaryColumn, new SortableHeader<AssetPageRow, String>(
                sortableHeaderGroup, constants.Categories(), categorySummaryColumn), false);

        TextColumn<AssetPageRow> externalSourceColumn = new TextColumn<AssetPageRow>() {
            public String getValue(AssetPageRow row) {
                return row.getExternalSource();
            }
        };
        columnPicker.addColumn(externalSourceColumn, new SortableHeader<AssetPageRow, String>(
                sortableHeaderGroup, constants.ExternalSource(), externalSourceColumn), false);

        Column<AssetPageRow, String> openColumn = new Column<AssetPageRow, String>(new ButtonCell()) {
            public String getValue(AssetPageRow row) {
                return constants.Open();
            }
        };
        openColumn.setFieldUpdater(new FieldUpdater<AssetPageRow, String>() {
            public void update(int index, AssetPageRow row, String value) {
                editEvent.open(row.getUuid());
            }
        });
        columnPicker.addColumn(openColumn, new TextHeader(constants.Open()), true);

        cellTable.setPageSize(pageSize);
        cellTable.setWidth("100%");

        pager = new SimplePager();
        pager.setDisplay(cellTable);

        dataProvider = new AsyncDataProvider<AssetPageRow>() {
            protected void onRangeChanged(HasData<AssetPageRow> display) {
                AssetPageRequest request = new AssetPageRequest();
                request.setPackageUuid(packageUuid);
                request.setFormatInList(formatInList);
                request.setFormatIsRegistered(formatIsRegistered);
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

        columnPickerButton = columnPicker.createToggleButton();
    }

    @UiHandler("feedImage")
    void openFeed(ClickEvent e) {
        Window.open( feedURL,
                     "_blank",
                     null );
    }

    @UiHandler("refreshButton")
    void refresh(ClickEvent e) {
        refresh();
    }

    @UiHandler("openSelectedButton")
    void openSelected(ClickEvent e) {
        Set<AssetPageRow> selectedSet = selectionModel.getSelectedSet();
        for ( AssetPageRow selected : selectedSet ) {
            // TODO directly push the selected AssetPageRow
            editEvent.open( selected.getUuid() );
        }
    }

    @UiHandler("openSelectedToSingleTabButton")
    public void openSelectedToSingleTab(ClickEvent e) {
        Set<AssetPageRow> selectedSet = selectionModel.getSelectedSet();
        // TODO directly push the selected AssetPageRows
        List<MultiViewRow> multiViewRowList = new ArrayList<MultiViewRow>( selectedSet.size() );
        for ( AssetPageRow selected : selectedSet ) {
            MultiViewRow row = new MultiViewRow();
            row.uuid = selected.getUuid();
            row.format = selected.getFormat();
            row.name = selected.getName();
            multiViewRowList.add( row );
        }
        editEvent.open( multiViewRowList.toArray( new MultiViewRow[multiViewRowList.size()] ) );
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
