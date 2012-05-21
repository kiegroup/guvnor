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
package org.jboss.bpm.console.client.common;

import java.io.IOException;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Timer;
import com.mvc4g.client.ActionInterface;
import com.mvc4g.client.Controller;
import org.jboss.bpm.console.client.ApplicationContext;
import org.jboss.bpm.console.client.LoadingStatusAction;
import org.jboss.bpm.console.client.util.ConsoleLog;

/**
 * @author Heiko.Braun <heiko.braun@jboss.com>
 */
public abstract class AbstractRESTAction implements ActionInterface {

    protected final ApplicationContext appContext;

    protected AbstractRESTAction(ApplicationContext applicationContext) {
        this.appContext = applicationContext;
    }

    public abstract String getId();

    public abstract String getUrl(Object event);

    public abstract RequestBuilder.Method getRequestMethod();

    public abstract void handleSuccessfulResponse(final Controller controller, final Object event, Response response);

    public void execute(final Controller controller, final Object object) {
        final String url = getUrl(object);
        RequestBuilder builder = new RequestBuilder(getRequestMethod(), URL.encode(url));

        ConsoleLog.debug(getRequestMethod() + ": " + url);

        try {
            //controller.handleEvent( LoadingStatusAction.ON );
            if (getDataDriven(controller) != null) {
                getDataDriven(controller).setLoading(true);
            }

            final Request request = builder.sendRequest(null,
                    new RequestCallback() {
                        public void onError(Request request, Throwable exception) {
                            // Couldn't connect to server (could be timeout, SOP violation, etc.)
                            handleError(url, exception);
                            controller.handleEvent(LoadingStatusAction.OFF);
                        }

                        public void onResponseReceived(Request request, Response response) {
                            try {
                                if (response.getText().indexOf("HTTP 401") != -1) // HACK
                                {
                                    appContext.getAuthentication().handleSessionTimeout();
                                } else if (200 == response.getStatusCode()) {
                                    handleSuccessfulResponse(controller, object, response);
                                } else {
                                    final String msg = response.getText().equals("") ? "Unknown error" : response.getText();
                                    handleError(
                                            url,
                                            new RequestException("HTTP " + response.getStatusCode() + ": " + msg)
                                    );
                                }
                            } finally {
                                //controller.handleEvent( LoadingStatusAction.OFF );
                                if (getDataDriven(controller) != null) {
                                    getDataDriven(controller).setLoading(false);
                                }
                            }
                        }
                    }
            );

            // Timer to handle pending request
            Timer t = new Timer() {

                public void run() {
                    if (request.isPending()) {
                        request.cancel();
                        handleError(
                                url,
                                new IOException("Request timeout")
                        );
                    }

                }
            };
            t.schedule(20000);

        } catch (RequestException e) {
            // Couldn't connect to server
            handleError(url, e);
            //controller.handleEvent( LoadingStatusAction.OFF );

            if (getDataDriven(controller) != null) {
                getDataDriven(controller).setLoading(false);
            }
        }
    }

    protected DataDriven getDataDriven(Controller controller) {
        return null;
    }

    protected void handleError(String url, Throwable t) {
        final String out =
                "<ul>" +
                        "<li>URL: '" + url + "'\n" +
                        "<li>Action: '" + getId() + "'\n" +
                        "<li>Exception: '" + t.getClass() + "'" +
                        "</ul>\n\n" +
                        t.getMessage();

        ConsoleLog.error(out, t);
        appContext.displayMessage(out, true);

    }
}
