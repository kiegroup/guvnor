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
