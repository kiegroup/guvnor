/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.guvnor.server.contenthandler.drools;

import java.io.IOException;
import java.io.OutputStream;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceConfig;
import org.drools.guvnor.server.files.RepositoryServlet;
import org.drools.guvnor.server.repository.Preferred;
import org.drools.repository.AssetItem;
import org.drools.repository.RulesRepository;

import static javax.servlet.http.HttpServletResponse.*;
import static org.drools.guvnor.server.generators.ServiceWarGenerator.*;

public class ServiceWarBuilderAndDownloadHandler extends RepositoryServlet {

    @Inject @Preferred
    private RulesRepository repository;

    private static final long serialVersionUID = 1L;

    protected void doGet(final HttpServletRequest req, final HttpServletResponse res) throws ServletException, IOException {

        final String uuid = req.getParameter("uuid");

        if (uuid == null) {
            res.sendError(SC_BAD_REQUEST);
        }

        final AssetItem asset = repository.loadAssetByUUID(uuid);

        if (asset == null) {
            res.sendError(SC_NO_CONTENT);
            return;
        }

        final ServiceConfig serviceConfig = ServiceConfigPersistence.getInstance().unmarshal(asset.getContent());

        try {
            res.setContentType("application/x-download");
            res.setHeader("Content-Disposition", "attachment; filename=drools-service.war;");
            buildWar(serviceConfig, repository, res.getOutputStream());
        } catch (Throwable e) {
            ((OutputStream) res.getOutputStream()).close();
            res.sendError(SC_INTERNAL_SERVER_ERROR, e.getMessage());
        } finally {
        }
    }
}
