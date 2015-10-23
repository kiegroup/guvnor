/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.guvnor.structure.organizationalunit;

import java.util.Collection;

import org.guvnor.structure.repositories.Repository;
import org.jboss.errai.bus.server.annotations.Remote;

@Remote
public interface OrganizationalUnitService {

    OrganizationalUnit getOrganizationalUnit( final String name );

    Collection<OrganizationalUnit> getOrganizationalUnits();

    OrganizationalUnit createOrganizationalUnit( final String name,
                                                 final String owner,
                                                 final String defaultGroupId );

    OrganizationalUnit createOrganizationalUnit( final String name,
                                                 final String owner,
                                                 final String defaultGroupId,
                                                 final Collection<Repository> repositories );

    OrganizationalUnit updateOrganizationalUnit( final String name,
                                                 final String owner,
                                                 final String defaultGroupId );

    void addRepository( final OrganizationalUnit organizationalUnit,
                        final Repository repository );

    void removeRepository( final OrganizationalUnit organizationalUnit,
                           final Repository repository );

    void addGroup( final OrganizationalUnit organizationalUnit,
                   final String group );

    void removeGroup( final OrganizationalUnit organizationalUnit,
                      final String group );

    void removeOrganizationalUnit( final String name );

    OrganizationalUnit getParentOrganizationalUnit( final Repository repository );

    String getSanitizedDefaultGroupId( final String proposedGroupId );

    Boolean isValidGroupId( final String proposedGroupId );
}
