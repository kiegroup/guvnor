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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.common.services.shared.metadata.MetadataService;
import org.guvnor.structure.repositories.impl.PortableVersionRecord;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.base.version.VersionAttributeView;
import org.uberfire.java.nio.base.version.VersionRecord;

public class VersionLoader {

    private IOService ioService;

    protected MetadataService metadataService;

    public VersionLoader() {
    }

    @Inject
    public VersionLoader(@Named("ioStrategy") IOService ioService,
                         MetadataService metadataService) {
        this.ioService = ioService;
        this.metadataService = metadataService;
    }

    public List<VersionRecord> load(Path path) {

        final List<VersionRecord> records = ioService.getFileAttributeView(Paths.convert(path),
                                                                           VersionAttributeView.class).readAttributes().history().records();

        List<VersionRecord> metadataVersionRecords = metadataService.getMetadata(path).getVersion();
        records.addAll(metadataVersionRecords);

        final List<VersionRecord> result = new ArrayList<VersionRecord>();

        for (final VersionRecord record : records) {
            if (doesNotContainID(record.id(), result)) {
                result.add(new PortableVersionRecord(record.id(),
                                                     record.author(),
                                                     record.email(),
                                                     record.comment(),
                                                     record.date(),
                                                     record.uri()));
            }
        }

        Collections.sort(
                result,
                new Comparator<VersionRecord>() {
                    @Override
                    public int compare(VersionRecord left, VersionRecord right) {
                        return left.date().compareTo(right.date());
                    }
                });

        return result;

    }

    private boolean doesNotContainID(String id, List<VersionRecord> records) {
        for (VersionRecord record : records) {
            if (record.id().equals(id)) {
                return false;
            }
        }
        return true;
    }
}
