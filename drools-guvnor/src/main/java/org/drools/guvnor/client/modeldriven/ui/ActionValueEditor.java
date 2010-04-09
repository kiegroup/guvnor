package org.drools.guvnor.client.modeldriven.ui;

import java.util.List;

import org.drools.guvnor.client.common.DirtyableComposite;
import org.drools.guvnor.client.common.DropDownValueChanged;
import org.drools.guvnor.client.common.FieldEditListener;
import org.drools.guvnor.client.common.FormStylePopup;
import org.drools.guvnor.client.common.InfoPopup;
import org.drools.guvnor.client.common.SmallLabel;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.modeldriven.DropDownData;
import org.drools.guvnor.client.modeldriven.SuggestionCompletionEngine;
import org.drools.guvnor.client.modeldriven.brl.ActionFieldValue;
import org.drools.guvnor.client.modeldriven.brl.ActionInsertFact;
import org.drools.guvnor.client.modeldriven.brl.FactPattern;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.KeyboardListener;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * This provides for editing of fields in the RHS of a rule.
 * 
 * @author Michael Neale
 * 
 */
public class ActionValueEditor extends DirtyableComposite {

    private ActionFieldValue value;
    private DropDownData enums;
    private SimplePanel root;
    private Constants constants = GWT.create(Constants.class);
    private RuleModeller model = null;
    private String variableType = null;
    private boolean readOnly;
	private boolean template;

    public ActionValueEditor(final ActionFieldValue val,
            final DropDownData enums, boolean readOnly) {
        this(val, enums, null, null, readOnly, false);
    }

    public ActionValueEditor(final ActionFieldValue val,
            final DropDownData enums) {
        this(val, enums, false);
    }

    public ActionValueEditor(final ActionFieldValue val,
            final DropDownData enums,
            RuleModeller model,
            String variableType) {
        this(val, enums, model, variableType, false);
    }

    public ActionValueEditor(ActionFieldValue val, DropDownData enums,
			RuleModeller model, String type, boolean readOnly) {
    	this(val, enums, model, type, readOnly, false);
	}

    public ActionValueEditor(final ActionFieldValue val,
            final DropDownData enums,
            RuleModeller model,
            String variableType, boolean readOnly, boolean template) {

        this.readOnly = readOnly;

        if (val.type.equals(SuggestionCompletionEngine.TYPE_BOOLEAN)) {
            this.enums = DropDownData.create(new String[]{"true", "false"});
        } else {
            this.enums = enums;
        }
        this.root = new SimplePanel();
        this.value = val;
        this.model = model;
        this.variableType = variableType;
        this.template = template;
        refresh();
        initWidget(root);
    }

	private void refresh() {
        root.clear();
        if (enums != null && (enums.fixedList != null || enums.queryExpression != null)) {
            //enum
            Widget list = boundEnum(value);
            root.add(list);
        } else {
            // FIX nheron il faut ajouter les autres choix pour appeller les
            // bons editeurs suivant le type
            // si la valeur vaut 0 il faut mettre un stylo (

            if (value.value != null && value.value.length() > 0 && value.nature == ActionFieldValue.TYPE_UNDEFINED) {
                ///JBDS-894
                if (value.value.charAt(0) == '=') {
                    value.nature = ActionFieldValue.TYPE_VARIABLE;
                } else {
                    value.nature = ActionFieldValue.TYPE_LITERAL;
                }
            }
            if (value.nature == ActionFieldValue.TYPE_UNDEFINED) {
                // we have a blank slate..
                // have to give them a choice
                root.add(choice());
            } else {
                if (value.nature == ActionFieldValue.TYPE_VARIABLE) {
                    Widget list = boundVariable(value);
                    root.add(list);
                } else if(value.nature == ActionFieldValue.TYPE_TEMPLATE){
                	value.type = SuggestionCompletionEngine.TYPE_STRING;
                    Widget box = boundTextBox(this.value);
                    root.add(box);
                } else {
                    //formula and literal
                    Widget box = boundTextBox(this.value);
                    root.add(box);
                }

            }

        }
    }

    private Widget boundVariable(final ActionFieldValue c) {
        /*
         * If there is a bound variable that is the same type of the current
         * variable type, then propose a list
         */
        ListBox listVariable = new ListBox();
        List<String> vars = model.getModel().getBoundFacts();
        for (String v : vars) {
            FactPattern factPattern = model.getModel().getBoundFact(v);
            String fv = model.getModel().getFieldConstraint(v);

            if ((factPattern != null && factPattern.factType.equals(this.variableType)) || (fv != null)) {
                // First selection is empty
                if (listVariable.getItemCount() == 0) {
                    listVariable.addItem("...");
                }

                listVariable.addItem(v);
            }
        }
        /*
         * add the bound variable of the rhs
         */
        List<String> vars2 = model.getModel().getRhsBoundFacts();
        for (String v : vars2) {
            ActionInsertFact factPattern = model.getModel().getRhsBoundFact(v);
            if (factPattern.factType.equals(this.variableType)) {
                // First selection is empty
                if (listVariable.getItemCount() == 0) {
                    listVariable.addItem("...");
                }

                listVariable.addItem(v);
            }
        }
        if (value.value.equals("=")) {
            listVariable.setSelectedIndex(0);
        } else {
            for (int i = 0; i < listVariable.getItemCount(); i++) {
                if (listVariable.getItemText(i).equals(value.value.substring(1))) {
                    listVariable.setSelectedIndex(i);
                }
            }
        }
        if (listVariable.getItemCount() > 0) {

            listVariable.addChangeListener(new ChangeListener() {

                public void onChange(Widget arg0) {
                    ListBox w = (ListBox) arg0;
                    value.value = "=" + w.getValue(w.getSelectedIndex());
                    makeDirty();
                    refresh();
                }
            });
        }

        if (this.readOnly) {
            return new SmallLabel(listVariable.getItemText(listVariable.getSelectedIndex()));
        }

        return listVariable;
    }

    private Widget boundEnum(final ActionFieldValue c) {
        EnumDropDown enumDropDown = new EnumDropDown(value.value, new DropDownValueChanged() {

            public void valueChanged(String newText, String newValue) {
                value.value = newValue;
                makeDirty();
            }
        }, enums);

        if (this.readOnly) {
            return new SmallLabel(enumDropDown.getItemText(enumDropDown.getSelectedIndex()));
        } else {
            return enumDropDown;
        }
    }

    private Widget boundTextBox(final ActionFieldValue c) {
        final TextBox box = new TextBox();
        box.setStyleName("constraint-value-Editor");
        if (c.value == null) {
            box.setText("");
        } else {
            if (c.value.trim().equals("")) {
                c.value = "";
            }
            box.setText(c.value);
        }

        if (c.value == null || c.value.length() < 5) {
            box.setVisibleLength(6);
        } else {
            box.setVisibleLength(c.value.length() - 1);
        }

        box.addChangeListener(new ChangeListener() {

            public void onChange(Widget w) {
                c.value = box.getText();
                makeDirty();
            }
        });

        box.addKeyboardListener(new FieldEditListener(new Command() {

            public void execute() {
                box.setVisibleLength(box.getText().length());
            }
        }));

        if (value.type.equals(SuggestionCompletionEngine.TYPE_NUMERIC)) {
            box.addKeyboardListener(getNumericFilter(box));
        }

        if (this.readOnly) {
            return new SmallLabel(box.getText());
        }

        return box;
    }

    /**
     * This will return a keyboard listener for field setters, which will obey
     * numeric conventions - it will also allow formulas (a formula is when the
     * first value is a "=" which means it is meant to be taken as the user
     * typed)
     */
    public static KeyboardListener getNumericFilter(final TextBox box) {
        return new KeyboardListener() {

            public void onKeyDown(Widget arg0,
                    char arg1,
                    int arg2) {
            }

            public void onKeyPress(Widget w,
                    char c,
                    int i) {
                if (Character.isLetter(c) && c != '=' && !(box.getText().startsWith("="))) {
                    ((TextBox) w).cancelKey();
                }
            }

            public void onKeyUp(Widget arg0,
                    char arg1,
                    int arg2) {
            }
        };
    }

    private Widget choice() {
        if (this.readOnly) {
            return new HTML();
        } else {
            Image clickme = new Image("images/edit.gif");
            clickme.addClickListener(new ClickListener() {
                public void onClick(Widget w) {
                    showTypeChoice(w);
                }
            });
            return clickme;
        }
    }

    protected void showTypeChoice(Widget w) {
        final FormStylePopup form = new FormStylePopup("images/newex_wiz.gif",
                constants.FieldValue());
        Button lit = new Button(constants.LiteralValue());
        lit.addClickListener(new ClickListener() {

            public void onClick(Widget w) {
                value.nature = ActionFieldValue.TYPE_LITERAL;
                value.value = " ";
                makeDirty();
                refresh();
                form.hide();
            }
        });

        form.addAttribute(constants.LiteralValue() + ":",
                widgets(lit,
                new InfoPopup(constants.Literal(),
                constants.ALiteralValueMeansTheValueAsTypedInIeItsNotACalculation())));
        
        if(isTemplate()){
	        Button templateButton = new Button(constants.TemplateKey(), new ClickListener() {
	            public void onClick(Widget arg0) {
	                value.nature = ActionFieldValue.TYPE_TEMPLATE;
	                value.value = " ";
	                makeDirty();
	                refresh();
	                form.hide();
	            }
	        });
	        form.addAttribute(constants.TemplateKey() + ":",
	                widgets(templateButton,
	                new InfoPopup(constants.Literal(),
	                constants.ALiteralValueMeansTheValueAsTypedInIeItsNotACalculation())));
        }
        
        form.addRow(new HTML("<hr/>"));
        form.addRow(new SmallLabel(constants.AdvancedSection()));

        Button formula = new Button(constants.Formula());
        formula.addClickListener(new ClickListener() {

            public void onClick(Widget w) {
                value.nature = ActionFieldValue.TYPE_FORMULA;
                value.value = "=";
                makeDirty();
                refresh();
                form.hide();
            }
        });

        /*
         * If there is a bound variable that is the same type of the current
         * variable type, then show abutton
         */
        List<String> vars = model.getModel().getBoundFacts();
        List<String> vars2 = model.getModel().getRhsBoundFacts();
        for (String i : vars2) {
            vars.add(i);
        }
        for (String v : vars) {
            boolean createButton = false;
            Button variable = new Button(constants.BoundVariable());
            if (vars2.contains(v) == false) {
                FactPattern factPattern = model.getModel().getBoundFact(v);
                if (factPattern.factType.equals(this.variableType)) {
                    createButton = true;
                }
            } else {
                ActionInsertFact factPattern = model.getModel().getRhsBoundFact(v);
                if (factPattern.factType.equals(this.variableType)) {
                    createButton = true;
                }
            }
            if (createButton == true) {
                form.addAttribute(constants.BoundVariable() + ":",
                        variable);
                variable.addClickListener(new ClickListener() {

                    public void onClick(Widget w) {
                        value.nature = ActionFieldValue.TYPE_VARIABLE;
                        value.value = "=";
                        makeDirty();
                        refresh();
                        form.hide();
                    }
                });
                break;
            }
        }

        form.addAttribute(constants.Formula() + ":",
                widgets(formula,
                new InfoPopup(constants.Formula(),
                constants.FormulaTip())));

        // if (model != null){
        // for (int i=0;i< model.lhs.length;i++){
        // IPattern p = model.lhs[i];
        //        		
        // if (model.lhs[i].)
        // }
        // if (model.lhs.)
        //        	
        // }
        form.show();
    }

    private Widget widgets(Button lit,
            InfoPopup popup) {
        HorizontalPanel h = new HorizontalPanel();
        h.add(lit);
        h.add(popup);
        return h;
    }
    
    public boolean isTemplate(){
    	return this.template;
    }
}
