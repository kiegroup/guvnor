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
package org.drools.guvnor.client.rpc;

/**
 * A Query request.
 * 
 * @author manstis
 */
public class QueryPageRequest extends PageRequest {

    private String  searchText;
    private Boolean searchArchived;

    // For GWT serialisation
    public QueryPageRequest() {
    }

    public QueryPageRequest(String searchText,
                            Boolean searchArchived,
                            int startRowIndex,
                            Integer pageSize) {
        super(startRowIndex, pageSize);
        this.searchText = searchText;
        this.searchArchived = searchArchived;
    }

    // ************************************************************************
    // Getters and setters
    // ************************************************************************

    public String getSearchText() {
        return searchText;
    }

    public Boolean isSearchArchived() {
        return searchArchived;
    }

    public void setSearchArchived(Boolean searchArchived) {
        this.searchArchived = searchArchived;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

}
