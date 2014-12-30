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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;

import org.uberfire.ext.editor.commons.version.impl.PortableVersionRecord;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.base.version.VersionAttributeView;
import org.uberfire.java.nio.base.version.VersionRecord;
import org.uberfire.java.nio.file.Path;

public class VersionLoader {

    private IOService   ioService;
    private VersionUtil util;

    public VersionLoader() {
    }

    @Inject
    public VersionLoader(@Named("ioStrategy") IOService ioService,
                         VersionUtil util) {
        this.ioService = ioService;
        this.util = util;
    }

    public List<VersionRecord> load(Path path) {

        final List<VersionRecord> records = loadAllVersionRecords(path);

        final List<VersionRecord> result = new ArrayList<VersionRecord>();

        for (final VersionRecord record : records) {
            if (doesNotContainID(record.id(), result)) {
                result.add(makePortable(record));
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

    private List<VersionRecord> loadAllVersionRecords(Path path) {
        final List<VersionRecord> records = new ArrayList<VersionRecord>();

        records.addAll(loadVersionRecords(path));
        records.addAll(loadVersionRecords(util.getDotFilePath(path)));

        return records;
    }

    public List<VersionRecord> loadVersionRecords(Path path) {
        if (ioService.exists(path)) {
            return ioService.getFileAttributeView(path,
                                                  VersionAttributeView.class).readAttributes().history().records();
        } else {
            return Collections.EMPTY_LIST;
        }
    }

    private PortableVersionRecord makePortable(VersionRecord record) {
        return new PortableVersionRecord(record.id(),
                                         record.author(),
                                         record.email(),
                                         record.comment(),
                                         record.date(),
                                         record.uri());
    }

    private boolean doesNotContainID(String id, List<VersionRecord> records) {
        for (VersionRecord record : records) {
            if (record.id().equals(id)) {
                return false;
            }
        }
        return true;
    }

    public VersionRecord loadRecord(Path path) throws URISyntaxException {

        for (VersionRecord record : loadVersionRecords(util.getPath(path, "master"))) {
            String version = util.getVersion(path);
            if ("master".equals(version)) {
                // Return first record when looking for master
                return record;
            } else if (record.id().equals(version)) {
                return record;
            }
        }

        return null;
    }
}
