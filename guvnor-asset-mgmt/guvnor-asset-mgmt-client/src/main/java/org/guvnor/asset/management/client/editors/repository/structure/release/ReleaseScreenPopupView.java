/*
 * Copyright 2015 JBoss Inc
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
package org.guvnor.asset.management.client.editors.repository.structure.release;

import com.github.gwtbootstrap.client.ui.base.HasVisibility;
import org.uberfire.client.mvp.UberView;

public interface ReleaseScreenPopupView extends UberView<ReleaseScreenPopupView.Presenter>, HasVisibility {

    interface Presenter {

        void onSubmit();

        void onCancel();

        void onDeployToRuntimeStateChanged( boolean checked );
    }

    void setUserName( String identifier );

    void setSourceBranch( String branch );

    void setRepository( String repositoryAlias );

    void setSourceBranchReadOnly( boolean b );

    void setRepositoryReadOnly( boolean b );

    void setServerURL( String serverUrl );

    void setVersion( String suggestedVersion );

    void setDeployToRuntime( boolean b );

    void setUserNameEnabled( boolean b );

    void setPasswordEnabled( boolean b );

    void setServerURLEnabled( boolean b );

    void clearWidgetsState();

    String getVersion();

    void showErrorVersionEmpty();

    void showErrorVersionSnapshot();

    void showCurrentVersionHelpText( String currentRepositoryVersion );

    String getSourceBranch();

    void showErrorSourceBranchNotRelease();

    boolean isDeployToRuntime();

    String getUserName();

    void showErrorUserNameEmpty();

    String getPassword();

    void showErrorPasswordEmpty();

    String getServerURL();

    void showErrorServerUrlEmpty();

}
