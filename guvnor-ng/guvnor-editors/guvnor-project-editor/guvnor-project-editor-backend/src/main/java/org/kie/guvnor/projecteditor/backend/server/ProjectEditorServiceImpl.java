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
import org.kie.builder.*;
import org.kie.commons.java.nio.file.DirectoryStream;
import org.kie.commons.java.nio.file.Files;
import org.kie.commons.java.nio.file.Paths;
import org.kie.guvnor.projecteditor.model.KProjectModel;
import org.kie.guvnor.projecteditor.service.ProjectEditorService;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.VFSService;
import org.uberfire.backend.vfs.impl.PathImpl;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@Service
@ApplicationScoped
public class ProjectEditorServiceImpl
        implements ProjectEditorService {

    @Inject
    private VFSService vfsService;

    @Override
    public Path makeNew(String name) {

        // Create project structure
        Path directory = vfsService.createDirectory(new PathImpl(name, projectURI(name)));

        vfsService.createDirectory(new PathImpl(name, directory.toURI() + "/src/kbases"));

        vfsService.createDirectory(new PathImpl(name, directory.toURI() + "/src/main/java"));
        PathImpl path = new PathImpl(name, directory.toURI() + "/src/main/resources/META-INF/kproject.xml");
        save(path, new KProjectModel());

        vfsService.createDirectory(new PathImpl(name, directory.toURI() + "/src/test/java"));
        vfsService.createDirectory(new PathImpl(name, directory.toURI() + "/src/test/resources"));

        return path;
    }

    @Override
    public void save(Path path, KProjectModel model) {
        vfsService.write(path, ProjectEditorContentHandler.toString(model));
    }

    @Override
    public KProjectModel load(Path path) {
        return ProjectEditorContentHandler.toModel(vfsService.readAllString(path));
    }

    @Override
    public void build(Path pathToKProjectXML) {

        KieServices kieServices = KieServices.Factory.get();
        KieFactory kieFactory = KieFactory.Factory.get();
        KieFileSystem kieFileSystem = kieFactory.newKieFileSystem();

        DirectoryStream<org.kie.commons.java.nio.file.Path> directoryStream = Files.newDirectoryStream(Paths.get(getProjectURI(pathToKProjectXML)));

        visitPaths(getProjectName(pathToKProjectXML), kieFileSystem, directoryStream);

        Messages messages = kieServices.newKieBuilder(kieFileSystem).build();

        for (Message message : messages.getInsertedMessages()) {
            System.out.println("EEEEEEEEEEE" + message.toString());
        }
        for (Message message : messages.getDeletedMessages()) {
            System.out.println("EEEEEEEEEEE" + message.toString());
        }
    }

    private String getProjectURI(Path path) {
        return path.toURI().substring(0, path.toURI().lastIndexOf("/src"));
    }

    private void visitPaths(String projectName, KieFileSystem kieFileSystem, DirectoryStream<org.kie.commons.java.nio.file.Path> directoryStream) {
        for (org.kie.commons.java.nio.file.Path path : directoryStream) {
            if (Files.isDirectory(path)) {
                visitPaths(projectName, kieFileSystem, Files.newDirectoryStream(path));
            } else {
                if (path.toUri().toString().endsWith(KieProject.KPROJECT_JAR_PATH)) {
                    System.out.println("ADDING krpoject.xml to " + KieProject.KPROJECT_JAR_PATH);
                    kieFileSystem.write(KieProject.KPROJECT_JAR_PATH, vfsService.readAllString(org.uberfire.backend.vfs.Paths.fromURI(path.toUri().toString())));
                } else {
                    String pathAsString = stripPath(projectName, path);
                    System.out.println("ADDING " + pathAsString);

                    kieFileSystem.write(pathAsString, vfsService.readAllString(org.uberfire.backend.vfs.Paths.fromURI(path.toUri().toString())));
                }
            }
        }
    }

    private String stripPath(String projectName, org.kie.commons.java.nio.file.Path path) {
        String s = path.toString();
        String substring = s.substring(projectName.length() + 2);
        return substring;
    }

    private String projectURI(String name) {
        return "default://uf-playground/" + name;
    }

    private String getProjectName(Path path) {
        String s = path.toURI();
        String substring = s.substring(s.indexOf("uf-playground/") + "uf-playground/".length());
        return substring.substring(0, substring.indexOf("/"));
    }

}
