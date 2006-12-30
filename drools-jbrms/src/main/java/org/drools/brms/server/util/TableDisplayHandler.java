package org.drools.brms.server.util;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import javax.jcr.RepositoryException;

import org.drools.brms.client.rpc.TableConfig;
import org.drools.brms.client.rpc.TableDataResult;
import org.drools.brms.client.rpc.TableDataRow;
import org.drools.repository.AssetItem;

import com.google.gwt.user.client.rpc.SerializableException;

/**
 * This utility class handles loading of tables.
 * 
 * This is to give some flexibility in what fields are displayed.
 * This will likely be dynamic in the future (driven of user config stored in the 
 * repository).
 * 
 * @author michael neale
 */
public class TableDisplayHandler {

    
    public TableDataResult loadRuleListTable(List list) throws SerializableException {
        List data = new ArrayList();
        
        for ( Iterator iter = list.iterator(); iter.hasNext(); ) {
            AssetItem rule = (AssetItem) iter.next();
            TableDataRow row = new TableDataRow();
            try {

                
                row.id = rule.getNode().getUUID();
                row.format = rule.getFormat();
                row.values = new String[4];
                row.values[0] = rule.getName();
                row.values[1] = formatDate(rule.getLastModified());
                row.values[2] = rule.getStateDescription();                
                row.values[3] = rule.getVersionNumber();
                data.add( row );
            } catch ( RepositoryException e ) {
                throw new SerializableException(e.getMessage());
            }            
        }
        TableDataResult result = new TableDataResult();
        result.data = (TableDataRow[]) data.toArray( new TableDataRow[data.size()] );
        result.numberOfRows = data.size();
        return result;        
    }
    
    public String formatDate(Calendar cal) {
        DateFormat localFormat = DateFormat.getDateInstance();
        
        return localFormat.format( cal.getTime() );
    }

    public TableConfig loadTableConfig(String listName) {
        final TableConfig config = new TableConfig();

        config.headers = new String[]{"name", 
                                      "last modified", 
                                      "state", 
                                      "version"};
        config.rowsPerPage = 30;
        return config;
    }
    
    
}
