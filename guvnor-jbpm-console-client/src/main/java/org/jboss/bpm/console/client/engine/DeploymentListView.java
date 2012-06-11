/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2006, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.bpm.console.client.engine;

import java.util.List;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.mvc4g.client.Controller;
import com.mvc4g.client.Event;
import com.mvc4g.client.ViewInterface;
import org.jboss.bpm.console.client.ApplicationContext;
import org.jboss.bpm.console.client.BpmConsoleClientFactory;
import org.jboss.bpm.console.client.common.CustomizableListBox;
import org.jboss.bpm.console.client.common.DataDriven;
import org.jboss.bpm.console.client.common.LoadingOverlay;
import org.jboss.bpm.console.client.model.DeploymentRef;
import org.jboss.bpm.console.client.util.SimpleDateFormat;

/**
 * List of deployments
 * @author Heiko.Braun <heiko.braun@jboss.com>
 */
public class DeploymentListView implements ViewInterface, IsWidget, DataDriven {

    public final static String ID = DeploymentListView.class.getName();

    private Controller controller;

    private boolean initialized;

    private VerticalPanel deploymentList = null;

    private CustomizableListBox<DeploymentRef> listBox;

    private DeploymentRef selection = null;

    private SimpleDateFormat dateFormat = new SimpleDateFormat();

    private int FILTER_NONE = 10;
    private int FILTER_ACTIVE = 20;
    private int FILTER_SUSPENDED = 30;
    private int currentFilter = FILTER_NONE;

    private List<DeploymentRef> deployments = null;

    private DeploymentDetailView detailView;

    DockPanel panel;

    private boolean isRiftsawInstance = false;
    private final ApplicationContext applicationContext;

    public DeploymentListView(BpmConsoleClientFactory clientFactory) {
        applicationContext = clientFactory.getApplicationContext();
        this.controller = clientFactory.getController();
        this.isRiftsawInstance = clientFactory.getApplicationContext().getConfig().getProfileName().equals("BPEL Console");
    }

    public Widget asWidget() {
        panel = new DockPanel();
        listBox = createListBox();

        initialize();

        panel.add(detailView, DockPanel.SOUTH);
        panel.add(deploymentList, DockPanel.CENTER);

        // create and register actions
        controller.addAction(UpdateDeploymentsAction.ID, new UpdateDeploymentsAction(applicationContext));
        controller.addAction(UpdateDeploymentDetailAction.ID, new UpdateDeploymentDetailAction());
        controller.addAction(DeleteDeploymentAction.ID, new DeleteDeploymentAction(applicationContext));
        controller.addAction(SuspendDeploymentAction.ID, new SuspendDeploymentAction(applicationContext));
        controller.addAction(ResumeDeploymentAction.ID, new ResumeDeploymentAction(applicationContext));
        //controller.addAction(ViewDeploymentAction.ID, new ViewDeploymentAction());

        controller.addView(DeploymentListView.ID, this);

        return panel;
    }

    private CustomizableListBox createListBox() {
        final CustomizableListBox<DeploymentRef> listBox =
                new CustomizableListBox<DeploymentRef>(
                        new CustomizableListBox.ItemFormatter<DeploymentRef>() {

                            public String format(DeploymentRef deploymentRef) {
                                String result = "";
                                String color = deploymentRef.isSuspended() ? "#CCCCCC" : "#000000";

                                String text = "<div style=\"color:" + color + "\">" + deploymentRef.getName() + "</div>";
                                result += new HTML(text);

                                String status = deploymentRef.isSuspended() ? "retired" : "active";
                                String s = "<div style=\"color:" + color + "\">" + status + "</div>";
                                result += new HTML(status);

                                return result;
                            }
                        }

                );

        listBox.setFirstLine("Deployment, Status");

        listBox.addChangeHandler(
                new ChangeHandler() {
                    public void onChange(ChangeEvent event) {
                        int index = listBox.getSelectedIndex();
                        if (index != -1) {
                            DeploymentRef item = listBox.getItem(index);

                            controller.handleEvent(
                                    new Event(UpdateDeploymentDetailAction.ID, item)
                            );
                        }
                    }
                }

        );

        return listBox;
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void initialize() {
        if (!initialized) {
            deploymentList = new VerticalPanel();

            // toolbar

            final HorizontalPanel toolBox = new HorizontalPanel();

            final MenuBar toolBar = new MenuBar();
            toolBar.addItem(
                    "Refresh",
                    new Command() {
                        public void execute() {

                            reset();

                            // force loading
                            controller.handleEvent(
                                    new Event(UpdateDeploymentsAction.ID, null)
                            );
                        }
                    }
            );

            MenuItem deleteBtn = new MenuItem(
                    "Delete",
                    new Command() {
                        public void execute() {
                            DeploymentRef deploymentRef = getSelection();
                            if (deploymentRef != null) {
                                if (Window.confirm("Delete deployment. Do you want to delete this deployment? Any related data will be removed.")) {
                                    controller.handleEvent(
                                            new Event(
                                                    DeleteDeploymentAction.ID,
                                                    getSelection().getId()
                                            )
                                    );
                                }
                            } else {
                                Window.alert("Missing selection. Please select a deployment");
                            }
                        }
                    }
            );

            if (!isRiftsawInstance) {
                toolBar.addItem(deleteBtn);
            }

            toolBox.add(toolBar);

            // filter
            VerticalPanel filterPanel = new VerticalPanel();
            filterPanel.setStyleName("mosaic-ToolBar");
            final com.google.gwt.user.client.ui.ListBox dropBox = new com.google.gwt.user.client.ui.ListBox(false);
            dropBox.setStyleName("bpm-operation-ui");
            dropBox.addItem("All");
            dropBox.addItem("Active");
            dropBox.addItem("Retired");

            dropBox.addChangeListener(new ChangeListener() {
                public void onChange(Widget sender) {
                    switch (dropBox.getSelectedIndex()) {
                        case 0:
                            currentFilter = FILTER_NONE;
                            break;
                        case 1:
                            currentFilter = FILTER_ACTIVE;
                            break;
                        case 2:
                            currentFilter = FILTER_SUSPENDED;
                            break;
                        default:
                            throw new IllegalArgumentException("No such index");
                    }

                    renderFiltered();
                }
            });
            filterPanel.add(dropBox);

            toolBox.add(filterPanel);

            this.deploymentList.add(toolBox);
            this.deploymentList.add(listBox);

            // details
            // detail panel
            detailView = new DeploymentDetailView();
            controller.addView(DeploymentDetailView.ID, detailView);

            Timer t = new Timer() {
                @Override
                public void run() {
                    controller.handleEvent(
                            new Event(UpdateDeploymentsAction.ID, null)
                    );
                }
            };

            t.schedule(500);

            initialized = true;
        }

    }

    public DeploymentRef getSelection() {
        DeploymentRef selection = null;
        if (isInitialized() && listBox.getSelectedIndex() != -1) {
            selection = listBox.getItem(listBox.getSelectedIndex());
        }
        return selection;
    }

    public void reset() {
        listBox.clear();

        // clear details
        controller.handleEvent(
                new Event(UpdateDeploymentDetailAction.ID, null)
        );
    }

    public void update(Object... data) {
        this.deployments = (List<DeploymentRef>) data[0];

        renderFiltered();
    }

    public void setLoading(boolean isLoading) {
        LoadingOverlay.on(deploymentList, isLoading);
    }

    private void renderFiltered() {
        if (this.deployments != null) {
            reset();

            for (DeploymentRef dpl : deployments) {
                if (FILTER_NONE == currentFilter) {
                    listBox.addItem(dpl);
                } else {
                    boolean showSuspended = (FILTER_SUSPENDED == currentFilter);
                    if (dpl.isSuspended() == showSuspended) {
                        listBox.addItem(dpl);
                    }
                }
            }
        }
    }

    public void select(String deploymentId) {
        for (int i = 0; i < listBox.getItemCount(); i++) {
            DeploymentRef ref = listBox.getItem(i);
            if (ref.getId().equals(deploymentId)) {
                listBox.setSelectedIndex(i);
                break;
            }
        }

    }
}
