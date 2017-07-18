/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.ala.ui.client.wizard.pipeline.item;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Event;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.Heading;
import org.jboss.errai.common.client.dom.Window;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.ForEvent;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Dependent
@Templated
public class PipelineItemView
        implements IsElement,
                   PipelineItemPresenter.View {

    @Inject
    @DataField("accented-area")
    private Div accentedArea;

    @Inject
    @Named("h2")
    @DataField("type-name")
    private Heading typeName;

    @Inject
    @DataField
    private Div body;

    @DataField
    private HTMLElement image = Window.getDocument().createElement("i");

    private PipelineItemPresenter presenter;

    @Override
    public void init(final PipelineItemPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setPipelineName(final String name) {
        this.typeName.setTextContent(name);
    }

    @EventHandler("image")
    public void onClick(@ForEvent("click") final Event event) {
        if (!accentedArea.getClassList().contains("remove-option")) {
            accentedArea.getClassList().toggle("card-pf-accented");
            if (accentedArea.getClassList().contains("card-pf-accented")) {
                removeOpacity();
            } else {
                addOpacity();
            }
            presenter.onContentChange();
        }
    }

    @Override
    public boolean isSelected() {
        return accentedArea.getClassList().contains("card-pf-accented");
    }

    @Override
    public void unSelect() {
        accentedArea.getClassList().remove("card-pf-accented");
        addOpacity();
    }

    private void addOpacity() {
        body.getStyle().setProperty("opacity",
                                    "0.3");
    }

    private void removeOpacity() {
        body.getStyle().removeProperty("opacity");
    }
}