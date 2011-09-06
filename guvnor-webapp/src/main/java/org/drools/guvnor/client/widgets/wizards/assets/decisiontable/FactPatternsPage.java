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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.drools.guvnor.client.widgets.wizards.WizardPageStatusChangeEvent;
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

    public FactPatternsPage(NewAssetWizardContext context,
                            GuidedDecisionTable52 dtable,
                            EventBus eventBus) {
        super( context,
               dtable,
               eventBus );
    }

    public String getTitle() {
        return constants.DecisionTableWizardFactPatterns();
    }

    public void initialise() {
        if ( sce == null ) {
            return;
        }
        view.setPresenter( this );

        List<String> availableTypes = Arrays.asList( sce.getFactTypes() );
        view.setAvailableFactTypes( availableTypes );

        List<Pattern52> chosenTypes = dtable.getConditionPatterns();
        view.setChosenFactTypes( chosenTypes );

        content.setWidget( view );
    }
    
    public void prepareView() {
        // Nothing needs to be done when the page is viewed; it is setup in initialise
    }

    public boolean isComplete() {
        if ( dtable.getConditionPatterns().size() == 0 ) {
            return false;
        }
        Set<String> bindings = new HashSet<String>();
        for ( Pattern52 pattern : dtable.getConditionPatterns() ) {
            String binding = pattern.getBoundName();
            if ( binding != null && !binding.equals( "" ) ) {
                if ( bindings.contains( binding ) ) {
                    return false;
                }
                bindings.add( binding );
            }
        }
        return true;
    }

    public boolean isPatternEvent(Pattern52 pattern) {
        return sce.isFactTypeAnEvent( pattern.getFactType() );
    }

    public void stateChanged() {
        WizardPageStatusChangeEvent event = new WizardPageStatusChangeEvent( this );
        eventBus.fireEvent( event );
    }

    public void setChosenPatterns(List<Pattern52> patterns) {
        dtable.setConditionPatterns( patterns );
    }

}
