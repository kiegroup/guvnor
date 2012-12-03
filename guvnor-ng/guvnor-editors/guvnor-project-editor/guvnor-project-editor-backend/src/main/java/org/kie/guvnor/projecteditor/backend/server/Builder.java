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

import org.kie.builder.KieBuilder;
import org.kie.builder.KieFactory;
import org.kie.builder.KieFileSystem;
import org.kie.builder.impl.KieProject;
import org.kie.builder.KieServices;
import org.kie.commons.java.nio.file.DirectoryStream;
import org.kie.commons.java.nio.file.Files;
import org.kie.commons.java.nio.file.Paths;
import org.kie.guvnor.projecteditor.model.builder.Messages;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.VFSService;

public class Builder {

    private final VFSService vfsService;
    private final KieBuilder kieBuilder;
    private final String projectName;
    private final KieFileSystem kieFileSystem;

    public Builder(Path pathToKProjectXML, VFSService vfsService) {
        this.vfsService = vfsService;

        KieServices kieServices = KieServices.Factory.get();
        KieFactory kieFactory = KieFactory.Factory.get();
        kieFileSystem = kieFactory.newKieFileSystem();

        DirectoryStream<org.kie.commons.java.nio.file.Path> directoryStream = Files.newDirectoryStream(Paths.get(getProjectURI(pathToKProjectXML)));

        projectName = getProjectName(pathToKProjectXML);
        visitPaths(directoryStream);

        kieBuilder = kieServices.newKieBuilder(kieFileSystem);
    }

    public Messages build() {

        kieBuilder.build();
        return new Messages();
    }

    private String getProjectURI(Path path) {
        return path.toURI().substring(0, path.toURI().lastIndexOf("/src"));
    }

    private void visitPaths(DirectoryStream<org.kie.commons.java.nio.file.Path> directoryStream) {
        for (org.kie.commons.java.nio.file.Path path : directoryStream) {
            if (Files.isDirectory(path)) {
                visitPaths(Files.newDirectoryStream(path));
            } else {
                if (path.toUri().toString().endsWith(KieProject.KPROJECT_JAR_PATH)) {
                    System.out.println("ADDING kproject.xml to " + KieProject.KPROJECT_JAR_PATH);
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
        return path.toString().substring(projectName.length() + 2);
    }

    private String getProjectName(Path path) {
        String s = path.toURI();
        String substring = s.substring(s.indexOf("uf-playground/") + "uf-playground/".length());
        return substring.substring(0, substring.indexOf("/"));
    }
}
