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

import java.util.Arrays;
import java.util.List;

import org.drools.guvnor.client.widgets.wizards.assets.NewAssetWizardContext;
import org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52;
import org.drools.ide.common.client.modeldriven.dt52.Pattern52;

import com.google.gwt.event.shared.EventBus;

/**
 * A page for the guided Decision Table Wizard to define Fact Patterns
 */
public class FactPatternsPage extends AbstractGuidedDecisionTableWizardPage
    implements
    FactPatternsPageView.Presenter {

    private FactPatternsPageView view = new FactPatternsPageViewImpl();

    private Validator            validator;

    public FactPatternsPage(NewAssetWizardContext context,
                            GuidedDecisionTable52 dtable,
                            EventBus eventBus) {
        super( context,
               dtable,
               eventBus );
        validator = new Validator( dtable.getConditionPatterns() );
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

        //Have patterns been defined?
        if ( dtable.getConditionPatterns() == null || dtable.getConditionPatterns().size() == 0 ) {
            return false;
        }

        //Are the patterns valid?
        boolean isValid = validator.arePatternBindingsUnique();
        view.setHasDuplicatePatternBindings( !isValid );
        return isValid;
    }

    public boolean isPatternEvent(Pattern52 pattern) {
        return sce.isFactTypeAnEvent( pattern.getFactType() );
    }

    public void signalRemovalOfPattern(Pattern52 pattern) {
        PatternRemovedEvent event = new PatternRemovedEvent( pattern );
        eventBus.fireEvent( event );
    }

}
