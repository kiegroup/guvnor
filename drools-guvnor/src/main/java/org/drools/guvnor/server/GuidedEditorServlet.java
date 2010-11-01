/**
 * Copyright 2010 JBoss Inc
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

package org.drools.guvnor.server;


import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class GuidedEditorServlet extends HttpServlet {

    public static String GE_PACKAGE_PARAMETER_NAME = "packageName";
    public static String GE_CATEGORY_PARAMETER_NAME = "categoryName";
    public static String GE_BRL_PARAMETER_NAME = "brlSource";
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(true);
        session.setAttribute(GE_PACKAGE_PARAMETER_NAME, req.getParameter(GE_PACKAGE_PARAMETER_NAME));
        session.setAttribute(GE_CATEGORY_PARAMETER_NAME, req.getParameter(GE_CATEGORY_PARAMETER_NAME));
        session.setAttribute(GE_BRL_PARAMETER_NAME, req.getParameterValues(GE_BRL_PARAMETER_NAME));
        
        resp.sendRedirect("GuidedEditor.html?"+req.getQueryString());
        //RequestDispatcher requestDispatcher = req.getRequestDispatcher("GuidedEditor.html");
        //requestDispatcher.forward(req, resp);
    }

	

}