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

package org.drools.guvnor.client.explorer.navigation.qa.testscenarios;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.drools.guvnor.client.resources.DroolsGuvnorImages;
import org.drools.ide.common.client.modeldriven.testing.FactAssignmentField;
import org.drools.ide.common.client.modeldriven.testing.Field;
import org.drools.ide.common.client.modeldriven.testing.FieldData;

public class FieldSelectorWidget
        implements IsWidget, ClickHandler {

    private final Field field;
    private final FieldConstraintHelper helper;
    private final ScenarioParentWidget parent;
    private final Image clickMe;


    public FieldSelectorWidget(Field field,
                               FieldConstraintHelper helper,
                               ScenarioParentWidget parent) {
        this.field = field;
        this.helper = helper;
        this.parent = parent;
        this.clickMe = new Image(DroolsGuvnorImages.INSTANCE.edit());
        this.clickMe.addClickHandler(this);
    }

    @Override
    public Widget asWidget() {
        return clickMe;
    }

    @Override
    public void onClick(ClickEvent event) {
        TypeChoiceFormPopup typeChoiceForm = new TypeChoiceFormPopup(helper);
        typeChoiceForm.addSelectionHandler(new SelectionHandler<Integer>() {

            @Override
            public void onSelection(SelectionEvent<Integer> selectionEvent) {
                helper.replaceFieldWith(createField(selectionEvent));

                parent.renderEditor();
            }
        });

        typeChoiceForm.show();
    }

    private void createField(SelectionEvent<Integer> selectionEvent) {
        if (selectionEvent.getSelectedItem() == FieldData.TYPE_FACT) {
            helper.replaceFieldWith(new FactAssignmentField(field.getName(), helper.getFieldType()));
        } else {
            FieldData fieldData = new FieldData(field.getName(), "");
            helper.replaceFieldWith(fieldData);
            applyFieldDataNature(
                    fieldData,
                    selectionEvent.getSelectedItem());
        }
    }

    private void applyFieldDataNature(FieldData fieldData, Integer fieldDataType) {
        if (fieldDataType == FieldData.TYPE_COLLECTION) {
            fieldData.setNature(
                    FieldData.TYPE_COLLECTION,
                    helper.getParametricFieldType());
        } else {
            fieldData.setNature(fieldDataType);
        }
    }
}
