/*
 * Copyright 2012 JBoss Inc
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

package org.kie.guvnor.projecteditor.client.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import org.kie.guvnor.projecteditor.client.resources.i18n.ProjectEditorConstants;
import org.uberfire.client.common.ErrorPopup;

import javax.inject.Inject;

public class ListFormComboPanelViewImpl
        extends Composite
        implements ListFormComboPanelView {

    private Presenter presenter;

    @UiTemplate("ListFormComboPanelViewImpl.ui.xml")
    interface ListFormComboPanelViewImplBinder
            extends
            UiBinder<Widget, ListFormComboPanelViewImpl> {

    }

    private static ListFormComboPanelViewImplBinder uiBinder = GWT.create(ListFormComboPanelViewImplBinder.class);

    @UiField
    ListBox kSessionsList;

    @UiField
    SimplePanel kSessionForm;

    public ListFormComboPanelViewImpl() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void addItem(String fullName) {
        kSessionsList.addItem(fullName);
    }

    @Override
    public void remove(String fullName) {
        for (int i = 0; i < kSessionsList.getItemCount(); i++) {
            if (kSessionsList.getItemText(i).equals(fullName)) {
                kSessionsList.removeItem(i);
                break;
            }
        }
    }

    @Override
    public void clearList() {
        kSessionsList.clear();
    }

    @Override
    public void setForm(Form form) {
        kSessionForm.clear();
        kSessionForm.add(form);
    }

    @Override
    public void setSelected(String fullName) {
        for (int i = 0; i < kSessionsList.getItemCount(); i++) {
            if (kSessionsList.getItemText(i).equals(fullName)) {
                kSessionsList.setSelectedIndex(i);
                break;
            }
        }
    }

    @Override
    public void showPleaseSelectAnItem() {
        ErrorPopup.showMessage(ProjectEditorConstants.INSTANCE.PleaseSelectAKSession());
    }

    @UiHandler("kSessionsList")
    public void handleChange(ChangeEvent event) {
        presenter.onSelect(kSessionsList.getValue(kSessionsList.getSelectedIndex()));
    }

    @UiHandler("addButton")
    public void add(ClickEvent clickEvent) {
        presenter.onAdd();
    }

    @UiHandler("renameButton")
    public void rename(ClickEvent clickEvent) {
        presenter.onRename();
    }

    @UiHandler("deleteButton")
    public void delete(ClickEvent clickEvent) {
        presenter.onRemove();
    }
}
