/*
 * Copyright 2013 JBoss Inc
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

package org.guvnor.udc.client.usagelist;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.guvnor.udc.client.i8n.Constants;
import org.guvnor.udc.client.resources.UsageDataImages;
import org.guvnor.udc.client.util.ResizableHeader;
import org.guvnor.udc.client.util.UtilUsageData;
import org.guvnor.udc.model.EventTypes;
import org.guvnor.udc.model.UsageEventSummary;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.workbench.events.NotificationEvent;

import com.github.gwtbootstrap.client.ui.DataGrid;
import com.github.gwtbootstrap.client.ui.Heading;
import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.github.gwtbootstrap.client.ui.NavLink;
import com.github.gwtbootstrap.client.ui.SimplePager;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.base.IconAnchor;
import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.CompositeCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.HasCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.SelectionModel;

@Dependent
@Templated(value = "UsageDataListViewImpl.html")
public class UsageDataListViewImpl extends Composite implements UsageDataPresenter.UsageDataView {
    
    private Constants constants = GWT.create(Constants.class);
    
    private UsageDataImages images = GWT.create(UsageDataImages.class);

    @Inject
    @DataField
    public NavLink clearEventsNavLink;

    @Inject
    @DataField
    public NavLink exportEventsNavLink;

    @Inject
    @DataField
    public TextBox searchBox;

    @Inject
    @DataField
    public NavLink showInfoEventsNavLink;
    
    @Inject
    @DataField
    public NavLink showInfoGFSNavLink;
    

    @DataField
    public Heading usageDataViewLabel = new Heading(4);

    @Inject
    @DataField
    public FlowPanel eventsViewContainer;

    @Inject
    @DataField
    public IconAnchor refreshIcon;
    
    @Inject
    @DataField
    public ListBox eventTypesList;

    @Inject
    private Event<NotificationEvent> notification;
    
    @Inject
    private PlaceManager placeManager;

    private UsageDataPresenter presenter;

    public DataGrid<UsageEventSummary> myEventListGrid;

    public SimplePager pager;

    private ListHandler<UsageEventSummary> sortHandler;

    private MultiSelectionModel<UsageEventSummary> selectionModel;
    
    @Override
    public void init(UsageDataPresenter presenter) {
        this.presenter = presenter;

        refreshIcon.setTitle(constants.Refresh());
        refreshIcon.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                refreshUsageDataCollector();
                searchBox.setText("");
                displayNotification(constants.Events_Refreshed());
            }
        });

        // By Default we will start in Grid View
        initializeGridView();

        clearEventsNavLink.setText(constants.Clear());
        clearEventsNavLink.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                clearEventsNavLink.setStyleName("active");
                showInfoEventsNavLink.setStyleName("");
                showInfoGFSNavLink.setStyleName("");
                exportEventsNavLink.setStyleName("");
                clearUsageData();
            }
        });

        showInfoEventsNavLink.setText(constants.Info());
        showInfoEventsNavLink.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                showInfoEventsNavLink.setStyleName("active");
                showInfoGFSNavLink.setStyleName("");
                clearEventsNavLink.setStyleName("");
                exportEventsNavLink.setStyleName("");
                showInfoUsageData();
            }
        });
        
        showInfoGFSNavLink.setText(constants.Gfs_info());
        showInfoGFSNavLink.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                showInfoGFSNavLink.setStyleName("active");
                showInfoEventsNavLink.setStyleName("");
                clearEventsNavLink.setStyleName("");
                exportEventsNavLink.setStyleName("");
                showInfoGFS();
            }
        });

        exportEventsNavLink.setText(constants.Export_Csv());
        exportEventsNavLink.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                showInfoEventsNavLink.setStyleName("");
                showInfoGFSNavLink.setStyleName("");
                clearEventsNavLink.setStyleName("");
                exportEventsNavLink.setStyleName("active");
                exportCsvEvents();
            }
        });

        usageDataViewLabel.setText(constants.List_Usage_Data());
        usageDataViewLabel.setStyleName("");

        searchBox.addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent event) {
                if (event.getNativeKeyCode() == 13 || event.getNativeKeyCode() == 32) {
                    displayNotification("Filter Event: |" + searchBox.getText() + "|");
                    searchEvents(searchBox.getText());
                }

            }
        });
        
        for ( EventTypes type : EventTypes.values() ) {
        	eventTypesList.addItem( type.getDescription(),  type.toString() );
        }
        eventTypesList.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				filterEventsByType(EventTypes.valueOf(eventTypesList.getValue()));
			}
		});
        
        refreshUsageDataCollector();
    }

    public void filterEventsByType(EventTypes eventType){
    	presenter.filterEventsByType(eventType);
    }
    
    public void searchEvents(String text) {
        presenter.searchEvents(text);
    }

    private void initializeGridView() {
        eventsViewContainer.clear();
        myEventListGrid = new DataGrid<UsageEventSummary>();
        myEventListGrid.setStyleName("table table-bordered table-striped table-hover");
        pager = new SimplePager();
        pager.setStyleName("pagination pagination-right pull-right");
        pager.setDisplay(myEventListGrid);
        pager.setPageSize(10);

        eventsViewContainer.add(myEventListGrid);
        eventsViewContainer.add(pager);

        myEventListGrid.setHeight("350px");
        // Set the message to display when the table is empty.
        myEventListGrid.setEmptyTableWidget(new Label(constants.No_Usage_Data()));

        // Attach a column sort handler to the ListDataProvider to sort the
        // list.
        sortHandler = new ColumnSortEvent.ListHandler<UsageEventSummary>(presenter.getAllEventsSummaries());

        myEventListGrid.addColumnSortHandler(sortHandler);

        myEventListGrid.setSelectionModel(selectionModel,
                DefaultSelectionEventManager.<UsageEventSummary> createCheckboxManager());

        initTableColumns(selectionModel);
        presenter.addDataDisplay(myEventListGrid);

    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void initTableColumns(final SelectionModel<UsageEventSummary> selectionModel) {
        // Timestamp.
        Column<UsageEventSummary, String> timeColumn = new Column<UsageEventSummary, String>(new TextCell()) {
            @Override
            public String getValue(UsageEventSummary object) {
                if (object.getTimestamp() != null) {
                    return UtilUsageData.getDateTime(object.getTimestamp(), UtilUsageData.patternDateTime);
                }
                return "";
            }
        };
        timeColumn.setSortable(true);
        myEventListGrid.setColumnWidth(timeColumn, "140px");

        myEventListGrid.addColumn(timeColumn, new ResizableHeader(constants.Time(), myEventListGrid, timeColumn));
        sortHandler.setComparator(timeColumn, new Comparator<UsageEventSummary>() {
            @Override
            public int compare(UsageEventSummary o1, UsageEventSummary o2) {
                if (o1.getTimestamp() == null || o2.getTimestamp() == null) {
                    return 0;
                }
                return o1.getTimestamp().compareTo(o2.getTimestamp());
            }
        });

        // Module.
        Column<UsageEventSummary, String> moduleNameColumn = new Column<UsageEventSummary, String>(new TextCell()) {
            @Override
            public String getValue(UsageEventSummary object) {
                return object.getModule();
            }
        };
        moduleNameColumn.setSortable(true);
        myEventListGrid.setColumnWidth(moduleNameColumn, "200px");

        myEventListGrid.addColumn(moduleNameColumn, new ResizableHeader(constants.Module(), myEventListGrid, moduleNameColumn));
        sortHandler.setComparator(moduleNameColumn, new Comparator<UsageEventSummary>() {
            @Override
            public int compare(UsageEventSummary o1, UsageEventSummary o2) {
                return o1.getModule().compareTo(o2.getModule());
            }
        });
        

        // User.
        Column<UsageEventSummary, String> userNameColumn = new Column<UsageEventSummary, String>(new TextCell()) {
            @Override
            public String getValue(UsageEventSummary object) {
                return object.getFrom();
            }
        };
        userNameColumn.setSortable(true);
        myEventListGrid.setColumnWidth(userNameColumn, "80px");

        myEventListGrid.addColumn(userNameColumn, new ResizableHeader(constants.User(), myEventListGrid, userNameColumn));
        sortHandler.setComparator(userNameColumn, new Comparator<UsageEventSummary>() {
            @Override
            public int compare(UsageEventSummary o1, UsageEventSummary o2) {
                return o1.getFrom().compareTo(o2.getFrom());
            }
        });

        // Component.
        Column<UsageEventSummary, String> componentNameColumn = new Column<UsageEventSummary, String>(new TextCell()) {
            @Override
            public String getValue(UsageEventSummary object) {
                return object.getComponent();
            }
        };
        componentNameColumn.setSortable(true);
        myEventListGrid.setColumnWidth(componentNameColumn, "100px");

        myEventListGrid.addColumn(componentNameColumn, new ResizableHeader(constants.Component(), myEventListGrid,
                componentNameColumn));
        sortHandler.setComparator(componentNameColumn, new Comparator<UsageEventSummary>() {
            @Override
            public int compare(UsageEventSummary o1, UsageEventSummary o2) {
                return o1.getComponent().compareTo(o2.getComponent());
            }
        });
        
        // Description
        Column<UsageEventSummary, String> descriptionNameColumn = new Column<UsageEventSummary, String>(new TextCell()) {
            @Override
            public String getValue(UsageEventSummary object) {
                return (object.getDescription()!=null)? UtilUsageData.wrapString(object.getDescription(), 110) : "";
            }
        };
        myEventListGrid.addColumn(descriptionNameColumn, new ResizableHeader(constants.Description(), myEventListGrid,
                descriptionNameColumn));
        
        // Action.
        Column<UsageEventSummary, String> actionNameColumn = new Column<UsageEventSummary, String>(new TextCell()) {
            @Override
            public String getValue(UsageEventSummary object) {
                return object.getAction();
            }
        };
        actionNameColumn.setSortable(true);
        myEventListGrid.setColumnWidth(actionNameColumn, "140px");

        myEventListGrid
                .addColumn(actionNameColumn, new ResizableHeader(constants.Actions(), myEventListGrid, actionNameColumn));
        sortHandler.setComparator(actionNameColumn, new Comparator<UsageEventSummary>() {
            @Override
            public int compare(UsageEventSummary o1, UsageEventSummary o2) {
                return o1.getAction().compareTo(o2.getAction());
            }
        });

        // key
        Column<UsageEventSummary, String> keyColumn = new Column<UsageEventSummary, String>(new TextCell()) {
            @Override
            public String getValue(UsageEventSummary object) {
                return object.getKey();
            }
        };
        keyColumn.setSortable(true);
        myEventListGrid.setColumnWidth(keyColumn, "90px");

        myEventListGrid.addColumn(keyColumn, new ResizableHeader(constants.Key(), myEventListGrid, keyColumn));
        sortHandler.setComparator(keyColumn, new Comparator<UsageEventSummary>() {
            @Override
            public int compare(UsageEventSummary o1, UsageEventSummary o2) {
                return o1.getKey().compareTo(o2.getKey());
            }
        });

        // Level.
        Column<UsageEventSummary, String> levelNameColumn = new Column<UsageEventSummary, String>(new TextCell()) {
            @Override
            public String getValue(UsageEventSummary object) {
                return object.getLevel();
            }
        };
        levelNameColumn.setSortable(true);
        myEventListGrid.setColumnWidth(levelNameColumn, "60px");

        myEventListGrid.addColumn(levelNameColumn, new ResizableHeader(constants.Level(), myEventListGrid, levelNameColumn));
        sortHandler.setComparator(levelNameColumn, new Comparator<UsageEventSummary>() {
            @Override
            public int compare(UsageEventSummary o1, UsageEventSummary o2) {
                return o1.getLevel().compareTo(o2.getLevel());
            }
        });

        // Status.
        Column<UsageEventSummary, String> statusNameColumn = new Column<UsageEventSummary, String>(new TextCell()) {
            @Override
            public String getValue(UsageEventSummary object) {
                return object.getStatus();
            }
        };
        statusNameColumn.setSortable(true);
        myEventListGrid.setColumnWidth(statusNameColumn, "80px");

        myEventListGrid.addColumn(statusNameColumn, new ResizableHeader(constants.Status(), myEventListGrid, statusNameColumn));
        sortHandler.setComparator(statusNameColumn, new Comparator<UsageEventSummary>() {
            @Override
            public int compare(UsageEventSummary o1, UsageEventSummary o2) {
                return o1.getStatus().compareTo(o2.getStatus());
            }
        });
        
        // FileName.
        Column<UsageEventSummary, String> fileNameColumn = new Column<UsageEventSummary, String>(new TextCell()) {
            @Override
            public String getValue(UsageEventSummary object) {
            	return object.getFileName() == null ?  "Session" : object.getFileName();  
            }
        };
        myEventListGrid.setColumnWidth(fileNameColumn, "200px");
        myEventListGrid.addColumn(fileNameColumn, new ResizableHeader(constants.FileName(), myEventListGrid, fileNameColumn));
        
        //Detail
        List<HasCell<UsageEventSummary, ?>> cells = new LinkedList<HasCell<UsageEventSummary, ?>>();
        cells.add(new DetailsHasCell(constants.Detail(), new ActionCell.Delegate<UsageEventSummary>() {
            @Override
            public void execute(UsageEventSummary usage) {
            	if(usage.getComponent()!=null && !usage.getComponent().isEmpty()){
            		placeManager.goTo(UtilUsageData.getPlaceRequestDetailUDC(usage));
            	}else{
            		displayNotification("There aren't details about this Event");
            	}
            }
        }));
        
        CompositeCell<UsageEventSummary> cell = new CompositeCell<UsageEventSummary>(cells);
        Column<UsageEventSummary, UsageEventSummary> detailColumn = new Column<UsageEventSummary, UsageEventSummary>(cell) {
            @Override
            public UsageEventSummary getValue(UsageEventSummary object) {
                return object;
            }
        };
        myEventListGrid.addColumn(detailColumn, constants.Detail());
        myEventListGrid.setColumnWidth(detailColumn, "60px");
        
    }

    @Override
    public void refreshUsageDataCollector() {
        presenter.refreshUsageDataCollector();
    }

    @Override
    public void clearUsageData() {
        presenter.clearUsageData();
    }

    @Override
    public void showInfoUsageData() {
        presenter.showInfoUsageData();
    }
    
    @Override
    public void showInfoGFS() {
        presenter.showInfoGFS();
    }
    

    @Override
    public void exportCsvEvents() {
        presenter.exportToCsv();
    }

    @Override
    public void displayNotification(String text) {
        notification.fire(new NotificationEvent(text));
    }

    @Override
    public MultiSelectionModel<UsageEventSummary> getSelectionModel() {
        return selectionModel;
    }

    public TextBox getSearchBox() {
        return searchBox;
    }
    
    private class DetailsHasCell implements HasCell<UsageEventSummary, UsageEventSummary> {

        private ActionCell<UsageEventSummary> cell;

        public DetailsHasCell(String text, ActionCell.Delegate<UsageEventSummary> delegate) {
            cell = new ActionCell<UsageEventSummary>(text, delegate) {
                @Override
                public void render(Cell.Context context, UsageEventSummary value, SafeHtmlBuilder sb) {
                    ImageResource detailsIcon = images.detailsIcon();
                    AbstractImagePrototype imageProto = AbstractImagePrototype.create(detailsIcon);
                    SafeHtmlBuilder mysb = new SafeHtmlBuilder();
                    mysb.appendHtmlConstant("<span title='" + constants.Info_Usage_Data() + "'>");
                    mysb.append(imageProto.getSafeHtml());
                    mysb.appendHtmlConstant("</span>");
                    sb.append(mysb.toSafeHtml());
                }
            };
        }

        @Override
        public Cell<UsageEventSummary> getCell() {
            return cell;
        }

        @Override
        public FieldUpdater<UsageEventSummary, UsageEventSummary> getFieldUpdater() {
            return null;
        }

        @Override
        public UsageEventSummary getValue(UsageEventSummary object) {
            return object;
        }
    }

    @Override
	public ListBox getEventTypesList() {
		return eventTypesList;
	}
    
    
}