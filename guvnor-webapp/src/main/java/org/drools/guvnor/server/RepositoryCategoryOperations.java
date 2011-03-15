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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.drools.guvnor.client.rpc.CategoryPageRequest;
import org.drools.guvnor.client.rpc.CategoryPageRow;
import org.drools.guvnor.client.rpc.DetailedSerializationException;
import org.drools.guvnor.client.rpc.PageResponse;
import org.drools.guvnor.client.rpc.TableDataResult;
import org.drools.guvnor.server.security.CategoryPathType;
import org.drools.guvnor.server.security.RoleTypes;
import org.drools.guvnor.server.util.HtmlCleaner;
import org.drools.guvnor.server.util.LoggingHelper;
import org.drools.guvnor.server.util.ServiceRowSizeHelper;
import org.drools.guvnor.server.util.TableDisplayHandler;
import org.drools.repository.AssetItem;
import org.drools.repository.AssetItemPageResult;
import org.drools.repository.CategoryItem;
import org.drools.repository.RulesRepository;
import org.drools.repository.RulesRepositoryException;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.security.Identity;

import com.google.gwt.user.client.rpc.SerializationException;

@Name("org.drools.guvnor.server.RepositoryCategoryOperations")
@AutoCreate
public class RepositoryCategoryOperations {
    private RulesRepository            repository;

    private static final LoggingHelper log = LoggingHelper
                                                                                 .getLogger( RepositoryCategoryOperations.class );

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

        CategoryItem item = getRulesRepository().loadCategory( categoryPath );
        List children = item.getChildTags();
        for ( int i = 0; i < children.size(); i++ ) {
            String childCategoryName = ((CategoryItem) children.get( i )).getName();
            if ( filter.acceptNavigate( categoryPath,
                                        childCategoryName ) ) {
                resultList.add( childCategoryName );
            }
        }

        String[] resultArr = resultList.toArray( new String[resultList.size()] );
        return resultArr;
    }

    protected Boolean createCategory(String path,
                                     String name,
                                     String description) {

        log.info( "USER:" + getCurrentUserName() + " CREATING cateogory: [" + name + "] in path [" + path + "]" );

        if ( path == null || "".equals( path ) ) {
            path = "/";
        }
        path = HtmlCleaner.cleanHTML( path );

        getRulesRepository().loadCategory( path ).addCategory( name,
                                                               description );
        getRulesRepository().save();
        return Boolean.TRUE;
    }

    protected void renameCategory(String fullPathAndName,
                                  String newName) {
        getRulesRepository().renameCategory( fullPathAndName,
                                             newName );
    }

    /**
     * loadRuleListForCategories
     * @deprecated in favour of {@link loadRuleListForCategories(CategoryPageRequest)}
     */
    protected TableDataResult loadRuleListForCategories(String categoryPath,
                                                        int skip,
                                                        int numRows,
                                                        String tableConfig) throws SerializationException {

        // First check the user has permission to access this categoryPath.
        if ( Contexts.isSessionContextActive() ) {
            if ( !Identity.instance().hasPermission( new CategoryPathType( categoryPath ),
                                                     RoleTypes.ANALYST_READ ) ) {

                TableDisplayHandler handler = new TableDisplayHandler( tableConfig );
                return handler.loadRuleListTable( new AssetItemPageResult() );
            }
        }

        AssetItemPageResult result = getRulesRepository().findAssetsByCategory( categoryPath,
                                                                                false,
                                                                                skip,
                                                                                numRows );
        TableDisplayHandler handler = new TableDisplayHandler( tableConfig );
        return handler.loadRuleListTable( result );

    }

    protected PageResponse<CategoryPageRow> loadRuleListForCategories(CategoryPageRequest request) throws SerializationException {

        // Do query
        long start = System.currentTimeMillis();

        // NOTE: Filtering is handled in repository.findAssetsByCategory()
        int numRowsToReturn = (request.getPageSize() == null ? -1 : request.getPageSize());
        AssetItemPageResult result = getRulesRepository().findAssetsByCategory( request.getCategoryPath(),
                                                                                false,
                                                                                request.getStartRowIndex(),
                                                                                numRowsToReturn );
        log.debug( "Search time: " + (System.currentTimeMillis() - start) );

        // Populate response
        boolean bHasMoreRows = result.hasNext;
        PageResponse<CategoryPageRow> pageResponse = new PageResponse<CategoryPageRow>();
        List<CategoryPageRow> rowList = fillCategoryPageRows( request,
                                                              result );
        pageResponse.setStartRowIndex( request.getStartRowIndex() );
        pageResponse.setPageRowList( rowList );
        pageResponse.setLastPage( !bHasMoreRows );

        // Fix Total Row Size
        ServiceRowSizeHelper serviceRowSizeHelper = new ServiceRowSizeHelper();
        serviceRowSizeHelper.fixTotalRowSize( request,
                                              pageResponse,
                                              -1,
                                              rowList.size(),
                                              bHasMoreRows );

        long methodDuration = System.currentTimeMillis() - start;
        log.debug( "Searched for Assest with Category (" + request.getCategoryPath() + ") in " + methodDuration + " ms." );
        return pageResponse;
    }

    private List<CategoryPageRow> fillCategoryPageRows(CategoryPageRequest request,
                                                       AssetItemPageResult result) {
        List<CategoryPageRow> rowList = new ArrayList<CategoryPageRow>();

        // Filtering and skipping records to the required page is handled in
        // repository.findAssetsByState() so we only need to simply copy
        Iterator<AssetItem> it = result.assets.iterator();
        while ( it.hasNext() ) {
            AssetItem assetItem = (AssetItem) it.next();
            rowList.add( makeCategoryPageRow( assetItem ) );
        }
        return rowList;
    }

    private CategoryPageRow makeCategoryPageRow(AssetItem assetItem) {
        CategoryPageRow row = new CategoryPageRow();
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

    protected void removeCategory(String categoryPath) throws SerializationException {
        log.info( "USER:" + getCurrentUserName() + " REMOVING CATEGORY path: [" + categoryPath + "]" );

        try {
            getRulesRepository().loadCategory( categoryPath ).remove();
            getRulesRepository().save();
        } catch ( RulesRepositoryException e ) {
            log.info( "Unable to remove category [" + categoryPath + "]. It is probably still used: " + e.getMessage() );

            throw new DetailedSerializationException( "Unable to remove category. It is probably still used.",
                                                      e.getMessage() );
        }
    }

    private String getCurrentUserName() {
        return getRulesRepository().getSession().getUserID();
    }
}
