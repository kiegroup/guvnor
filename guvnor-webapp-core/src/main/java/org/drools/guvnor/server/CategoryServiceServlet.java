/*
 * Copyright 2012 JBoss Inc
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

package org.drools.guvnor.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import org.drools.guvnor.client.rpc.CategoryService;
import org.jboss.solder.core.Veto;

import javax.inject.Inject;

@Veto
public class CategoryServiceServlet
        extends RemoteServiceServlet
        implements CategoryService {


    @Inject
    private RepositoryCategoryService categoryService;

    public java.lang.String[] loadChildCategories(java.lang.String p0) {
        return categoryService.loadChildCategories( p0 );
    }

    public org.drools.guvnor.client.rpc.TableDataResult loadRuleListForCategories(java.lang.String p0, int p1, int p2, java.lang.String p3) throws com.google.gwt.user.client.rpc.SerializationException {
        return categoryService.loadRuleListForCategories( p0, p1, p2, p3 );
    }

    public org.drools.guvnor.client.rpc.PageResponse loadRuleListForCategories(org.drools.guvnor.client.rpc.CategoryPageRequest p0) throws com.google.gwt.user.client.rpc.SerializationException {
        return categoryService.loadRuleListForCategories( p0 );
    }

    public java.lang.Boolean createCategory(java.lang.String p0, java.lang.String p1, java.lang.String p2) {
        return categoryService.createCategory( p0, p1, p2 );
    }

    public void removeCategory(java.lang.String p0) throws com.google.gwt.user.client.rpc.SerializationException {
        categoryService.removeCategory( p0 );
    }

    public void renameCategory(java.lang.String p0, java.lang.String p1) {
        categoryService.renameCategory( p0, p1 );
    }


}
