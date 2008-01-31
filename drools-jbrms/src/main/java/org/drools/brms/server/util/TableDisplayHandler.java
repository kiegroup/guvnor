package org.drools.brms.server.util;

/*
 * Copyright 2005 JBoss Inc
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

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import org.drools.brms.client.rpc.TableConfig;
import org.drools.brms.client.rpc.TableDataResult;
import org.drools.brms.client.rpc.TableDataRow;
import org.drools.repository.AssetItem;
import org.drools.repository.AssetItemIterator;
import org.drools.repository.AssetPageList;

import com.google.gwt.user.client.rpc.SerializableException;

/**
 * This utility class handles loading of tables.
 *
 * This is to give some flexibility in what fields are displayed.
 * rulelist.properties and archivedrulelist.properties are the files used.
 *
 * @author Michael Neale
 */
public class TableDisplayHandler {

    private RowLoader          ASSET_LIST;

    public static final String DEFAULT_TABLE_TEMPLATE      = "rulelist";
    public static final String ARCHIVED_RULE_LIST_TABLE_ID = "archivedrulelist";

    /**
     * Produce a table dataset for a given iterator.
     * @param list The iterator.
     * @param numRows The number of rows to go to. -1 means don't stop.
     * @throws SerializableException
     */

    public TableDisplayHandler(String tableconfig) {
        ASSET_LIST = new RowLoader( tableconfig );
    }

    public TableDataResult loadRuleListTable(AssetPageList list) throws SerializableException {
        List<TableDataRow> data = loadRows(list.assets.iterator(), -1);
        TableDataResult result = new TableDataResult();
        result.data = (TableDataRow[]) data.toArray( new TableDataRow[data.size()] );
        result.total  = list.totalSize;
        result.hasNext = list.hasNext;
        return result;
    }

    public TableDataResult loadRuleListTable(AssetItemIterator it, int skip, int numRows) {
    	if (numRows != -1) {
    		it.skip(skip);
    	}
        List<TableDataRow> data = loadRows(it, numRows);
        TableDataResult result = new TableDataResult();
        result.data = (TableDataRow[]) data.toArray( new TableDataRow[data.size()] );
        result.total  = it.getSize();
        result.hasNext = it.hasNext();
        return result;

    }

	private List<TableDataRow> loadRows(Iterator<AssetItem> it, int numRows) {
		List<TableDataRow> data = new ArrayList<TableDataRow>();

        for ( Iterator<AssetItem> iter = it; iter.hasNext(); ) {
            AssetItem r = (AssetItem) iter.next();
            TableDataRow row = new TableDataRow();

            row.id = r.getUUID();
            row.format = r.getFormat();
            row.values = ASSET_LIST.getRow( r );
            data.add( row );
            if ( numRows != -1 ) {
                if ( data.size() == numRows ) {
                    break;
                }
            }
        }
		return data;
	}

    public String formatDate(Calendar cal) {
        DateFormat localFormat = DateFormat.getDateInstance();

        return localFormat.format( cal.getTime() );
    }

    public TableConfig loadTableConfig() {
        final TableConfig config = new TableConfig();

        config.headers = ASSET_LIST.getHeaders();
        config.rowsPerPage = 40;
        return config;
    }
}