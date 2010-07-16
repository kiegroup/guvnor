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

package org.drools.guvnor.client.qa;

import org.drools.guvnor.client.rpc.AnalysisFactUsage;
import org.drools.guvnor.client.rpc.AnalysisFieldUsage;
import org.drools.guvnor.client.messages.Constants;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TreeItem;
import com.gwtext.client.util.Format;

/**
 * 
 * @author Toni Rikkola
 */
class FactUsagesItem extends TreeItem {

    private Constants constants = GWT.create( Constants.class );

    public FactUsagesItem(AnalysisFactUsage[] factUsages) {
        setStyleName( "analysis-Report" );

        setHTML( Format.format( "<img src='images/fact_template.gif'/><b>{0}</b>",
                                constants.ShowFactUsages() ) );

        setUserObject( new HTML( Format.format( "<img src='images/fact_template.gif'/><b>{0}:</b>",
                                                constants.FactUsages() ) ) );

        doFacts( factUsages );
    }

    private void doFacts(AnalysisFactUsage[] factUsages) {
        for ( AnalysisFactUsage factUsage : factUsages ) {

            TreeItem fact = new TreeItem( "<img src='images/fact.gif'/>" + factUsage.name );

            TreeItem fieldList = doFields( factUsage.fields );
            fact.addItem( fieldList );
            fieldList.setState( true );

            addItem( fact );
            fact.setState( true );
        }
    }

    private TreeItem doFields(AnalysisFieldUsage[] fields) {
        TreeItem fieldList = new TreeItem( constants.FieldsUsed() );

        for ( AnalysisFieldUsage fieldUsage : fields ) {
            TreeItem field = new TreeItem( "<img src='images/field.gif'/>" + fieldUsage.name );
            fieldList.addItem( field );
            TreeItem ruleList = doAffectedRules( fieldUsage );
            field.addItem( ruleList );
            field.setState( true );
        }

        return fieldList;
    }

    private TreeItem doAffectedRules(AnalysisFieldUsage fieldUsage) {
        TreeItem ruleList = new TreeItem( constants.ShowRulesAffected() );
        ruleList.setUserObject( new HTML( constants.RulesAffected() ) );
        for ( String ruleName : fieldUsage.rules ) {
            ruleList.addItem( new TreeItem( "<img src='images/rule_asset.gif'/>" + ruleName ) );
        }
        return ruleList;
    }
}
