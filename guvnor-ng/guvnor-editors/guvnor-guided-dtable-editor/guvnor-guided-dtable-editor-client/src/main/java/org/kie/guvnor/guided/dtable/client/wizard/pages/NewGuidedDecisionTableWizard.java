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

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Widget;
import org.kie.guvnor.commons.ui.client.wizards.AbstractWizard;
import org.kie.guvnor.commons.ui.client.wizards.WizardView;
import org.kie.guvnor.commons.ui.client.wizards.WizardPage;
import org.kie.guvnor.datamodel.oracle.DataModelOracle;
import org.kie.guvnor.guided.dtable.client.widget.Validator;
import org.kie.guvnor.guided.dtable.client.wizard.util.NewAssetWizardContext;
import org.kie.guvnor.guided.dtable.client.wizard.util.NewGuidedDecisionTableAssetWizardContext;
import org.kie.guvnor.guided.dtable.model.Analysis;
import org.kie.guvnor.guided.dtable.model.BaseColumn;
import org.kie.guvnor.guided.dtable.model.ConditionCol52;
import org.kie.guvnor.guided.dtable.model.DTCellValue52;
import org.kie.guvnor.guided.dtable.model.GuidedDecisionTable52;

/**
 * Wizard for creating a Guided Decision Table
 */
public class NewGuidedDecisionTableWizard extends AbstractWizard<NewAssetWizardContext> {

    private DataModelOracle oracle;

    private List<WizardPage> pages = new ArrayList<WizardPage>();

    private GuidedDecisionTable52 model = new GuidedDecisionTable52();

    private SummaryPage summaryPage;

    private ColumnExpansionPage columnExpansionPage;

    public NewGuidedDecisionTableWizard( final EventBus eventBus,
                                         final NewGuidedDecisionTableAssetWizardContext context,
                                         final WizardView.Presenter presenter,
                                         final DataModelOracle oracle ) {
        super( eventBus,
               context,
               presenter );
        this.oracle = oracle;

        final Validator validator = new Validator( model.getConditions() );
        this.summaryPage = new SummaryPage( context,
                                            model,
                                            eventBus,
                                            validator );
        this.columnExpansionPage = new ColumnExpansionPage( context,
                                                            model,
                                                            eventBus,
                                                            validator );

        pages.add( summaryPage );
        pages.add( new FactPatternsPage( context,
                                         model,
                                         eventBus,
                                         validator ) );
        pages.add( new FactPatternConstraintsPage( context,
                                                   model,
                                                   eventBus,
                                                   validator ) );
        pages.add( new ActionSetFieldsPage( context,
                                            model,
                                            eventBus,
                                            validator ) );
        pages.add( new ActionInsertFactFieldsPage( context,
                                                   model,
                                                   eventBus,
                                                   validator ) );
        pages.add( columnExpansionPage );

        model.setTableFormat( context.getTableFormat() );

        for ( WizardPage page : pages ) {
            AbstractGuidedDecisionTableWizardPage dtp = (AbstractGuidedDecisionTableWizardPage) page;
            dtp.setDataModelOracle( oracle );
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
        presenter.showSavingIndicator();

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
