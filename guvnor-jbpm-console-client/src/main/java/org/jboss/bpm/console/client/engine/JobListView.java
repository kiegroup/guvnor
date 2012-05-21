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

import java.util.Date;
import java.util.List;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.mvc4g.client.Controller;
import com.mvc4g.client.Event;
import com.mvc4g.client.ViewInterface;
import org.jboss.bpm.console.client.ClientFactory;
import org.jboss.bpm.console.client.common.CustomizableListBox;
import org.jboss.bpm.console.client.common.DataDriven;
import org.jboss.bpm.console.client.common.LoadingOverlay;
import org.jboss.bpm.console.client.model.JobRef;
import org.jboss.bpm.console.client.util.SimpleDateFormat;

/**
 * Display a list of jobs waiting for execution.<br/>
 * I.e. pending Timers and Messages.
 * @author Heiko.Braun <heiko.braun@jboss.com>
 */
public class JobListView implements ViewInterface, IsWidget, DataDriven {

    public final static String ID = JobListView.class.getName();

    private Controller controller;

    private VerticalPanel jobList = null;

    private CustomizableListBox<JobRef> listBox;

    private JobRef selection = null;

    private SimpleDateFormat dateFormat = new SimpleDateFormat();

    private int FILTER_NONE = 10;
    private int FILTER_TIMER = 20;
    private int FILTER_MESSAGE = 30;
    private int currentFilter = FILTER_NONE;

    private List<JobRef> jobs = null;

    SimplePanel panel;

    private boolean initialized;
    private final ClientFactory clientFactory;

    public JobListView(ClientFactory clientFactory) {
        this.controller = clientFactory.getController();
        this.clientFactory = clientFactory;
    }

    public Widget asWidget() {
        panel = new SimplePanel();

        listBox = createListBox();

        initialize();

        panel.add(jobList);

        controller.addView(JobListView.ID, this);
        controller.addAction(ExecuteJobAction.ID, new ExecuteJobAction(clientFactory.getApplicationContext()));
        return panel;
    }

    private CustomizableListBox<JobRef> createListBox() {
        final CustomizableListBox<JobRef> listBox =
                new CustomizableListBox<JobRef>(new CustomizableListBox.ItemFormatter<JobRef>() {

                    public String format(JobRef item) {

                        String result = "";

                        result += item.getId();

                        result += " ";

                        long ts = item.getTimestamp();
                        String ds = ts > 0 ? dateFormat.format(new Date(ts)) : "";
                        result += ds;

                        result += " ";

                        result += item.getType();
                        return result;
                    }
                });

        listBox.setFirstLine("ID, Due Date, Type");

//        listBox.addChangeHandler(
//                new ChangeHandler() {
//                    public void onChange(ChangeEvent event) {
////                        int index = listBox.getSelectedIndex();
////                        if (index != -1) {
//                            // TODO: Looks like this does nothing? -Rikkola-
////                            JobRef item = listBox.getItem(index);
//
//                            /*controller.handleEvent(
//                                new Event(UpdateJobDetailAction.ID, item)
//                            );*/
////                        }
//
//                    }
//                }
//        );

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
            jobList = new VerticalPanel();

            // toolbar

            final HorizontalPanel toolBox = new HorizontalPanel();

            // toolbar
            final MenuBar toolBar = new MenuBar();
            toolBar.addItem(
                    "Refresh",
                    new Command() {
                        public void execute() {
                            // force loading
                            controller.handleEvent(
                                    new Event(UpdateJobsAction.ID, null)
                            );
                        }
                    }
            );

            toolBar.addItem(
                    "Execute",
                    new Command() {
                        public void execute() {
                            JobRef selection = getSelection();
                            if (null == selection) {
                                Window.alert("Missing selection. Please select a job!");
                            } else {
                                controller.handleEvent(
                                        new Event(ExecuteJobAction.ID, selection.getId())
                                );
                            }
                        }
                    }
            );

            toolBox.add(toolBar);

            // filter
            VerticalPanel filterPanel = new VerticalPanel();
            filterPanel.setStyleName("mosaic-ToolBar");
            final com.google.gwt.user.client.ui.ListBox dropBox = new com.google.gwt.user.client.ui.ListBox(false);
            dropBox.setStyleName("bpm-operation-ui");
            dropBox.addItem("All");
            dropBox.addItem("Timers");
            dropBox.addItem("Messages");

            dropBox.addChangeListener(
                    new ChangeListener() {
                        public void onChange(Widget sender) {
                            switch (dropBox.getSelectedIndex()) {
                                case 0:
                                    currentFilter = FILTER_NONE;
                                    break;
                                case 1:
                                    currentFilter = FILTER_TIMER;
                                    break;
                                case 2:
                                    currentFilter = FILTER_MESSAGE;
                                    break;
                                default:
                                    throw new IllegalArgumentException("No such index");
                            }

                            renderFiltered();
                        }
                    }

            );
            filterPanel.add(dropBox);

            toolBox.add(filterPanel);

            this.jobList.add(toolBox);
            this.jobList.add(listBox);

            // details
            /*JobDetailView detailsView = new JobDetailView();
            controller.addView(JobDetailView.ID, detailsView);
            controller.addAction(UpdateJobDetailAction.ID, new UpdateJobDetailAction());
            layout.add(detailsView, new BorderLayoutData(BorderLayout.Region.SOUTH, 10,200));
            */

            Timer t = new Timer() {
                @Override
                public void run() {
                    controller.handleEvent(new Event(UpdateJobsAction.ID, null));
                }
            };

            t.schedule(500);

            controller.addAction(UpdateJobsAction.ID, new UpdateJobsAction(clientFactory.getApplicationContext()));

            this.initialized = true;
        }
    }

    public void reset() {
        listBox.clear();
    }

    public void update(Object... data) {
        this.jobs = (List<JobRef>) data[0];
        renderFiltered();
    }

    public void setLoading(boolean isLoading) {
        LoadingOverlay.on(jobList, isLoading);
    }

    private void renderFiltered() {
        if (this.jobs != null) {
            reset();

            for (JobRef def : jobs) {
                if (FILTER_NONE == currentFilter) {
                    listBox.addItem(def);
                } else {
                    if (FILTER_TIMER == currentFilter
                            && def.getType().equals("timer")) {
                        listBox.addItem(def);
                    } else if (FILTER_MESSAGE == currentFilter
                            && def.getType().equals("message")) {
                        listBox.addItem(def);
                    }
                }
            }

            // clear details
            /* controller.handleEvent(
               new Event(UpdateJobDetailAction.ID, null)
           ); */
        }
    }

    public JobRef getSelection() {
        JobRef selection = null;
        if (isInitialized() && listBox.getSelectedIndex() != -1) {
            selection = listBox.getItem(listBox.getSelectedIndex());
        }
        return selection;
    }

}
