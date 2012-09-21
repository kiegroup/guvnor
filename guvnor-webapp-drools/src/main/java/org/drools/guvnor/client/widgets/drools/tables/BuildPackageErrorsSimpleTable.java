/*
 * Copyright 2011 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.drools.guvnor.client.widgets.drools.tables;

import java.util.Set;

import org.drools.guvnor.client.common.AssetEditorFactory;
import org.drools.guvnor.client.explorer.AssetEditorPlace;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.resources.ComparableImageResource;
import org.drools.guvnor.client.rpc.BuilderResultLine;
import org.drools.guvnor.client.widgets.tables.*;
import org.drools.guvnor.client.widgets.tables.ComparableImageResourceCell;

import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.cellview.client.TextHeader;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.ProvidesKey;

/**
 * Widget with a table of Build Package errors.
 */
public class BuildPackageErrorsSimpleTable extends AbstractSimpleTable<BuilderResultLine> {

    private final ClientFactory clientFactory;

    // UI
    interface BuildPackageErrorsSimpleTableBinder
        extends
        UiBinder<Widget, BuildPackageErrorsSimpleTable> {
    }

    @UiField()
    protected Button                                   openSelectedButton;

    private static BuildPackageErrorsSimpleTableBinder uiBinder = GWT.create( BuildPackageErrorsSimpleTableBinder.class );

    private MultiSelectionModel<BuilderResultLine>     selectionModel;

    public BuildPackageErrorsSimpleTable(ClientFactory clientFactory) {
        super();
        this.clientFactory = clientFactory;
    }

    @Override
    protected void doCellTable() {

        ProvidesKey<BuilderResultLine> providesKey = new ProvidesKey<BuilderResultLine>() {
            public Object getKey(BuilderResultLine row) {
                return row.getUuid();
            }
        };

        cellTable = new CellTable<BuilderResultLine>( providesKey );
        selectionModel = new MultiSelectionModel<BuilderResultLine>( providesKey );
        cellTable.setSelectionModel( selectionModel );
        SelectionColumn.createAndAddSelectionColumn( cellTable );

        ColumnPicker<BuilderResultLine> columnPicker = new ColumnPicker<BuilderResultLine>( cellTable );
        SortableHeaderGroup<BuilderResultLine> sortableHeaderGroup = new SortableHeaderGroup<BuilderResultLine>( cellTable );

        // Add any additional columns
        addAncillaryColumns( columnPicker,
                             sortableHeaderGroup );

        cellTable.setWidth( "100%" );
        columnPickerButton = columnPicker.createToggleButton();

    }

    @Override
    protected void addAncillaryColumns(ColumnPicker<BuilderResultLine> columnPicker,
                                       SortableHeaderGroup<BuilderResultLine> sortableHeaderGroup) {

        Column<BuilderResultLine, String> uuidColumn = new TextColumn<BuilderResultLine>() {
            public String getValue(BuilderResultLine row) {
                return row.getUuid();
            }
        };
        columnPicker.addColumn( uuidColumn,
                                new SortableHeader<BuilderResultLine, String>(
                                                                               sortableHeaderGroup,
                                                                               Constants.INSTANCE.uuid(),
                                                                               uuidColumn ),
                                false );

        Column<BuilderResultLine, String> assetNameColumn = new TextColumn<BuilderResultLine>() {
            public String getValue(BuilderResultLine row) {
                return row.getAssetName();
            }
        };
        columnPicker.addColumn( assetNameColumn,
                                new SortableHeader<BuilderResultLine, String>( sortableHeaderGroup,
                                                                               Constants.INSTANCE.Name(),
                                                                               assetNameColumn ),
                                true );

        Column<BuilderResultLine, ComparableImageResource> formatColumn = new Column<BuilderResultLine, ComparableImageResource>( new ComparableImageResourceCell() ) {

            public ComparableImageResource getValue(BuilderResultLine row) {
                AssetEditorFactory factory = clientFactory.getAssetEditorFactory();
                return new ComparableImageResource(row.getAssetFormat(), factory.getAssetEditorIcon(row.getAssetFormat()));
            }
        };
        columnPicker.addColumn( formatColumn,
                                new SortableHeader<BuilderResultLine, ComparableImageResource>(
                                                                                                sortableHeaderGroup,
                                                                                                Constants.INSTANCE.Format(),
                                                                                                formatColumn ),
                                true );

        Column<BuilderResultLine, String> messageColumn = new TextColumn<BuilderResultLine>() {
            public String getValue(BuilderResultLine row) {
                return row.getMessage();
            }
        };
        columnPicker.addColumn( messageColumn,
                                new SortableHeader<BuilderResultLine, String>( sortableHeaderGroup,
                                                                               Constants.INSTANCE.Message1(),
                                                                               messageColumn ),
                                true );

        // Add "Open" button column
        Column<BuilderResultLine, String> openColumn = new Column<BuilderResultLine, String>( new ButtonCell() ) {
            public String getValue(BuilderResultLine row) {
                return Constants.INSTANCE.Open();
            }
        };
        openColumn.setFieldUpdater( new FieldUpdater<BuilderResultLine, String>() {
            public void update(int index,
                               BuilderResultLine row,
                               String value) {
                clientFactory.getDeprecatedPlaceController().goTo( new AssetEditorPlace( row.getUuid() ));
            }
        } );
        columnPicker.addColumn( openColumn,
                                new TextHeader( Constants.INSTANCE.Open() ),
                                true );

    }

    @Override
    protected Widget makeWidget() {
        return uiBinder.createAndBindUi( this );
    }

    @UiHandler("openSelectedButton")
    void openSelected(ClickEvent e) {
        Set<BuilderResultLine> selectedSet = selectionModel.getSelectedSet();
        for ( BuilderResultLine selected : selectedSet ) {
            clientFactory.getDeprecatedPlaceController().goTo( new AssetEditorPlace( selected.getUuid() ));
        }
    }

}
