/*
 * Copyright 2013 JBoss by Red Hat.
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

package org.guvnor.udc.client.info;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.guvnor.udc.client.i8n.Constants;
import org.guvnor.udc.client.util.ResizableHeader;
import org.guvnor.udc.model.InfoUsageDataSummary;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.workbench.events.NotificationEvent;

import com.github.gwtbootstrap.client.ui.DataGrid;
import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.SimplePager;
import com.github.gwtbootstrap.client.ui.base.IconAnchor;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;

@Dependent
@Templated(value = "InfoUsageDataViewImpl.html")
public class InfoUsageDataViewImpl extends Composite implements InfoUsageDataPresenter.InfoUsageDataEventView {
    
    private Constants constants = GWT.create(Constants.class);

    @Inject
    private Event<NotificationEvent> notification;

    @Inject
    @DataField
    public FlowPanel modulesViewContainer;
    
    public DataGrid<InfoUsageDataSummary> modulesAuditedListGrid;
    
    @Inject
    @DataField
    public FlowPanel infoViewContainer;
    
    public DataGrid<InfoUsageDataSummary> infoListGrid;
    
    @Inject
    @DataField
    public IconAnchor refreshIcon;
    
    private InfoUsageDataPresenter presenter;
    
    public SimplePager pager;


    @Override
    public void init(InfoUsageDataPresenter presenter) {
        this.presenter = presenter;
        initializeInfoGridView();
        initializeModuleGridView();
        refreshIcon.setTitle(constants.Refresh());
        refreshIcon.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
            	refreshGridsInfo();
            }
        });
    }

    private void initializeInfoGridView() {
        infoViewContainer.clear();
        infoListGrid = new DataGrid<InfoUsageDataSummary>();
        infoListGrid.setStyleName("table table-bordered table-striped table-hover");
        infoViewContainer.add(infoListGrid);
        infoListGrid.setHeight("200px");
        initInfoColumns();
        presenter.addInfoDataDisplay(infoListGrid);
    }
    
    private void initializeModuleGridView() {
        modulesViewContainer.clear();
        modulesAuditedListGrid = new DataGrid<InfoUsageDataSummary>();
        modulesAuditedListGrid.setStyleName("table table-bordered table-striped table-hover");
        pager = new SimplePager();
        pager.setStyleName("pagination pagination-right pull-right");
        pager.setDisplay(modulesAuditedListGrid);
        pager.setPageSize(10);
        modulesViewContainer.add(modulesAuditedListGrid);
        modulesViewContainer.add(pager);
        modulesAuditedListGrid.setHeight("250px");
        modulesAuditedListGrid.setEmptyTableWidget(new Label(constants.No_Module_Audited()));
        initModuleColumns();
        presenter.addModuleDataDisplay(modulesAuditedListGrid);
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void initInfoColumns() {
        // Type.
        Column<InfoUsageDataSummary, String> typeNameColumn = new Column<InfoUsageDataSummary, String>(new TextCell()) {
            @Override
            public String getValue(InfoUsageDataSummary object) {
                return object.getComponent();
            }
        };
        infoListGrid.setColumnWidth(typeNameColumn, "160px");
        infoListGrid.addColumn(typeNameColumn, new ResizableHeader(constants.Type(), infoListGrid, typeNameColumn));

        // Description.
        Column<InfoUsageDataSummary, String> descriptionNameColumn = new Column<InfoUsageDataSummary, String>(new TextCell()) {
            @Override
            public String getValue(InfoUsageDataSummary object) {
                return object.getDescription();
            }
        };
        infoListGrid.addColumn(descriptionNameColumn, new ResizableHeader(constants.Description(), infoListGrid,
                descriptionNameColumn));

    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void initModuleColumns() {
        // Module.
        Column<InfoUsageDataSummary, String> moduleNameColumn = new Column<InfoUsageDataSummary, String>(new TextCell()) {
            @Override
            public String getValue(InfoUsageDataSummary object) {
                return object.getComponent();
            }
        };
        modulesAuditedListGrid.setColumnWidth(moduleNameColumn, "200px");
        modulesAuditedListGrid.addColumn(moduleNameColumn, new ResizableHeader(constants.Module(), modulesAuditedListGrid, moduleNameColumn));

        // Components.
        Column<InfoUsageDataSummary, String> componentsNameColumn = new Column<InfoUsageDataSummary, String>(new TextCell()) {
            @Override
            public String getValue(InfoUsageDataSummary object) {
                return object.getDescription();
            }
        };
        modulesAuditedListGrid.addColumn(componentsNameColumn, new ResizableHeader(constants.Component(), modulesAuditedListGrid,
                componentsNameColumn));

    }
    
    @Override
    public void refreshGridsInfo() {
    	presenter.refreshInfo();
        presenter.refreshModules();
    }

    @Override
    public void displayNotification(String text) {
        notification.fire(new NotificationEvent(text));
    }
    
}
