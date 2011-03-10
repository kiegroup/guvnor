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

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.ui.*;
import org.drools.guvnor.client.common.ErrorPopup;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.rpc.IFramePerspectiveConfiguration;
import org.drools.guvnor.client.util.SaveCommand;

import java.util.ArrayList;
import java.util.Collection;

public class PerspectivesManagerViewImpl extends Composite implements PerspectivesManagerView {

    private Constants constants = GWT.create( Constants.class );

    interface PerspectivesManagerViewImplBinder
            extends
            UiBinder<Widget, PerspectivesManagerViewImpl> {
    }

    private static PerspectivesManagerViewImplBinder uiBinder = GWT.create(PerspectivesManagerViewImplBinder.class);

    private Presenter presenter;

    @UiField
    ListBox perspectivesList;

    @UiField
    Button newPerspective;

    @UiField
    Button editPerspective;

    @UiField
    Button removePerspective;

    private PerspectiveEditorPopUp perspectiveEditorPopUp;

    public PerspectivesManagerViewImpl() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    public String getSelectedPerspectiveUuid() {
        int selectedIndex = perspectivesList.getSelectedIndex();
        if (selectedIndex < 0) {
            return null;
        } else {
            return perspectivesList.getValue(selectedIndex);
        }
    }

    public void addPerspective(String uuid, String name) {
        perspectivesList.addItem(name, uuid);
    }

    public void openPopUp(SaveCommand<IFramePerspectiveConfiguration> saveCommand) {
        perspectiveEditorPopUp = new PerspectiveEditorPopUp(new PerspectiveEditorPopUpViewImpl());
        perspectiveEditorPopUp.show(saveCommand);
    }

    public void openPopUp(SaveCommand<IFramePerspectiveConfiguration> saveCommand, IFramePerspectiveConfiguration iFramePerspectiveConfiguration) {
        perspectiveEditorPopUp = new PerspectiveEditorPopUp(new PerspectiveEditorPopUpViewImpl());
        perspectiveEditorPopUp.setConfiguration(iFramePerspectiveConfiguration);
        perspectiveEditorPopUp.show(saveCommand);
    }

    public void closePopUp() {
        perspectiveEditorPopUp.hide();
    }

    public void removePerspective(String uuid) {
        for (int i = 0; i < perspectivesList.getItemCount(); i++) {
            if (perspectivesList.getValue(i).equals(uuid)) {
                perspectivesList.removeItem(i);
                break;
            }
        }
    }

    public Collection<String> getListOfPerspectiveNames() {
        Collection<String> result = new ArrayList<String>();
        for (int i = 0; i < perspectivesList.getItemCount(); i++) {
            result.add(perspectivesList.getItemText(i));
        }
        return result;
    }

    public void showNameTakenError(String name) {
        ErrorPopup.showMessage(constants.NameTakenForModel(name));
    }

    public void showNoSelectedPerspectiveError() {
        ErrorPopup.showMessage(constants.PleaseSelectAPerspective());
    }

    @UiHandler("newPerspective")
    public void addNewPerspective(ClickEvent event) {
        presenter.onAddNewPerspective();
    }

    @UiHandler("editPerspective")
    public void editPerspective(ClickEvent event) {
        try {
            presenter.onEditPerspective();
        } catch (SerializationException e) {
            ErrorPopup.showMessage(constants.FailedToLoadPerspective());
        }
    }

    @UiHandler("removePerspective")
    public void removePerspective(ClickEvent event) {
        presenter.onRemovePerspective();
    }
}
