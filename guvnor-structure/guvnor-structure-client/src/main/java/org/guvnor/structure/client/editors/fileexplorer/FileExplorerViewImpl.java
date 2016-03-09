/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.structure.client.editors.fileexplorer;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import org.guvnor.structure.repositories.Repository;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.widgets.core.client.tree.Tree;
import org.uberfire.ext.widgets.core.client.tree.TreeItem;

public class FileExplorerViewImpl
        extends Composite
        implements FileExplorerView {

    TreeItem rootTreeItem = null;

    private final Tree tree = GWT.create(Tree.class);

    private final FlowPanel panel = GWT.create(FlowPanel.class);

    private final Map<Repository, TreeItem> repositoryToTreeItemMap = new HashMap<Repository, TreeItem>();

    private static final String REPOSITORY_ID = "repositories";
    private static final String LAZY_LOAD = "Loading...";

    private FileExplorerPresenter presenter = null;

    public void init( final FileExplorerPresenter presenter ) {
        this.presenter = presenter;
        rootTreeItem = tree.addItem( TreeItem.Type.FOLDER, "Repositories" );
        rootTreeItem.setState( TreeItem.State.OPEN );

        panel.getElement().getStyle().setFloat(Style.Float.LEFT);
        panel.getElement().getStyle().setWidth(100, Style.Unit.PCT);
        panel.add( tree );
        initWidget( panel );

        tree.addOpenHandler( new OpenHandler<TreeItem>() {
            @Override
            public void onOpen( final OpenEvent<TreeItem> event ) {
                if ( needsLoading( event.getTarget() ) && event.getTarget().getUserObject() instanceof Path ) {
                    presenter.loadDirectoryContent( new FileExplorerItem( event.getTarget() ), (Path) event.getTarget().getUserObject() );
                }
            }
        } );

        tree.addSelectionHandler( new SelectionHandler<TreeItem>() {
            @Override
            public void onSelection( SelectionEvent<TreeItem> event ) {
                final Object userObject = event.getSelectedItem().getUserObject();
                if ( userObject != null && userObject instanceof Path ) {
                    final Path path = (Path) userObject;
                    presenter.redirect( path );
                } else if ( userObject != null && userObject instanceof Repository ) {
                    final Repository root = (Repository) userObject;
                    presenter.redirect( root );
                } else if ( event.getSelectedItem().getUserObject() instanceof String &&
                        ( event.getSelectedItem().getUserObject() ).equals( REPOSITORY_ID ) ) {
                    presenter.redirectRepositoryList();
                }
            }
        } );

    }

    @Override
    public void reset() {
        rootTreeItem.setUserObject( REPOSITORY_ID );
        rootTreeItem.addItem( TreeItem.Type.LOADING, LAZY_LOAD );
        rootTreeItem.removeItems();
        repositoryToTreeItemMap.clear();
    }

    @Override
    public void removeRepository( final Repository repo ) {
        if ( !repositoryToTreeItemMap.containsKey( repo ) ) {
            return;
        }
        final TreeItem repositoryRootItem = repositoryToTreeItemMap.remove( repo );
        repositoryRootItem.remove();
    }

    @Override
    public void addNewRepository( final Repository repository,
                                  final String branch) {
        final TreeItem repositoryRootItem = rootTreeItem.addItem( TreeItem.Type.FOLDER,
                                                                  repository.getAlias() );
        repositoryRootItem.setUserObject( repository );
        repositoryRootItem.setState( TreeItem.State.OPEN,
                                     false,
                                     false );

        repositoryToTreeItemMap.put( repository,
                                     repositoryRootItem );

        presenter.loadDirectoryContent( new FileExplorerItem( repositoryRootItem ),
                                        repository.getBranchRoot( branch ) );
    }

    private boolean needsLoading( final TreeItem item ) {
        return item.getChildCount() == 1 && LAZY_LOAD.equals( item.getChild( 0 ).getText() );
    }

}