package org.drools.guvnor.client.modeldriven.ui.factPattern;

import org.drools.guvnor.client.common.FormStylePopup;
import org.drools.guvnor.client.common.InfoPopup;
import org.drools.guvnor.client.common.SmallLabel;
import org.drools.guvnor.client.modeldriven.SuggestionCompletionEngine;
import org.drools.guvnor.client.modeldriven.brl.CompositeFieldConstraint;
import org.drools.guvnor.client.modeldriven.brl.FactPattern;
import org.drools.guvnor.client.modeldriven.brl.FieldConstraint;
import org.drools.guvnor.client.modeldriven.brl.SingleFieldConstraint;
import org.drools.guvnor.client.modeldriven.ui.RuleModeller;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class PopupCreator {
    
    private FactPattern                pattern;
    private SuggestionCompletionEngine completions;
    private RuleModeller               modeller;
    private boolean                    bindable;

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
        final Button ok = new Button("Set");
        vn.add( varName );
        vn.add( ok );

        ok.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                String var = varName.getText();
                if (modeller.isVariableNameUsed( var )) {
                    Window.alert( "The variable name [" + var + "] is already taken.");
                    return;
                }
                con.fieldBinding = var;
                modeller.refreshWidget();
                popup.hide();
            }
        } );
        popup.addAttribute("Bind the field called [" + con.fieldName + "] to a variable:", vn );
        if (fields != null) {
            Button sub = new Button("Show sub fields...");
            popup.addAttribute("Apply a constraint to a sub-field of [" + con.fieldName + "]:", sub);
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
        final FormStylePopup popup = new FormStylePopup( "images/newex_wiz.gif",
                                                         "Add fields to this constraint" );

        final ListBox box = new ListBox();
        box.addItem( "..." );
        String[] fields = this.completions.getFieldCompletions( this.pattern.factType );
        for ( int i = 0; i < fields.length; i++ ) {
            box.addItem( fields[i] );
        }

        box.setSelectedIndex( 0 );

        box.addChangeListener( new ChangeListener() {
            public void onChange(Widget w) {
                composite.addConstraint( new SingleFieldConstraint( box.getItemText( box.getSelectedIndex() ) ) );
                modeller.refreshWidget();
                popup.hide();
            }
        } );
        popup.addAttribute( "Add a restriction on a field", box );


        final ListBox composites = new ListBox();
        composites.addItem("...");
        composites.addItem( "All of (And)", CompositeFieldConstraint.COMPOSITE_TYPE_AND );
        composites.addItem( "Any of (Or)", CompositeFieldConstraint.COMPOSITE_TYPE_OR );
        composites.setSelectedIndex( 0 );

        composites.addChangeListener( new ChangeListener() {
            public void onChange(Widget w) {
                CompositeFieldConstraint comp = new CompositeFieldConstraint();
                comp.compositeJunctionType = composites.getValue( composites.getSelectedIndex() );
                composite.addConstraint( comp );
                modeller.refreshWidget();
                popup.hide();
            }
        });

        InfoPopup infoComp = new InfoPopup("Multiple field constraints", "You can specify constraints that span multiple fields (and more). The results of all these constraints can be combined with a 'and' or an 'or' logically." +
                "You can also have other multiple field constraints nested inside these restrictions.");

        HorizontalPanel horiz = new HorizontalPanel();
        horiz.add( composites );
        horiz.add( infoComp );
        popup.addAttribute( "Multiple field constraint", horiz );

        popup.show();

    }

    /**
     * This shows a popup allowing you to add field constraints to a pattern (its a popup).
     */
    public void showPatternPopup(Widget w, final String factType, final FieldConstraint con) {

        String title = (con == null) ? "Modify constraints for " + factType : "Add sub-field constraint";
        final FormStylePopup popup = new FormStylePopup( "images/newex_wiz.gif",
                                                          title );

        final ListBox box = new ListBox();
        box.addItem( "..." );
        String[] fields = this.completions.getFieldCompletions( factType );
        for ( int i = 0; i < fields.length; i++ ) {
            box.addItem( fields[i] );
        }

        box.setSelectedIndex( 0 );

        box.addChangeListener( new ChangeListener() {
            public void onChange(Widget w) {
                String fieldName = box.getItemText( box.getSelectedIndex() );
                String qualifiedName = factType + "." + fieldName;
                String fieldType = (String) completions.fieldTypes.get(qualifiedName);
                pattern.addConstraint( new SingleFieldConstraint( fieldName, fieldType, con ) );
                modeller.refreshWidget();
                popup.hide();
            }
        } );
        popup.addAttribute( "Add a restriction on a field", box );


        final ListBox composites = new ListBox();
        composites.addItem("...");
        composites.addItem( "All of (And)", CompositeFieldConstraint.COMPOSITE_TYPE_AND );
        composites.addItem( "Any of (Or)", CompositeFieldConstraint.COMPOSITE_TYPE_OR );
        composites.setSelectedIndex( 0 );

        composites.addChangeListener( new ChangeListener() {
            public void onChange(Widget w) {
                CompositeFieldConstraint comp = new CompositeFieldConstraint();
                comp.compositeJunctionType = composites.getValue( composites.getSelectedIndex() );
                pattern.addConstraint( comp );
                modeller.refreshWidget();
                popup.hide();
            }
        });

        InfoPopup infoComp = new InfoPopup("Multiple field constraints", "You can specify constraints that span multiple fields (and more). The results of all these constraints can be combined with a 'and' or an 'or' logically." +
                "You can also have other multiple field constraints nested inside these restrictions.");

        HorizontalPanel horiz = new HorizontalPanel();

        horiz.add( composites );
        horiz.add( infoComp );
        if (con == null) {
            popup.addAttribute( "Multiple field constraint", horiz );
        }


        //popup.addRow( new HTML("<hr/>") );
        if (con == null) {
            popup.addRow( new SmallLabel("<i>Advanced options:</i>") );
            final Button predicate = new Button( "New formula" );
            predicate.addClickListener( new ClickListener() {
                public void onClick(Widget w) {
                    SingleFieldConstraint con = new SingleFieldConstraint();
                    con.constraintValueType = SingleFieldConstraint.TYPE_PREDICATE;
                    pattern.addConstraint( con );
                    modeller.refreshWidget();
                    popup.hide();
                }
            } );
            popup.addAttribute( "Add a new formula style expression", predicate );

            doBindingEditor( popup );
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
        if ( bindable && !(modeller.getModel().isBoundFactUsed( pattern.boundName )) ) {
            HorizontalPanel varName = new HorizontalPanel();
            final TextBox varTxt = new TextBox();
            if (pattern.boundName == null) {
                varTxt.setText("");
            }else {
                varTxt.setText( pattern.boundName );
            }

            varTxt.setVisibleLength( 6 );
            varName.add( varTxt );

            Button bindVar = new Button( "Set" );
            bindVar.addClickListener( new ClickListener() {
                public void onClick(Widget w) {
                    String var = varTxt.getText();
                    if (modeller.isVariableNameUsed( var )) {
                        Window.alert( "The variable name [" + var + "] is already taken." );
                        return;
                    }
                    pattern.boundName = varTxt.getText();
                    modeller.refreshWidget();
                    popup.hide();
                }
            } );

            varName.add( bindVar );
            popup.addAttribute( "Variable name", varName );

        }
    }
}
