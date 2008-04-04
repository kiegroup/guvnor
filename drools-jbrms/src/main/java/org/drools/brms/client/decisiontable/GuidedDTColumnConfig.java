package org.drools.brms.client.decisiontable;

import java.util.HashSet;
import java.util.Set;

import org.drools.brms.client.common.FormStylePopup;
import org.drools.brms.client.common.ImageButton;
import org.drools.brms.client.common.InfoPopup;
import org.drools.brms.client.common.SmallLabel;
import org.drools.brms.client.modeldriven.HumanReadable;
import org.drools.brms.client.modeldriven.SuggestionCompletionEngine;
import org.drools.brms.client.modeldriven.brl.ISingleFieldConstraint;
import org.drools.brms.client.modeldriven.dt.ConditionCol;
import org.drools.brms.client.modeldriven.dt.GuidedDecisionTable;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

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


		setTitle("Condition column configuration");



		HorizontalPanel pattern = new HorizontalPanel();
		pattern.add(patternLabel);
		doPatternLabel();

		Image changePattern = new ImageButton("images/edit.gif", "Choose an existing pattern that this column adds to", new ClickListener() {
			public void onClick(Widget w) {
				showChangePattern(w);
			}
		});
		pattern.add(changePattern);


		addAttribute("Pattern:", pattern);

		//now a radio button with the type
		RadioButton literal = new RadioButton("constraintValueType", "Literal value");
		RadioButton formula = new RadioButton("constraintValueType", "Formula");
		RadioButton predicate = new RadioButton("constraintValueType", "Predicate");


		HorizontalPanel valueTypes = new HorizontalPanel();
		valueTypes.add(literal);
		valueTypes.add(formula);
		valueTypes.add(predicate);
		addAttribute("Calculation type:", valueTypes);

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
		Image editField = new ImageButton("images/edit.gif", "Edit the field that this column operates on", new ClickListener() {
			public void onClick(Widget w) {
				showFieldChange();
			}
		});
		field.add(editField);
		addAttribute("Field:", field);
		doFieldLabel();


		HorizontalPanel operator = new HorizontalPanel();
		operator.add(operatorLabel);
		Image editOp = new ImageButton("images/edit.gif", "Edit the operator that is used to compare data with this field", new ClickListener() {
			public void onClick(Widget w) {
				showOperatorChange();
			}
		});
		operator.add(editOp);
		addAttribute("Operator:", operator);
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
		vl.add(new InfoPopup("Value list", "Value lists are an optional comma separated list of values to show as a drop down."));
		addAttribute("(optional) value list:", vl);

		final TextBox header = new TextBox();
		header.setText(col.header);
		header.addChangeListener(new ChangeListener() {
			public void onChange(Widget w) {
				editingCol.header = header.getText();
			} });
		addAttribute("Column header (description):", header);


		Button apply = new Button("Apply changes");
		apply.addClickListener(new ClickListener() {
			public void onClick(Widget w) {
				if (isNew) {
					dt.conditionCols.add(editingCol);
				} else {
					col.boundName = editingCol.boundName;
					col.constraintValueType = editingCol.constraintValueType;
					col.factField = editingCol.factField;
					col.factType = editingCol.factType;
					col.header = editingCol.header;
					col.operator = editingCol.operator;
					col.valueList = editingCol.valueList;
				}
				refreshGrid.execute();
				hide();

			}
		});
		addAttribute("", apply);





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
			operatorLabel.setText("(not needed for predicate)");
		} else if (nil(editingCol.factType)) {
			operatorLabel.setText("(please select a pattern first)");
		} else if (nil(editingCol.factField)) {
			operatorLabel.setText("(please choose a field first)");
		} else if (nil(editingCol.operator)) {
			operatorLabel.setText("(please select a field)");
		} else {
			operatorLabel.setText(HumanReadable.getOperatorDisplayName(editingCol.operator));
		}
	}

	private void showOperatorChange() {
		final FormStylePopup pop = new FormStylePopup();
		pop.setTitle("Set the operator");
		pop.setModal(false);
		String[] ops = this.sce.getOperatorCompletions(editingCol.factType, editingCol.factField);
		final ListBox box = new ListBox();
		for (int i = 0; i < ops.length; i++) {
			box.addItem(HumanReadable.getOperatorDisplayName(ops[i]), ops[i]);
		}
		pop.addAttribute("Operator:", box);
		Button b = new Button("OK");
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
			fieldLabel.setText("(not needed for predicate)");
		} else if (nil(editingCol.factType)) {
			fieldLabel.setText("(please select a pattern first)");
		}
		else if (nil(editingCol.factField)) {
			fieldLabel.setText("(please select a field)");
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
		pop.addAttribute("Field:", box);
		Button b = new Button("OK");
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
		Button ok = new Button("OK");
		HorizontalPanel hp = new HorizontalPanel();
		hp.add(pats);
		hp.add(ok);


		pop.addAttribute("Choose existing pattern to add column to:", hp);
		pop.addAttribute("", new HTML("<i><b>---OR---</i></b>"));

		Button createPattern = new Button("Create new fact pattern");
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
		pop.setTitle("Create a new fact pattern");
		final ListBox types = new ListBox();
		for (int i = 0; i < sce.factTypes.length; i++) {
			types.addItem(sce.factTypes[i]);
		}
		pop.addAttribute("Fact type:", types);
		final TextBox binding = new TextBox();
		pop.addAttribute("name:", binding);

		Button ok = new Button("OK");
		ok.addClickListener(new ClickListener() {
			public void onClick(Widget w) {
				editingCol.boundName = binding.getText();
				editingCol.factType = types.getItemText(types.getSelectedIndex());
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
			ConditionCol c = (ConditionCol) dt.conditionCols.get(i);
			if (!vars.contains(c.boundName)) {
				patterns.addItem(c.factType + " [" + c.boundName + "]", c.factType + " " + c.boundName);
				vars.add(c.boundName);
			}
		}

		return patterns;

	}



}
