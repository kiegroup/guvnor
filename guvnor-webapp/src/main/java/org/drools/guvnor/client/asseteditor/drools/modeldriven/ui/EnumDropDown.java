/*
 * Copyright 2011 JBoss Inc
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
import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.common.IDirtyable;
import org.drools.guvnor.client.common.LoadingPopup;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;
import org.drools.ide.common.client.modeldriven.DropDownData;
import org.drools.ide.common.client.modeldriven.ui.ConstraintValueEditorHelper;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.ListBox;

/**
 * A drop down for enumerated values
 */
public class EnumDropDown extends ListBox
        implements
        IDirtyable {

    private static final Constants     constants = GWT.create( Constants.class );

    private final DropDownValueChanged valueChangedCommand;

    public EnumDropDown(final String currentValue,
                        final DropDownValueChanged valueChanged,
                        final DropDownData dropData) {

        this.valueChangedCommand = valueChanged;

        addChangeHandler( new ChangeHandler() {
            public void onChange(ChangeEvent event) {
                valueChangedCommand.valueChanged( getItemText( getSelectedIndex() ),
                                                  getValue( getSelectedIndex() ) );
            }
        } );

        setDropDownData( currentValue,
                         dropData );
    }

    public void setDropDownData(final String currentValue,
                                final DropDownData dropData) {

        //if we have to do it lazy, we will hit up the server when the widget gets focus
        if ( dropData != null && dropData.fixedList == null && dropData.queryExpression != null ) {
            Scheduler.get().scheduleDeferred( new Command() {
                public void execute() {
                    LoadingPopup.showMessage( constants.RefreshingList() );
                    RepositoryServiceFactory.getService().loadDropDownExpression( dropData.valuePairs,
                                                                                  dropData.queryExpression,
                                                                                  new GenericCallback<String[]>() {
                                                                                      public void onSuccess(String[] data) {
                                                                                          LoadingPopup.close();

                                                                                          if ( data.length == 0 ) {
                                                                                              data = new String[]{constants.UnableToLoadList()};
                                                                                          }

                                                                                          fillDropDown( currentValue,
                                                                                                        data );
                                                                                      }

                                                                                      public void onFailure(Throwable t) {
                                                                                          LoadingPopup.close();
                                                                                          //just do an empty drop down...
                                                                                          fillDropDown( currentValue,
                                                                                                        new String[]{constants.UnableToLoadList()} );
                                                                                      }
                                                                                  } );
                }
            } );

        } else {
            //otherwise its just a normal one...
            fillDropDown( currentValue,
                          dropData );
        }

    }

    private void fillDropDown(final String currentValue,
                              final DropDownData dropData) {
        if ( dropData == null ) {
            fillDropDown( currentValue,
                          new String[0] );
        } else {
            fillDropDown( currentValue,
                          dropData.fixedList );
        }
    }

    private void fillDropDown(final String currentValue,
                              final String[] enumeratedValues) {
        clear();
        //        addItem( constants.Choose() );
        boolean selected = false;

        for ( int i = 0; i < enumeratedValues.length; i++ ) {
            String v = enumeratedValues[i];
            String val;
            if ( v.indexOf( '=' ) > 0 ) {
                //using a mapping
                String[] splut = ConstraintValueEditorHelper.splitValue( v );
                String realValue = splut[0];
                String display = splut[1];
                val = realValue;
                addItem( display,
                         realValue );
            } else {
                addItem( v );
                val = v;
            }
            if ( currentValue != null && currentValue.equals( val ) ) {
                setSelectedIndex( i );
                //                setSelectedIndex( i + 1 );
                selected = true;
            }
        }

        if ( !selected ) {
            setSelectedIndex( 0 );
            valueChangedCommand.valueChanged( getItemText( getSelectedIndex() ),
                                              getValue( getSelectedIndex() ) );
        }
    }
}
