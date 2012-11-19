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

package org.kie.projecteditor.client.forms;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.kie.projecteditor.shared.model.KSessionModel;

import javax.enterprise.inject.New;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class KSessionsPanel
        implements IsWidget, KSessionsPanelView.Presenter {

    private Map<String, KSessionModel> sessions = new HashMap<String, KSessionModel>();
    private final KSessionsPanelView view;

    @Inject
    public KSessionsPanel(@New KSessionsPanelView view) {
        this.view = view;
        view.setPresenter(this);
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    public void setSessions(ArrayList<KSessionModel> sessions) {
        view.clearList();

        for (KSessionModel model : sessions) {

            this.sessions.put(model.getFullName(), model);

            view.addKSessionModel(model);
        }
    }

    @Override
    public void selectKSession(String selectedFullName) {
        view.setSelectedSession(sessions.get(selectedFullName));
    }
}
