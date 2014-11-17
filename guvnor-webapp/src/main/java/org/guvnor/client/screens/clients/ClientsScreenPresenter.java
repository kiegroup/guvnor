/*
 * Copyright 2014 JBoss Inc
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

package org.guvnor.client.screens.clients;

import java.util.ArrayList;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.NavSearch;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import org.guvnor.messageconsole.client.console.HyperLinkCell;
import org.uberfire.ext.widgets.common.client.tables.SimpleTable;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;

@Dependent
@WorkbenchScreen(identifier = "clientsScreen")
public class ClientsScreenPresenter
        extends Composite {

    interface Binder
            extends
            UiBinder<Widget, ClientsScreenPresenter> {

    }

    private static Binder uiBinder = GWT.create(Binder.class);

    @UiField
    SimpleTable table;

    @Inject
    private PlaceManager placeManager;

    public ClientsScreenPresenter() {
        initWidget(uiBinder.createAndBindUi(this));

        TextBox textBox = new TextBox();
        textBox.setSearchQuery(true);
        textBox.setPlaceholder("Search...");
        table.getLeftToolbar().add(textBox);

        Column<String, HyperLinkCell.HyperLink> column = new Column<String, HyperLinkCell.HyperLink>(new HyperLinkCell()) {
            @Override
            public HyperLinkCell.HyperLink getValue(String object) {
                return HyperLinkCell.HyperLink.newLink("third-party");
            }
        };
        table.addColumn(column, "OAuth Client Name");
        table.addColumn(new Column<String, String>(new TextCell()) {
            @Override
            public String getValue(String object) {
                return "true";
            }
        }, "Enabled");

        column.setFieldUpdater(new FieldUpdater<String, HyperLinkCell.HyperLink>() {
            @Override
            public void update(final int index,
                    final String row,
                    final HyperLinkCell.HyperLink value) {
                placeManager.goTo("oauthClientSettingsScreen");
            }
        });

        ListDataProvider<String> dataProvider = new ListDataProvider<String>();

        dataProvider.addDataDisplay(table);
        ArrayList<String> list = new ArrayList<String>();
        list.add("mock");
        dataProvider.setList(list);
    }

    @WorkbenchPartView
    public Widget getWidget() {
        return this;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Clients";
    }
}
