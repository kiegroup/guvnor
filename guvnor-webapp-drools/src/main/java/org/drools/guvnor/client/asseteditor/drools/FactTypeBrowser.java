/*
 * Copyright 2010 JBoss Inc
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

package org.drools.guvnor.client.asseteditor.drools;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import org.drools.guvnor.client.common.ClickableLabel;
import org.drools.guvnor.client.common.SmallLabel;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.resources.DroolsGuvnorImageResources;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;

public class FactTypeBrowser extends Composite {

    public FactTypeBrowser(SuggestionCompletionEngine sce,
            final ClickEvent ev) {
        final Tree tree = new Tree();

        final VerticalPanel panel = new VerticalPanel();

        final HorizontalPanel hpFactsAndHide = new HorizontalPanel();
        final HorizontalPanel hpShow = new HorizontalPanel();

        hpShow.add(new ClickableLabel(Constants.INSTANCE.ShowFactTypes(),
                new ClickHandler() {
                    public void onClick(com.google.gwt.event.dom.client.ClickEvent event) {
                        hpShow.setVisible(false);
                        hpFactsAndHide.setVisible(true);
                        tree.setVisible(true);
                    }
                }));
        panel.add(hpShow);

        hpFactsAndHide.add(new SmallLabel(Constants.INSTANCE.FactTypes()));
        hpFactsAndHide.add(new ClickableLabel(Constants.INSTANCE.hide(),
                new ClickHandler() {
                    public void onClick(com.google.gwt.event.dom.client.ClickEvent event) {
                        hpShow.setVisible(true);
                        hpFactsAndHide.setVisible(false);
                        tree.setVisible(false);
                    }
                }));
        panel.add(hpFactsAndHide);

        panel.add(tree);
        if (sce.getFactTypes() != null) {
            for (String type : sce.getFactTypes()) {
                TreeItem it = new TreeItem();
                it.setHTML(AbstractImagePrototype.create(DroolsGuvnorImageResources.INSTANCE.classImage()).getHTML()
                        + "<small>"
                        + type + "</small>");
                it.setUserObject(type + "( )");
                tree.addItem(it);

                String[] fields = (String[]) sce.getModelFields(type);
                if (fields != null) {
                    for (String field : fields) {
                        TreeItem fi = new TreeItem();
                        fi.setHTML(AbstractImagePrototype.create(DroolsGuvnorImageResources.INSTANCE.field()).getHTML()
                                + "<small>"
                                + field + "</small>");
                        fi.setUserObject(field);
                        it.addItem(fi);
                    }
                }
            }
        }

        tree.setStyleName("category-explorer-Tree"); //NON-NLS
        tree.addSelectionHandler(new SelectionHandler<TreeItem>() {

            public void onSelection(SelectionEvent<TreeItem> event) {
                Object o = event.getSelectedItem().getUserObject();
                if (o instanceof String) {
                    ev.selected((String) o);
                }
            }
        });

        tree.setVisible(false);
        hpFactsAndHide.setVisible(false);
        hpShow.setVisible(true);

        initWidget(panel);
    }

    public static interface ClickEvent {

        public void selected(String text);
    }

}
