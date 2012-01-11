/*
 * Copyright 2010 JBoss Inc
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

package org.drools.guvnor.client.asseteditor.drools;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;

import org.drools.guvnor.client.common.SmallLabel;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.moduleeditor.drools.SuggestionCompletionCache;
import org.drools.guvnor.client.rpc.Asset;
import org.drools.guvnor.client.rpc.WorkingSetConfigData;
import org.drools.ide.common.client.factconstraints.ConstraintConfiguration;
import org.drools.ide.common.client.factconstraints.customform.CustomFormConfiguration;
import org.drools.ide.common.client.factconstraints.helper.CustomFormsContainer;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CustomFormsEditorPanel extends Composite {
    private ListBox factsCombo = new ListBox(false);
    private ListBox fieldsCombo = new ListBox(false);
    private TextBox customFormURL = new TextBox();
    private TextBox customFormWidth = new TextBox();
    private TextBox customFormHeight = new TextBox();
    private boolean validFactsChanged = true;
    private Map<String, ConstraintConfiguration> contraintsMap = new HashMap<String, ConstraintConfiguration>();
    private final Asset workingSet;
    private final WorkingSetEditor workingSetEditor;

    public CustomFormsEditorPanel(final WorkingSetEditor workingSetEditor) {

        this.workingSetEditor = workingSetEditor;

        this.workingSet = workingSetEditor.getWorkingSet();

        factsCombo.setVisibleItemCount(1);
        fieldsCombo.setVisibleItemCount(1);
        customFormURL.setWidth("400px");
        customFormURL.setTitle("Leave it blank if you want to remove the Custom Form URL"); //TODO: I18N
        customFormHeight.setWidth("50px");
        customFormWidth.setWidth("50px");

        factsCombo.addChangeHandler(new ChangeHandler() {
            public void onChange(ChangeEvent event) {
                fillSelectedFactFields();
            }
        });

        fieldsCombo.addChangeHandler(new ChangeHandler() {
            public void onChange(ChangeEvent event) {
                fillFieldConstrains();
            }
        });

        final FlexTable table = new FlexTable();

        VerticalPanel vp = new VerticalPanel();
        Constants constants = GWT.create(Constants.class);
        vp.add(new SmallLabel(constants.FactTypes()));
        vp.add(factsCombo);
        table.setWidget(0,
                0,
                vp);

        vp = new VerticalPanel();
        vp.add(new SmallLabel(constants.Field()));
        vp.add(fieldsCombo);
        table.setWidget(1,
                0,
                vp);

        vp = new VerticalPanel();
        vp.add(new SmallLabel("Custom Form URL:")); //TODO i18n

        Button btnUpdateURL = new Button(constants.OK(),
                new ClickHandler() {
                    public void onClick(ClickEvent event) {

                        int w;
                        int h;

                        try {
                            w = Integer.parseInt(customFormWidth.getText());
                            h = Integer.parseInt(customFormHeight.getText());
                        } catch (NumberFormatException ex) {
                            Window.alert("Width and Height must be integer values!"); //TODO: I18N
                            return;
                        }

                        if (((WorkingSetConfigData) workingSet.getContent()).customForms == null) {
                            ((WorkingSetConfigData) workingSet.getContent()).customForms = new ArrayList<CustomFormConfiguration>();
                        }

                        String factType = factsCombo.getItemText(factsCombo.getSelectedIndex());
                        String fieldName = fieldsCombo.getItemText(fieldsCombo.getSelectedIndex());

                        CustomFormConfiguration newCustomFormConfiguration = CustomFormsContainer.getEmptyCustomFormConfiguration();
                        newCustomFormConfiguration.setFactType(factType);
                        newCustomFormConfiguration.setFieldName(fieldName);
                        newCustomFormConfiguration.setCustomFormURL(customFormURL.getText());
                        newCustomFormConfiguration.setCustomFormWidth(w);
                        newCustomFormConfiguration.setCustomFormHeight(h);

                        workingSetEditor.getCustomFormsContainer().putCustomForm(newCustomFormConfiguration);
                        ((WorkingSetConfigData) workingSet.getContent()).customForms = workingSetEditor.getCustomFormsContainer().getCustomForms();
                    }
                });

        vp.add(customFormURL);
        vp.add(new SmallLabel("Width:"));
        vp.add(customFormWidth);
        vp.add(new SmallLabel("Height:"));
        vp.add(customFormHeight);
        table.setWidget(2,
                0,
                vp);

        table.setWidget(3,
                0,
                btnUpdateURL);

        fillSelectedFacts();
        fillSelectedFactFields();
        fillFieldConstrains();

        this.initWidget(table);
    }

    protected final void fillSelectedFacts() {
        if (validFactsChanged) {
            String s = factsCombo.getSelectedIndex() != -1 ? factsCombo.getItemText(factsCombo.getSelectedIndex()) : "";
            factsCombo.clear();
            validFactsChanged = false;
            for (int i = 0; i < workingSetEditor.getValidFactsListBox().getItemCount(); i++) {
                String itemText = workingSetEditor.getValidFactsListBox().getItemText(i);
                factsCombo.addItem(itemText);
                if (s.equals(itemText)) {
                    factsCombo.setSelectedIndex(i);
                }
            }
            if (factsCombo.getSelectedIndex() == -1 && factsCombo.getItemCount() > 0) {
                factsCombo.setSelectedIndex(0);
            }
            fillSelectedFactFields();
        }
    }

    private void fillSelectedFactFields() {
        if (factsCombo.getSelectedIndex() != -1) {
            String fact = factsCombo.getItemText(factsCombo.getSelectedIndex());
            fieldsCombo.clear();
            for (String field : getCompletionEngine().getFieldCompletions(fact)) {
                fieldsCombo.addItem(field);
            }
        }
        if (fieldsCombo.getSelectedIndex() == -1 && fieldsCombo.getItemCount() > 0) {
            fieldsCombo.setSelectedIndex(0);
        }
        fillFieldConstrains();
    }

    private void fillFieldConstrains() {
        if (fieldsCombo.getSelectedIndex() > 0 && factsCombo.getSelectedIndex() > 0) {
            String fieldName = fieldsCombo.getItemText(fieldsCombo.getSelectedIndex());
            String factField = factsCombo.getItemText(factsCombo.getSelectedIndex());
            contraintsMap.clear();

            if (this.workingSetEditor.getCustomFormsContainer().containsCustomFormFor(factField,
                    fieldName)) {
                CustomFormConfiguration customForm = this.workingSetEditor.getCustomFormsContainer().getCustomForm(factField,
                        fieldName);
                this.customFormURL.setText(customForm.getCustomFormURL());
                this.customFormWidth.setText(String.valueOf(customForm.getCustomFormWidth()));
                this.customFormHeight.setText(String.valueOf(customForm.getCustomFormHeight()));
            } else {
                this.customFormURL.setText("");
                this.customFormWidth.setText("");
                this.customFormHeight.setText("");
            }
        }
    }

    private SuggestionCompletionEngine getCompletionEngine() {
        return SuggestionCompletionCache.getInstance().getEngineFromCache(workingSet.getMetaData().getModuleName());
    }

    public void notifyValidFactsChanged() {
        this.validFactsChanged = true;
    }
}
