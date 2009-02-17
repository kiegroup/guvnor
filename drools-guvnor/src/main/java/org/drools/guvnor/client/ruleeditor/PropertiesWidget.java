package org.drools.guvnor.client.ruleeditor;
/*
 * Copyright 2005 JBoss Inc
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

import java.util.ArrayList;
import java.util.List;

import org.drools.guvnor.client.packages.AssetAttachmentFileWidget;
import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.guvnor.client.messages.Constants;

import com.gwtext.client.core.EventObject;
import com.gwtext.client.data.ArrayReader;
import com.gwtext.client.data.FieldDef;
import com.gwtext.client.data.MemoryProxy;
import com.gwtext.client.data.Record;
import com.gwtext.client.data.RecordDef;
import com.gwtext.client.data.Store;
import com.gwtext.client.data.StringFieldDef;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.Toolbar;
import com.gwtext.client.widgets.ToolbarButton;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.form.TextField;
import com.gwtext.client.widgets.grid.ColumnConfig;
import com.gwtext.client.widgets.grid.ColumnModel;
import com.gwtext.client.widgets.grid.EditorGridPanel;
import com.gwtext.client.widgets.grid.GridEditor;
import com.google.gwt.core.client.GWT;

/**
 * Properties (key/value pairs) editor with a file attachment.
 *
 * @author Anton Arhipov
 */
public class PropertiesWidget extends AssetAttachmentFileWidget implements SaveEventListener {

    PropertiesHolder properties;
    Store store;
    private Constants constants = ((Constants) GWT.create(Constants.class));

    public PropertiesWidget(final RuleAsset asset, final RuleViewer viewer) {
        super(asset, viewer);

        if (asset.content == null) {
            properties = new PropertiesHolder();
        } else {
            properties = (PropertiesHolder) asset.content;
        }

        Panel panel = new Panel();
        panel.setBorder(false);
        panel.setPaddings(15);

        final RecordDef recordDef = new RecordDef(
                new FieldDef[]{new StringFieldDef("key"), new StringFieldDef("value")}   //NON-NLS
        );

        String[][] data = new String[properties.list.size()][];
        int dataIndex = 0;
        for (PropertyHolder holder : properties.list) {
            data[dataIndex++] = new String[]{holder.name, holder.value};
        }

        MemoryProxy proxy = new MemoryProxy(data);
        store = new Store(proxy, new ArrayReader(recordDef));
        store.load();

        ColumnConfig keyCol = new ColumnConfig("Key?", "key", 100, true, null, "key");    //NON-NLS
        keyCol.setEditor(new GridEditor(new TextField()));
        keyCol.setFixed(false);

        ColumnConfig valueCol = new ColumnConfig("Value?", "value", 100, true, null, "value"); //NON-NLS
        valueCol.setEditor(new GridEditor(new TextField()));
        valueCol.setFixed(false);

        ColumnConfig[] columnConfigs = {keyCol, valueCol};

        ColumnModel columnModel = new ColumnModel(columnConfigs);
        columnModel.setDefaultSortable(true);

        final EditorGridPanel grid = new EditorGridPanel();

        Toolbar toolbar = new Toolbar();
        ToolbarButton add = new ToolbarButton(constants.Add(), new ButtonListenerAdapter() {
            public void onClick(Button button, EventObject e) {
                addNewField(recordDef, grid);
            }
        });

        toolbar.addButton(add);

        /*ToolbarButton delete = new ToolbarButton("Delete", new ButtonListenerAdapter() {
            public void onClick(Button button, EventObject e) {
                store.remove(store.getRecordAt(grid.getPosition()[1]));
                if(store.getTotalCount() == 0){
                    addNewField(recordDef, grid);
                }
            }
        });

        toolbar.addButton(delete);*/

        ToolbarButton clear = new ToolbarButton(constants.Clear(), new ButtonListenerAdapter() {
            public void onClick(Button button, EventObject e) {
                store.removeAll();
                addNewField(recordDef, grid);
            }
        });

        toolbar.addButton(clear);

        grid.setStore(store);
        grid.setColumnModel(columnModel);
        grid.setWidth(215);
        grid.setHeight(300);
        grid.setTitle(constants.Properties());
        grid.setFrame(true);
        grid.setClicksToEdit(2);
        grid.setTopToolbar(toolbar);

        panel.add(grid);

        layout.addRow(grid);
    }

    private void addNewField(RecordDef recordDef, EditorGridPanel grid) {
        Record pair = recordDef.createRecord(new Object[]{"", ""});
        grid.stopEditing();
        store.insert(0, pair);
        grid.startEditing(0, 0);
    }


    public String getIcon() {
        return "";       //TODO: set correct icon
    }

    public String getOverallStyleName() {
        return "";       //TODO: set correct style
    }

    public void onSave() {
        final List<PropertyHolder> result = new ArrayList<PropertyHolder>();

        Record[] records = store.getRecords();
        for (Record record : records) {
            String key = record.getAsString("key"); //NON-NLS
            if (key != null && !"".equals(key)) {
                result.add(new PropertyHolder(key, record.getAsString("value")));
            }
        }

        properties.list = result;
        asset.content = properties;
    }

    public void onAfterSave() {

    }

}


