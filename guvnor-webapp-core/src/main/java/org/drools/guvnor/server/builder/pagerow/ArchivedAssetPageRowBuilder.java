/*
 * Copyright 2011 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.drools.guvnor.server.builder.pagerow;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.drools.guvnor.client.rpc.AdminArchivedPageRow;
import org.drools.guvnor.client.rpc.PageRequest;
import org.drools.repository.AssetItem;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.impl.PathImpl;

public class ArchivedAssetPageRowBuilder
    implements PageRowBuilder<PageRequest, Iterator<AssetItem>> {

    private PageRequest         pageRequest;
    private Iterator<AssetItem> iterator;

    public List<AdminArchivedPageRow> build() {
        validate();
        int skipped = 0;
        Integer pageSize = pageRequest.getPageSize();
        int startRowIndex = pageRequest.getStartRowIndex();
        List<AdminArchivedPageRow> rowList = new ArrayList<AdminArchivedPageRow>();
        while ( iterator.hasNext() && (pageSize == null || rowList.size() < pageSize) ) {
            AssetItem archivedAssetItem = iterator.next();

            // Cannot use AssetItemIterator.skip() as it skips non-filtered
            // assets whereas startRowIndex is the index of the
            // first displayed asset (i.e. filtered)
            if (skipped >= startRowIndex) {
                rowList.add(makeAdminArchivedPageRow(archivedAssetItem));
            }
            skipped++;

        }
        return rowList;
    }

    private AdminArchivedPageRow makeAdminArchivedPageRow(AssetItem assetItem) {
        AdminArchivedPageRow row = new AdminArchivedPageRow();
        //REVISIT: get a Path instance from drools-repository-vfs
        Path path = new PathImpl();
        path.setUUID(assetItem.getUUID());
        row.setPath( path );
        row.setFormat( assetItem.getFormat() );
        row.setName( assetItem.getName() );
        row.setPackageName( assetItem.getModuleName() );
        row.setLastContributor( assetItem.getLastContributor() );
        row.setLastModified( assetItem.getLastModified().getTime() );
        return row;
    }

    public void validate() {
        if ( pageRequest == null ) {
            throw new IllegalArgumentException( "PageRequest cannot be null" );
        }

        if ( iterator == null ) {
            throw new IllegalArgumentException( "Content cannot be null" );
        }

    }

    public ArchivedAssetPageRowBuilder withPageRequest(PageRequest pageRequest) {
        this.pageRequest = pageRequest;
        return this;
    }

    public ArchivedAssetPageRowBuilder withContent(Iterator<AssetItem> iterator) {
        this.iterator = iterator;
        return this;
    }
}
