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

package org.drools.guvnor.client.explorer.navigation.qa.testscenarios;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import org.drools.guvnor.client.common.FormStylePopup;
import org.drools.guvnor.client.common.ImageButton;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.resources.Images;
import org.drools.ide.common.client.testscenarios.Scenario;

import java.util.List;

public class ConfigWidget extends Composite {

    public ConfigWidget(final Scenario sc,
                        final String packageName,
                        final ScenarioWidget scWidget) {

        final ListBox box = new ListBox( true );

        for ( int i = 0; i < sc.getRules().size(); i++ ) {
            box.addItem( (String) sc.getRules().get( i ) );
        }
        HorizontalPanel filter = new HorizontalPanel();

        final Image add = new ImageButton( Images.INSTANCE.newItem(),
                                           Constants.INSTANCE.AddANewRule() );
        add.addClickHandler( new ClickHandler() {
            public void onClick(ClickEvent event) {
                showRulePopup( (Widget) event.getSource(),
                               box,
                               packageName,
                               sc.getRules(),
                               scWidget );
            }
        } );

        final Image remove = new ImageButton( Images.INSTANCE.trash(),
                                              Constants.INSTANCE.RemoveSelectedRule() );
        remove.addClickHandler( new ClickHandler() {
            public void onClick(ClickEvent event) {
                if ( box.getSelectedIndex() == -1 ) {
                    Window.alert( Constants.INSTANCE.PleaseChooseARuleToRemove() );
                } else {
                    String r = box.getItemText( box.getSelectedIndex() );
                    sc.getRules().remove( r );
                    box.removeItem( box.getSelectedIndex() );
                }
            }
        } );
        VerticalPanel actions = new VerticalPanel();
        actions.add( add );
        actions.add( remove );

        final ListBox drop = new ListBox();
        drop.addItem( Constants.INSTANCE.AllowTheseRulesToFire(),
                      "inc" ); //NON-NLS
        drop.addItem( Constants.INSTANCE.PreventTheseRulesFromFiring(),
                      "exc" ); //NON-NLS
        drop.addItem( Constants.INSTANCE.AllRulesMayFire() );
        drop.addChangeHandler( new ChangeHandler() {
            public void onChange(ChangeEvent event) {
                String s = drop.getValue( drop.getSelectedIndex() );
                if ( s.equals( "inc" ) ) { //NON-NLS
                    sc.setInclusive( true );
                    add.setVisible( true );
                    remove.setVisible( true );
                    box.setVisible( true );
                } else if ( s.equals( "exc" ) ) { //NON-NLS
                    sc.setInclusive( false );
                    add.setVisible( true );
                    remove.setVisible( true );
                    box.setVisible( true );
                } else {
                    sc.getRules().clear();
                    box.clear();
                    box.setVisible( false );
                    add.setVisible( false );
                    remove.setVisible( false );
                }
            }
        } );

        if ( sc.getRules().size() > 0 ) {
            drop.setSelectedIndex( (sc.isInclusive()) ? 0 : 1 );
        } else {
            drop.setSelectedIndex( 2 );
            box.setVisible( false );
            add.setVisible( false );
            remove.setVisible( false );
        }

        filter.add( drop );
        filter.add( box );
        filter.add( actions );

        initWidget( filter );
    }

    private void showRulePopup(Widget w,
                               final ListBox box,
                               String packageName,
                               final List<String> filterList,
                               ScenarioWidget scw) {
        final FormStylePopup pop = new FormStylePopup( Images.INSTANCE.ruleAsset(),
                                                       Constants.INSTANCE.SelectRule() );

        Widget ruleSelector = scw.getRuleSelectionWidget( packageName,
                                                          new RuleSelectionEvent() {
                                                              public void ruleSelected(String r) {
                                                                  filterList.add( r );
                                                                  box.addItem( r );
                                                                  pop.hide();

                                                              }
                                                          } );

        pop.addRow( ruleSelector );

        pop.show();

    }

}
