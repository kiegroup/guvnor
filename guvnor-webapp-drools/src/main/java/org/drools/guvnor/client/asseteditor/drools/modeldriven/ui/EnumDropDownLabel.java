/*
 * Copyright 2010 JBoss Inc
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

package org.drools.guvnor.client.asseteditor.drools.modeldriven.ui;

import org.drools.guvnor.client.common.DropDownValueChanged;
import org.drools.guvnor.client.messages.Constants;
import org.drools.ide.common.client.modeldriven.DropDownData;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.brl.BaseSingleFieldConstraint;
import org.drools.ide.common.client.modeldriven.brl.CompositeFieldConstraint;
import org.drools.ide.common.client.modeldriven.brl.ConnectiveConstraint;
import org.drools.ide.common.client.modeldriven.brl.SingleFieldConstraint;
import org.drools.ide.common.client.modeldriven.brl.SingleFieldConstraintEBLeftSide;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;

public class EnumDropDownLabel extends Composite {

    protected final Label                textWidget;
    protected final EnumDropDown         enumDropDown;
    protected final Button               okButton;
    protected final Panel                panel = new HorizontalPanel();
    protected final PopupPanel           popup;

    protected Command                    onValueChangeCommand;

    private String                       factType;
    private CompositeFieldConstraint     constraintList;

    protected SuggestionCompletionEngine sce;
    protected BaseSingleFieldConstraint  constraint;
    protected boolean                    enabled;

    public EnumDropDownLabel(String factType,
                             CompositeFieldConstraint constraintList,
                             SuggestionCompletionEngine sce,
                             BaseSingleFieldConstraint constraint,
                             boolean enabled) {
        this.factType = factType;
        this.constraintList = constraintList;
        this.constraint = constraint;
        this.sce = sce;
        this.enabled = enabled;

        textWidget = createTextLabel();
        enumDropDown = createEnumDropDown();
        okButton = new Button( Constants.INSTANCE.OK() );
        panel.add( textWidget );

        updateTextWidget();
        updateModel();

        popup = createPopup();

        initWidget( panel );

    }

    private void showPopup() {
        popup.setPopupPosition( this.getAbsoluteLeft(),
                                this.getAbsoluteTop() );
        //Change from multi-select
        final String operator = constraint.getOperator();
        if ( SuggestionCompletionEngine.operatorRequiresList( operator ) && !enumDropDown.isMultipleSelect() ) {
            //Reset the current value since we are changing type of the list from multi to non-multi or vice versa
            enumDropDown.setMultipleSelect( true );
            constraint.setValue( "" );

        } else if ( !SuggestionCompletionEngine.operatorRequiresList( operator ) && enumDropDown.isMultipleSelect() ) {
            enumDropDown.setMultipleSelect( false );
            constraint.setValue( "" );
        }
        //Lazy initialisation of drop-down data as it's content could depend on another drop-down
        enumDropDown.setDropDownData( constraint.getValue(),
                                      getDropDownData() );

        popup.show();
    }

    private DropDownData getDropDownData() {
        String valueType;
        String fieldName;
        if ( constraint instanceof SingleFieldConstraintEBLeftSide ) {
            SingleFieldConstraintEBLeftSide sfexp = (SingleFieldConstraintEBLeftSide) this.constraint;
            fieldName = sfexp.getExpressionLeftSide().getFieldName();
            valueType = sfexp.getExpressionLeftSide().getGenericType();

        } else if ( constraint instanceof ConnectiveConstraint ) {
            ConnectiveConstraint cc = (ConnectiveConstraint) constraint;
            fieldName = cc.getFieldName();
            valueType = cc.getFieldType();

        } else if ( constraint instanceof SingleFieldConstraint ) {
            SingleFieldConstraint sfc = (SingleFieldConstraint) this.constraint;
            fieldName = sfc.getFieldName();
            valueType = sce.getFieldType( factType,
                                          fieldName );
        } else {
            throw new IllegalArgumentException( "Unrecognised constraint type." );
        }

        final DropDownData dropDownData;
        if ( SuggestionCompletionEngine.TYPE_BOOLEAN.equals( valueType ) ) {
            dropDownData = DropDownData.create( new String[]{"true", "false"} ); //NON-NLS
        } else {
            dropDownData = sce.getEnums( this.factType,
                                         this.constraintList,
                                         fieldName );
        }
        return dropDownData;
    }

    private EnumDropDown createEnumDropDown() {

        final EnumDropDown box = new EnumDropDown( constraint.getValue(),
                                                   new DropDownValueChanged() {
                                                       public void valueChanged(String newText,
                                                                                String newValue) {
                                                           constraint.setValue( newValue );
                                                           textWidget.setText( newText );
                                                       }
                                                   },
                                                   getDropDownData(),
                                                   "in".equals( constraint.getOperator() ) );

        if ( box.getItemCount() > 6 ) {
            box.setVisibleItemCount( 6 );
        } else {
            box.setVisibleItemCount( box.getItemCount() );
        }

        box.setEnabled( enabled );

        return box;
    }

    private Label createTextLabel() {
        Label label = new Label();
        label.setStyleName( "form-field" );
        if ( enabled ) {
            label.addClickHandler( new ClickHandler() {

                public void onClick(ClickEvent event) {
                    showPopup();
                }
            } );
        }
        return label;
    }

    private PopupPanel createPopup() {
        final PopupPanel popup = new PopupPanel();
        popup.setGlassEnabled( true );

        okButton.addClickHandler( new ClickHandler() {

            public void onClick(ClickEvent event) {
                executeOnValueChangeCommand();
                panel.clear();
                panel.add( textWidget );
                popup.hide();
            }
        } );

        HorizontalPanel horizontalPanel = new HorizontalPanel();
        horizontalPanel.add( enumDropDown );
        horizontalPanel.add( okButton );
        popup.add( horizontalPanel );
        return popup;
    }

    private void executeOnValueChangeCommand() {
        if ( this.onValueChangeCommand != null ) {
            this.onValueChangeCommand.execute();
        }
    }

    public void setOnValueChangeCommand(Command onValueChangeCommand) {
        this.onValueChangeCommand = onValueChangeCommand;
    }

    public void refreshDropDownData() {
        this.enumDropDown.setDropDownData( constraint.getValue(),
                                           getDropDownData() );

        //Copy selections in UI to data-model again, as updating the drop-downs
        //can lead to some selected values being cleared when dependent drop-downs
        //are used.
        updateTextWidget();
        updateModel();
    }

    //Lookup display text from drop-down selection
    private void updateTextWidget() {
        textWidget.setText( enumDropDown.getSelectedItemsText() );
    }

    //Lookup model value from drop-down selection
    private void updateModel() {
        constraint.setValue( enumDropDown.getSelectedValue() );
    }

}
