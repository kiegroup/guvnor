package org.drools.guvnor.client.modeldriven.ui;

/*
 * Copyright 2005 JBoss Inc
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
import java.util.List;

import org.drools.guvnor.client.common.DirtyableComposite;
import org.drools.guvnor.client.common.FormStylePopup;
import org.drools.guvnor.client.common.InfoPopup;
import org.drools.guvnor.client.common.SmallLabel;
import org.drools.guvnor.client.common.ValueChanged;
import org.drools.guvnor.client.messages.Constants;
import org.drools.ide.common.client.modeldriven.DropDownData;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.brl.FactPattern;
import org.drools.ide.common.client.modeldriven.brl.ISingleFieldConstraint;
import org.drools.ide.common.client.modeldriven.brl.RuleModel;
import org.drools.ide.common.client.modeldriven.brl.SingleFieldConstraint;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * This is an editor for constraint values.
 * How this behaves depends on the constraint value type.
 * When the constraint value has no type, it will allow the user to choose the first time.
 *
 * @author Michael Neale
 * @author Fernando Meyer
 */
public class ConstraintValueEditor extends DirtyableComposite {

    private final FactPattern pattern;
    private final String fieldName;
    private final SuggestionCompletionEngine sce;
    private final ISingleFieldConstraint constraint;
    private final Panel panel;
    private final RuleModel model;
    private final RuleModeller modeller;
    private final boolean numericValue;
    private DropDownData dropDownData;
    private Constants constants = ((Constants) GWT.create(Constants.class));
    private String fieldType;
    private boolean readOnly;

    public ConstraintValueEditor(FactPattern pattern, String fieldName,
    		ISingleFieldConstraint con, RuleModeller modeller, String valueType,
			boolean readOnly) {
        this.pattern = pattern;
        this.fieldName = fieldName;
        this.sce = modeller.getSuggestionCompletions();
        this.constraint = con;
        this.panel = new SimplePanel();
        this.model = modeller.getModel();
        this.modeller = modeller;

        valueType = sce.getFieldType(pattern.factType, fieldName);
        this.fieldType = valueType;
        if (SuggestionCompletionEngine.TYPE_NUMERIC.equals(valueType)) {
            this.numericValue = true;
        } else {
            this.numericValue = false;
        }
        
        this.readOnly = readOnly;
        if (SuggestionCompletionEngine.TYPE_BOOLEAN.equals(valueType)) {
            this.dropDownData = DropDownData.create(new String[]{"true", "false"}); //NON-NLS
        } else {
            this.dropDownData = sce.getEnums(pattern,
                    fieldName);
        }

        refreshEditor();
        initWidget(panel);

    }

	private void refreshEditor() {
        panel.clear();
        Widget constraintWidget = null;
        if (constraint.constraintValueType == SingleFieldConstraint.TYPE_UNDEFINED) {
            Image clickme = new Image("images/edit.gif"); //NON-NLS
            clickme.addClickListener(new ClickListener() {

                public void onClick(Widget w) {
                    showTypeChoice(w,
                            constraint);
                }
            });
            constraintWidget = clickme;
        } else {
            switch (constraint.constraintValueType) {
                case SingleFieldConstraint.TYPE_LITERAL:
                    if (this.dropDownData != null) {
                        constraintWidget = new EnumDropDownLabel(this.pattern,
                                this.fieldName,
                                this.sce,
                                this.constraint);
                    } else if (SuggestionCompletionEngine.TYPE_DATE.equals(this.fieldType)) {

                        DatePickerLabel datePicker = new DatePickerLabel(constraint.value);

                        // Set the default time
                        this.constraint.value = datePicker.getDateString();

                        if (!this.readOnly) {
                            datePicker.addValueChanged(new ValueChanged() {

                                public void valueChanged(String newValue) {
                                    constraint.value = newValue;
                                }
                            });

                            constraintWidget =  datePicker;
                        } else {
                        	constraintWidget = new SmallLabel(this.constraint.value);
                        }
                    } else {
                        if (!this.readOnly) {
                        	constraintWidget = new DefaultLiteralEditor(this.constraint,
                                    this.numericValue);
                        } else {
                        	constraintWidget = new SmallLabel(this.constraint.value);
                        }
                    }
                    break;
                case SingleFieldConstraint.TYPE_RET_VALUE:
                	constraintWidget = returnValueEditor();
                    break;
                case SingleFieldConstraint.TYPE_EXPR_BUILDER:
                	constraintWidget = expressionEditor();
                    break;
                case SingleFieldConstraint.TYPE_VARIABLE:
                	constraintWidget = variableEditor();
                    break;
                case ISingleFieldConstraint.TYPE_TEMPLATE:
                	constraintWidget = new DefaultLiteralEditor(this.constraint, false);
                    break;
                default:
                    break;
            }
        }
        panel.add(constraintWidget);
    }

    private Widget variableEditor() {

        if (this.readOnly){
            return new SmallLabel(this.constraint.value);
        }

        List vars = this.model.getBoundVariablesInScope(this.constraint);

        final ListBox box = new ListBox();

        if (this.constraint.value == null) {
            box.addItem(constants.Choose());
        }

        int j = 0;
        for (int i = 0; i < vars.size(); i++) {
            String var = (String) vars.get(i);
            FactPattern f = model.getBoundFact(var);
            String fv = model.getFieldConstraint(var);
            if ((f != null && f.factType.equals(this.fieldType)) || (fv != null && fv.equals(this.fieldType))) {
                box.addItem(var);
                if (this.constraint.value != null && this.constraint.value.equals(var)) {
                    box.setSelectedIndex(j);
                }
                j++;
            } else {
                // for collection, present the list of possible bound variable
                String factCollectionType = sce.getParametricFieldType(pattern.factType,
                        this.fieldName);
                if ((f != null && factCollectionType != null && f.factType.equals(factCollectionType)) || (factCollectionType != null && factCollectionType.equals(fv))) {
                    box.addItem(var);
                    if (this.constraint.value != null && this.constraint.value.equals(var)) {
                        box.setSelectedIndex(j);
                    }
                    j++;
                }
            }
        }

        box.addChangeListener(new ChangeListener() {

            public void onChange(Widget w) {
                constraint.value = box.getItemText(box.getSelectedIndex());
            }
        });

        return box;
    }

    /**
     * An editor for the retval "formula" (expression).
     */
    private Widget returnValueEditor() {
        TextBox box = new BoundTextBox(constraint);
        String msg = constants.FormulaEvaluateToAValue();
        Image img = new Image("images/function_assets.gif"); //NON-NLS
        img.setTitle(msg);
        box.setTitle(msg);
        Widget ed = widgets(img,
                box);
        return ed;
    }

    private Widget expressionEditor() {
        if (!(this.constraint instanceof SingleFieldConstraint)) {
            throw new IllegalArgumentException("Expected SingleFieldConstraint, but " + constraint.getClass().getName() + " found.");
        }
        ExpressionBuilder builder = new ExpressionBuilder(this.modeller, ((SingleFieldConstraint) this.constraint).getExpression());
        String msg = constants.ExpressionEditor();
        Widget ed = widgets(new HTML("&nbsp;"),
                builder);
        return ed;
    }

    /**
     * Show a list of possibilities for the value type.
     */
    private void showTypeChoice(Widget w,
            final ISingleFieldConstraint con) {
        final FormStylePopup form = new FormStylePopup("images/newex_wiz.gif",
                constants.FieldValue());

        Button lit = new Button(constants.LiteralValue());
        lit.addClickListener(new ClickListener() {

            public void onClick(Widget w) {
                con.constraintValueType = SingleFieldConstraint.TYPE_LITERAL;
                doTypeChosen(form);
            }
        });
        form.addAttribute(constants.LiteralValue() + ":",
                widgets(lit,
                new InfoPopup(constants.LiteralValue(),
                constants.LiteralValTip())));

		if (modeller.isTemplate()) {
	        String templateKeyLabel = constants.TemplateKey();
	        Button templateKeyButton = new Button(templateKeyLabel);
	        templateKeyButton.addClickListener(new ClickListener() {
	            public void onClick(Widget arg0) {
	                con.constraintValueType = ISingleFieldConstraint.TYPE_TEMPLATE;
	                doTypeChosen(form);
	            }
	        });
	
	        form.addAttribute(templateKeyLabel + ":",
	                widgets(templateKeyButton,
	                new InfoPopup(templateKeyLabel,
	                constants.LiteralValTip())));
        }
        
        form.addRow(new HTML("<hr/>"));
        form.addRow(new SmallLabel(constants.AdvancedOptions()));

        //only want to show variables if we have some !
        if (this.model.getBoundVariablesInScope(this.constraint).size() > 0 || SuggestionCompletionEngine.TYPE_COLLECTION.equals(this.fieldType)) {
            List vars = this.model.getBoundFacts();
            boolean foundABouncVariableThatMatches = false;
            for (int i = 0; i < vars.size(); i++) {
                String var = (String) vars.get(i);
                FactPattern f = model.getBoundFact(var);
                String fieldConstraint = model.getFieldConstraint(var);

                if ((f != null && f.factType.equals(this.fieldType)) || this.fieldType.equals(fieldConstraint)) {
                    foundABouncVariableThatMatches = true;
                    break;
                } else {
                    // for collection, present the list of possible bound variable
                    String factCollectionType = sce.getParametricFieldType(pattern.factType,
                            this.fieldName);
                    if ((f != null && factCollectionType != null && f.factType.equals(factCollectionType)) || (factCollectionType != null && factCollectionType.equals(fieldConstraint))) {
                        foundABouncVariableThatMatches = true;
                        break;
                    }
                }
            }
            if (foundABouncVariableThatMatches == true) {
                Button variable = new Button(constants.BoundVariable());
                variable.addClickListener(new ClickListener() {

                    public void onClick(Widget w) {
                        con.constraintValueType = SingleFieldConstraint.TYPE_VARIABLE;
                        doTypeChosen(form);
                    }
                });
                form.addAttribute(constants.AVariable(),
                        widgets(variable,
                        new InfoPopup(constants.ABoundVariable(),
                        constants.BoundVariableTip())));
            }
        }

        Button formula = new Button(constants.NewFormula());
        formula.addClickListener(new ClickListener() {

            public void onClick(Widget w) {
                con.constraintValueType = SingleFieldConstraint.TYPE_RET_VALUE;
                doTypeChosen(form);
            }
        });

        form.addAttribute(constants.AFormula() + ":",
                widgets(formula,
                new InfoPopup(constants.AFormula(),
                constants.FormulaExpressionTip())));

        Button expression = new Button(constants.ExpressionEditor());
        expression.addClickListener(new ClickListener() {

            public void onClick(Widget w) {
                con.constraintValueType = SingleFieldConstraint.TYPE_EXPR_BUILDER;
                doTypeChosen(form);
            }
        });

        form.addAttribute(constants.ExpressionEditor() + ":",
                widgets(expression,
                new InfoPopup(constants.ExpressionEditor(),
                constants.ExpressionEditor())));


        form.show();
    }

    private void doTypeChosen(final FormStylePopup form) {
        refreshEditor();
        form.hide();
    }

    private Panel widgets(Widget left,
            Widget right) {
        HorizontalPanel panel = new HorizontalPanel();
        panel.add(left);
        panel.add(right);
        panel.setWidth("100%");
        return panel;
    }

    public boolean isDirty() {
        return super.isDirty();
    }
}
