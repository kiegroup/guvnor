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
package org.drools.guvnor.client.widgets.wizards.assets.decisiontable;

import java.util.ArrayList;
import java.util.List;

import org.drools.guvnor.client.factmodel.ModelNameHelper;
import org.drools.guvnor.client.widgets.wizards.assets.NewAssetWizardContext;
import org.drools.ide.common.client.modeldriven.brl.BaseSingleFieldConstraint;
import org.drools.ide.common.client.modeldriven.dt52.ConditionCol52;
import org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52;
import org.drools.ide.common.client.modeldriven.dt52.Pattern52;

import com.google.gwt.event.shared.EventBus;

/**
 * A page for the guided Decision Table Wizard to define Fact Patterns
 */
public class FactPatternConstraintsPage extends AbstractGuidedDecisionTableWizardPage
    implements
    FactPatternConstraintsPageView.Presenter {

    private final ModelNameHelper          modelNameHelper = new ModelNameHelper();

    private FactPatternConstraintsPageView view            = new FactPatternConstraintsPageViewImpl();

    private Validator                      validator;

    public FactPatternConstraintsPage(NewAssetWizardContext context,
                                      GuidedDecisionTable52 dtable,
                                      EventBus eventBus) {
        super( context,
               dtable,
               eventBus );
        validator = new Validator( dtable.getConditionPatterns() );
    }

    public String getTitle() {
        return constants.DecisionTableWizardFactPatternConstraints();
    }

    public void initialise() {
        if ( sce == null ) {
            return;
        }
        view.setPresenter( this );
        content.setWidget( view );
    }

    public void prepareView() {
        view.setAvailablePatterns( dtable.getConditionPatterns() );
    }

    public boolean isComplete() {

        //Have patterns been defined?
        if ( dtable.getConditionPatterns() == null || dtable.getConditionPatterns().size() == 0 ) {
            return false;
        }

        //Have all patterns conditions been defined?
        boolean isValid = true;
        for ( Pattern52 p : dtable.getConditionPatterns() ) {
            for ( ConditionCol52 c : p.getConditions() ) {
                if ( !validator.isConditionValid( c ) ) {
                    isValid = false;
                    break;
                }

            }
        }

        view.setHasIncompleteConditionDefinitions( !isValid );
        return isValid;
    }

    public void patternSelected(Pattern52 pattern) {
        String type = pattern.getFactType();

        //Add Fact fields
        String[] fieldNames = sce.getFieldCompletions( type );
        List<AvailableField> availableFields = new ArrayList<AvailableField>();
        for ( String fieldName : fieldNames ) {
            String fieldType = modelNameHelper.getUserFriendlyTypeName( sce.getFieldClassName( type,
                                                                                               fieldName ) );
            AvailableField field = new AvailableField( fieldName,
                                                       fieldType,
                                                       BaseSingleFieldConstraint.TYPE_LITERAL );
            availableFields.add( field );
        }

        //Add predicates
        AvailableField field = new AvailableField( constants.DecisionTableWizardPredicate(),
                                                   BaseSingleFieldConstraint.TYPE_PREDICATE );
        availableFields.add( field );

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

}
