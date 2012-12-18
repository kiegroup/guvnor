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

import org.kie.KieServices;
import org.kie.builder.KieBuilder;
import org.kie.builder.KieFileSystem;
import org.kie.builder.Message;
import org.kie.commons.io.IOService;
import org.kie.commons.java.nio.file.DirectoryStream;
import org.kie.commons.java.nio.file.Files;
import org.kie.commons.java.nio.file.Path;
import org.kie.guvnor.projecteditor.model.builder.Messages;

public class Builder {


    private final KieBuilder kieBuilder;
    private final String projectName;
    private final KieFileSystem kieFileSystem;
    private final IOService ioService;

    public Builder(Path moduleDirectory,
                   IOService ioService) {
        this.ioService = ioService;

        KieServices kieServices = KieServices.Factory.get();
        kieFileSystem = kieServices.newKieFileSystem();

        DirectoryStream<org.kie.commons.java.nio.file.Path> directoryStream = Files.newDirectoryStream(moduleDirectory);

        projectName = getProjectName(moduleDirectory);
        visitPaths(directoryStream);

        kieBuilder = kieServices.newKieBuilder(kieFileSystem);
    }

    public Messages build() {

        kieBuilder.buildAll();

        Messages messages = new Messages();

        for (Message message : kieBuilder.getResults().getMessages()) {
            org.kie.guvnor.projecteditor.model.builder.Message m = new org.kie.guvnor.projecteditor.model.builder.Message();
            switch (message.getLevel()) {
                case ERROR:
                    m.setLevel(org.kie.guvnor.projecteditor.model.builder.Message.Level.ERROR);
                    break;
                case WARNING:
                    m.setLevel(org.kie.guvnor.projecteditor.model.builder.Message.Level.WARNING);
                    break;
                case INFO:
                    m.setLevel(org.kie.guvnor.projecteditor.model.builder.Message.Level.INFO);
                    break;
            }

            m.setId(message.getId());
            m.setLine(message.getLine());
//            m.setPath(message.getPath());
            m.setColumn(message.getColumn());
            m.setText(message.getText());

            messages.getMessages().add(m);
        }

        return messages;
    }

    private void visitPaths(DirectoryStream<org.kie.commons.java.nio.file.Path> directoryStream) {
        for (org.kie.commons.java.nio.file.Path path : directoryStream) {
            if (Files.isDirectory(path)) {
                visitPaths(Files.newDirectoryStream(path));
            } else {
                if (path.toUri().toString().endsWith("src/main/resources/META-INF/kmodule.xml")) {
                    kieFileSystem.write("META-INF/kmodule.xml", ioService.readAllString(path));
                } else {
                    String pathAsString = stripPath(projectName, path);
                    String pathAsString = stripPath(projectName, path);
                    kieFileSystem.write(pathAsString, ioService.readAllString(path));
                }
            }
        }
    }

    private String stripPath(String projectName, org.kie.commons.java.nio.file.Path path) {
        return path.toString().substring(projectName.length() + 2);
    }

    private String getProjectName(Path path) {
        String substring = path.toUri().toString();
        return substring.substring(substring.indexOf("uf-playground/") + "uf-playground/".length());
    }
}
