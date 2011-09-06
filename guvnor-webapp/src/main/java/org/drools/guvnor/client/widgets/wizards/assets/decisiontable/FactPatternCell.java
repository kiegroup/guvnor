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
package org.drools.guvnor.client.widgets.wizards.assets.decisiontable;

import org.drools.guvnor.client.resources.WizardResources;
import org.drools.guvnor.client.widgets.wizards.assets.decisiontable.FactPatternCell.Pattern52Wrapper;
import org.drools.ide.common.client.modeldriven.dt52.Pattern52;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

/**
 * A cell to display Fact Patterns
 */
public class FactPatternCell extends AbstractCell<Pattern52Wrapper> {

    interface FactPatternCellTemplate
        extends
        SafeHtmlTemplates {

        @Template("<div class=\"{0}\" >{1}</div>")
        SafeHtml text(String cssStyleName,
                      String message);
    }

    private static final FactPatternCellTemplate TEMPLATE = GWT.create( FactPatternCellTemplate.class );

    @Override
    public void render(Context context,
                       Pattern52Wrapper value,
                       SafeHtmlBuilder sb) {
        String binding = value.pattern.getBoundName();
        StringBuilder b = new StringBuilder();
        if ( binding == null || "".equals( binding ) ) {
            b.append( value.pattern.getFactType() );
        } else {
            b.append( value.pattern.getBoundName() );
            b.append( " : " );
            b.append( value.pattern.getFactType() );
        }
        sb.append( TEMPLATE.text( getCssStyleName(value),
                                  b.toString() ) );
    }

    private String getCssStyleName(Pattern52Wrapper pw) {
        if(pw.isDuplicateBinding) {
            return WizardResources.INSTANCE.style().wizardDTableDuplicatePattern();
        }
        return "";
    }

    public static class Pattern52Wrapper {
        
        private Pattern52 pattern;
        private boolean   isDuplicateBinding;

        public Pattern52Wrapper(Pattern52 pattern) {
            this.pattern=pattern;
        }
        
        public boolean isDuplicateBinding() {
            return isDuplicateBinding;
        }

        public void setDuplicateBinding(boolean isDuplicateBinding) {
            this.isDuplicateBinding = isDuplicateBinding;
        }

        public Pattern52 getPattern() {
            return pattern;
        }

    }

}
