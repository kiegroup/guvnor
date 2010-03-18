package org.drools.guvnor.client.qa;

import org.drools.guvnor.client.common.FormStyleLayout;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.rpc.AnalysisReport;
import org.drools.guvnor.client.rpc.AnalysisReportLine;
import org.drools.guvnor.client.rulelist.EditItemEvent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.TreeListener;
import com.google.gwt.user.client.ui.Widget;
import com.gwtext.client.util.Format;

/**
 * Shows the results of an analysis run.
 * @author Michael Neale
 */
public class VerifierResultWidget extends Composite {

    private Constants     constants = GWT.create( Constants.class );
    private EditItemEvent edit      = null;

    public VerifierResultWidget(AnalysisReport report,
                                boolean showFactUsage,
                                EditItemEvent edit) {
        this( report,
              showFactUsage );
        this.edit = edit;
    }

    public VerifierResultWidget(AnalysisReport report,
                                boolean showFactUsage) {
        FormStyleLayout layout = new FormStyleLayout();

        Tree tree = new Tree();

        TreeItem errors = doMessageLines( constants.Errors(),
                                          "images/error.gif",
                                          report.errors );
        tree.addItem( errors );

        TreeItem warnings = doMessageLines( constants.Warnings(),
                                            "images/warning.gif",
                                            report.warnings );
        tree.addItem( warnings );

        TreeItem notes = doMessageLines( constants.Notes(),
                                         "images/note.gif",
                                         report.notes );
        tree.addItem( notes );

        if ( showFactUsage ) {
            tree.addItem( new FactUsagesItem( report.factUsages ) );
        }

        tree.addTreeListener( swapTitleWithUserObject() );
        layout.addRow( tree );

        initWidget( layout );
    }

    private TreeItem doMessageLines(String messageType,
                                    String icon,
                                    AnalysisReportLine[] lines) {

        TreeItem linesItem;

        String summary = Format.format( constants.analysisResultSummary(),
                                        messageType,
                                        "" + lines.length );

        String topicHtml = Format.format( "<img src='{0}' /> &nbsp; {1}",
                                          icon,
                                          summary );

        linesItem = new VerifierMessageLinesItem( topicHtml,
                                                  lines,
                                                  edit );

        return linesItem;
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

}
