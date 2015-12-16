/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
package org.guvnor.m2repo.client.widgets;

import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortList;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.Range;
import javax.enterprise.event.Event;
import org.guvnor.m2repo.model.JarListPageRequest;
import org.guvnor.m2repo.model.JarListPageRow;
import org.guvnor.m2repo.service.M2RepoService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mocks.CallerMock;
import org.uberfire.paging.PageResponse;
import org.uberfire.workbench.events.NotificationEvent;

import static org.junit.Assert.*;

@RunWith( MockitoJUnitRunner.class )
public class ArtifactListTest {

    private static final int REQUEST_RANGE_LENGTH = 53;
    private static final int REQUEST_RANGE_START = 19;
    private final boolean REQUEST_SORT_ORDER = true;
    private final String REQUEST_SORT_COLUMN = "C";
    private static final boolean RESPONSE_EXACT_ROWS = true;
    private static final int RESPONSE_ROWS_COUNT = 61;
    private static final String POM_TEXT = "POM text";
    @Mock
    private Event<NotificationEvent> event;
    @Mock
    private M2RepoService m2service;
    @Mock
    private PageResponse<JarListPageRow> response;
    @Mock
    private ArtifactListView view;
    @Mock
    private HasData<JarListPageRow> table;
    @Mock
    private Range range;
    @Mock
    @SuppressWarnings( "rawtypes" )
    private Column column;
    @Mock
    private ColumnSortList sortList;
    @Mock
    private ColumnSortList.ColumnSortInfo sortInfo;
    @Captor
    private ArgumentCaptor<JarListPageRequest> request;

    @Before
    @SuppressWarnings( "unchecked" )
    public void setUp() {
        Mockito.when( m2service.listArtifacts( Mockito.any( JarListPageRequest.class ) ) ).thenReturn( response );
        Mockito.when( m2service.getPomText( Mockito.anyString() ) ).thenReturn( POM_TEXT );
        Mockito.when( response.getTotalRowSize() ).thenReturn( RESPONSE_ROWS_COUNT );
        Mockito.when( response.isTotalRowSizeExact() ).thenReturn( RESPONSE_EXACT_ROWS );

        Mockito.when( view.getDisplay() ).thenReturn( table );
        Mockito.when( table.getVisibleRange() ).thenReturn( range );
        Mockito.when( range.getStart() ).thenReturn( REQUEST_RANGE_START );
        Mockito.when( range.getLength() ).thenReturn( REQUEST_RANGE_LENGTH );

        Mockito.when( view.getColumnSortList() ).thenReturn( sortList );
        Mockito.when( sortList.size() ).thenReturn( 1 );
        Mockito.when( sortList.get( 0 ) ).thenReturn( sortInfo );
        Mockito.when( sortInfo.isAscending() ).thenReturn( REQUEST_SORT_ORDER );
        Mockito.when( sortInfo.getColumn() ).thenReturn( column ); // unchecked
        Mockito.when( column.getDataStoreName() ).thenReturn( REQUEST_SORT_COLUMN );
    }

    @Test
    public void testSearch() {
        ArtifactListPresenterImpl presenter = new ArtifactListPresenterImpl( view,
                                                                             new CallerMock<M2RepoService>( m2service ),
                                                                             event );
        // Disable sort info for this test
        Mockito.when( view.getColumnSortList() ).thenReturn( null );
        presenter.init();

        // Initial request without filter
        Mockito.verify( m2service ).listArtifacts( request.capture() );
        JarListPageRequest initialRequest = request.getValue();
        verifyRequest( initialRequest,
                       null,
                       null,
                       REQUEST_RANGE_LENGTH,
                       REQUEST_RANGE_START,
                       ArtifactListPresenterImpl.DEFAULT_ORDER_ASCENDING );

        presenter.search( "filters" );
        Mockito.verify( event ).fire( Mockito.any( NotificationEvent.class ) );

        // Search request with filter
        Mockito.verify( m2service, Mockito.times( 2 ) ).listArtifacts( request.capture() );
        JarListPageRequest searchRequest = request.getValue();
        verifyRequest( searchRequest,
                       null,
                       "filters",
                       REQUEST_RANGE_LENGTH,
                       REQUEST_RANGE_START,
                       ArtifactListPresenterImpl.DEFAULT_ORDER_ASCENDING );

        // Row data updated
        Mockito.verify( table, Mockito.times( 2 ) ).setRowCount( RESPONSE_ROWS_COUNT,
                                                                 RESPONSE_EXACT_ROWS );
    }

    @Test
    public void testColumnSortList() {
        ArtifactListPresenterImpl presenter = new ArtifactListPresenterImpl( view,
                                                                             new CallerMock<M2RepoService>( m2service ),
                                                                             event );
        presenter.init();

        // Initial request with default sort parameters
        Mockito.verify( m2service ).listArtifacts( request.capture() );
        verifyRequest( request.getValue(),
                       REQUEST_SORT_COLUMN,
                       null,
                       REQUEST_RANGE_LENGTH,
                       REQUEST_RANGE_START,
                       REQUEST_SORT_ORDER );

        // Change sort parameters and refresh
        Mockito.when( sortInfo.isAscending() ).thenReturn( !REQUEST_SORT_ORDER );
        Mockito.when( column.getDataStoreName() ).thenReturn( "X" );
        presenter.refresh();

        // Verify request
        Mockito.verify( m2service, Mockito.times( 2 ) ).listArtifacts( request.capture() );
        verifyRequest( request.getValue(),
                       "X",
                       null,
                       REQUEST_RANGE_LENGTH,
                       REQUEST_RANGE_START,
                       !REQUEST_SORT_ORDER );

        // Row data updated
        Mockito.verify( table, Mockito.times( 2 ) ).setRowCount( RESPONSE_ROWS_COUNT,
                                                                 RESPONSE_EXACT_ROWS );
    }

    @Test
    public void testShowPom() {
        ArtifactListPresenterImpl presenter = new ArtifactListPresenterImpl( view,
                                                                             new CallerMock<M2RepoService>( m2service ),
                                                                             event );
        presenter.init();
        presenter.onOpenPom( "" );
        Mockito.verify( view ).showPom( POM_TEXT );
    }

    private static void verifyRequest( final JarListPageRequest request,
                                       final String dataSourceName,
                                       final String filters,
                                       final Integer pageSize,
                                       final int startRowIndex,
                                       final boolean isAscending ) {
        assertEquals( dataSourceName, request.getDataSourceName() );
        assertEquals( filters, request.getFilters() );
        assertEquals( pageSize, request.getPageSize() );
        assertEquals( startRowIndex, request.getStartRowIndex() );
        assertEquals( isAscending, request.isAscending() );
    }
}
