package org.drools.guvnor.client.decisiontable;

import java.util.HashSet;
import java.util.Set;

import org.drools.guvnor.client.common.FormStylePopup;
import org.drools.guvnor.client.common.ImageButton;
import org.drools.guvnor.client.common.InfoPopup;
import org.drools.guvnor.client.common.SmallLabel;
import org.drools.guvnor.client.modeldriven.SuggestionCompletionEngine;
import org.drools.guvnor.client.modeldriven.dt.ActionCol;
import org.drools.guvnor.client.modeldriven.dt.ActionInsertFactCol;
import org.drools.guvnor.client.modeldriven.dt.ConditionCol;
import org.drools.guvnor.client.modeldriven.dt.GuidedDecisionTable;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * This is an editor for columns that are for inserting facts.
 * @author Michael Neale
 *
 */
public class ActionInsertColumn extends FormStylePopup {

	private GuidedDecisionTable dt;
	private SuggestionCompletionEngine sce;
	private ActionInsertFactCol editingCol;
	private SmallLabel patternLabel = new SmallLabel();
	private TextBox fieldLabel = getFieldLabel();

	public ActionInsertColumn(SuggestionCompletionEngine sce, final GuidedDecisionTable dt, final Command refreshGrid, final ActionInsertFactCol col, final boolean isNew) {
		this.setModal(false);
		this.dt = dt;
		this.sce = sce;
		this.editingCol = new ActionInsertFactCol();
		editingCol.boundName = col.boundName;
		editingCol.type = col.type;
		editingCol.factField = col.factField;
		editingCol.factType = col.factType;
		editingCol.header = col.header;
		editingCol.valueList = col.valueList;

		setTitle("Action column configuration (inserting a new fact)");



		HorizontalPanel pattern = new HorizontalPanel();
		pattern.add(patternLabel );
		doPatternLabel();

		Image changePattern = new ImageButton("images/edit.gif", "Choose a pattern that this column adds data to", new ClickListener() {
			public void onClick(Widget w) {
				showChangePattern(w);
			}
		});
		pattern.add(changePattern);
		addAttribute("Pattern:", pattern);

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
					col.type = editingCol.type;
					col.factField = editingCol.factField;
					col.factType = editingCol.factType;
					col.header = editingCol.header;
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
				editingCol.type = sce.getFieldType(editingCol.factType, editingCol.factField);
				doFieldLabel();
				pop.hide();
			}
		});
		pop.show();

	}

	private void doFieldLabel() {
		if (nil(this.editingCol.factField)) {
			fieldLabel.setText("(please choose fact type)");
		} else {
			fieldLabel.setText(editingCol.factField);
		}

	}

	private boolean nil(String s) {
		return s == null || s.equals("");
	}

	private void doPatternLabel() {
		if (this.editingCol.factType != null) {
			this.patternLabel.setText(this.editingCol.factType + " [" + editingCol.boundName + "]");
		}
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
		pop.setTitle("New fact - select the type");
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

		for (Object o : dt.actionCols) {
			ActionCol col = (ActionCol) o;
			if (col instanceof ActionInsertFactCol) {
				ActionInsertFactCol c = (ActionInsertFactCol) col;
				if (!vars.contains(c.boundName)) {
					patterns.addItem(c.factType + " [" + c.boundName + "]", c.factType + " " + c.boundName);
					vars.add(c.boundName);
				}
			}

		}

		return patterns;

	}



}
