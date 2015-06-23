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

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.Form.SubmitCompleteEvent;
import com.github.gwtbootstrap.client.ui.Form.SubmitEvent;
import com.github.gwtbootstrap.client.ui.event.HideEvent;
import com.github.gwtbootstrap.client.ui.event.HideHandler;
import org.guvnor.m2repo.client.event.M2RepoSearchEvent;
import org.jboss.errai.ioc.client.container.SyncBeanManager;

import static org.guvnor.m2repo.model.HTMLFileManagerFields.*;
import static org.guvnor.m2repo.utils.FileNameUtilities.*;

@Dependent
public class UploadFormPresenter implements UploadFormView.Presenter {

    private Event<M2RepoSearchEvent> searchEvent;

    private UploadFormView view;

    @Inject
    public UploadFormPresenter( final UploadFormView view,
                                final Event<M2RepoSearchEvent> searchEvent,
                                final SyncBeanManager iocManager ) {
        this.view = view;
        //When pop-up is closed destroy bean to avoid memory leak
        view.addHideHandler( new HideHandler() {
            @Override
            public void onHide( HideEvent hideEvent ) {
                iocManager.destroyBean( UploadFormPresenter.this );
            }
        } );
        this.searchEvent = searchEvent;
    }

    @PostConstruct
    public void init() {
        view.init( this );
    }

    @Override
    public void handleSubmitComplete( final SubmitCompleteEvent event ) {
        view.hideUploadingBusy();
        if ( UPLOAD_OK.equalsIgnoreCase( event.getResults() ) ) {
            view.showUploadedSuccessfullyMessage();
            view.hideGAVInputs();
            fireSearchEvent();
            view.hide();

        } else if ( UPLOAD_MISSING_POM.equalsIgnoreCase( event.getResults() ) ) {
            view.showInvalidJarNoPomWarning();
            view.showGAVInputs();

        } else if ( UPLOAD_UNABLE_TO_PARSE_POM.equalsIgnoreCase( event.getResults() ) ) {
            view.showInvalidPomWarning();
            view.hide();

        } else {
            view.showUploadFailedError( event.getResults() );
            view.hideGAVInputs();
            view.hide();
        }
    }

    @Override
    public void handleSubmit( final SubmitEvent event ) {
        String fileName = view.getFileName();
        if ( fileName == null || "".equals( fileName ) ) {
            view.showSelectFileUploadWarning();
            event.cancel();
        } else if ( !( isValid( fileName ) ) ) {
            view.showUnsupportedFileTypeWarning();
            event.cancel();
        } else {
            view.showUploadingBusy();
        }
    }

    public void showView() {
        view.show();
    }

    public void fireSearchEvent() {
        searchEvent.fire( new M2RepoSearchEvent() );
    }
}
