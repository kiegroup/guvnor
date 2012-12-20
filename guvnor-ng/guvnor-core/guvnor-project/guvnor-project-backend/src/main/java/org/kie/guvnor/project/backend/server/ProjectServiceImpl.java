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

package org.kie.guvnor.project.backend.server;

import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.commons.io.IOService;
import org.kie.commons.java.nio.file.Files;
import org.kie.guvnor.commons.data.workingset.WorkingSetSettings;
import org.kie.guvnor.project.model.GroupArtifactVersionModel;
import org.kie.guvnor.project.service.ProjectService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import java.io.IOException;
import java.util.Collection;

import static java.util.Collections.emptyList;

@Service
@ApplicationScoped
public class ProjectServiceImpl
        implements ProjectService {

    private static final String SOURCE_FILENAME = "src";
    private static final String POM_FILENAME = "pom.xml";
    private static final String KMODULE_FILENAME = "src/main/resources/META-INF/kmodule.xml";
    private IOService ioService;
    private Paths paths;


    public ProjectServiceImpl() {
        // Boilerplate sacrifice for Weld
    }

    public ProjectServiceImpl(final @Named("ioStrategy") IOService ioService,
                              final Paths paths) {
        this.ioService = ioService;
        this.paths = paths;
    }


    @Override
    public Collection<Path> listProjectResources(final Path project) {
        //TODO {porcelli}
        return emptyList();
    }

    @Override
    public WorkingSetSettings loadWorkingSetConfig(final Path project) {
        //TODO {porcelli}
        return new WorkingSetSettings();
    }

    @Override
    public GroupArtifactVersionModel loadGav(final Path path) {
        try {
            return new GroupArtifactVersionModelContentHandler().toModel(ioService.readAllString(paths.convert(path)));
        } catch (IOException e) {
            e.printStackTrace();  //TODO Need to use the Problems screen for these -Rikkola-
        } catch (XmlPullParserException e) {
            e.printStackTrace();  //TODO Need to use the Problems screen for these -Rikkola-
        }
        return null;

    }

    @Override
    public Path resolveProject(final Path resource) {

        //Null resource paths cannot resolve to a Project
        if (resource == null) {
            return null;
        }

        //A project root is the folder containing the pom.xml file. This will be the parent of the "src" folder
        org.kie.commons.java.nio.file.Path p = paths.convert(resource).normalize();
        if (Files.isRegularFile(p)) {
            p = p.getParent();
        }
        while (p.getNameCount() > 0 && !p.getFileName().toString().equals(SOURCE_FILENAME)) {
            p = p.getParent();
        }
        if (p.getNameCount() == 0) {
            return null;
        }
        p = p.getParent();
        if (p.getNameCount() == 0 || p == null) {
            return null;
        }
        final org.kie.commons.java.nio.file.Path pomPath = p.resolve(POM_FILENAME);
        if (!Files.exists(pomPath)) {
            return null;
        }
        final org.kie.commons.java.nio.file.Path kmodulePath = p.resolve(KMODULE_FILENAME);
        if (!Files.exists(kmodulePath)) {
            return null;
        }
        return PathFactory.newPath(p.toUri().toString());
    }

}
