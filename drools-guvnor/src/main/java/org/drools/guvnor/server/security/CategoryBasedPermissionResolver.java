package org.drools.guvnor.server.security;

import static org.jboss.seam.ScopeType.APPLICATION;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.security.Identity;
import org.jboss.seam.security.permission.PermissionResolver;

/**
 * PermissionResolvers are chained together to resolve permission check, the check returns true if
 * one of the PermissionResolvers in the chain returns true.
 *
 * This PermissionResolver resolves category-based permissions. It returns true under following situations:
 * 1. The user is admin
 * Or
 * 2. The user has at least one analyst role, and at least one of the analyst role has access to requested category path.
 * Or
 * 3. The user does not have any Analyst role(eg, the user only has other roles like package.admin|package.developer|package.readonly)
 *
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

	/**
     * check permission
     *
     * @param requestedCategoryPath
     *            the requestedCategoryPath must be an instance of CategoryPathType,
     *            otherwise return false;
     * @param requestedRole
     *            the requestedRole must be an instance of String, its value has to be one of the
     *            followings: admin|analyst|package.admin|package.developer|package.readonly,
     *            otherwise return false;
     * @return true if the permission can be granted on the requested category path with the
     * requested role; return false otherwise.
     *
     */
	public boolean hasPermission(Object requestedCategoryPath, String requestedRole) {

		//the admin can do everything
		if (Identity.instance().hasRole(RoleTypes.ADMIN)) {
			return true;
		}

		List<RoleBasedPermission> permissions = (List<RoleBasedPermission>) Contexts
				.getSessionContext().get("packageBasedPermission");

		String requestedPath;
		if (requestedCategoryPath instanceof CategoryPathType) {
			requestedPath = ((CategoryPathType)requestedCategoryPath).getCategoryPath();
		} else {
			// CategoryBasedPermissionResolver only grants permissions based on categoryPath.
			// Return false if the input is not a categoryPath, as this will be the responsibility
			//of other PermissionResolvers in the resolver chain.
			return false;
		}



		//category path based permission check only applies to analyst role. If there is no Analyst
		//role (e.g, only other roles like admin|package.admin|package.dev|package.readonly) we always grant permisssion.
		boolean isPermitted = true;
		//return true when there is no analyst role, or one of the analyst role has permission to access this category
		for (RoleBasedPermission pbp : permissions) {
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
		if(requestedPath == null || allowedPath == null) {
			return false;
		}
		return requestedPath.equals(allowedPath) || isSubPath(allowedPath, requestedPath);
	}

	boolean isSubPath(String parentPath, String subPath) {
		parentPath = (parentPath.startsWith("/")) ? parentPath.substring(1) : parentPath;
		subPath = (subPath.startsWith("/")) ? subPath.substring(1) : subPath;
		String[] parentTags = parentPath.split("/");
		String[] subTags = subPath.split("/");
		if (parentTags.length > subTags.length) return false;
		for (int i = 0; i < parentTags.length; i++) {
			if (!parentTags[i].equals(subTags[i])) return false;
		}

		return true;
	}

	public void filterSetByAction(Set<Object> targets, String action) {
	}
}
