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


import org.drools.guvnor.client.rpc.CategoryPageRequest;
import org.drools.guvnor.client.rpc.CategoryPageRow;
import org.drools.guvnor.client.rpc.CategoryService;
import org.drools.guvnor.client.rpc.PageResponse;
import org.drools.guvnor.client.rpc.TableDataResult;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.user.client.rpc.SerializationException;
import org.uberfire.security.annotations.Roles;

@ApplicationScoped
@Named("org.drools.guvnor.client.rpc.CategoryService")
public class RepositoryCategoryService
    implements
    CategoryService {

    private static final long            serialVersionUID             = 12365;

    @Inject
    private RepositoryCategoryOperations repositoryCategoryOperations;

    public String[] loadChildCategories(String categoryPath) {
        return repositoryCategoryOperations.loadChildCategories( categoryPath );
    }

    @Roles({"ADMIN"})
    public Boolean createCategory(String path,
                                  String name,
                                  String description) {
        return repositoryCategoryOperations.createCategory( path,
                                                            name,
                                                            description );
    }

    public void renameCategory(String fullPathAndName,
                               String newName) {
        repositoryCategoryOperations.renameCategory( fullPathAndName,
                                                     newName );
    }

    /**
     * loadRuleListForCategories
     *
     * Role-based Authorization check: This method only returns rules that the user has
     * permission to access. The user is considered to has permission to access the particular category when:
     * The user has ANALYST_READ role or higher (i.e., ANALYST) to this category
     * 
     * @deprecated in favour of {@link loadRuleListForCategories(CategoryPageRequest)}
     */
    public TableDataResult loadRuleListForCategories(String categoryPath,
                                                     int skip,
                                                     int numRows,
                                                     String tableConfig) throws SerializationException {
        return repositoryCategoryOperations.loadRuleListForCategories( categoryPath,
                                                                       skip,
                                                                       numRows,
                                                                       tableConfig );
    }

    public PageResponse<CategoryPageRow> loadRuleListForCategories(CategoryPageRequest request) throws SerializationException {
        if ( request == null ) {
            throw new IllegalArgumentException( "request cannot be null" );
        }
        if ( request.getPageSize() != null && request.getPageSize() < 0 ) {
            throw new IllegalArgumentException( "pageSize cannot be less than zero." );
        }

        return repositoryCategoryOperations.loadRuleListForCategories( request );
    }

    public void removeCategory(String categoryPath) throws SerializationException {
        repositoryCategoryOperations.removeCategory( categoryPath );
    }

}
