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
package org.jboss.bpm.console.client.task;

import java.util.List;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.mvc4g.client.Event;
import org.jboss.bpm.console.client.ApplicationContext;
import org.jboss.bpm.console.client.ClientFactory;
import org.jboss.bpm.console.client.ServerPlugins;
import org.jboss.bpm.console.client.common.CustomizableListBox;
import org.jboss.bpm.console.client.common.DataDriven;
import org.jboss.bpm.console.client.common.IFrameWindowCallback;
import org.jboss.bpm.console.client.common.IFrameWindowPanel;
import org.jboss.bpm.console.client.common.LoadingOverlay;
import org.jboss.bpm.console.client.common.Model;
import org.jboss.bpm.console.client.common.ModelCommands;
import org.jboss.bpm.console.client.common.ModelParts;
import org.jboss.bpm.console.client.common.PagingCallback;
import org.jboss.bpm.console.client.common.PagingPanel;
import org.jboss.bpm.console.client.model.TaskRef;
import org.jboss.bpm.console.client.task.events.DetailViewEvent;
import org.jboss.bpm.console.client.task.events.TaskIdentityEvent;
import org.jboss.bpm.console.client.util.SimpleDateFormat;
import org.jboss.errai.bus.client.ErraiBus;
import org.jboss.errai.bus.client.api.Message;
import org.jboss.errai.bus.client.api.MessageCallback;

/**
 * @author Heiko.Braun <heiko.braun@jboss.com>
 */
public class AssignedTasksView extends AbstractTaskList implements IsWidget, DataDriven {

    public final static String ID = AssignedTasksView.class.getName();

    private final ApplicationContext appContext;

    private IFrameWindowPanel iframeWindow = null;

    private TaskDetailView detailsView;

    private SimpleDateFormat dateFormat = new SimpleDateFormat();

    private boolean hasDispatcherPlugin;

    private PagingPanel pagingPanel;

    private DockPanel panel;

    public AssignedTasksView(ClientFactory clientFactory) {
        this.appContext = clientFactory.getApplicationContext();
        this.controller = clientFactory.getController();
    }

    public Widget asWidget() {
        panel = new DockPanel();

        initialize();

        panel.add(detailsView, DockPanel.SOUTH);
        panel.add(taskList, DockPanel.CENTER);

        controller.addView(AssignedTasksView.ID, this);

        return panel;
    }

    public void initialize() {
        if (!isInitialized) {
            // workaround
            OpenTasksView.registerCommonActions(appContext, controller);

            taskList = new VerticalPanel();

            listBox =
                    new CustomizableListBox<TaskRef>(
                            new CustomizableListBox.ItemFormatter<TaskRef>() {
                                public String format(TaskRef taskRef) {

                                    String result = "";

                                    result += String.valueOf(taskRef.getPriority());

                                    result += " ";

                                    result += taskRef.getProcessId();

                                    result += " ";

                                    result += taskRef.getName();

                                    result += " ";

                                    result += taskRef.getDueDate() != null ? dateFormat.format(taskRef.getDueDate()) : "";

                                    return result;
                                }
                            }
                    );

            listBox.setFirstLine("Priority, Process, Task Name, Due Date");

            listBox.addChangeHandler(
                    new ChangeHandler() {
                        public void onChange(ChangeEvent event) {
                            TaskRef task = getSelection(); // first call always null?
                            if (task != null) {
                                controller.handleEvent(
                                        new Event(UpdateDetailsAction.ID, new DetailViewEvent("AssignedDetailView", task))
                                );
                            }

                        }
                    }
            );

            // toolbar
            final VerticalPanel toolBox = new VerticalPanel();
            toolBox.setSpacing(5);

            final MenuBar toolBar = new MenuBar();
            toolBar.addItem(
                    "Refresh",
                    new Command() {
                        public void execute() {
                            reload();
                        }
                    }
            );

            MenuItem viewBtn = new MenuItem(
                    "View",
                    new Command() {
                        public void execute() {
                            TaskRef selection = getSelection();

                            if (selection != null) {
                                if (selection.getUrl() != null && !selection.getUrl().equals("")) {
                                    iframeWindow = new IFrameWindowPanel(
                                            selection.getUrl(), "Task Form: " + selection.getName()
                                    );

                                    iframeWindow.setCallback(
                                            new IFrameWindowCallback() {
                                                public void onWindowClosed() {
                                                    reload();
                                                }
                                            }
                                    );

                                    iframeWindow.show();
                                } else {
                                    Window.alert("Invalid operation. The task doesn't provide a UI");
                                }
                            } else {
                                Window.alert("Missing selection. Please select a task");
                            }
                        }
                    }
            );
            toolBar.addItem(viewBtn);

            toolBar.addItem(
                    "Release",
                    new Command() {
                        public void execute() {
                            TaskRef selection = getSelection();

                            if (selection != null) {
                                TaskIdentityEvent payload = new TaskIdentityEvent(
                                        null, selection
                                );

                                controller.handleEvent(
                                        new Event(ReleaseTaskAction.ID, payload)
                                );
                            } else {
                                Window.alert("Missing selection. Please select a task");
                            }
                        }
                    }
            );

            toolBox.add(toolBar);

            this.taskList.add(toolBox);
            this.taskList.add(listBox);

            pagingPanel = new

                    PagingPanel(
                    new PagingCallback() {
                        public void rev
                                () {
                            renderUpdate();
                        }

                        public void ffw() {
                            renderUpdate();
                        }
                    }
            );

            this.taskList.add(pagingPanel);

            detailsView = new TaskDetailView(false);
            controller.addView("AssignedDetailView", detailsView);
            detailsView.initialize();

            // plugin availability
            this.hasDispatcherPlugin =
                    ServerPlugins.has("org.jboss.bpm.console.server.plugin.FormDispatcherPlugin");
            viewBtn.setEnabled(hasDispatcherPlugin);

            // deployments model listener
            ErraiBus.get().subscribe(Model.SUBJECT,
                    new MessageCallback() {
                        public void callback(Message message) {
                            switch (ModelCommands.valueOf(message.getCommandType())) {
                                case HAS_BEEN_UPDATED:
                                    if (message.get(String.class, ModelParts.CLASS).equals(Model.PROCESS_MODEL)) {
                                        reload();
                                    }
                                    break;
                            }
                        }
                    });

            Timer t = new Timer() {
                @Override
                public void run() {
                    // force loading
                    reload();
                }
            };

            t.schedule(500);

            isInitialized = true;
        }
    }

    private void reload() {
        // force loading
        controller.handleEvent(
                new Event(LoadTasksAction.ID, appContext.getAuthentication().getUsername())
        );
    }

    public void reset() {
        listBox.clear();

        // clear details
        controller.handleEvent(
                new Event(UpdateDetailsAction.ID, new DetailViewEvent("AssignedDetailView", null))
        );
    }

    public void update
            (Object... data) {
        this.identity = (String) data[0];
        this.cachedTasks = (List<TaskRef>) data[1];
        pagingPanel.reset();
        renderUpdate();
    }

    public void setLoading
            (
                    boolean isLoading) {
        if (panel.isVisible()) {
            LoadingOverlay.on(taskList, isLoading);
        }
    }

    private void renderUpdate
            () {
        // lazy init
        initialize();

        reset();

        List<TaskRef> trimmed = pagingPanel.trim(cachedTasks);
        for (TaskRef task : trimmed) {
            if (TaskRef.STATE.ASSIGNED == task.getCurrentState()) {
                listBox.addItem(task);
            }
        }

    }

}
