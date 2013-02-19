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
package org.kie.guvnor.guided.dtable.client.wizard.pages;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.Widget;
import org.kie.guvnor.commons.ui.client.wizards.Wizard;
import org.kie.guvnor.commons.ui.client.wizards.WizardPage;
import org.kie.guvnor.datamodel.oracle.DataModelOracle;
import org.kie.guvnor.guided.dtable.client.widget.Validator;
import org.kie.guvnor.guided.dtable.client.wizard.util.NewGuidedDecisionTableAssetWizardContext;
import org.kie.guvnor.guided.dtable.model.Analysis;
import org.kie.guvnor.guided.dtable.model.BaseColumn;
import org.kie.guvnor.guided.dtable.model.ConditionCol52;
import org.kie.guvnor.guided.dtable.model.DTCellValue52;
import org.kie.guvnor.guided.dtable.model.GuidedDecisionTable52;

/**
 * Wizard for creating a Guided Decision Table
 */
public class NewGuidedDecisionTableWizard implements Wizard<NewGuidedDecisionTableAssetWizardContext> {

    private DataModelOracle oracle;

    private final List<WizardPage> pages = new ArrayList<WizardPage>();

    private final GuidedDecisionTable52 model = new GuidedDecisionTable52();

    private final Validator validator = new Validator( model.getConditions() );

    private SummaryPage summaryPage;

    private ColumnExpansionPage columnExpansionPage;

    public NewGuidedDecisionTableWizard( final NewGuidedDecisionTableAssetWizardContext context ) {
        this.summaryPage = new SummaryPage();
        this.columnExpansionPage = new ColumnExpansionPage();

        pages.add( summaryPage );
        pages.add( new FactPatternsPage() );
        pages.add( new FactPatternConstraintsPage() );
        pages.add( new ActionSetFieldsPage() );
        pages.add( new ActionInsertFactFieldsPage() );
        pages.add( columnExpansionPage );

        model.setTableFormat( context.getTableFormat() );

    }

    public void setContent( final NewGuidedDecisionTableAssetWizardContext context,
                            final DataModelOracle oracle ) {
        this.oracle = oracle;
        for ( WizardPage page : pages ) {
            final AbstractGuidedDecisionTableWizardPage dtp = (AbstractGuidedDecisionTableWizardPage) page;
            dtp.setContent( context,
                            oracle,
                            model,
                            validator );
            dtp.initialise();
        }
    }

    public String getTitle() {
        return "Guided Decision Table Wizard";
    }

    public List<WizardPage> getPages() {
        return this.pages;
    }

    public Widget getPageWidget( final int pageNumber ) {
        final AbstractGuidedDecisionTableWizardPage dtp = (AbstractGuidedDecisionTableWizardPage) this.pages.get( pageNumber );
        final Widget w = dtp.asWidget();
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
        //TODO presenter.showSavingIndicator();

        //Ensure each page updates the decision table as necessary
        for ( WizardPage page : this.pages ) {
            AbstractGuidedDecisionTableWizardPage gep = (AbstractGuidedDecisionTableWizardPage) page;
            gep.makeResult( model );
        }

        //Expand rows
        final RowExpander re = new RowExpander( model,
                                                oracle );

        //Mark columns on which we are to expand (default is to include all)
        for ( BaseColumn c : model.getExpandedColumns() ) {
            re.setExpandColumn( c,
                                false );
        }
        final List<ConditionCol52> columns = columnExpansionPage.getColumnsToExpand();
        for ( ConditionCol52 c : columns ) {
            re.setExpandColumn( c,
                                true );
        }

        //Slurp out expanded rows and construct decision table data
        int rowIndex = 0;
        final RowExpander.RowIterator ri = re.iterator();
        model.initAnalysisColumn();
        while ( ri.hasNext() ) {
            List<DTCellValue52> row = ri.next();
            model.getData().add( row );
            model.getData().get( rowIndex ).get( 0 ).setNumericValue( new BigDecimal( rowIndex + 1 ) );
            model.getAnalysisData().add( new Analysis() );
            rowIndex++;
        }

        //Save it!
        //NewGuidedDecisionTableAssetConfiguration config = new NewGuidedDecisionTableAssetConfiguration( summaryPage.getAssetName(),
        //                                                                                                context.getPackageName(),
        //                                                                                                context.getPackageUUID(),
        //                                                                                                context.getDescription(),
        //                                                                                                context.getInitialCategory(),
        //                                                                                                context.getFormat(),
        //                                                                                                dtable );
        //save( config,
        //      dtable );
    }

}
