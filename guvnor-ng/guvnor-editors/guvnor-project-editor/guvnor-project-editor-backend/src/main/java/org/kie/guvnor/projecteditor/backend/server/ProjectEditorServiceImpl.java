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

package org.kie.guvnor.projecteditor.backend.server;

import org.jboss.errai.bus.server.annotations.Service;
import org.kie.commons.io.IOService;
import org.kie.guvnor.projecteditor.model.GroupArtifactVersionModel;
import org.kie.guvnor.projecteditor.model.KProjectModel;
import org.kie.guvnor.projecteditor.model.builder.Messages;
import org.kie.guvnor.projecteditor.service.ProjectEditorService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

@Service
@ApplicationScoped
public class ProjectEditorServiceImpl
        implements ProjectEditorService {


    private IOService ioService;
    private Paths paths;
    private KProjectEditorContentHandler projectEditorContentHandler;
    private GroupArtifactVersionModelContentHandler gavModelContentHandler;


    public ProjectEditorServiceImpl() {
        // Weld needs this for proxying.
    }

    @Inject
    public ProjectEditorServiceImpl(final @Named("ioStrategy") IOService ioService,
                                    final Paths paths,
                                    final KProjectEditorContentHandler projectEditorContentHandler,
                                    final GroupArtifactVersionModelContentHandler gavModelContentHandler) {
        this.ioService = ioService;
        this.paths = paths;
        this.projectEditorContentHandler = projectEditorContentHandler;
        this.gavModelContentHandler = gavModelContentHandler;
    }


    @Override
    public Path newProject(String name) {
        Path pathToPom = PathFactory.newPath(getGavURI(name));
        saveGav(pathToPom, new GroupArtifactVersionModel());
        return setUpProjectStructure(pathToPom);
    }

    @Override
    public Path setUpProjectStructure(final Path pathToPom) {

        // Create project structure
        final org.kie.commons.java.nio.file.Path directory = getPomDirectoryPath(pathToPom);

        org.kie.commons.java.nio.file.Path resolve = directory.resolve("src/kbases");
        ioService.createDirectory(resolve);

        ioService.createDirectory(directory.resolve( "src/main/java"));
        final org.kie.commons.java.nio.file.Path pathToKProjectXML = directory.resolve( "src/main/resources/META-INF/kproject.xml");
        saveKProject(pathToKProjectXML, new KProjectModel());

        ioService.createDirectory(directory.resolve("src/test/java"));
        ioService.createDirectory(directory.resolve("src/test/resources"));

        return paths.convert(pathToKProjectXML);
    }

    @Override
    public void saveKProject(final Path path,
                             final KProjectModel model) {
        saveKProject(paths.convert(path), model);
    }

    @Override
    public void saveGav(final Path path,
                        final GroupArtifactVersionModel gavModel) {
        ioService.write(paths.convert(path), gavModelContentHandler.toString(gavModel));
    }

    @Override
    public KProjectModel loadKProject(final Path path) {
        return projectEditorContentHandler.toModel(ioService.readAllString(paths.convert(path)));
    }

    @Override
    public Messages build(Path pathToKProjectXML) {

        Builder builder = new Builder(pathToKProjectXML, ioService);

        return builder.build();
    }

    @Override
    public GroupArtifactVersionModel loadGav(final Path path) {
        return gavModelContentHandler.toModel(ioService.readAllString(paths.convert(path)));
    }

    @Override
    public Path pathToRelatedKProjectFileIfAny(final Path pathToPomXML) {
        final org.kie.commons.java.nio.file.Path directory = getPomDirectoryPath(pathToPomXML);

        final org.kie.commons.java.nio.file.Path pathToKProjectXML = directory.resolve("/src/main/resources/META-INF/kproject.xml");

        if (ioService.exists(pathToKProjectXML)) {
            return paths.convert(pathToKProjectXML);
        } else {
            return null;
        }
    }

    private void saveKProject(final org.kie.commons.java.nio.file.Path path,
                              final KProjectModel model) {
        ioService.write(path, projectEditorContentHandler.toString(model));
    }

    private org.kie.commons.java.nio.file.Path getPomDirectoryPath(final Path pathToPomXML) {
        return paths.convert(pathToPomXML).getParent();
    }

    private String getGavURI(String name) {
        return "default://uf-playground/" + name + "/pom.xml";
    }
}
