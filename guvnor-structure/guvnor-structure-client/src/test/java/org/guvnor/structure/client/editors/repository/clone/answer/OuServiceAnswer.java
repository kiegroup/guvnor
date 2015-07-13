/*
 * Copyright 2015 JBoss Inc
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

package org.guvnor.structure.client.editors.repository.clone.answer;

<<<<<<< HEAD
import java.util.Collection;

=======
>>>>>>> clone repo form refacotred to MVP
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

<<<<<<< HEAD
import static org.mockito.Mockito.*;
=======
import java.util.Collection;

import static org.mockito.Mockito.when;
>>>>>>> clone repo form refacotred to MVP

public class OuServiceAnswer implements Answer<OrganizationalUnitService> {

    private Collection<OrganizationalUnit> units;
    private OrganizationalUnitService ouService;

<<<<<<< HEAD
    public OuServiceAnswer( Collection<OrganizationalUnit> units,
                            OrganizationalUnitService ouService ) {
=======
    public OuServiceAnswer(Collection<OrganizationalUnit> units, OrganizationalUnitService ouService) {
>>>>>>> clone repo form refacotred to MVP
        this.units = units;
        this.ouService = ouService;
    }

    @Override
<<<<<<< HEAD
    public OrganizationalUnitService answer( InvocationOnMock invocation ) throws Throwable {

        when( ouService.getOrganizationalUnits() ).then( new Answer<Collection<OrganizationalUnit>>() {

            @Override
            public Collection<OrganizationalUnit> answer( InvocationOnMock invocation ) throws Throwable {
                return units;
            }
        } );

        @SuppressWarnings("unchecked")
        final RemoteCallback<Collection<OrganizationalUnit>> callback = (RemoteCallback<Collection<OrganizationalUnit>>) invocation.getArguments()[ 0 ];
        callback.callback( units );
=======
    public OrganizationalUnitService answer(InvocationOnMock invocation) throws Throwable {

        when(ouService.getOrganizationalUnits()).then(new Answer<Collection<OrganizationalUnit>>() {

            @Override
            public Collection<OrganizationalUnit> answer(InvocationOnMock invocation) throws Throwable {

                return units;
            }
        });

        @SuppressWarnings("unchecked")
        final RemoteCallback<Collection<OrganizationalUnit>> callback = (RemoteCallback<Collection<OrganizationalUnit>>) invocation.getArguments()[0];
        callback.callback(units);
>>>>>>> clone repo form refacotred to MVP

        return ouService;
    }
}