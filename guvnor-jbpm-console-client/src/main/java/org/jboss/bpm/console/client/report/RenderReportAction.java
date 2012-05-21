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

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.*;
import com.google.gwt.user.client.Timer;
import com.mvc4g.client.ActionInterface;
import com.mvc4g.client.Controller;
import org.jboss.bpm.console.client.ApplicationContext;
import org.jboss.bpm.console.client.ClientFactory;
import org.jboss.bpm.console.client.LoadingStatusAction;
import org.jboss.bpm.console.client.util.ConsoleLog;
//import org.jboss.errai.workspaces.client.framework.Registry;

import java.io.IOException;

/**
 * Engage a report generation and update {@link org.jboss.bpm.console.client.report.ReportView}
 * when the report is finished.
 *
 * @author Heiko.Braun <heiko.braun@jboss.com>
 */
public class RenderReportAction implements ActionInterface
{

  public final static String ID = RenderReportAction.class.getName();

  private final ApplicationContext appContext ;

  public RenderReportAction(ClientFactory clientFactory)
  {
      this.appContext = clientFactory.getApplicationContext();
  }

  public void execute(final Controller controller, Object object)
  {
    final RenderDispatchEvent event = (RenderDispatchEvent)object;

    final String url = event.getDispatchUrl();
    RequestBuilder builder = new RequestBuilder(RequestBuilder.POST, url);

    ConsoleLog.debug(RequestBuilder.POST+": " + url);
    final ReportLaunchPadView view = (ReportLaunchPadView)controller.getView(ReportLaunchPadView.ID);

    view.reset();
    view.setLoading(true);

    try
    {
      controller.handleEvent( LoadingStatusAction.ON );
      //view.setLoading(true);

      String parameters = event.getParameters();
      final Request request = builder.sendRequest(parameters,
          new RequestCallback()
          {
            public void onError(Request request, Throwable exception) {
              // Couldn't connect to server (could be timeout, SOP violation, etc.)
              handleError(controller,url, exception);
              controller.handleEvent( LoadingStatusAction.OFF );
            }

            public void onResponseReceived(Request request, Response response) {
              try
              {
                if(response.getText().indexOf("HTTP 401")!=-1) // HACK
                {
                  appContext.getAuthentication().handleSessionTimeout();
                }
                else if (200 == response.getStatusCode())
                {
                  // update view
                  view.displayReport(event.getTitle(), event.getDispatchUrl());
                }
                else
                {                  
                  final String msg = response.getText().equals("") ? "Unknown error" : response.getText();
                  handleError(
                      controller,
                      url,
                      new RequestException("HTTP "+ response.getStatusCode()+ ": " + msg)
                  );
                }
              }
              finally
              {
                controller.handleEvent( LoadingStatusAction.OFF );
                view.setLoading(false);
              }
            }
          }
      );

      // Timer to handle pending request
      Timer t = new Timer() {

        public void run()
        {
          if(request.isPending())
          {
            request.cancel();
            handleError(
                controller,
                url,
                new IOException("Request timeout")
            );
          }

        }
      };
      t.schedule(20000);

    }
    catch (Throwable e)
    {
      // Couldn't connect to server
      controller.handleEvent( LoadingStatusAction.OFF );
      handleError(controller, url, e);
      view.setLoading(false);
    }
  }

  protected void handleError(Controller controller, String url, Throwable t)
  {
    final String out =
        "<ul>"+
            "<li>URL: '" + url + "'\n"+
            "<li>Action: '" + ID + "'\n" +
            "<li>Exception: '" + t.getClass() +"'"+
            "</ul>\n\n"+
            t.getMessage();

    ConsoleLog.error(out, t);
    appContext.displayMessage(out, true);

    controller.handleEvent( LoadingStatusAction.OFF );
  }
}
