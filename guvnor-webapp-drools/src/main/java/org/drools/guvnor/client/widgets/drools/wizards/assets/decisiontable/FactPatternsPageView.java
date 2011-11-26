/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.guvnor.client.widgets.drools.wizards.assets.decisiontable;

import java.util.List;

import org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52;
import org.drools.ide.common.client.modeldriven.dt52.Pattern52;

import com.google.gwt.user.client.ui.IsWidget;

/**
 * View and Presenter definitions for the Fact Patterns page
 */
public interface FactPatternsPageView
        extends
        IsWidget {

    interface Presenter {

        boolean isPatternEvent(Pattern52 pattern);

        void signalRemovalOfPattern(Pattern52 pattern);

        void stateChanged();

    }

    /**
     * Set the Presenter for the View to callback to
     * 
     * @param presenter
     */
    void setPresenter(Presenter presenter);

    void setDecisionTable(GuidedDecisionTable52 dtable);

    void setAvailableFactTypes(List<String> types);

    void setChosenPatterns(List<Pattern52> types);

    void setArePatternBindingsUnique(boolean arePatternBindingsUnique);

}
