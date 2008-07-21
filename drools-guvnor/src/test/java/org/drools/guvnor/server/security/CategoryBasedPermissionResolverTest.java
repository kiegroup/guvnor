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

public class CategoryBasedPermissionResolverTest extends TestCase {
	
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
		pbps.add(new RoleBasedPermission("jervis", RoleTypes.PACKAGE_ADMIN, "631b3d79-5b67-42fb-83da-714624970a6b", null));
		pbps.add(new RoleBasedPermission("jervis", RoleTypes.PACKAGE_READONLY, "47982482-7912-4881-97ec-e852494383d7", null));	
		pbps.add(new RoleBasedPermission("jervis", RoleTypes.ANALYST, null, "category1"));
		pbps.add(new RoleBasedPermission("jervis", RoleTypes.ANALYST, null, "category2"));
    	Contexts.getSessionContext().set("packageBasedPermission", pbps);
    	
    	CategoryBasedPermissionResolver resolver = new CategoryBasedPermissionResolver();
        assertTrue(resolver.hasPermission(new CategoryPathType("category1"), null));
        assertTrue(resolver.hasPermission(new CategoryPathType("category2"), null));
        assertFalse(resolver.hasPermission(new CategoryPathType("category3/category3"), null));
        //TODO:
        //assertTrue(resolver.hasPermission(new CategoryPathType("/category1/category2"), null));
 
    	Lifecycle.endApplication();   
    } 
 }