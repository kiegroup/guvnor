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

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.shared.workitems.PortableWorkDefinition;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Nothing to see here. Just extends this for your own mock impl.
 */
public class RepositoryServiceAsyncMock implements RepositoryServiceAsync {
    public void loadRuleListForState(StatePageRequest p0, AsyncCallback<PageResponse<StatePageRow>> cb) {
    }

    public void loadRuleListForState(String p0, int p1, int p2, String p3, AsyncCallback<TableDataResult> cb) {
    }

    public void loadTableConfig(String p0, AsyncCallback<TableConfig> cb) {
        
    }

    public void createNewRule(String p0, String p1, String p2, String p3, String p4, AsyncCallback<String> cb) {
        
    }
    
    public void createNewRule(NewAssetConfiguration p0, AsyncCallback<String> cb) {
        
    }

    public void createNewRule(NewGuidedDecisionTableAssetConfiguration p0, AsyncCallback<String> cb) {
        
    }

    public void createNewImportedRule(String p0, String p1, AsyncCallback<String> cb) {
        
    }

    public void deleteUncheckedRule(String p0, AsyncCallback cb) {
    }

    public void deleteUncheckedRule(String p0, String p1, AsyncCallback cb) {
        
    }

    public void clearRulesRepository(AsyncCallback cb) {
        
    }

    public void listWorkspaces(AsyncCallback<String[]> cb) {
        
    }

    public void createWorkspace(String p0, AsyncCallback cb) {
        
    }

    public void removeWorkspace(String p0, AsyncCallback cb) {
        
    }

    public void updateWorkspace(String p0, String[] p1, String[] p2, AsyncCallback cb) {
        
    }

    public void updateDependency(String p0, String p1, AsyncCallback cb) {
        
    }

    public void getDependencies(String p0, AsyncCallback<String[]> cb) {
        
    }

    public void checkinVersion(Asset p0, AsyncCallback<String> cb) {
        
    }

    public void restoreVersion(String p0, String p1, String p2, AsyncCallback cb) {
        
    }

    public void listStates(AsyncCallback<String[]> cb) {
        
    }

    public void createState(String p0, AsyncCallback<String> cb) {
        
    }

    public void renameState(String p0, String p1, AsyncCallback cb) {
        
    }

    public void removeState(String p0, AsyncCallback cb) {
        
    }

    public void changeState(String p0, String p1, boolean p2, AsyncCallback cb) {
        
    }

    public void loadSuggestionCompletionEngine(String p0, AsyncCallback<SuggestionCompletionEngine> cb) {
        
    }

    public void getCustomSelectors(AsyncCallback<String[]> cb) {
        
    }

    public void showLog(PageRequest p0, AsyncCallback<PageResponse<LogPageRow>> cb) {
        
    }

    public void showLog(AsyncCallback<LogEntry[]> cb) {
        
    }

    public void cleanLog(AsyncCallback cb) {
        
    }

    public void loadDropDownExpression(String[] p0, String p1, AsyncCallback<String[]> cb) {
        
    }

    public void queryFullText(QueryPageRequest p0, AsyncCallback<PageResponse<QueryPageRow>> cb) {
        
    }

    public void queryMetaData(QueryMetadataPageRequest p0, AsyncCallback<PageResponse<QueryPageRow>> cb) {
        
    }

    public void queryMetaData(MetaDataQuery[] p0, Date p1, Date p2, Date p3, Date p4, boolean p5, int p6, int p7, AsyncCallback<TableDataResult> cb) {
        
    }

    public void listUserPermissions(PageRequest p0, AsyncCallback<PageResponse<PermissionsPageRow>> cb) {
        
    }

    public void listUserPermissions(AsyncCallback cb) {
        
    }

    public void retrieveUserPermissions(String p0, AsyncCallback cb) {
        
    }

    public void updateUserPermissions(String p0, Map p1, AsyncCallback cb) {
        
    }

    public void listAvailablePermissionTypes(AsyncCallback<String[]> cb) {
        
    }

    public void listAvailablePermissionRoleTypes(AsyncCallback<List<String>> callback) {
    }

    public void deleteUser(String p0, AsyncCallback cb) {
        
    }

    public void createUser(String p0, AsyncCallback cb) {
        
    }

    public void loadDiscussionForAsset(String p0, AsyncCallback cb) {
        
    }

    public void addToDiscussionForAsset(String p0, String p1, AsyncCallback cb) {
        
    }

    public void clearAllDiscussionsForAsset(String p0, AsyncCallback cb) {
        
    }

    public void subscribe(AsyncCallback cb) {
        
    }

    public void loadInbox(String p0, AsyncCallback<TableDataResult> cb) {
        
    }

    public void loadInbox(InboxPageRequest p0, AsyncCallback<PageResponse<InboxPageRow>> cb) {
        
    }

    public void processTemplate(String p0, Map p1, AsyncCallback<String> cb) {
        
    }

    public void loadSpringContextElementData(AsyncCallback cb) {
        
    }

    public void loadWorkitemDefinitionElementData(AsyncCallback cb) {
    }
    
    public void loadWorkItemDefinitions(String p0, AsyncCallback<Set<PortableWorkDefinition>> cb) {
    }

    public void doesAssetExistInModule(String p0, String p1, AsyncCallback<Boolean> cb) {
    }
    
}
