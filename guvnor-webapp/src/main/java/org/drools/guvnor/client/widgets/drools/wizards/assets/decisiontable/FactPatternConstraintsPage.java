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

import org.drools.guvnor.client.asseteditor.drools.factmodel.ModelNameHelper;
import org.drools.guvnor.client.decisiontable.DTCellValueWidgetFactory;
import org.drools.guvnor.client.decisiontable.Validator;
import org.drools.guvnor.client.widgets.drools.wizards.assets.NewAssetWizardContext;
import org.drools.guvnor.client.widgets.drools.wizards.assets.decisiontable.events.ConditionsDefinedEvent;
import org.drools.guvnor.client.widgets.drools.wizards.assets.decisiontable.events.DuplicatePatternsEvent;
import org.drools.ide.common.client.modeldriven.brl.BaseSingleFieldConstraint;
import org.drools.ide.common.client.modeldriven.dt52.ConditionCol52;
import org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52;
import org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52.TableFormat;
import org.drools.ide.common.client.modeldriven.dt52.Pattern52;

import com.google.gwt.event.shared.EventBus;

/**
 * A page for the guided Decision Table Wizard to define Fact Pattern
 * Constraints
 */
public class FactPatternConstraintsPage extends AbstractGuidedDecisionTableWizardPage
    implements
    FactPatternConstraintsPageView.Presenter,
    DuplicatePatternsEvent.Handler,
    ConditionsDefinedEvent.Handler {

    private final ModelNameHelper          modelNameHelper = new ModelNameHelper();

    private FactPatternConstraintsPageView view;

    public FactPatternConstraintsPage(NewAssetWizardContext context,
                                      GuidedDecisionTable52 dtable,
                                      EventBus eventBus,
                                      Validator validator) {
        super( context,
               dtable,
               eventBus,
               validator );
        this.view = new FactPatternConstraintsPageViewImpl( getValidator() );

        //Wire-up the events
        eventBus.addHandler( DuplicatePatternsEvent.TYPE,
                             this );
        eventBus.addHandler( ConditionsDefinedEvent.TYPE,
                             this );
    }

    public String getTitle() {
        return constants.DecisionTableWizardFactPatternConstraints();
    }

    public void initialise() {
        if ( sce == null ) {
            return;
        }
        view.setPresenter( this );
        view.setDTCellValueWidgetFactory( new DTCellValueWidgetFactory( dtable,
                                                                        sce ) );
        content.setWidget( view );
    }

    public void prepareView() {
        //Setup the available patterns, that could have changed each time this page is visited
        view.setAvailablePatterns( dtable.getConditionPatterns() );
    }

    public boolean isComplete() {

        //Have all patterns conditions been defined?
        boolean areConditionsDefined = true;
        for ( Pattern52 p : dtable.getConditionPatterns() ) {
            for ( ConditionCol52 c : p.getConditions() ) {
                if ( !getValidator().isConditionValid( c ) ) {
                    areConditionsDefined = false;
                    break;
                }
            }
        }

        //Signal Condition definitions to other pages
        ConditionsDefinedEvent event = new ConditionsDefinedEvent( areConditionsDefined );
        eventBus.fireEvent( event );

        return areConditionsDefined;
    }

    public void onDuplicatePatterns(DuplicatePatternsEvent event) {
        view.setArePatternBindingsUnique( event.getArePatternBindingsUnique() );
    }

    public void onConditionsDefined(ConditionsDefinedEvent event) {
        view.setAreConditionsDefined( event.getAreConditionsDefined() );
    }

    public void selectPattern(Pattern52 pattern) {
        String type = pattern.getFactType();

        //Add Fact fields
        String[] fieldNames = sce.getFieldCompletions( type );
        List<AvailableField> availableFields = new ArrayList<AvailableField>();
        for ( String fieldName : fieldNames ) {
            String fieldType = sce.getFieldType( type,
                                                 fieldName );
            String fieldDisplayType = modelNameHelper.getUserFriendlyTypeName( fieldType );
            AvailableField field = new AvailableField( fieldName,
                                                       fieldType,
                                                       fieldDisplayType,
                                                       BaseSingleFieldConstraint.TYPE_LITERAL );
            availableFields.add( field );
        }

        //Add predicates
        if ( dtable.getTableFormat() == TableFormat.EXTENDED_ENTRY ) {
            AvailableField field = new AvailableField( constants.DecisionTableWizardPredicate(),
                                                       BaseSingleFieldConstraint.TYPE_PREDICATE );
            availableFields.add( field );
        }

        view.setAvailableFields( availableFields );
        view.setChosenConditions( pattern.getConditions() );
    }

    public void setChosenConditions(Pattern52 pattern,
                                    List<ConditionCol52> conditions) {
        pattern.getConditions().clear();
        pattern.getConditions().addAll( conditions );
    }

    public String[] getOperatorCompletions(Pattern52 selectedPattern,
                                           ConditionCol52 selectedCondition) {
        String[] ops = sce.getOperatorCompletions( selectedPattern.getFactType(),
                                                   selectedCondition.getFactField() );
        return ops;
    }

    public TableFormat getTableFormat() {
        return dtable.getTableFormat();
    }

}
