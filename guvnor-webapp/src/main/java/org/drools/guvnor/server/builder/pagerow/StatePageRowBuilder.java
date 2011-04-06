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

import org.apache.commons.lang.StringUtils;
import org.drools.guvnor.client.rpc.PageRequest;
import org.drools.guvnor.client.rpc.StatePageRow;
import org.drools.repository.AssetItem;

public class StatePageRowBuilder {

    public List<StatePageRow> createRows(final PageRequest pageRequest,
                                         Iterator<AssetItem> it) {
        List<StatePageRow> rowList = new ArrayList<StatePageRow>();

        // Filtering and skipping records to the required page is handled in
        // repository.findAssetsByState() so we only need to simply copy
        while ( it.hasNext() ) {
            AssetItem assetItem = (AssetItem) it.next();
            rowList.add( makeStatePageRow( assetItem ) );
        }
        return rowList;
    }

    private StatePageRow makeStatePageRow(AssetItem assetItem) {
        StatePageRow row = new StatePageRow();
        row.setUuid( assetItem.getUUID() );
        row.setFormat( assetItem.getFormat() );
        row.setName( assetItem.getName() );
        row.setDescription( assetItem.getDescription() );
        row.setAbbreviatedDescription( StringUtils.abbreviate( assetItem.getDescription(),
                                                               80 ) );
        row.setLastModified( assetItem.getLastModified().getTime() );
        row.setStateName( assetItem.getState().getName() );
        row.setPackageName( assetItem.getPackageName() );
        return row;
    }
}
