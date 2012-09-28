/*
 * Copyright 2011 JBoss Inc
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
package org.drools.guvnor.server.security.rules;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.drools.guvnor.server.ServiceImplementation;
import org.drools.guvnor.server.repository.Preferred;
import org.drools.guvnor.server.security.ModuleUUIDType;
import org.drools.guvnor.server.security.RoleBasedPermission;
import org.drools.repository.RulesRepository;
import org.drools.repository.RulesRepositoryException;

@ApplicationScoped
public class PackageUUIDTypePermissionRule
    implements
    PermissionRule {

    @Inject @Preferred
    private RulesRepository rulesRepository;

    public boolean hasPermission(Object requestedObject,
                                 String requestedPermission,
                                 List<RoleBasedPermission> permissions) {
        String targetName;
        String targetUUID = ((ModuleUUIDType) requestedObject).getUUID();
        try {
            targetName = rulesRepository.loadModuleByUUID( targetUUID ).getName();
        } catch ( RulesRepositoryException e ) {
            return false;
        }

        return new PackagePermissionRule().hasPermission( targetName,
                                                          requestedPermission,
                                                          permissions );
    }

}
