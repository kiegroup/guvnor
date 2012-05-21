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

import java.util.Date;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Frame;

/**
 * Embeds an HTML report.
 * @author Heiko.Braun <heiko.braun@jboss.com>
 */
public class ReportFrame extends Composite {

    private Frame frame;

    public ReportFrame() {
        super();

        // report frame
        frame = new Frame();
        DOM.setStyleAttribute(frame.getElement(), "border", "1px solid #E8E8E8");
        DOM.setStyleAttribute(frame.getElement(), "backgroundColor", "#ffffff");
        initWidget(frame);
    }

    public void setFrameUrl(String url) {
        // https://jira.jboss.org/jira/browse/JBPM-2244
        frame.getElement().setId(
                String.valueOf(new Date().getTime())
        );

        frame.setUrl(url);
    }

}
