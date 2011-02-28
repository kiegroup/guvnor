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

package org.drools.guvnor.client.modeldriven.ui;

import org.drools.guvnor.client.common.DropDownValueChanged;
import org.drools.guvnor.client.messages.Constants;
import org.drools.ide.common.client.modeldriven.DropDownData;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.brl.BaseSingleFieldConstraint;
import org.drools.ide.common.client.modeldriven.brl.FactPattern;

import com.google.gwt.core.client.GWT;
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

    private Constants          constants = ((Constants) GWT.create( Constants.class ));
    protected Panel            panel     = new HorizontalPanel();

    // The value is not always same as the text
    private final Label        textWidget;

    private final EnumDropDown enumDropDown;

    private final Button       okButton  = new Button( constants.OK() );

    private Command onValueChangeCommand;

    public EnumDropDownLabel(FactPattern pattern,
                             String fieldName,
                             SuggestionCompletionEngine sce,
                             BaseSingleFieldConstraint constraint, boolean enabled) {
        this.textWidget = getTextLabel(enabled);        
        this.enumDropDown = getEnumDropDown( constraint,
                                             sce,
                                             pattern,
                                             fieldName );
        this.enumDropDown.setEnabled(enabled);
        panel.add( textWidget );

        initWidget( panel );

    }

    private Label getTextLabel(boolean enabled) {
        Label label = new Label();
        label.setStyleName( "x-form-field" );
        if (enabled){
	        label.addClickHandler(new ClickHandler() {
	
	            public void onClick(ClickEvent event) {
	                showPopup();
	
	            }
	        });
        }
        
        if ( label.getText() == null && "".equals( label.getText() ) ) {
            label.setText( constants.Value() );
        }

        return label;
    }

    private void showPopup() {
        final PopupPanel popup = new PopupPanel();
        popup.setGlassEnabled( true );
        HorizontalPanel horizontalPanel = new HorizontalPanel();

        popup.setPopupPosition( this.getAbsoluteLeft(),
                                this.getAbsoluteTop() );

        okButton.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                executeOnValueChangeCommand();
                panel.clear();
                panel.add( textWidget );
                popup.hide();

            }
        });
 
        horizontalPanel.add( enumDropDown );
        horizontalPanel.add( okButton );

        popup.add( horizontalPanel );

        popup.show();

    }

    private EnumDropDown getEnumDropDown(final BaseSingleFieldConstraint constraint,
                                         SuggestionCompletionEngine sce,
                                         FactPattern pattern,
                                         String fieldName) {
        String valueType = sce.getFieldType( pattern.getFactType(),
                                             fieldName );

        final DropDownData dropDownData;
        if ( SuggestionCompletionEngine.TYPE_BOOLEAN.equals( valueType ) ) {
            dropDownData = DropDownData.create( new String[]{"true", "false"} ); //NON-NLS
        } else {
            dropDownData = sce.getEnums( pattern,
                                         fieldName );
        }

        final EnumDropDown box = new EnumDropDown( constraint.getValue(),
                                                   new DropDownValueChanged() {
                                                       public void valueChanged(String newText,
                                                                                String newValue) {
                                                           textWidget.setText( newText );
                                                           constraint.setValue(newValue);
                                                           okButton.click();
                                                       }
                                                   },
                                                   dropDownData );

        if ( box.getItemCount() > 6 ) {
            box.setVisibleItemCount( 6 );
        } else {
            box.setVisibleItemCount( box.getItemCount() );
        }

        return box;
    }

    private void executeOnValueChangeCommand(){
        if (this.onValueChangeCommand != null){
            this.onValueChangeCommand.execute();
        }
    }

    public void setOnValueChangeCommand(Command onValueChangeCommand) {
        this.onValueChangeCommand = onValueChangeCommand;
    }

}
