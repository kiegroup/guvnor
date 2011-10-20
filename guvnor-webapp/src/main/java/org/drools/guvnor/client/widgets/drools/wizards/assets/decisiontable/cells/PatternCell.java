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
import org.drools.ide.common.client.modeldriven.dt52.Pattern52;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

/**
 * A cell to display a Fact Pattern
 */
public class PatternCell extends AbstractCell<Pattern52> {

    protected Validator validator;

    interface FactPatternCellTemplate
        extends
        SafeHtmlTemplates {

        @Template("<div class=\"{0}\" >{1}</div>")
        SafeHtml text(String cssStyleName,
                      String message);
    }

    private static final FactPatternCellTemplate TEMPLATE = GWT.create( FactPatternCellTemplate.class );

    public PatternCell(Validator validator) {
        this.validator = validator;
    }

    @Override
    public void render(Context context,
                       Pattern52 value,
                       SafeHtmlBuilder sb) {
        String binding = value.getBoundName();
        StringBuilder b = new StringBuilder();
        if ( binding == null || "".equals( binding ) ) {
            b.append( value.getFactType() );
        } else {
            b.append( value.getBoundName() );
            b.append( " : " );
            b.append( value.getFactType() );
        }
        sb.append( TEMPLATE.text( getCssStyleName( value ),
                                  b.toString() ) );
    }

    protected String getCssStyleName(Pattern52 p) {
        if ( !validator.isPatternBindingUnique( p ) ) {
            return WizardResources.INSTANCE.style().wizardDTableValidationError();
        }
        return "";
    }

}
