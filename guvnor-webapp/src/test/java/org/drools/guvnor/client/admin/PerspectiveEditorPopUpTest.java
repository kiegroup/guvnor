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

import org.drools.guvnor.client.rpc.IFramePerspectiveConfiguration;
import org.drools.guvnor.client.util.SaveCommand;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

import org.drools.guvnor.client.admin.PerspectiveEditorPopUpView.Presenter;

public class PerspectiveEditorPopUpTest {

    private PerspectiveEditorPopUp perspectiveEditorPopUp;
    private PerspectiveEditorPopUpView view;
    private Presenter presenter;
    private SaveCommandImpl saveCommand;

    @Before
    public void setUp() throws Exception {
        view = mock(PerspectiveEditorPopUpView.class);
        perspectiveEditorPopUp = new PerspectiveEditorPopUp(view);
        saveCommand = new SaveCommandImpl();
        presenter = getPresenter();
    }

    private Presenter getPresenter() {
        return perspectiveEditorPopUp;
    }

    @Test
    public void testPresenterSet() throws Exception {
        verify(view).setPresenter(presenter);
    }

    @Test
    public void testAddNew() throws Exception {
        userOpensAnEmptyPopUp();
        verifyThatPopUpOpened();

        verifyThatPopUpFieldsAreNotSet();

        userEditsFormFields("Manual", "http://drools.org/manual");
        userClicksSave();

        assertThatTheCorrectValuesWereSaved("Manual", "http://drools.org/manual");
    }

    @Test
    public void testModify() throws Exception {
        userOpensThePopUpToEditAnExistingConfiguration(getConfiguration("Manual", "http://drools.org/manual"));
        verifyThatPopUpOpened();
        verifyThatFollowingValuesWereSetToForm("Manual", "http://drools.org/manual");

        userEditsFormFields("Runtime", "http://drools.org/runtime");

        userClicksSave();

        assertThatTheCorrectValuesWereSaved("Runtime", "http://drools.org/runtime");
    }

    @Test
    public void testNameCanNotBeNullOrEmpty() throws Exception {
        userOpensAnEmptyPopUp();
        userClicksSave();

        verifyNameCanNotBeNullWarningWasShownToUser();
    }

    @Test
    public void testUrlCanNotBeNullOrEmpty() throws Exception {
        userOpensAnEmptyPopUp();
        userEditsFormFields("name", "");
        userClicksSave();

        verifyUrlCanNotBeNullWarningWasShownToUser();
    }

    @Test
    public void testCancel() throws Exception {
        userOpensAnEmptyPopUp();

        userEditsFormFields("name", "url");

        userClicksCancel();

        verifyFieldWereCleared();

        verifyThePopUpWasClosed();
    }

    private void verifyThePopUpWasClosed() {
        verify(view).hide();
    }

    private void verifyFieldWereCleared() {
        verify(view).setName("");
        verify(view).setUrl("");
    }

    private void userClicksCancel() {
        presenter.onCancel();
    }

    private void verifyUrlCanNotBeNullWarningWasShownToUser() {
        verify(view).showUrlCanNotBeEmptyWarning();
    }

    private void verifyNameCanNotBeNullWarningWasShownToUser() {
        verify(view).showNameCanNotBeEmptyWarning();
    }

    private void verifyThatPopUpFieldsAreNotSet() {
        verify(view, never()).setName(anyString());
        verify(view, never()).setUrl(anyString());
    }

    private void userOpensAnEmptyPopUp() {
        saveCommand = new SaveCommandImpl();
        perspectiveEditorPopUp.show(saveCommand);
    }

    private void assertThatTheCorrectValuesWereSaved(String name, String url) {
        IFramePerspectiveConfiguration saveConfiguration = saveCommand.getIFramePerspectiveConfiguration();
        assertEquals(name, saveConfiguration.getName());
        assertEquals(url, saveConfiguration.getUrl());
    }

    private void verifyThatFollowingValuesWereSetToForm(String name, String url) {
        verify(view).setName(name);
        verify(view).setUrl(url);
    }

    private void userOpensThePopUpToEditAnExistingConfiguration(IFramePerspectiveConfiguration iFramePerspectiveConfiguration) {
        perspectiveEditorPopUp.setConfiguration(iFramePerspectiveConfiguration);
        perspectiveEditorPopUp.show(saveCommand);
    }

    private void verifyThatPopUpOpened() {
        verify(view).show();
    }

    private void userClicksSave() {
        presenter.onSave();
    }

    private IFramePerspectiveConfiguration getConfiguration(String name, String url) {
        IFramePerspectiveConfiguration loadedConfiguration = new IFramePerspectiveConfiguration();
        loadedConfiguration.setName(name);
        loadedConfiguration.setUrl(url);
        return loadedConfiguration;
    }

    private void userEditsFormFields(String name, String url) {
        when(view.getName()).thenReturn(name);
        when(view.getUrl()).thenReturn(url);
    }

    private class SaveCommandImpl implements SaveCommand<IFramePerspectiveConfiguration> {

        private IFramePerspectiveConfiguration iFramePerspectiveConfiguration;

        public void save(IFramePerspectiveConfiguration iFramePerspectiveConfiguration) {
            this.iFramePerspectiveConfiguration = iFramePerspectiveConfiguration;
        }

        public IFramePerspectiveConfiguration getIFramePerspectiveConfiguration() {
            return iFramePerspectiveConfiguration;
        }
    }
}
