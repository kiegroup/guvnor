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

import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.rpc.AnalysisReportLine;
import org.drools.guvnor.client.rpc.Cause;
import org.drools.guvnor.client.rulelist.EditItemEvent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author Toni Rikkola
 */
class VerifierMessageLinesItem extends TreeItem {

    private Constants     constants = GWT.create( Constants.class );

    private EditItemEvent edit;

    public VerifierMessageLinesItem(String topicHtml,
                                    AnalysisReportLine[] lines,
                                    EditItemEvent edit) {

        this.edit = edit;

        setStyleName( "analysis-Report" );
        setHTML( topicHtml );

        for ( AnalysisReportLine line : lines ) {
            TreeItem report = new TreeItem( new HTML( line.description ) );
            if ( line.reason != null ) {
                report.addItem( new TreeItem( new HTML( "<b>" + constants.Reason() + ":</b>&nbsp;" + line.reason ) ) );
            }

            TreeItem impactedRules = doImpactedRules( line );
            report.addItem( impactedRules );

            if ( line.causes.length > 0 ) {
                TreeItem causes = doCauses( new HTML( "<b>" + constants.Causes() + ":</b>" ),
                                            line.causes );

                report.addItem( causes );
                causes.setState( true );
            }

            addItem( report );
        }

        setState( true );

    }

    private TreeItem doImpactedRules(AnalysisReportLine line) {

        TreeItem impactedRules = new TreeItem( new HTML( "<b>" + constants.ImpactedRules() + ":</b>&nbsp;" ) );

        for ( final String ruleAssetGuid : line.impactedRules.keySet() ) {
            HTML rule = new HTML( "<img src='images/rule_asset.gif'/>" + line.impactedRules.get( ruleAssetGuid ) );
            rule.addClickListener( new ClickListener() {
                public void onClick(Widget arg0) {
                    edit.open( ruleAssetGuid );
                }
            } );
            impactedRules.addItem( rule );
        }

        return impactedRules;
    }

    private TreeItem doCauses(HTML title,
                              Cause[] causes) {

        TreeItem treeItem = new TreeItem( title );

        for ( Cause cause : causes ) {
            treeItem.addItem( doCauses( new HTML( cause.getCause() ),
                                        cause.getCauses() ) );
        }

        return treeItem;
    }
}
