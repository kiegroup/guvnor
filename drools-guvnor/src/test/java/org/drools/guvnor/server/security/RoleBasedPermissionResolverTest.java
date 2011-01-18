/**
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

package org.drools.guvnor.server.security;

/*
 * Copyright 2005 JBoss Inc
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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.drools.guvnor.server.GuvnorTestBase;
import org.drools.guvnor.server.ServiceImplementation;
import org.jboss.seam.contexts.Contexts;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class RoleBasedPermissionResolverTest extends GuvnorTestBase {

    @Before
    public void setup() {
        setUpSeam();
        setUpMockIdentity();
    }

    @After
    public void teardown() {
        tearAllDown();
    }

    @Test
    public void testCategoryBasedPermissionAnalyst() throws Exception {
        //NOTE: Have to have this call, otherwise this test will fail others tests. Seems to be related to
        //how Seam context initializes the JCR repository, but dont know the exact cause yet. 
        ServiceImplementation impl = getServiceImplementation();

        String package1Name = "testCategoryBasedPermissionAnalystPackageName1";
        String package2Name = "testCategoryBasedPermissionAnalystPackageName2";

        List<RoleBasedPermission> pbps = new ArrayList<RoleBasedPermission>();
        pbps.add( new RoleBasedPermission( "jervis",
                                           RoleTypes.PACKAGE_ADMIN,
                                           package1Name,
                                           null ) );
        pbps.add( new RoleBasedPermission( "jervis",
                                           RoleTypes.PACKAGE_READONLY,
                                           package2Name,
                                           null ) );
        pbps.add( new RoleBasedPermission( "jervis",
                                           RoleTypes.ANALYST,
                                           null,
                                           "category1" ) );
        pbps.add( new RoleBasedPermission( "jervis",
                                           RoleTypes.ANALYST,
                                           null,
                                           "category2" ) );
        MockRoleBasedPermissionStore store = new MockRoleBasedPermissionStore( pbps );
        Contexts.getSessionContext().set( "org.drools.guvnor.server.security.RoleBasedPermissionStore",
                                          store );

        // Put permission list in session.
        RoleBasedPermissionManager testManager = new RoleBasedPermissionManager();
        testManager.create();
        Contexts.getSessionContext().set( "roleBasedPermissionManager",
                                          testManager );

        RoleBasedPermissionResolver resolver = new RoleBasedPermissionResolver();
        resolver.setEnableRoleBasedAuthorization( true );

        assertTrue( resolver.hasPermission( new CategoryPathType( "category1" ),
                                            null ) );
        assertTrue( resolver.hasPermission( new CategoryPathType( "category2" ),
                                            null ) );
        assertFalse( resolver.hasPermission( new CategoryPathType( "category3/category3" ),
                                             null ) );
        assertTrue( resolver.hasPermission( new CategoryPathType( "/category1/category2" ),
                                            null ) );

        assertTrue( resolver.hasPermission( new CategoryPathType( "category2" ),
                                            RoleTypes.ANALYST ) );
        assertTrue( resolver.hasPermission( new CategoryPathType( "category2" ),
                                            RoleTypes.ANALYST_READ ) );

        assertFalse( resolver.hasPermission( new CategoryPathType( "category3/category3" ),
                                             RoleTypes.ANALYST ) );
        assertFalse( resolver.hasPermission( new CategoryPathType( "category3/category3" ),
                                             RoleTypes.ANALYST_READ ) );

    }

    @Test
    public void testCategoryBasedPermissionAnalystReadOnly() throws Exception {
        String package1Name = "testCategoryBasedPermissionAnalystPackageName1";
        String package2Name = "testCategoryBasedPermissionAnalystPackageName2";

        String categoryPath = "category1";
        String categoryPath2 = "category2";

        List<RoleBasedPermission> pbps = new ArrayList<RoleBasedPermission>();
        pbps.add( new RoleBasedPermission( "jervis",
                                           RoleTypes.PACKAGE_ADMIN,
                                           package1Name,
                                           null ) );
        pbps.add( new RoleBasedPermission( "jervis",
                                           RoleTypes.PACKAGE_READONLY,
                                           package2Name,
                                           null ) );
        pbps.add( new RoleBasedPermission( "jervis",
                                           RoleTypes.ANALYST_READ,
                                           null,
                                           categoryPath ) );
        pbps.add( new RoleBasedPermission( "jervis",
                                           RoleTypes.ANALYST,
                                           null,
                                           categoryPath2 ) );
        MockRoleBasedPermissionStore store = new MockRoleBasedPermissionStore( pbps );
        Contexts.getSessionContext().set( "org.drools.guvnor.server.security.RoleBasedPermissionStore",
                                          store );

        // Put permission list in session.
        RoleBasedPermissionManager testManager = new RoleBasedPermissionManager();
        testManager.create();
        Contexts.getSessionContext().set( "roleBasedPermissionManager",
                                          testManager );

        RoleBasedPermissionResolver resolver = new RoleBasedPermissionResolver();
        resolver.setEnableRoleBasedAuthorization( true );

        assertFalse( resolver.hasPermission( new CategoryPathType( categoryPath ),
                                             null ) );
        assertTrue( resolver.hasPermission( new CategoryPathType( categoryPath2 ),
                                            null ) );
        assertFalse( resolver.hasPermission( new CategoryPathType( "category3/category3" ),
                                             null ) );
        assertTrue( resolver.hasPermission( new CategoryPathType( categoryPath ),
                                            RoleTypes.ANALYST_READ ) );
        assertFalse( resolver.hasPermission( new CategoryPathType( categoryPath ),
                                             RoleTypes.ANALYST ) );

        assertTrue( resolver.hasPermission( new CategoryPathType( categoryPath2 ),
                                            RoleTypes.ANALYST ) );
        assertTrue( resolver.hasPermission( new CategoryPathType( categoryPath2 ),
                                            RoleTypes.ANALYST_READ ) );

    }

    @Test
    public void testCategoryBasedPermissionAnalystReadOnly2() throws Exception {

        String categoryPath = "category1";

        List<RoleBasedPermission> pbps = new ArrayList<RoleBasedPermission>();
        pbps.add( new RoleBasedPermission( "jervis",
                                           RoleTypes.ANALYST_READ,
                                           null,
                                           categoryPath ) );
        MockRoleBasedPermissionStore store = new MockRoleBasedPermissionStore( pbps );
        Contexts.getSessionContext().set( "org.drools.guvnor.server.security.RoleBasedPermissionStore",
                                          store );

        // Put permission list in session.
        RoleBasedPermissionManager testManager = new RoleBasedPermissionManager();
        testManager.create();
        Contexts.getSessionContext().set( "roleBasedPermissionManager",
                                          testManager );

        RoleBasedPermissionResolver resolver = new RoleBasedPermissionResolver();
        resolver.setEnableRoleBasedAuthorization( true );

        assertTrue( resolver.hasPermission( new CategoryPathType( categoryPath ),
                                            RoleTypes.ANALYST_READ ) );
        assertFalse( resolver.hasPermission( new CategoryPathType( categoryPath ),
                                             RoleTypes.ANALYST ) );

    }

    @Test
    public void testIsSubPath() {
        RoleBasedPermissionResolver pr = new RoleBasedPermissionResolver();
        assertTrue( pr.isSubPath( "foo",
                                  "foo/bar" ) );
        assertTrue( pr.isSubPath( "foo",
                                  "/foo/bar" ) );
        assertTrue( pr.isSubPath( "/foo/bar",
                                  "/foo/bar" ) );
        assertFalse( pr.isSubPath( "/foo/bar",
                                   "foo" ) );

        assertTrue( pr.isSubPath( "foo",
                                  "foo/bar/baz" ) );
        assertTrue( pr.isSubPath( "foo/bar",
                                  "foo/bar/baz" ) );
        assertFalse( pr.isSubPath( "wang",
                                   "foo/bar/baz" ) );
        assertFalse( pr.isSubPath( "wang/whee",
                                   "foo/bar/baz" ) );

        assertFalse( pr.isSubPath( "foo1",
                                   "foo2" ) );
        assertTrue( pr.isSubPath( "foo1",
                                  "foo1" ) );
    }

    /**
     * This tests that we can navigate the tree if we have sub path permissions.
     */
    @Test
    public void testCategoryBasedSubPerms() throws Exception {

        List<RoleBasedPermission> pbps = new ArrayList<RoleBasedPermission>();
        pbps.add( new RoleBasedPermission( "jervis",
                                           RoleTypes.ANALYST_READ,
                                           null,
                                           "category1/sub1" ) );
        pbps.add( new RoleBasedPermission( "jervis",
                                           RoleTypes.ANALYST,
                                           null,
                                           "category2/sub1/sub2" ) );
        pbps.add( new RoleBasedPermission( "jervis",
                                           RoleTypes.ANALYST,
                                           null,
                                           "category4" ) );
        MockRoleBasedPermissionStore store = new MockRoleBasedPermissionStore( pbps );
        Contexts.getSessionContext().set( "org.drools.guvnor.server.security.RoleBasedPermissionStore",
                                          store );

        // Put permission list in session.
        RoleBasedPermissionManager testManager = new RoleBasedPermissionManager();
        testManager.create();
        Contexts.getSessionContext().set( "roleBasedPermissionManager",
                                          testManager );

        RoleBasedPermissionResolver resolver = new RoleBasedPermissionResolver();
        resolver.setEnableRoleBasedAuthorization( true );

        assertFalse( resolver.hasPermission( new CategoryPathType( "category1" ),
                                             null ) );
        assertFalse( resolver.hasPermission( new CategoryPathType( "category2" ),
                                             null ) );
        assertFalse( resolver.hasPermission( new CategoryPathType( "category1" ),
                                             RoleTypes.ANALYST_READ ) );
        assertFalse( resolver.hasPermission( new CategoryPathType( "category2/sub1" ),
                                             RoleTypes.ANALYST_READ ) );
        assertFalse( resolver.hasPermission( new CategoryPathType( "category1" ),
                                             RoleTypes.ANALYST ) );

        assertTrue( resolver.hasPermission( new CategoryPathType( "category1/sub1" ),
                                            RoleTypes.ANALYST_READ ) );
        assertTrue( resolver.hasPermission( new CategoryPathType( "category2/sub1/sub2" ),
                                            RoleTypes.ANALYST ) );
        assertTrue( resolver.hasPermission( new CategoryPathType( "category2/sub1/sub2" ),
                                            null ) );

        assertTrue( resolver.hasPermission( new CategoryPathType( "category4" ),
                                            "navigate" ) );
        assertTrue( resolver.hasPermission( new CategoryPathType( "category1" ),
                                            "navigate" ) );
        assertTrue( resolver.hasPermission( new CategoryPathType( "category2" ),
                                            "navigate" ) );
        assertTrue( resolver.hasPermission( new CategoryPathType( "category1/sub1" ),
                                            "navigate" ) );
        assertTrue( resolver.hasPermission( new CategoryPathType( "category2/sub1" ),
                                            "navigate" ) );
        assertTrue( resolver.hasPermission( new CategoryPathType( "category1/sub1/sub2" ),
                                            "navigate" ) );
        assertFalse( resolver.hasPermission( new CategoryPathType( "category3" ),
                                             "navigate" ) );

    }

    //admin: everything
    @Test
    public void testPackageBasedPermissionAdmin() throws Exception {
        String package1Name = "testPackageBasedPermissionAdminPackageName1";
        String package2Name = "testPackageBasedPermissionAdminPackageName2";

        List<RoleBasedPermission> pbps = new ArrayList<RoleBasedPermission>();
        pbps.add( new RoleBasedPermission( "jervis",
                                           RoleTypes.ADMIN,
                                           package1Name,
                                           null ) );
        pbps.add( new RoleBasedPermission( "jervis",
                                           RoleTypes.PACKAGE_READONLY,
                                           package2Name,
                                           null ) );
        MockRoleBasedPermissionStore store = new MockRoleBasedPermissionStore( pbps );
        Contexts.getSessionContext().set( "org.drools.guvnor.server.security.RoleBasedPermissionStore",
                                          store );

        // Put permission list in session.
        RoleBasedPermissionManager testManager = new RoleBasedPermissionManager();
        testManager.create();
        Contexts.getSessionContext().set( "roleBasedPermissionManager",
                                          testManager );

        RoleBasedPermissionResolver resolver = new RoleBasedPermissionResolver();
        resolver.setEnableRoleBasedAuthorization( true );

        assertTrue( resolver.hasPermission( new PackageNameType( package1Name ),
                                            RoleTypes.ADMIN ) );
        assertTrue( resolver.hasPermission( new PackageNameType( package2Name ),
                                            RoleTypes.ADMIN ) );

    }

    //Package.admin: everything for that package, including creating snapshots for that package.
    @Test
    public void testPackageBasedPermissionPackageAdmin() throws Exception {
        String packageName = "testPackageBasedPermissionPackageAdminPackageName";

        List<RoleBasedPermission> pbps = new ArrayList<RoleBasedPermission>();
        pbps.add( new RoleBasedPermission( "jervis",
                                           RoleTypes.PACKAGE_ADMIN,
                                           packageName,
                                           null ) );
        MockRoleBasedPermissionStore store = new MockRoleBasedPermissionStore( pbps );
        Contexts.getSessionContext().set( "org.drools.guvnor.server.security.RoleBasedPermissionStore",
                                          store );

        // Put permission list in session.
        RoleBasedPermissionManager testManager = new RoleBasedPermissionManager();
        testManager.create();
        Contexts.getSessionContext().set( "roleBasedPermissionManager",
                                          testManager );

        RoleBasedPermissionResolver resolver = new RoleBasedPermissionResolver();
        resolver.setEnableRoleBasedAuthorization( true );

        assertTrue( resolver.hasPermission( new PackageNameType( packageName ),
                                            RoleTypes.PACKAGE_ADMIN ) );
        assertTrue( resolver.hasPermission( new PackageNameType( packageName ),
                                            RoleTypes.PACKAGE_DEVELOPER ) );
        assertTrue( resolver.hasPermission( new PackageNameType( packageName ),
                                            RoleTypes.PACKAGE_READONLY ) );

        assertFalse( resolver.hasPermission( "47982482-7912-4881-97ec-e852494383d7",
                                             RoleTypes.PACKAGE_READONLY ) );

    }

    //Package.admin: everything for that package, including creating snapshots for that package.
    @Test
    public void testPackageBasedWebDavPermissionPackageAdmin() throws Exception {
        String packageName = "testPackageBasedWebDavPermissionPackageAdmin";

        List<RoleBasedPermission> pbps = new ArrayList<RoleBasedPermission>();
        pbps.add( new RoleBasedPermission( "analyst",
                                           RoleTypes.ANALYST,
                                           packageName,
                                           null ) );
        MockRoleBasedPermissionStore store = new MockRoleBasedPermissionStore( pbps );
        Contexts.getSessionContext().set( "org.drools.guvnor.server.security.RoleBasedPermissionStore",
                                          store );

        // Put permission list in session.
        RoleBasedPermissionManager testManager = new RoleBasedPermissionManager();
        testManager.create();
        Contexts.getSessionContext().set( "roleBasedPermissionManager",
                                          testManager );

        RoleBasedPermissionResolver resolver = new RoleBasedPermissionResolver();
        resolver.setEnableRoleBasedAuthorization( true );
        assertFalse( resolver.hasPermission( new WebDavPackageNameType( packageName ),
                                             RoleTypes.ANALYST ) );
        assertFalse( resolver.hasPermission( new WebDavPackageNameType( packageName ),
                                             RoleTypes.ANALYST_READ ) );

        assertFalse( resolver.hasPermission( "47982482-7912-4881-97ec-e852494383d7",
                                             RoleTypes.PACKAGE_READONLY ) );

    }

    //Package.developer:  everything for that package, NOT snapshots (can view snapshots of that package only)
    @Test
    public void testPackageBasedPermissionPackageDeveloper() throws Exception {
        String package1Name = "testPackageBasedPermissionPackageDeveloperPackageName1";
        String package2Name = "testPackageBasedPermissionPackageDeveloperPackageName2";

        List<RoleBasedPermission> pbps = new ArrayList<RoleBasedPermission>();
        pbps.add( new RoleBasedPermission( "jervis",
                                           RoleTypes.PACKAGE_DEVELOPER,
                                           package1Name,
                                           null ) );
        MockRoleBasedPermissionStore store = new MockRoleBasedPermissionStore( pbps );
        Contexts.getSessionContext().set( "org.drools.guvnor.server.security.RoleBasedPermissionStore",
                                          store );

        // Put permission list in session.
        RoleBasedPermissionManager testManager = new RoleBasedPermissionManager();
        testManager.create();
        Contexts.getSessionContext().set( "roleBasedPermissionManager",
                                          testManager );

        RoleBasedPermissionResolver resolver = new RoleBasedPermissionResolver();
        resolver.setEnableRoleBasedAuthorization( true );

        assertFalse( resolver.hasPermission( new PackageNameType( package1Name ),
                                             RoleTypes.PACKAGE_ADMIN ) );
        assertTrue( resolver.hasPermission( new PackageNameType( package1Name ),
                                            RoleTypes.PACKAGE_DEVELOPER ) );
        assertTrue( resolver.hasPermission( new PackageNameType( package1Name ),
                                            RoleTypes.PACKAGE_READONLY ) );

        assertFalse( resolver.hasPermission( package2Name,
                                             RoleTypes.PACKAGE_READONLY ) );

    }

    //Package.readonly: read only as the name suggested
    @Test
    public void testPackageBasedPermissionPackageReadOnly() throws Exception {
        String package1Name = "testPackageBasedPermissionPackageReadOnlyPackageName1";
        String package2Name = "testPackageBasedPermissionPackageReadOnlyPackageName2";

        List<RoleBasedPermission> pbps = new ArrayList<RoleBasedPermission>();
        pbps.add( new RoleBasedPermission( "jervis",
                                           RoleTypes.PACKAGE_READONLY,
                                           package1Name,
                                           null ) );
        MockRoleBasedPermissionStore store = new MockRoleBasedPermissionStore( pbps );
        Contexts.getSessionContext().set( "org.drools.guvnor.server.security.RoleBasedPermissionStore",
                                          store );

        // Put permission list in session.
        RoleBasedPermissionManager testManager = new RoleBasedPermissionManager();
        testManager.create();
        Contexts.getSessionContext().set( "roleBasedPermissionManager",
                                          testManager );

        RoleBasedPermissionResolver resolver = new RoleBasedPermissionResolver();
        resolver.setEnableRoleBasedAuthorization( true );

        assertFalse( resolver.hasPermission( new PackageNameType( package1Name ),
                                             RoleTypes.PACKAGE_DEVELOPER ) );
        assertFalse( resolver.hasPermission( new PackageNameType( package1Name ),
                                             RoleTypes.PACKAGE_DEVELOPER ) );
        assertTrue( resolver.hasPermission( new PackageNameType( package1Name ),
                                            RoleTypes.PACKAGE_READONLY ) );

        assertFalse( resolver.hasPermission( package2Name,
                                             RoleTypes.PACKAGE_READONLY ) );

    }

    @Test
    public void testPackageBasedPermissionAnalyst() throws Exception {
        String package1Name = "testPackageBasedPermissionAnalystPackageName1";
        String package2Name = "testPackageBasedPermissionAnalystPackageName2";

        List<RoleBasedPermission> pbps = new ArrayList<RoleBasedPermission>();
        pbps.add( new RoleBasedPermission( "jervis",
                                           RoleTypes.PACKAGE_READONLY,
                                           package1Name,
                                           null ) );
        pbps.add( new RoleBasedPermission( "jervis",
                                           RoleTypes.ANALYST,
                                           null,
                                           "category1" ) );
        MockRoleBasedPermissionStore store = new MockRoleBasedPermissionStore( pbps );
        Contexts.getSessionContext().set( "org.drools.guvnor.server.security.RoleBasedPermissionStore",
                                          store );

        // Put permission list in session.
        RoleBasedPermissionManager testManager = new RoleBasedPermissionManager();
        testManager.create();
        Contexts.getSessionContext().set( "roleBasedPermissionManager",
                                          testManager );

        RoleBasedPermissionResolver resolver = new RoleBasedPermissionResolver();
        resolver.setEnableRoleBasedAuthorization( true );

        assertFalse( resolver.hasPermission( new PackageNameType( package1Name ),
                                             RoleTypes.ANALYST ) );
        assertFalse( resolver.hasPermission( new PackageNameType( package2Name ),
                                             RoleTypes.ANALYST ) );
        assertTrue( resolver.hasPermission( new CategoryPathType( "category1" ),
                                            RoleTypes.ANALYST ) );

    }

}
