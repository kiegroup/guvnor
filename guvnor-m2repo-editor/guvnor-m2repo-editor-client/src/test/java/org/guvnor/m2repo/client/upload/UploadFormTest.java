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

package org.guvnor.m2repo.client.upload;

import static org.guvnor.m2repo.model.HTMLFileManagerFields.UPLOAD_MISSING_POM;
import static org.guvnor.m2repo.model.HTMLFileManagerFields.UPLOAD_OK;
import static org.guvnor.m2repo.model.HTMLFileManagerFields.UPLOAD_UNABLE_TO_PARSE_POM;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.enterprise.event.Event;

import com.github.gwtbootstrap.client.ui.Form.SubmitCompleteEvent;
import com.github.gwtbootstrap.client.ui.Form.SubmitEvent;
import com.github.gwtbootstrap.client.ui.event.HideEvent;
import com.github.gwtbootstrap.client.ui.event.HideHandler;
import org.guvnor.m2repo.client.event.M2RepoSearchEvent;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class UploadFormTest {

    @Mock
    private UploadFormView view;

    @Mock
    private Event<M2RepoSearchEvent> searchEvent;

    @Mock
    private SyncBeanManager iocManager;

    @Mock
    private SubmitCompleteEvent submitCompleteEvent;

    @Mock
    private HideEvent hideEvent;

    @Captor
    private ArgumentCaptor<HideHandler> captor;

    private UploadFormPresenter uploadFormPresenter;

    @Before
    public void before() {
        uploadFormPresenter = new UploadFormPresenter(view, searchEvent, iocManager);

        verify(view).addHideHandler(captor.capture());
    }

    @Test
    public void emptyFilenameTest() {
        when(view.getFileName()).thenReturn(null);
        uploadFormPresenter.handleSubmit(new SubmitEvent());

        verify(view).showSelectFileUploadWarning();
        verify(view, never()).showUploadingBusy();
    }

    @Test
    public void nullFilenameTest() {
        when(view.getFileName()).thenReturn("");
        uploadFormPresenter.handleSubmit(new SubmitEvent());

        verify(view).showSelectFileUploadWarning();
        verify(view, never()).showUploadingBusy();
    }

    @Test
    public void unsupportedFilenameTest() {
        when(view.getFileName()).thenReturn("//!#@%^&*()\\23\\(0");
        uploadFormPresenter.handleSubmit(new SubmitEvent());

        verify(view).showUnsupportedFileTypeWarning();
        verify(view, never()).showUploadingBusy();
    }

    @Test
    public void correctFilenameTest() {
        when(view.getFileName()).thenReturn("/home/user/something/pom.xml");
        uploadFormPresenter.handleSubmit(new SubmitEvent());

        verify(view).showUploadingBusy();
    }

    @Test
    public void uploadOkSubmitHandlerTest() {
        when(submitCompleteEvent.getResults()).thenReturn(UPLOAD_OK);
        uploadFormPresenter.handleSubmitComplete(submitCompleteEvent);

        verify(view).hideUploadingBusy();
        verify(view).hideUploadingBusy();
        verify(view).hideGAVInputs();

        verify(view).showUploadedSuccessfullyMessage();
    }

    @Test
    public void uploadMissingPomSubmitHandlerTest() {
        when(submitCompleteEvent.getResults()).thenReturn(UPLOAD_MISSING_POM);
        uploadFormPresenter.handleSubmitComplete(submitCompleteEvent);

        verify(view).hideUploadingBusy();
        verify(view).showGAVInputs();

        verify(view).showInvalidJarNoPomWarning();
    }

    @Test
    public void uploadUnableToParsePomSubmitHandlerTest() {
        when(submitCompleteEvent.getResults()).thenReturn(UPLOAD_UNABLE_TO_PARSE_POM);
        uploadFormPresenter.handleSubmitComplete(submitCompleteEvent);

        verify(view).hideUploadingBusy();
        verify(view).hide();

        verify(view).showInvalidPomWarning();
    }

    @Test
    public void uploadUnknownErrorTest() {
        String errorText = "Some unknown error text.";

        when(submitCompleteEvent.getResults()).thenReturn(errorText);
        uploadFormPresenter.handleSubmitComplete(submitCompleteEvent);

        verify(view).hideUploadingBusy();
        view.hideGAVInputs();
        view.hide();

        view.showUploadFailedError(errorText);
    }

    @Test
    public void isViewDestroyedDuringHidingTest() {
        HideHandler hideHandler = captor.getValue();

        hideHandler.onHide(hideEvent);

        verify(iocManager).destroyBean(uploadFormPresenter);
    }
}