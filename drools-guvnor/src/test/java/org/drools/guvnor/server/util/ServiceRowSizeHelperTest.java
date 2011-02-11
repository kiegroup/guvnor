/**
 * Copyright 2011 JBoss Inc
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

package org.drools.guvnor.server.util;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.drools.guvnor.client.rpc.AbstractPageRow;
import org.drools.guvnor.client.rpc.PageRequest;
import org.drools.guvnor.client.rpc.PageResponse;
import org.junit.Test;

/**
 * 
 * @author Jari Timonen
 *
 */
public class ServiceRowSizeHelperTest {
    @Test(expected = IllegalStateException.class)
    public void testFixTotalRowSizeThrowsExceptionWhenTotalRowsCountOverInteger() {
        ServiceRowSizeHelper serviceRowSizeHelper = new ServiceRowSizeHelper();
        serviceRowSizeHelper.fixTotalRowSize( null, null, Long.MAX_VALUE, 0, true );
    }

    @Test
    public void testFixTotalRowSizeAndValueIsMinusOneAndIsNotLastPage() {
        PageRequest pageRequest = createPageRequestMock();
        PageResponse<AbstractPageRow> pageResponse = createPageResponseMock();
        initPageRequestMock( pageRequest );
        ServiceRowSizeHelper serviceRowSizeHelper = new ServiceRowSizeHelper();
        serviceRowSizeHelper.fixTotalRowSize( pageRequest, pageResponse, -1, 11, false );

        verifyExpected( pageResponse, 21, true );
    }

    @Test
    public void testFixTotalRowSizeAndValueIsMinusOneAndIsLastPage() {
        PageRequest pageRequest = createPageRequestMock();

        PageResponse<AbstractPageRow> pageResponse = createPageResponseMock();
        initPageRequestMock( pageRequest );
        ServiceRowSizeHelper serviceRowSizeHelper = new ServiceRowSizeHelper();
        serviceRowSizeHelper.fixTotalRowSize( pageRequest, pageResponse, -1, 11, true );

        verifyExpected( pageResponse, -1, false );

    }

    @Test
    public void testFixTotalRowSizeAndValueIsZeroOrOver() {
        PageRequest pageRequest = createPageRequestMock();

        PageResponse<AbstractPageRow> pageResponse = createPageResponseMock();
        initPageRequestMock( pageRequest );
        ServiceRowSizeHelper serviceRowSizeHelper = new ServiceRowSizeHelper();
        serviceRowSizeHelper.fixTotalRowSize( pageRequest, pageResponse, 100, 11, true );

        verifyExpected( pageResponse, 100, true );
    }

    private void initPageRequestMock(PageRequest pageRequest) {
        when( pageRequest.getStartRowIndex() ).thenReturn( 10 );
    }

    private void verifyExpected(final PageResponse< ? > pageResponse, int totalRowSize, boolean totalRowSizeExact) {
        verify( pageResponse ).setTotalRowSize( totalRowSize );
        verify( pageResponse ).setTotalRowSizeExact( totalRowSizeExact );
    }

    private PageRequest createPageRequestMock() {
        return mock( PageRequest.class );
    }

    @SuppressWarnings("unchecked")
    private PageResponse<AbstractPageRow> createPageResponseMock() {
        return (PageResponse<AbstractPageRow>) mock( PageResponse.class );
    }

}
