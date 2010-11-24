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

package org.drools.guvnor.client.qa;

import org.drools.guvnor.client.common.DirtyableFlexTable;
import org.drools.guvnor.client.common.ImageButton;
import org.drools.guvnor.client.common.SmallLabel;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.resources.Images;
import org.drools.guvnor.client.util.Format;
import org.drools.ide.common.client.modeldriven.testing.FixtureList;
import org.drools.ide.common.client.modeldriven.testing.Scenario;
import org.drools.ide.common.client.modeldriven.testing.VerifyRuleFired;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;

/**
 * Created by IntelliJ IDEA.
 * User: nheron
 * Date: 7 nov. 2009
 * Time: 19:30:04
 * To change this template use File | Settings | File Templates.
 */
public class VerifyRulesFiredWidget extends Composite {

    private Constants     constants = GWT.create( Constants.class );
    private static Images images    = GWT.create( Images.class );

    private Grid          outer;
    private boolean       showResults;

    /**
     * @param rfl List<VeryfyRuleFired>
     * @param scenario = the scenario to add/remove from
     */
    public VerifyRulesFiredWidget(final FixtureList rfl,
                                  final Scenario scenario,
                                  boolean showResults) {
        outer = new Grid( 2,
                          1 );
        this.showResults = showResults;
        outer.getCellFormatter().setStyleName( 0,
                                               0,
                                               "modeller-fact-TypeHeader" ); //NON-NLS
        outer.getCellFormatter().setAlignment( 0,
                                               0,
                                               HasHorizontalAlignment.ALIGN_CENTER,
                                               HasVerticalAlignment.ALIGN_MIDDLE );
        outer.setStyleName( "modeller-fact-pattern-Widget" ); //NON-NLS

        outer.setWidget( 0,
                         0,
                         new SmallLabel( constants.ExpectRules() ) );
        initWidget( outer );

        FlexTable data = render( rfl,
                                 scenario );
        outer.setWidget( 1,
                         0,
                         data );
    }

    private FlexTable render(final FixtureList rfl,
                             final Scenario sc) {
        FlexTable data = new DirtyableFlexTable();

        for ( int i = 0; i < rfl.size(); i++ ) {
            final VerifyRuleFired v = (VerifyRuleFired) rfl.get( i );

            if ( showResults && v.successResult != null ) {
                if ( !v.successResult.booleanValue() ) {
                    data.setWidget( i,
                                    0,
                                    new Image( images.warning() ) );
                    data.setWidget( i,
                                    4,
                                    new HTML( Format.format( constants.ActualResult(),
                                                             v.actualResult ) ) );

                    data.getCellFormatter().addStyleName( i,
                                                          4,
                                                          "testErrorValue" ); //NON-NLS

                } else {
                    data.setWidget( i,
                                    0,
                                    new Image( images.testPassed() ) );
                }

            }
            data.setWidget( i,
                            1,
                            new SmallLabel( v.ruleName + ":" ) );
            data.getFlexCellFormatter().setAlignment( i,
                                                      1,
                                                      HasHorizontalAlignment.ALIGN_RIGHT,
                                                      HasVerticalAlignment.ALIGN_MIDDLE );

            final ListBox b = new ListBox();
            b.addItem( constants.firedAtLeastOnce(),
                       "y" );
            b.addItem( constants.didNotFire(),
                       "n" );
            b.addItem( constants.firedThisManyTimes(),
                       "e" );
            final TextBox num = new TextBox();
            num.setVisibleLength( 5 );

            if ( v.expectedFire != null ) {
                b.setSelectedIndex( (v.expectedFire.booleanValue()) ? 0 : 1 );
                num.setVisible( false );
            } else {
                b.setSelectedIndex( 2 );
                String xc = (v.expectedCount != null) ? "" + v.expectedCount.intValue() : "0";
                num.setText( xc );
            }

            b.addChangeHandler( new ChangeHandler() {
                public void onChange(ChangeEvent event) {
                    String s = b.getValue( b.getSelectedIndex() );
                    if ( s.equals( "y" ) || s.equals( "n" ) ) {
                        num.setVisible( false );
                        v.expectedFire = (s.equals( "y" )) ? Boolean.TRUE : Boolean.FALSE;
                        v.expectedCount = null;
                    } else {
                        num.setVisible( true );
                        v.expectedFire = null;
                        num.setText( "1" );
                        v.expectedCount = new Integer( 1 );
                    }
                }
            } );

            b.addItem( constants.ChooseDotDotDot() );

            num.addChangeHandler( new ChangeHandler() {
                public void onChange(ChangeEvent event) {
                    v.expectedCount = new Integer( num.getText() );
                }
            } );

            HorizontalPanel h = new HorizontalPanel();
            h.add( b );
            h.add( num );
            data.setWidget( i,
                            2,
                            h );

            Image del = new ImageButton( images.deleteItemSmall(),
                                         constants.RemoveThisRuleExpectation(),
                                         new ClickHandler() {
                                             public void onClick(ClickEvent w) {
                                                 if ( Window.confirm( constants.AreYouSureYouWantToRemoveThisRuleExpectation() ) ) {
                                                     rfl.remove( v );
                                                     sc.removeFixture( v );
                                                     outer.setWidget( 1,
                                                                      0,
                                                                      render( rfl,
                                                                              sc ) );
                                                 }
                                             }
                                         } );

            data.setWidget( i,
                            3,
                            del );

            //we only want numbers here...
            num.addKeyPressHandler( new KeyPressHandler() {
                public void onKeyPress(KeyPressEvent event) {
                    if ( Character.isLetter( event.getCharCode() ) ) {
                        ((TextBox) event.getSource()).cancelKey();
                    }
                }
            } );
        }
        return data;
    }
}
