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

package org.kie.guvnor.configresource.client.widget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

public class ImportsWidgetViewImpl
        extends Composite
        implements ImportsWidgetView {

    interface Binder
            extends UiBinder<Widget, ImportsWidgetViewImpl> {
    }

    private static Binder uiBinder = GWT.create(Binder.class);

    @UiField
    ListBox importsList;

    private Presenter presenter;

    public ImportsWidgetViewImpl() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void showPleaseSelectAnImport() {
        //TODO -Rikkola-
    }

    @Override
    public void addImport(String type) {
        importsList.addItem(type);
    }

    @Override
    public String getSelected() {
        return importsList.getValue(importsList.getSelectedIndex());
    }

    @Override
    public void removeImport(String selected) {
        for (int i = 0; i < importsList.getItemCount(); i++) {
            if (importsList.getValue(i).equals(selected)) {
                importsList.removeItem(i);
                break;
            }
        }
    }

    @Override
    public void setupReadOnly() {
        importsList.setEnabled(false);
    }

    @UiHandler("newImport")
    public void onAddNew(ClickEvent clickEvent) {
        presenter.onAddImport();
    }

    @UiHandler("removeImport")
    public void onRemoveImport(ClickEvent clickEvent) {
        presenter.onRemoveImport();
    }

}
