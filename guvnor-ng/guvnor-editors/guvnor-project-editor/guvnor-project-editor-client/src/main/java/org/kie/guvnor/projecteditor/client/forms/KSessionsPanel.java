/*
 * Copyright 2012 JBoss Inc
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

package org.kie.guvnor.projecteditor.client.forms;

import org.kie.guvnor.projecteditor.client.widgets.ListFormComboPanel;
import org.kie.guvnor.projecteditor.client.widgets.ListFormComboPanelView;
import org.kie.guvnor.projecteditor.client.widgets.NamePopup;
import org.kie.guvnor.projecteditor.model.KSessionModel;

import javax.inject.Inject;

public class KSessionsPanel
        extends ListFormComboPanel<KSessionModel> {

    @Inject
    public KSessionsPanel(ListFormComboPanelView view,
                          KSessionForm form,
                          NamePopup namePopup) {
        super(view, form, namePopup);
        view.setPresenter(this);
    }

    @Override
    protected KSessionModel createNew(String name) {
        KSessionModel kSessionModel = new KSessionModel();
        kSessionModel.setName(name);
        return kSessionModel;
    }
}
