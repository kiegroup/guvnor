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

	//admin can do everything
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
		pbps.add(new PackageBasedPermission("631b3d79-5b67-42fb-83da-714624970a6b", "jervis", "package.admin"));
		pbps.add(new PackageBasedPermission("47982482-7912-4881-97ec-e852494383d7", "jervis", "package.guest"));		
    	Contexts.getSessionContext().set("packageBasedPermission", pbps);
    	
    	PackageBasedPermissionResolver resolver = new PackageBasedPermissionResolver();
    	
        assertTrue(resolver.hasPermission("631b3d79-5b67-42fb-83da-714624970a6b", "create"));
        assertTrue(resolver.hasPermission("47982482-7912-4881-97ec-e852494383d7", "create"));

    	Lifecycle.endApplication();
    }    
	
    //Package.admin can do everything within this package
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
		pbps.add(new PackageBasedPermission("631b3d79-5b67-42fb-83da-714624970a6b", "jervis", "package.admin"));
    	Contexts.getSessionContext().set("packageBasedPermission", pbps);
    	
    	PackageBasedPermissionResolver resolver = new PackageBasedPermissionResolver();
        assertTrue(resolver.hasPermission("631b3d79-5b67-42fb-83da-714624970a6b", "create"));
        assertTrue(resolver.hasPermission("631b3d79-5b67-42fb-83da-714624970a6b", "read"));
        assertTrue(resolver.hasPermission("631b3d79-5b67-42fb-83da-714624970a6b", "update"));
        assertTrue(resolver.hasPermission("631b3d79-5b67-42fb-83da-714624970a6b", "delete"));
        
        assertFalse(resolver.hasPermission("47982482-7912-4881-97ec-e852494383d7", "read"));

    	Lifecycle.endApplication();   
    } 
    
    //Package.guest can do read only
    public void testPackageGuest() throws Exception {
    	//Mock up SEAM contexts
    	Map application = new HashMap<String, Object>();    	
    	Lifecycle.beginApplication(application);
    	Lifecycle.beginCall();   	
    	MockIdentity midentity = new MockIdentity();
    	//this makes Identity.hasRole("admin") return false
    	midentity.setHasRole(false);    	
    	Contexts.getSessionContext().set("org.jboss.seam.security.identity", midentity);
    	
    	
    	List<PackageBasedPermission> pbps = new ArrayList<PackageBasedPermission>();
		pbps.add(new PackageBasedPermission("47982482-7912-4881-97ec-e852494383d7", "jervis", "package.guest"));		
    	Contexts.getSessionContext().set("packageBasedPermission", pbps);
    	
    	PackageBasedPermissionResolver resolver = new PackageBasedPermissionResolver();
        
        assertFalse(resolver.hasPermission("47982482-7912-4881-97ec-e852494383d7", "create"));
        assertTrue(resolver.hasPermission("47982482-7912-4881-97ec-e852494383d7", "read"));
        assertFalse(resolver.hasPermission("47982482-7912-4881-97ec-e852494383d7", "update"));
        assertFalse(resolver.hasPermission("47982482-7912-4881-97ec-e852494383d7", "delete"));
        
        assertFalse(resolver.hasPermission("631b3d79-5b67-42fb-83da-714624970a6b", "read"));

    	Lifecycle.endApplication();   
    } 
    
}