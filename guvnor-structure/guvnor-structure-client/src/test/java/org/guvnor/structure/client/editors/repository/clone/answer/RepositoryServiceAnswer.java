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

import org.guvnor.structure.repositories.RepositoryService;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.Matchers.any;
<<<<<<< HEAD
import static org.mockito.Mockito.*;
=======
import static org.mockito.Mockito.when;
>>>>>>> clone repo form refacotred to MVP

public class RepositoryServiceAnswer implements Answer<RepositoryService> {

    private String response;
    private RepositoryService repoService;

<<<<<<< HEAD
    public RepositoryServiceAnswer( String response,
                                    RepositoryService repoService ) {
=======
    public RepositoryServiceAnswer(String response, RepositoryService repoService) {
>>>>>>> clone repo form refacotred to MVP
        this.response = response;
        this.repoService = repoService;
    }

    @Override
<<<<<<< HEAD
    public RepositoryService answer( InvocationOnMock invocation ) throws Throwable {

        when( repoService.normalizeRepositoryName( any( String.class ) ) ).then( new Answer<String>() {

            @Override
            public String answer( InvocationOnMock invocation ) throws Throwable {
                return response;
            }
        } );

        @SuppressWarnings("unchecked")
        final RemoteCallback<String> callback = (RemoteCallback<String>) invocation.getArguments()[ 0 ];
        callback.callback( response );
=======
    public RepositoryService answer(InvocationOnMock invocation) throws Throwable {

        when(repoService.normalizeRepositoryName(any(String.class))).then(new Answer<String>() {

            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {

                return response;
            }
        });

        @SuppressWarnings("unchecked")
        final RemoteCallback<String> callback = (RemoteCallback<String>) invocation.getArguments()[0];
        callback.callback(response);
>>>>>>> clone repo form refacotred to MVP

        return repoService;
    }
}