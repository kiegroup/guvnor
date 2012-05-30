/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.guvnor.client.widgets.drools.wizards.assets.decisiontable.cells;

import org.drools.guvnor.client.decisiontable.Validator;
import org.drools.guvnor.client.resources.WizardResources;
import org.drools.ide.common.client.modeldriven.brl.BaseSingleFieldConstraint;
import org.drools.ide.common.client.modeldriven.dt52.ConditionCol52;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

/**
 * A cell to display Conditions
 */
public class ConditionCell extends AbstractCell<ConditionCol52> {

    private Validator validator;

    interface ConditionCellTemplate
        extends
        SafeHtmlTemplates {

        @Template("<div class=\"{0}\" >{1}</div>")
        SafeHtml text(String cssStyleName,
                      String message);
    }

    private static final ConditionCellTemplate TEMPLATE = GWT.create( ConditionCellTemplate.class );

    public ConditionCell(Validator validator) {
        this.validator = validator;
    }

    @Override
    public void render(Context context,
                       ConditionCol52 value,
                       SafeHtmlBuilder sb) {
        StringBuilder b = new StringBuilder();

        switch ( value.getConstraintValueType() ) {
            case BaseSingleFieldConstraint.TYPE_LITERAL :
                makeLiteral( b,
                             value );
                break;
            case BaseSingleFieldConstraint.TYPE_RET_VALUE :
                makeFormula( b,
                             value );
                break;
            case BaseSingleFieldConstraint.TYPE_PREDICATE :
                makePredicate( b,
                               value );
        }
        sb.append( TEMPLATE.text( getCssStyleName( value ),
                                  b.toString() ) );
    }

    private void makeLiteral(StringBuilder sb,
                             ConditionCol52 condition) {
        appendHeader( sb,
                      condition );
        sb.append( condition.getFactField() );
    }

    private void makeFormula(StringBuilder sb,
                             ConditionCol52 condition) {
        appendHeader( sb,
                      condition );
        sb.append( condition.getFactField() );
    }

    private void makePredicate(StringBuilder sb,
                               ConditionCol52 condition) {
        appendHeader( sb,
                      condition );
        sb.append( condition.getFactField() );
    }

    private void appendHeader(StringBuilder sb,
                              ConditionCol52 condition) {
        if ( validator.isConditionHeaderValid( condition ) ) {
            sb.append( "[" );
            sb.append( condition.getHeader() );
            sb.append( "] " );
        }
    }

    private String getCssStyleName(ConditionCol52 c) {
        if ( !validator.isConditionValid( c ) ) {
            return WizardResources.INSTANCE.style().wizardDTableValidationError();
        }
        return "";
    }

}
