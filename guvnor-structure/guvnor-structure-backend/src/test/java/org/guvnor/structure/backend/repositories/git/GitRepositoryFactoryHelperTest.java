/*
 * Copyright 2014 JBoss Inc
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

package org.guvnor.structure.backend.repositories.git;

import org.guvnor.structure.backend.repositories.EnvironmentParameters;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.server.config.ConfigGroup;
import org.guvnor.structure.server.config.ConfigItem;
import org.junit.Before;
import org.junit.Test;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.Path;

import java.net.URI;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GitRepositoryFactoryHelperTest {


    private IOService ioService;
    private GitRepositoryFactoryHelper helper;
    private FileSystem fileSystem;
    private ArrayList<Path> rootDirectories;

    @Before
    public void setUp() throws Exception {
        ioService = mock(IOService.class);
        helper = new GitRepositoryFactoryHelper(ioService);

        fileSystem = mock(FileSystem.class);
        when(
                ioService.newFileSystem(any(URI.class), anyMap())
        ).thenReturn(
                fileSystem
        );

        rootDirectories = new ArrayList<Path>();
        when(fileSystem.getRootDirectories()).thenReturn(rootDirectories);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNoSchemeConfigItem() throws Exception {
        helper.newRepository(new ConfigGroup());
    }

    @Test(expected = IllegalStateException.class)
    public void testNotValid() throws Exception {
        helper.newRepository(getConfigGroup());
    }

    @Test
    public void testBranches() throws Exception {

        rootDirectories.add(createPath("default://origin@uf-playground"));
        rootDirectories.add(createPath("default://master@uf-playground"));
        rootDirectories.add(createPath("default://branch1@uf-playground"));

        ConfigGroup configGroup = getConfigGroup();
        configGroup.setName("test");

        Repository repository = helper.newRepository(configGroup);

        assertEquals(3, repository.getBranches().size());
        assertEquals("master",repository.getCurrentBranch());
    }

    @Test
    public void testLoadBranch1() throws Exception {

        rootDirectories.add(createPath("default://origin@uf-playground"));
        rootDirectories.add(createPath("default://master@uf-playground"));
        rootDirectories.add(createPath("default://branch1@uf-playground"));

        ConfigGroup configGroup = getConfigGroup();
        configGroup.setName("test");

        Repository repository = helper.newRepository(configGroup,"branch1");

        assertEquals(3, repository.getBranches().size());
        assertEquals("branch1",repository.getCurrentBranch());
    }

    private Path createPath(String uri) {
        Path path = mock(Path.class);
        when(path.toUri()).thenReturn(URI.create(uri));
        when(path.getFileSystem()).thenReturn(fileSystem);
        return path;
    }

    private ConfigGroup getConfigGroup() {
        ConfigGroup repoConfig = new ConfigGroup();
        ConfigItem configItem = new ConfigItem();
        configItem.setName(EnvironmentParameters.SCHEME);
        repoConfig.addConfigItem(configItem);
        return repoConfig;
    }
}
