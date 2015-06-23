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

import com.github.gwtbootstrap.client.ui.Form;
import com.github.gwtbootstrap.client.ui.base.HasVisibleHandlers;
import org.guvnor.m2repo.client.upload.UploadFormView.Presenter;
import org.uberfire.client.mvp.UberView;

public interface UploadFormView extends UberView<Presenter>,
                                        HasVisibleHandlers {

    interface Presenter {

        void handleSubmitComplete( Form.SubmitCompleteEvent event );

        void handleSubmit( Form.SubmitEvent event );
    }

    String getFileName();

    void showSelectFileUploadWarning();

    void showUnsupportedFileTypeWarning();

    void showUploadedSuccessfullyMessage();

    void showInvalidJarNoPomWarning();

    void showInvalidPomWarning();

    void showUploadFailedError( String message );

    void showGAVInputs();

    void hideGAVInputs();

    void showUploadingBusy();

    void hideUploadingBusy();

    void show();

    void hide();
}
