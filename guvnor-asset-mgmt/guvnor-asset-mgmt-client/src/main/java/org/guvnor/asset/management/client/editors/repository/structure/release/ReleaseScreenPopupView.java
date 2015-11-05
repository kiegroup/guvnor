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

import com.github.gwtbootstrap.client.ui.constants.ControlGroupType;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import org.uberfire.client.mvp.UberView;

public interface ReleaseScreenPopupView extends UberView<ReleaseScreenPopupPresenter> {

    

    interface Presenter {

    }

    void setUserName(String identifier);
    
    void setSourceBranch(String branch);

    void setRepository(String repositoryAlias);

    void setSourceBranchReadOnly(boolean b);

    void setRepositoryReadOnly(boolean b);

    void setServerURL(String replaceFirst);

    void setVersion(String suggestedVersion);

    void setUserNameEnabled(boolean b);

    void setPasswordEnabled(boolean b);

    void setServerURLEnabled(boolean b);

    void show();

    String getVersion();

    void setVersionStatus(ControlGroupType status);

    void setVersionHelpText(String helpText);

    String getSourceBranch();

    void setSourceBranchStatus(ControlGroupType status);

    void setSourceBranchHelpText(String helpText);

    boolean isDeployToRuntime();

    String getUserName();

    void setUserNameStatus(ControlGroupType status);

    void setUserNameTextHelp(String helpText);

    String getPassword();

    void setPasswordStatus(ControlGroupType status);

    void setPasswordHelpText(String helpText);

    String getServerURL();

    void setServerURLStatus(ControlGroupType status);

    void setServerURLHelpText(String helpText);

    void hide();
    
    void setDeployToRuntimeValueChangeHandler(ValueChangeHandler<Boolean> valueChangeHandler);

}
