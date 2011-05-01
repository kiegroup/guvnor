/*
 * Copyright 2011 JBoss Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package org.drools.guvnor.client.admin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import org.drools.guvnor.client.admin.PerspectivesManagerView.Presenter;
import org.drools.guvnor.client.rpc.ConfigurationServiceAsync;
import org.drools.guvnor.client.rpc.IFramePerspectiveConfiguration;
import org.drools.guvnor.client.util.SaveCommand;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.exceptions.base.MockitoException;

import com.google.gwt.user.client.rpc.SerializationException;

public class PerspectivesManagerTest {

    private PerspectivesManager                            perspectivesManager;
    private PerspectivesManagerView                        view;
    private Presenter                                      presenter;
    private ConfigurationServiceAsync                      configurationServiceAsync;
    private ArgumentCaptor< ? >                            saveCommandCaptor;
    private ArgumentCaptor<IFramePerspectiveConfiguration> iFramePerspectiveConfigurationCaptor;

    @Before
    public void setUp() throws Exception {
        view = mock( PerspectivesManagerView.class );
        configurationServiceAsync = new ConfigurationServiceAsyncMock();
        saveCommandCaptor = ArgumentCaptor.forClass( Object.class );
        iFramePerspectiveConfigurationCaptor = ArgumentCaptor.forClass( IFramePerspectiveConfiguration.class );
        addPerspectiveToResult( getConfiguration( "mock-runtime-uuid",
                                                  "Runtime",
                                                  "http://drools.org/runtime" ) );
        addPerspectiveToResult( getConfiguration( "mock-manual-uuid",
                                                  "Manual",
                                                  "http://drools.org/manual" ) );
        perspectivesManager = new PerspectivesManager( configurationServiceAsync,
                                                       view );
        presenter = getPresenter();
    }

    private Presenter getPresenter() {
        return perspectivesManager;
    }

    @Test
    public void testPresenterSet() throws Exception {
        verify( view ).setPresenter( presenter );
    }

    @Test
    public void testShowExisting() throws Exception {
        verifyThatTheFollowingPerspectiveWasAddedToThePerspectivesList( "mock-runtime-uuid",
                                                                        "Runtime" );
        verifyThatTheFollowingPerspectiveWasAddedToThePerspectivesList( "mock-manual-uuid",
                                                                        "Manual" );
    }

    @Test
    public void testAddPerspective() throws Exception {
        userClicksAddNewPerspective();

        verifyThePopUpForTheConfigurationBecomesVisible();

        userFillsTheFieldsWithTheFollowingValuesAndClicksSaveInThePopUp( "Manual",
                                                                         "http://drools.org/manual" );

        verifyThePopUpIsClosed();

        assertFollowingConfigurationWasSavedToRepository( null,
                                                          "Manual",
                                                          "http://drools.org/manual" );

        verifyThatTheFollowingPerspectiveWasAddedToThePerspectivesList( "mock-manual-uuid",
                                                                        "Manual" );
    }

    @Test
    public void testModifyPerspective() throws Exception {
        thePerspectivesListContainsTheFollowingItem( getConfiguration( "mock-runtime-uuid",
                                                                       "Runtime",
                                                                       "http://drools.org/runtime" ) );

        userSelectsAPerspectiveFromTheList( "mock-runtime-uuid" );

        userClicksEditPerspectiveButton();

        verifyThatTheEditingPopUpOpensWithTheFollowingValues( "mock-runtime-uuid",
                                                              "Runtime",
                                                              "http://drools.org/runtime" );

        userFillsTheFieldsWithTheFollowingValuesAndClicksSaveInThePopUp( "Manual",
                                                                         "http://drools.org/manual" );

        verifyThePopUpIsClosed();

        assertFollowingConfigurationWasSavedToRepository( "mock-runtime-uuid",
                                                          "Manual",
                                                          "http://drools.org/manual" );

        verifyThatTheItemIsRemovedFromTheList( "mock-runtime-uuid" );

        verifyThatTheFollowingPerspectiveWasAddedToThePerspectivesList( "mock-runtime-uuid",
                                                                        "Manual" );
    }

    @Test
    public void testDeletePerspective() throws Exception {
        thePerspectivesListContainsTheFollowingItem( getConfiguration( "mock-runtime-uuid",
                                                                       "Runtime",
                                                                       "http://drools.org/runtime" ) );

        userSelectsAPerspectiveFromTheList( "mock-runtime-uuid" );

        userClicksDeletePerspectiveButton();

        verifyThatTheItemIsRemovedFromRepository( "mock-runtime-uuid" );

        verifyThatTheItemIsRemovedFromTheList( "mock-runtime-uuid" );
    }

    @Test
    public void testNameSetForAPerspectiveAlreadyExists() throws Exception {
        thePerspectivesListContainsTheFollowingItem( getConfiguration( "mock-runtime-uuid",
                                                                       "Runtime",
                                                                       "http://drools.org/runtime" ) );

        userClicksAddNewPerspective();

        verifyThePopUpForTheConfigurationBecomesVisible();

        userFillsTheFieldsWithTheFollowingValuesAndClicksSaveInThePopUp( "Runtime",
                                                                         "http://drools.org/urlDoesNotNeedToMatch" );

        verifyThePopUpHasNotBeenClosed();

        verifyUserGetsAWarningAboutUsedName();
    }

    @Test
    public void testNoSelectedItemWhenEditing() throws Exception {
        thePerspectivesListContainsTheFollowingItem( getConfiguration( "mock-runtime-uuid",
                                                                       "Runtime",
                                                                       "http://drools.org/runtime" ) );

        userClicksEditPerspectiveButton();

        verifyThePopUpForTheConfigurationWasNotOpened();

        verifyNoSelectedPerspectiveErrorWasShown();
    }

    @Test
    public void testNoSelectedItemWhenDeleting() throws Exception {
        thePerspectivesListContainsTheFollowingItem( getConfiguration( "mock-runtime-uuid",
                                                                       "Runtime",
                                                                       "http://drools.org/runtime" ) );

        userClicksDeletePerspectiveButton();

        verifyNoSelectedPerspectiveErrorWasShown();
    }

    private void verifyThePopUpIsClosed() {
        verify( view ).closePopUp();
    }

    private void verifyThePopUpHasNotBeenClosed() {
        verify( view,
                never() ).closePopUp();
    }

    private void verifyUserGetsAWarningAboutUsedName() {
        verify( view ).showNameTakenError( anyString() );
    }

    private void verifyNoSelectedPerspectiveErrorWasShown() {
        verify( view ).showNoSelectedPerspectiveError();
    }

    private void verifyThatTheItemIsRemovedFromTheList(String uuid) {
        verify( view ).removePerspective( uuid );
    }

    private void verifyThatTheItemIsRemovedFromRepository(String uuid) {
        String removedUuid = getMockService().getRemovedUuid();
        assertEquals( uuid,
                      removedUuid );
    }

    private void userClicksDeletePerspectiveButton() {
        presenter.onRemovePerspective();
    }

    private void userFillsTheFieldsWithTheFollowingValuesAndClicksSaveInThePopUp(String name,
                                                                                 String url) {
        IFramePerspectiveConfiguration configurationBeingEdited = getConfigurationBeingEdited();
        SaveCommand<IFramePerspectiveConfiguration> saveCommand = (SaveCommand<IFramePerspectiveConfiguration>) saveCommandCaptor.getValue();
        if ( areWeCreatingANewConfiguration( configurationBeingEdited ) ) {
            saveCommand.save( getConfiguration( null,
                                                name,
                                                url ) );
        } else {
            saveCommand.save( getConfiguration( configurationBeingEdited.getUuid(),
                                                name,
                                                url ) );
        }
    }

    private IFramePerspectiveConfiguration getConfigurationBeingEdited() {
        try {
            return iFramePerspectiveConfigurationCaptor.getValue();
        } catch ( MockitoException e ) {
            return null;
        }
    }

    private void assertFollowingConfigurationWasSavedToRepository(String uuid,
                                                                  String name,
                                                                  String url) {
        assertConfigurationsEquals( uuid,
                                    name,
                                    url,
                                    getMockService().getSaved() );
    }

    private boolean areWeCreatingANewConfiguration(IFramePerspectiveConfiguration configurationBeingEdited) {
        return configurationBeingEdited == null;
    }

    private void verifyThatTheEditingPopUpOpensWithTheFollowingValues(String uuid,
                                                                      String name,
                                                                      String url) {
        verify( view ).openPopUp( (SaveCommand<IFramePerspectiveConfiguration>) saveCommandCaptor.capture(),
                                  iFramePerspectiveConfigurationCaptor.capture() );

        assertConfigurationsEquals( uuid,
                                    name,
                                    url,
                                    getConfigurationBeingEdited() );
    }

    private void thePerspectivesListContainsTheFollowingItem(IFramePerspectiveConfiguration configuration) {
        getMockService().setUpLoad( configuration );
        ArrayList names = new ArrayList();
        names.add( configuration.getName() );
        when( view.getListOfPerspectiveNames() ).thenReturn( names );
    }

    private void userClicksEditPerspectiveButton() {
        try {
            presenter.onEditPerspective();
        } catch ( SerializationException e ) {
            fail( e.getMessage() );
        }
    }

    private void userSelectsAPerspectiveFromTheList(String uuid) {
        when( view.getSelectedPerspectiveUuid() ).thenReturn( uuid );
    }

    private void assertConfigurationsEquals(String uuid,
                                            String name,
                                            String url,
                                            IFramePerspectiveConfiguration editedConfiguration) {
        assertEquals( uuid,
                      editedConfiguration.getUuid() );
        assertEquals( name,
                      editedConfiguration.getName() );
        assertEquals( url,
                      editedConfiguration.getUrl() );
    }

    private void verifyThatTheFollowingPerspectiveWasAddedToThePerspectivesList(String uuid,
                                                                                String name) {
        verify( view ).addPerspective( uuid,
                                       name );
    }

    private void verifyThePopUpForTheConfigurationBecomesVisible() {
        verify( view ).openPopUp( (SaveCommand<IFramePerspectiveConfiguration>) saveCommandCaptor.capture() );
    }

    private void verifyThePopUpForTheConfigurationWasNotOpened() {
        verify( view,
                never() ).openPopUp( Matchers.<SaveCommand<IFramePerspectiveConfiguration>> any() );
    }

    private void userClicksAddNewPerspective() {
        presenter.onAddNewPerspective();
    }

    private IFramePerspectiveConfiguration getConfiguration(String uuid,
                                                            String name,
                                                            String url) {
        IFramePerspectiveConfiguration iFramePerspectiveConfiguration = new IFramePerspectiveConfiguration();
        iFramePerspectiveConfiguration.setUuid( uuid );
        iFramePerspectiveConfiguration.setName( name );
        iFramePerspectiveConfiguration.setUrl( url );
        return iFramePerspectiveConfiguration;
    }

    private void addPerspectiveToResult(IFramePerspectiveConfiguration iFramePerspectiveConfiguration) {
        getMockService().getResult().add( iFramePerspectiveConfiguration );
    }

    private ConfigurationServiceAsyncMock getMockService() {
        return ((ConfigurationServiceAsyncMock) configurationServiceAsync);
    }
}
