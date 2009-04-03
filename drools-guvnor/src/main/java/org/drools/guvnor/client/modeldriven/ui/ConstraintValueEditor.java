package org.drools.guvnor.client.modeldriven.ui;
/*
 * Copyright 2005 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



import java.util.List;

import org.drools.guvnor.client.common.DirtyableComposite;
import org.drools.guvnor.client.common.FieldEditListener;
import org.drools.guvnor.client.common.FormStylePopup;
import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.common.InfoPopup;
import org.drools.guvnor.client.common.LoadingPopup;
import org.drools.guvnor.client.common.SmallLabel;
import org.drools.guvnor.client.common.ValueChanged;
import org.drools.guvnor.client.modeldriven.DropDownData;
import org.drools.guvnor.client.modeldriven.SuggestionCompletionEngine;
import org.drools.guvnor.client.modeldriven.brl.FactPattern;
import org.drools.guvnor.client.modeldriven.brl.ISingleFieldConstraint;
import org.drools.guvnor.client.modeldriven.brl.RuleModel;
import org.drools.guvnor.client.modeldriven.brl.SingleFieldConstraint;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;
import org.drools.guvnor.client.messages.Constants;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.KeyboardListener;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.core.client.GWT;

/**
 * This is an editor for constraint values.
 * How this behaves depends on the constraint value type.
 * When the constraint value has no type, it will allow the user to choose the first time.
 *
 * @author Michael Neale
 * @author Fernando Meyer
 */
public class ConstraintValueEditor extends DirtyableComposite {

    private final ISingleFieldConstraint constraint;
    private final Panel      panel;
    private final RuleModel model;
    private final boolean numericValue;
    private DropDownData dropDownData;
    private Constants constants = ((Constants) GWT.create(Constants.class));
    private String fieldType;
    //private String[] enumeratedValues;

    /**
     * @param con The constraint being edited.
     */
    public ConstraintValueEditor(FactPattern pattern, String fieldName, ISingleFieldConstraint con, RuleModeller modeller, String valueType /* eg is numeric */) {
        this.constraint = con;
        valueType = modeller.getSuggestionCompletions().getFieldType(pattern.factType, fieldName);
        this.fieldType = valueType;
        SuggestionCompletionEngine sce = modeller.getSuggestionCompletions();
        if ( SuggestionCompletionEngine.TYPE_NUMERIC.equals(valueType)) {
            this.numericValue = true;
        } else {
            this.numericValue = false;
        }
        if (SuggestionCompletionEngine.TYPE_BOOLEAN.equals(valueType)) {
            this.dropDownData = DropDownData.create(new String[] {"true", "false" }); //NON-NLS
        } else {
        	this.dropDownData = sce.getEnums(pattern, fieldName);
        }
        this.model = modeller.getModel();




        panel = new SimplePanel();
        refreshEditor();
        initWidget( panel );

    }

    private void refreshEditor() {
        panel.clear();

        if ( constraint.constraintValueType == SingleFieldConstraint.TYPE_UNDEFINED ) {
            Image clickme = new Image( "images/edit.gif" );     //NON-NLS
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
            box.addItem( constants.Choose() );
        }
        for ( int i = 0; i < vars.size(); i++ ) {
            String var = (String) vars.get( i );
            FactPattern f= model.getBoundFact(var);
            if (f.factType.equals(this.fieldType)) {
				box.addItem(var);
				if (this.constraint.value != null
						&& this.constraint.value.equals(var)) {
					box.setSelectedIndex(i);
				}
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
        String msg = constants.FormulaEvaluateToAValue();
        Image img = new Image("images/function_assets.gif"); //NON-NLS
        img.setTitle( msg );
        box.setTitle( msg );
        Widget ed = widgets( img, box);
        return ed;
    }

    /**
     * An editor for literal values.
     */
    private Widget literalEditor() {

        //use a drop down if we have a fixed list
        if (this.dropDownData != null) {
	            return enumDropDown(constraint.value, new ValueChanged() {
	                public void valueChanged(String newValue) {
	                    constraint.value = newValue;
	                }
	            }, this.dropDownData);

        } else {

            final TextBox box = boundTextBox(constraint);

            if (this.numericValue) {
                box.addKeyboardListener( new KeyboardListener() {

                    public void onKeyDown(Widget arg0, char arg1, int arg2) {

                    }

                    public void onKeyPress(Widget w, char c, int i) {
                        if (Character.isLetter( c ) ) {
                            ((TextBox) w).cancelKey();
                        }
                    }

                    public void onKeyUp(Widget arg0, char arg1, int arg2) {
                    }

                } );
            }

            box.setTitle(constants.LiteralValueTip());
            return box;
        }
    }

    /**
     * This will do a drop down for enumerated values..
     */
    public static Widget enumDropDown(final String currentValue, final ValueChanged valueChanged,
                                final DropDownData dropData) {
        final ListBox box = new ListBox();
        final Constants cs =  GWT.create(Constants.class);

        //if we have to do it lazy, we will hit up the server when the widget gets focus
        if (dropData.fixedList == null && dropData.queryExpression != null) {
			DeferredCommand.addCommand(new Command() {
				public void execute() {
					LoadingPopup.showMessage(cs.RefreshingList());
					RepositoryServiceFactory.getService().loadDropDownExpression(dropData.valuePairs, dropData.queryExpression, new GenericCallback() {
						public void onSuccess(Object data) {
							LoadingPopup.close();
							String[] list = (String[]) data;
							doDropDown(currentValue, list, box);
						}

						public void onFailure(Throwable t) {
							LoadingPopup.close();
							//just do an empty drop down...
							doDropDown(currentValue, new String[] {cs.UnableToLoadList()}, box);
						}
					});
				}
			});

//	        box.addFocusListener(new FocusListener() {
//
//
//				public void onFocus(Widget w) {
//					DeferredCommand.addCommand(new Command() {
//						public void execute() {
//							LoadingPopup.showMessage("Refreshing list...");
//							RepositoryServiceFactory.getService().loadDropDownExpression(dropData.valuePairs, dropData.queryExpression, new GenericCallback() {
//								public void onSuccess(Object data) {
//									LoadingPopup.close();
//									String[] list = (String[]) data;
//									doDropDown(currentValue, list, box);
//								}
//							});
//						}
//					});
//				}
//				public void onLostFocus(Widget w) {}
//	        });
        } else {
        	//otherwise its just a normal one...
        	doDropDown(currentValue, dropData.fixedList, box);
        }


        box.addChangeListener( new ChangeListener() {
            public void onChange(Widget w) {
                valueChanged.valueChanged( box.getValue( box.getSelectedIndex() ) );
                //constraint.value = box.getValue( box.getSelectedIndex() );
            }
        });

        if (currentValue == null || "".equals( currentValue )) {
        	int ix = box.getSelectedIndex();
        	if (ix > -1) {
	            String val = box.getValue(ix);
	            valueChanged.valueChanged(val);
        	}
        }

        return box;
    }

//    /**
//     * This will do a drop down for enumerated values..
//     */
//    public static Widget enumDropDown(//final ISingleFieldConstraint constraint,
//                                       final String currentValue, final ValueChanged valueChanged,
//                                final String[] enumeratedValues) {
//        final ListBox box = new ListBox();
//
//        if (currentValue == null || "".equals( currentValue )) {
//            box.addItem( "Choose ..." );
//        }
//
//        doDropDown(currentValue, enumeratedValues, box);
//
//        box.addChangeListener( new ChangeListener() {
//            public void onChange(Widget w) {
//                valueChanged.valueChanged( box.getValue( box.getSelectedIndex() ) );
//                //constraint.value = box.getValue( box.getSelectedIndex() );
//            }
//        });
//        return box;
//    }

	private static void doDropDown(final String currentValue,
			final String[] enumeratedValues, final ListBox box) {
		boolean selected = false;

		box.clear();

        for ( int i = 0; i < enumeratedValues.length; i++ ) {
            String v = enumeratedValues[i];
            String val;
            if (v.indexOf( '=' ) > 0) {
                //using a mapping
                String[] splut = ConstraintValueEditorHelper.splitValue(v);
                String realValue = splut[0];
                String display = splut[1];
                val = realValue;
                box.addItem( display, realValue );
            } else {
                box.addItem( v, v );
                val = v;
            }
            if (currentValue != null && currentValue.equals( val )) {
                box.setSelectedIndex( i );
                selected = true;
            }
        }

        if (currentValue != null  && !"".equals(currentValue) && !selected) {
            //need to add this value
            box.addItem( currentValue, currentValue );
            box.setSelectedIndex( enumeratedValues.length );
        }
	}



    private TextBox boundTextBox(final ISingleFieldConstraint c) {
        final TextBox box = new TextBox();
        box.setStyleName( "constraint-value-Editor" );    //NON-NLS
        if (c.value == null) {
        	box.setText("");
        } else {
        	box.setText( c.value );
        }


        String v = c.value;
        if (c.value == null || v.length() < 7 ) {
            box.setVisibleLength( 8 );
        } else {
            box.setVisibleLength( v.length() + 1 );
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
                constants.FieldValue());

        Button lit = new Button(constants.LiteralValue());
        lit.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                con.constraintValueType = SingleFieldConstraint.TYPE_LITERAL;
                doTypeChosen( form );
            }

        } );
        form.addAttribute( constants.LiteralValue() + ":", widgets( lit, new InfoPopup( constants.LiteralValue(),
                constants.LiteralValTip()) ) );




        form.addRow( new HTML( "<hr/>" ) );
        form.addRow( new SmallLabel(constants.AdvancedOptions()) );

        //only want to show variables if we have some !
        if (this.model.getBoundVariablesInScope( this.constraint ).size() > 0) {
            Button variable = new Button(constants.BoundVariable());
            variable.addClickListener( new ClickListener() {
                public void onClick(Widget w) {
                    con.constraintValueType = SingleFieldConstraint.TYPE_VARIABLE;
                    doTypeChosen( form );
                }
            });
            form.addAttribute(constants.AVariable(), widgets( variable, new InfoPopup(constants.ABoundVariable(), constants.BoundVariableTip())) );
        }

        Button formula = new Button(constants.NewFormula());
        formula.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                con.constraintValueType = SingleFieldConstraint.TYPE_RET_VALUE;
                doTypeChosen( form );
            }
        } );

        form.addAttribute(constants.AFormula() + ":", widgets( formula, new InfoPopup(constants.AFormula(),
                constants.FormulaExpressionTip()) ) );

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

