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
import org.drools.guvnor.client.asseteditor.drools.serviceconfig.AssetReference;
import org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceConfig;
import org.drools.repository.AssetItem;
import org.drools.repository.RulesRepository;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.mvel2.templates.CompiledTemplate;
import org.mvel2.templates.SimpleTemplateRegistry;
import org.mvel2.templates.TemplateRegistry;
import org.mvel2.templates.TemplateRuntime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.*;
import static org.drools.guvnor.server.maven.cache.GuvnorArtifactCacheSupport.*;
import static org.mvel2.templates.TemplateCompiler.*;

public final class ServiceWarGenerator {

    private static final Logger log = LoggerFactory.getLogger(ServiceWarGenerator.class);

    private static final Map<String, CompiledTemplate> templateMap = new HashMap<String, CompiledTemplate>(5) {{
        put("WEB-INF/classes/beans.xml", compileTemplate(getResourceContent("servicewar/beans.xml.template")));
        put("WEB-INF/classes/camel-server.xml", compileTemplate(getResourceContent("servicewar/camel-server.xml.template")));
        put("WEB-INF/classes/knowledge-services.xml", compileTemplate(getResourceContent("servicewar/knowledge-services.xml.template")));
        put("WEB-INF/web.xml", compileTemplate(getResourceContent("servicewar/web.xml.template")));
    }};

    private static final TemplateRegistry templateRegistry = new SimpleTemplateRegistry() {{
        addNamedTemplate("ksession.uri", compileTemplate(getResourceContent("servicewar/ksession.uri.template")));
        addNamedTemplate("kagent.def", compileTemplate(getResourceContent("servicewar/kagent.def.template")));
        addNamedTemplate("ksession.def", compileTemplate(getResourceContent("servicewar/ksession.def.template")));
        addNamedTemplate("resource.def", compileTemplate(getResourceContent("servicewar/resource.def.template")));
        addNamedTemplate("kbase-config.def", compileTemplate(getResourceContent("servicewar/kbase-config.def.template")));
    }};

    private ServiceWarGenerator() {
    }

    public static void buildWar(final ServiceConfig config, final RulesRepository repository, final OutputStream out) {
        final Map<String, File> models = new HashMap<String, File>();
        for (final AssetReference model : config.getModels()) {
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

        final Map<String, Object> data = new HashMap<String, Object>() {{
            put("serviceConfig", config);
        }};

        final WebArchive archive = ShrinkWrap.create(WebArchive.class, "drools-service.war");

        for (Map.Entry<String, CompiledTemplate> activeTemplate : templateMap.entrySet()) {
            final String content = (String) TemplateRuntime.execute(activeTemplate.getValue(), null, data, templateRegistry);
            archive.add(new StringAsset(content), activeTemplate.getKey());
        }

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