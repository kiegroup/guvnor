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
import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.drools.guvnor.client.factmodel.ModelNameHelper;
import org.drools.guvnor.client.widgets.wizards.assets.NewAssetWizardContext;
import org.drools.guvnor.client.widgets.wizards.assets.decisiontable.events.ActionInsertFactFieldsDefinedEvent;
import org.drools.guvnor.client.widgets.wizards.assets.decisiontable.events.PatternRemovedEvent;
import org.drools.ide.common.client.modeldriven.brl.BaseSingleFieldConstraint;
import org.drools.ide.common.client.modeldriven.dt52.ActionCol52;
import org.drools.ide.common.client.modeldriven.dt52.ActionInsertFactCol52;
import org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52;
import org.drools.ide.common.client.modeldriven.dt52.Pattern52;

import com.google.gwt.event.shared.EventBus;

/**
 * A page for the guided Decision Table Wizard to define new Facts and fields.
 * This page does not use the GuidedDecisionTable model directly; instead
 * maintaining its own Pattern-to-Action associations.
 */
public class ActionInsertFactFieldsPage extends AbstractGuidedDecisionTableWizardPage
    implements
    ActionInsertFactFieldsPageView.Presenter,
    PatternRemovedEvent.Handler,
    ActionInsertFactFieldsDefinedEvent.Handler {

    private final ModelNameHelper                       modelNameHelper     = new ModelNameHelper();

    private ActionInsertFactFieldsPageView              view;

    //GuidedDecisionTable52 maintains a single collection of Actions, linked to patterns by boundName. Thus if multiple 
    //patterns are bound to the same name we cannot distinguish which Actions relate to which Patterns. The Wizard therefore 
    //maintains it's own internal association of Patterns to Actions. IdentityHashMap is used as it is possible to have two 
    //identically defined Patterns (i.e. they have the same property values) although they represent different instances. 
    //A WeakIdentityHashMap would have been more appropriate, however JavaScript has no concept of a weak reference, and so 
    //it can't be implement in GWT. In the absence of such a Map an Event is raised by FactPatternsPage when a Pattern is 
    //removed that is handled here to synchronise the Pattern lists.
    private Map<Pattern52, List<ActionInsertFactCol52>> patternToActionsMap = new IdentityHashMap<Pattern52, List<ActionInsertFactCol52>>();

    public ActionInsertFactFieldsPage(NewAssetWizardContext context,
                                      GuidedDecisionTable52 dtable,
                                      EventBus eventBus,
                                      Validator validator) {
        super( context,
               dtable,
               eventBus,
               validator );

        //Set-up validator for the pattern-to-action mapping voodoo
        getValidator().setPatternToActionInsertFactFieldsMap( patternToActionsMap );
        this.view = new ActionInsertFactFieldsPageViewImpl( getValidator() );

        //Wire-up the events
        eventBus.addHandler( PatternRemovedEvent.TYPE,
                             this );
        eventBus.addHandler( ActionInsertFactFieldsDefinedEvent.TYPE,
                             this );
    }

    //See comments about use of IdentityHashMap in instance member declaration section
    public void onPatternRemoved(PatternRemovedEvent event) {
        removePattern( event.getPattern() );
    }

    public String getTitle() {
        return constants.DecisionTableWizardActionInsertFacts();
    }

    public void initialise() {
        if ( sce == null ) {
            return;
        }
        view.setPresenter( this );

        //Available types
        List<String> availableTypes = Arrays.asList( sce.getFactTypes() );
        view.setAvailableFactTypes( availableTypes );

        //Existing ActionInsertFactCols (should be empty for a new Decision Table)
        for ( ActionCol52 a : dtable.getActionCols() ) {
            if ( a instanceof ActionInsertFactCol52 ) {
                ActionInsertFactCol52 aif = (ActionInsertFactCol52) a;
                Pattern52 p = lookupExistingInsertFactPattern( aif.getBoundName() );
                List<ActionInsertFactCol52> actions = patternToActionsMap.get( p );
                getValidator().addActionPattern( p );
                actions.add( aif );
            }
        }
        view.setChosenPatterns( new ArrayList<Pattern52>( patternToActionsMap.keySet() ) );

        content.setWidget( view );
    }

    private Pattern52 lookupExistingInsertFactPattern(String boundName) {
        for ( Pattern52 p : patternToActionsMap.keySet() ) {
            if ( p.getBoundName().equals( boundName ) ) {
                return p;
            }
        }
        Pattern52 p = new Pattern52();
        patternToActionsMap.put( p,
                                 new ArrayList<ActionInsertFactCol52>() );
        return p;
    }

    public void prepareView() {
        //Nothing to do here, this page is self-contained
    }

    public boolean isComplete() {

        //Are all Actions defined?
        boolean areActionInsertFieldsDefined = true;
        for ( List<ActionInsertFactCol52> actions : patternToActionsMap.values() ) {
            for ( ActionInsertFactCol52 a : actions ) {
                if ( !getValidator().isActionValid( a ) ) {
                    areActionInsertFieldsDefined = false;
                    break;
                }
            }
        }

        //Signal Action Insert Fact Fields to other pages
        ActionInsertFactFieldsDefinedEvent eventFactFields = new ActionInsertFactFieldsDefinedEvent( areActionInsertFieldsDefined );
        eventBus.fireEvent( eventFactFields );

        return areActionInsertFieldsDefined;
    }

    public void onActionInsertFactFieldsDefined(ActionInsertFactFieldsDefinedEvent event) {
        view.setAreActionInsertFactFieldsDefined( event.getAreActionInsertFactFieldsDefined() );
    }

    public void addPattern(Pattern52 pattern) {
        patternToActionsMap.put( pattern,
                                 new ArrayList<ActionInsertFactCol52>() );
        getValidator().addActionPattern( pattern );
    }

    public void removePattern(Pattern52 pattern) {
        patternToActionsMap.remove( pattern );
        getValidator().removeActionPattern( pattern );
    }

    public void selectPattern(Pattern52 pattern) {

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
        List<ActionInsertFactCol52> actionsForPattern = patternToActionsMap.get( pattern );
        if ( actionsForPattern == null ) {
            actionsForPattern = new ArrayList<ActionInsertFactCol52>();
            patternToActionsMap.put( pattern,
                                     actionsForPattern );
        }
        view.setChosenFields( actionsForPattern );
    }

    @Override
    public void makeResult(GuidedDecisionTable52 dtable) {
        for ( List<ActionInsertFactCol52> actions : patternToActionsMap.values() ) {
            for ( ActionInsertFactCol52 af : actions ) {
                dtable.getActionCols().add( af );
            }
        }
    }

}
