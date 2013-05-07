package org.kie.guvnor.query.service;

import org.jboss.errai.bus.server.annotations.Remote;
import org.kie.guvnor.query.model.QueryMetadataPageRequest;
import org.kie.guvnor.query.model.SearchPageRow;
import org.kie.guvnor.query.model.SearchTermPageRequest;
import org.uberfire.client.tables.PageResponse;

@Remote
public interface SearchService {

    PageResponse<SearchPageRow> fullTextSearch( final SearchTermPageRequest searchTerm );

    PageResponse<SearchPageRow> queryMetadata( final QueryMetadataPageRequest queryRequest );
}
