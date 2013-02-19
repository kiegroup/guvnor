package org.kie.guvnor.query.backend.server;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.bus.server.annotations.Service;
import org.kie.guvnor.commons.data.tables.PageResponse;
import org.kie.guvnor.query.model.QueryMetadataPageRequest;
import org.kie.guvnor.query.model.SearchPageRow;
import org.kie.guvnor.query.model.SearchTermPageRequest;
import org.kie.guvnor.query.service.SearchService;
import org.uberfire.backend.vfs.VFSService;

@Service
@ApplicationScoped
public class SearchServiceImpl implements SearchService {

    @Inject
    private VFSService service;

    @Override
    public PageResponse<SearchPageRow> fullTextSearch( final SearchTermPageRequest pageRequest ) {
        return null;
    }

    @Override
    public PageResponse<SearchPageRow> queryMetadata( final QueryMetadataPageRequest pageRequest ) {
        return null;
    }
}
