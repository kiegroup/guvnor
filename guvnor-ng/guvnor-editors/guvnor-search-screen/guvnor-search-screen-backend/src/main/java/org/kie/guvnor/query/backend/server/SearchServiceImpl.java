package org.kie.guvnor.query.backend.server;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.jboss.errai.bus.server.annotations.Service;
import org.kie.guvnor.commons.data.tables.PageResponse;
import org.kie.guvnor.query.model.QueryMetadataPageRequest;
import org.kie.guvnor.query.model.SearchPageRow;
import org.kie.guvnor.query.model.SearchTermPageRequest;
import org.kie.guvnor.query.service.SearchService;
import org.uberfire.backend.vfs.VFSService;
import org.uberfire.shared.workbench.type.ResourceTypeDefinition;

@Service
@ApplicationScoped
public class SearchServiceImpl implements SearchService {

    @Inject
    private VFSService service;

    @Inject
    @Any
    Instance<ResourceTypeDefinition> typeRegister;

    private Map<String, ResourceTypeDefinition> types = new HashMap<String, ResourceTypeDefinition>();

    @PostConstruct
    private void init() {
        for ( ResourceTypeDefinition actviveType : typeRegister ) {
            types.put( actviveType.getShortName().toLowerCase(), actviveType );
        }
    }

    @Override
    public PageResponse<SearchPageRow> fullTextSearch( final SearchTermPageRequest pageRequest ) {
        return null;
    }

    @Override
    public PageResponse<SearchPageRow> queryMetadata( final QueryMetadataPageRequest pageRequest ) {
        final String format = pageRequest.getMetadata().get( "format" ).toString();
        if ( format != null ) {
            System.out.println( "TypeFound: " + types.get( format.toLowerCase() ) );
        }
        return null;
    }
}
