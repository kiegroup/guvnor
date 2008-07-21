package org.drools.guvnor.server.security;

import static org.jboss.seam.ScopeType.APPLICATION;
import static org.jboss.seam.annotations.Install.FRAMEWORK;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.drools.guvnor.server.ServiceImplementation;
import org.drools.repository.PackageItem;
import org.drools.repository.RulesRepository;
import org.drools.repository.RulesRepositoryException;
import org.jboss.seam.Component;
import org.jboss.seam.Seam;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.security.Identity;
import org.jboss.seam.security.Role;
import org.jboss.seam.security.SimplePrincipal;
import org.jboss.seam.security.permission.PermissionResolver;

/**
 * Resolves category-based permissions.
 *  
 * @author Jervis Liu
 */
@Name("org.drools.guvnor.server.security.categoryBasedPermissionResolver")
@Scope(APPLICATION)
@BypassInterceptors
@Install(precedence = org.jboss.seam.annotations.Install.APPLICATION)
@Startup
public class CategoryBasedPermissionResolver implements PermissionResolver,
		Serializable {

	@Create
	public void create() {
	}

	public boolean hasPermission(Object target, String action) {
		List<RoleBasedPermission> permissions = (List<RoleBasedPermission>) Contexts
				.getSessionContext().get("packageBasedPermission");

		String requestedPath;
		if (target instanceof CategoryPathType) {
			requestedPath = ((CategoryPathType)target).getCategoryPath();
		} else {
			// CategoryBasedPermissionResolver only grants permissions based on categoryPath. 
			// Return false if the input is not a categoryPath, as this will be the reponsibility 
			//of other PermissionResolvers in the resolver chain.
			return false;
		}
		
		//the admin can do everything
		if (Identity.instance().hasRole(RoleTypes.ADMIN)) {
			return true;
		}
		
		//category path based permission check only applies to analyst role. For all the other 
		//roles(admin|package.admin|package.dev|package.readonly) we always grant permisssion.
		boolean isPermitted = true;
		for (RoleBasedPermission pbp : permissions) {
			//the permission check only applies to the analyst role
			if (RoleTypes.ANALYST.equals(pbp.getRole())) {
				isPermitted = false;
				if(isPermitted(requestedPath, pbp.getCategoryPath())) {
					return true;
				}
			}
		}

		return isPermitted;
	}

	private boolean isPermitted(String requestedPath, String allowedPath) {
		return requestedPath.equals(allowedPath) || isSubPath(allowedPath, requestedPath);
	}
	
	private boolean isSubPath(String parentPath, String subPath) {
		//TODO: 
		return false;
	}

	public void filterSetByAction(Set<Object> targets, String action) {
	}
}
