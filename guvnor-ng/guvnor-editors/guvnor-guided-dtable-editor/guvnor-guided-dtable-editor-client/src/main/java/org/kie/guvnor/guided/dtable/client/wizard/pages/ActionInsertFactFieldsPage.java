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
import org.kie.guvnor.guided.dtable.client.wizard.pages.events.ActionInsertFactFieldsDefinedEvent;
import org.kie.guvnor.guided.dtable.client.wizard.pages.events.DuplicatePatternsEvent;
import org.kie.guvnor.guided.dtable.client.wizard.util.NewAssetWizardContext;
import org.kie.guvnor.guided.dtable.model.ActionCol52;
import org.kie.guvnor.guided.dtable.model.ActionInsertFactCol52;
import org.kie.guvnor.guided.dtable.model.ActionInsertFactFieldsPattern;
import org.kie.guvnor.guided.dtable.model.DTCellValue52;
import org.kie.guvnor.guided.dtable.model.GuidedDecisionTable52;
import org.kie.guvnor.guided.rule.model.BaseSingleFieldConstraint;

/**
 * A page for the guided Decision Table Wizard to define new Facts and fields.
 * This page does not use the GuidedDecisionTable model directly; instead
 * maintaining its own Pattern-to-Action associations.
 */
public class ActionInsertFactFieldsPage extends AbstractGuidedDecisionTableWizardPage
        implements
        ActionInsertFactFieldsPageView.Presenter,
        DuplicatePatternsEvent.Handler,
        ActionInsertFactFieldsDefinedEvent.Handler {

    private ActionInsertFactFieldsPageView view;

    //GuidedDecisionTable52 maintains a single collection of Actions, linked to patterns by boundName. Thus if multiple 
    //patterns are bound to the same name we cannot distinguish which Actions relate to which Patterns. The Wizard therefore 
    //maintains it's own internal association of Patterns to Actions. IdentityHashMap is used as it is possible to have two 
    //identically defined Patterns (i.e. they have the same property values) although they represent different instances. 
    //A WeakIdentityHashMap would have been more appropriate, however JavaScript has no concept of a weak reference, and so 
    //it can't be implement in GWT. In the absence of such a Map an Event is raised by FactPatternsPage when a Pattern is 
    //removed that is handled here to synchronise the Pattern lists.
    private Map<ActionInsertFactFieldsPattern, List<ActionInsertFactCol52>> patternToActionsMap = new IdentityHashMap<ActionInsertFactFieldsPattern, List<ActionInsertFactCol52>>();

    public ActionInsertFactFieldsPage( final NewAssetWizardContext context,
                                       final GuidedDecisionTable52 model,
                                       final EventBus eventBus,
                                       final Validator validator ) {
        super( context,
               model,
               eventBus,
               validator );

        //Set-up validator for the pattern-to-action mapping voodoo
        getValidator().setPatternToActionInsertFactFieldsMap( patternToActionsMap );
        this.view = new ActionInsertFactFieldsPageViewImpl( getValidator() );

        //Wire-up the events
        eventBus.addHandler( DuplicatePatternsEvent.TYPE,
                             this );
        eventBus.addHandler( ActionInsertFactFieldsDefinedEvent.TYPE,
                             this );
    }

    public String getTitle() {
        return Constants.INSTANCE.DecisionTableWizardActionInsertFacts();
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

        //Available types
        final List<String> availableTypes = Arrays.asList( oracle.getFactTypes() );
        view.setAvailableFactTypes( availableTypes );

        //Existing ActionInsertFactCols (should be empty for a new Decision Table)
        for ( ActionCol52 a : model.getActionCols() ) {
            if ( a instanceof ActionInsertFactCol52 ) {
                final ActionInsertFactCol52 aif = (ActionInsertFactCol52) a;
                final ActionInsertFactFieldsPattern p = lookupExistingInsertFactPattern( aif.getBoundName() );
                final List<ActionInsertFactCol52> actions = patternToActionsMap.get( p );
                getValidator().addActionPattern( p );
                actions.add( aif );
            }
        }
        view.setChosenPatterns( new ArrayList<ActionInsertFactFieldsPattern>( patternToActionsMap.keySet() ) );

        content.setWidget( view );
    }

    private ActionInsertFactFieldsPattern lookupExistingInsertFactPattern( final String boundName ) {
        for ( ActionInsertFactFieldsPattern p : patternToActionsMap.keySet() ) {
            if ( p.getBoundName().equals( boundName ) ) {
                return p;
            }
        }
        final ActionInsertFactFieldsPattern p = new ActionInsertFactFieldsPattern();
        patternToActionsMap.put( p,
                                 new ArrayList<ActionInsertFactCol52>() );
        return p;
    }

    public void prepareView() {
        //Nothing to do here, this page is self-contained
    }

    public boolean isComplete() {

        //Do all Patterns have unique bindings?
        final boolean arePatternBindingsUnique = getValidator().arePatternBindingsUnique();

        //Signal duplicates to other pages
        final DuplicatePatternsEvent event = new DuplicatePatternsEvent( arePatternBindingsUnique );
        eventBus.fireEventFromSource( event,
                                      context );

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
        final ActionInsertFactFieldsDefinedEvent eventFactFields = new ActionInsertFactFieldsDefinedEvent( areActionInsertFieldsDefined );
        eventBus.fireEventFromSource( eventFactFields,
                                      context );

        return arePatternBindingsUnique && areActionInsertFieldsDefined;
    }

    public void onDuplicatePatterns( final DuplicatePatternsEvent event ) {
        if ( event.getSource() != context ) {
            return;
        }
        view.setArePatternBindingsUnique( event.getArePatternBindingsUnique() );
    }

    public void onActionInsertFactFieldsDefined( final ActionInsertFactFieldsDefinedEvent event ) {
        if ( event.getSource() != context ) {
            return;
        }
        view.setAreActionInsertFactFieldsDefined( event.getAreActionInsertFactFieldsDefined() );
    }

    public void addPattern( final ActionInsertFactFieldsPattern pattern ) {
        patternToActionsMap.put( pattern,
                                 new ArrayList<ActionInsertFactCol52>() );
        getValidator().addActionPattern( pattern );
    }

    public void removePattern( final ActionInsertFactFieldsPattern pattern ) {
        patternToActionsMap.remove( pattern );
        getValidator().removeActionPattern( pattern );
    }

    public void selectPattern( final ActionInsertFactFieldsPattern pattern ) {

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
        List<ActionInsertFactCol52> actionsForPattern = patternToActionsMap.get( pattern );
        if ( actionsForPattern == null ) {
            actionsForPattern = new ArrayList<ActionInsertFactCol52>();
            patternToActionsMap.put( pattern,
                                     actionsForPattern );
        }
        view.setChosenFields( actionsForPattern );
    }

    @Override
    public void makeResult( final GuidedDecisionTable52 model ) {
        //Copy actions to decision table model
        int fi = 1;
        for ( Map.Entry<ActionInsertFactFieldsPattern, List<ActionInsertFactCol52>> ps : patternToActionsMap.entrySet() ) {
            final ActionInsertFactFieldsPattern p = ps.getKey();
            if ( !getValidator().isPatternValid( p ) ) {
                String binding = NEW_FACT_PREFIX + ( fi++ );
                p.setBoundName( binding );
                while ( !getValidator().isPatternBindingUnique( p ) ) {
                    binding = NEW_FACT_PREFIX + ( fi++ );
                    p.setBoundName( binding );
                }
            }

            final String factType = p.getFactType();
            final String boundName = p.getBoundName();
            final boolean isLogicalInsert = p.isInsertedLogically();

            for ( ActionInsertFactCol52 aif : ps.getValue() ) {
                aif.setFactType( factType );
                aif.setBoundName( boundName );
                aif.setInsertLogical( isLogicalInsert );
                model.getActionCols().add( aif );
            }
        }

    }

    public GuidedDecisionTable52.TableFormat getTableFormat() {
        return model.getTableFormat();
    }

    @Override
    public boolean hasEnums( final ActionInsertFactCol52 selectedAction ) {
        for ( Map.Entry<ActionInsertFactFieldsPattern, List<ActionInsertFactCol52>> e : this.patternToActionsMap.entrySet() ) {
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
    public void assertDefaultValue( final ActionInsertFactCol52 selectedAction ) {
        final List<String> valueList = Arrays.asList( modelUtils.getValueList( selectedAction ) );
        if ( valueList.size() > 0 ) {
            final String defaultValue = cellUtils.asString( selectedAction.getDefaultValue() );
            if ( !valueList.contains( defaultValue ) ) {
                selectedAction.getDefaultValue().clearValues();
            }
        } else {
            //Ensure the Default Value has been updated to represent the column's data-type.
            final DTCellValue52 defaultValue = selectedAction.getDefaultValue();
            final DataType.DataTypes dataType = cellUtils.getDataType( selectedAction );
            cellUtils.assertDTCellValue( dataType,
                                         defaultValue );
        }
    }

}
