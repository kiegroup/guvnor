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
import javax.enterprise.context.Dependent;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.Repository;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.POM;
import org.kie.scanner.embedder.MavenProjectLoader;

@Dependent
public class POMContentHandler {

    public POMContentHandler() {
        // Weld needs this for proxying.
    }

    public String toString(POM pomModel)
            throws IOException {
        return toString(pomModel,new Model());
    }

    private String toString(POM pom, Model model)
            throws IOException {
        model.setGroupId(pom.getGav().getGroupId());
        model.setArtifactId(pom.getGav().getArtifactId());
        model.setVersion(pom.getGav().getVersion());
        model.setModelVersion(pom.getModelVersion());

        for (org.guvnor.common.services.project.model.Repository repository : pom.getRepositories()) {
            model.addRepository(fromClientModelToPom(repository));
        }

        for (org.guvnor.common.services.project.model.Dependency dependency : pom.getDependencies()) {
            model.addDependency(fromClientModelToPom(dependency));
        }

        StringWriter stringWriter = new StringWriter();
        new MavenXpp3Writer().write(stringWriter, model);

        return stringWriter.toString();
    }

    /**
     * @param gavModel The model that is saved
     * @param originalPomAsText The original pom in text form, since the guvnor POM model does not cover all the pom.xml features.
     * @return pom.xml for saving, The original pom.xml with the fields edited in gavModel replaced.
     * @throws IOException
     */
    public String toString(POM gavModel, String originalPomAsText)
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
                new GAV(
                        (model.getGroupId() == null ? model.getParent().getGroupId() : model.getGroupId()),
                        (model.getGroupId() == null ? model.getParent().getArtifactId() : model.getArtifactId()),
                        (model.getGroupId() == null ? model.getParent().getVersion() : model.getVersion())
                )
        );

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

}
