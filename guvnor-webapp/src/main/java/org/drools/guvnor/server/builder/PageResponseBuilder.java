/*
 * Copyright 2011 JBoss Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package org.drools.guvnor.server.builder;

import org.drools.guvnor.client.rpc.AbstractPageRow;
import org.drools.guvnor.client.rpc.PageResponse;

import java.util.List;

public class PageResponseBuilder<T extends AbstractPageRow> {
    private final PageResponse<T> pageResponse = new PageResponse<T>();

    public PageResponseBuilder<T> withStartRowIndex(int startRowIndex) {
        pageResponse.setStartRowIndex(startRowIndex);
        return this;
    }

    public PageResponseBuilder<T> withPageRowList(final List<T> assetPageRowList) {
        pageResponse.setPageRowList(assetPageRowList);
        return this;
    }

    public PageResponseBuilder<T> withLastPage(final boolean isLastPage) {
        pageResponse.setLastPage(isLastPage);
        return this;
    }

    public PageResponseBuilder<T> withTotalRowSize(final int totalRowSize) {
        pageResponse.setTotalRowSize(totalRowSize);
        return this;
    }

    public PageResponseBuilder<T> withTotalRowSizeHelper(final int totalRowCount) {
        fixTotalRowSize(totalRowCount);
        return this;
    }

    public PageResponseBuilder<T> withTotalRowSizeExact() {
        pageResponse.setTotalRowSizeExact(true);
        return this;
    }

    public void fixTotalRowSize(long totalRowsCount) {

        // CellTable only handles integer row counts
        if (totalRowsCount > Integer.MAX_VALUE) {
            throw new IllegalStateException("The totalRowSize (" + totalRowsCount + ") is too big.");
        }

        // Unable to ascertain size of whole data-set
        if (totalRowsCount == -1) {

            // Last page, we can be derive absolute size
            if (pageResponse.isLastPage()) {
                pageResponse.setTotalRowSize(pageResponse.getStartRowIndex() + pageResponse.getPageRowList().size());
                pageResponse.setTotalRowSizeExact(true);
            } else {
                pageResponse.setTotalRowSize(-1);
                pageResponse.setTotalRowSizeExact(false);
            }
        } else {
            pageResponse.setTotalRowSize((int) totalRowsCount);
            pageResponse.setTotalRowSizeExact(true);
        }
    }

    public PageResponse<T> build() {
        return pageResponse;
    }

    public PageResponse<T> buildWithTotalRowCount(final long totalRowCount) {
        fixTotalRowSize(totalRowCount);
        return pageResponse;
    }

}
