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
import org.drools.guvnor.client.modeldriven.ui.factPattern.Connectives;
import org.drools.guvnor.client.modeldriven.ui.factPattern.PopupCreator;
import org.drools.ide.common.client.modeldriven.brl.CompositeFieldConstraint;
import org.drools.ide.common.client.modeldriven.brl.FactPattern;
import org.drools.ide.common.client.modeldriven.brl.FieldConstraint;
import org.drools.ide.common.client.modeldriven.brl.IPattern;
import org.drools.ide.common.client.modeldriven.brl.SingleFieldConstraint;
import org.drools.ide.common.client.modeldriven.brl.SingleFieldConstraintEBLeftSide;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.gwtext.client.util.Format;

/**
 * This is the new smart widget that works off the model.
 * @author Michael Neale
 *
 */
public class FactPatternWidget extends RuleModellerWidget {

    private FactPattern pattern;
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

    public FactPatternWidget(RuleModeller ruleModeller, IPattern pattern,
			boolean canBind, Boolean readOnly) {
    	this(ruleModeller, pattern, null, canBind, readOnly);
	}

    public FactPatternWidget(RuleModeller mod, IPattern p,
            String customLabel, boolean canBind, Boolean readOnly) {
        super(mod);
        this.pattern = (FactPattern) p;
        this.bindable = canBind;

        this.connectives = new Connectives();
        this.connectives.setCompletions(mod.getSuggestionCompletions());
        this.connectives.setModeller(mod);
        this.connectives.setPattern(pattern);

        this.popupCreator = new PopupCreator();
        this.popupCreator.setBindable(bindable);
        this.popupCreator.setCompletions(mod.getSuggestionCompletions());
        this.popupCreator.setModeller(mod);
        this.popupCreator.setPattern(pattern);

        this.customLabel = customLabel;

        //if readOnly == null, the RO attribute is calculated.
        if (readOnly == null){
            this.readOnly = !connectives.getCompletions().containsFactType(this.pattern.factType);
        }else{
            this.readOnly = readOnly;
        }

        layout.setWidget(0, 0, getPatternLabel());
        FlexCellFormatter formatter = layout.getFlexCellFormatter();
        formatter.setAlignment(0, 0, HasHorizontalAlignment.ALIGN_LEFT, HasVerticalAlignment.ALIGN_BOTTOM);
        formatter.setStyleName(0, 0, "modeller-fact-TypeHeader");

        List<FieldConstraint> sortedConst = sortConstraints(pattern.getFieldConstraints());
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
    private void drawConstraints(List<FieldConstraint> sortedConst) {
        final DirtyableFlexTable table = new DirtyableFlexTable();
        layout.setWidget(1, 0, table);
        List<FieldConstraint> parents = new ArrayList<FieldConstraint>();

        for (int i = 0; i < sortedConst.size(); i++) {
            int tabs = -1;
            FieldConstraint current = sortedConst.get(i);
            if (current instanceof SingleFieldConstraint) {
                SingleFieldConstraint single = (SingleFieldConstraint) current;
                FieldConstraint parent = single.getParent();

                for (int j = 0; j < parents.size(); j++) {
                    FieldConstraint storedParent = parents.get(j);
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
            clear.addClickHandler(new ClickHandler() {
				
				public void onClick(ClickEvent event) {
					if (Window.confirm(constants.RemoveThisItem())) {
                        setModified(true);
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
    private List<FieldConstraint> sortConstraints(FieldConstraint[] constraints) {
        List<FieldConstraint> sortedConst = new ArrayList<FieldConstraint>(constraints.length);
        for (int i = 0; i < constraints.length; i++) {
            FieldConstraint current = constraints[i];
            if (current instanceof SingleFieldConstraint) {
                SingleFieldConstraint single = (SingleFieldConstraint) current;
                int index = sortedConst.indexOf(single.getParent());
                if (single.getParent() == null) {
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
    private void insertSingleFieldConstraint(SingleFieldConstraint fieldConst, List<FieldConstraint> sortedConst) {
        if (fieldConst.getParent() instanceof SingleFieldConstraint) {
            insertSingleFieldConstraint((SingleFieldConstraint) fieldConst.getParent(), sortedConst);
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

    	ClickHandler click = new ClickHandler() {
			
			public void onClick(ClickEvent event) {
                popupCreator.showPatternPopupForComposite((Widget) event.getSource(), constraint);
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
                clear.addClickHandler(new ClickHandler() {
					
					public void onClick(ClickEvent event) {
						if (Window.confirm(constants.RemoveThisItemFromNestedConstraint())) {
                            setModified(true);
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
            final DirtyableFlexTable inner, final int row, final SingleFieldConstraint constraint,
            boolean showBinding, final int tabs) {

        final int col = 1; //for offsetting, just a slight indent

        inner.setWidget(row, 0, new HTML("&nbsp;&nbsp;&nbsp;&nbsp;"));
        //inner.getFlexCellFormatter().setWidth(row, 0, "15%");
        //DOCNHERON
        if (constraint.getConstraintValueType() != SingleFieldConstraint.TYPE_PREDICATE) {

        	HorizontalPanel ebContainer = null;
        	if (constraint instanceof SingleFieldConstraintEBLeftSide) {
        		ebContainer = expressionBuilderLS(
        				(SingleFieldConstraintEBLeftSide) constraint, showBinding, tabs * 20);
				inner.setWidget(row, 0 + col, ebContainer);
        	} else {
        		inner.setWidget(row, 0 + col, fieldLabel(constraint, showBinding, tabs * 20));
        	}
            inner.setWidget(row, 1 + col, operatorDropDown(constraint));
            inner.setWidget(row, 2 + col, valueEditor(constraint, constraint.getFieldType()));
            inner.setWidget(row, 3 + col, connectives.connectives(constraint, constraint.getFieldType()));
            
            if (ebContainer != null && ebContainer.getWidgetCount() > 0) {
            	if (ebContainer.getWidget(0) instanceof ExpressionBuilder) {
					ExpressionBuilder eb = (ExpressionBuilder) ebContainer.getWidget(0);
					eb.addExpressionTypeChangeHandler(new ExpressionTypeChangeHandler() {
						
						public void onExpressionTypeChanged(ExpressionTypeChangeEvent event) {
							try {
								constraint.setFieldType(event.getNewType());
								inner.setWidget(row, 1 + col, operatorDropDown(constraint, constraint.getFieldType()));
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});
				}
            }
            
            Image addConnective = new ImageButton("images/add_connective.gif"); //NON-NLS
            addConnective.setTitle(constants.AddMoreOptionsToThisFieldsValues());
            addConnective.addClickHandler(new ClickHandler() {
				
				public void onClick(ClickEvent event) {
                    setModified(true);
                    constraint.addNewConnective();
                    modeller.refreshWidget();
                }
            });

            if (!this.readOnly) {
                inner.setWidget(row, 4 + col, addConnective);
            }
        } else if (constraint.getConstraintValueType() == SingleFieldConstraint.TYPE_PREDICATE) {
            inner.setWidget(row, 1, predicateEditor(constraint));
            inner.getFlexCellFormatter().setColSpan(row, 1, 5);
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
        if (c.getValue() == null) {
            c.setValue("");
        }

        final TextBox box = new TextBox();
        box.setText(c.getValue());

        if (!this.readOnly) {
            box.addChangeHandler(new ChangeHandler() {
				
				public void onChange(ChangeEvent event) {
					setModified(true);
                    c.setValue(box.getText());
                    getModeller().makeDirty();
                }
            });
            box.setWidth("100%");
            pred.add(box);
        } else {
            pred.add(new SmallLabel(c.getValue()));
        }

        return pred;
    }

    /**
     * This returns the pattern label.
     */
    private Widget getPatternLabel() {
    	ClickHandler click = new ClickHandler() {
			
			public void onClick(ClickEvent event) {
                String factTypeShortName = (pattern.factType.contains(".")?pattern.factType.substring(pattern.factType.lastIndexOf(".")+1):pattern.factType);
                popupCreator.showPatternPopup((Widget) event.getSource(), factTypeShortName, null);
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
        ConstraintValueEditor constraintValueEditor = new ConstraintValueEditor(pattern, c.getFieldName(), c, this.getModeller(), c.getFieldType(),this.readOnly);
        constraintValueEditor.setOnValueChangeCommand(new Command() {
            public void execute() {
                setModified(true);
            }
        });
        return constraintValueEditor;
    }

    
    
    private Widget operatorDropDown(final SingleFieldConstraint c) {
    	return operatorDropDown(c, connectives.getCompletions()
    			.getFieldType(pattern.factType, c.getFieldName()));
    }
    
    private Widget operatorDropDown(final SingleFieldConstraint c, String type) {
        if (!this.readOnly) {
            String[] ops = connectives.getCompletions().getOperatorCompletions(type);
            final ListBox box = new ListBox();
            box.addItem(constants.pleaseChoose(), "");
            for (int i = 0; i < ops.length; i++) {
                String op = ops[i];
                box.addItem(HumanReadable.getOperatorDisplayName(op), op);
                if (op.equals(c.getOperator())) {
                    box.setSelectedIndex(i + 1);
                }

            }

            box.addChangeHandler(new ChangeHandler() {
				
				public void onChange(ChangeEvent event) {
                    setModified(true);
                    c.setOperator(box.getValue(box.getSelectedIndex()));
                    if (c.getOperator().equals("")) {
                        c.setOperator(null);
                    }
                    getModeller().makeDirty();
                }
            });

            return box;
        } else {
            SmallLabel sl = new SmallLabel("<b>"+(c.getOperator()==null?constants.pleaseChoose():HumanReadable.getOperatorDisplayName(c.getOperator()))+"</b>");
            return sl;
        }

    }

    private HorizontalPanel expressionBuilderLS(final SingleFieldConstraintEBLeftSide con, boolean showBinding, int padding) {
    	HorizontalPanel ab = new HorizontalPanel();
        ab.setStyleName("modeller-field-Label");

        if (!con.isBound()) {
            if (bindable && showBinding && !this.readOnly) {
				ab.add(new ExpressionBuilder(getModeller(), con.getExpressionLeftSide()));
            } else {
                ab.add(new SmallLabel(con.getExpressionLeftSide().getText()));
            }
        } else {
        	ab.add(new ExpressionBuilder(getModeller(), con.getExpressionLeftSide()));
        }
        return ab;
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
            	ClickHandler click = new ClickHandler() {
					
					public void onClick(ClickEvent event) {
                        String[] fields = connectives.getCompletions().getFieldCompletions(con.getFieldType());
                        popupCreator.showBindFieldPopup((Widget) event.getSource(), con, fields, popupCreator);
                    }
                };

                Image bind = new ImageButton("images/edit_tiny.gif", constants.GiveFieldVarName()); //NON-NLS

                bind.addClickHandler(click);
                ClickableLabel cl = new ClickableLabel(con.getFieldName(), click, !this.readOnly);
                DOM.setStyleAttribute(cl.getElement(), "marginLeft", "" + padding + "pt"); //NON-NLS
                ab.add(cl);
                //ab.add( bind );
            } else {
                ab.add(new SmallLabel(con.getFieldName()));
            }

        } else {
            ab.add(new SmallLabel(con.getFieldName()));
            ab.add(new SmallLabel(" <b>[" + con.getFieldBinding() + "]</b>"));       //NON-NLS
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
