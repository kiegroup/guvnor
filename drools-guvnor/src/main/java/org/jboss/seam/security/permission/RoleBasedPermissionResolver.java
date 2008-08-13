package org.jboss.seam.security.permission;

import static org.jboss.seam.ScopeType.APPLICATION;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import org.drools.guvnor.server.ServiceImplementation;
import org.drools.guvnor.server.security.CategoryPathType;
import org.drools.guvnor.server.security.PackageNameType;
import org.drools.guvnor.server.security.PackageUUIDType;
import org.drools.guvnor.server.security.RoleBasedPermission;
import org.drools.guvnor.server.security.RoleBasedPermissionStore;
import org.drools.guvnor.server.security.RoleTypes;
import org.drools.repository.PackageItem;
import org.drools.repository.RulesRepositoryException;
import org.jboss.seam.Component;
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
 * This PermissionResolver resolves category-based permissions and package-based permissions.
 *
 * If the input is category-based request, it returns true under following situations:
 *
 * For category-based permissions:
 * 1. The user is admin
 * Or
 * 2. The user has at least one analyst role, and at least one of the analyst role has access to requested category path.
 * Or
 * 3. The user does not have any Analyst role(eg, the user only has other roles like package.admin|package.developer|package.readonly)
 *
 * If the input is package-based request, it returns true under following situations:
 * 1. The user is admin
 * Or
 * 2. The user has one of the following roles package.admin|package.developer|package.readonly on the requested
 * package, and requested role requires lower privilege than assigned role(I.e., package.admin>package.developer>package.readonly)
 * Or
 * 3. The user is Analyst
 *
 *

 * @author Jervis Liu
 */
@Name("org.jboss.seam.security.roleBasedPermissionResolver")
@Scope(APPLICATION)
@BypassInterceptors
@Install(precedence = org.jboss.seam.annotations.Install.APPLICATION)
@Startup
public class RoleBasedPermissionResolver implements PermissionResolver,
		Serializable {

	private boolean enableRoleBasedAuthorization = false;

	@Create
	public void create() {
	}

	/**
     * check permission
     *
     * @param requestedObject
     *            the requestedObject must be an instance of CategoryPathType,
     *            or PackageNameType or PackageUUIDType.
     *            Otherwise return false;
     * @param requestedRole
     *            the requestedRole must be an instance of String, its value has to be one of the
     *            followings: admin|analyst|package.admin|package.developer|package.readonly,
     *            otherwise return false;
     * @return true if the permission can be granted on the requested object with the
     * requested role; return false otherwise.
     *
     */
	public boolean hasPermission(Object requestedObject, String requestedRole) {
		if (!((requestedObject instanceof CategoryPathType)
				|| (requestedObject instanceof PackageNameType)
				|| (requestedObject instanceof PackageUUIDType))) {
			return false;
		}

		if (!enableRoleBasedAuthorization) {
			return true;
		}

        RoleBasedPermissionStore pbps = (RoleBasedPermissionStore) Component
				.getInstance("org.drools.guvnor.server.security.RoleBasedPermissionStore");
		List<RoleBasedPermission> permissions = pbps
				.getRoleBasedPermissionsByUserName(Identity.instance()
						.getCredentials().getUsername());

		for (RoleBasedPermission p : permissions) {
			if (RoleTypes.ADMIN.equalsIgnoreCase(p.getRole())) {
				return true;
			}
		}

		if (requestedObject instanceof CategoryPathType) {
			String requestedPath = ((CategoryPathType) requestedObject)
					.getCategoryPath();

			//category path based permission check only applies to analyst role. If there is no Analyst
			//role (e.g, only other roles like admin|package.admin|package.dev|package.readonly) we always grant permisssion.
			boolean isPermitted = true;
			//return true when there is no analyst role, or one of the analyst role has permission to access this category
			for (RoleBasedPermission pbp : permissions) {
				if (RoleTypes.ANALYST.equals(pbp.getRole())) {
					isPermitted = false;
					if(isPermittedCategoryPath(requestedPath, pbp.getCategoryPath())) {
						return true;
					}
				}
			}

			return isPermitted;
		} else {
			String targetName = "";

			if (requestedObject instanceof PackageUUIDType) {
				String targetUUID = ((PackageUUIDType) requestedObject).getUUID();
				try {
					ServiceImplementation si = (ServiceImplementation) Component
							.getInstance("org.drools.guvnor.client.rpc.RepositoryService");
					targetName = si.repository.loadPackageByUUID(targetUUID).getName();
				} catch (RulesRepositoryException e) {
					return false;
				}
			} else if (requestedObject instanceof PackageNameType) {
				targetName = ((PackageNameType) requestedObject).getPackageName();
			}

			//package based permission check only applies to admin|package.admin|package.dev|package.readonly role.
			//For Analyst we always grant permission.
			for (RoleBasedPermission pbp : permissions) {
				if (RoleTypes.ANALYST.equals(pbp.getRole())) {
					return true;
				} else if (targetName.equalsIgnoreCase(pbp.getPackageName())
						&& isPermittedPackage(requestedRole, pbp.getRole())) {
					return true;
				}
			}

			return false;
		}
	}

	private boolean isPermittedCategoryPath(String requestedPath, String allowedPath) {
		if(requestedPath == null || allowedPath == null) {
			return false;
		}
		return requestedPath.equals(allowedPath) || isSubPath(allowedPath, requestedPath);
	}


	private boolean isPermittedPackage(String requestedAction, String role) {
		if (RoleTypes.PACKAGE_ADMIN.equalsIgnoreCase(role)) {
			return true;
		} else if (RoleTypes.PACKAGE_DEVELOPER.equalsIgnoreCase(role)) {
			if (RoleTypes.PACKAGE_ADMIN.equalsIgnoreCase(requestedAction)) {
				return false;
			} else if (RoleTypes.PACKAGE_DEVELOPER.equalsIgnoreCase(requestedAction)) {
				return true;
			} else if (RoleTypes.PACKAGE_READONLY.equalsIgnoreCase(requestedAction)) {
				return true;
			}
		} else if (RoleTypes.PACKAGE_READONLY.equalsIgnoreCase(role)) {
			if (RoleTypes.PACKAGE_ADMIN.equalsIgnoreCase(requestedAction)) {
				return false;
			} else if (RoleTypes.PACKAGE_DEVELOPER.equalsIgnoreCase(requestedAction)) {
				return false;
			} else if (RoleTypes.PACKAGE_READONLY.equalsIgnoreCase(requestedAction)) {
				return true;
			}
		}

		return false;
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

	public boolean isEnableRoleBasedAuthorization() {
		return enableRoleBasedAuthorization;
	}

	public void setEnableRoleBasedAuthorization(boolean enableRoleBasedAuthorization) {
		this.enableRoleBasedAuthorization = enableRoleBasedAuthorization;
	}
}
