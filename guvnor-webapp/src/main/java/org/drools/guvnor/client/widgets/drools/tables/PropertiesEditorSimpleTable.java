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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.drools.guvnor.client.asseteditor.PropertyHolder;
import org.drools.guvnor.client.decisiontable.cells.PopupTextEditCell;
import org.drools.guvnor.client.rpc.AbstractPageRow;
import org.drools.guvnor.client.widgets.drools.tables.PropertiesEditorSimpleTable.PropertyHolderAdaptor;
import org.drools.guvnor.client.widgets.tables.AbstractSimpleTable;
import org.drools.guvnor.client.widgets.tables.ColumnPicker;
import org.drools.guvnor.client.widgets.tables.SelectionColumn;
import org.drools.guvnor.client.widgets.tables.SortableHeader;
import org.drools.guvnor.client.widgets.tables.SortableHeaderGroup;

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.ProvidesKey;

/**
 * Widget with a table of Properties that can be edited.
 */
public class PropertiesEditorSimpleTable extends AbstractSimpleTable<PropertyHolderAdaptor> {

    // UI
    interface PropertiesEditorSimpleTableBinder
        extends
        UiBinder<Widget, PropertiesEditorSimpleTable> {
    }

    private MultiSelectionModel<PropertyHolderAdaptor> selectionModel;

    private static PropertiesEditorSimpleTableBinder   uiBinder = GWT.create( PropertiesEditorSimpleTableBinder.class );

    @UiField()
    Button                                             addPropertyButton;

    @UiField()
    Button                                             deleteSelectedPropertiesButton;
  
    // Wrapper class to allow re-use of AbstractSimpleTable. Changing
    // PropertyHolder to extend AbstractPageRow leads to de-serialisation
    // errors of existing assets
    static class PropertyHolderAdaptor extends AbstractPageRow {

        static long            counter = 0;

        private long           index;
        private PropertyHolder ph;

        private PropertyHolderAdaptor(PropertyHolder ph) {
            this.ph = ph;
            synchronized ( this ) {
                index = counter++;
            }
        }

        long getIndex() {
            return this.index;
        }

        String getName() {
            return ph.getName();
        }

        String getValue() {
            return this.ph.getValue();
        }

        void setName(String name) {
            this.ph.setName( name );
        }

        void setValue(String value) {
            this.ph.setValue( value );
        }

    }

    // Adapted PropertyHolders for the UI
    private List<PropertyHolderAdaptor> adaptedProperties = new ArrayList<PropertyHolderAdaptor>();

    /**
     * Constructor
     * 
     * @param properties
     *            Properties to include in the UI
     */
    public PropertiesEditorSimpleTable(List<PropertyHolder> properties) {
        super();
        this.adaptedProperties = adaptPropertyHolders( properties );
        this.setRowData( this.adaptedProperties );
        this.setRowCount( this.adaptedProperties.size() );
    }

    /**
     * Scrape the properties from the UI into a List suitable for persisting
     * 
     * @return
     */
    public List<PropertyHolder> getPropertyHolders() {
        List<PropertyHolder> properties = new ArrayList<PropertyHolder>();
        for ( PropertyHolderAdaptor pha : this.adaptedProperties ) {
            properties.add( new PropertyHolder( pha.getName(),
                                                pha.getValue() ) );
        }
        return properties;
    }

    private List<PropertyHolderAdaptor> adaptPropertyHolders(List<PropertyHolder> properties) {
        List<PropertyHolderAdaptor> adaptedProperties = new ArrayList<PropertyHolderAdaptor>();
        for ( PropertyHolder ph : properties ) {
            adaptedProperties.add( new PropertyHolderAdaptor( ph ) );
        }
        return adaptedProperties;
    }

    @Override
    protected void addAncillaryColumns(ColumnPicker<PropertyHolderAdaptor> columnPicker,
                                       SortableHeaderGroup<PropertyHolderAdaptor> sortableHeaderGroup) {

        Column<PropertyHolderAdaptor, String> propertyNameColumn = new Column<PropertyHolderAdaptor, String>( new PopupTextEditCell() ) {

            @Override
            public String getValue(PropertyHolderAdaptor object) {
                return object.getName();
            }

        };
        propertyNameColumn.setFieldUpdater( new FieldUpdater<PropertyHolderAdaptor, String>() {

            public void update(int index,
                               PropertyHolderAdaptor object,
                               String value) {
                object.setName( value );
            }

        } );
        columnPicker.addColumn( propertyNameColumn,
                                new SortableHeader<PropertyHolderAdaptor, String>(
                                                                                   sortableHeaderGroup,
                                                                                   constants.Item(),
                                                                                   propertyNameColumn ),
                                true );

        Column<PropertyHolderAdaptor, String> propertyValueColumn = new Column<PropertyHolderAdaptor, String>( new PopupTextEditCell() ) {

            @Override
            public String getValue(PropertyHolderAdaptor object) {
                return object.getValue();
            }

        };
        propertyValueColumn.setFieldUpdater( new FieldUpdater<PropertyHolderAdaptor, String>() {

            public void update(int index,
                               PropertyHolderAdaptor object,
                               String value) {
                object.setValue( value );
            }

        } );
        columnPicker.addColumn( propertyValueColumn,
                                new SortableHeader<PropertyHolderAdaptor, String>(
                                                                                   sortableHeaderGroup,
                                                                                   constants.Value(),
                                                                                   propertyValueColumn ),
                                true );

    }

    @Override
    protected void doCellTable() {

        ProvidesKey<PropertyHolderAdaptor> providesKey = new ProvidesKey<PropertyHolderAdaptor>() {
            public Object getKey(PropertyHolderAdaptor row) {
                return row.getIndex();
            }
        };

        cellTable = new CellTable<PropertyHolderAdaptor>( providesKey );
        selectionModel = new MultiSelectionModel<PropertyHolderAdaptor>( providesKey );
        cellTable.setSelectionModel( selectionModel );
        SelectionColumn.createAndAddSelectionColumn( cellTable );

        ColumnPicker<PropertyHolderAdaptor> columnPicker = new ColumnPicker<PropertyHolderAdaptor>( cellTable );
        SortableHeaderGroup<PropertyHolderAdaptor> sortableHeaderGroup = new SortableHeaderGroup<PropertyHolderAdaptor>( cellTable );

        // Add any additional columns
        addAncillaryColumns( columnPicker,
                             sortableHeaderGroup );

        cellTable.setWidth( "100%" );
        columnPickerButton = columnPicker.createToggleButton();

    }

    @Override
    protected Widget makeWidget() {
        return uiBinder.createAndBindUi( this );
    }

    @UiHandler("addPropertyButton")
    void addProperty(ClickEvent event) {
        this.adaptedProperties.add( new PropertyHolderAdaptor( new PropertyHolder( "",
                                                                                   "" ) ) );
        cellTable.setRowData( this.adaptedProperties );
        cellTable.setRowCount( this.adaptedProperties.size() );
    }

    @UiHandler("deleteSelectedPropertiesButton")
    void deleteSelectedProperties(ClickEvent event) {
        Set<PropertyHolderAdaptor> selectedProperties = selectionModel.getSelectedSet();
        for ( PropertyHolderAdaptor pha : selectedProperties ) {
            this.adaptedProperties.remove( pha );
        }
        selectionModel.clear();
        cellTable.setRowData( adaptedProperties );
        cellTable.setRowCount( adaptedProperties.size() );
    }

}
