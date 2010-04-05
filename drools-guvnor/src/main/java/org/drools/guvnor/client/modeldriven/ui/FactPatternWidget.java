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

import java.util.ArrayList;
import java.util.List;

import org.drools.guvnor.client.common.ClickableLabel;
import org.drools.guvnor.client.common.DirtyableFlexTable;
import org.drools.guvnor.client.common.ImageButton;
import org.drools.guvnor.client.common.SmallLabel;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.modeldriven.HumanReadable;
import org.drools.guvnor.client.modeldriven.SuggestionCompletionEngine;
import org.drools.guvnor.client.modeldriven.brl.CompositeFieldConstraint;
import org.drools.guvnor.client.modeldriven.brl.FactPattern;
import org.drools.guvnor.client.modeldriven.brl.FieldConstraint;
import org.drools.guvnor.client.modeldriven.brl.IPattern;
import org.drools.guvnor.client.modeldriven.brl.SingleFieldConstraint;
import org.drools.guvnor.client.modeldriven.ui.factPattern.Connectives;
import org.drools.guvnor.client.modeldriven.ui.factPattern.PopupCreator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.gwtext.client.util.Format;

/**
 * This is the new smart widget that works off the model.
 * @author Michael Neale
 *
 */
public class FactPatternWidget extends RuleModellerWidget {

    private FactPattern pattern;
    private SuggestionCompletionEngine completions;
    private DirtyableFlexTable layout = new DirtyableFlexTable();
    private Connectives connectives;
    private PopupCreator popupCreator;
    private boolean bindable;
    private Constants constants = ((Constants) GWT.create(Constants.class));
    private String customLabel;
    private boolean readOnly;

    public FactPatternWidget(RuleModeller mod, IPattern p,
            boolean canBind) {
        this(mod, p, null, canBind,null);
    }

    public FactPatternWidget(RuleModeller mod, IPattern p,
            boolean canBind, Boolean readOnly) {
        this(mod, p, null, canBind, readOnly);
    }

     public FactPatternWidget(RuleModeller mod, IPattern p,
            String customLabel, boolean canBind) {
         this(mod, p, null, canBind, null);
     }

    

    /**
     * Creates a new FactPatternWidget
     * @param mod
     * @param p
     * @param customLabel
     * @param canBind
     * @param readOnly if the widget should be in RO mode. If this parameter
     * is null, the readOnly attribute is calculated.
     */
    public FactPatternWidget(RuleModeller mod, IPattern p,
            String customLabel, boolean canBind, Boolean readOnly) {
        super(mod);
        this.pattern = (FactPattern) p;
        this.completions = mod.getSuggestionCompletions();
        this.bindable = canBind;

        this.connectives = new Connectives();
        this.connectives.setCompletions(completions);
        this.connectives.setModeller(mod);
        this.connectives.setPattern(pattern);

        this.popupCreator = new PopupCreator();
        this.popupCreator.setBindable(bindable);
        this.popupCreator.setCompletions(completions);
        this.popupCreator.setModeller(mod);
        this.popupCreator.setPattern(pattern);

        this.customLabel = customLabel;

        //if readOnly == null, the RO attribute is calculated.
        if (readOnly == null){
            this.readOnly = !completions.containsFactType(this.pattern.factType);
        }else{
            this.readOnly = readOnly;
        }

        layout.setWidget(0, 0, getPatternLabel());
        FlexCellFormatter formatter = layout.getFlexCellFormatter();
        formatter.setAlignment(0, 0, HasHorizontalAlignment.ALIGN_LEFT, HasVerticalAlignment.ALIGN_BOTTOM);
        formatter.setStyleName(0, 0, "modeller-fact-TypeHeader");

        ArrayList sortedConst = sortConstraints(pattern.getFieldConstraints());
        pattern.setFieldConstraints(sortedConst);
        drawConstraints(sortedConst);

        

        if (this.readOnly){
            layout.addStyleName("editor-disabled-widget");
        }

        if (bindable) {
            layout.addStyleName("modeller-fact-pattern-Widget");
        }
        initWidget(layout);

    }

    /**
     * Render a hierarchy of constraints, hierarchy here means constraints that may
     * themselves depend on members of constraint objects. With this code, the GUI
     * enables clicking rules of the form:
     *
     *     $result = RoutingResult( NerOption.types contains "arzt" )
     *
     * @param sortedConst a sorted list of constraints to display.
     * */
    private void drawConstraints(ArrayList sortedConst) {
        final DirtyableFlexTable table = new DirtyableFlexTable();
        layout.setWidget(1, 0, table);
        List parents = new ArrayList();

        for (int i = 0; i < sortedConst.size(); i++) {
            int tabs = -1;
            FieldConstraint current = (FieldConstraint) sortedConst.get(i);
            if (current instanceof SingleFieldConstraint) {
                SingleFieldConstraint single = (SingleFieldConstraint) current;
                FieldConstraint parent = single.parent;

                for (int j = 0; j < parents.size(); j++) {
                    FieldConstraint storedParent = (FieldConstraint) parents.get(j);
                    if (storedParent != null && storedParent.equals(parent)) {
                        tabs = j + 1;
                        for (int k = j + 1; k < parents.size(); k++) {
                            parents.remove(j + 1);
                        }
                        parents.add(current);
                        break;
                    }
                }

                if (tabs < 0) {
                    tabs = 0;
                    parents.add(current);
                }
            }
            renderFieldConstraint(table, i, current, true, tabs);

            //now the clear icon
            final int currentRow = i;
            Image clear = new ImageButton("images/delete_faded.gif");//NON-NLS
            clear.setTitle(constants.RemoveThisWholeRestriction());
            clear.addClickListener(new ClickListener() {

                public void onClick(Widget w) {
                    if (Window.confirm(constants.RemoveThisItem())) {
                        pattern.removeConstraint(currentRow);
                        getModeller().refreshWidget();
                    }
                }
            });

            if (!this.readOnly) {
                table.setWidget(currentRow, 5, clear);
            }

        }
    }

    /**
     * Sort the rule constraints such that parent rules are inserted directly before
     * their child rules.
     * @param constraints the list of inheriting constraints to sort.
     * @return a sorted list of constraints ready for display.
     * */
    private ArrayList sortConstraints(FieldConstraint[] constraints) {
        ArrayList sortedConst = new ArrayList(constraints.length);
        for (int i = 0; i < constraints.length; i++) {
            FieldConstraint current = constraints[i];
            if (current instanceof SingleFieldConstraint) {
                SingleFieldConstraint single = (SingleFieldConstraint) current;
                int index = sortedConst.indexOf(single.parent);
                if (single.parent == null) {
                    sortedConst.add(single);
                } else if (index >= 0) {
                    sortedConst.add(index + 1, single);
                } else {
                    insertSingleFieldConstraint(single, sortedConst);
                }
            } else {
                sortedConst.add(current);
            }
        }
        return sortedConst;
    }

    /**
     * Recursively add constraints and their parents.
     * @param sortedConst the array to fill.
     * @param fieldConst the constraint to investigate.
     * */
    private void insertSingleFieldConstraint(SingleFieldConstraint fieldConst, ArrayList sortedConst) {
        if (fieldConst.parent instanceof SingleFieldConstraint) {
            insertSingleFieldConstraint((SingleFieldConstraint) fieldConst.parent, sortedConst);
        }
        sortedConst.add(fieldConst);
    }

    /**
     * This will render a field constraint into the given table.
     * The row is the row number to stick it into.
     */
    private void renderFieldConstraint(final DirtyableFlexTable inner, int row, FieldConstraint constraint, boolean showBinding, int tabs) {
        //if nesting, or predicate, then it will need to span 5 cols.
        if (constraint instanceof SingleFieldConstraint) {
            renderSingleFieldConstraint(this.getModeller(), inner, row, (SingleFieldConstraint) constraint, showBinding, tabs);
        } else if (constraint instanceof CompositeFieldConstraint) {
            inner.setWidget(row, 1, compositeFieldConstraintEditor((CompositeFieldConstraint) constraint));
            inner.getFlexCellFormatter().setColSpan(row, 1, 5);
            inner.setWidget(row, 0, new HTML("&nbsp;&nbsp;&nbsp;&nbsp;")); //NON-NLS
        }
    }

    /**
     * This will show the constraint editor - allowing field constraints to be nested etc.
     */
    private Widget compositeFieldConstraintEditor(final CompositeFieldConstraint constraint) {
        FlexTable t = new FlexTable();
        String desc = null;


        ClickListener click = new ClickListener() {

            public void onClick(Widget w) {
                popupCreator.showPatternPopupForComposite(w, constraint);
            }
        };

        if (constraint.compositeJunctionType.equals(CompositeFieldConstraint.COMPOSITE_TYPE_AND)) {
            desc = constants.AllOf() + ":";
        } else {
            desc = constants.AnyOf() + ":";
        }

        t.setWidget(0, 0, new ClickableLabel(desc, click, !this.readOnly));
        t.getFlexCellFormatter().setColSpan(0, 0, 2);
        //t.getFlexCellFormatter().setWidth(0, 0, "15%");

        FieldConstraint[] nested = constraint.constraints;
        DirtyableFlexTable inner = new DirtyableFlexTable();
        inner.setStyleName("modeller-inner-nested-Constraints"); //NON-NLS
        if (nested != null) {
            for (int i = 0; i < nested.length; i++) {
                this.renderFieldConstraint(inner, i, nested[i], false, 0);
                //add in remove icon here...
                final int currentRow = i;
                Image clear = new ImageButton("images/delete_faded.gif"); //NON-NLS
                clear.setTitle(constants.RemoveThisNestedRestriction());

                clear.addClickListener(new ClickListener() {

                    public void onClick(Widget w) {
                        if (Window.confirm(constants.RemoveThisItemFromNestedConstraint())) {
                            constraint.removeConstraint(currentRow);
                            getModeller().refreshWidget();
                        }
                    }
                });
                if (!this.readOnly) {
                    inner.setWidget(i, 5, clear);
                }
            }
        }

        t.setWidget(1, 1, inner);
        t.setWidget(1, 0, new HTML("&nbsp;&nbsp;&nbsp;&nbsp;"));
        return t;
    }

    /**
     * Applies a single field constraint to the given table, and start row.
     */
    private void renderSingleFieldConstraint(final RuleModeller modeller,
            final DirtyableFlexTable inner, int row, final SingleFieldConstraint constraint,
            boolean showBinding, int tabs) {

        int col = 1; //for offsetting, just a slight indent


        inner.setWidget(row, 0, new HTML("&nbsp;&nbsp;&nbsp;&nbsp;"));
        //inner.getFlexCellFormatter().setWidth(row, 0, "15%");
        //DOCNHERON
        if (constraint.constraintValueType != SingleFieldConstraint.TYPE_PREDICATE) {

            inner.setWidget(row, 0 + col, fieldLabel(constraint, showBinding, tabs * 20));
            inner.setWidget(row, 1 + col, operatorDropDown(constraint));
            inner.setWidget(row, 2 + col, valueEditor(constraint, constraint.fieldType));
            inner.setWidget(row, 3 + col, connectives.connectives(constraint, constraint.fieldType));
            Image addConnective = new ImageButton("images/add_connective.gif"); //NON-NLS
            addConnective.setTitle(constants.AddMoreOptionsToThisFieldsValues());
            addConnective.addClickListener(new ClickListener() {

                public void onClick(Widget w) {
                    constraint.addNewConnective();
                    modeller.refreshWidget();
                }
            });

            if (!this.readOnly) {
                inner.setWidget(row, 4 + col, addConnective);
            }
        } else if (constraint.constraintValueType == SingleFieldConstraint.TYPE_PREDICATE) {
            inner.setWidget(row, 0 + col, predicateEditor(constraint));
            inner.getFlexCellFormatter().setColSpan(row, 0, 5);
        }
    }

    /**
     * This provides an inline formula editor, not unlike a spreadsheet does.
     */
    private Widget predicateEditor(final SingleFieldConstraint c) {

        HorizontalPanel pred = new HorizontalPanel();
        pred.setWidth("100%");
        Image img = new Image("images/function_assets.gif"); //NON-NLS
        img.setTitle(constants.FormulaBooleanTip());

        pred.add(img);
        if (c.value == null) {
            c.value = "";
        }

        final TextBox box = new TextBox();
        box.setText(c.value);

        if (!this.readOnly) {
            box.addChangeListener(new ChangeListener() {

                public void onChange(Widget w) {
                    c.value = box.getText();
                    getModeller().makeDirty();
                }
            });
            box.setWidth("100%");
            pred.add(box);
        } else {
            pred.add(new SmallLabel(c.value));
        }




        return pred;
    }

    /**
     * This returns the pattern label.
     */
    private Widget getPatternLabel() {
        ClickListener click = new ClickListener() {

            public void onClick(Widget w) {
                popupCreator.showPatternPopup(w, pattern.factType, null);
            }
        };

        String patternName = (pattern.boundName != null) ? pattern.factType + " <b>[" + pattern.boundName + "]</b>" : pattern.factType;

        String desc = this.getCustomLabel();
        if (desc == null) {
            if (pattern.constraintList != null && pattern.constraintList.constraints.length > 0) {
                desc = Format.format(constants.ThereIsAAn0With(), patternName);
            } else {
                desc = Format.format(constants.ThereIsAAn0(), patternName);
            }
            desc = anA(desc, patternName);
        } else {
            desc = Format.format(desc, patternName);
        }

        return new ClickableLabel(desc, click, !this.readOnly);
    }

    /** Change to an/a depending on context - only for english */
    private String anA(String desc, String patternName) {
        if (desc.startsWith("There is a/an")) { //NON-NLS
            String vowel = patternName.substring(0, 1);
            if (vowel.equalsIgnoreCase("A") || vowel.equalsIgnoreCase("E") || vowel.equalsIgnoreCase("I") || vowel.equalsIgnoreCase("O") || vowel.equalsIgnoreCase("U")) { //NON-NLS
                return desc.replace("There is a/an", "There is an"); //NON-NLS
            } else {
                return desc.replace("There is a/an", "There is a");  //NON-NLS
            }
        } else {
            return desc;
        }
    }

    private Widget valueEditor(final SingleFieldConstraint c, String factType) {
        //String type = this.modeller.getSuggestionCompletions().getFieldType( factType, c.fieldName );
        return new ConstraintValueEditor(pattern, c.fieldName, c, this.getModeller(), c.fieldType,this.readOnly);
    }

    private Widget operatorDropDown(final SingleFieldConstraint c) {
        if (!this.readOnly) {
            String[] ops = completions.getOperatorCompletions(pattern.factType, c.fieldName);
            final ListBox box = new ListBox();
            box.addItem(constants.pleaseChoose(), "");
            for (int i = 0; i < ops.length; i++) {
                String op = ops[i];
                box.addItem(HumanReadable.getOperatorDisplayName(op), op);
                if (op.equals(c.operator)) {
                    box.setSelectedIndex(i + 1);
                }

            }

            box.addChangeListener(new ChangeListener() {

                public void onChange(Widget w) {
                    c.operator = box.getValue(box.getSelectedIndex());
                    if (c.operator.equals("")) {
                        c.operator = null;
                    }
                    getModeller().makeDirty();
                }
            });

            return box;
        } else {
            SmallLabel sl = new SmallLabel("<b>"+(c.operator==null?constants.pleaseChoose():HumanReadable.getOperatorDisplayName(c.operator))+"</b>");
            return sl;
        }

    }

    /**
     * get the field widget. This may be a simple label, or it may
     * be bound (and show the var name) or a icon to create a binding.
     * It will only show the binding option of showBinding is true.
     */
    private Widget fieldLabel(final SingleFieldConstraint con, boolean showBinding, int padding) {//, final Command onChange) {
        HorizontalPanel ab = new HorizontalPanel();
        ab.setStyleName("modeller-field-Label");

        if (!con.isBound()) {
            if (bindable && showBinding && !this.readOnly) {

                ClickListener click = new ClickListener() {

                    public void onClick(Widget w) {
                        String[] fields = completions.getFieldCompletions(con.fieldType);
                        popupCreator.showBindFieldPopup(w, con, fields, popupCreator);
                    }
                };

                Image bind = new ImageButton("images/edit_tiny.gif", constants.GiveFieldVarName()); //NON-NLS

                bind.addClickListener(click);
                ClickableLabel cl = new ClickableLabel(con.fieldName, click, !this.readOnly);
                DOM.setStyleAttribute(cl.getElement(), "marginLeft", "" + padding + "pt"); //NON-NLS
                ab.add(cl);
                //ab.add( bind );
            } else {
                ab.add(new SmallLabel(con.fieldName));
            }

        } else {
            ab.add(new SmallLabel(con.fieldName));
            ab.add(new SmallLabel(" <b>[" + con.fieldBinding + "]</b>"));       //NON-NLS
        }


        return ab;
    }

    public String getCustomLabel() {
        return customLabel;
    }

    public void setCustomLabel(String customLabel) {
        this.customLabel = customLabel;
    }

    public boolean isDirty() {
        return layout.hasDirty();
    }

    @Override
    public boolean isReadOnly() {
        return this.readOnly;
    }

}
