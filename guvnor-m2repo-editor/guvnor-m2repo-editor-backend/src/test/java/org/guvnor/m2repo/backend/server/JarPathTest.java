/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.m2repo.backend.server;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Collection;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemHeaders;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.m2repo.backend.server.helpers.FormData;
import org.guvnor.m2repo.backend.server.helpers.HttpPostHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;

public class JarPathTest {

    @Before
    public void setupGuvnorM2Repository() {
        new GuvnorM2Repository().init();
    }

    @Test
    public void testLinuxPathSeparators() {
        final M2RepoServiceImpl service = new M2RepoServiceImpl();
        final String jarPath = service.getJarPath( GuvnorM2Repository.M2_REPO_DIR + "/a/b/c",
                                                   "/" );
        assertEquals( "a/b/c",
                      jarPath );
    }

    @Test
    public void testWindowsPathSeparators() {
        final M2RepoServiceImpl service = new M2RepoServiceImpl();
        final String jarPath = service.getJarPath( GuvnorM2Repository.M2_REPO_DIR + "\\a\\b\\c",
                                                   "\\" );
        assertEquals( "a/b/c",
                      jarPath );
    }

}
