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

package org.kie.guvnor.enums.client.editor;

import javax.annotation.PostConstruct;

import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.ListDataProvider;
import org.kie.guvnor.commons.ui.client.resources.i18n.CommonConstants;
import org.kie.guvnor.enums.client.widget.DeleteButtonCellWidget;

public class EnumEditorView
        extends Composite
        implements EnumEditorPresenter.View {

    private final ListDataProvider<EnumRow> dataProvider = new ListDataProvider<EnumRow>();

    private boolean isDirty = false;

    @PostConstruct
    public void init() {
        final CellTable cellTable = new CellTable<EnumRow>();
        cellTable.setWidth( "100%" );

        final VerticalPanel panel = new VerticalPanel();

        final DeleteButtonCellWidget deleteButton = new DeleteButtonCellWidget();

        final Column<EnumRow, String> delete = new Column<EnumRow, String>( deleteButton ) {
            @Override
            public String getValue( final EnumRow enumRow1 ) {
                return "";
            }
        };

        final Column<EnumRow, String> columnFirst = new Column<EnumRow, String>( new EditTextCell() ) {

            @Override
            public String getValue( final EnumRow enumRow ) {
                return enumRow.getFactName();
            }
        };
        final Column<EnumRow, String> columnSecond = new Column<EnumRow, String>( new EditTextCell() ) {

            @Override
            public String getValue( final EnumRow enumRow ) {
                return enumRow.getFieldName();
            }
        };
        final Column<EnumRow, String> columnThird = new Column<EnumRow, String>( new EditTextCell() ) {

            @Override
            public String getValue( final EnumRow enumRow ) {
                return enumRow.getContext();
            }
        };

        columnFirst.setFieldUpdater( new FieldUpdater<EnumRow, String>() {

            public void update( final int index,
                                final EnumRow object,
                                final String value ) {
                isDirty = true;
                object.setFactName( value );

            }
        } );

        columnSecond.setFieldUpdater( new FieldUpdater<EnumRow, String>() {

            public void update( final int index,
                                final EnumRow object,
                                final String value ) {

                isDirty = true;
                object.setFieldName( value );

            }
        } );

        columnThird.setFieldUpdater( new FieldUpdater<EnumRow, String>() {

            public void update( final int index,
                                final EnumRow object,
                                final String value ) {
                isDirty = true;
                object.setContext( value );
            }
        } );

        cellTable.addColumn( delete );
        cellTable.addColumn( columnFirst,
                             "Fact" );
        cellTable.addColumn( columnSecond,
                             "Field" );
        cellTable.addColumn( columnThird,
                             "Context" );

        // Connect the table to the data provider.
        dataProvider.addDataDisplay( cellTable );

        delete.setFieldUpdater( new FieldUpdater<EnumRow, String>() {

            public void update( int index,
                                EnumRow object,
                                String value ) {
                isDirty = true;
                dataProvider.getList().remove( index );
            }
        } );

        final Button addButton = new Button( "+", new ClickHandler() {
            public void onClick( ClickEvent clickEvent ) {
                isDirty = true;
                EnumRow enumRow = new EnumRow( "" );
                dataProvider.getList().add( enumRow );
            }
        } );

        panel.add( cellTable );
        panel.add( addButton );

        initWidget( panel );
    }

    @Override
    public void setContent( final String input ) {
        if ( input == null || input.isEmpty() ) {
            return;
        }

        final String[] lines = input.split( "\n" );
        for ( final String line : lines ) {
            final EnumRow enumRow = new EnumRow( line );
            dataProvider.getList().add( enumRow );
        }
    }

    @Override
    public String getContent() {
        if ( dataProvider.getList().isEmpty() ) {
            return "";
        }

        final StringBuilder sb = new StringBuilder();
        for ( final EnumRow enumRow : dataProvider.getList() ) {
            sb.append( enumRow.getText() ).append( '\n' );
        }
        return sb.toString();
    }

    @Override
    public boolean isDirty() {
        return isDirty;
    }

    @Override
    public void setNotDirty() {
        this.isDirty = false;
    }

    @Override
    public boolean confirmClose() {
        return Window.confirm( CommonConstants.INSTANCE.DiscardUnsavedData() );
    }
}