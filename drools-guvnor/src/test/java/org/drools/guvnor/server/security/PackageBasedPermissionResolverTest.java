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
    	
    	
    	List<RoleBasedPermission> pbps = new ArrayList<RoleBasedPermission>();
		pbps.add(new RoleBasedPermission("jervis", RoleTypes.ADMIN, "631b3d79-5b67-42fb-83da-714624970a6b", null));
		pbps.add(new RoleBasedPermission("jervis", RoleTypes.PACKAGE_READONLY, "47982482-7912-4881-97ec-e852494383d7", null));		
    	Contexts.getSessionContext().set("packageBasedPermission", pbps);
    	
    	PackageBasedPermissionResolver resolver = new PackageBasedPermissionResolver();
    	
        assertTrue(resolver.hasPermission(new PackageUUIDType("631b3d79-5b67-42fb-83da-714624970a6b"), RoleTypes.ADMIN));
        assertTrue(resolver.hasPermission(new PackageUUIDType("47982482-7912-4881-97ec-e852494383d7"), RoleTypes.ADMIN));

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
    	
    	
    	List<RoleBasedPermission> pbps = new ArrayList<RoleBasedPermission>();
		pbps.add(new RoleBasedPermission("jervis", RoleTypes.PACKAGE_ADMIN, "631b3d79-5b67-42fb-83da-714624970a6b", null));
    	Contexts.getSessionContext().set("packageBasedPermission", pbps);
    	
    	PackageBasedPermissionResolver resolver = new PackageBasedPermissionResolver();
        assertTrue(resolver.hasPermission(new PackageUUIDType("631b3d79-5b67-42fb-83da-714624970a6b"), RoleTypes.PACKAGE_ADMIN));
    	assertTrue(resolver.hasPermission(new PackageUUIDType("631b3d79-5b67-42fb-83da-714624970a6b"), RoleTypes.PACKAGE_DEVELOPER));
        assertTrue(resolver.hasPermission(new PackageUUIDType("631b3d79-5b67-42fb-83da-714624970a6b"), RoleTypes.PACKAGE_READONLY));
        
        assertFalse(resolver.hasPermission("47982482-7912-4881-97ec-e852494383d7", RoleTypes.PACKAGE_READONLY));

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
    	
    	
    	List<RoleBasedPermission> pbps = new ArrayList<RoleBasedPermission>();
		pbps.add(new RoleBasedPermission("jervis", RoleTypes.PACKAGE_DEVELOPER, "47982482-7912-4881-97ec-e852494383d7", null));		
    	Contexts.getSessionContext().set("packageBasedPermission", pbps);
    	
    	PackageBasedPermissionResolver resolver = new PackageBasedPermissionResolver();
        
    	assertFalse(resolver.hasPermission(new PackageUUIDType("47982482-7912-4881-97ec-e852494383d7"), RoleTypes.PACKAGE_ADMIN));
    	assertTrue(resolver.hasPermission(new PackageUUIDType("47982482-7912-4881-97ec-e852494383d7"), RoleTypes.PACKAGE_DEVELOPER));
        assertTrue(resolver.hasPermission(new PackageUUIDType("47982482-7912-4881-97ec-e852494383d7"), RoleTypes.PACKAGE_READONLY));
        
        assertFalse(resolver.hasPermission("631b3d79-5b67-42fb-83da-714624970a6b", RoleTypes.PACKAGE_READONLY));

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
    	
    	
    	List<RoleBasedPermission> pbps = new ArrayList<RoleBasedPermission>();
		pbps.add(new RoleBasedPermission("jervis", RoleTypes.PACKAGE_READONLY, "47982482-7912-4881-97ec-e852494383d7", null));		
    	Contexts.getSessionContext().set("packageBasedPermission", pbps);
    	
    	PackageBasedPermissionResolver resolver = new PackageBasedPermissionResolver();
        
        assertFalse(resolver.hasPermission(new PackageUUIDType("47982482-7912-4881-97ec-e852494383d7"), RoleTypes.PACKAGE_DEVELOPER));
        assertFalse(resolver.hasPermission(new PackageUUIDType("47982482-7912-4881-97ec-e852494383d7"), RoleTypes.PACKAGE_DEVELOPER));
        assertTrue(resolver.hasPermission(new PackageUUIDType("47982482-7912-4881-97ec-e852494383d7"), RoleTypes.PACKAGE_READONLY));
        
        assertFalse(resolver.hasPermission("631b3d79-5b67-42fb-83da-714624970a6b", RoleTypes.PACKAGE_READONLY));

    	Lifecycle.endApplication();   
    } 
        
    public void testAnalyst() throws Exception {
    	//Mock up SEAM contexts
    	Map application = new HashMap<String, Object>();    	
    	Lifecycle.beginApplication(application);
    	Lifecycle.beginCall();   	
    	MockIdentity midentity = new MockIdentity();
    	//this makes Identity.hasRole("admin") return false
    	midentity.setHasRole(false);    	
    	Contexts.getSessionContext().set("org.jboss.seam.security.identity", midentity);
    	
    	
    	List<RoleBasedPermission> pbps = new ArrayList<RoleBasedPermission>();
		pbps.add(new RoleBasedPermission("jervis", RoleTypes.PACKAGE_READONLY, "47982482-7912-4881-97ec-e852494383d7", null));		
		pbps.add(new RoleBasedPermission("jervis", RoleTypes.ANALYST, null, "category1"));		
    	Contexts.getSessionContext().set("packageBasedPermission", pbps);
    	
    	PackageBasedPermissionResolver resolver = new PackageBasedPermissionResolver();
        
        assertTrue(resolver.hasPermission(new PackageUUIDType("47982482-7912-4881-97ec-e852494383d7"), RoleTypes.ANALYST));
        assertTrue(resolver.hasPermission(new PackageUUIDType("631b3d79-5b67-42fb-83da-714624970a6b"), RoleTypes.ANALYST));

    	Lifecycle.endApplication();   
    } 
    
    
}