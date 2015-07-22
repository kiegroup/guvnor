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

import com.github.gwtbootstrap.client.ui.constants.ControlGroupType;

public interface CloneRepositoryView {

    interface Presenter {

        void handleCancelClick();

        void handleCloneClick();
    }

    void init(Presenter presenter, boolean isOuMandatory);

    void hide();

    void show();

    void addOrganizationalUnitSelectEntry();

    void addOrganizationalUnit(String item, String value);

    String getOrganizationalUnit(int index);

    int getSelectedOrganizationalUnit();

    boolean isGitUrlEmpty();

    boolean isNameEmpty();

    String getGitUrl();

    String getUsername();

    String getPassword();

    String getName();

    void setName(String name);

    void showUrlHelpManatoryMessage();

    void showUrlHelpInvalidFormatMessage();

    void setUrlGroupType(ControlGroupType type);

    void showNameHelpMandatoryMessage();

    void setNameGroupType(ControlGroupType type);

    void showOrganizationalUnitHelpMandatoryMessage();

    void setOrganizationalUnitGroupType(ControlGroupType type);

    void setNameEnabled(boolean enabled);

    void setOrganizationalUnitEnabled(boolean enabled);

    void setGitUrlEnabled(boolean enabled);

    void setUsernameEnabled(boolean enabled);

    void setPasswordEnabled(boolean enabled);

    void setCloneEnabled(boolean enabled);

    void setCancelEnabled(boolean enabled);

    void setPopupCloseVisible(boolean closeVisible);

    void showBusyPopupMessage();

    void closeBusyPopup();

    boolean showAgreeNormalizeNameWindow(String normalizedName);

    void alertRepositoryCloned();

    void errorRepositoryAlreadyExist();

    void errorCloneRepositoryFail(Throwable cause);

    void errorLoadOrganizationalUnitsFail(Throwable cause);
}
