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

import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.resources.DroolsGuvnorImages;
import org.drools.guvnor.client.rpc.AnalysisFactUsage;
import org.drools.guvnor.client.rpc.AnalysisFieldUsage;

import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TreeItem;

class FactUsagesItem extends TreeItem {

    public FactUsagesItem(AnalysisFactUsage[] factUsages) {
        setStyleName( "analysis-Report" );

        setHTML( AbstractImagePrototype.create(DroolsGuvnorImages.INSTANCE.factTemplate()).getHTML() + "<b>" + Constants.INSTANCE.ShowFactUsages() + "</b>" );

        setUserObject( new HTML( AbstractImagePrototype.create(DroolsGuvnorImages.INSTANCE.factTemplate()).getHTML() + "<b>" + Constants.INSTANCE.FactUsages() + ":</b>" ) );

        doFacts( factUsages );
    }

    private void doFacts(AnalysisFactUsage[] factUsages) {
        for ( AnalysisFactUsage factUsage : factUsages ) {

            TreeItem fact = new TreeItem( AbstractImagePrototype.create(DroolsGuvnorImages.INSTANCE.fact()).getHTML() + factUsage.name );
            TreeItem fieldList = doFields( factUsage.fields );
            fact.addItem( fieldList );
            fieldList.setState( true );

            addItem( fact );
            fact.setState( true );
        }
    }

    private TreeItem doFields(AnalysisFieldUsage[] fields) {
        TreeItem fieldList = new TreeItem( Constants.INSTANCE.FieldsUsed() );

        for ( AnalysisFieldUsage fieldUsage : fields ) {
            TreeItem field = new TreeItem( AbstractImagePrototype.create(DroolsGuvnorImages.INSTANCE.field()).getHTML() + fieldUsage.name );
            fieldList.addItem( field );
            TreeItem ruleList = doAffectedRules( fieldUsage );
            field.addItem( ruleList );
            field.setState( true );
        }

        return fieldList;
    }

    private TreeItem doAffectedRules(AnalysisFieldUsage fieldUsage) {
        TreeItem ruleList = new TreeItem( Constants.INSTANCE.ShowRulesAffected() );
        ruleList.setUserObject( new HTML( Constants.INSTANCE.RulesAffected() ) );
        for ( String ruleName : fieldUsage.rules ) {
            ruleList.addItem( new TreeItem( AbstractImagePrototype.create(DroolsGuvnorImages.INSTANCE.ruleAsset()).getHTML() + ruleName ) );
        }
        return ruleList;
    }
}
