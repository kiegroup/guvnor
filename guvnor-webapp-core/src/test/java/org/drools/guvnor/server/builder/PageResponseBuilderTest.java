package org.drools.guvnor.server.builder;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.drools.guvnor.client.rpc.AbstractPageRow;
import org.drools.guvnor.client.rpc.PageResponse;
import org.junit.Test;

public class PageResponseBuilderTest {

    private PageResponseBuilder<AbstractPageRow> createPageResponseBuilderWith(int startRowIndex,
                                                                               List<AbstractPageRow> rowList,
                                                                               boolean hasMoreRows) {
        return new PageResponseBuilder<AbstractPageRow>()
                .withStartRowIndex( startRowIndex )
                .withPageRowList( rowList )
                .withLastPage( !hasMoreRows );
    }

    @Test(expected = IllegalStateException.class)
    public void testFixTotalRowSizeThrowsExceptionWhenTotalRowsCountOverInteger() {
        createPageResponseBuilderWith( 0,
                                       null,
                                       true ).buildWithTotalRowCount( Long.MAX_VALUE );
    }

    @Test
    public void testFixTotalRowSizeAndValueIsMinusOneAndIsNotLastPage() {
        @SuppressWarnings("rawtypes")
        List rowList = mock( List.class );
        when( rowList.size() ).thenReturn( 11 );
        @SuppressWarnings("unchecked")
        PageResponse<AbstractPageRow> pageResponse = createPageResponseBuilderWith( 10,
                                                                                    rowList,
                                                                                    false ).buildWithTotalRowCount( -1 );

        assertEquals( pageResponse.getTotalRowSize(),
                      21 );
        assertEquals( pageResponse.isTotalRowSizeExact(),
                      true );

    }

    @Test
    public void testFixTotalRowSizeAndValueIsMinusOneAndIsLastPage() {

        @SuppressWarnings("rawtypes")
        List rowList = mock( List.class );
        when( rowList.size() ).thenReturn( 11 );
        @SuppressWarnings("unchecked")
        PageResponse<AbstractPageRow> pageResponse = createPageResponseBuilderWith( 10,
                                                                                    rowList,
                                                                                    true ).buildWithTotalRowCount( -1 );

        assertEquals( pageResponse.getTotalRowSize(),
                      -1 );
        assertEquals( pageResponse.isTotalRowSizeExact(),
                      false );

    }

    @Test
    public void testFixTotalRowSizeAndValueIsZeroOrOver() {
        @SuppressWarnings("rawtypes")
        List rowList = mock( List.class );
        when( rowList.size() ).thenReturn( 11 );
        @SuppressWarnings("unchecked")
        PageResponse<AbstractPageRow> pageResponse = createPageResponseBuilderWith( 10,
                                                                                    rowList,
                                                                                    true ).buildWithTotalRowCount( 100 );

        assertEquals( pageResponse.getTotalRowSize(),
                      100 );
        assertEquals( pageResponse.isTotalRowSizeExact(),
                      true );
    }
}