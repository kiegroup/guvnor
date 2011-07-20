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

package org.drools.guvnor.server.security;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.drools.guvnor.server.GuvnorTestBase;
import org.jboss.seam.contexts.Contexts;
import org.junit.Test;

public class RoleBasedPermissionResolverTest extends GuvnorTestBase {

    @Test
    public void testCategoryBasedPermissionAnalyst() throws Exception {
        //NOTE: Have to have this call, otherwise this test will fail others tests. Seems to be related to
        //how Seam context initializes the JCR repository, but dont know the exact cause yet. 

        String package1Name = "testCategoryBasedPermissionAnalystPackageName1";
        String package2Name = "testCategoryBasedPermissionAnalystPackageName2";

        List<RoleBasedPermission> pbps = new ArrayList<RoleBasedPermission>();
        pbps.add( new RoleBasedPermission( "jervis",
                                           RoleType.PACKAGE_ADMIN.getName(),
                                           package1Name,
                                           null ) );
        pbps.add( new RoleBasedPermission( "jervis",
                                           RoleType.PACKAGE_READONLY.getName(),
                                           package2Name,
                                           null ) );
        pbps.add( new RoleBasedPermission( "jervis",
                                           RoleType.ANALYST.getName(),
                                           null,
                                           "category1" ) );
        pbps.add( new RoleBasedPermission( "jervis",
                                           RoleType.ANALYST.getName(),
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
                                            RoleType.ANALYST.getName() ) );
        assertTrue( resolver.hasPermission( new CategoryPathType( "category2" ),
                                            RoleType.ANALYST_READ.getName() ) );

        assertFalse( resolver.hasPermission( new CategoryPathType( "category3/category3" ),
                                             RoleType.ANALYST.getName() ) );
        assertFalse( resolver.hasPermission( new CategoryPathType( "category3/category3" ),
                                             RoleType.ANALYST_READ.getName() ) );

    }

    @Test
    public void testCategoryBasedPermissionAnalystReadOnly() throws Exception {
        String package1Name = "testCategoryBasedPermissionAnalystPackageName1";
        String package2Name = "testCategoryBasedPermissionAnalystPackageName2";

        String categoryPath = "category1";
        String categoryPath2 = "category2";

        List<RoleBasedPermission> pbps = new ArrayList<RoleBasedPermission>();
        pbps.add( new RoleBasedPermission( "jervis",
                                           RoleType.PACKAGE_ADMIN.getName(),
                                           package1Name,
                                           null ) );
        pbps.add( new RoleBasedPermission( "jervis",
                                           RoleType.PACKAGE_READONLY.getName(),
                                           package2Name,
                                           null ) );
        pbps.add( new RoleBasedPermission( "jervis",
                                           RoleType.ANALYST_READ.getName(),
                                           null,
                                           categoryPath ) );
        pbps.add( new RoleBasedPermission( "jervis",
                                           RoleType.ANALYST.getName(),
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
                                            RoleType.ANALYST_READ.getName() ) );
        assertFalse( resolver.hasPermission( new CategoryPathType( categoryPath ),
                                             RoleType.ANALYST.getName() ) );

        assertTrue( resolver.hasPermission( new CategoryPathType( categoryPath2 ),
                                            RoleType.ANALYST.getName() ) );
        assertTrue( resolver.hasPermission( new CategoryPathType( categoryPath2 ),
                                            RoleType.ANALYST_READ.getName() ) );

    }

    @Test
    public void testCategoryBasedPermissionAnalystReadOnly2() throws Exception {

        String categoryPath = "category1";

        List<RoleBasedPermission> pbps = new ArrayList<RoleBasedPermission>();
        pbps.add( new RoleBasedPermission( "jervis",
                                           RoleType.ANALYST_READ.getName(),
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
                                            RoleType.ANALYST_READ.getName() ) );
        assertFalse( resolver.hasPermission( new CategoryPathType( categoryPath ),
                                             RoleType.ANALYST.getName() ) );

    }

    @Test
    public void testIsSubPath() {

        assertTrue( PathHelper.isSubPath( "foo",
                                          "foo/bar" ) );
        assertTrue( PathHelper.isSubPath( "foo",
                                          "/foo/bar" ) );
        assertTrue( PathHelper.isSubPath( "/foo/bar",
                                          "/foo/bar" ) );
        assertFalse( PathHelper.isSubPath( "/foo/bar",
                                           "foo" ) );

        assertTrue( PathHelper.isSubPath( "foo",
                                          "foo/bar/baz" ) );
        assertTrue( PathHelper.isSubPath( "foo/bar",
                                          "foo/bar/baz" ) );
        assertFalse( PathHelper.isSubPath( "wang",
                                           "foo/bar/baz" ) );
        assertFalse( PathHelper.isSubPath( "wang/whee",
                                           "foo/bar/baz" ) );

        assertFalse( PathHelper.isSubPath( "foo1",
                                           "foo2" ) );
        assertTrue( PathHelper.isSubPath( "foo1",
                                          "foo1" ) );
    }

    /**
     * This tests that we can navigate the tree if we have sub path permissions.
     */
    @Test
    public void testCategoryBasedSubPerms() throws Exception {

        List<RoleBasedPermission> pbps = new ArrayList<RoleBasedPermission>();
        pbps.add( new RoleBasedPermission( "jervis",
                                           RoleType.ANALYST_READ.getName(),
                                           null,
                                           "category1/sub1" ) );
        pbps.add( new RoleBasedPermission( "jervis",
                                           RoleType.ANALYST.getName(),
                                           null,
                                           "category2/sub1/sub2" ) );
        pbps.add( new RoleBasedPermission( "jervis",
                                           RoleType.ANALYST.getName(),
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
                                             RoleType.ANALYST_READ.getName() ) );
        assertFalse( resolver.hasPermission( new CategoryPathType( "category2/sub1" ),
                                             RoleType.ANALYST_READ.getName() ) );
        assertFalse( resolver.hasPermission( new CategoryPathType( "category1" ),
                                             RoleType.ANALYST.getName() ) );

        assertTrue( resolver.hasPermission( new CategoryPathType( "category1/sub1" ),
                                            RoleType.ANALYST_READ.getName() ) );
        assertTrue( resolver.hasPermission( new CategoryPathType( "category2/sub1/sub2" ),
                                            RoleType.ANALYST.getName() ) );
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
                                           RoleType.ADMIN.getName(),
                                           package1Name,
                                           null ) );
        pbps.add( new RoleBasedPermission( "jervis",
                                           RoleType.PACKAGE_READONLY.getName(),
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
                                            RoleType.ADMIN.getName() ) );
        assertTrue( resolver.hasPermission( new PackageNameType( package2Name ),
                                            RoleType.ADMIN.getName() ) );

    }

    //Package.admin: everything for that package, including creating snapshots for that package.
    @Test
    public void testPackageBasedPermissionPackageAdmin() throws Exception {
        String packageName = "testPackageBasedPermissionPackageAdminPackageName";

        List<RoleBasedPermission> pbps = new ArrayList<RoleBasedPermission>();
        pbps.add( new RoleBasedPermission( "jervis",
                                           RoleType.PACKAGE_ADMIN.getName(),
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
                                            RoleType.PACKAGE_ADMIN.getName() ) );
        assertTrue( resolver.hasPermission( new PackageNameType( packageName ),
                                            RoleType.PACKAGE_DEVELOPER.getName() ) );
        assertTrue( resolver.hasPermission( new PackageNameType( packageName ),
                                            RoleType.PACKAGE_READONLY.getName() ) );

        assertFalse( resolver.hasPermission( "47982482-7912-4881-97ec-e852494383d7",
                                             RoleType.PACKAGE_READONLY.getName() ) );

    }

    //Package.admin: everything for that package, including creating snapshots for that package.
    @Test
    public void testPackageBasedWebDavPermissionPackageAdmin() throws Exception {
        String packageName = "testPackageBasedWebDavPermissionPackageAdmin";

        List<RoleBasedPermission> pbps = new ArrayList<RoleBasedPermission>();
        pbps.add( new RoleBasedPermission( "analyst",
                                           RoleType.ANALYST.getName(),
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
                                             RoleType.ANALYST.getName() ) );
        assertFalse( resolver.hasPermission( new WebDavPackageNameType( packageName ),
                                             RoleType.ANALYST_READ.getName() ) );

        assertFalse( resolver.hasPermission( "47982482-7912-4881-97ec-e852494383d7",
                                             RoleType.PACKAGE_READONLY.getName() ) );

    }

    //Package.developer:  everything for that package, NOT snapshots (can view snapshots of that package only)
    @Test
    public void testPackageBasedPermissionPackageDeveloper() throws Exception {
        String package1Name = "testPackageBasedPermissionPackageDeveloperPackageName1";
        String package2Name = "testPackageBasedPermissionPackageDeveloperPackageName2";

        List<RoleBasedPermission> pbps = new ArrayList<RoleBasedPermission>();
        pbps.add( new RoleBasedPermission( "jervis",
                                           RoleType.PACKAGE_DEVELOPER.getName(),
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
                                             RoleType.PACKAGE_ADMIN.getName() ) );
        assertTrue( resolver.hasPermission( new PackageNameType( package1Name ),
                                            RoleType.PACKAGE_DEVELOPER.getName() ) );
        assertTrue( resolver.hasPermission( new PackageNameType( package1Name ),
                                            RoleType.PACKAGE_READONLY.getName() ) );

        assertFalse( resolver.hasPermission( package2Name,
                                             RoleType.PACKAGE_READONLY.getName() ) );

    }

    //Package.readonly: read only as the name suggested
    @Test
    public void testPackageBasedPermissionPackageReadOnly() throws Exception {
        String package1Name = "testPackageBasedPermissionPackageReadOnlyPackageName1";
        String package2Name = "testPackageBasedPermissionPackageReadOnlyPackageName2";

        List<RoleBasedPermission> pbps = new ArrayList<RoleBasedPermission>();
        pbps.add( new RoleBasedPermission( "jervis",
                                           RoleType.PACKAGE_READONLY.getName(),
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
                                             RoleType.PACKAGE_DEVELOPER.getName() ) );
        assertFalse( resolver.hasPermission( new PackageNameType( package1Name ),
                                             RoleType.PACKAGE_DEVELOPER.getName() ) );
        assertTrue( resolver.hasPermission( new PackageNameType( package1Name ),
                                            RoleType.PACKAGE_READONLY.getName() ) );

        assertFalse( resolver.hasPermission( package2Name,
                                             RoleType.PACKAGE_READONLY.getName() ) );

    }

    @Test
    public void testPackageBasedPermissionAnalyst() throws Exception {
        String package1Name = "testPackageBasedPermissionAnalystPackageName1";
        String package2Name = "testPackageBasedPermissionAnalystPackageName2";

        List<RoleBasedPermission> pbps = new ArrayList<RoleBasedPermission>();
        pbps.add( new RoleBasedPermission( "jervis",
                                           RoleType.PACKAGE_READONLY.getName(),
                                           package1Name,
                                           null ) );
        pbps.add( new RoleBasedPermission( "jervis",
                                           RoleType.ANALYST.getName(),
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
                                             RoleType.ANALYST.getName() ) );
        assertFalse( resolver.hasPermission( new PackageNameType( package2Name ),
                                             RoleType.ANALYST.getName() ) );
        assertTrue( resolver.hasPermission( new CategoryPathType( "category1" ),
                                            RoleType.ANALYST.getName() ) );

    }

}
