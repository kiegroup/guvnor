package org.drools.guvnor.client.modeldriven.ui;

import org.drools.guvnor.client.common.DirtyableComposite;
import org.drools.guvnor.client.common.FieldEditListener;
import org.drools.guvnor.client.common.FormStylePopup;
import org.drools.guvnor.client.common.InfoPopup;
import org.drools.guvnor.client.common.SmallLabel;
import org.drools.guvnor.client.common.ValueChanged;
import org.drools.guvnor.client.modeldriven.DropDownData;
import org.drools.guvnor.client.modeldriven.SuggestionCompletionEngine;
import org.drools.guvnor.client.modeldriven.brl.ActionFieldValue;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.KeyboardListener;
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

	public ActionValueEditor(final ActionFieldValue val, final DropDownData enums) {
		if (val.type.equals(SuggestionCompletionEngine.TYPE_BOOLEAN)) {
			this.enums = DropDownData.create(new String[] {"true", "false" });
		} else {
			this.enums = enums;
		}
		this.root = new SimplePanel();
		this.value = val;

		refresh();
		initWidget(root);
	}

	private void refresh() {
		root.clear();
		if (enums != null && (enums.fixedList != null || enums.queryExpression != null)) {
			root.add(ConstraintValueEditor.enumDropDown(value.value,
					new ValueChanged() {
						public void valueChanged(String newValue) {
							value.value = newValue;
							makeDirty();
						}
					}, enums));
		} else {
			if (value.value == null || "".equals(value.value)) {
				// we have a blank slate..
				// have to give them a choice
				root.add(choice());
			} else {
				TextBox box = boundTextBox(this.value);
				root.add(box);
			}

		}
	}

    private TextBox boundTextBox(final ActionFieldValue c) {
        final TextBox box = new TextBox();
        box.setStyleName( "constraint-value-Editor" );
        if (c.value == null) {
        	box.setText("");
        } else {
        	if (c.value.trim().equals("")) {
        		c.value = "";
        	}
        	box.setText( c.value );
        }


        if (c.value == null || c.value.length() < 5 ) {
            box.setVisibleLength( 6 );
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

        if (value.type.equals( SuggestionCompletionEngine.TYPE_NUMERIC )) {
            box.addKeyboardListener( getNumericFilter( box ));
        }

        return box;
    }

    /**
     * This will return a keyboard listener for field setters, which
     * will obey numeric conventions - it will also allow formulas
     * (a formula is when the first value is a "=" which means
     * it is meant to be taken as the user typed)
     */
    public static KeyboardListener getNumericFilter(final TextBox box) {
        return new KeyboardListener() {

            public void onKeyDown(Widget arg0, char arg1, int arg2) {

            }

            public void onKeyPress(Widget w, char c, int i) {
                if (Character.isLetter( c ) && c != '='
                    && !(box.getText().startsWith( "=" ))) {
                    ((TextBox) w).cancelKey();
                }
            }

            public void onKeyUp(Widget arg0, char arg1, int arg2) {
            }

        };
    }

	private Widget choice() {
		Image clickme = new Image( "images/edit.gif" );
		clickme.addClickListener(new ClickListener() {
			public void onClick(Widget w) {
				showTypeChoice(w);
			}
		});
		return clickme;
	}


	protected void showTypeChoice(Widget w) {
        final FormStylePopup form = new FormStylePopup( "images/newex_wiz.gif", "Field value" );
        Button lit = new Button( "Literal value" );
        lit.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                value.value = " ";
                makeDirty();
                refresh();
                form.hide();
            }

        } );
        form.addAttribute( "Literal value:", widgets( lit, new InfoPopup( "Literal",
                                                                          "A literal value means the " + "constraint is directly against the value that you type (ie. what you see on screen)." ) ) );
        form.addRow( new HTML( "<hr/>" ) );
        form.addRow( new SmallLabel( "<i>Advanced</i>" ) );

        Button formula = new Button("Formula");
        formula.addClickListener(new ClickListener() {

			public void onClick(Widget w) {
				value.value = "=";
				makeDirty();
				refresh();
				form.hide();
			}

        });

        form.addAttribute("Formula:", widgets(formula, new InfoPopup("Formula", "A formula is used when values are calculated, or a variable is used.")));
        form.show();
	}

	private Widget widgets(Button lit, InfoPopup popup) {
		HorizontalPanel h = new HorizontalPanel();
		h.add(lit);
		h.add(popup);
		return h;
	}



}
