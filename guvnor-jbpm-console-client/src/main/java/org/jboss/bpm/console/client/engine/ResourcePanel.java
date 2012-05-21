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

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.mvc4g.client.Controller;
import org.jboss.bpm.console.client.common.PropertyGrid;
import org.jboss.bpm.console.client.model.DeploymentRef;

/**
 * List resources associated with a process deployment.
 * @author Heiko.Braun <heiko.braun@jboss.com>
 */
public class ResourcePanel extends VerticalPanel {

    private Controller controller;

    private PropertyGrid propGrid;

    private DeploymentRef currentDeployment = null;

    private boolean initialized;

    private SimplePanel resources = new SimplePanel();

    private void initialize() {
        if (!initialized) {
            this.propGrid = new PropertyGrid(new String[]{"Deployment ID:"});

            this.add(propGrid);
            this.add(resources);

            this.initialized = true;
        }
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public void update(DeploymentRef deployment) {
        initialize();
        this.currentDeployment = deployment;

        StringBuffer sb = new StringBuffer();
        sb.append("<ul>");
        for (String res : deployment.getResourceNames()) {
            if (!res.endsWith("/")) {
                sb.append("<li>").append(res);
            }
        }
        sb.append("</ul>");

        HTML html = new HTML(sb.toString());
        resources.clear();
        resources.add(html);

        propGrid.update(new String[]{deployment.getId()});
    }

    public void clearView() {
        initialize();
        this.currentDeployment = null;
        propGrid.clear();
        resources.clear();
    }

    private DeploymentRef getSelection() {
        return this.currentDeployment;
    }
}
