package org.drools.guvnor.server.builder.pagerow;

import org.drools.guvnor.client.rpc.QueryMetadataPageRequest;
import org.drools.guvnor.client.rpc.QueryPageRow;
import org.drools.guvnor.server.CategoryFilter;
import org.drools.guvnor.server.util.QueryPageRowCreator;
import org.drools.repository.AssetItem;
import org.drools.repository.RepositoryFilter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class QueryMetadataPageRowBuilder
        implements
        PageRowBuilder<QueryMetadataPageRequest, Iterator<AssetItem>> {

    private QueryMetadataPageRequest pageRequest;
    private Iterator<AssetItem> iterator;

    public List<QueryPageRow> build() {
        validate();
        int skipped = 0;
        Integer pageSize = pageRequest.getPageSize();
        int startRowIndex = pageRequest.getStartRowIndex();
        RepositoryFilter categoryFilter = new CategoryFilter();
        List<QueryPageRow> rowList = new ArrayList<QueryPageRow>();

        while (iterator.hasNext() && (pageSize == null || rowList.size() < pageSize)) {
            AssetItem assetItem = iterator.next();

            // Cannot use AssetItemIterator.skip() as it skips non-filtered
            // assets whereas startRowIndex is the index of the
            // first displayed asset (i.e. filtered)
            if (skipped >= startRowIndex) {
                rowList.add(QueryPageRowCreator.makeQueryPageRow(assetItem));
            }
            skipped++;
        }
        return rowList;
    }

    public void validate() {
        if (pageRequest == null) {
            throw new IllegalArgumentException("PageRequest cannot be null");
        }

        if (iterator == null) {
            throw new IllegalArgumentException("Content cannot be null");
        }

    }

    public QueryMetadataPageRowBuilder withPageRequest(QueryMetadataPageRequest pageRequest) {
        this.pageRequest = pageRequest;
        return this;
    }

    public QueryMetadataPageRowBuilder withContent(Iterator<AssetItem> iterator) {
        this.iterator = iterator;
        return this;
    }

}
