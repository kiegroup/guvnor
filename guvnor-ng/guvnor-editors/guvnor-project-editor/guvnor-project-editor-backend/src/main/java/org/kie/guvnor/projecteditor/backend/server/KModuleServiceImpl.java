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
import org.kie.guvnor.project.model.KModuleModel;
import org.kie.guvnor.project.service.KModuleService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

@Service
@ApplicationScoped
public class KModuleServiceImpl
        implements KModuleService {

    private IOService ioService;
    private Paths paths;
    private KModuleContentHandler moduleContentHandler;

    public KModuleServiceImpl() {
        // Weld needs this for proxying.
    }

    @Inject
    public KModuleServiceImpl(final @Named("ioStrategy") IOService ioService,
                              final Paths paths,
                              final KModuleContentHandler moduleContentHandler) {
        this.ioService = ioService;
        this.paths = paths;
        this.moduleContentHandler = moduleContentHandler;
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
    public KModuleModel loadKModule(final Path path) {
        return moduleContentHandler.toModel(ioService.readAllString(paths.convert(path)));
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
        String content = moduleContentHandler.toString(model);
        ioService.write(path, content);
    }

    private org.kie.commons.java.nio.file.Path getPomDirectoryPath(final Path pathToPomXML) {
        return paths.convert(pathToPomXML).getParent();
    }
}