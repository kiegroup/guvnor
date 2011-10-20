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

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.*;

import org.drools.guvnor.client.common.ClickableLabel;
import org.drools.guvnor.client.common.SmallLabel;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.resources.Images;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;

public class FactTypeBrowser extends Composite {

    private static Images images = GWT.create(Images.class);

    public FactTypeBrowser(SuggestionCompletionEngine sce,
                           final ClickEvent ev) {
        Tree tree = new Tree();

        final VerticalPanel panel = new VerticalPanel();

        HorizontalPanel hp = new HorizontalPanel();

        Constants constants = GWT.create(Constants.class);
        hp.add(new SmallLabel(constants.FactTypes()));
        hp.add(new ClickableLabel(constants.hide(),
                new ClickHandler() {
                    public void onClick(com.google.gwt.event.dom.client.ClickEvent event) {
                        panel.setVisible(false);
                    }
                }));
        panel.add(hp);

        panel.add(tree);
        if (sce.getFactTypes() != null) {
            for (String type : sce.getFactTypes()) {
                TreeItem it = new TreeItem();
                it.setHTML(AbstractImagePrototype.create(images.classImage()).getHTML() 
                        + "<small>"
                        + type + "</small>");
                it.setUserObject(type + "( )");
                tree.addItem(it);

                String[] fields = (String[]) sce.getModelFields(type);
                if (fields != null) {
                    for (String field : fields) {
                        TreeItem fi = new TreeItem();
                        fi.setHTML(AbstractImagePrototype.create(images.field()).getHTML() 
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

        initWidget(panel);
    }

    public static interface ClickEvent {
        public void selected(String text);
    }

}
