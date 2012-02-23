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

package org.drools.guvnor.server.generators;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceConfig;
import org.drools.repository.AssetItem;
import org.drools.repository.RulesRepository;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.*;
import static org.drools.guvnor.server.maven.cache.GuvnorArtifactCacheSupport.*;

public final class ServiceWarGenerator {

    private static final Logger log = LoggerFactory.getLogger(ServiceWarGenerator.class);

    private ServiceWarGenerator() {
    }

    public static void buildWar(final ServiceConfig config, final RulesRepository repository, final OutputStream out) {
        final Map<String, File> models = new HashMap<String, File>();
        for (final ServiceConfig.AssetReference model : config.getModels()) {
            try {
                final AssetItem asset = repository.loadAssetByUUID(model.getUrl());
                final File temp = File.createTempFile(asset.getBinaryContentAttachmentFileName(), ".jar");
                temp.deleteOnExit();
                final FileOutputStream tempStream = new FileOutputStream(temp);
                IOUtils.copy(asset.getBinaryContentAttachment(), tempStream);
                tempStream.flush();
                tempStream.close();
                models.put(asset.getBinaryContentAttachmentFileName(), temp);
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
        buildWar(config, models, out);
    }

    public static void buildWar(final ServiceConfig config, final Map<String, File> models, final OutputStream out) {
        checkNotNull(config);
        checkNotNull(out);

        final String xmlCamelServer;
        if (config.getProtocol().equals(ServiceConfig.Protocol.REST)) {
            xmlCamelServer = getResourceContent("servicewar/camel-rest-server.xml.template");
        } else {
            xmlCamelServer = getResourceContent("servicewar/camel-ws-server.xml.template");
        }
        final String xmlBeans = getResourceContent("servicewar/beans.xml.template");
        final String xmlTempKservices = getResourceContent("servicewar/knowledge-services.xml.template");
        final String xmlWeb = getResourceContent("servicewar/web.xml.template");

        final StringBuilder sbRes = new StringBuilder();
        for (ServiceConfig.AssetReference assetRef : config.getResources()) {
            sbRes.append("            <drools:resource  type=\"")
                    .append(assetRef.getFormat())
                    .append("\" source=\"")
                    .append(assetRef.getUrl())
                    .append("\" />\n");
        }

        final String temp = xmlTempKservices.replace("{__drools__resources__here__}", sbRes.toString());
        final String xmlKservices = temp.replace("{__polling_frequency__}", String.valueOf(config.getPollingFrequency()));

        final WebArchive archive = ShrinkWrap.create(WebArchive.class, "drools-service.war")
                .add(new StringAsset(xmlBeans), "WEB-INF/classes/beans.xml")
                .add(new StringAsset(xmlCamelServer), "WEB-INF/classes/camel-server.xml")
                .add(new StringAsset(xmlKservices), "WEB-INF/classes/knowledge-services.xml")
                .add(new StringAsset(xmlWeb), "WEB-INF/classes/web.xml");

        for (final File lib : resolveArtifacts(config.getExcludedArtifacts())) {
            archive.addAsLibraries(lib);
        }

        if (models != null && models.size() > 0) {
            for (final Map.Entry<String, File> model : models.entrySet()) {
                archive.addAsLibrary(model.getValue(), model.getKey());
            }
        }

        try {
            archive.as(ZipExporter.class).exportTo(out);
            out.flush();
            out.close();
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private static String getResourceContent(final String fileName) {
        try {
            ByteArrayOutputStream outContent = new ByteArrayOutputStream();
            BufferedInputStream inContent = new BufferedInputStream(ServiceWarGenerator.class.getClassLoader().getResourceAsStream(fileName));
            IOUtils.copy(inContent, outContent);

            return outContent.toString();
        } catch (IOException ex) {
            throw new IllegalArgumentException("Error " + fileName, ex);
        }
    }
}