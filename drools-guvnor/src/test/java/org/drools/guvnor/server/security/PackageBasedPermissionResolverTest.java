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




import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.contexts.Lifecycle;
import org.jboss.seam.security.Identity;

import junit.framework.TestCase;

public class PackageBasedPermissionResolverTest extends TestCase {

	//admin: everything
    public void testAdmin() throws Exception {
    	//Mock up SEAM contexts
    	Map application = new HashMap<String, Object>();    	
    	Lifecycle.beginApplication(application);
    	Lifecycle.beginCall();   	
    	MockIdentity midentity = new MockIdentity();
    	//this makes Identity.hasRole("admin") return true
    	midentity.setHasRole(true);    	
    	Contexts.getSessionContext().set("org.jboss.seam.security.identity", midentity);
    	
    	
    	List<PackageBasedPermission> pbps = new ArrayList<PackageBasedPermission>();
		pbps.add(new PackageBasedPermission("631b3d79-5b67-42fb-83da-714624970a6b", "jervis", RoleTypes.ADMIN));
		pbps.add(new PackageBasedPermission("47982482-7912-4881-97ec-e852494383d7", "jervis", RoleTypes.PACKAGE_READONLY));		
    	Contexts.getSessionContext().set("packageBasedPermission", pbps);
    	
    	PackageBasedPermissionResolver resolver = new PackageBasedPermissionResolver();
    	
        assertTrue(resolver.hasPermission("631b3d79-5b67-42fb-83da-714624970a6b", "create"));
        assertTrue(resolver.hasPermission("47982482-7912-4881-97ec-e852494383d7", "create"));

    	Lifecycle.endApplication();
    }    
	
    //Package.admin: everything for that package, including creating snapshots for that package.
    public void testPackageAdmin() throws Exception {
    	//Mock up SEAM contexts
    	Map application = new HashMap<String, Object>();    	
    	Lifecycle.beginApplication(application);
    	Lifecycle.beginCall();   	
    	MockIdentity midentity = new MockIdentity();
    	//this makes Identity.hasRole("admin") return false
    	midentity.setHasRole(false);    	
    	Contexts.getSessionContext().set("org.jboss.seam.security.identity", midentity);
    	
    	
    	List<PackageBasedPermission> pbps = new ArrayList<PackageBasedPermission>();
		pbps.add(new PackageBasedPermission("631b3d79-5b67-42fb-83da-714624970a6b", "jervis", RoleTypes.PACKAGE_ADMIN));
    	Contexts.getSessionContext().set("packageBasedPermission", pbps);
    	
    	PackageBasedPermissionResolver resolver = new PackageBasedPermissionResolver();
        assertTrue(resolver.hasPermission("631b3d79-5b67-42fb-83da-714624970a6b", "package.admin"));
    	assertTrue(resolver.hasPermission("631b3d79-5b67-42fb-83da-714624970a6b", "package.developer"));
        assertTrue(resolver.hasPermission("631b3d79-5b67-42fb-83da-714624970a6b", "package.analyst"));
        assertTrue(resolver.hasPermission("631b3d79-5b67-42fb-83da-714624970a6b", "package.testonly"));
        assertTrue(resolver.hasPermission("631b3d79-5b67-42fb-83da-714624970a6b", "package.readonly"));
        
        assertFalse(resolver.hasPermission("47982482-7912-4881-97ec-e852494383d7", "package.readonly"));

    	Lifecycle.endApplication();   
    } 
    
    //Package.developer:  everything for that package, NOT snapshots (can view snapshots of that package only)
    public void testPackageDeveloper() throws Exception {
    	//Mock up SEAM contexts
    	Map application = new HashMap<String, Object>();    	
    	Lifecycle.beginApplication(application);
    	Lifecycle.beginCall();   	
    	MockIdentity midentity = new MockIdentity();
    	//this makes Identity.hasRole("admin") return false
    	midentity.setHasRole(false);    	
    	Contexts.getSessionContext().set("org.jboss.seam.security.identity", midentity);
    	
    	
    	List<PackageBasedPermission> pbps = new ArrayList<PackageBasedPermission>();
		pbps.add(new PackageBasedPermission("47982482-7912-4881-97ec-e852494383d7", "jervis", RoleTypes.PACKAGE_DEVELOPER));		
    	Contexts.getSessionContext().set("packageBasedPermission", pbps);
    	
    	PackageBasedPermissionResolver resolver = new PackageBasedPermissionResolver();
        
    	assertFalse(resolver.hasPermission("47982482-7912-4881-97ec-e852494383d7", "package.admin"));
    	assertTrue(resolver.hasPermission("47982482-7912-4881-97ec-e852494383d7", "package.developer"));
        assertTrue(resolver.hasPermission("47982482-7912-4881-97ec-e852494383d7", "package.analyst"));
        assertTrue(resolver.hasPermission("47982482-7912-4881-97ec-e852494383d7", "package.testonly"));
        assertTrue(resolver.hasPermission("47982482-7912-4881-97ec-e852494383d7", "package.readonly"));
        
        assertFalse(resolver.hasPermission("631b3d79-5b67-42fb-83da-714624970a6b", "package.readonly"));

    	Lifecycle.endApplication();   
    }
    
    //Package.analyst:  can read all contents. Can only edit/create files of "business" type, 
    //can run tests, and edit tests.
    public void testPackageAnalyst() throws Exception {
    	//Mock up SEAM contexts
    	Map application = new HashMap<String, Object>();    	
    	Lifecycle.beginApplication(application);
    	Lifecycle.beginCall();   	
    	MockIdentity midentity = new MockIdentity();
    	//this makes Identity.hasRole("admin") return false
    	midentity.setHasRole(false);    	
    	Contexts.getSessionContext().set("org.jboss.seam.security.identity", midentity);
    	
    	
    	List<PackageBasedPermission> pbps = new ArrayList<PackageBasedPermission>();
		pbps.add(new PackageBasedPermission("47982482-7912-4881-97ec-e852494383d7", "jervis", RoleTypes.PACKAGE_ANALYST));		
    	Contexts.getSessionContext().set("packageBasedPermission", pbps);
    	
    	PackageBasedPermissionResolver resolver = new PackageBasedPermissionResolver();
        
    	assertFalse(resolver.hasPermission("47982482-7912-4881-97ec-e852494383d7", "package.admin"));
    	assertFalse(resolver.hasPermission("47982482-7912-4881-97ec-e852494383d7", "package.developer"));
        assertTrue(resolver.hasPermission("47982482-7912-4881-97ec-e852494383d7", "package.analyst"));
        assertTrue(resolver.hasPermission("47982482-7912-4881-97ec-e852494383d7", "package.testonly"));
        assertTrue(resolver.hasPermission("47982482-7912-4881-97ec-e852494383d7", "package.readonly"));
        
        assertFalse(resolver.hasPermission("631b3d79-5b67-42fb-83da-714624970a6b", "package.readonly"));

    	Lifecycle.endApplication();   
    }
    
    //Package.testonly:  can create, run, edit, and delete tests only.
    public void testPackageTestonly() throws Exception {
    	//Mock up SEAM contexts
    	Map application = new HashMap<String, Object>();    	
    	Lifecycle.beginApplication(application);
    	Lifecycle.beginCall();   	
    	MockIdentity midentity = new MockIdentity();
    	//this makes Identity.hasRole("admin") return false
    	midentity.setHasRole(false);    	
    	Contexts.getSessionContext().set("org.jboss.seam.security.identity", midentity);
    	
    	
    	List<PackageBasedPermission> pbps = new ArrayList<PackageBasedPermission>();
		pbps.add(new PackageBasedPermission("47982482-7912-4881-97ec-e852494383d7", "jervis", RoleTypes.PACKAGE_TESTONLY));		
    	Contexts.getSessionContext().set("packageBasedPermission", pbps);
    	
    	PackageBasedPermissionResolver resolver = new PackageBasedPermissionResolver();
        
    	assertFalse(resolver.hasPermission("47982482-7912-4881-97ec-e852494383d7", "package.admin"));
    	assertFalse(resolver.hasPermission("47982482-7912-4881-97ec-e852494383d7", "package.developer"));
    	assertFalse(resolver.hasPermission("47982482-7912-4881-97ec-e852494383d7", "package.analyst"));
        assertTrue(resolver.hasPermission("47982482-7912-4881-97ec-e852494383d7", "package.testonly"));
        assertTrue(resolver.hasPermission("47982482-7912-4881-97ec-e852494383d7", "package.readonly"));
        
        assertFalse(resolver.hasPermission("631b3d79-5b67-42fb-83da-714624970a6b", "package.readonly"));

    	Lifecycle.endApplication();   
    }
    
    //Package.readonly: read only as the name suggested
    public void testPackageReadOnly() throws Exception {
    	//Mock up SEAM contexts
    	Map application = new HashMap<String, Object>();    	
    	Lifecycle.beginApplication(application);
    	Lifecycle.beginCall();   	
    	MockIdentity midentity = new MockIdentity();
    	//this makes Identity.hasRole("admin") return false
    	midentity.setHasRole(false);    	
    	Contexts.getSessionContext().set("org.jboss.seam.security.identity", midentity);
    	
    	
    	List<PackageBasedPermission> pbps = new ArrayList<PackageBasedPermission>();
		pbps.add(new PackageBasedPermission("47982482-7912-4881-97ec-e852494383d7", "jervis", RoleTypes.PACKAGE_READONLY));		
    	Contexts.getSessionContext().set("packageBasedPermission", pbps);
    	
    	PackageBasedPermissionResolver resolver = new PackageBasedPermissionResolver();
        
        assertFalse(resolver.hasPermission("47982482-7912-4881-97ec-e852494383d7", "package.admin"));
        assertFalse(resolver.hasPermission("47982482-7912-4881-97ec-e852494383d7", "package.developer"));
        assertFalse(resolver.hasPermission("47982482-7912-4881-97ec-e852494383d7", "package.analyst"));
        assertFalse(resolver.hasPermission("47982482-7912-4881-97ec-e852494383d7", "package.testonly"));
        assertTrue(resolver.hasPermission("47982482-7912-4881-97ec-e852494383d7", "package.readonly"));
        
        assertFalse(resolver.hasPermission("631b3d79-5b67-42fb-83da-714624970a6b", "package.readonly"));

    	Lifecycle.endApplication();   
    } 
    
    
}