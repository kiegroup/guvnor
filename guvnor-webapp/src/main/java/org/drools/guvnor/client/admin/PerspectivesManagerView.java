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

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.ui.IsWidget;
import org.drools.guvnor.client.rpc.IFramePerspectiveConfiguration;
import org.drools.guvnor.client.util.SaveCommand;

import java.util.Collection;

public interface PerspectivesManagerView extends IsWidget{

    interface Presenter {

        void onAddNewPerspective();

        void onEditPerspective() throws SerializationException;

        void onRemovePerspective();

    }

    void setPresenter(Presenter presenter);

    String getSelectedPerspectiveUuid();

    void addPerspective(String uuid, String name);

    void openPopUp(SaveCommand<IFramePerspectiveConfiguration> saveCommand);

    void openPopUp(SaveCommand<IFramePerspectiveConfiguration> capture, IFramePerspectiveConfiguration iFramePerspectiveConfiguration);

    void closePopUp();

    void removePerspective(String uuid);

    Collection<String> getListOfPerspectiveNames();

    void showNameTakenError(String name);

    void showNoSelectedPerspectiveError();
}
