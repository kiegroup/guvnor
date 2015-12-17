/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.structure.client.editors.repository.list;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;
import org.guvnor.structure.repositories.Repository;
import org.uberfire.ext.widgets.core.client.resources.i18n.CoreConstants;

@Dependent
public class RepositoriesView extends Composite
        implements
        RequiresResize,
        RepositoriesPresenter.View {

    interface RepositoriesEditorViewBinder
            extends
            UiBinder<Widget, RepositoriesView> {

    }

    private static RepositoriesEditorViewBinder uiBinder = GWT.create( RepositoriesEditorViewBinder.class );

    private RepositoriesPresenter presenter;

    @UiField
    public FlowPanel panel;

    private Map<Repository, Widget> repositoryToWidgetMap = new HashMap<Repository, Widget>();

    @PostConstruct
    public void init() {
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    @Override
    public void init( final RepositoriesPresenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void addRepository( final Repository repository ) {
        final RepositoriesViewItem item = new RepositoriesViewItem( repository.getAlias(),
                                                                    null,
                                                                    repository.getPublicURIs(),
                                                                    CoreConstants.INSTANCE.Empty(),
                                                                    repository.getCurrentBranch(),
                                                                    repository.getBranches(),
                                                                    new RemoveRepositoryCmd( repository, presenter ),
                                                                    new UpdateRepositoryCmd( repository, presenter ) );
        repositoryToWidgetMap.put( repository,
                                   item );
        panel.add( item );

    }

    @Override
    public boolean confirmDeleteRepository( final Repository repository ) {
        return Window.confirm( CoreConstants.INSTANCE.ConfirmDeleteRepository0( repository.getAlias() ) );
    }

    @Override
    public void removeIfExists( final Repository repository ) {
        Widget w = repositoryToWidgetMap.remove( repository );
        if ( w == null ) {
            return;
        }
        panel.remove( w );
    }

    @Override
    public void clear() {
        repositoryToWidgetMap.clear();
        panel.clear();
    }

    @Override
    public void onResize() {
        int height = getParent().getOffsetHeight();
        int width = getParent().getOffsetWidth();
        panel.setPixelSize( width, height );
    }

    @Override
    public void updateRepository( final Repository old,
                                  final Repository updated ) {
        RepositoriesViewItem item = (RepositoriesViewItem) repositoryToWidgetMap.remove( old );

        if ( item != null ) {
            item.update( updated, presenter );
            repositoryToWidgetMap.put( updated, item );
        }
    }
}