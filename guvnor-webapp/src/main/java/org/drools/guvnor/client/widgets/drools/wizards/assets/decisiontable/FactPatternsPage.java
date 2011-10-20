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

import java.util.Arrays;
import java.util.List;

import org.drools.guvnor.client.decisiontable.Validator;
import org.drools.guvnor.client.widgets.drools.wizards.assets.NewAssetWizardContext;
import org.drools.guvnor.client.widgets.drools.wizards.assets.decisiontable.events.DuplicatePatternsEvent;
import org.drools.guvnor.client.widgets.drools.wizards.assets.decisiontable.events.PatternRemovedEvent;
import org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52;
import org.drools.ide.common.client.modeldriven.dt52.Pattern52;

import com.google.gwt.event.shared.EventBus;

/**
 * A page for the guided Decision Table Wizard to define Fact Patterns
 */
public class FactPatternsPage extends AbstractGuidedDecisionTableWizardPage
    implements
    FactPatternsPageView.Presenter,
    DuplicatePatternsEvent.Handler {

    private FactPatternsPageView view;

    public FactPatternsPage(NewAssetWizardContext context,
                            GuidedDecisionTable52 dtable,
                            EventBus eventBus,
                            Validator validator) {
        super( context,
               dtable,
               eventBus,
               validator );
        this.view = new FactPatternsPageViewImpl( getValidator() );

        //Wire-up the events
        eventBus.addHandler( DuplicatePatternsEvent.TYPE,
                             this );
    }

    public String getTitle() {
        return constants.DecisionTableWizardFactPatterns();
    }

    public void initialise() {
        if ( sce == null ) {
            return;
        }
        view.setPresenter( this );
        view.setDecisionTable( dtable );

        List<String> availableTypes = Arrays.asList( sce.getFactTypes() );
        view.setAvailableFactTypes( availableTypes );

        List<Pattern52> chosenTypes = dtable.getConditionPatterns();
        view.setChosenPatterns( chosenTypes );

        content.setWidget( view );
    }

    public void prepareView() {
        // Nothing needs to be done when the page is viewed; it is setup in initialise
    }

    public boolean isComplete() {

        //Are the patterns valid?
        boolean arePatternBindingsUnique = getValidator().arePatternBindingsUnique();

        //Signal duplicates to other pages
        DuplicatePatternsEvent event = new DuplicatePatternsEvent( arePatternBindingsUnique );
        eventBus.fireEvent( event );

        return arePatternBindingsUnique;
    }

    public void onDuplicatePatterns(DuplicatePatternsEvent event) {
        view.setArePatternBindingsUnique( event.getArePatternBindingsUnique() );
    }

    public boolean isPatternEvent(Pattern52 pattern) {
        return sce.isFactTypeAnEvent( pattern.getFactType() );
    }

    public void signalRemovalOfPattern(Pattern52 pattern) {
        PatternRemovedEvent event = new PatternRemovedEvent( pattern );
        eventBus.fireEvent( event );
    }

    @Override
    public void makeResult(GuidedDecisionTable52 dtable) {
        //Ensure every Pattern is bound
        int fi = 1;
        for ( Pattern52 p : dtable.getConditionPatterns() ) {
            if ( !getValidator().isPatternValid( p ) ) {
                String binding = NEW_FACT_PREFIX + (fi++);
                p.setBoundName( binding );
                while ( !getValidator().isPatternBindingUnique( p ) ) {
                    binding = NEW_FACT_PREFIX + (fi++);
                    p.setBoundName( binding );
                }
            }
        }
    }

}
