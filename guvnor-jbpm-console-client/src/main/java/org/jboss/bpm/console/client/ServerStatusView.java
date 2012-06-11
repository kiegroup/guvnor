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
package org.jboss.bpm.console.client;

import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.mvc4g.client.Controller;
import com.mvc4g.client.ViewInterface;
import org.jboss.bpm.console.client.common.HeaderLabel;
import org.jboss.bpm.console.client.model.PluginInfo;
import org.jboss.bpm.console.client.model.ServerStatus;

/**
 * @author Heiko.Braun <heiko.braun@jboss.com>
 */
public class ServerStatusView
        implements ViewInterface, LazyPanel, IsWidget {

    public final static String ID = ServerStatusView.class.getName();

    private Controller controller;

    private final ApplicationContext appContext;

    private boolean initialized;

    VerticalPanel layoutPanel;
    HorizontalPanel pluginPanel;

    public ServerStatusView(BpmConsoleClientFactory clientFactory) {
        this.appContext = clientFactory.getApplicationContext();
        this.controller = clientFactory.getController();
    }

    public Widget asWidget() {
        layoutPanel = new VerticalPanel();

        // console info
        HeaderLabel console = new HeaderLabel("Console Info");
        layoutPanel.add(console);

        VerticalPanel layout1 = new VerticalPanel();
        layout1.add(new HTML("Version:"));
        layout1.add(new HTML(Version.VERSION));

        layoutPanel.add(layout1);

        // server info
        HeaderLabel server = new HeaderLabel("Server Info");
        layoutPanel.add(server);

        HorizontalPanel layout2 = new HorizontalPanel();
        VerticalPanel row1 = new VerticalPanel();
        row1.add(new HTML("Host:"));
        //TODO: -Rikkola-
//        layout2.add(new HTML(Registry.get(ApplicationContext.class).getConfig().getConsoleServerUrl()));

        pluginPanel = new HorizontalPanel();
        VerticalPanel row2 = new VerticalPanel();
        row2.add(new Label("Plugins:"));
        row2.add(pluginPanel);
        layout2.add(row1);
        layout2.add(row2);

        layoutPanel.add(layout2);

        // ---

        controller.addView(ServerStatusView.ID, this);

        update(ServerPlugins.getStatus());

        return layoutPanel;
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void initialize() {
        if (!initialized) {
            update(ServerPlugins.getStatus());
            initialized = true;
        }
    }

    private void update(ServerStatus status) {
        pluginPanel.clear();

        Grid g = new Grid(status.getPlugins().size(), 2);
        g.setWidth("100%");

        for (int row = 0; row < status.getPlugins().size(); ++row) {
            PluginInfo p = status.getPlugins().get(row);
            String type = p.getType().substring(
                    p.getType().lastIndexOf(".") + 1, p.getType().length()
            );

            g.setText(row, 0, type);

            final Image img = p.isAvailable() ?
                    new Image("images/icons/confirm_small.png") :
                    new Image("images/icons/deny_small.png");

            g.setWidget(row, 1, img);
        }

        pluginPanel.add(g);
    }

}
