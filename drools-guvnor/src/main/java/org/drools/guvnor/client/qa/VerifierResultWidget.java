package org.drools.guvnor.client.qa;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.*;
import com.gwtext.client.util.Format;
import org.drools.guvnor.client.common.FormStyleLayout;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.rpc.AnalysisFactUsage;
import org.drools.guvnor.client.rpc.AnalysisFieldUsage;
import org.drools.guvnor.client.rpc.AnalysisReport;
import org.drools.guvnor.client.rpc.AnalysisReportLine;

/**
 * Shows the results of an analysis run.
 * @author Michael Neale
 */
public class VerifierResultWidget extends Composite {
    private Constants constants = GWT.create( Constants.class );

    public VerifierResultWidget(AnalysisReport report,
                                boolean showFactUsage) {
        FormStyleLayout layout = new FormStyleLayout();

        Tree tree = new Tree();
        
        tree.addItem( renderItems( report.errors,
                                   "images/error.gif",
                                   constants.Errors() ) );
        tree.addItem( renderItems( report.warnings,
                                   "images/warning.gif",
                                   constants.Warnings() ) );
        tree.addItem( renderItems( report.notes,
                                   "images/note.gif",
                                   constants.Notes() ) );
        if ( showFactUsage ) {
            tree.addItem( renderFactUsage( report.factUsages ) );
        }
        tree.addTreeListener( swapTitleWithUserObject() );
        layout.addRow( tree );

        initWidget( layout );
    }

    private TreeListener swapTitleWithUserObject() {
        return new TreeListener() {
            public void onTreeItemSelected(TreeItem x) {
            }

            //swap around with user object to toggle
            public void onTreeItemStateChanged(TreeItem x) {
                if ( x.getUserObject() != null ) {
                    Widget currentW = x.getWidget();
                    x.setWidget( (Widget) x.getUserObject() );
                    x.setUserObject( currentW );
                }
            }
        };
    }

    private TreeItem renderFactUsage(AnalysisFactUsage[] factUsages) {

        TreeItem root = new TreeItem( new HTML( "<img src='images/fact_template.gif'/><b>" + constants.ShowFactUsages() + "</b>" ) );
        root.setUserObject( new HTML( "<img src='images/fact_template.gif'/><b>" + constants.FactUsages() + ":</b>" ) );
        root.setStyleName( "analysis-Report" );

        for ( int i = 0; i < factUsages.length; i++ ) {

            AnalysisFactUsage fu = factUsages[i];
            TreeItem fact = new TreeItem( new HTML( "<img src='images/fact.gif'/>" + fu.name ) );

            TreeItem fieldList = new TreeItem( new HTML( constants.FieldsUsed() ) );

            for ( int j = 0; j < fu.fields.length; j++ ) {
                AnalysisFieldUsage fiu = fu.fields[j];
                TreeItem field = new TreeItem( new HTML( "<img src='images/field.gif'/>" + fiu.name ) );
                fieldList.addItem( field );
                TreeItem ruleList = new TreeItem( new HTML( constants.ShowRulesAffected() ) );
                ruleList.setUserObject( new HTML( constants.RulesAffected() ) );
                for ( int k = 0; k < fiu.rules.length; k++ ) {
                    ruleList.addItem( new TreeItem( new HTML( "<img src='images/rule_asset.gif'/>" + fiu.rules[k] ) ) );
                }
                field.addItem( ruleList );
                field.setState( true );
            }

            fact.addItem( fieldList );
            fieldList.setState( true );

            root.addItem( fact );
            fact.setState( true );
        }

        return root;
    }

    private TreeItem renderItems(AnalysisReportLine[] lines,
                                 String icon,
                                 String msg) {
        if ( lines.length == 0 ) {
            TreeItem nil = new TreeItem( new HTML( "<i>No " + msg + "</i>" ) );
            nil.setStyleName( "analysis-Report" );
            return nil;
        }

        String m = Format.format( constants.analysisResultSummary(),
                                  new String[]{msg, "" + lines.length} );
        TreeItem lineNode = new TreeItem( new HTML( "<img src='" + icon + "' /> &nbsp;  " + m ) );

        lineNode.setStyleName( "analysis-Report" );

        for ( int i = 0; i < lines.length; i++ ) {
            AnalysisReportLine r = lines[i];
            TreeItem w = new TreeItem( new HTML( r.description ) );
            w.addItem( new TreeItem( new HTML( "<b>" + constants.Reason() + ":</b>&nbsp;" + r.reason ) ) );
            TreeItem causes = new TreeItem( new HTML( "<b>" + constants.Cause() + ":</b>" ) );

            for ( int j = 0; j < r.cause.length; j++ ) {
                causes.addItem( new HTML( r.cause[j] ) );
            }
            if ( r.cause.length > 0 ) {
                w.addItem( causes );
                causes.setState( true );
            }
            lineNode.addItem( w );
        }
        lineNode.setState( true );
        return lineNode;
    }

}
