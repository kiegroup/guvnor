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

import java.util.List;

import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.util.AddButton;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.drools.guvnor.client.util.Format;

/**
 * 
 * @author rikkola
 *
 */
public class FactFieldsEditor extends Composite {

    private static Constants constants = ((Constants) GWT.create( Constants.class ));

    interface FactFieldsEditorBinder
        extends
        UiBinder<Widget, FactFieldsEditor> {
    }

    private static FactFieldsEditorBinder uiBinder = GWT.create( FactFieldsEditorBinder.class );

    @UiField
    VerticalPanel                         fieldsPanel;
    @UiField
    AddButton                             addFieldIcon;

    private final ModelNameHelper         modelNameHelper;

    private final List<FieldMetaModel>    fields;

    public FactFieldsEditor(final List<FieldMetaModel> fields,
                            final ModelNameHelper modelNameHelper) {

        this.fields = fields;
        this.modelNameHelper = modelNameHelper;

        initWidget( uiBinder.createAndBindUi( this ) );

        addFieldRows();

        addFieldIcon.setTitle( constants.AddField() );
        addFieldIcon.setText( constants.AddField() );
    }

    @UiHandler("addFieldIcon")
    void addNewFieldClick(ClickEvent event) {
        final FieldEditorPopup popup = new FieldEditorPopup( modelNameHelper );

        popup.setOkCommand( new Command() {

            public void execute() {
                createNewField( popup );
            }

            private void createNewField(final FieldEditorPopup popup) {
                FieldMetaModel field = popup.getField();
                fields.add( field );
                addFieldRow( field );
            }
        } );

        popup.show();

    }

    private void addFieldRows() {
        for ( FieldMetaModel fieldMetaModel : fields ) {
            addFieldRow( fieldMetaModel );
        }
    }

    private void addFieldRow(final FieldMetaModel fieldMetaModel) {
        final FactFieldEditor editor = new FactFieldEditor( fieldMetaModel,
                                                            modelNameHelper );

        editor.setDeleteCommand( new Command() {
            public void execute() {
                if ( Window.confirm( Format.format( constants.AreYouSureYouWantToRemoveTheField0(),
                                                    fieldMetaModel.name ) ) ) {
                    fieldsPanel.remove( editor );
                    fields.remove( fieldMetaModel );
                }
            }
        } );

        fieldsPanel.add( editor );
    }
}
