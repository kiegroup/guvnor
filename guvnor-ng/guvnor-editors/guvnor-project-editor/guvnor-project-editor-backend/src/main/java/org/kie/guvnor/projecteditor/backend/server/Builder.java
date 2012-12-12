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

import org.kie.commons.io.IOService;
import org.kie.guvnor.projecteditor.model.builder.Messages;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;

public class Builder {

    // TODO: Finish this when the core is more stable

//    private final KieBuilder kieBuilder;
//    private final String projectName;
//    private final KieFileSystem kieFileSystem;
//    private final IOService ioService;

    public Builder(Path pathToKModuleXML,
                   IOService ioService,
                   Paths paths) {
//        this.ioService = ioService;
//
//        KieServices kieServices = KieServices.Factory.get();
//        KieFactory kieFactory = KieFactory.Factory.get();
//        kieFileSystem = kieFactory.newKieFileSystem();
//
//        // TODO This will not work fix me
//        DirectoryStream<org.kie.commons.java.nio.file.Path> directoryStream = Files.newDirectoryStream(paths.convert(pathToKModuleXML));
//
//        projectName = getProjectName(pathToKModuleXML);
//        visitPaths(directoryStream);
//
//        kieBuilder = kieServices.newKieBuilder(kieFileSystem);
    }

    public Messages build() {

//        kieBuilder.build();
//        return new Messages();
        return null;
    }

//    private void visitPaths(DirectoryStream<org.kie.commons.java.nio.file.Path> directoryStream) {
//        for (org.kie.commons.java.nio.file.Path path : directoryStream) {
//            if (Files.isDirectory(path)) {
//                visitPaths(Files.newDirectoryStream(path));
//            } else {
//                if (path.toUri().toString().endsWith("src/main/resources/META-INF/kmodule.xml")) {
//                    kieFileSystem.write("META-INF/kmodule.xml", ioService.readAllString(path));
//                } else {
//                    String pathAsString = stripPath(projectName, path);
//                    System.out.println("ADDING " + pathAsString);
//
//                    kieFileSystem.write(pathAsString, ioService.readAllString(path));
//                }
//            }
//        }
//    }
//
//    private String stripPath(String projectName, org.kie.commons.java.nio.file.Path path) {
//        return path.toString().substring(projectName.length() + 2);
//    }
//
//    private String getProjectName(Path path) {
//        String s = path.toURI();
//        String substring = s.substring(s.indexOf("uf-playground/") + "uf-playground/".length());
//        return substring.substring(0, substring.indexOf("/"));
//    }
}
