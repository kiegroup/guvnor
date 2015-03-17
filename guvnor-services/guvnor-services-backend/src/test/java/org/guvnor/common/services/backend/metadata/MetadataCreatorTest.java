/*
 * Copyright 2015 JBoss Inc
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

package org.guvnor.common.services.backend.metadata;

import java.util.ArrayList;
import java.util.Date;

import org.guvnor.common.services.backend.metadata.attribute.DiscussionView;
import org.guvnor.common.services.backend.metadata.attribute.OtherMetaView;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.io.attribute.DublinCoreView;
import org.uberfire.java.nio.base.version.VersionAttributeView;
import org.uberfire.java.nio.base.version.VersionAttributes;
import org.uberfire.java.nio.base.version.VersionRecord;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.fs.file.SimpleFileSystemProvider;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MetadataCreatorTest {

    private SimpleFileSystemProvider fileSystemProvider;

    @Mock private DublinCoreView       dcoreView;
    @Mock private DiscussionView       discussView;
    @Mock private OtherMetaView        otherMetaView;
    @Mock private VersionAttributeView versionAttributeView;

    private MetadataCreator          service;
    private Path                     mainFilePath;
    private ArrayList<VersionRecord> versionRecords;

    @Before
    public void setUp() throws Exception {

        versionRecords = new ArrayList<VersionRecord>();
        VersionAttributes versionAttributes = new VersionAttributesMock(versionRecords);
        when(versionAttributeView.readAttributes()).thenReturn(versionAttributes);

        when(dcoreView.readAttributes()).thenReturn(new DublinCoreAttributesMock());
        when(otherMetaView.readAttributes()).thenReturn(new OtherMetaAttributesMock());
        when(discussView.readAttributes()).thenReturn(new DiscussionAttributesMock());

        fileSystemProvider = new SimpleFileSystemProvider();

        //Ensure URLs use the default:// scheme
        fileSystemProvider.forceAsDefault();

        mainFilePath = fileSystemProvider.getPath(this.getClass().getResource("myfile.file").toURI());

        service = new MetadataCreator(mainFilePath, configIOService, sessionInfo, dcoreView, discussView, otherMetaView, versionAttributeView);
    }

    @Test
    public void testSimple() throws Exception {

        versionRecords.add(createVersionRecord());

        Metadata metadata = service.create();

        assertNotNull(metadata);
        assertNotNull(metadata.getCategories());
        assertNotNull(metadata.getDiscussion());
        assertNotNull(metadata.getVersion());
    }

    private VersionRecord createVersionRecord() {
        return new VersionRecord() {
            @Override public String id() {
                return "1";
            }

            @Override public String author() {
                return "admin";
            }

            @Override public String email() {
                return "admin@mail.zap";
            }

            @Override public String comment() {
                return "Some commit";
            }

            @Override public Date date() {
                return new Date();
            }

            @Override public String uri() {
                return "myfile.file";
            }
        };
    }
}