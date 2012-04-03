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
package org.drools.guvnor.server;

import com.google.gwt.user.client.rpc.SerializationException;
import org.drools.guvnor.client.rpc.*;
import org.drools.guvnor.server.builder.PageResponseBuilder;
import org.drools.guvnor.server.builder.pagerow.CategoryRuleListPageRowBuilder;
import org.drools.guvnor.server.util.HtmlCleaner;
import org.drools.guvnor.server.util.LoggingHelper;
import org.drools.guvnor.server.util.TableDisplayHandler;
import org.drools.repository.AssetItemPageResult;
import org.drools.repository.CategoryItem;
import org.drools.repository.RulesRepository;
import org.drools.repository.RulesRepositoryException;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import java.util.ArrayList;
import java.util.List;

@Name("org.drools.guvnor.server.RepositoryCategoryOperations")
@AutoCreate
public class RepositoryCategoryOperations {
    private RulesRepository repository;

    private static final LoggingHelper log = LoggingHelper.getLogger(RepositoryCategoryOperations.class);
    private final ServiceSecurity serviceSecurity = new ServiceSecurity();

    public void setRulesRepository(RulesRepository repository) {
        this.repository = repository;
    }

    public RulesRepository getRulesRepository() {
        return repository;
    }

    @SuppressWarnings("rawtypes")
    protected String[] loadChildCategories(String categoryPath) {
        List<String> resultList = new ArrayList<String>();
        CategoryFilter filter = new CategoryFilter();

        CategoryItem item = getRulesRepository().loadCategory(categoryPath);
        List children = item.getChildTags();
        for (Object aChildren : children) {
            String childCategoryName = ((CategoryItem) aChildren).getName();
            if (filter.acceptNavigate(categoryPath,
                    childCategoryName)) {
                resultList.add(childCategoryName);
            }
        }

        return resultList.toArray(new String[resultList.size()]);
    }

    protected Boolean createCategory(String path,
                                     String name,
                                     String description) {

        log.info("USER:" + getCurrentUserName() + " CREATING cateogory: [" + name + "] in path [" + path + "]");

        if (path == null || "".equals(path)) {
            path = "/";
        }
        path = HtmlCleaner.cleanHTML(path);

        getRulesRepository().loadCategory(path).addCategory(name,
                description);
        getRulesRepository().save();
        return Boolean.TRUE;
    }

    protected void renameCategory(String fullPathAndName,
                                  String newName) {
        getRulesRepository().renameCategory(fullPathAndName,
                newName);
    }

    /**
     * loadRuleListForCategories
     *
     * @deprecated in favour of {@link #loadRuleListForCategories(CategoryPageRequest)}
     */
    protected TableDataResult loadRuleListForCategories(String categoryPath,
                                                        int skip,
                                                        int numRows,
                                                        String tableConfig) throws SerializationException {

        // First check the user has permission to access this categoryPath.
        if (!serviceSecurity.hasPermissionAnalystReadWithCategoryPathType(categoryPath)) {
            TableDisplayHandler handler = new TableDisplayHandler(tableConfig);
            return handler.loadRuleListTable(new AssetItemPageResult());

        }

        AssetItemPageResult result = getRulesRepository().findAssetsByCategory(categoryPath,
                false,
                skip,
                numRows);
        TableDisplayHandler handler = new TableDisplayHandler(tableConfig);
        return handler.loadRuleListTable(result);

    }

    protected PageResponse<CategoryPageRow> loadRuleListForCategories(CategoryPageRequest request) {

        // Do query
        long start = System.currentTimeMillis();

        // NOTE: Filtering is handled in repository.findAssetsByCategory()
        int numRowsToReturn = (request.getPageSize() == null ? -1 : request.getPageSize());
        AssetItemPageResult result = getRulesRepository().findAssetsByCategory(request.getCategoryPath(),
                false,
                request.getStartRowIndex(),
                numRowsToReturn);
        log.debug("Search time: " + (System.currentTimeMillis() - start));

        // Populate response
        boolean hasMoreRows = result.hasNext;

        List<CategoryPageRow> rowList = new CategoryRuleListPageRowBuilder()
                .withPageRequest(request)
                .withContent(result.assets.iterator())
                .build();

        //We can't use iterator.size() for the totalRowCount as some rows may have been filtered out based on permissions
        PageResponse<CategoryPageRow> pageResponse = new PageResponseBuilder<CategoryPageRow>()
                .withStartRowIndex(request.getStartRowIndex())
                .withPageRowList(rowList)
                .withLastPage(!hasMoreRows)
                .buildWithTotalRowCount(-1);

        long methodDuration = System.currentTimeMillis() - start;
        log.debug("Searched for Assest with Category (" + request.getCategoryPath() + ") in " + methodDuration + " ms.");
        return pageResponse;
    }

    protected void removeCategory(String categoryPath) throws SerializationException {
        log.info("USER:" + getCurrentUserName() + " REMOVING CATEGORY path: [" + categoryPath + "]");

        try {
            getRulesRepository().loadCategory(categoryPath).remove();
            getRulesRepository().save();
        } catch (RulesRepositoryException e) {
            log.info("Unable to remove category [" + categoryPath + "]. It is probably still used: " + e.getMessage());

            throw new DetailedSerializationException("Unable to remove category. It is probably still used.",
                    e.getMessage());
        }
    }

    private String getCurrentUserName() {
        return getRulesRepository().getSession().getUserID();
    }
}
