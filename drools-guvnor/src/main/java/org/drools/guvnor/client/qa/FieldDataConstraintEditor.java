package org.drools.guvnor.client.qa;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.*;
import com.gwtext.client.util.Format;
import org.drools.guvnor.client.common.*;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.modeldriven.DropDownData;
import org.drools.guvnor.client.modeldriven.SuggestionCompletionEngine;
import org.drools.guvnor.client.modeldriven.testing.ExecutionTrace;
import org.drools.guvnor.client.modeldriven.testing.FactData;
import org.drools.guvnor.client.modeldriven.testing.FieldData;
import org.drools.guvnor.client.modeldriven.testing.Scenario;
import org.drools.guvnor.client.modeldriven.ui.ActionValueEditor;
import org.drools.guvnor.client.modeldriven.ui.ConstraintValueEditor;

import java.util.List;
import java.util.Map;

/**
 * Constraint editor for the FieldData in the Given Section
 *
 * @author Nicolas Heron
 */

public class FieldDataConstraintEditor extends DirtyableComposite {

	private String factType;
	private FieldData field;
	private FactData givenFact;
	private final Panel panel;
	private Scenario scenario;
    private ExecutionTrace executionTrace;
	private SuggestionCompletionEngine sce;
	private ValueChanged callback;
	private Constants constants = ((Constants) GWT.create(Constants.class));

    public FieldDataConstraintEditor(String factType, ValueChanged callback,
			FieldData field,FactData givenFact, SuggestionCompletionEngine sce, Scenario scenario,ExecutionTrace exec) {
		this.field = field;
		this.sce = sce;
		this.factType = factType;
		this.callback = callback;
		this.scenario = scenario;
        this.executionTrace = exec;
		this.givenFact = givenFact;
		panel = new SimplePanel();
		refreshEditor();
		initWidget(panel);
	}

	private void refreshEditor() {
		String key = factType + "." + field.name;
		String flType = sce.fieldTypes.get(key);
		panel.clear();
		if (flType.equals(SuggestionCompletionEngine.TYPE_NUMERIC)) {
			final TextBox box = editableTextBox(callback, field.name,
					field.value);
			box.addKeyboardListener(ActionValueEditor.getNumericFilter(box));
			panel.add(box);
		} else if (flType.equals(SuggestionCompletionEngine.TYPE_BOOLEAN)) {
			String[] c = new String[] { "true", "false" };
			panel.add(ConstraintValueEditor.enumDropDown(field.value, callback,
					DropDownData.create(c)));
		} else {
			String[] enums = sce.dataEnumLists.get(key);
			if (enums != null) {
				panel.add(ConstraintValueEditor.enumDropDown(field.value,
						callback, DropDownData.create(enums)));

			} else {
                if (field.value != null && field.value.length() > 0 && field.nature == FieldData.TYPE_UNDEFINED ){
                    //  GUVNOR-337
                    if (field.value.charAt(0)=='='){
                       field.nature = FieldData.TYPE_VARIABLE;
                    } else {
                        field.nature =FieldData.TYPE_LITERAL;
                    }
                }
				if (field.nature == FieldData.TYPE_UNDEFINED
						&& isThereABoundVariableToSet() == true) {
					Image clickme = new Image("images/edit.gif"); // NON-NLS
					clickme.addClickListener(new ClickListener() {
						public void onClick(Widget w) {
							showTypeChoice(w, field);
						}
					});
					panel.add(clickme);
				} else if (field.nature == FieldData.TYPE_VARIABLE) {
					panel.add(variableEditor());
				} else {
					panel
							.add(editableTextBox(callback, field.name,
									field.value));
				}
			}
		}

	}

	private static TextBox editableTextBox(final ValueChanged changed,
			String fieldName, String initialValue) {
		// Fixme nheron
		final TextBox tb = new TextBox();
		tb.setText(initialValue);
		String m = Format.format(((Constants) GWT.create(Constants.class))
				.ValueFor0(), fieldName);
		tb.setTitle(m);
		tb.addChangeListener(new ChangeListener() {
			public void onChange(Widget w) {
				changed.valueChanged(tb.getText());
			}
		});

		return tb;
	}

	private Widget variableEditor() {
		// sce.
		List vars = this.scenario.getFactNamesInScope(
				this.executionTrace,true);

		final ListBox box = new ListBox();

		if (this.field.value == null) {
			box.addItem(constants.Choose());
		}
		int j=0;
		for (int i = 0; i < vars.size(); i++) {
			String var = (String) vars.get(i);
			Map m = this.scenario.getVariableTypes();
			FactData f = (FactData) this.scenario.getFactTypes().get(var);
			String fieldType = sce.getFieldType(this.factType, field.name);
			if (f.type.equals(fieldType)) {
				if (box.getItemCount() == 0) {
					box.addItem("...");
				    j++;
				}
				box.addItem("="+var);
				if (this.field.value != null && this.field.value.equals("="+var)) {
					box.setSelectedIndex(j);

				}
                j++;
			}
		}

		box.addChangeListener(new ChangeListener() {
			public void onChange(Widget w) {
				field.value = box.getItemText(box.getSelectedIndex());
			}
		});

		return box;
	}

	private void showTypeChoice(Widget w, final FieldData con) {
		final FormStylePopup form = new FormStylePopup("images/newex_wiz.gif",
				constants.FieldValue());

		Button lit = new Button(constants.LiteralValue());
		lit.addClickListener(new ClickListener() {
			public void onClick(Widget w) {
				con.nature = FieldData.TYPE_LITERAL;
				doTypeChosen(form);
			}

		});
		form.addAttribute(constants.LiteralValue() + ":", widgets(lit,
				new InfoPopup(constants.LiteralValue(), constants
						.LiteralValTip())));

		form.addRow(new HTML("<hr/>"));
		form.addRow(new SmallLabel(constants.AdvancedOptions()));

		// If we are here, then there must be a bound variable compatible with
		// me

		Button variable = new Button(constants.BoundVariable());
		variable.addClickListener(new ClickListener() {
			public void onClick(Widget w) {
				con.nature = FieldData.TYPE_VARIABLE;
				doTypeChosen(form);
			}
		});
		form.addAttribute(constants.AVariable(), widgets(variable,
				new InfoPopup(constants.ABoundVariable(), constants
						.BoundVariableTip())));

		form.show();
	}

	private boolean isThereABoundVariableToSet() {
		boolean retour = false;
		List vars = scenario.getFactNamesInScope(
				this.executionTrace,true);
		if (vars.size() > 0) {
			for (int i = 0; i < vars.size(); i++) {
				String var = (String) vars.get(i);
				Map m = this.scenario.getVariableTypes();
				FactData f = (FactData) scenario.getFactTypes().get(var);
				String fieldType = sce.getFieldType(this.factType, field.name);
				if (f.type.equals(fieldType)) {
					retour = true;
					break;
				}
			}
		}
		return retour;
	}

	private void doTypeChosen(final FormStylePopup form) {
		refreshEditor();
		form.hide();
	}

	private Panel widgets(Widget left, Widget right) {
		HorizontalPanel panel = new HorizontalPanel();
		panel.add(left);
		panel.add(right);
		panel.setWidth("100%");
		return panel;
	}

}