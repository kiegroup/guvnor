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
 * The request which contains filter properties, paging properties, etc.
 * @see AssetPageResponse
 * @author Geoffrey De Smet
 */
public class AssetPageRequest
        implements IsSerializable {

    // Filter properties: null properties are ignored for filtering
    private String packageUuid = null;
    private List<String> formatInList = null;
    private Boolean formatIsRegistered = null;

    private int startRowIndex = 0;
    private Integer pageSize = null; // null returns all pages

    // ************************************************************************
    // Getters and setters
    // ************************************************************************

    public String getPackageUuid() {
        return packageUuid;
    }

    public void setPackageUuid(String packageUuid) {
        this.packageUuid = packageUuid;
    }

    public List<String> getFormatInList() {
        return formatInList;
    }

    public void setFormatInList(List<String> formatInList) {
        this.formatInList = formatInList;
    }

    public Boolean getFormatIsRegistered() {
        return formatIsRegistered;
    }

    public void setFormatIsRegistered(Boolean formatIsRegistered) {
        this.formatIsRegistered = formatIsRegistered;
    }

    public int getStartRowIndex() {
        return startRowIndex;
    }

    public void setStartRowIndex(int startRowIndex) {
        this.startRowIndex = startRowIndex;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

}
