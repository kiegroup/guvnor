/*
 * Copyright 2011 JBoss Inc
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
package org.drools.guvnor.server.util;

import org.drools.guvnor.client.rpc.PackageConfigData;
import org.drools.repository.PackageItem;

public class PackageConfigDataFactory {
    public static PackageConfigData createPackageConfigDataWithDependencies(PackageItem item) {
        PackageConfigData data = create( item );
        data.dependencies = item.getDependencies();
        return data;
    }

    public static PackageConfigData createPackageConfigDataWithOutDependencies(PackageItem item) {
        PackageConfigData data = create( item );
        return data;
    }

    private static PackageConfigData create(PackageItem item) {
        PackageConfigData data = new PackageConfigData();
        data.uuid = item.getUUID();
        data.header = DroolsHeader.getDroolsHeader( item );
        data.externalURI = item.getExternalURI();
        data.catRules = item.getCategoryRules();
        data.description = item.getDescription();
        data.archived = item.isArchived();
        data.name = item.getName();
        data.lastModified = item.getLastModified().getTime();
        data.dateCreated = item.getCreatedDate().getTime();
        data.checkinComment = item.getCheckinComment();
        data.lasContributor = item.getLastContributor();
        data.state = item.getStateDescription();
        data.isSnapshot = item.isSnapshot();
        return data;
    }

    
}
