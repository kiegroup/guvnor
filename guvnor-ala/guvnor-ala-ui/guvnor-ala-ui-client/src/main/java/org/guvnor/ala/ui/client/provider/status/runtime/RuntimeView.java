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

package org.guvnor.ala.ui.client.provider.status.runtime;

import java.util.Collection;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.Anchor;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Event;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.ListItem;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.common.client.dom.Window;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.ForEvent;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Dependent
@Templated
public class RuntimeView implements org.jboss.errai.ui.client.local.api.IsElement,
                                    RuntimePresenter.View {

    @Inject
    @DataField("start-item")
    private ListItem startItem;

    @Inject
    @DataField
    private Anchor start;

    @Inject
    @DataField("stop-item")
    private ListItem stopItem;

    @Inject
    @DataField
    private Anchor stop;

    @Inject
    @DataField
    private Anchor rebuild;

    @Inject
    @DataField("runtime-status")
    private Span status;

    @Inject
    @DataField("runtime-name")
    private Div name;

    @Inject
    @DataField
    private Span date;

    @DataField
    private HTMLElement pipeline = Window.getDocument().createElement("strong");

    @Inject
    @DataField("runtime-endpoint")
    private Anchor endpoint;

    @Inject
    @DataField("expansion-area")
    private Div expansionArea;

    @Inject
    @DataField("expansion-content")
    private Div expansionContent;

    @Inject
    @DataField("expand-chevron")
    private Span chevron;

    private RuntimePresenter presenter;

    @Override
    public void init(final RuntimePresenter presenter) {
        this.presenter = presenter;
    }

    @EventHandler("start")
    public void onStart(@ForEvent("click") final Event event) {
        presenter.start();
    }

    @EventHandler("stop")
    public void onStop(@ForEvent("click") final Event event) {
        presenter.stop();
    }

    @EventHandler("rebuild")
    public void onRebuild(@ForEvent("click") final Event event) {
        presenter.rebuild();
    }

    @EventHandler("delete")
    public void onDelete(@ForEvent("click") final Event event) {
        presenter.delete();
    }

    @EventHandler("expand-chevron")
    public void onOpenCloseExpand(@ForEvent("click") final Event event) {
        openCloseExpand();
    }

    @EventHandler("close-expansion")
    public void onCloseExpansion(@ForEvent("click") final Event event) {
        openCloseExpand();
    }

    private void openCloseExpand() {
        chevron.getClassList().toggle("fa-chevron-down");
        if (!chevron.getClassList().contains("fa-chevron-down")) {
            expansionArea.getStyle().setProperty("display",
                                                 "none");
            chevron.getClassList().add("fa-chevron-right");
        } else {
            chevron.getClassList().remove("fa-chevron-right");
            expansionArea.getStyle().removeProperty("display");
        }
    }

    @Override
    public void setup(final String name,
                      final String date,
                      final String pipeline) {
        this.name.setTextContent(name);
        this.date.setTextContent(date);
        this.pipeline.setTextContent(pipeline);
    }

    @Override
    public void setEndpoint(final String endpoint) {
        this.endpoint.setHref(endpoint);
        this.endpoint.setTextContent(endpoint);
    }

    @Override
    public void disableStart() {
        this.startItem.getClassList().add("disabled");
    }

    @Override
    public void enableStart() {
        this.startItem.getClassList().remove("disabled");
    }

    @Override
    public void disableStop() {
        this.stopItem.getClassList().add("disabled");
    }

    @Override
    public void enableStop() {
        this.stopItem.getClassList().remove("disabled");
    }

    @Override
    public void setStatus(final Collection<String> styles) {
        this.status.removeAttribute("class");
        for (String style : styles) {
            this.status.getClassList().add(style);
        }
    }

    @Override
    public void addExpandedContent(final IsElement element) {
        expansionContent.appendChild(element.getElement());
    }
}
