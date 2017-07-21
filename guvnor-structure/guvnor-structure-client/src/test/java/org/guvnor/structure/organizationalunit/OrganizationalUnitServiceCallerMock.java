/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
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
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;

/**
 * Utility class for client side testing.
 * <p>
 * Example usage:
 * <p>
 * OrganizationalUnitService serviceMock = mock( OrganizationalUnitService.class );
 * OrganizationalUnitServiceCallerMock serviceCallerMock = new OrganizationalUnitServiceCallerMock( serviceMock );
 * when( serviceMock.someMethod() ).thenReturn( someValue );
 * <p>
 * finally pass the serviceCallerMock to the given presenter.
 */
public class OrganizationalUnitServiceCallerMock
        implements Caller<OrganizationalUnitService> {

    protected OrganizationalUnitServiceWrapper organizationalUnitServiceWrapper;

    protected RemoteCallback remoteCallback;

    public OrganizationalUnitServiceCallerMock(OrganizationalUnitService organizationalUnitService) {
        this.organizationalUnitServiceWrapper = new OrganizationalUnitServiceWrapper(organizationalUnitService);
    }

    @Override
    public OrganizationalUnitService call() {
        return organizationalUnitServiceWrapper;
    }

    @Override
    public OrganizationalUnitService call(RemoteCallback<?> remoteCallback) {
        return call(remoteCallback,
                    null);
    }

    @Override
    public OrganizationalUnitService call(RemoteCallback<?> remoteCallback,
                                          ErrorCallback<?> errorCallback) {
        this.remoteCallback = remoteCallback;
        return organizationalUnitServiceWrapper;
    }

    private class OrganizationalUnitServiceWrapper
            implements OrganizationalUnitService {

        OrganizationalUnitService organizationalUnitService;

        public OrganizationalUnitServiceWrapper(OrganizationalUnitService organizationalUnitService) {
            this.organizationalUnitService = organizationalUnitService;
        }

        @Override
        public OrganizationalUnit getOrganizationalUnit(String name) {
            OrganizationalUnit result = organizationalUnitService.getOrganizationalUnit(name);
            remoteCallback.callback(result);
            return result;
        }

        @Override
        public Collection<OrganizationalUnit> getAllOrganizationalUnits() {
            Collection<OrganizationalUnit> result = organizationalUnitService.getAllOrganizationalUnits();
            remoteCallback.callback(result);
            return result;
        }

        @Override
        public Collection<OrganizationalUnit> getOrganizationalUnits() {
            Collection<OrganizationalUnit> result = organizationalUnitService.getOrganizationalUnits();
            remoteCallback.callback(result);
            return result;
        }

        @Override
        public OrganizationalUnit createOrganizationalUnit(String name,
                                                           String owner,
                                                           String defaultGroupId) {
            OrganizationalUnit result = organizationalUnitService.createOrganizationalUnit(name,
                                                                                           owner,
                                                                                           defaultGroupId);
            remoteCallback.callback(result);
            return result;
        }

        @Override
        public OrganizationalUnit createOrganizationalUnit(String name,
                                                           String owner,
                                                           String defaultGroupId,
                                                           Collection<Repository> repositories) {
            OrganizationalUnit result = organizationalUnitService.createOrganizationalUnit(name,
                                                                                           owner,
                                                                                           defaultGroupId,
                                                                                           repositories);
            remoteCallback.callback(result);
            return result;
        }

        @Override
        public OrganizationalUnit updateOrganizationalUnit(String name,
                                                           String owner,
                                                           String defaultGroupId) {
            OrganizationalUnit result = organizationalUnitService.updateOrganizationalUnit(name,
                                                                                           owner,
                                                                                           defaultGroupId);
            remoteCallback.callback(result);
            return result;
        }

        @Override
        public void addRepository(OrganizationalUnit organizationalUnit,
                                  Repository repository) {
            organizationalUnitService.addRepository(organizationalUnit,
                                                    repository);
        }

        @Override
        public void removeRepository(OrganizationalUnit organizationalUnit,
                                     Repository repository) {
            organizationalUnitService.removeRepository(organizationalUnit,
                                                       repository);
        }

        @Override
        public void addGroup(OrganizationalUnit organizationalUnit,
                             String group) {
            organizationalUnitService.addGroup(organizationalUnit,
                                               group);
        }

        @Override
        public void removeGroup(OrganizationalUnit organizationalUnit,
                                String group) {
            organizationalUnitService.removeGroup(organizationalUnit,
                                                  group);
        }

        @Override
        public void removeOrganizationalUnit(String name) {
            organizationalUnitService.removeOrganizationalUnit(name);
        }

        @Override
        public OrganizationalUnit getParentOrganizationalUnit(Repository repository) {
            OrganizationalUnit result = organizationalUnitService.getParentOrganizationalUnit(repository);
            remoteCallback.callback(result);
            return result;
        }

        @Override
        public String getSanitizedDefaultGroupId(String proposedGroupId) {
            String result = organizationalUnitService.getSanitizedDefaultGroupId(proposedGroupId);
            remoteCallback.callback(result);
            return result;
        }

        @Override
        public Boolean isValidGroupId(String proposedGroupId) {
            Boolean result = organizationalUnitService.isValidGroupId(proposedGroupId);
            remoteCallback.callback(result);
            return result;
        }
    }
}