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
package org.drools.guvnor.client.widgets.drools.wizards.assets.decisiontable;

import java.util.ArrayList;
import java.util.List;

import org.drools.guvnor.client.common.LoadingPopup;
import org.drools.guvnor.client.decisiontable.Validator;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.moduleeditor.drools.SuggestionCompletionCache;
import org.drools.guvnor.client.rpc.NewAssetConfiguration;
import org.drools.guvnor.client.rpc.NewGuidedDecisionTableAssetConfiguration;
import org.drools.guvnor.client.widgets.drools.wizards.assets.AbstractNewAssetWizard;
import org.drools.guvnor.client.widgets.drools.wizards.assets.NewGuidedDecisionTableAssetWizardContext;
import org.drools.guvnor.client.widgets.drools.wizards.assets.decisiontable.RowExpander.RowIterator;
import org.drools.guvnor.client.widgets.wizards.WizardActivityView;
import org.drools.guvnor.client.widgets.wizards.WizardPage;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.dt52.Analysis;
import org.drools.ide.common.client.modeldriven.dt52.ConditionCol52;
import org.drools.ide.common.client.modeldriven.dt52.DTCellValue52;
import org.drools.ide.common.client.modeldriven.dt52.DTColumnConfig52;
import org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Widget;

/**
 * Wizard for creating a Guided Decision Table
 */
public class NewGuidedDecisionTableWizard
        extends AbstractNewAssetWizard<GuidedDecisionTable52> {

    private SuggestionCompletionEngine sce;

    private List<WizardPage>           pages  = new ArrayList<WizardPage>();

    private GuidedDecisionTable52      dtable = new GuidedDecisionTable52();

    private SummaryPage                summaryPage;

    private ColumnExpansionPage        columnExpansionPage;

    public NewGuidedDecisionTableWizard(final ClientFactory clientFactory,
                                        final EventBus eventBus,
                                        final NewGuidedDecisionTableAssetWizardContext context,
                                        final WizardActivityView.Presenter presenter) {
        super( clientFactory,
               eventBus,
               context,
               presenter );

        Validator validator = new Validator( dtable.getConditionPatterns() );
        this.summaryPage = new SummaryPage( context,
                                            dtable,
                                            eventBus,
                                            validator );
        this.columnExpansionPage = new ColumnExpansionPage( context,
                                                            dtable,
                                                            eventBus,
                                                            validator );

        pages.add( summaryPage );
        pages.add( new FactPatternsPage( context,
                                         dtable,
                                         eventBus,
                                         validator ) );
        pages.add( new FactPatternConstraintsPage( context,
                                                   dtable,
                                                   eventBus,
                                                   validator ) );
        pages.add( new ActionSetFieldsPage( context,
                                            dtable,
                                            eventBus,
                                            validator ) );
        pages.add( new ActionInsertFactFieldsPage( context,
                                                   dtable,
                                                   eventBus,
                                                   validator ) );
        pages.add( columnExpansionPage );

        dtable.setTableFormat( context.getTableFormat() );

        SuggestionCompletionCache.getInstance().loadPackage( context.getPackageName(),
                                                                 new Command() {

                                                                     public void execute() {
                                                                         LoadingPopup.close();
                                                                         sce = SuggestionCompletionCache.getInstance().getEngineFromCache( context.getPackageName() );
                                                                         for ( WizardPage page : pages ) {
                                                                             AbstractGuidedDecisionTableWizardPage dtp = (AbstractGuidedDecisionTableWizardPage) page;
                                                                             dtp.setSuggestionCompletionEngine( sce );
                                                                             dtp.initialise();
                                                                         }
                                                                     }

                                                                 } );
    }

    public String getTitle() {
        return "Guided Decision Table Wizard";
    }

    public List<WizardPage> getPages() {
        return this.pages;
    }

    public Widget getPageWidget(int pageNumber) {
        AbstractGuidedDecisionTableWizardPage dtp = (AbstractGuidedDecisionTableWizardPage) this.pages.get( pageNumber );
        Widget w = dtp.asWidget();
        dtp.prepareView();
        return w;
    }

    public int getPreferredHeight() {
        return 500;
    }

    public int getPreferredWidth() {
        return 800;
    }

    public boolean isComplete() {
        for ( WizardPage page : this.pages ) {
            if ( !page.isComplete() ) {
                return false;
            }
        }
        return true;
    }

    public void complete() {

        //Show a "busy" indicator
        presenter.showSavingIndicator();

        //Ensure each page updates the decision table as necessary
        for ( WizardPage page : this.pages ) {
            AbstractGuidedDecisionTableWizardPage gep = (AbstractGuidedDecisionTableWizardPage) page;
            gep.makeResult( dtable );
        }

        //Expand rows
        RowExpander re = new RowExpander( dtable,
                                          sce );

        //Mark columns on which we are to expand (default is to include)
        for ( DTColumnConfig52 c : dtable.getAllColumns() ) {
            re.setExpandColumn( c,
                                false );
        }
        List<ConditionCol52> columns = columnExpansionPage.getColumnsToExpand();
        for ( ConditionCol52 c : columns ) {
            re.setExpandColumn( c,
                                true );
        }

        //Slurp out expanded rows and construct decision table data
        int rowIndex = 0;
        RowIterator ri = re.iterator();
        dtable.initAnalysisColumn();
        while ( ri.hasNext() ) {
            List<String> row = ri.next();
            dtable.getData().add( makeRow( row ) );
            dtable.getAnalysisData().add( new Analysis() );
            rowIndex++;
        }

        //Save it!
        NewAssetConfiguration config = new NewGuidedDecisionTableAssetConfiguration( summaryPage.getAssetName(),
                                                                                     context.getPackageName(),
                                                                                     context.getPackageUUID(),
                                                                                     ((NewGuidedDecisionTableAssetWizardContext) context).getTableFormat(),
                                                                                     context.getDescription(),
                                                                                     context.getInitialCategory(),
                                                                                     context.getFormat() );
        save( config,
              dtable );
    }

    private List<DTCellValue52> makeRow(List<String> row) {
        //All enumerated lists (Guvnor enums, Java enums and Decision Table Value Lists) are handled as Strings
        List<DTCellValue52> dtRow = new ArrayList<DTCellValue52>();
        for ( String value : row ) {
            dtRow.add( new DTCellValue52( value ) );
        }
        return dtRow;
    }

}
