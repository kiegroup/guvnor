/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.guvnor.client.rpc;

import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * A page of an assets table, contains a list of asset summaries/headers.
 * @see AssetPageRequest
 * @author Geoffrey De Smet
 */
public class AssetPageResponse
        implements IsSerializable {

    private int totalRowSize;
    private int startRowIndex;
    private List<AssetPageRow> assetPageRowList;
    private boolean lastPage;

    public boolean isFirstPage() {
        return startRowIndex == 0L;
    }

    // ************************************************************************
    // Getters and setters
    // ************************************************************************

    public int getTotalRowSize() {
        return totalRowSize;
    }

    public void setTotalRowSize(int totalRowSize) {
        this.totalRowSize = totalRowSize;
    }

    public int getStartRowIndex() {
        return startRowIndex;
    }

    public void setStartRowIndex(int startRowIndex) {
        this.startRowIndex = startRowIndex;
    }

    public List<AssetPageRow> getAssetPageRowList() {
        return assetPageRowList;
    }

    public void setAssetPageRowList(List<AssetPageRow> assetPageRowList) {
        this.assetPageRowList = assetPageRowList;
    }

    public boolean isLastPage() {
        return lastPage;
    }

    public void setLastPage(boolean lastPage) {
        this.lastPage = lastPage;
    }

}
