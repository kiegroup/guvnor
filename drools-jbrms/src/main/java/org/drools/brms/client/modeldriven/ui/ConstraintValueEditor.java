package org.drools.brms.client.modeldriven.ui;

import java.util.List;

import org.drools.brms.client.common.DirtyableComposite;
import org.drools.brms.client.common.FieldEditListener;
import org.drools.brms.client.common.FormStylePopup;
import org.drools.brms.client.common.InfoPopup;
import org.drools.brms.client.common.Lbl;
import org.drools.brms.client.modeldriven.brxml.SingleFieldConstraint;
import org.drools.brms.client.modeldriven.brxml.ISingleFieldConstraint;
import org.drools.brms.client.modeldriven.brxml.RuleModel;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * This is an editor for constraint values.
 * How this behaves depends on the constraint value type.
 * When the constraint value has no type, it will allow the user to choose the first time.
 * 
 * @author Michael Neale
 * @author Fernando Meyer
 */
public class ConstraintValueEditor extends DirtyableComposite {

    private ISingleFieldConstraint constraint;
    private Panel      panel;
    private RuleModel model;

    /**
     * @param con The constraint being edited.
     */
    public ConstraintValueEditor(ISingleFieldConstraint con, RuleModel model) {
        this.constraint = con;
        this.model = model;
        panel = new SimplePanel();
        refreshEditor();
        initWidget( panel );
    }

    private void refreshEditor() {
        panel.clear();

        if ( constraint.constraintValueType == SingleFieldConstraint.TYPE_UNDEFINED ) {
            Image clickme = new Image( "images/edit.gif" );
            clickme.addClickListener( new ClickListener() {
                public void onClick(Widget w) {
                    showTypeChoice( w, constraint );
                }
            } );
            panel.add( clickme );
        } else {
            switch ( constraint.constraintValueType ) {
                case SingleFieldConstraint.TYPE_LITERAL :
                    panel.add( literalEditor() );
                    break;
                case SingleFieldConstraint.TYPE_RET_VALUE :
                    panel.add( returnValueEditor() );
                    break;
                case SingleFieldConstraint.TYPE_VARIABLE :
                    panel.add( variableEditor() );
                    break;
                default :
                    break;
            }
        }
    }

    private Widget variableEditor() {
        List vars = this.model.getBoundVariablesInScope( this.constraint );
        
        final ListBox box = new ListBox();
        
        if (this.constraint.value == null) {
            box.addItem( "Choose ..." );
        }
        for ( int i = 0; i < vars.size(); i++ ) {
            String var = (String) vars.get( i );
            box.addItem( var );
            if (this.constraint.value != null && this.constraint.value.equals( var )) {
                box.setSelectedIndex( i );
            }
        }
        
        box.addChangeListener( new ChangeListener() {
            public void onChange(Widget w) {
                constraint.value = box.getItemText( box.getSelectedIndex() );
            }
        });
        
        return box;
    }

    /**
     * An editor for the retval "formula" (expression).
     */
    private Widget returnValueEditor() {
        TextBox box = boundTextBox(constraint);
        String msg = "This is a formula expression which will evaluate to a value.";
        Image img = new Image("images/function_assets.gif");
        img.setTitle( msg );
        box.setTitle( msg );
        Widget ed = widgets( img, box);
        return ed;
    }

    /**
     * An editor for literal values.
     */
    private TextBox literalEditor() {
        TextBox box = boundTextBox(constraint);
        box.setTitle( "This is a literal value. What is shown is what the field is checked against." );
        return box;
    }

    private TextBox boundTextBox(final ISingleFieldConstraint c) {
        final TextBox box = new TextBox();
        box.setStyleName( "constraint-value-Editor" );
        box.setText( c.value );
        if ( c.value == null || c.value.length() < 5 ) {
            box.setVisibleLength( 3 );
        } else {
            box.setVisibleLength( c.value.length() - 1 );
        }

        box.addChangeListener( new ChangeListener() {
            public void onChange(Widget w) {
                c.value = box.getText();
                makeDirty();
            }

        } );

        box.addKeyboardListener( new FieldEditListener( new Command() {
            public void execute() {
                box.setVisibleLength( box.getText().length() );
            }
        } ) );

        
        
        return box;
    }

    /**
     * Show a list of possibilities for the value type. 
     */
    private void showTypeChoice(Widget w, final ISingleFieldConstraint con) {
        final FormStylePopup form = new FormStylePopup( "images/newex_wiz.gif",
                                                        "Field value" );

        Button lit = new Button( "Literal value" );
        lit.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                con.constraintValueType = SingleFieldConstraint.TYPE_LITERAL;
                doTypeChosen( form );
            }

        } );
        form.addAttribute( "Literal value:", widgets( lit, new InfoPopup( "Literal",
                                                                          "A literal value means the " + "constraint is directly against the value that you type (ie. what you see on screen)." ) ) );
        
        
        

        form.addRow( new HTML( "<hr/>" ) );
        form.addRow( new Lbl( "Advanced options",
                              "weak-Text" ) );
        
        //only want to show variables if we have some !
        if (this.model.getBoundVariablesInScope( this.constraint ).size() > 0) {
            Button variable = new Button("Bound variable");
            variable.addClickListener( new ClickListener() {
                public void onClick(Widget w) {
                    con.constraintValueType = SingleFieldConstraint.TYPE_VARIABLE;
                    doTypeChosen( form );
                }
            });
            form.addAttribute( "A variable:", widgets( variable, new InfoPopup("A bound variable", "Will apply a constraint that compares a field to a bound variable.")) );
        }
        
        Button formula = new Button( "New formula" );
        formula.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                con.constraintValueType = SingleFieldConstraint.TYPE_RET_VALUE;
                doTypeChosen( form );
            }
        } );

        form.addAttribute( "A formula:", widgets( formula, new InfoPopup( "A formula",
                                                                          "A formula is an expression that calculates and returns a value " + ". That value is used to enforce the constraint." ) ) );

        form.setPopupPosition( w.getAbsoluteLeft(), w.getAbsoluteTop() );
        form.show();
    }

    private void doTypeChosen(final FormStylePopup form) {
        refreshEditor();
        form.hide();
    }

    private Panel widgets(Widget left, Widget right) {
        HorizontalPanel panel = new HorizontalPanel();
        panel.add( left );
        panel.add( right );
        panel.setWidth("100%");
        return panel;
    }

    public boolean isDirty() {
        return super.isDirty();
    }
    
    

}
