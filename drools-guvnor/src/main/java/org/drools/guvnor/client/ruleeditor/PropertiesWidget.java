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

import com.gwtext.client.core.EventObject;
import com.gwtext.client.data.*;
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
import org.drools.guvnor.client.packages.AssetAttachmentFileWidget;
import org.drools.guvnor.client.rpc.RuleAsset;

import java.util.ArrayList;
import java.util.List;

/**
 * Properties (key/value pairs) editor with a file attachment.
 *
 * @author Anton Arhipov
 */
public class PropertiesWidget extends AssetAttachmentFileWidget implements SaveEventListener {

    PropertiesHolder properties;
    Store store;

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
                new FieldDef[]{new StringFieldDef("key"), new StringFieldDef("value")}
        );

        String[][] data = new String[properties.list.size()][];
        int dataIndex = 0;
        for (PropertyHolder holder : properties.list) {
            data[dataIndex++] = new String[]{holder.name, holder.value};
        }

        MemoryProxy proxy = new MemoryProxy(data);
        store = new Store(proxy, new ArrayReader(recordDef));
        store.load();

        ColumnConfig keyCol = new ColumnConfig("Key?", "key", 100, true, null, "key");
        keyCol.setEditor(new GridEditor(new TextField()));
        keyCol.setFixed(false);

        ColumnConfig valueCol = new ColumnConfig("Value?", "value", 100, true, null, "value");
        valueCol.setEditor(new GridEditor(new TextField()));
        valueCol.setFixed(false);

        ColumnConfig[] columnConfigs = {keyCol, valueCol};

        ColumnModel columnModel = new ColumnModel(columnConfigs);
        columnModel.setDefaultSortable(true);

        final EditorGridPanel grid = new EditorGridPanel();

        Toolbar toolbar = new Toolbar();
        ToolbarButton button = new ToolbarButton("Add ...", new ButtonListenerAdapter() {
            public void onClick(Button button, EventObject e) {
                Record pair = recordDef.createRecord(new Object[]{"", ""});
                grid.stopEditing();
                store.insert(0, pair);
                grid.startEditing(0, 0);
            }
        });
        toolbar.addButton(button);

        grid.setStore(store);
        grid.setColumnModel(columnModel);
        grid.setWidth(215);
        grid.setHeight(300);
        grid.setTitle("Properties");
        grid.setFrame(true);
        grid.setClicksToEdit(1);
        grid.setTopToolbar(toolbar);

        panel.add(grid);

        layout.addRow(grid);
    }


    public String getIcon() {
        return "";       //TODO: set correct icon
    }

    public String getOverallStyleName() {
        return "";       //TODOL set correct style
    }

    public void onSave() {
        final List<PropertyHolder> result = new ArrayList<PropertyHolder>();

        Record[] records = store.getRecords();
        for (Record record : records) {
            result.add(new PropertyHolder(record.getAsString("key"), record.getAsString("value")));
        }

        properties.list = result;
        asset.content = properties;
    }

    public void onAfterSave() {

    }

}


