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
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.drools.guvnor.client.asseteditor.drools.factmodel.ModelNameHelper;
import org.drools.guvnor.client.decisiontable.DTCellValueWidgetFactory;
import org.drools.guvnor.client.decisiontable.Validator;
import org.drools.guvnor.client.widgets.drools.wizards.assets.NewAssetWizardContext;
import org.drools.guvnor.client.widgets.drools.wizards.assets.decisiontable.events.ActionSetFieldsDefinedEvent;
import org.drools.guvnor.client.widgets.drools.wizards.assets.decisiontable.events.DuplicatePatternsEvent;
import org.drools.guvnor.client.widgets.drools.wizards.assets.decisiontable.events.PatternRemovedEvent;
import org.drools.ide.common.client.modeldriven.brl.BaseSingleFieldConstraint;
import org.drools.ide.common.client.modeldriven.dt52.ActionCol52;
import org.drools.ide.common.client.modeldriven.dt52.ActionSetFieldCol52;
import org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52;
import org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52.TableFormat;
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
    PatternRemovedEvent.Handler,
    DuplicatePatternsEvent.Handler,
    ActionSetFieldsDefinedEvent.Handler {

    private final ModelNameHelper                     modelNameHelper     = new ModelNameHelper();

    private ActionSetFieldsPageView                   view;

    //GuidedDecisionTable52 maintains a single collection of Actions, linked to patterns by boundName. Thus if multiple 
    //patterns are bound to the same name we cannot distinguish which Actions relate to which Patterns. The Wizard therefore 
    //maintains it's own internal association of Patterns to Actions. IdentityHashMap is used as it is possible to have two 
    //identically defined Patterns (i.e. they have the same property values) although they represent different instances. 
    //A WeakIdentityHashMap would have been more appropriate, however JavaScript has no concept of a weak reference, and so 
    //it can't be implement in GWT. In the absence of such a Map an Event is raised by FactPatternsPage when a Pattern is 
    //removed that is handled here to synchronise the Pattern lists.
    private Map<Pattern52, List<ActionSetFieldCol52>> patternToActionsMap = new IdentityHashMap<Pattern52, List<ActionSetFieldCol52>>();

    public ActionSetFieldsPage(NewAssetWizardContext context,
                               GuidedDecisionTable52 dtable,
                               EventBus eventBus,
                               Validator validator) {
        super( context,
               dtable,
               eventBus,
               validator );

        //Set-up validator for the pattern-to-action mapping voodoo
        getValidator().setPatternToActionSetFieldsMap( patternToActionsMap );
        this.view = new ActionSetFieldsPageViewImpl( getValidator() );

        //Wire-up the events
        eventBus.addHandler( PatternRemovedEvent.TYPE,
                             this );
        eventBus.addHandler( DuplicatePatternsEvent.TYPE,
                             this );
        eventBus.addHandler( ActionSetFieldsDefinedEvent.TYPE,
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
        view.setDTCellValueWidgetFactory( new DTCellValueWidgetFactory( dtable,
                                                                        sce ) );

        //Existing ActionSetFieldCols (should be empty for a new Decision Table)
        for ( ActionCol52 a : dtable.getActionCols() ) {
            if ( a instanceof ActionSetFieldCol52 ) {
                ActionSetFieldCol52 asf = (ActionSetFieldCol52) a;
                Pattern52 p = dtable.getConditionPattern( asf.getBoundName() );
                if ( !patternToActionsMap.containsKey( p ) ) {
                    patternToActionsMap.put( p,
                                             new ArrayList<ActionSetFieldCol52>() );
                }
                List<ActionSetFieldCol52> actions = patternToActionsMap.get( p );
                actions.add( asf );
            }
        }

        content.setWidget( view );
    }

    public void prepareView() {
        //Setup the available patterns, that could have changed each time this page is visited
        List<Pattern52> availablePatterns = new ArrayList<Pattern52>();
        for ( Pattern52 p : dtable.getConditionPatterns() ) {
            availablePatterns.add( p );
        }
        view.setAvailablePatterns( availablePatterns );
    }

    public boolean isComplete() {

        //Have all Actions been defined?
        boolean areActionSetFieldsDefined = true;
        for ( List<ActionSetFieldCol52> actions : patternToActionsMap.values() ) {
            for ( ActionSetFieldCol52 a : actions ) {
                if ( !getValidator().isActionValid( a ) ) {
                    areActionSetFieldsDefined = false;
                    break;
                }
            }
        }

        //Signal Action Set Fields definitions to other pages
        ActionSetFieldsDefinedEvent event = new ActionSetFieldsDefinedEvent( areActionSetFieldsDefined );
        eventBus.fireEvent( event );

        return areActionSetFieldsDefined;
    }

    public void onDuplicatePatterns(DuplicatePatternsEvent event) {
        view.setArePatternBindingsUnique( event.getArePatternBindingsUnique() );
    }

    public void onActionSetFieldsDefined(ActionSetFieldsDefinedEvent event) {
        view.setAreActionSetFieldsDefined( event.getAreActionSetFieldsDefined() );
    }

    public void selectPattern(Pattern52 pattern) {

        //Add fields available
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
        //Copy actions to decision table model. Assertion of bindings occurs in FactPatternsPage
        for ( Map.Entry<Pattern52, List<ActionSetFieldCol52>> ps : patternToActionsMap.entrySet() ) {
            Pattern52 p = ps.getKey();
            String binding = p.getBoundName();
            for ( ActionSetFieldCol52 a : ps.getValue() ) {
                a.setBoundName( binding );
                dtable.getActionCols().add( a );
            }
        }
    }

    public TableFormat getTableFormat() {
        return dtable.getTableFormat();
    }

}
