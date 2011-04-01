/*
 * Copyright 2011 JBoss Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package org.drools.guvnor.client.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;


/**
 * Nothing to see here. Just extends this for your own mock impl.
 */
public class CategoryServiceAsyncMock implements CategoryServiceAsync {
    public void loadChildCategories(String p0, AsyncCallback<String[]> cb) {
    }

    public void loadRuleListForCategories(CategoryPageRequest p0, AsyncCallback<PageResponse<CategoryPageRow>> cb) {
    }

    public void loadRuleListForCategories(String p0, int p1, int p2, String p3, AsyncCallback<TableDataResult> cb) {
    }

    public void createCategory(String p0, String p1, String p2, AsyncCallback<Boolean> cb) {
    }

    public void removeCategory(String p0, AsyncCallback cb) {
    }

    public void renameCategory(String p0, String p1, AsyncCallback cb) {
    }
}
