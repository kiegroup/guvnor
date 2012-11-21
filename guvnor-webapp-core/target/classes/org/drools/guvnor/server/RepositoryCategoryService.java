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
import java.util.List;

import org.drools.guvnor.client.rpc.CategoryPageRequest;
import org.drools.guvnor.client.rpc.CategoryPageRow;
import org.drools.guvnor.client.rpc.CategoryService;
import org.drools.guvnor.client.rpc.PageResponse;
import org.drools.guvnor.client.rpc.TableDataResult;
import org.drools.guvnor.server.builder.PageResponseBuilder;
import org.drools.guvnor.server.security.CategoryPathType;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.jboss.seam.remoting.annotations.WebRemote;

import com.google.gwt.user.client.rpc.SerializationException;
import org.jboss.seam.security.annotations.LoggedIn;

@ApplicationScoped
@Named("org.drools.guvnor.client.rpc.CategoryService")
public class RepositoryCategoryService
    implements
    CategoryService {

    private static final long            serialVersionUID             = 12365;

    @Inject
    private ServiceSecurity serviceSecurity;

    @Inject
    private RepositoryCategoryOperations repositoryCategoryOperations;

    @WebRemote
    @LoggedIn
    public String[] loadChildCategories(String categoryPath) {
        return repositoryCategoryOperations.loadChildCategories( categoryPath );
    }

    @WebRemote
    public Boolean createCategory(String path,
                                  String name,
                                  String description) {
        serviceSecurity.checkSecurityIsAdmin();
        return repositoryCategoryOperations.createCategory( path,
                                                            name,
                                                            description );
    }

    @WebRemote
    @LoggedIn
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
    @WebRemote
    @LoggedIn
    public TableDataResult loadRuleListForCategories(String categoryPath,
                                                     int skip,
                                                     int numRows,
                                                     String tableConfig) throws SerializationException {
        return repositoryCategoryOperations.loadRuleListForCategories( categoryPath,
                                                                       skip,
                                                                       numRows,
                                                                       tableConfig );
    }

    @WebRemote
    @LoggedIn
    public PageResponse<CategoryPageRow> loadRuleListForCategories(CategoryPageRequest request) throws SerializationException {
        if ( request == null ) {
            throw new IllegalArgumentException( "request cannot be null" );
        }
        if ( request.getPageSize() != null && request.getPageSize() < 0 ) {
            throw new IllegalArgumentException( "pageSize cannot be less than zero." );
        }

        // Role-based Authorization check: This method only returns rules that
        // the user has permission to access. The user is considered to has
        // permission to access the particular category when: The user has
        // ANALYST_READ role or higher (i.e., ANALYST) to this category
        if ( !serviceSecurity.isSecurityIsAnalystReadWithTargetObject( new CategoryPathType( request.getCategoryPath() ) ) ) {
            List<CategoryPageRow> rowList = new ArrayList<CategoryPageRow>();
            return new PageResponseBuilder<CategoryPageRow>()
                    .withStartRowIndex(request.getStartRowIndex())
                    .withPageRowList(rowList)
                    .withLastPage(true)
                    .buildWithTotalRowCount(0);
        }

        return repositoryCategoryOperations.loadRuleListForCategories( request );
    }

    @WebRemote
    @LoggedIn
    public void removeCategory(String categoryPath) throws SerializationException {
        repositoryCategoryOperations.removeCategory( categoryPath );
    }

}
