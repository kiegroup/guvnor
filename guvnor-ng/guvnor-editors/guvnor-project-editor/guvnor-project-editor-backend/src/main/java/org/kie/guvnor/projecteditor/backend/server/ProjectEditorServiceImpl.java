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

import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.commons.io.IOService;
import org.kie.guvnor.projecteditor.model.GroupArtifactVersionModel;
import org.kie.guvnor.projecteditor.model.KModuleModel;
import org.kie.guvnor.projecteditor.model.builder.Messages;
import org.kie.guvnor.projecteditor.service.ProjectEditorService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;

@Service
@ApplicationScoped
public class ProjectEditorServiceImpl
        implements ProjectEditorService {


    private IOService ioService;
    private Paths paths;
    private KModuleEditorContentHandler moduleEditorContentHandler;
    private GroupArtifactVersionModelContentHandler gavModelContentHandler;
    private Event<Messages> messagesEvent;


    public ProjectEditorServiceImpl() {
        // Weld needs this for proxying.
    }

    @Inject
    public ProjectEditorServiceImpl(final @Named("ioStrategy") IOService ioService,
                                    final Paths paths,
                                    final Event<Messages> messagesEvent,
                                    final KModuleEditorContentHandler moduleEditorContentHandler,
                                    final GroupArtifactVersionModelContentHandler gavModelContentHandler) {
        this.ioService = ioService;
        this.paths = paths;
        this.messagesEvent = messagesEvent;
        this.moduleEditorContentHandler = moduleEditorContentHandler;
        this.gavModelContentHandler = gavModelContentHandler;
    }


    @Override
    public Path newProject(String name) {
        return saveGav(PathFactory.newPath(getGavURI(name)), new GroupArtifactVersionModel());
    }

    @Override
    public Path setUpKModuleStructure(final Path pathToPom) {
        try {
            // Create project structure
            final org.kie.commons.java.nio.file.Path directory = getPomDirectoryPath(pathToPom);

            ioService.createDirectory(directory.resolve("src/main/java"));
            ioService.createDirectory(directory.resolve("src/main/resources"));
            final org.kie.commons.java.nio.file.Path pathToKModuleXML = directory.resolve("src/main/resources/META-INF/kmodule.xml");
            saveKModule(pathToKModuleXML, new KModuleModel());

            ioService.createDirectory(directory.resolve("src/test/java"));
            ioService.createDirectory(directory.resolve("src/test/resources"));

            return paths.convert(pathToKModuleXML);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void saveKModule(final Path path,
                            final KModuleModel model) {
        saveKModule(paths.convert(path), model);
    }

    @Override
    public Path saveGav(final Path pathToGAV,
                        final GroupArtifactVersionModel gavModel) {
        try {
            return paths.convert(ioService.write(paths.convert(pathToGAV), gavModelContentHandler.toString(gavModel)));
        } catch (IOException e) {
            e.printStackTrace();  //TODO -Rikkola-
        }
        return null;
    }

    @Override
    public KModuleModel loadKModule(final Path path) {
        return moduleEditorContentHandler.toModel(ioService.readAllString(paths.convert(path)));
    }

    @Override
    public void build(Path pathToKModuleXML) {

        Builder builder = new Builder(getPomDirectoryPath(pathToKModuleXML), loadGav(pathToKModuleXML), ioService, paths, messagesEvent);

        builder.build();
    }

    @Override
    public GroupArtifactVersionModel loadGav(final Path path) {
        try {
            return gavModelContentHandler.toModel(ioService.readAllString(paths.convert(path)));
        } catch (IOException e) {
            e.printStackTrace();  //TODO -Rikkola-
        } catch (XmlPullParserException e) {
            e.printStackTrace();  //TODO -Rikkola-
        }
        return null;

    }

    @Override
    public Path pathToRelatedKModuleFileIfAny(final Path pathToPomXML) {
        final org.kie.commons.java.nio.file.Path directory = getPomDirectoryPath(pathToPomXML);

        final org.kie.commons.java.nio.file.Path pathToKModuleXML = directory.resolve("src/main/resources/META-INF/kmodule.xml");

        if (ioService.exists(pathToKModuleXML)) {
            return paths.convert(pathToKModuleXML);
        } else {
            return null;
        }
    }

    private void saveKModule(final org.kie.commons.java.nio.file.Path path,
                             final KModuleModel model) {
        ioService.write(path, moduleEditorContentHandler.toString(model));
    }

    private org.kie.commons.java.nio.file.Path getPomDirectoryPath(final Path pathToPomXML) {
        return paths.convert(pathToPomXML).getParent();
    }

    private String getGavURI(String name) {
        return "default://uf-playground/" + name + "/pom.xml";
    }
}
