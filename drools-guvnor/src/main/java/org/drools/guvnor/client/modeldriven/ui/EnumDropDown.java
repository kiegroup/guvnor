/**
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
import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.common.IDirtyable;
import org.drools.guvnor.client.common.LoadingPopup;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;
import org.drools.ide.common.client.modeldriven.DropDownData;
import org.drools.ide.common.client.modeldriven.ui.ConstraintValueEditorHelper;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * A drop down for enumerated values
 * 
 * @author Toni Rikkola
 *
 */
public class EnumDropDown extends ListBox
    implements
    IDirtyable {
    private DropDownValueChanged valueChanged;

    public EnumDropDown(final String currentValue,
                        final DropDownValueChanged valueChanged,
                        final DropDownData dropData) {
        final Constants cs = GWT.create( Constants.class );

        this.valueChanged = valueChanged;

        addChangeListener( new ChangeListener() {
            public void onChange(Widget w) {
                valueChanged.valueChanged( getItemText( getSelectedIndex() ),
                                           getValue( getSelectedIndex() ) );
            }
        } );

        //if we have to do it lazy, we will hit up the server when the widget gets focus
        if ( dropData.fixedList == null && dropData.queryExpression != null ) {
            DeferredCommand.addCommand( new Command() {
                public void execute() {
                    LoadingPopup.showMessage( cs.RefreshingList() );
                    RepositoryServiceFactory.getService().loadDropDownExpression( dropData.valuePairs,
                                                                                  dropData.queryExpression,
                                                                                  new GenericCallback() {
                                                                                      public void onSuccess(Object data) {
                                                                                          LoadingPopup.close();
                                                                                          String[] list = (String[]) data;

                                                                                          if ( list.length == 0 ) {
                                                                                              list = new String[]{cs.UnableToLoadList()};
                                                                                          }

                                                                                          fillDropDown( currentValue,
                                                                                                        list );
                                                                                      }

                                                                                      public void onFailure(Throwable t) {
                                                                                          LoadingPopup.close();
                                                                                          //just do an empty drop down...
                                                                                          fillDropDown( currentValue,
                                                                                                        new String[]{cs.UnableToLoadList()} );
                                                                                      }
                                                                                  } );
                }
            } );

        } else {
            //otherwise its just a normal one...
            fillDropDown( currentValue,
                          dropData.fixedList );
        }

        if ( currentValue == null || "".equals( currentValue ) ) {
            int ix = getSelectedIndex();
            if ( ix > -1 ) {
                valueChanged.valueChanged( getItemText( ix ),
                                           getValue( ix ) );
            }
        }
    }

    private void fillDropDown(final String currentValue,
                              final String[] enumeratedValues) {
        boolean selected = false;

        clear();

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
                addItem( v,
                         v );
                val = v;
            }
            if ( currentValue != null && currentValue.equals( val ) ) {
                setSelectedIndex( i );
                selected = true;
                valueChanged.valueChanged( getItemText( getSelectedIndex() ),
                                           getValue( getSelectedIndex() ) );
            }
        }

        if ( currentValue != null && !"".equals( currentValue ) && !selected ) {
            //need to add this value
            addItem( currentValue,
                     currentValue );
            setSelectedIndex( enumeratedValues.length );
        }
    }
}