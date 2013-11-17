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

package org.guvnor.m2repo.client.widgets;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ListDataProvider;
import org.guvnor.m2repo.model.JarListPageRow;
import org.guvnor.m2repo.service.M2RepoService;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.uberfire.paging.PageRequest;
import org.uberfire.paging.PageResponse;

public class ArtifactListPresenterImpl
        implements ArtifactListPresenter {

    @Inject
    protected ArtifactListViewImpl view;

    @Inject
    private Caller<M2RepoService> m2RepoService;

    private ListDataProvider<JarListPageRow> dataProvider;

    @PostConstruct
    public void init() {
        dataProvider = new ListDataProvider<JarListPageRow>() {
            protected void onRangeChanged( HasData<JarListPageRow> display ) {
                PageRequest request = new PageRequest( view.getPageStart(),
                                                       view.getPageSize() );

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
        };
        view.init( this );
    }

    public void refresh() {
        m2RepoService.call( new RemoteCallback<PageResponse<JarListPageRow>>() {
            @Override
            public void callback( final PageResponse<JarListPageRow> response ) {
                dataProvider.setList( response.getPageRowList() );
                dataProvider.refresh();
            }
        } ).listJars( new PageRequest( 0, view.getPageSize() ), view.getCurrentFilter() );
    }

    @Override
    public ListDataProvider<JarListPageRow> getDataProvider() {
        return dataProvider;
    }

    @Override
    public void addDataDisplay( final HasData<JarListPageRow> display ) {
        dataProvider.addDataDisplay( display );
    }

    public void search( final String filter ) {
        view.setCurrentFilter( filter );
        refresh();
    }

    public ArtifactListViewImpl getView() {
        return view;
    }
}
