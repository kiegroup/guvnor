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

import org.drools.guvnor.client.common.FormStylePopup;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.util.Format;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextBox;

/**
 * 
 * @author rikkola
 *
 */
public class FactEditorPopup {

    private static Constants      constants = ((Constants) GWT.create( Constants.class ));

    private final FactMetaModel   factModel;
    private final ModelNameHelper modelNameHelper;

    private Command               okCommand;

    public FactEditorPopup(ModelNameHelper modelNameHelper) {
        this( new FactMetaModel(),
              modelNameHelper );
    }

    public FactEditorPopup(FactMetaModel factModel,
                           ModelNameHelper modelNameHelper) {
        this.factModel = factModel;
        this.modelNameHelper = modelNameHelper;
    }

    public FactMetaModel getFactModel() {
        return factModel;
    }

    public void setOkCommand(Command okCommand) {
        this.okCommand = okCommand;
    }

    public void show() {

        final FormStylePopup pop = new FormStylePopup();
        pop.setTitle( constants.Name() );
        HorizontalPanel changeName = new HorizontalPanel();
        final TextBox name = new TextBox();
        name.setText( factModel.name );
        changeName.add( name );
        Button nameButton = new Button( constants.OK() );

        nameButton.addKeyPressHandler( new NoSpaceKeyPressHandler() );

        nameButton.addClickHandler( new ClickHandler() {

            public void onClick(ClickEvent event) {
                if ( doesTheNameExist() ) {
                    Window.alert( Format.format( constants.NameTakenForModel(),
                                                 name.getText() ) );
                    return;
                }

                if ( factModelAlreadyHasAName() ) {
                    if ( isTheUserSureHeWantsToChangeTheName() ) {
                        setNameAndClose();
                    }
                } else {
                    setNameAndClose();
                }
            }

            private boolean factModelAlreadyHasAName() {
                return factModel.name != null;
            }

            private void setNameAndClose() {
                String oldName = factModel.name;
                String newName = name.getText();

                modelNameHelper.changeNameInModelNameHelper( oldName,
                                                             newName );
                factModel.name = newName;

                okCommand.execute();

                pop.hide();
            }

            private boolean isTheUserSureHeWantsToChangeTheName() {
                return Window.confirm( constants.ModelNameChangeWarning() );
            }

            private boolean doesTheNameExist() {
                return !modelNameHelper.isUniqueName( name.getText() );
            }
        } );
        changeName.add( nameButton );
        pop.addAttribute( constants.Name(),
                          changeName );

        pop.show();
    }
}
