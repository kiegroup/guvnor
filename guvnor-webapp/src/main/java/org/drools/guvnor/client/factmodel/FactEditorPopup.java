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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.drools.guvnor.client.common.FormStylePopup;
import org.drools.guvnor.client.messages.Constants;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;

public class FactEditorPopup {

    private static Constants          constants     = ((Constants) GWT.create( Constants.class ));

    // A valid Fact, Field or Annotation name
    private static final RegExp VALID_NAME = RegExp.compile( "^[a-zA-Z][a-zA-Z\\d_$]*$" );
    
    private final FactMetaModel       factModel;
    private final List<FactMetaModel> factModels;
    private final ModelNameHelper     modelNameHelper;
    private final ListBox             lstSuperTypes = new ListBox();

    private Command                   okCommand;

    public FactEditorPopup(ModelNameHelper modelNameHelper,
                           List<FactMetaModel> factModels) {
        this( new FactMetaModel(),
              factModels,
              modelNameHelper );
    }

    public FactEditorPopup(FactMetaModel factModel,
                           List<FactMetaModel> factModels,
                           ModelNameHelper modelNameHelper) {
        this.factModel = factModel;
        this.factModels = factModels;
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
        name.setText( factModel.getName() );
        changeName.add( name );

        int selectedIndex = 0;
        lstSuperTypes.addItem( constants.DoesNotExtend() );
        for ( FactMetaModel fmm : factModels ) {
            if ( !fmm.getName().equals( factModel.getName() ) ) {
                lstSuperTypes.addItem( fmm.getName() );
                if ( factModel.getSuperType() != null && factModel.getSuperType().equals( fmm.getName() ) ) {
                    selectedIndex = lstSuperTypes.getItemCount() - 1;
                }
            }
        }
        lstSuperTypes.setSelectedIndex( selectedIndex );
        if ( lstSuperTypes.getItemCount() == 1 ) {
            lstSuperTypes.setEnabled( false );
        }

        lstSuperTypes.addChangeHandler( new ChangeHandler() {

            public void onChange(ChangeEvent event) {
                if ( lstSuperTypes.getSelectedIndex() <= 0 ) {
                    factModel.setSuperType( null );
                } else {
                    String oldSuperType = factModel.getSuperType();
                    String newSuperType = lstSuperTypes.getItemText( lstSuperTypes.getSelectedIndex() );
                    factModel.setSuperType( newSuperType );
                    if ( createsCircularDependency( newSuperType ) ) {
                        Window.alert( constants.CreatesCircularDependency( name.getText() ) );
                        factModel.setSuperType( oldSuperType );
                        lstSuperTypes.setSelectedIndex( getSelectedIndex( oldSuperType ) );
                        return;
                    } else {
                        factModel.setSuperType( newSuperType );
                    }
                }

            }

        } );

        Button nameButton = new Button( constants.OK() );

        nameButton.addKeyPressHandler( new NoSpaceKeyPressHandler() );

        nameButton.addClickHandler( new ClickHandler() {

            public void onClick(ClickEvent event) {
                String factName = name.getText();
                if ( !isNameValid( factName ) ) {
                    Window.alert( constants.InvalidModelName( factName ) );
                    return;
                }
                if ( doesTheNameExist( factName ) ) {
                    Window.alert( constants.NameTakenForModel( factName ) );
                    return;
                }
                if ( factModelAlreadyHasAName( factName ) ) {
                    if ( isTheUserSureHeWantsToChangeTheName() ) {
                        setNameAndClose();
                    }
                } else {
                    setNameAndClose();
                }
            }

            private boolean isNameValid(String name) {
                if ( name == null || "".equals( name ) ) {
                    return false;
                }
                return VALID_NAME.test( name );
            }

            private boolean factModelAlreadyHasAName(String name) {
                return factModel.getName() != null && !factModel.getName().equals( name );
            }

            private void setNameAndClose() {
                String oldName = factModel.getName();
                String newName = name.getText();

                modelNameHelper.changeNameInModelNameHelper( oldName,
                                                             newName );
                factModel.setName( newName );

                okCommand.execute();

                pop.hide();
            }

            private boolean isTheUserSureHeWantsToChangeTheName() {
                return Window.confirm( constants.ModelNameChangeWarning() );
            }

            private boolean doesTheNameExist(String name) {
                //The name may not have changed
                if ( factModel.getName() != null && factModel.getName().equals( name ) ) {
                    return false;
                }
                return !modelNameHelper.isUniqueName( name );
            }
        } );

        pop.addAttribute( constants.Name(),
                          changeName );
        pop.addAttribute( constants.TypeExtends(),
                          lstSuperTypes );
        pop.addRow( nameButton );

        pop.show();
    }

    private int getSelectedIndex(String superType) {
        if ( superType == null ) {
            return 0;
        }
        for ( int i = 1; i < lstSuperTypes.getItemCount(); i++ ) {
            if ( superType.equals( lstSuperTypes.getItemText( i ) ) ) {
                return i;
            }
        }
        return 0;
    }

    private boolean createsCircularDependency(String type) {
        Set<String> circulars = new HashSet<String>();
        FactMetaModel fmm = getFactMetaModel( type );
        return addCircular( fmm,
                            circulars );
    }

    private boolean addCircular(FactMetaModel fmm,
                                Set<String> circulars) {
        if ( !fmm.hasSuperType() ) {
            return false;
        }
        String type = fmm.getName();
        if ( circulars.contains( type ) ) {
            return true;
        }
        circulars.add( type );
        FactMetaModel efmm = getFactMetaModel( fmm.getSuperType() );
        return addCircular( efmm,
                            circulars );
    }

    private FactMetaModel getFactMetaModel(String type) {
        for ( FactMetaModel fmm : this.factModels ) {
            if ( fmm.getName().equals( type ) ) {
                return fmm;
            }
        }
        return null;
    }
}
