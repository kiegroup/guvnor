/*
 * Copyright 2014 JBoss Inc
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
package org.guvnor.m2repo.client.widgets;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.Range;
import org.guvnor.m2repo.client.resources.i18n.M2RepoEditorConstants;
import org.guvnor.m2repo.model.JarListPageRow;
import org.guvnor.m2repo.service.M2RepoService;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.uberfire.paging.PageRequest;
import org.uberfire.paging.PageResponse;
import org.uberfire.workbench.events.NotificationEvent;

public class ArtifactListPresenterImpl
        implements ArtifactListPresenter {

    @Inject
    protected ArtifactListViewImpl view;

    @Inject
    private Caller<M2RepoService> m2RepoService;

    @Inject
    private Event<NotificationEvent> notification;

    private AsyncDataProvider<JarListPageRow> dataProvider;

    @PostConstruct
    public void init() {
        view.init( this );
    }

    @Override
    public void refresh() {
        dataProvider = new AsyncDataProvider<JarListPageRow>() {
            protected void onRangeChanged( HasData<JarListPageRow> display ) {
                final Range range = display.getVisibleRange();
                PageRequest request = new PageRequest( range.getStart(),
                                                       range.getLength() );

                m2RepoService.call( new RemoteCallback<PageResponse<JarListPageRow>>() {
                    @Override
                    public void callback( final PageResponse<JarListPageRow> response ) {
                        updateRowCount( response.getTotalRowSize(),
                                        response.isTotalRowSizeExact() );
                        updateRowData( response.getStartRowIndex(),
                                       response.getPageRowList() );
                        notification.fire( new NotificationEvent( M2RepoEditorConstants.INSTANCE.RefreshedSuccessfully() ) );
                    }
                } ).listJars( request, view.getCurrentFilter() );
            }
        };
        dataProvider.addDataDisplay( view.getDisplay() );
    }

    public void search( final String filter ) {
        view.setCurrentFilter( filter );
        refresh();
    }

    public ArtifactListViewImpl getView() {
        return view;
    }
}
