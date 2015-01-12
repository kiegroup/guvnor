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

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.base.version.VersionRecord;
import org.uberfire.java.nio.file.Path;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class PathResolverImplTest {

    private VersionLoader                      versionLoader;
    private PathResolverImpl                   resolver;
    private HashMap<Path, List<VersionRecord>> versionRecords;
    private Path                               pathToMainFile;
    private Path                               pathToDotFile;
    private IOService                          ioService;
    private VersionUtil                        versionUtil;

    @Before
    public void setUp() throws Exception {
        ioService = mock(IOService.class);
        versionUtil = mock(VersionUtil.class);
        versionLoader = new VersionLoaderMock(ioService, versionUtil);

        when(versionUtil.getPath(any(Path.class), eq("master"))).thenReturn(pathToMainFile);

        pathToMainFile = makePath("text.txt");
        pathToDotFile = makePath(".text.txt");

        resolver = new PathResolverImpl(
                versionLoader,
                new VersionUtil() {
                    @Override Path getPath(Path path, String version) throws URISyntaxException {
                        return super.getPath(path, version);
                    }
                });

        versionRecords = new HashMap<Path, List<VersionRecord>>();

        ArrayList<VersionRecord> mainHistory = new ArrayList<VersionRecord>();

        mainHistory.add(makeVersionRecord("id1", new Date(1)));
        mainHistory.add(makeVersionRecord("id3", new Date(3)));
        mainHistory.add(makeVersionRecord("id4", new Date(4)));

        ArrayList<VersionRecord> dotFileHistory = new ArrayList<VersionRecord>();
        dotFileHistory.add(makeVersionRecord("id1", new Date(1)));
        dotFileHistory.add(makeVersionRecord("id2", new Date(2)));
        dotFileHistory.add(makeVersionRecord("id5", new Date(5)));

        versionRecords.put(pathToMainFile, mainHistory);
        versionRecords.put(pathToDotFile, dotFileHistory);
    }

    @Test
    public void testResolveFromMainFile() throws Exception {

        assertEquals("text.txt",
                     resolver.resolveMainFilePath(pathToMainFile).getFileName().toString());
    }

    @Test
    public void testResolveFromDotFile() throws Exception {

        assertEquals("text.txt",
                     resolver.resolveMainFilePath(pathToDotFile).getFileName().toString());
    }

    private Path makePath(String fileName) {
        Path path = mock(Path.class);

        Path fileNamePath = mock(Path.class);

        when(path.getFileName()).thenReturn(fileNamePath);
        when(fileNamePath.toString()).thenReturn(fileName);

        when(path.resolveSibling(anyString())).thenAnswer(
                new Answer<Path>() {
                    @Override
                    public Path answer(InvocationOnMock invocationOnMock) throws Throwable {
                        return makePath((String) invocationOnMock.getArguments()[0]);
                    }
                });

        return path;
    }

    private VersionRecord makeVersionRecord(final String id, final Date date) {
        return new VersionRecord() {
            @Override public String id() {
                return id;
            }

            @Override public String author() {
                return null;
            }

            @Override public String email() {
                return null;
            }

            @Override public String comment() {
                return null;
            }

            @Override public Date date() {
                return date;
            }

            @Override public String uri() {
                return null;
            }
        };
    }

    private class VersionLoaderMock
            extends VersionLoader {

        public VersionLoaderMock(IOService ioService, VersionUtil versionUtil) {
            super(ioService, versionUtil);
        }

        @Override
        public List<VersionRecord> load(Path path) {
            return versionRecords.get(path);
        }
    }
}