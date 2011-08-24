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

import org.drools.guvnor.client.common.FormStyleLayout;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.resources.Images;
import org.drools.guvnor.client.rpc.AnalysisReport;
import org.drools.guvnor.client.rpc.AnalysisReportLine;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

/**
 * Shows the results of an analysis run.
 */
public class VerifierResultWidget extends Composite {

    private Constants     constants = GWT.create( Constants.class );
    private static Images images    = GWT.create( Images.class );

    public VerifierResultWidget(AnalysisReport report,
                                boolean showFactUsage) {
        
        FormStyleLayout layout = new FormStyleLayout();

        Tree tree = new Tree();

        TreeItem errors = doMessageLines( constants.Errors(),
                                          images.error(),
                                          report.errors );
        tree.addItem( errors );

        TreeItem warnings = doMessageLines( constants.Warnings(),
                                            images.warning(),
                                            report.warnings );
        tree.addItem( warnings );

        TreeItem notes = doMessageLines( constants.Notes(),
                                         images.note(),
                                         report.notes );
        tree.addItem( notes );

        if ( showFactUsage ) {
            tree.addItem( new FactUsagesItem( report.factUsages ) );
        }

        tree.addCloseHandler( new CloseHandler<TreeItem>() {
            public void onClose(CloseEvent<TreeItem> event) {
                swapTitleWithUserObject( event.getTarget() );
            }
        } );
        tree.addOpenHandler( new OpenHandler<TreeItem>() {
            public void onOpen(OpenEvent<TreeItem> event) {
                swapTitleWithUserObject( event.getTarget() );
            }
        } );
        layout.addRow( tree );

        initWidget( layout );
    }

    private TreeItem doMessageLines(String messageType,
                                    ImageResource icon,
                                    AnalysisReportLine[] lines) {

        TreeItem linesItem;

        String summary = constants.analysisResultSummary(messageType, lines.length );

        String topicHtml = AbstractImagePrototype.create(icon).getHTML() + "&nbsp; " + summary;

        linesItem = new VerifierMessageLinesItem( topicHtml,
                                                  lines );

        return linesItem;
    }

    private void swapTitleWithUserObject(TreeItem x) {
        if ( x.getUserObject() != null ) {
            Widget currentW = x.getWidget();
            x.setWidget( (Widget) x.getUserObject() );
            x.setUserObject( currentW );
        }
    }

}
