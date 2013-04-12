/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.guvnor.client.asseteditor.drools.enums;

import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.ListDataProvider;
import org.drools.guvnor.client.asseteditor.EditorWidget;
import org.drools.guvnor.client.asseteditor.RuleViewer;
import org.drools.guvnor.client.asseteditor.SaveCommand;
import org.drools.guvnor.client.asseteditor.SaveEventListener;
import org.drools.guvnor.client.common.DirtyableComposite;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.rpc.Asset;
import org.drools.guvnor.client.rpc.RuleContentText;

/**
 * This is the default rule editor widget (just text editor based) - more to come later.
 */
public class EnumEditor extends DirtyableComposite implements EditorWidget,
                                                              SaveEventListener {

    private VerticalPanel panel;

    private CellTable<EnumRow> cellTable;

    final private RuleContentText data;
    private ListDataProvider<EnumRow> dataProvider = new ListDataProvider<EnumRow>();

    public EnumEditor( Asset a,
                       RuleViewer v,
                       ClientFactory clientFactory,
                       EventBus eventBus ) {
        this( a );
    }

    public EnumEditor( Asset a ) {
        this( a,
              -1 );
    }

    public EnumEditor( Asset a,
                       int visibleLines ) {
        data = (RuleContentText) a.getContent();

        if ( data.content == null ) {
            data.content = "";
        }

        cellTable = new CellTable<EnumRow>();
        cellTable.setWidth( "100%" );
        panel = new VerticalPanel();

        final CellTable<EnumRow> cellTable = new CellTable<EnumRow>( Integer.MAX_VALUE );
        cellTable.setWidth( "100%" );

        //Column definitions
        final DeleteButtonCell deleteButton = new DeleteButtonCell();
        final Column<EnumRow, String> deleteButtonColumn = new Column<EnumRow, String>( deleteButton ) {
            @Override
            public String getValue( final EnumRow enumRow ) {
                return "";
            }
        };
        final Column<EnumRow, String> factNameColumn = new Column<EnumRow, String>( new EditTextCell() ) {
            @Override
            public String getValue( final EnumRow enumRow ) {
                return enumRow.getFactName();
            }
        };
        final Column<EnumRow, String> fieldNameColumn = new Column<EnumRow, String>( new EditTextCell() ) {
            @Override
            public String getValue( final EnumRow enumRow ) {
                return enumRow.getFieldName();
            }
        };
        final Column<EnumRow, String> contextColumn = new Column<EnumRow, String>( new EditTextCell() ) {
            @Override
            public String getValue( final EnumRow enumRow ) {
                return enumRow.getContext();
            }
        };

        //Write updates back to the model
        deleteButtonColumn.setFieldUpdater( new FieldUpdater<EnumRow, String>() {
            @Override
            public void update( final int index,
                                final EnumRow object,
                                final String value ) {
                dataProvider.getList().remove( index );
            }
        } );
        factNameColumn.setFieldUpdater( new FieldUpdater<EnumRow, String>() {
            @Override
            public void update( final int index,
                                final EnumRow object,
                                final String value ) {
                object.setFactName( value );
            }
        } );
        fieldNameColumn.setFieldUpdater( new FieldUpdater<EnumRow, String>() {
            @Override
            public void update( final int index,
                                final EnumRow object,
                                final String value ) {
                object.setFieldName( value );
            }
        } );
        contextColumn.setFieldUpdater( new FieldUpdater<EnumRow, String>() {
            @Override
            public void update( final int index,
                                final EnumRow object,
                                final String value ) {
                object.setContext( value );
            }
        } );

        cellTable.addColumn( deleteButtonColumn );
        cellTable.addColumn( factNameColumn,
                             "Fact" );
        cellTable.addColumn( fieldNameColumn,
                             "Field" );
        cellTable.addColumn( contextColumn,
                             "Context" );

        // Connect the table to the data provider.
        dataProvider.setList( EnumParser.parseEnums( data.content ) );
        dataProvider.addDataDisplay( cellTable );

        final Button addButton = new Button( "+",
                                             new ClickHandler() {
                                                 public void onClick( ClickEvent clickEvent ) {
                                                     final EnumRow enumRow = new EnumRow();
                                                     dataProvider.getList().add( enumRow );
                                                 }
                                             } );

        panel.add( addButton );
        panel.add( cellTable );

        initWidget( panel );
    }

    public void onSave( SaveCommand saveCommand ) {
        data.content = "";

        if ( !dataProvider.getList().isEmpty() ) {
            final StringBuilder sb = new StringBuilder();
            for ( final EnumRow enumRow : dataProvider.getList() ) {
                if ( enumRow.isValid() ) {
                    sb.append( enumRow.toString() ).append( "\n" );
                }
            }
            data.content = sb.toString();
        }

        saveCommand.save();
    }

    public void onAfterSave() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

}