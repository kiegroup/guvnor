package org.drools.brms.client.decisiontable;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.drools.brms.client.common.FormStylePopup;
import org.drools.brms.client.common.ImageButton;
import org.drools.brms.client.common.InfoPopup;
import org.drools.brms.client.common.SmallLabel;
import org.drools.brms.client.modeldriven.SuggestionCompletionEngine;
import org.drools.brms.client.modeldriven.dt.ActionSetFieldCol;
import org.drools.brms.client.modeldriven.dt.ConditionCol;
import org.drools.brms.client.modeldriven.dt.GuidedDecisionTable;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class ActionSetColumn extends FormStylePopup {

	private ActionSetFieldCol editingCol;
	private SmallLabel bindingLabel = new SmallLabel();
	private TextBox fieldLabel = getFieldLabel();
	private GuidedDecisionTable dt;
	private SuggestionCompletionEngine sce;

	public ActionSetColumn(SuggestionCompletionEngine sce, final GuidedDecisionTable dt, final Command refreshGrid, final ActionSetFieldCol col, final boolean isNew) {
		this.editingCol = new ActionSetFieldCol();
		this.dt = dt;
		this.sce = sce;

		editingCol.boundName = col.boundName;
		editingCol.factField = col.factField;
		editingCol.header = col.header;
		editingCol.type = col.type;
		editingCol.valueList = col.valueList;
		super.setModal(false);
		setTitle("Column configuration (set a field on a fact)");



		HorizontalPanel pattern = new HorizontalPanel();
		pattern.add(bindingLabel);
		doBindingLabel();

		Image changePattern = new ImageButton("images/edit.gif", "Choose a bound fact that this column pertains to", new ClickListener() {
			public void onClick(Widget w) {
				showChangeFact(w);
			}
		});
		pattern.add(changePattern);
		addAttribute("Fact:", pattern);


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
					dt.actionCols.add(editingCol);

				} else {
					col.boundName = editingCol.boundName;
					col.factField = editingCol.factField;
					col.header = editingCol.header;
					col.type = editingCol.type;
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

	private void showFieldChange() {
		final FormStylePopup pop = new FormStylePopup();
		pop.setModal(false);
		final String factType = getFactType(this.editingCol.boundName);
		String[] fields = this.sce.getFieldCompletions(factType);
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
				editingCol.type = sce.getFieldType(factType, editingCol.factField);
				doFieldLabel();
				pop.hide();
			}
		});
		pop.show();

	}

	private void doFieldLabel() {
		if (this.editingCol.factField != null) {
			this.fieldLabel.setText(this.editingCol.factField);
		} else {
			this.fieldLabel.setText("(please choose a fact pattern first)");
		}
	}

	private String getFactType(String boundName) {
		for (Iterator iterator = dt.conditionCols.iterator(); iterator.hasNext();) {
			ConditionCol col = (ConditionCol) iterator.next();
			if (col.boundName.equals(boundName)) {
				return col.factType;
			}
		}
		return "";
	}

	private void showChangeFact(Widget w) {
		final FormStylePopup pop = new FormStylePopup();

		final ListBox pats = this.loadBoundFacts();
		pop.addAttribute("Choose fact:", pats);
		Button ok = new Button("OK");
		pop.addAttribute("", ok);

		ok.addClickListener(new ClickListener() {
			public void onClick(Widget w) {
				String val = pats.getValue(pats.getSelectedIndex());
				editingCol.boundName = val;
				doBindingLabel();
				pop.hide();
			}
		});

		pop.show();

	}

	private ListBox loadBoundFacts() {
		Set facts = new HashSet();
		for (int i = 0; i < this.dt.conditionCols.size(); i++) {
			ConditionCol c = (ConditionCol) dt.conditionCols.get(i);
			facts.add(c.boundName);
		}

		ListBox box = new ListBox();
		for (Iterator iterator = facts.iterator(); iterator.hasNext();) {
			String b = (String) iterator.next();
			box.addItem(b);
		}

		String[] globs = this.sce.getGlobalVariables();
		for (int i = 0; i < globs.length; i++) {
			box.addItem(globs[i]);
		}

		return box;
	}

	private void doBindingLabel() {
		if (this.editingCol.boundName != null) {
			this.bindingLabel.setText("" + this.editingCol.boundName);
		} else {
			this.bindingLabel.setText("(please choose a bound fact for this column)");
		}
	}

}
