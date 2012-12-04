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
import org.kie.guvnor.projecteditor.model.GroupArtifactVersionModel;
import org.kie.guvnor.projecteditor.model.KProjectModel;
import org.kie.guvnor.projecteditor.model.builder.Messages;
import org.kie.guvnor.projecteditor.service.ProjectEditorService;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.VFSService;
import org.uberfire.backend.vfs.impl.PathImpl;

import javax.enterprise.context.ApplicationScoped;

@Service
@ApplicationScoped
public class ProjectEditorServiceImpl
        implements ProjectEditorService {

    private final VFSService vfsService;
    private final KProjectEditorContentHandler projectEditorContentHandler;
    private final GroupArtifactVersionModelContentHandler gavModelContentHandler;

    public ProjectEditorServiceImpl(VFSService vfsService,
                                    KProjectEditorContentHandler projectEditorContentHandler,
                                    GroupArtifactVersionModelContentHandler gavModelContentHandler) {
        this.vfsService = vfsService;
        this.projectEditorContentHandler = projectEditorContentHandler;
        this.gavModelContentHandler = gavModelContentHandler;
    }

    @Override
    public Path setUpProjectStructure(Path pathToPom) {

        // Create project structure
        Path directory = getPomPath(pathToPom);

        vfsService.createDirectory(new PathImpl(directory.toURI() + "/src/kbases"));

        vfsService.createDirectory(new PathImpl(directory.toURI() + "/src/main/java"));
        PathImpl pathToKProjectXML = new PathImpl(directory.toURI() + "/src/main/resources/META-INF/kproject.xml");
        saveKProject(pathToKProjectXML, new KProjectModel());

        vfsService.createDirectory(new PathImpl(directory.toURI() + "/src/test/java"));
        vfsService.createDirectory(new PathImpl(directory.toURI() + "/src/test/resources"));

        return pathToKProjectXML;
    }

    @Override
    public void saveKProject(Path path, KProjectModel model) {
        vfsService.write(path, projectEditorContentHandler.toString(model));
    }

    @Override
    public void saveGav(Path path, GroupArtifactVersionModel gavModel) {
        vfsService.write(path, gavModelContentHandler.toString(gavModel));
    }

    @Override
    public KProjectModel loadKProject(Path path) {
        return projectEditorContentHandler.toModel(vfsService.readAllString(path));
    }

    @Override
    public Messages build(Path pathToKProjectXML) {

        Builder builder = new Builder(pathToKProjectXML, vfsService);

        return builder.build();
    }

    @Override
    public GroupArtifactVersionModel loadGav(Path path) {
        return gavModelContentHandler.toModel(vfsService.readAllString(path));
    }

    @Override
    public Path pathToRelatedKProjectFileIfAny(Path pathToPomXML) {
        PathImpl directory = getPomPath(pathToPomXML);

        PathImpl pathToKProjectXML = new PathImpl(directory.toURI() + "/src/main/resources/META-INF/kproject.xml");

        if (vfsService.exists(pathToKProjectXML)) {
            return pathToKProjectXML;
        } else {
            return null;
        }
    }

    private PathImpl getPomPath(Path pathToPomXML) {
        String s = pathToPomXML.toURI();
        return new PathImpl(s.substring(0, s.length() - "/pom.xml".length()));
    }
}
