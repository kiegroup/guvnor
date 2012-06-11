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

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.Response;
import com.mvc4g.client.Controller;
import org.jboss.bpm.console.client.ApplicationContext;
import org.jboss.bpm.console.client.common.AbstractRESTAction;
import org.jboss.bpm.console.client.model.JSOParser;
import org.jboss.bpm.report.model.ReportReference;

/**
 * Retrieves the inital reporting config in order to setup the
 * {@link org.jboss.bpm.console.client.report.ReportLaunchPadView}
 * @author Heiko.Braun <heiko.braun@jboss.com>
 * @see org.jboss.bpm.console.client.report.ReportView#configure(java.util.List)
 * @see org.jboss.bpm.console.client.report.ReportLaunchPadView#update(java.util.List)
 */
public class UpdateReportConfigAction extends AbstractRESTAction {

    public final static String ID = UpdateReportConfigAction.class.getName();

    protected UpdateReportConfigAction(ApplicationContext applicationContext) {
        super(applicationContext);
    }

    public String getId() {
        return ID;
    }

    public String getUrl(Object event) {
        return appContext.getConfig().getConsoleServerUrl() + "/rs/report/config";
    }

    public RequestBuilder.Method getRequestMethod() {
        return RequestBuilder.GET;
    }

    public void handleSuccessfulResponse(
            final Controller controller, final Object event, Response response) {
        String json = response.getText();
        List<ReportReference> reports = JSOParser.parseReportConfig(json);
        ReportView view = (ReportView) controller.getView(ReportView.ID);
        view.configure(reports);
    }
}
