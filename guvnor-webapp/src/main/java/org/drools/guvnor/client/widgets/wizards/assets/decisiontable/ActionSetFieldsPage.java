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
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.drools.guvnor.client.factmodel.ModelNameHelper;
import org.drools.guvnor.client.widgets.wizards.assets.NewAssetWizardContext;
import org.drools.ide.common.client.modeldriven.brl.BaseSingleFieldConstraint;
import org.drools.ide.common.client.modeldriven.dt52.ActionSetFieldCol52;
import org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52;
import org.drools.ide.common.client.modeldriven.dt52.Pattern52;

import com.google.gwt.event.shared.EventBus;

/**
 * A page for the guided Decision Table Wizard to define Actions setting fields
 * on previously bound patterns. This page does not use the GuidedDecisionTable
 * model directly; instead maintaining its own Pattern-to-Action associations.
 */
public class ActionSetFieldsPage extends AbstractGuidedDecisionTableWizardPage
    implements
    ActionSetFieldsPageView.Presenter,
    PatternRemovedEvent.Handler {

    private final ModelNameHelper                     modelNameHelper     = new ModelNameHelper();

    private ActionSetFieldsPageView                   view                = new ActionSetFieldsPageViewImpl();

    //GuidedDecisionTable52 maintains a single collection of Actions, linked to patterns by boundName. Thus if multiple 
    //patterns are bound to the same name we cannot distinguish which Actions relate to which Patterns. The Wizard therefore 
    //maintains it's own internal association of Patterns to Actions. IdentityHashMap is used as it is possible to have two 
    //identically defined Patterns (i.e. they have the same property values) although they represent different instances. 
    //A WeakIdentityHashMap would have been more appropriate, however JavaScript has no concept of a weak reference, and so 
    //it can't be implement in GWT. In the absence of such a Map an Event is raised by FactPatternsPage when a Pattern is 
    //removed that is handled here to synchronise the Pattern lists.
    private Map<Pattern52, List<ActionSetFieldCol52>> patternToActionsMap = new IdentityHashMap<Pattern52, List<ActionSetFieldCol52>>();

    private Validator                                 validator;

    public ActionSetFieldsPage(NewAssetWizardContext context,
                                      GuidedDecisionTable52 dtable,
                                      EventBus eventBus) {
        super( context,
               dtable,
               eventBus );
        validator = new Validator( dtable.getConditionPatterns() );

        //Wire-up the events
        eventBus.addHandler( PatternRemovedEvent.TYPE,
                             this );
    }

    //See comments about use of IdentityHashMap in instance member declaration section
    public void onPatternRemoved(PatternRemovedEvent event) {
        patternToActionsMap.remove( event.getPattern() );
    }

    public String getTitle() {
        return constants.DecisionTableWizardActionSetFields();
    }

    public void initialise() {
        if ( sce == null ) {
            return;
        }
        view.setPresenter( this );
        content.setWidget( view );
    }

    public void prepareView() {
        List<Pattern52> availablePatterns = new ArrayList<Pattern52>();
        for ( Pattern52 p : dtable.getConditionPatterns() ) {
            if ( p.getBoundName() != null && !p.getBoundName().equals( "" ) ) {
                availablePatterns.add( p );
            }
        }
        view.setAvailablePatterns( availablePatterns );
    }

    public boolean isComplete() {

        //Have patterns been defined?
        if ( dtable.getConditionPatterns() == null || dtable.getConditionPatterns().size() == 0 ) {
            return false;
        }

        //Have all Actions been defined?
        boolean isValid = true;
        for ( List<ActionSetFieldCol52> actions : patternToActionsMap.values() ) {
            for ( ActionSetFieldCol52 a : actions ) {
                if ( !validator.isActionValid( a ) ) {
                    isValid = false;
                    break;
                }
            }
        }

        view.setHasIncompleteFieldDefinitions( !isValid );
        return isValid;
    }

    public void patternSelected(Pattern52 pattern) {

        //Add fields available
        String type = pattern.getFactType();
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
        view.setAvailableFields( availableFields );

        //Set fields already chosen
        List<ActionSetFieldCol52> actionsForPattern = patternToActionsMap.get( pattern );
        if ( actionsForPattern == null ) {
            actionsForPattern = new ArrayList<ActionSetFieldCol52>();
            patternToActionsMap.put( pattern,
                                     actionsForPattern );
        }
        view.setChosenFields( actionsForPattern );
    }

    @Override
    public void makeResult(GuidedDecisionTable52 dtable) {
        dtable.getActionCols().clear();
        for ( List<ActionSetFieldCol52> actions : patternToActionsMap.values() ) {
            for ( ActionSetFieldCol52 af : actions ) {
                dtable.getActionCols().add( af );
            }
        }
    }

}
