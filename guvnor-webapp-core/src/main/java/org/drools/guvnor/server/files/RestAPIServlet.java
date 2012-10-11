/*
 * Copyright 2005 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.guvnor.server.files;

import org.drools.guvnor.server.files.Response;
import org.drools.guvnor.server.files.RestAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * This servlet is the entry point for the rest API.
 */
public class RestAPIServlet extends RepositoryServlet {

    private static final long serialVersionUID = 510l;
    public static final Logger log = LoggerFactory.getLogger(RestAPIServlet.class);

    @Inject
    private RestAPI restAPI;

    @Override
    protected void doPost(final HttpServletRequest req,
                          final HttpServletResponse res) throws ServletException,
            IOException {
        doAuthorizedAction(req,
                res,
                new Command() {
                    public void execute() throws Exception {
                        res.setContentType("text/html");
                        RestAPI api = getAPI();
                        String comment = req.getHeader("Checkin-Comment");
                        api.post(req.getRequestURI(),
                                req.getInputStream(),
                                (comment != null) ? comment : "");
                        res.getWriter().write("OK");
                    }
                });
    }

    @Override
    protected void doGet(final HttpServletRequest req,
                         final HttpServletResponse res) throws ServletException,
            IOException {
        doAuthorizedAction(req,
                res,
                new Command() {
                    public void execute() throws Exception {
                        RestAPI api = getAPI();
                        String qString = req.getQueryString();
                        String ur = req.getRequestURI();
                        if (qString != null && qString.length() > 0) {
                            ur = ur + '?' + qString;
                        }
                        Response apiRes = api.get(ur);
                        res.setContentType("application/x-download");
                        res.setHeader("Content-Disposition",
                                "attachment; filename=data;");
                        apiRes.writeData(res.getOutputStream());
                        res.getOutputStream().flush();
                    }
                });

    }

    @Override
    protected void doPut(final HttpServletRequest req,
                         final HttpServletResponse res)
            throws ServletException,
            IOException {
        doAuthorizedAction(req,
                res,
                new Command() {
                    public void execute() throws Exception {
                        res.setContentType("text/html");
                        RestAPI api = getAPI();
                        String comment = req.getHeader("Checkin-Comment");
                        Calendar lastMod = getModified(req.getHeader("Last-Modified"));
                        api.put(req.getRequestURI(),
                                lastMod,
                                req.getInputStream(),
                                (comment != null) ? comment : "");
                        res.getWriter().write("OK");
                    }
                });
    }

    Calendar getModified(String f) throws ParseException {

        if (f == null) return null;
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = RestAPI.getISODateFormat();
        try {
            c.setTime(sdf.parse(f));
        } catch (ParseException e) {
            DateFormat df = DateFormat.getInstance();
            c.setTime(df.parse(f));
        }
        return c;
    }

    @Override
    protected void doDelete(final HttpServletRequest req,
                            final HttpServletResponse res)
            throws ServletException,
            IOException {
        doAuthorizedAction(req,
                res,
                new Command() {
                    public void execute() throws Exception {
                        res.setContentType("text/html");
                        RestAPI api = getAPI();
                        api.delete(req.getRequestURI());
                        res.getWriter().write("OK");
                    }
                });
    }

    RestAPI getAPI() {
        return restAPI;
    }

}
