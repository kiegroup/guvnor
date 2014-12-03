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
import org.uberfire.commons.validation.PortablePreconditions;
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

    private RefreshableAsyncDataProvider dataProvider;

    @PostConstruct
    public void init() {
        view.init( this );
    }

    @Override
    public void refresh() {
        // We couldn't attach the Display to the DataProvider in the @PostConstruct method
        // as this initializes the Display with data using AsyncDataProvider.onRangeChanged()
        // at which time the View may not have been initialised fully.
        if ( dataProvider == null ) {
            dataProvider = new RefreshableAsyncDataProvider( view,
                                                             m2RepoService );
            dataProvider.addDataDisplay( view.getDisplay() );
        } else {
            for ( HasData<JarListPageRow> display : dataProvider.getDataDisplays() ) {
                dataProvider.refresh( display );
            }
            notification.fire( new NotificationEvent( M2RepoEditorConstants.INSTANCE.RefreshedSuccessfully() ) );
        }
    }

    @Override
    public void search( final String filter ) {
        view.setCurrentFilter( filter );
        refresh();
    }

    public ArtifactListViewImpl getView() {
        return view;
    }

    /**
     * Extension to AsyncDataProvider that supports refreshing the table
     */
    private static class RefreshableAsyncDataProvider extends AsyncDataProvider<JarListPageRow> {

        private ArtifactListViewImpl view;
        private Caller<M2RepoService> m2RepoService;

        protected RefreshableAsyncDataProvider( final ArtifactListViewImpl view,
                                                final Caller<M2RepoService> m2RepoService ) {
            this.view = PortablePreconditions.checkNotNull( "view",
                                                            view );
            this.m2RepoService = PortablePreconditions.checkNotNull( "m2RepoService",
                                                                     m2RepoService );
        }

        protected void refresh( final HasData<JarListPageRow> display ) {
            onRangeChanged( display );
        }

        @Override
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
                }
            } ).listJars( request, view.getCurrentFilter() );
        }

    }
}
