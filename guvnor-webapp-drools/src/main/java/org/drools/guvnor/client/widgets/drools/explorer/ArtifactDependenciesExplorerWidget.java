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
package org.drools.guvnor.client.widgets.drools.explorer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;
import org.drools.guvnor.client.common.DirtyableComposite;
import org.drools.guvnor.client.resources.Images;
import org.drools.guvnor.client.rpc.MavenArtifact;

public class ArtifactDependenciesExplorerWidget
        extends DirtyableComposite
        implements ArtifactDependenciesExplorer {

    private static Images images = GWT.create(Images.class);
    private final TreeItem root;

    //UI Elements

    // UI
    interface ArtifactDependenciesExplorerWidgetBinder
            extends
            UiBinder<Widget, ArtifactDependenciesExplorerWidget> {

    }

    private static ArtifactDependenciesExplorerWidgetBinder uiBinder = GWT.create(ArtifactDependenciesExplorerWidgetBinder.class);

    @UiField
    protected Tree treeArtifacts;

    public ArtifactDependenciesExplorerWidget(final String rootName,
            final Collection<MavenArtifact> tree,
            final Collection<MavenArtifact> excluded) {

        this.initWidget(uiBinder.createAndBindUi(this));

        this.root = treeArtifacts.addItem(rootName);
        setupTree(tree, excluded);
    }

    private void setupTree(final Collection<MavenArtifact> tree, final Collection<MavenArtifact> unchecked) {
        for (MavenArtifact mavenArtifact : tree) {
            addItem(root, mavenArtifact, unchecked);
        }
        root.setState(true);
    }

    private void addItem(final TreeItem root, final MavenArtifact artifact, final Collection<MavenArtifact> unchecked) {
        final SafeHtmlBuilder sb = new SafeHtmlBuilder();
        sb.append(SafeHtmlUtils.fromTrustedString(AbstractImagePrototype.create(images.modelAsset()).getHTML()));
        sb.appendEscaped(" ").appendEscaped(artifact.toLabel());

        final CheckBox checkBox = new CheckBox(sb.toSafeHtml());
        if (!unchecked.contains(artifact)) {
            checkBox.setValue(true);
        }

        final TreeItem newTreeItem = root.addItem(checkBox);
        newTreeItem.setUserObject(artifact);
        if (artifact.hasChild()) {
            for (MavenArtifact chilldren : artifact.getChild()) {
                addItem(newTreeItem, chilldren, unchecked);
            }
        }

        checkBox.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                final CheckBox me = ((CheckBox) event.getSource());
                boolean checked = me.getValue();
                if (newTreeItem.getChildCount() > 0) {
                    for (int i = 0; i < newTreeItem.getChildCount(); i++) {
                        defineState(newTreeItem.getChild(i), checked);
                    }
                }
            }

            private void defineState(TreeItem currentItem, boolean checked) {
                ((CheckBox) currentItem.getWidget()).setValue(checked);
                if (currentItem.getChildCount() > 0) {
                    for (int i = 0; i < currentItem.getChildCount(); i++) {
                        defineState(currentItem.getChild(i), checked);
                    }
                }
            }
        });
    }

    public void processExcludedArtifacts(final ArtifactDependenciesReadyCommand callback) {
        final List<MavenArtifact> result = new ArrayList<MavenArtifact>();
        buildExcludedList(root, result);
        callback.onSuccess(result);
    }

    private void buildExcludedList(final TreeItem item, final List<MavenArtifact> result) {
        if (item.getWidget() != null) {
            if (!((CheckBox) item.getWidget()).getValue()) {
                result.add((MavenArtifact) item.getUserObject());
            }
        }

        if (item.getChildCount() > 0) {
            for (int i = 0; i < item.getChildCount(); i++) {
                buildExcludedList(item.getChild(i), result);
            }
        }
    }
}
