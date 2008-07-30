package org.drools.guvnor.server.security;

import static org.jboss.seam.ScopeType.APPLICATION;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import org.drools.guvnor.server.ServiceImplementation;
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
 * This PermissionResolver resolves package-based permissions. It returns true under following situations:
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
@Name("org.drools.guvnor.server.security.packageBasedPermissionResolver")
@Scope(APPLICATION)
@BypassInterceptors
@Install(precedence = org.jboss.seam.annotations.Install.APPLICATION)
@Startup
public class PackageBasedPermissionResolver implements PermissionResolver,
		Serializable {

	@Create
	public void create() {
	}

	/**
     * check permission
     *
     * @param requestedPackage
     *            the requestedPackage must be an instance of PackageUUIDType or PackageNameType,
     *            otherwise return false;
     * @param requestedRole
     *            the requestedRole must be an instance of String, its value has to be one of the
     *            followings: admin|analyst|package.admin|package.developer|package.readonly,
     *            otherwise return false;
     * @return true if the permission can be granted on the requested packaged with the
     * requested role; return false otherwise.
     *
     */
	public boolean hasPermission(Object requestedPackage, String requestedRole) {

		//admin can do everything
		if (Identity.instance().hasRole(RoleTypes.ADMIN)) {
			return true;
		}

		List<RoleBasedPermission> permissions = (List<RoleBasedPermission>) Contexts
				.getSessionContext().get("packageBasedPermission");

		String targetUUDI = "";

		if (requestedPackage instanceof PackageUUIDType) {
			targetUUDI = ((PackageUUIDType) requestedPackage).getUUID();
		} else if (requestedPackage instanceof PackageNameType) {
			try {
				ServiceImplementation si = (ServiceImplementation) Component
						.getInstance("org.drools.guvnor.client.rpc.RepositoryService");
				PackageItem source = si.repository
						.loadPackage(((PackageNameType) requestedPackage)
								.getPackageName());
				targetUUDI = source.getUUID();
			} catch (RulesRepositoryException e) {
				return false;
			}

		} else {
			// PackageBasedPermissionResolver only grants permissions based on package info.
			// Return false if the input is not a pacakge info, as this will be the reponsibility
			//of other PermissionResolvers in the resolver chain.
			return false;
		}



		//package based permission check only applies to admin|package.admin|package.dev|package.readonly role.
		//For Analyst we always grant permisssion.
		for (RoleBasedPermission pbp : permissions) {
			if (RoleTypes.ANALYST.equals(pbp.getRole())) {
				return true;
			} else if (targetUUDI.equalsIgnoreCase(pbp.getPackageUUID())
					&& isPermitted(requestedRole, pbp.getRole())) {
				return true;
			}
		}

		return false;
	}

	private boolean isPermitted(String requestedAction, String role) {
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

	public void filterSetByAction(Set<Object> targets, String action) {
	}
}
