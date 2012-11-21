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

import org.kie.projecteditor.shared.model.KnowledgeBaseConfiguration;

import javax.inject.Inject;

public class AddNewKBasePopup
        implements AddNewKBasePopupView.Presenter {

    private AddKBaseCommand command;
    private final AddNewKBasePopupView view;

    @Inject
    public AddNewKBasePopup(AddNewKBasePopupView view) {
        view.setPresenter(this);
        this.view = view;
    }

    public void show(AddKBaseCommand command) {
        this.command = command;
        view.show();
    }

    @Override
    public void onOk() {
        if (view.getName() != null && !view.getName().trim().equals("")) {
            KnowledgeBaseConfiguration knowledgeBaseConfiguration = new KnowledgeBaseConfiguration();

            knowledgeBaseConfiguration.setFullName(view.getName());
            command.add(knowledgeBaseConfiguration);
        }else{
            view.showNameEmptyWarning();
        }
    }
}
