/*
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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class StandaloneEditorServlet extends HttpServlet {

    public static enum STANDALONE_EDITOR_SERVLET_PARAMETERS {

        GE_PACKAGE_PARAMETER_NAME("packageName", false), 
        GE_CATEGORY_PARAMETER_NAME("categoryName", false), 
        GE_BRL_PARAMETER_NAME("brlSource", true), 
        GE_ASSETS_UUIDS_PARAMETER_NAME("assetsUUIDs", true), 
        GE_CREATE_NEW_ASSET_PARAMETER_NAME("createNewAsset", false),
        
        //Only used when creating a new Rule
        GE_ASSET_NAME_PARAMETER_NAME("assetName", false), 
        GE_ASSET_FORMAT_PARAMETER_NAME("assetFormat", false),
        GE_HIDE_RULE_LHS_PARAMETER_NAME("hideRuleLHS", false), 
        GE_HIDE_RULE_RHS_PARAMETER_NAME("hideRuleRHS", false), 
        GE_HIDE_RULE_ATTRIBUTES_PARAMETER_NAME("hideRuleAttributes", false),
        GE_VALID_FACT_TYPE_PARAMETER_NAME("validFactType", true),
        GE_CLIENT_NAME_PARAMETER_NAME("client", false),
        
        //UUID of working-set to activate
        GE_ACTIVE_WORKING_SET_UUIDS_PARAMETER_NAME("activeWorkingSetUUIDs", true);

        private final String parameterName;
        private final boolean multipleValues;

        private STANDALONE_EDITOR_SERVLET_PARAMETERS(String parameterName,
                                                     boolean multipleValues) {
            this.parameterName = parameterName;
            this.multipleValues = multipleValues;
        }

        public String getParameterName() {
            return parameterName;
        }

        public boolean isMultipleValues() {
            return multipleValues;
        }
    }

    @Override
    public void service(HttpServletRequest req,
                        HttpServletResponse resp) throws ServletException,
            IOException {
        HttpSession session = req.getSession(true);
        //Each request uses its own parameters map (this allows concurrent requests
        //from the same cilent)
        Map<String, Object> parameters = new HashMap<String, Object>();
        //copy each registered parameter from request to session
        for (STANDALONE_EDITOR_SERVLET_PARAMETERS parameter : STANDALONE_EDITOR_SERVLET_PARAMETERS.values()) {
            if (parameter.isMultipleValues()) {
                parameters.put(parameter.getParameterName(),
                        req.getParameterValues(parameter.getParameterName()));
            } else {
                parameters.put(parameter.getParameterName(),
                        req.getParameter(parameter.getParameterName()));
            }
        }

        String parametersUUID = UUID.randomUUID().toString();
        session.setAttribute(parametersUUID,
                parameters);

        resp.sendRedirect("StandaloneEditor.html?pUUID=" + parametersUUID + "&" + req.getQueryString());
    }
}
