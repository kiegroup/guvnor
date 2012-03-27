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
import java.util.Arrays;
import java.util.List;

import org.drools.guvnor.client.asseteditor.drools.factmodel.ModelNameHelper;
import org.drools.guvnor.client.decisiontable.DTCellValueWidgetFactory;
import org.drools.guvnor.client.decisiontable.Validator;
import org.drools.guvnor.client.messages.Constants;
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
        return Constants.INSTANCE.DecisionTableWizardFactPatternConstraints();
    }

    public void initialise() {
        if ( sce == null ) {
            return;
        }
        view.setPresenter( this );

        //Set-up a factory for value editors
        view.setDTCellValueWidgetFactory( DTCellValueWidgetFactory.getInstance( dtable,
                                                                                sce,
                                                                                false,
                                                                                allowEmptyValues() ) );
        content.setWidget( view );
    }

    public void prepareView() {
        //Setup the available patterns, that could have changed each time this page is visited
        view.setAvailablePatterns( this.dtable.getPatterns() );
    }

    public boolean isComplete() {

        //Have all patterns conditions been defined?
        boolean areConditionsDefined = true;
        for ( Pattern52 p : dtable.getPatterns() ) {
            for ( ConditionCol52 c : p.getChildColumns() ) {
                if ( !getValidator().isConditionValid( c ) ) {
                    areConditionsDefined = false;
                    break;
                }
            }
        }

        //Signal Condition definitions to other pages
        ConditionsDefinedEvent event = new ConditionsDefinedEvent( areConditionsDefined );
        eventBus.fireEventFromSource( event,
                                      context );

        return areConditionsDefined;
    }

    public void onDuplicatePatterns(DuplicatePatternsEvent event) {
        if ( event.getSource() != context ) {
            return;
        }
        view.setArePatternBindingsUnique( event.getArePatternBindingsUnique() );
    }

    public void onConditionsDefined(ConditionsDefinedEvent event) {
        if ( event.getSource() != context ) {
            return;
        }
        view.setAreConditionsDefined( event.getAreConditionsDefined() );
    }

    public void selectPattern(Pattern52 pattern) {

        //Pattern is null when programmatically deselecting an item
        if ( pattern == null ) {
            return;
        }

        //Add Fact fields
        String type = pattern.getFactType();
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
            AvailableField field = new AvailableField( Constants.INSTANCE.DecisionTableWizardPredicate(),
                                                           BaseSingleFieldConstraint.TYPE_PREDICATE );
            availableFields.add( field );
        }

        view.setAvailableFields( availableFields );
        view.setChosenConditions( pattern.getChildColumns() );
    }

    public void setChosenConditions(Pattern52 pattern,
                                    List<ConditionCol52> conditions) {
        pattern.getChildColumns().clear();
        pattern.getChildColumns().addAll( conditions );
    }

    public String[] getOperatorCompletions(Pattern52 selectedPattern,
                                           ConditionCol52 selectedCondition) {
        //The "in" (a comma separated list) operator is provided by the SCE when the Field type is STRING
        final String factType = selectedPattern.getFactType();
        final String factField = selectedCondition.getFactField();
        String[] ops = this.sce.getOperatorCompletions( factType,
                                                        factField );

        //We need to add "in" manually if the Calculation Type is a Literal
        final List<String> filteredOps = new ArrayList<String>();
        for ( String op : ops ) {
            filteredOps.add( op );
        }
        if ( BaseSingleFieldConstraint.TYPE_LITERAL == selectedCondition.getConstraintValueType() ) {
            if ( !filteredOps.contains( "in" ) ) {
                filteredOps.add( "in" );
            }
        } else {
            filteredOps.remove( "in" );
        }

        //But remove "in" if the Fact\Field is enumerated
        if ( sce.hasEnums( factType,
                           factField ) ) {
            filteredOps.remove( "in" );
        }

        final String[] displayOps = new String[filteredOps.size()];
        filteredOps.toArray( displayOps );

        return displayOps;
    }

    public TableFormat getTableFormat() {
        return dtable.getTableFormat();
    }

    @Override
    public boolean hasEnum(Pattern52 selectedPattern,
                           ConditionCol52 selectedCondition) {
        final String factType = selectedPattern.getFactType();
        final String factField = selectedCondition.getFactField();
        return sce.hasEnums( factType,
                             factField );
    }

    @Override
    public boolean requiresValueList(Pattern52 selectedPattern,
                                     ConditionCol52 selectedCondition) {
        //Don't show a Value List if either the Fact\Field is empty
        final String factType = selectedPattern.getFactType();
        final String factField = selectedCondition.getFactField();
        boolean enableValueList = !((factType == null || "".equals( factType )) || (factField == null || "".equals( factField )));

        //Don't show Value List if operator does not accept one
        if ( enableValueList ) {
            enableValueList = validator.doesOperatorAcceptValueList( selectedCondition );
        }

        //Don't show a Value List if the Fact\Field has an enumeration
        if ( enableValueList ) {
            enableValueList = !sce.hasEnums( factType,
                                             factField );
        }
        return enableValueList;
    }

    @Override
    public void assertDefaultValue(final ConditionCol52 selectedPattern) {
        final List<String> valueList = Arrays.asList( dtable.getValueList( selectedPattern ) );
        final String defaultValue = utilities.asString( selectedPattern.getDefaultValue() );
        if ( !valueList.contains( defaultValue ) ) {
            selectedPattern.getDefaultValue().clearValues();
        }
    }

}
