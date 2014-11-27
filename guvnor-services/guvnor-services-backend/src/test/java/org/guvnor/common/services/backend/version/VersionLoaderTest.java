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

package org.guvnor.common.services.backend.version;

import java.util.ArrayList;
import java.util.List;

import org.guvnor.common.services.backend.MockIOService;
import org.guvnor.common.services.shared.metadata.MetadataService;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.junit.Before;
import org.junit.Test;
import org.uberfire.backend.vfs.Path;
import org.uberfire.java.nio.IOException;
import org.uberfire.java.nio.base.version.VersionAttributeView;
import org.uberfire.java.nio.base.version.VersionAttributes;
import org.uberfire.java.nio.base.version.VersionHistory;
import org.uberfire.java.nio.base.version.VersionRecord;
import org.uberfire.java.nio.file.attribute.FileAttributeView;
import org.uberfire.java.nio.file.attribute.FileTime;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class VersionLoaderTest {

    private MetadataService metadataService;
    private MockIOService   ioService;
    private Path            pathToFile;
    private Path            pathToDotFile;
    private ArrayList<VersionRecord> dotFileVersionRecords  = new ArrayList<VersionRecord>();
    private ArrayList<VersionRecord> mainFileVersionRecords = new ArrayList<VersionRecord>();

    @Before
    public void setUp() throws Exception {
        pathToFile = mock(Path.class);
        pathToDotFile = mock(Path.class);
        ioService = new MockIOService() {
            @Override
            public <V extends FileAttributeView> V getFileAttributeView(org.uberfire.java.nio.file.Path path, Class<V> vClass) throws IllegalArgumentException {
                return (V) new MockVersionAttributeView(path);
            }
        };
        metadataService = mock(MetadataService.class);
        when(metadataService.getMetadata(pathToFile))
                .thenReturn(
                        new Metadata() {
                            @Override
                            public List<VersionRecord> getVersion() {
                                return dotFileVersionRecords;
                            }
                        });
    }

    @Test
    public void testSimple() throws Exception {

        VersionLoader versionLoader = new VersionLoader(
                ioService,
                metadataService
        );

        List<VersionRecord> versions = versionLoader.load(pathToFile);

        assertEquals(3, versions.size());
        assertEquals("id1", versions.get(0).id());
        assertEquals("id2", versions.get(1).id());
        assertEquals("id3", versions.get(2).id());

    }

    private class MockVersionAttributeView
            extends VersionAttributeView<org.uberfire.java.nio.file.Path> {

        public MockVersionAttributeView(org.uberfire.java.nio.file.Path path) {
            super(path);
        }

        @Override public VersionAttributes readAttributes() throws IOException {
            return new VersionAttributes() {
                @Override public VersionHistory history() {
                    return new VersionHistory() {
                        @Override public List<VersionRecord> records() {
                            return mainFileVersionRecords;
                        }
                    };
                }

                @Override public FileTime lastModifiedTime() {
                    return null;
                }

                @Override public FileTime lastAccessTime() {
                    return null;
                }

                @Override public FileTime creationTime() {
                    return null;
                }

                @Override public boolean isRegularFile() {
                    return false;
                }

                @Override public boolean isDirectory() {
                    return false;
                }

                @Override public boolean isSymbolicLink() {
                    return false;
                }

                @Override public boolean isOther() {
                    return false;
                }

                @Override public long size() {
                    return 0;
                }

                @Override public Object fileKey() {
                    return null;
                }
            };
        }

        @Override public Class[] viewTypes() {
            return new Class[0];
        }
    }

}