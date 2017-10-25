/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.common.services.project.backend.server.utils;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.*;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.apache.maven.model.*;
import org.apache.maven.model.Build;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.Repository;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.guvnor.common.services.project.backend.server.utils.configuration.ConfigurationKey;
import org.guvnor.common.services.project.backend.server.utils.configuration.ConfigurationStrategy;
import org.guvnor.common.services.project.model.*;

@ApplicationScoped
public class POMContentHandler {

    private String COMPILE = "compile";
    private String COMPILER_ID ="compilerId";
    private String CONFIGURATION ="configuration";
    private String COMPILER ="javac";
    private String KJAR ="kjar";
    private String TRUE = "true";
    private String MAVEN_SKIP = "skip";
    private String MAVEN_SKIP_MAIN = "skipMain";
    private String MAVEN_DEFAULT_COMPILE = "default-compile";
    private String MAVEN_PHASE_NONE = "none";
    private String MAVEN_PLUGIN_CONFIGURATION = "configuration";
    private final List<ConfigurationStrategy> configurationStrategies = new ArrayList<>();

    public POMContentHandler(Iterator<ConfigurationStrategy> configurations) {
        configurations.forEachRemaining(s->this.configurationStrategies.add(s));
    }

    @Inject
    public POMContentHandler(Instance<ConfigurationStrategy> configuration) {
        this(configuration.iterator());
    }

    public POMContentHandler() {
    }

    public String toString(final POM pomModel)
            throws IOException {
        return toString(pomModel,
                        new Model());
    }

    private String toString(final POM pom,
                            final Model model) throws IOException {
        ConfigurationStrategy configurationStrategy = configurationStrategies.get(0);
        Map<ConfigurationKey, String> conf = configurationStrategy.loadConfiguration();

        addDependencies(pom, model, conf);

        Build build = new Build();
        model.setBuild(build);
        build.addPlugin(getKieMavenPlugin(conf));
        build.addPlugin(getNewCompilerPlugin(conf));
        build.addPlugin(getDisableMavenCompiler(conf));
        model.setPackaging(KJAR);


        model.setName(pom.getName());
        model.setDescription(pom.getDescription());
        model.setArtifactId(pom.getGav().getArtifactId());
        model.setModelVersion(pom.getModelVersion());
        model.setGroupId(pom.getGav().getGroupId());
        model.setVersion(pom.getGav().getVersion());
        model.setParent(getParent(pom));
        model.setModules(getModules(pom));
        model.setRepositories(getRepositories(pom));

        StringWriter stringWriter = new StringWriter();
        new MavenXpp3Writer().write(stringWriter,
                                    model);
        return stringWriter.toString();
    }

    private void addDependencies(POM pom, Model model, Map<ConfigurationKey, String> conf) {
        String kieVersion = conf.get(ConfigurationKey.KIE_VERSION);
        if(pom.getDependencies().isEmpty()) {
            List<Dependency> dependencies = new ArrayList<Dependency>();
            dependencies.add(getDependency(conf, "org.kie", "kie-api", kieVersion, "provided"));
            dependencies.add(getDependency(conf, "org.optaplanner", "optaplanner-core", kieVersion, "provided"));
            dependencies.add(getDependency(conf, "org.optaplanner", "org.optaplanner", kieVersion, "provided"));
            dependencies.add(getDependency(conf, "junit", "junit", "4.12", "test"));
            model.setDependencies(dependencies);
        }else {
            List<org.guvnor.common.services.project.model.Dependency> guvDeps = new ArrayList<>();
            guvDeps.add(getGuvDependency(conf, "org.kie", "kie-api", kieVersion, "provided"));
            guvDeps.add(getGuvDependency(conf, "org.optaplanner", "optaplanner-core", kieVersion, "provided"));
            guvDeps.add(getGuvDependency(conf, "org.optaplanner", "org.optaplanner", kieVersion, "provided"));
            guvDeps.add(getGuvDependency(conf, "junit", "junit", "4.12", "test"));
            pom.setDependencies(guvDeps);
            new DependencyUpdater(model.getDependencies()).updateDependencies(pom.getDependencies());
        }
    }

    private Dependency getDependency(Map<ConfigurationKey, String> conf, String groupID, String artifactID, String version, String scope) {
        Dependency dep = new Dependency();
        dep.setGroupId(groupID);
        dep.setArtifactId(artifactID);
        dep.setVersion(version);
        dep.setScope(scope);
        return dep;
    }

    private org.guvnor.common.services.project.model.Dependency getGuvDependency(Map<ConfigurationKey, String> conf, String groupID, String artifactID, String version, String scope) {
        org.guvnor.common.services.project.model.Dependency dep = new org.guvnor.common.services.project.model.Dependency();
        dep.setGroupId(groupID);
        dep.setArtifactId(artifactID);
        dep.setVersion(version);
        dep.setScope(scope);
        return dep;
    }


    protected Plugin getNewCompilerPlugin(Map<ConfigurationKey, String> conf) {

        Plugin newCompilerPlugin = new Plugin();
        newCompilerPlugin.setGroupId(conf.get(ConfigurationKey.ALTERNATIVE_COMPILER_PLUGINS));
        newCompilerPlugin.setArtifactId(conf.get(ConfigurationKey.ALTERNATIVE_COMPILER_PLUGIN));
        newCompilerPlugin.setVersion(conf.get(ConfigurationKey.ALTERNATIVE_COMPILER_PLUGIN_VERSION));

        PluginExecution execution = new PluginExecution();
        execution.setId(COMPILE);
        execution.setGoals(Arrays.asList(COMPILE));
        execution.setPhase(COMPILE);

        Xpp3Dom compilerId = new Xpp3Dom(COMPILER_ID);
        compilerId.setValue(COMPILER);
        Xpp3Dom configuration = new Xpp3Dom(CONFIGURATION);
        configuration.addChild(compilerId);

        execution.setConfiguration(configuration);
        newCompilerPlugin.setExecutions(Arrays.asList(execution));

        return newCompilerPlugin;
    }

    protected Plugin getKieMavenPlugin(Map<ConfigurationKey, String> conf) {
        String kieVersion = conf.get(ConfigurationKey.KIE_VERSION);
        Plugin kieMavenPlugin = new Plugin();
        kieMavenPlugin.setGroupId("org.kie");
        kieMavenPlugin.setArtifactId("kie-maven-plugin");
        kieMavenPlugin.setVersion(kieVersion);
        kieMavenPlugin.setExtensions(true);
        return kieMavenPlugin;
    }



    protected Plugin getDisableMavenCompiler(Map<ConfigurationKey, String> conf) {
        Plugin plugin = new Plugin();
        plugin.setArtifactId(conf.get(ConfigurationKey.MAVEN_COMPILER_PLUGIN));

        Xpp3Dom skipMain = new Xpp3Dom(MAVEN_SKIP_MAIN);
        skipMain.setValue(TRUE);
        Xpp3Dom skip = new Xpp3Dom(MAVEN_SKIP);
        skip.setValue(TRUE);

        Xpp3Dom configuration = new Xpp3Dom(MAVEN_PLUGIN_CONFIGURATION);
        configuration.addChild(skipMain);
        configuration.addChild(skip);

        plugin.setConfiguration(configuration);

        PluginExecution exec = new PluginExecution();
        exec.setId(MAVEN_DEFAULT_COMPILE);
        exec.setPhase(MAVEN_PHASE_NONE);
        List<PluginExecution> executions = new ArrayList<>();
        executions.add(exec);
        plugin.setExecutions(executions);
        return  plugin;
    }




    private Build getBuild(final POM pom,
                           final Model model) {
        return new BuildContentHandler().update(pom.getBuild(),
                                                model.getBuild());
    }

    private ArrayList<Repository> getRepositories(final POM pom) {
        ArrayList<Repository> result = new ArrayList<Repository>();
        for (org.guvnor.common.services.project.model.Repository repository : pom.getRepositories()) {
            result.add(fromClientModelToPom(repository));
        }
        return result;
    }

    private ArrayList<String> getModules(final POM pom) {
        ArrayList<String> result = new ArrayList<String>();
        if (pom.getModules() != null) {
            for (String module : pom.getModules()) {
                result.add(module);
            }
        }
        return result;
    }

    private Parent getParent(final POM pom) {
        if (pom.getParent() == null) {
            return null;
        } else {
            Parent parent = new Parent();
            parent.setGroupId(pom.getParent().getGroupId());
            parent.setArtifactId(pom.getParent().getArtifactId());
            parent.setVersion(pom.getParent().getVersion());
            return parent;
        }
    }

    /**
     * @param gavModel The model that is saved
     * @param originalPomAsText The original pom in text form, since the guvnor POM model does not cover all the pom.xml features.
     * @return pom.xml for saving, The original pom.xml with the fields edited in gavModel replaced.
     * @throws IOException
     */
    public String toString(final POM gavModel,
                           final String originalPomAsText) throws IOException, XmlPullParserException {

        return toString(gavModel,
                        new MavenXpp3Reader().read(new StringReader(originalPomAsText)));
    }

    private Repository fromClientModelToPom(final org.guvnor.common.services.project.model.Repository from) {
        Repository to = new Repository();
        to.setId(from.getId());
        to.setName(from.getName());
        to.setUrl(from.getUrl());

        return to;
    }

    public POM toModel(final String pomAsString) throws IOException, XmlPullParserException {
        Model model = new MavenXpp3Reader().read(new StringReader(pomAsString));

        POM pomModel = new POM(
                model.getName(),
                model.getDescription(),
                new GAV(
                        (model.getGroupId() == null ? model.getParent().getGroupId() : model.getGroupId()),
                        (model.getArtifactId() == null ? model.getParent().getArtifactId() : model.getArtifactId()),
                        (model.getVersion() == null ? model.getParent().getVersion() : model.getVersion())
                )
        );

        pomModel.setPackaging(model.getPackaging());

        if (model.getParent() != null) {
            pomModel.setParent(new GAV(model.getParent().getGroupId(),
                                       model.getParent().getArtifactId(),
                                       model.getParent().getVersion()));
        }

        pomModel.getModules().clear();
        for (String module : model.getModules()) {
            pomModel.getModules().add(module);
            pomModel.setPackaging("pom");
        }
        for (Repository repository : model.getRepositories()) {
            pomModel.addRepository(fromPomModelToClientModel(repository));
        }

        pomModel.setDependencies(new DependencyContentHandler().fromPomModelToClientModel(model.getDependencies()));

        pomModel.setBuild(new BuildContentHandler().fromPomModelToClientModel(model.getBuild()));

        return pomModel;
    }

    private org.guvnor.common.services.project.model.Repository fromPomModelToClientModel(final Repository from) {
        org.guvnor.common.services.project.model.Repository to = new org.guvnor.common.services.project.model.Repository();

        to.setId(from.getId());
        to.setName(from.getName());
        to.setUrl(from.getUrl());

        return to;
    }
}
