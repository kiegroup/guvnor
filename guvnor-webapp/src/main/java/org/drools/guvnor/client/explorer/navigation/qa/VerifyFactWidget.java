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

package org.drools.guvnor.client.explorer.navigation.qa;

import org.drools.guvnor.client.common.FormStylePopup;
import org.drools.guvnor.client.common.ImageButton;
import org.drools.guvnor.client.common.SmallLabel;
import org.drools.guvnor.client.common.ValueChanged;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.resources.Images;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.testing.ExecutionTrace;
import org.drools.ide.common.client.modeldriven.testing.Scenario;
import org.drools.ide.common.client.modeldriven.testing.VerifyFact;
import org.drools.ide.common.client.modeldriven.testing.VerifyField;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * Created by IntelliJ IDEA.
 * User: nheron
 * Date: 7 nov. 2009
 * Time: 19:31:47
 * To change this template use File | Settings | File Templates.
 */
public class VerifyFactWidget extends Composite {

    private Constants                  constants = GWT.create( Constants.class );
    private static Images              images    = GWT.create( Images.class );

    private Grid                       outer;
    private boolean                    showResults;
    private String                     type;
    private SuggestionCompletionEngine sce;
    private Scenario                   scenario;
    private ExecutionTrace             executionTrace;

    public VerifyFactWidget(final VerifyFact vf,
                            final Scenario sc,
                            final SuggestionCompletionEngine sce,
                            ExecutionTrace executionTrace,
                            boolean showResults) {
        outer = new Grid( 2,
                          1 );
        outer.getCellFormatter().setStyleName( 0,
                                               0,
                                               "modeller-fact-TypeHeader" ); //NON-NLS
        outer.getCellFormatter().setAlignment( 0,
                                               0,
                                               HasHorizontalAlignment.ALIGN_CENTER,
                                               HasVerticalAlignment.ALIGN_MIDDLE );
        outer.setStyleName( "modeller-fact-pattern-Widget" ); //NON-NLS
        this.sce = sce;
        this.scenario = sc;
        this.executionTrace = executionTrace;
        HorizontalPanel ab = new HorizontalPanel();
        if ( !vf.anonymous ) {
            type = (String) sc.getVariableTypes().get( vf.getName() );
            ab.add( new SmallLabel( constants.scenarioFactTypeHasValues( type, vf.getName() ) ) );
        } else {
            type = vf.getName();
            ab.add( new SmallLabel( constants.AFactOfType0HasValues( vf.getName() ) ) );
        }
        this.showResults = showResults;

        Image add = new ImageButton( images.addFieldToFact(),
                                     constants.AddAFieldToThisExpectation(),
                                     new ClickHandler() {
                                         public void onClick(ClickEvent w) {

                                             String[] fields = (String[]) sce.getModelFields( type );
                                             final FormStylePopup pop = new FormStylePopup( images.ruleAsset(),
                                                                                            constants.ChooseAFieldToAdd() );
                                             final ListBox b = new ListBox();
                                             for ( int i = 0; i < fields.length; i++ ) {
                                                 b.addItem( fields[i] );
                                             }
                                             pop.addRow( b );
                                             Button ok = new Button( constants.OK() );
                                             ok.addClickHandler( new ClickHandler() {
                                                 public void onClick(ClickEvent w) {
                                                     String f = b.getItemText( b.getSelectedIndex() );
                                                     vf.getFieldValues().add( new VerifyField( f,
                                                                                          "",
                                                                                          "==" ) );
                                                     FlexTable data = render( vf );
                                                     outer.setWidget( 1,
                                                                      0,
                                                                      data );
                                                     pop.hide();
                                                 }
                                             } );
                                             pop.addRow( ok );
                                             pop.show();

                                         }
                                     } );

        ab.add( add );
        outer.setWidget( 0,
                         0,
                         ab );
        initWidget( outer );

        FlexTable data = render( vf );
        outer.setWidget( 1,
                         0,
                         data );

    }

    private FlexTable render(final VerifyFact vf) {
        FlexTable data = new FlexTable();
        for ( int i = 0; i < vf.getFieldValues().size(); i++ ) {
            final VerifyField fld = (VerifyField) vf.getFieldValues().get( i );
            data.setWidget( i,
                            1,
                            new SmallLabel( fld.getFieldName() + ":" ) );
            data.getFlexCellFormatter().setHorizontalAlignment( i,
                                                                1,
                                                                HasHorizontalAlignment.ALIGN_RIGHT );

            final ListBox opr = new ListBox();
            opr.addItem( constants.equalsScenario(),
                         "==" );
            opr.addItem( constants.doesNotEqualScenario(),
                         "!=" );
            if ( fld.getOperator().equals( "==" ) ) {
                opr.setSelectedIndex( 0 );
            } else {
                opr.setSelectedIndex( 1 );
            }
            opr.addChangeHandler( new ChangeHandler() {
                public void onChange(ChangeEvent event) {
                    fld.setOperator( opr.getValue( opr.getSelectedIndex() ) );
                }
            } );

            data.setWidget( i,
                            2,
                            opr );
            Widget cellEditor = new VerifyFieldConstraintEditor( type,
                                                                 new ValueChanged() {
                                                                     public void valueChanged(String newValue) {
                                                                         fld.setExpected( newValue );
                                                                     }

                                                                 },
                                                                 fld,
                                                                 sce,
                                                                 this.scenario,
                                                                 this.executionTrace );

            data.setWidget( i,
                            3,
                            cellEditor );

            Image del = new ImageButton( images.deleteItemSmall(),
                                         constants.RemoveThisFieldExpectation(),
                                         new ClickHandler() {
                                             public void onClick(ClickEvent w) {
                                                 if ( Window.confirm( constants.AreYouSureYouWantToRemoveThisFieldExpectation(
                                                                                     fld.getFieldName() ) ) ) {
                                                     vf.getFieldValues().remove( fld );
                                                     FlexTable data = render( vf );
                                                     outer.setWidget( 1,
                                                                      0,
                                                                      data );
                                                 }
                                             }
                                         } );
            data.setWidget( i,
                            4,
                            del );

            if ( showResults && fld.getSuccessResult() != null ) {
                if ( !fld.getSuccessResult().booleanValue() ) {
                    data.setWidget( i,
                                    0,
                                    new Image( images.warning() ) );
                    data.setWidget( i,
                                    5,
                                    new HTML( constants.ActualResult( fld.getActualResult() ) ) );

                    data.getCellFormatter().addStyleName( i,
                                                          5,
                                                          "testErrorValue" ); //NON-NLS

                } else {
                    data.setWidget( i,
                                    0,
                                    new Image( images.testPassed() ) );
                }
            }

        }
        return data;
    }

}
