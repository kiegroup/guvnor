package org.drools.guvnor.client.decisiontable;

import java.util.HashSet;
import java.util.Set;

import org.drools.guvnor.client.common.FormStylePopup;
import org.drools.guvnor.client.common.ImageButton;
import org.drools.guvnor.client.common.InfoPopup;
import org.drools.guvnor.client.common.SmallLabel;
import org.drools.guvnor.client.modeldriven.HumanReadable;
import org.drools.guvnor.client.modeldriven.SuggestionCompletionEngine;
import org.drools.guvnor.client.modeldriven.brl.ISingleFieldConstraint;
import org.drools.guvnor.client.modeldriven.dt.ConditionCol;
import org.drools.guvnor.client.modeldriven.dt.GuidedDecisionTable;
import org.drools.guvnor.client.modeldriven.dt.DTColumnConfig;
import org.drools.guvnor.client.messages.Constants;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.core.client.GWT;

/**
 * This is a configuration editor for a column in a the guided decision table.
 * @author Michael Neale
 *
 */
public class GuidedDTColumnConfig extends FormStylePopup {

	private GuidedDecisionTable dt;
	private SuggestionCompletionEngine sce;
	private ConditionCol editingCol;
	private SmallLabel patternLabel = new SmallLabel();
	private TextBox fieldLabel = getFieldLabel();
	private SmallLabel operatorLabel = new SmallLabel();
    private Constants constants = ((Constants) GWT.create(Constants.class));

    /**
	 * Pass in a null col and it will create a new one.
	 */
	public GuidedDTColumnConfig(SuggestionCompletionEngine sce, final GuidedDecisionTable dt, final Command refreshGrid, final ConditionCol col, final boolean isNew) {
		super();
		this.setModal(false);
		this.dt = dt;
		this.sce = sce;
		this.editingCol = new ConditionCol();
		editingCol.boundName = col.boundName;
		editingCol.constraintValueType = col.constraintValueType;
		editingCol.factField = col.factField;
		editingCol.factType = col.factType;
		editingCol.header = col.header;
		editingCol.operator = col.operator;
		editingCol.valueList = col.valueList;
        editingCol.defaultValue = col.defaultValue;
        editingCol.hideColumn = col.hideColumn;


		setTitle(constants.ConditionColumnConfiguration());



		HorizontalPanel pattern = new HorizontalPanel();
		pattern.add(patternLabel);
		doPatternLabel();

		Image changePattern = new ImageButton("images/edit.gif", constants.ChooseAnExistingPatternThatThisColumnAddsTo(), new ClickListener() { //NON-NLS
			public void onClick(Widget w) {
				showChangePattern(w);
			}
		});
		pattern.add(changePattern);


		addAttribute(constants.Pattern(), pattern);

		//now a radio button with the type
		RadioButton literal = new RadioButton("constraintValueType", constants.LiteralValue());//NON-NLS
		RadioButton formula = new RadioButton("constraintValueType", constants.Formula());     //NON-NLS
		RadioButton predicate = new RadioButton("constraintValueType", constants.Predicate()); //NON-NLS


		HorizontalPanel valueTypes = new HorizontalPanel();
		valueTypes.add(literal);
		valueTypes.add(formula);
		valueTypes.add(predicate);
		addAttribute(constants.CalculationType(), valueTypes);

		switch (editingCol.constraintValueType) {
			case ISingleFieldConstraint.TYPE_LITERAL:
				literal.setChecked(true);
				break;
			case ISingleFieldConstraint.TYPE_RET_VALUE :
				formula.setChecked(true);
				break;
			case ISingleFieldConstraint.TYPE_PREDICATE :
				predicate.setChecked(true);
		}

		literal.addClickListener(new ClickListener() {
			public void onClick(Widget w) {
				applyConsTypeChange(ISingleFieldConstraint.TYPE_LITERAL);
			}
		});


		formula.addClickListener(new ClickListener() {
			public void onClick(Widget w) {
				applyConsTypeChange(ISingleFieldConstraint.TYPE_RET_VALUE);
			}
		});
		predicate.addClickListener(new ClickListener() {
			public void onClick(Widget w) {
				applyConsTypeChange(ISingleFieldConstraint.TYPE_PREDICATE);
			}
		});


		HorizontalPanel field = new HorizontalPanel();
		field.add(fieldLabel);
		Image editField = new ImageButton("images/edit.gif", constants.EditTheFieldThatThisColumnOperatesOn(), new ClickListener() { //NON-NLS
			public void onClick(Widget w) {
				showFieldChange();
			}
		});
		field.add(editField);
		addAttribute(constants.Field(), field);
		doFieldLabel();


		HorizontalPanel operator = new HorizontalPanel();
		operator.add(operatorLabel);
		Image editOp = new ImageButton("images/edit.gif", constants.EditTheOperatorThatIsUsedToCompareDataWithThisField(), new ClickListener() { //NON-NLS
			public void onClick(Widget w) {
				showOperatorChange();
			}
		});
		operator.add(editOp);
		addAttribute(constants.Operator(), operator);
		doOperatorLabel();

		final TextBox valueList = new TextBox();
		valueList.setText(editingCol.valueList);
		valueList.addChangeListener(new ChangeListener() {
			public void onChange(Widget w) {
				editingCol.valueList = valueList.getText();
			}
		});
		HorizontalPanel vl = new HorizontalPanel();
		vl.add(valueList);
		vl.add(new InfoPopup(constants.ValueList(), constants.ValueListsExplanation()));
		addAttribute(constants.optionalValueList(), vl);

		final TextBox header = new TextBox();
		header.setText(col.header);
		header.addChangeListener(new ChangeListener() {
			public void onChange(Widget w) {
				editingCol.header = header.getText();
			} });
		addAttribute(constants.ColumnHeaderDescription(), header);


        addAttribute(constants.DefaultValue(), getDefaultEditor(editingCol));


		Button apply = new Button(constants.ApplyChanges());
		apply.addClickListener(new ClickListener() {
			public void onClick(Widget w) {
                if (null == editingCol.header || "".equals(editingCol.header)) {
                    Window.alert(constants.YouMustEnterAColumnHeaderValueDescription());
                    return;
                }
				if (isNew) {
                    if (!unique(editingCol.header)) {
                        Window.alert(constants.ThatColumnNameIsAlreadyInUsePleasePickAnother());
                        return;
                    }
					dt.conditionCols.add(editingCol);
				} else {
                    if (!col.header.equals(editingCol.header)) {
                        if (!unique(editingCol.header)) {
                            Window.alert(constants.ThatColumnNameIsAlreadyInUsePleasePickAnother());
                            return;
                        }
                    }
					col.boundName = editingCol.boundName;
					col.constraintValueType = editingCol.constraintValueType;
					col.factField = editingCol.factField;
					col.factType = editingCol.factType;

					col.header = editingCol.header;
					col.operator = editingCol.operator;
					col.valueList = editingCol.valueList;
                    col.defaultValue = editingCol.defaultValue;
                    col.hideColumn = editingCol.hideColumn;
				}
				refreshGrid.execute();
				hide();

			}
		});
		addAttribute("", apply);





	}

    /**
     * An editor for setting the default value.
     */
    public static HorizontalPanel getDefaultEditor(final DTColumnConfig editingCol) {
        final TextBox defaultValue = new TextBox();
        defaultValue.setText(editingCol.defaultValue);
        final CheckBox hide = new CheckBox(((Constants) GWT.create(Constants.class)).HideThisColumn());
        hide.setChecked(editingCol.hideColumn);
        hide.addClickListener(new ClickListener() {
            public void onClick(Widget sender) {
                editingCol.hideColumn = hide.isChecked();
            }
        });
        defaultValue.addChangeListener(new ChangeListener() {
            public void onChange(Widget sender) {
                editingCol.defaultValue = defaultValue.getText();
            }
        });
        HorizontalPanel hp = new HorizontalPanel();
        hp.add(defaultValue);
        hp.add(hide);
        return hp;
    }

    private boolean unique(String header) {
        for (ConditionCol o : dt.conditionCols) {
            if (o.header.equals(header)) return false;
        }
        return true;
    }

    private TextBox getFieldLabel() {
		final TextBox box = new TextBox();
		box.addChangeListener(new ChangeListener() {
			public void onChange(Widget w) {
				editingCol.factField = box.getText();
			}
		});
		return box;
	}

	private void applyConsTypeChange(int newType) {
		editingCol.constraintValueType = newType;
		doFieldLabel();
		doOperatorLabel();
	}


	private void doOperatorLabel() {
		if (editingCol.constraintValueType == ISingleFieldConstraint.TYPE_PREDICATE) {
			operatorLabel.setText(constants.notNeededForPredicate());
		} else if (nil(editingCol.factType)) {
			operatorLabel.setText(constants.pleaseSelectAPatternFirst());
		} else if (nil(editingCol.factField)) {
			operatorLabel.setText(constants.pleaseChooseAFieldFirst());
		} else if (nil(editingCol.operator)) {
			operatorLabel.setText(constants.pleaseSelectAField());
		} else {
			operatorLabel.setText(HumanReadable.getOperatorDisplayName(editingCol.operator));
		}
	}

	private void showOperatorChange() {
		final FormStylePopup pop = new FormStylePopup();
		pop.setTitle(constants.SetTheOperator());
		pop.setModal(false);
		String[] ops = this.sce.getOperatorCompletions(editingCol.factType, editingCol.factField);
		final ListBox box = new ListBox();
		for (int i = 0; i < ops.length; i++) {
			box.addItem(HumanReadable.getOperatorDisplayName(ops[i]), ops[i]);
		}
		box.addItem(constants.noOperator(), "");
		pop.addAttribute(constants.Operator(), box);
		Button b = new Button(constants.OK());
		pop.addAttribute("", b);
		b.addClickListener(new ClickListener() {
			public void onClick(Widget w) {
				editingCol.operator = box.getValue(box.getSelectedIndex());
				doOperatorLabel();
				pop.hide();
			}
		});
		pop.show();

	}

	private void doFieldLabel() {
		if (editingCol.constraintValueType == ISingleFieldConstraint.TYPE_PREDICATE) {
			fieldLabel.setText(constants.notNeededForPredicate());
		} else if (nil(editingCol.factType)) {
			fieldLabel.setText(constants.pleaseSelectAPatternFirst());
		}
		else if (nil(editingCol.factField)) {
			fieldLabel.setText(constants.pleaseSelectAField());
		} else {
			fieldLabel.setText(this.editingCol.factField);
		}
	}

	private boolean nil(String s) {
		return s == null || s.equals("");
	}

	protected void showFieldChange() {
		final FormStylePopup pop = new FormStylePopup();
		pop.setModal(false);
		String[] fields = this.sce.getFieldCompletions(this.editingCol.factType);
		final ListBox box = new ListBox();
		for (int i = 0; i < fields.length; i++) {
			box.addItem(fields[i]);
		}
		pop.addAttribute(constants.Field(), box);
		Button b = new Button(constants.OK());
		pop.addAttribute("", b);
		b.addClickListener(new ClickListener() {
			public void onClick(Widget w) {
				editingCol.factField = box.getItemText(box.getSelectedIndex());
				doFieldLabel();
				doOperatorLabel();
				pop.hide();
			}
		});
		pop.show();
	}

	private void doPatternLabel() {
		if (this.editingCol.factType != null) {
			this.patternLabel.setText(this.editingCol.factType + " [" + editingCol.boundName + "]");
		}
		doFieldLabel();
		doOperatorLabel();

	}

	protected void showChangePattern(Widget w) {

		final ListBox pats = this.loadPatterns();
		if (pats.getItemCount() == 0) {
			showNewPatternDialog();
			return;
		}
		final FormStylePopup pop = new FormStylePopup();
		Button ok = new Button(constants.OK());
		HorizontalPanel hp = new HorizontalPanel();
		hp.add(pats);
		hp.add(ok);


		pop.addAttribute(constants.ChooseExistingPatternToAddColumnTo(), hp);
		pop.addAttribute("", new HTML(constants.ORwithEmphasis()));

		Button createPattern = new Button(constants.CreateNewFactPattern());
		createPattern.addClickListener(new ClickListener() {
			public void onClick(Widget w) {
				pop.hide();
				showNewPatternDialog();
			}
		});
		pop.addAttribute("", createPattern);




		ok.addClickListener(new ClickListener() {
			public void onClick(Widget w) {
				String[] val = pats.getValue(pats.getSelectedIndex()).split("\\s");
				editingCol.factType = val[0];
				editingCol.boundName = val[1];
				doPatternLabel();
				pop.hide();
			}
		});

		pop.show();
	}

	protected void showNewPatternDialog() {
		final FormStylePopup pop = new FormStylePopup();
		pop.setTitle(constants.CreateANewFactPattern());
		final ListBox types = new ListBox();
		for (int i = 0; i < sce.factTypes.length; i++) {
			types.addItem(sce.factTypes[i]);
		}
		pop.addAttribute(constants.FactType(), types);
		final TextBox binding = new TextBox();
		pop.addAttribute(constants.name(), binding);

		Button ok = new Button(constants.OK());
		ok.addClickListener(new ClickListener() {
			public void onClick(Widget w) {
                String ft = types.getItemText(types.getSelectedIndex());
                String fn = binding.getText();
                if (fn.equals("")) {
                    Window.alert(constants.PleaseEnterANameForFact());
                    return;
                } else if (fn.equals(ft)) {
                    Window.alert(constants.PleaseEnterANameThatIsNotTheSameAsTheFactType());
                    return;
                }
				editingCol.boundName = fn;
				editingCol.factType = ft;
				doPatternLabel();
				pop.hide();
			}
		});
		pop.addAttribute("", ok);

		pop.show();

	}

	private ListBox loadPatterns() {
		Set vars = new HashSet();
		ListBox patterns = new ListBox();
		for (int i = 0; i < dt.conditionCols.size(); i++) {
			ConditionCol c = dt.conditionCols.get(i);
			if (!vars.contains(c.boundName)) {
				patterns.addItem(c.factType + " [" + c.boundName + "]", c.factType + " " + c.boundName);
				vars.add(c.boundName);
			}
		}

		return patterns;

	}



}
