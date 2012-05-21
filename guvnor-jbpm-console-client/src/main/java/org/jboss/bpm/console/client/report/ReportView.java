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
package org.jboss.bpm.console.client.report;

import java.util.List;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.mvc4g.client.Controller;
import com.mvc4g.client.Event;
import com.mvc4g.client.ViewInterface;
import org.jboss.bpm.console.client.ClientFactory;
import org.jboss.bpm.console.client.common.WidgetWindowPanel;
import org.jboss.bpm.console.client.report.search.UpdateSearchDefinitionsAction;
import org.jboss.bpm.report.model.ReportReference;

/**
 * @author Heiko.Braun <heiko.braun@jboss.com>
 */
public class ReportView implements ViewInterface, IsWidget {

    public final static String ID = ReportView.class.getName();

    private Controller controller;
    private boolean isInitialized;
    private ReportLaunchPadView coverpanel;

    private SimplePanel panel;
    private final ClientFactory clientFactory;

    public ReportView(ClientFactory clientFactory) {
        this.controller = clientFactory.getController();
        this.clientFactory = clientFactory;
    }

    public Widget asWidget() {
        panel = new SimplePanel();

        initialize();

        controller.addView(ReportView.ID, this);
        controller.addAction(UpdateReportConfigAction.ID, new UpdateReportConfigAction(clientFactory.getApplicationContext()));

        // ----

        Timer t = new Timer() {
            @Override
            public void run() {
                controller.handleEvent(new Event(UpdateReportConfigAction.ID, null));
            }
        };

        t.schedule(500);

        return panel;
    }

    public boolean isInitialized() {
        return isInitialized;
    }

    public void initialize() {
        if (!isInitialized) {
            // cover
            coverpanel = new ReportLaunchPadView(clientFactory);
            panel.add(coverpanel);

            // views and actions
            controller.addView(ReportLaunchPadView.ID, coverpanel);

            controller.addAction(UpdateSearchDefinitionsAction.ID, new UpdateSearchDefinitionsAction(clientFactory.getApplicationContext()));
            controller.addAction(RenderReportAction.ID, new RenderReportAction(clientFactory));

            this.isInitialized = true;
        }
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public void configure(List<ReportReference> reports) {
        // update coverview
        coverpanel.update(reports);
    }

    public void displayReport(String title, String dispatchUrl) {
        ReportFrame reportFrame = new ReportFrame();
        reportFrame.setFrameUrl(dispatchUrl);
        new WidgetWindowPanel(title, reportFrame, true);
    }
}
