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

package org.guvnor.jcr2vfsmigration.vfs;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.inject.Produces;
import javax.inject.Named;
import javax.inject.Singleton;

import org.kie.commons.java.nio.file.FileSystems;
import org.uberfire.backend.vfs.ActiveFileSystems;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.impl.ActiveFileSystemsImpl;
import org.uberfire.backend.vfs.impl.FileSystemImpl;
import org.uberfire.backend.vfs.impl.PathImpl;

import static java.util.Arrays.*;

@Singleton
public class ActiveFileSystemsFactory {

    private ActiveFileSystems activeFileSystems;

    @PostConstruct
    public void onStartup() {
        activeFileSystems = new ActiveFileSystemsImpl();
        String name = "guvnor-jcr2vfs-migration";
        FileSystems.newFileSystem(URI.create("git://" + name), new HashMap<String, Object>());
        final Path root = new PathImpl(name, "default://" + name);
        activeFileSystems.addBootstrapFileSystem(new FileSystemImpl(asList(root)));
    }

    @Produces @Named("fs")
    public ActiveFileSystems getActiveFileSystems() {
        return activeFileSystems;
    }

}
