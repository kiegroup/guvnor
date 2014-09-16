package org.guvnor.common.services.project.backend.server;
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

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Properties;
import javax.enterprise.context.Dependent;

import org.apache.maven.model.*;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.POM;

import javax.enterprise.context.Dependent;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Properties;

@Dependent
public class POMContentHandler {

    private static String MULTI_MODULE = "pom";
    private static String PACKAGING = "kjar";
    private static String KIE_PLUGIN_VERSION_FILENAME = "/kie-plugin-version.properties";
    private static String KIE_PLUGIN_VERSION_PROPERTY_NAME = "kie_plugin_version";

    private static String kieMavenPluginGroupId = "org.kie";
    private static String kieMavenPluginArtifactId = "kie-maven-plugin";
    private static String kieMavenPluginVersion = getKiePluginVersion();

    private static Plugin kieMavenPlugin = getKieMavenPlugin();

    public POMContentHandler() {
        // Weld needs this for proxying.
    }

    public String toString(POM pomModel)
            throws IOException {
        return toString(pomModel, new Model());
    }

    private String toString(POM pom,
                            Model model)
            throws IOException {
        model.setName(pom.getName());
        model.setDescription(pom.getDescription());
        model.setArtifactId(pom.getGav().getArtifactId());
        model.setModelVersion(pom.getModelVersion());

        if (pom.isMultiModule()) { // if it is a parent pom
            model.setPackaging(MULTI_MODULE);
            model.setGroupId(pom.getGav().getGroupId());
            model.setVersion(pom.getGav().getVersion());
            model.getModules().clear();
            for (String module : pom.getModules()) {
                model.addModule(module);
            }
            model.getRepositories().clear();
            for (org.guvnor.common.services.project.model.Repository repository : pom.getRepositories()) {
                model.addRepository(fromClientModelToPom(repository));
            }
        } else { // If it is a kjar
            model.setPackaging(PACKAGING);
            if (pom.getParent() != null) {
                Parent parent = new Parent();
                parent.setGroupId(pom.getParent().getGroupId());
                parent.setArtifactId(pom.getParent().getArtifactId());
                parent.setVersion(pom.getParent().getVersion());
                model.setParent(parent);
            }
            model.setGroupId(pom.getGav().getGroupId());
            model.setVersion(pom.getGav().getVersion());

            Build build = model.getBuild();
            if (build == null) {
                build = new Build();
                model.setBuild(build);
            }
            if (!build.getPlugins().contains(kieMavenPlugin)) {
                build.addPlugin(kieMavenPlugin);
            }

            model.getDependencies().clear();
            for (org.guvnor.common.services.project.model.Dependency dependency : pom.getDependencies()) {
                model.addDependency(fromClientModelToPom(dependency));
            }

        }

        StringWriter stringWriter = new StringWriter();
        new MavenXpp3Writer().write(stringWriter, model);

        return stringWriter.toString();
    }

    /**
     * @param gavModel          The model that is saved
     * @param originalPomAsText The original pom in text form, since the guvnor POM model does not cover all the pom.xml features.
     * @return pom.xml for saving, The original pom.xml with the fields edited in gavModel replaced.
     * @throws IOException
     */
    public String toString(POM gavModel,
                           String originalPomAsText)
            throws IOException, XmlPullParserException {

        return toString(gavModel, new MavenXpp3Reader().read(new StringReader(originalPomAsText)));
    }

    private Repository fromClientModelToPom(org.guvnor.common.services.project.model.Repository from) {
        Repository to = new Repository();
        to.setId(from.getId());
        to.setName(from.getName());
        to.setUrl(from.getUrl());

        return to;
    }

    public POM toModel(String pomAsString)
            throws IOException, XmlPullParserException {
        Model model = new MavenXpp3Reader().read(new StringReader(pomAsString));

        POM gavModel = new POM(
                model.getName(),
                model.getDescription(),
                new GAV(
                        (model.getGroupId() == null ? model.getParent().getGroupId() : model.getGroupId()),
                        (model.getArtifactId() == null ? model.getParent().getArtifactId() : model.getArtifactId()),
                        (model.getVersion() == null ? model.getParent().getVersion() : model.getVersion())
                )
        );

        if (model.getParent() != null) {
            gavModel.setParent(new GAV(model.getParent().getGroupId(), model.getParent().getArtifactId(), model.getParent().getVersion()));
        }

        gavModel.getModules().clear();
        for (String module : model.getModules()) {
            gavModel.getModules().add(module);
            gavModel.setMultiModule( true );
        }
        for (Repository repository : model.getRepositories()) {
            gavModel.addRepository(fromPomModelToClientModel(repository));
        }

        for (Dependency dependency : model.getDependencies()) {
            gavModel.getDependencies().add(fromPomModelToClientModel(dependency));
        }

        return gavModel;
    }

    private org.guvnor.common.services.project.model.Repository fromPomModelToClientModel(Repository from) {
        org.guvnor.common.services.project.model.Repository to = new org.guvnor.common.services.project.model.Repository();

        to.setId(from.getId());
        to.setName(from.getName());
        to.setUrl(from.getUrl());

        return to;
    }

    private org.guvnor.common.services.project.model.Dependency fromPomModelToClientModel(Dependency from) {
        org.guvnor.common.services.project.model.Dependency dependency = new org.guvnor.common.services.project.model.Dependency();

        dependency.setArtifactId(from.getArtifactId());
        dependency.setGroupId(from.getGroupId());
        dependency.setVersion(from.getVersion());

        return dependency;
    }

    private Dependency fromClientModelToPom(org.guvnor.common.services.project.model.Dependency from) {
        Dependency dependency = new Dependency();

        dependency.setArtifactId(from.getArtifactId());
        dependency.setGroupId(from.getGroupId());
        dependency.setVersion(from.getVersion());

        return dependency;
    }

    private static Plugin getKieMavenPlugin() {
        final Plugin plugin = new Plugin();
        plugin.setGroupId(kieMavenPluginGroupId);
        plugin.setArtifactId(kieMavenPluginArtifactId);
        plugin.setVersion(kieMavenPluginVersion);
        plugin.setExtensions(true);
        return plugin;
    }

    //Used by tests; hence public accessor
    public static String getKiePluginVersion() {
        Properties p = new Properties();
        try {
            p.load(POMContentHandler.class.getResourceAsStream(KIE_PLUGIN_VERSION_FILENAME));
        } catch (IOException e) {

        }
        return p.getProperty(KIE_PLUGIN_VERSION_PROPERTY_NAME);
    }

}
