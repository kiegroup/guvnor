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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.event.shared.EventBus;
import org.kie.guvnor.commons.ui.client.widget.HumanReadableDataTypes;
import org.kie.guvnor.datamodel.oracle.DataType;
import org.kie.guvnor.guided.dtable.client.resources.i18n.Constants;
import org.kie.guvnor.guided.dtable.client.widget.DTCellValueWidgetFactory;
import org.kie.guvnor.guided.dtable.client.widget.Validator;
import org.kie.guvnor.guided.dtable.client.wizard.pages.events.ActionSetFieldsDefinedEvent;
import org.kie.guvnor.guided.dtable.client.wizard.pages.events.DuplicatePatternsEvent;
import org.kie.guvnor.guided.dtable.client.wizard.pages.events.PatternRemovedEvent;
import org.kie.guvnor.guided.dtable.client.wizard.util.NewAssetWizardContext;
import org.kie.guvnor.guided.dtable.model.ActionCol52;
import org.kie.guvnor.guided.dtable.model.ActionSetFieldCol52;
import org.kie.guvnor.guided.dtable.model.DTCellValue52;
import org.kie.guvnor.guided.dtable.model.GuidedDecisionTable52;
import org.kie.guvnor.guided.dtable.model.Pattern52;
import org.kie.guvnor.guided.rule.model.BaseSingleFieldConstraint;

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

    private ActionSetFieldsPageView view;

    //GuidedDecisionTable52 maintains a single collection of Actions, linked to patterns by boundName. Thus if multiple 
    //patterns are bound to the same name we cannot distinguish which Actions relate to which Patterns. The Wizard therefore 
    //maintains it's own internal association of Patterns to Actions. IdentityHashMap is used as it is possible to have two 
    //identically defined Patterns (i.e. they have the same property values) although they represent different instances. 
    //A WeakIdentityHashMap would have been more appropriate, however JavaScript has no concept of a weak reference, and so 
    //it can't be implement in GWT. In the absence of such a Map an Event is raised by FactPatternsPage when a Pattern is 
    //removed that is handled here to synchronise the Pattern lists.
    private Map<Pattern52, List<ActionSetFieldCol52>> patternToActionsMap = new IdentityHashMap<Pattern52, List<ActionSetFieldCol52>>();

    public ActionSetFieldsPage( final NewAssetWizardContext context,
                                final GuidedDecisionTable52 dtable,
                                final EventBus eventBus,
                                final Validator validator ) {
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
    public void onPatternRemoved( final PatternRemovedEvent event ) {
        if ( event.getSource() != context ) {
            return;
        }
        patternToActionsMap.remove( event.getPattern() );
    }

    public String getTitle() {
        return Constants.INSTANCE.DecisionTableWizardActionSetFields();
    }

    public void initialise() {
        if ( oracle == null ) {
            return;
        }
        view.setPresenter( this );

        //Set-up a factory for value editors
        view.setDTCellValueWidgetFactory( DTCellValueWidgetFactory.getInstance( model,
                                                                                oracle,
                                                                                false,
                                                                                allowEmptyValues() ) );

        //Existing ActionSetFieldCols (should be empty for a new Decision Table)
        for ( ActionCol52 a : model.getActionCols() ) {
            if ( a instanceof ActionSetFieldCol52 ) {
                final ActionSetFieldCol52 asf = (ActionSetFieldCol52) a;
                final Pattern52 p = model.getConditionPattern( asf.getBoundName() );
                if ( !patternToActionsMap.containsKey( p ) ) {
                    patternToActionsMap.put( p,
                                             new ArrayList<ActionSetFieldCol52>() );
                }
                final List<ActionSetFieldCol52> actions = patternToActionsMap.get( p );
                actions.add( asf );
            }
        }

        content.setWidget( view );
    }

    public void prepareView() {
        //Setup the available patterns, that could have changed each time this page is visited
        view.setAvailablePatterns( model.getPatterns() );
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
        final ActionSetFieldsDefinedEvent event = new ActionSetFieldsDefinedEvent( areActionSetFieldsDefined );
        eventBus.fireEventFromSource( event,
                                      context );

        return areActionSetFieldsDefined;
    }

    public void onDuplicatePatterns( final DuplicatePatternsEvent event ) {
        if ( event.getSource() != context ) {
            return;
        }
        view.setArePatternBindingsUnique( event.getArePatternBindingsUnique() );
    }

    public void onActionSetFieldsDefined( final ActionSetFieldsDefinedEvent event ) {
        if ( event.getSource() != context ) {
            return;
        }
        view.setAreActionSetFieldsDefined( event.getAreActionSetFieldsDefined() );
    }

    public void selectPattern( final Pattern52 pattern ) {

        //Pattern is null when programmatically deselecting an item
        if ( pattern == null ) {
            return;
        }

        //Add fields available
        final String type = pattern.getFactType();
        final String[] fieldNames = oracle.getFieldCompletions( type );
        final List<AvailableField> availableFields = new ArrayList<AvailableField>();
        for ( String fieldName : fieldNames ) {
            final String fieldType = oracle.getFieldType( type,
                                                          fieldName );
            final String fieldDisplayType = HumanReadableDataTypes.getUserFriendlyTypeName( fieldType );
            final AvailableField field = new AvailableField( fieldName,
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
    public void makeResult( final GuidedDecisionTable52 model ) {
        //Copy actions to decision table model. Assertion of bindings occurs in FactPatternsPage
        for ( Map.Entry<Pattern52, List<ActionSetFieldCol52>> ps : patternToActionsMap.entrySet() ) {
            final Pattern52 p = ps.getKey();

            //Patterns with no conditions don't get created
            if ( p.getChildColumns().size() > 0 ) {
                final String binding = p.getBoundName();
                for ( ActionSetFieldCol52 a : ps.getValue() ) {
                    a.setBoundName( binding );
                    model.getActionCols().add( a );
                }
            }
        }
    }

    public GuidedDecisionTable52.TableFormat getTableFormat() {
        return model.getTableFormat();
    }

    @Override
    public boolean hasEnums( final ActionSetFieldCol52 selectedAction ) {
        for ( Map.Entry<Pattern52, List<ActionSetFieldCol52>> e : this.patternToActionsMap.entrySet() ) {
            if ( e.getValue().contains( selectedAction ) ) {
                final String factType = e.getKey().getFactType();
                final String factField = selectedAction.getFactField();
                return this.oracle.hasEnums( factType,
                                             factField );
            }
        }
        return false;
    }

    @Override
    public void assertDefaultValue( final Pattern52 selectedPattern,
                                    final ActionSetFieldCol52 selectedAction ) {
        final List<String> valueList = Arrays.asList( modelUtils.getValueList( selectedAction ) );
        if ( valueList.size() > 0 ) {
            final String defaultValue = cellUtils.asString( selectedAction.getDefaultValue() );
            if ( !valueList.contains( defaultValue ) ) {
                selectedAction.getDefaultValue().clearValues();
            }
        } else {
            //Ensure the Default Value has been updated to represent the column's data-type.
            final DTCellValue52 defaultValue = selectedAction.getDefaultValue();
            final DataType.DataTypes dataType = cellUtils.getDataType( selectedPattern,
                                                                       selectedAction );
            cellUtils.assertDTCellValue( dataType,
                                         defaultValue );
        }
    }

}
