package org.drools.guvnor.server.util;

import org.drools.guvnor.client.rpc.AbstractPageRow;
import org.drools.guvnor.client.rpc.PageRequest;
import org.drools.guvnor.client.rpc.PageResponse;

public class ServiceRowSizeHelper {
    // The total Record Count returned from AssetItemIterator.getSize() can be
    // -1 which is not very helpful. We can however derive the total row count
    // when on the last page of data
    public void fixTotalRowSize(PageRequest request, PageResponse< ? extends AbstractPageRow> response, long totalRowsCount, int rowsRetrievedCount, boolean bHasMoreRows) {

        // CellTable only handles integer row counts
        if ( totalRowsCount > Integer.MAX_VALUE ) {
            throw new IllegalStateException( "The totalRowSize (" + totalRowsCount + ") is too big." );
        }

        // Unable to ascertain size of whole data-set
        if ( totalRowsCount == -1 ) {

            // Last page, we can be derive absolute size
            if ( !bHasMoreRows ) {
                response.setTotalRowSize( request.getStartRowIndex() + rowsRetrievedCount );
                response.setTotalRowSizeExact( true );
            } else {
                response.setTotalRowSize( -1 );
                response.setTotalRowSizeExact( false );
            }
        } else {
            response.setTotalRowSize( (int) totalRowsCount );
            response.setTotalRowSizeExact( true );
        }
    }
}
