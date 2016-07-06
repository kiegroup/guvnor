/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.guvnor.structure.repositories;

import java.util.Arrays;
import java.util.Collection;

import com.google.gwtmockito.GwtMockitoTestRunner;
import com.sun.corba.se.impl.activation.RepositoryImpl;
import org.guvnor.structure.client.security.RepositoryTreeProvider;
import org.guvnor.structure.organizationalunit.OrganizationalUnitSearchService;
import org.guvnor.structure.repositories.impl.git.GitRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.mocks.CallerMock;
import org.uberfire.security.authz.Permission;
import org.uberfire.security.authz.PermissionManager;
import org.uberfire.security.client.authz.tree.PermissionNode;
import org.uberfire.security.client.authz.tree.PermissionTree;
import org.uberfire.security.impl.authz.DefaultPermissionManager;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class RepositoryTreeProviderTest {

    @Mock
    RepositorySearchService searchService;

    @Mock
    PermissionTree permissionTree;

    PermissionManager permissionManager;
    RepositoryTreeProvider treeProvider;
    Repository repo1;
    Repository repo2;
    PermissionNode rootNode;

    @Before
    public void setup() {
        permissionManager = new DefaultPermissionManager();
        treeProvider = new RepositoryTreeProvider(permissionManager, new CallerMock<>(searchService));
        repo1 =  new GitRepository("repo1");
        repo2 =  new GitRepository("repo2");
        rootNode = treeProvider.buildRootNode();
        rootNode.setPermissionTree(permissionTree);

        when(permissionTree.getChildrenResourceIds(any())).thenReturn(null);
        when(searchService.searchByAlias(anyString(), anyInt(), anyBoolean())).thenReturn(Arrays.asList(repo1, repo2));
    }

    @Test
    public void testRootNode() {
        assertEquals(rootNode.getPermissionList().size(), 4);
        checkDependencies(rootNode);
    }

    @Test
    public void testChildrenNodes() {
        rootNode.expand(children -> {
            verify(searchService).searchByAlias(anyString(), anyInt(), anyBoolean());
            for (PermissionNode child : children) {
                assertEquals(child.getPermissionList().size(), 3);
                checkDependencies(child);
            }
        });
    }

    protected void checkDependencies(PermissionNode permissionNode) {
        for (Permission permission : permissionNode.getPermissionList()) {
            Collection<Permission> dependencies = permissionNode.getDependencies(permission);

            if (permission.getName().startsWith("repository.read")) {
                assertEquals(dependencies.size(), 2);
            }
            else {
                assertNull(dependencies);
            }
        }
    }
}
