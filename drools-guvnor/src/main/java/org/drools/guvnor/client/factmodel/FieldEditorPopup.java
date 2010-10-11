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
package org.drools.guvnor.client.factmodel;

import java.util.Map;

import org.drools.guvnor.client.common.FormStylePopup;
import org.drools.guvnor.client.messages.Constants;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;

/**
 * 
 * @author rikkola
 *
 */
public class FieldEditorPopup {

    private static Constants      constants = ((Constants) GWT.create( Constants.class ));

    private final FieldMetaModel  field;

    private final ModelNameHelper modelNameHelper;

    private Command               okCommand;

    public FieldEditorPopup(ModelNameHelper modelNameHelper) {
        this( new FieldMetaModel(),
              modelNameHelper );
    }

    public FieldEditorPopup(FieldMetaModel field,
                            ModelNameHelper modelNameHelper) {
        this.field = field;
        this.modelNameHelper = modelNameHelper;
    }

    public FieldMetaModel getField() {
        return field;
    }

    public void setOkCommand(Command okCommand) {
        this.okCommand = okCommand;
    }

    public void show() {
        final FormStylePopup pop = new FormStylePopup();
        final TextBox fieldName = new TextBox();
        final TextBox fieldType = new TextBox();
        fieldName.addKeyPressHandler( new NoSpaceKeyPressHandler() );
        fieldType.addKeyPressHandler( new NoSpaceKeyPressHandler() );
        if ( field != null ) {
            fieldName.setText( field.name );
            fieldType.setText( field.type );
        }
        HorizontalPanel typeP = new HorizontalPanel();
        typeP.add( fieldType );
        final ListBox typeChoice = new ListBox();
        typeChoice.addItem( constants.chooseType() );

        for ( Map.Entry<String, String> entry : modelNameHelper.getTypeDescriptions().entrySet() ) {
            typeChoice.addItem( entry.getValue(),
                                entry.getKey() );
        }

        typeChoice.setSelectedIndex( 0 );
        typeChoice.addChangeHandler( new ChangeHandler() {
            public void onChange(ChangeEvent event) {
                fieldType.setText( typeChoice.getValue( typeChoice.getSelectedIndex() ) );
            }
        } );

        typeP.add( typeChoice );

        pop.addAttribute( constants.FieldNameAttribute(),
                          fieldName );
        pop.addAttribute( constants.Type(),
                          typeP );

        Button ok = new Button( constants.OK() );
        ok.addClickHandler( new ClickHandler() {

            public void onClick(ClickEvent event) {

                field.name = fieldName.getText();
                field.type = fieldType.getText();

                okCommand.execute();

                pop.hide();
            }
        } );
        pop.addAttribute( "",
                          ok );

        pop.show();
    }
}
