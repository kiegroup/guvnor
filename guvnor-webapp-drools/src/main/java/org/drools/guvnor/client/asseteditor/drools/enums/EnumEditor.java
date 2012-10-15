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
public class EnumEditor extends DirtyableComposite implements EditorWidget,SaveEventListener {



    private VerticalPanel panel;

    private CellTable cellTable;
    /*private Column<EnumRow, String> column = new Column<EnumRow, String>(new EditTextCell()) {


        @Override
        public String getValue(EnumRow enumRow) {
            return enumRow.getText();
        }
    } ; */




    final private RuleContentText data;
    private ListDataProvider<EnumRow> dataProvider = new ListDataProvider<EnumRow>();


    public EnumEditor(Asset a,
                      RuleViewer v,
                      ClientFactory clientFactory,
                      EventBus eventBus) {
        this(a);
    }

    public EnumEditor(Asset a) {
        this(a,
                -1);
    }

    public EnumEditor(Asset a,
                      int visibleLines) {
        data = (RuleContentText) a.getContent();

        if (data.content == null) {
            data.content = "";
        }

        cellTable = new CellTable<EnumRow>();
        cellTable.setWidth("100%");





        panel = new VerticalPanel();


        String[] array = data.content.split("\n");

        for(String line: array){
            EnumRow enumRow = new EnumRow(line);

            dataProvider.getList().add(enumRow);
        }

        DeleteButtonCell deleteButton= new DeleteButtonCell();
        Column <EnumRow,String> delete= new Column <EnumRow,String>(deleteButton)
        {
            @Override
            public String getValue(EnumRow enumRow1)
            {
                return "";
            }
        };

         Column<EnumRow,String> columnFirst = new Column<EnumRow, String>(new EditTextCell()) {


            @Override
            public String getValue(EnumRow enumRow) {
                return enumRow.getFactName();
            }
        } ;
        Column<EnumRow,String> columnSecond = new Column<EnumRow, String>(new EditTextCell()) {


            @Override
            public String getValue(EnumRow enumRow) {
                return enumRow.getFieldName();
            }
        } ;
        Column<EnumRow,String> columnThird = new Column<EnumRow, String>(new EditTextCell()) {


            @Override
            public String getValue(EnumRow enumRow) {
                return enumRow.getContext();
            }
        } ;
        columnFirst.setFieldUpdater(new FieldUpdater<EnumRow, String>() {

            public void update(int index, EnumRow object, String value) {
               object.setFactName(value);

            }
        });
        columnSecond.setFieldUpdater(new FieldUpdater<EnumRow, String>() {

            public void update(int index, EnumRow object, String value) {

                object.setFieldName(value);

            }
        });
        columnThird.setFieldUpdater(new FieldUpdater<EnumRow, String>() {

            public void update(int index, EnumRow object, String value) {

                object.setContext(value);
            }
        });

        cellTable.addColumn(delete);
        cellTable.addColumn(columnFirst, "Fact");
        cellTable.addColumn(columnSecond, "Field");
        cellTable.addColumn(columnThird, "Context");

        // Connect the table to the data provider.
        dataProvider.addDataDisplay(cellTable);



        delete.setFieldUpdater(new FieldUpdater<EnumRow, String>() {

            public void update(int index, EnumRow object, String value) {
                dataProvider.getList().remove(object);
            }
        });

        Button addButton = new Button("+", new ClickHandler() {
            public void onClick(ClickEvent clickEvent) {
                EnumRow enumRow = new EnumRow("");
                dataProvider.getList().add(enumRow);
            }
        });



        panel.add(cellTable);
        panel.add(addButton);
        initWidget(panel);

    }



    public void onSave(SaveCommand saveCommand) {
        data.content = "";


        for(EnumRow enumRow : dataProvider.getList()){
                data.content += enumRow.getText() + "\n";

        }

        saveCommand.save();
    }

    public void onAfterSave() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

}