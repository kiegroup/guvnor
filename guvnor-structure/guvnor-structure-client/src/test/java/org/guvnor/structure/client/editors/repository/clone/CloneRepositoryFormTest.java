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

package org.guvnor.structure.client.editors.repository.clone;

import java.util.ArrayList;
import java.util.List;

import org.guvnor.structure.client.editors.repository.RepositoryPreferences;
import org.guvnor.structure.client.editors.repository.clone.answer.RepositoryServiceAnswer;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryService;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.client.mvp.PlaceManager;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.client.mvp.PlaceManager;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Matchers.any;


@RunWith(MockitoJUnitRunner.class)
public class CloneRepositoryFormTest {

    @Mock
    private PlaceManager placeManager;
    @Mock
    private RepositoryPreferences repositoryPreferences;
    @Mock
    private CloneRepositoryView view;
    @Mock
    private Caller<RepositoryService> repoServiceCaller;
    @Mock
    private Caller<OrganizationalUnitService> ouServiceCaller;
    @Mock
    private OrganizationalUnitService  ouService;
    @Mock
    private RepositoryService repoService;
    @Mock
    private OrganizationalUnit ouUnit1;
    @Mock
    private OrganizationalUnit ouUnit2;
    @Mock
    private Repository repository;

    @Captor
    private ArgumentCaptor<Boolean> boolArgument;

    private CloneRepositoryPresenter presenter;
    
    @Before
    public void initPresenter() {
        List<OrganizationalUnit> units = new ArrayList<OrganizationalUnit>();
        units.add(ouUnit1);
        units.add(ouUnit2);

        when(ouUnit1.getName()).thenReturn("OU1");
        when(ouUnit2.getName()).thenReturn("OU2");

        when(view.getName()).thenReturn("OU1");

        when(ouService.getOrganizationalUnits()).thenReturn(new ArrayList<OrganizationalUnit>());
        when(ouServiceCaller.call(any(RemoteCallback.class), any(ErrorCallback.class))).thenReturn(ouService);
        when(repoService.normalizeRepositoryName(any(String.class))).thenReturn("OU1");
        when(repoServiceCaller.call(any(RemoteCallback.class))).thenAnswer(new RepositoryServiceAnswer("OU1", repoService));
        when(repoServiceCaller.call(any(RemoteCallback.class), any(ErrorCallback.class))).thenReturn(repoService);
        when(view.getUsername()).thenReturn("username");
        when(view.getPassword()).thenReturn("password");

        when(repositoryPreferences.isOUMandatory()).thenReturn(false);

        presenter = new CloneRepositoryPresenter(repositoryPreferences, view, repoServiceCaller, ouServiceCaller, placeManager);

        presenter.init();
    }

    /**
     * BZ 1003005 - Clone repository dialogue stays operational.
     */
    @Test
    public void testComponentsStaysOperational() {
        when(view.isGitUrlEmpty()).thenReturn(false);
        when(view.getGitUrl()).thenReturn("some.git.url");

        presenter.handleCloneClick();

        verify(view).setCloneEnabled(boolArgument.capture());
        assertEquals(false, boolArgument.getValue());

        verify(view).setGitUrlEnabled(boolArgument.capture());
        assertEquals(false, boolArgument.getValue());

        verify(view).setNameEnabled(boolArgument.capture());
        assertEquals(false, boolArgument.getValue());

        verify(view).setOrganizationalUnitEnabled(boolArgument.capture());
        assertEquals(false, boolArgument.getValue());

        verify(view).setUsernameEnabled(boolArgument.capture());
        assertEquals(false, boolArgument.getValue());

        verify(view).setPasswordEnabled(boolArgument.capture());
        assertEquals(false, boolArgument.getValue());
    }

    /**
     * BZ 1006906 - Repository clone doesn't validate URL
     */
    @Test
    public void testGitUrlValidation() {
        when(view.isGitUrlEmpty()).thenReturn(true);
        when(view.getGitUrl()).thenReturn("");

        presenter.handleCloneClick();
        verify(view).showUrlHelpManatoryMessage();

        when(view.isGitUrlEmpty()).thenReturn(false);
        when(view.getGitUrl()).thenReturn("a b c");

        presenter.handleCloneClick();
        verify(view).showUrlHelpInvalidFormatMessage();
    }

}
