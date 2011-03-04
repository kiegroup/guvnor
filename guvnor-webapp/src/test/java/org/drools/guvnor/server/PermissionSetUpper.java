package org.drools.guvnor.server;

import org.drools.guvnor.server.security.RoleBasedPermission;
import org.drools.repository.RulesRepository;
import org.drools.repository.security.PermissionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PermissionSetUpper {

    private PermissionManager permissionManager;

    public PermissionSetUpper(RulesRepository rulesRepository) {
        this.permissionManager = new PermissionManager(rulesRepository);
    }

    void addRoleBasedPermissionForTesting(String userName, RoleBasedPermission roleBasedPermission) {
        Map<String, List<String>> userPermissions = permissionManager.retrieveUserPermissions(userName);

        Object permissionsPerRolesObject = userPermissions.get(roleBasedPermission.getRole());
        if (permissionsPerRolesObject != null) {
            List<String> permissionsPerRoles = (List<String>) permissionsPerRolesObject;
            if (roleBasedPermission.getPackageName() != null) {
                permissionsPerRoles.add(createPackagePermissionString(roleBasedPermission.getPackageName()));
            } else if (roleBasedPermission.getCategoryPath() != null) {
                permissionsPerRoles.add(createCategoryPermissionString(roleBasedPermission.getPackageName()));
            }
        } else {
            List<String> permission = new ArrayList<String>();
            if (roleBasedPermission.getPackageName() != null) {
                permission.add(createPackagePermissionString(roleBasedPermission.getPackageName()));
            } else if (roleBasedPermission.getCategoryPath() != null) {
                permission.add(createCategoryPermissionString(roleBasedPermission.getCategoryPath()));
            }
            userPermissions.put(roleBasedPermission.getRole(), permission);
        }

        permissionManager.updateUserPermissions(userName, userPermissions);
    }

    private String createCategoryPermissionString(String categoryPath) {
        return String.format("category=%s", categoryPath);
    }

    private String createPackagePermissionString(String packageName) {
        return String.format("package=%s", packageName);
    }

}
