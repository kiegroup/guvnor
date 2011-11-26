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

package org.drools.guvnor.client.asseteditor.drools.modeldriven.ui.factPattern;

import org.drools.guvnor.client.asseteditor.drools.modeldriven.HumanReadable;
import org.drools.guvnor.client.asseteditor.drools.modeldriven.ui.CEPOperatorsDropdown;
import org.drools.guvnor.client.asseteditor.drools.modeldriven.ui.ConstraintValueEditor;
import org.drools.guvnor.client.asseteditor.drools.modeldriven.ui.OperatorSelection;
import org.drools.guvnor.client.asseteditor.drools.modeldriven.ui.RuleModeller;
import org.drools.guvnor.client.common.ImageButton;
import org.drools.guvnor.client.common.SmallLabel;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.resources.Images;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.brl.BaseSingleFieldConstraint;
import org.drools.ide.common.client.modeldriven.brl.ConnectiveConstraint;
import org.drools.ide.common.client.modeldriven.brl.FactPattern;
import org.drools.ide.common.client.modeldriven.brl.SingleFieldConstraint;
import org.drools.ide.common.client.modeldriven.brl.SingleFieldConstraintEBLeftSide;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class Connectives {

    private Constants     constants = ((Constants) GWT.create( Constants.class ));
    private static Images images    = GWT.create( Images.class );

    private RuleModeller  modeller;
    private FactPattern   pattern;
    private Boolean       isReadOnly;

    public Connectives(RuleModeller modeller,
                       FactPattern pattern,
                       Boolean isReadOnly) {
        this.pattern = pattern;
        this.modeller = modeller;
        this.isReadOnly = isReadOnly;
    }

    /**
     * Returns the pattern.
     */
    public FactPattern getPattern() {
        return pattern;
    }

    /**
     * Returns the completions.
     */
    public SuggestionCompletionEngine getCompletions() {
        return this.modeller.getSuggestionCompletions();
    }

    public Widget connectives(SingleFieldConstraint c,
                              String factClass) {
        HorizontalPanel hp = new HorizontalPanel();
        if ( c.connectives != null && c.connectives.length > 0 ) {
            hp.setVerticalAlignment( HasVerticalAlignment.ALIGN_MIDDLE );
            hp.setHorizontalAlignment( HasHorizontalAlignment.ALIGN_CENTER );
            for ( int i = 0; i < c.connectives.length; i++ ) {

                ConnectiveConstraint con = c.connectives[i];

                hp.add( connectiveOperatorDropDown( con,
                                                    c ) );
                hp.add( connectiveValueEditor( con,
                                               factClass,
                                               c.getFieldName() ) );

                if ( !isReadOnly ) {
                    Image clear = new ImageButton( images.deleteItemSmall() );
                    clear.setTitle( constants.RemoveThisRestriction() );
                    clear.addClickHandler( createClickHandlerForClearImageButton( c,
                                                                                  i ) );
                    hp.add( clear );
                }

            }
        }
        return hp;

    }

    private Widget connectiveValueEditor(final BaseSingleFieldConstraint con,
                                         String factClass,
                                         String fieldName) {

        return new ConstraintValueEditor( pattern,
                                          fieldName,
                                          con,
                                          this.modeller,
                                          isReadOnly );
    }

    private Widget connectiveOperatorDropDown(final ConnectiveConstraint cc,
                                              final SingleFieldConstraint sfc) {

        if ( !isReadOnly ) {
            String fieldName = cc.getFieldName();
            String factType = this.pattern.getFactType();

            if ( fieldName != null && fieldName.contains( "." ) ) {
                int index = fieldName.indexOf( "." );
                factType = fieldName.substring( 0,
                                                index );
                fieldName = fieldName.substring( index + 1 );
            }

            if ( sfc instanceof SingleFieldConstraintEBLeftSide ) {
                SingleFieldConstraintEBLeftSide sfcex = (SingleFieldConstraintEBLeftSide) sfc;
                factType = sfcex.getExpressionLeftSide().getPreviousClassType();
            }

            String[] operators = this.getCompletions().getConnectiveOperatorCompletions( factType,
                                                                                         fieldName );
            CEPOperatorsDropdown w = new CEPOperatorsDropdown( operators,
                                                               cc );

            w.addValueChangeHandler( new ValueChangeHandler<OperatorSelection>() {

                public void onValueChange(ValueChangeEvent<OperatorSelection> event) {
                    OperatorSelection selection = event.getValue();
                    String selected = selection.getValue();
                    cc.setOperator( selected );
                }
            } );

            return w;

        } else {
            SmallLabel sl = new SmallLabel( "<b>" + (cc.getOperator() == null ? constants.pleaseChoose() : HumanReadable.getOperatorDisplayName( cc.getOperator() )) + "</b>" );
            return sl;
        }
    }

    private ClickHandler createClickHandlerForClearImageButton(final SingleFieldConstraint sfc,
                                                               final int index) {
        return new ClickHandler() {

            public void onClick(ClickEvent event) {
                if ( Window.confirm( constants.RemoveThisItem() ) ) {
                    sfc.removeConnective( index );
                    modeller.makeDirty();
                    modeller.refreshWidget();
                }
            }
        };
    }
}
