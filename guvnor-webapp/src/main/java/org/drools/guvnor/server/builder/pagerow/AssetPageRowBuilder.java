/*
 * Copyright 2011 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.drools.guvnor.server.builder.pagerow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.drools.guvnor.client.rpc.AssetPageRequest;
import org.drools.guvnor.client.rpc.AssetPageRow;
import org.drools.guvnor.client.rpc.SortableColumnMetaData;
import org.drools.guvnor.client.rpc.SortableColumnsMetaData;
import org.drools.guvnor.server.util.AssetPageRowPopulator;
import org.drools.repository.AssetItem;
import org.drools.repository.AssetItemIterator;

public class AssetPageRowBuilder
        implements
        PageRowBuilder<AssetPageRequest, AssetItemIterator> {

    private AssetPageRequest pageRequest;
    private AssetItemIterator iterator;

    public List<AssetPageRow> build() {
        validate();
        Integer pageSize = pageRequest.getPageSize();
        int pageStartRowIndex = pageRequest.getStartRowIndex();
        AssetPageRowPopulator assetPageRowPopulator = new AssetPageRowPopulator();

        iterator.skip( pageStartRowIndex );
        List<AssetPageRow> rowList = new ArrayList<AssetPageRow>();
        while ( iterator.hasNext() && ( pageSize == null || rowList.size() < pageSize ) ) {
            AssetItem assetItem = iterator.next();
            rowList.add( assetPageRowPopulator.populateFrom( assetItem ) );
        }
        return rowList;
    }

    private void sortData( final List<AssetPageRow> assetItems,
                           final SortableColumnsMetaData sortableColumnsMetaData ) {
        Collections.sort( assetItems,
                          new Comparator<AssetPageRow>() {
                              public int compare( AssetPageRow leftRow,
                                                  AssetPageRow rightRow ) {
                                  for ( SortableColumnMetaData sortableColumnMetaData : sortableColumnsMetaData.getSortListOrder() ) {

                                      Comparable leftColumnValue = getValue( sortableColumnMetaData,
                                                                             leftRow );
                                      Comparable rightColumnValue = getValue( sortableColumnMetaData,
                                                                              rightRow );
                                      int comparison = ( leftColumnValue == rightColumnValue ) ? 0
                                              : ( leftColumnValue == null ) ? -1
                                              : ( rightColumnValue == null ) ? 1
                                              : leftColumnValue.compareTo( rightColumnValue );
                                      if ( comparison != 0 ) {
                                          switch ( sortableColumnMetaData.getSortDirection() ) {
                                              case ASCENDING:
                                                  break;
                                              case DESCENDING:
                                                  comparison = -comparison;
                                                  break;
                                              default:
                                                  throw new IllegalStateException( "Sorting can only be enabled for ASCENDING or" +
                                                                                           " DESCENDING, not sortDirection (" + sortableColumnMetaData.getSortDirection() + ") ." );
                                          }
                                          return comparison;
                                      }
                                  }
                                  return leftRow.compareTo( rightRow );
                              }

                              private Comparable getValue( final SortableColumnMetaData sortableColumnMetaData,
                                                           final AssetPageRow row ) {
                                  final String columnName = sortableColumnMetaData.getColumnName();
                                  if ( columnName == null ) {
                                      throw new IllegalStateException( "Attempt to sort on null column name." );
                                  }
                                  return row.getName();
                              }
                          } );
    }

    public void validate() {
        if ( pageRequest == null ) {
            throw new IllegalArgumentException( "PageRequest cannot be null" );
        }

        if ( iterator == null ) {
            throw new IllegalArgumentException( "Content cannot be null" );
        }

    }

    public AssetPageRowBuilder withPageRequest( AssetPageRequest pageRequest ) {
        this.pageRequest = pageRequest;
        return this;
    }

    public AssetPageRowBuilder withContent( AssetItemIterator iterator ) {
        this.iterator = iterator;
        return this;
    }
}
