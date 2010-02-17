package org.drools.guvnor.client.modeldriven.ui.factPattern;

import org.drools.guvnor.client.common.FormStylePopup;
import org.drools.guvnor.client.common.InfoPopup;
import org.drools.guvnor.client.common.SmallLabel;
import org.drools.guvnor.client.modeldriven.FieldAccessorsAndMutators;
import org.drools.guvnor.client.modeldriven.SuggestionCompletionEngine;
import org.drools.guvnor.client.modeldriven.brl.CompositeFieldConstraint;
import org.drools.guvnor.client.modeldriven.brl.FactPattern;
import org.drools.guvnor.client.modeldriven.brl.FieldConstraint;
import org.drools.guvnor.client.modeldriven.brl.SingleFieldConstraint;
import org.drools.guvnor.client.modeldriven.ui.RuleModeller;
import org.drools.guvnor.client.messages.Constants;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.core.client.GWT;
import com.gwtext.client.util.Format;

public class PopupCreator {

    private FactPattern pattern;
    private SuggestionCompletionEngine completions;
    private RuleModeller modeller;
    private boolean bindable;
    private Constants constants = ((Constants) GWT.create(Constants.class));

    /**
     * Returns the pattern.
     */
    public FactPattern getPattern() {
        return pattern;
    }

    /**
     * @param pattern the pattern to set
     */
    public void setPattern(FactPattern pattern) {
        this.pattern = pattern;
    }

    /**
     * Returns the completions.
     */
    public SuggestionCompletionEngine getCompletions() {
        return completions;
    }

    /**
     * @param completions the completions to set
     */
    public void setCompletions(SuggestionCompletionEngine completions) {
        this.completions = completions;
    }

    /**
     * Returns the modeller.
     */
    public RuleModeller getModeller() {
        return modeller;
    }

    /**
     * @param modeller the modeller to set
     */
    public void setModeller(RuleModeller modeller) {
        this.modeller = modeller;
    }

    /**
     * Returns the bindable.
     */
    public boolean isBindable() {
        return bindable;
    }

    /**
     * @param bindable the bindable to set
     */
    public void setBindable(boolean bindable) {
        this.bindable = bindable;
    }

    /**
     * Display a little editor for field bindings.
     */
    public void showBindFieldPopup(final Widget w, final SingleFieldConstraint con, String[] fields, final PopupCreator popupCreator) {


        final FormStylePopup popup = new FormStylePopup();
        popup.setWidth(500);
        final HorizontalPanel vn = new HorizontalPanel();
        final TextBox varName = new TextBox();
        final Button ok = new Button(constants.Set());
        vn.add(varName);
        vn.add(ok);

        ok.addClickListener(new ClickListener() {

            public void onClick(Widget w) {
                String var = varName.getText();
                if (modeller.isVariableNameUsed(var)) {
                    Window.alert(Format.format(constants.TheVariableName0IsAlreadyTaken(), var));
                    return;
                }
                con.fieldBinding = var;
                modeller.refreshWidget();
                popup.hide();
            }
        });
        popup.addAttribute(Format.format(constants.BindTheFieldCalled0ToAVariable(), con.fieldName), vn);
        if (fields != null) {
            Button sub = new Button(constants.ShowSubFields());
            popup.addAttribute(Format.format(constants.ApplyAConstraintToASubFieldOf0(), con.fieldName), sub);
            sub.addClickListener(new ClickListener() {

                public void onClick(Widget sender) {
                    popup.hide();
                    popupCreator.showPatternPopup(w, con.fieldType, con);
                }
            });
        }

        popup.show();
    }

    /**
     * This shows a popup for adding fields to a composite
     */
    public void showPatternPopupForComposite(Widget w, final CompositeFieldConstraint composite) {
        final FormStylePopup popup = new FormStylePopup("images/newex_wiz.gif", //NON-NLS
                constants.AddFieldsToThisConstraint());

        final ListBox box = new ListBox();
        box.addItem("...");
        String[] fields = this.completions.getFieldCompletions(this.pattern.factType);
        for (int i = 0; i < fields.length; i++) {
            box.addItem(fields[i]);
        }

        box.setSelectedIndex(0);

        box.addChangeListener(new ChangeListener() {

            public void onChange(Widget w) {
                composite.addConstraint(new SingleFieldConstraint(box.getItemText(box.getSelectedIndex())));
                modeller.refreshWidget();
                popup.hide();
            }
        });
        popup.addAttribute(constants.AddARestrictionOnAField(), box);


        final ListBox composites = new ListBox();
        composites.addItem("..."); //NON-NLS
        composites.addItem(constants.AllOfAnd(), CompositeFieldConstraint.COMPOSITE_TYPE_AND);
        composites.addItem(constants.AnyOfOr(), CompositeFieldConstraint.COMPOSITE_TYPE_OR);
        composites.setSelectedIndex(0);

        composites.addChangeListener(new ChangeListener() {

            public void onChange(Widget w) {
                CompositeFieldConstraint comp = new CompositeFieldConstraint();
                comp.compositeJunctionType = composites.getValue(composites.getSelectedIndex());
                composite.addConstraint(comp);
                modeller.refreshWidget();
                popup.hide();
            }
        });

        InfoPopup infoComp = new InfoPopup(constants.MultipleFieldConstraints(), constants.MultipleConstraintsTip());

        HorizontalPanel horiz = new HorizontalPanel();
        horiz.add(composites);
        horiz.add(infoComp);
        popup.addAttribute(constants.MultipleFieldConstraint(), horiz);

        popup.show();

    }

    /**
     * This shows a popup allowing you to add field constraints to a pattern (its a popup).
     */
    public void showPatternPopup(Widget w, final String factType, final FieldConstraint con) {

        String title = (con == null) ? Format.format(constants.ModifyConstraintsFor0(), factType) : constants.AddSubFieldConstraint();
        final FormStylePopup popup = new FormStylePopup("images/newex_wiz.gif", //NON-NLS
                title);

        final ListBox box = new ListBox();
        box.addItem("...");
        String[] fields = this.completions.getFieldCompletions( FieldAccessorsAndMutators.ACCESSOR,
                                                                factType );
        for (int i = 0; i < fields.length; i++) {
            box.addItem(fields[i]);
        }

        box.setSelectedIndex(0);

        box.addChangeListener(new ChangeListener() {

            public void onChange(Widget w) {
                String fieldName = box.getItemText(box.getSelectedIndex());
                String qualifiedName = factType + "." + fieldName;
                String fieldType = (String) completions.getFieldType(qualifiedName);
                pattern.addConstraint(new SingleFieldConstraint(fieldName, fieldType, con));
                modeller.refreshWidget();
                popup.hide();
            }
        });
        popup.addAttribute(constants.AddARestrictionOnAField(), box);


        final ListBox composites = new ListBox();
        composites.addItem("...");
        composites.addItem(constants.AllOfAnd(), CompositeFieldConstraint.COMPOSITE_TYPE_AND);
        composites.addItem(constants.AnyOfOr(), CompositeFieldConstraint.COMPOSITE_TYPE_OR);
        composites.setSelectedIndex(0);

        composites.addChangeListener(new ChangeListener() {

            public void onChange(Widget w) {
                CompositeFieldConstraint comp = new CompositeFieldConstraint();
                comp.compositeJunctionType = composites.getValue(composites.getSelectedIndex());
                pattern.addConstraint(comp);
                modeller.refreshWidget();
                popup.hide();
            }
        });

        InfoPopup infoComp = new InfoPopup(constants.MultipleFieldConstraints(), constants.MultipleConstraintsTip1());

        HorizontalPanel horiz = new HorizontalPanel();

        horiz.add(composites);
        horiz.add(infoComp);
        if (con == null) {
            popup.addAttribute(constants.MultipleFieldConstraint(), horiz);
        }


        //popup.addRow( new HTML("<hr/>") );
        if (con == null) {
            popup.addRow(new SmallLabel("<i>" + constants.AdvancedOptionsColon() + "</i>")); //NON-NLS
            final Button predicate = new Button(constants.NewFormula());
            predicate.addClickListener(new ClickListener() {

                public void onClick(Widget w) {
                    SingleFieldConstraint con = new SingleFieldConstraint();
                    con.constraintValueType = SingleFieldConstraint.TYPE_PREDICATE;
                    pattern.addConstraint(con);
                    modeller.refreshWidget();
                    popup.hide();
                }
            });
            popup.addAttribute(constants.AddANewFormulaStyleExpression(), predicate);

            doBindingEditor(popup);
        }

        popup.show();
    }

    /**
     * This adds in (optionally) the editor for changing the bound variable name.
     * If its a bindable pattern, it will show the editor,
     * if it is already bound, and the name is used, it should
     * not be editable.
     */
    private void doBindingEditor(final FormStylePopup popup) {
        if (bindable && !(modeller.getModel().isBoundFactUsed(pattern.boundName))) {
            HorizontalPanel varName = new HorizontalPanel();
            final TextBox varTxt = new TextBox();
            if (pattern.boundName == null) {
                varTxt.setText("");
            } else {
                varTxt.setText(pattern.boundName);
            }

            varTxt.setVisibleLength(6);
            varName.add(varTxt);

            Button bindVar = new Button(constants.Set());
            bindVar.addClickListener(new ClickListener() {

                public void onClick(Widget w) {
                    String var = varTxt.getText();
                    if (modeller.isVariableNameUsed(var)) {
                        Window.alert(Format.format(constants.TheVariableName0IsAlreadyTaken(), var));
                        return;
                    }
                    pattern.boundName = varTxt.getText();
                    modeller.refreshWidget();
                    popup.hide();
                }
            });

            varName.add(bindVar);
            popup.addAttribute(constants.VariableName(), varName);

        }
    }
}
