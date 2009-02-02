package org.drools.guvnor.client.decisiontable;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.drools.guvnor.client.common.FormStylePopup;
import org.drools.guvnor.client.common.ImageButton;
import org.drools.guvnor.client.common.InfoPopup;
import org.drools.guvnor.client.common.SmallLabel;
import org.drools.guvnor.client.modeldriven.SuggestionCompletionEngine;
import org.drools.guvnor.client.modeldriven.dt.ActionSetFieldCol;
import org.drools.guvnor.client.modeldriven.dt.ConditionCol;
import org.drools.guvnor.client.modeldriven.dt.GuidedDecisionTable;
import org.drools.guvnor.client.modeldriven.dt.ActionCol;
import org.drools.guvnor.client.messages.Messages;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.core.client.GWT;

public class ActionSetColumn extends FormStylePopup {

	private ActionSetFieldCol editingCol;
	private SmallLabel bindingLabel = new SmallLabel();
	private TextBox fieldLabel = getFieldLabel();
	private GuidedDecisionTable dt;
	private SuggestionCompletionEngine sce;
    private Messages constants = GWT.create(Messages.class);

    public ActionSetColumn(SuggestionCompletionEngine sce, final GuidedDecisionTable dt, final Command refreshGrid, final ActionSetFieldCol col, final boolean isNew) {
		this.editingCol = new ActionSetFieldCol();
		this.dt = dt;
		this.sce = sce;

		editingCol.boundName = col.boundName;
		editingCol.factField = col.factField;
		editingCol.header = col.header;
		editingCol.type = col.type;
		editingCol.valueList = col.valueList;
		editingCol.update = col.update;

		super.setModal(false);
		setTitle(constants.ColumnConfigurationSetAFieldOnAFact());



		HorizontalPanel pattern = new HorizontalPanel();
		pattern.add(bindingLabel);
		doBindingLabel();

		Image changePattern = new ImageButton("images/edit.gif", constants.ChooseABoundFactThatThisColumnPertainsTo(), new ClickListener() {
			public void onClick(Widget w) {
				showChangeFact(w);
			}
		});
		pattern.add(changePattern);
		addAttribute(constants.Fact(), pattern);


		HorizontalPanel field = new HorizontalPanel();
		field.add(fieldLabel);
		Image editField = new ImageButton("images/edit.gif", constants.EditTheFieldThatThisColumnOperatesOn(), new ClickListener() {
			public void onClick(Widget w) {
				showFieldChange();
			}
		});
		field.add(editField);
		addAttribute(constants.Field(), field);
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
		vl.add(new InfoPopup(constants.ValueList(), constants.ValueListsExplanation()));
		addAttribute(constants.optionalValueList(), vl);

		final TextBox header = new TextBox();
		header.setText(col.header);
		header.addChangeListener(new ChangeListener() {
			public void onChange(Widget w) {
				editingCol.header = header.getText();
			} });
		addAttribute(constants.ColumnHeaderDescription(), header);

		addAttribute(constants.UpdateEngineWithChanges(), doUpdate());


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
					dt.actionCols.add(editingCol);

				} else {
                    if (!col.header.equals(editingCol.header)) {
                        if (!unique(editingCol.header)) {
                            Window.alert(constants.ThatColumnNameIsAlreadyInUsePleasePickAnother());
                            return;
                        }
                    }
                    
					col.boundName = editingCol.boundName;
					col.factField = editingCol.factField;
					col.header = editingCol.header;
					col.type = editingCol.type;
					col.valueList = editingCol.valueList;
					col.update = editingCol.update;
				}
				refreshGrid.execute();
				hide();

			}
		});
		addAttribute("", apply);


	}

    private boolean unique(String header) {
        for (ActionCol o : dt.actionCols) {
            if (o.header.equals(header)) return false;
        }
        return true;
    }

    private Widget doUpdate() {
		HorizontalPanel hp = new HorizontalPanel();

		final CheckBox cb = new CheckBox();
		cb.setChecked(editingCol.update);
		cb.setText("");
		cb.addClickListener(new ClickListener() {
			public void onClick(Widget arg0) {
				editingCol.update = cb.isChecked();
			}
		});
		hp.add(cb);
		hp.add(new InfoPopup(constants.UpdateFact(), constants.UpdateDescription()));
		return hp;
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
		pop.addAttribute(constants.Field(), box);
		Button b = new Button(constants.OK());
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
			this.fieldLabel.setText(constants.pleaseChooseAFactPatternFirst());
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
		pop.addAttribute(constants.ChooseFact(), pats);
		Button ok = new Button(constants.OK());
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
			this.bindingLabel.setText(constants.pleaseChooseABoundFactForThisColumn());
		}
	}

}
