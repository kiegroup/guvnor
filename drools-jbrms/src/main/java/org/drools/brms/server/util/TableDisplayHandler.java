package org.drools.brms.server.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.drools.brms.client.rpc.TableConfig;
import org.drools.brms.client.rpc.TableDataResult;
import org.drools.brms.client.rpc.TableDataRow;
import org.drools.repository.AssetItem;
import org.drools.repository.RulesRepositoryException;

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

    
    private RowLoader ASSET_LIST = new RowLoader(TableDisplayHandler.class.getResourceAsStream( "/AssetListTable.properties" ));
    
    /**
     * Produce a table dataset for a given iterator.
     * @param list The iterator.
     * @param numRows The number of rows to go to. -1 means don't stop.
     * @throws SerializableException
     */
    public TableDataResult loadRuleListTable(Iterator list, int numRows) throws SerializableException {
        List data = new ArrayList();
        
        
        
        for ( Iterator iter = list; iter.hasNext(); ) {
            AssetItem rule = (AssetItem) iter.next();
            TableDataRow row = new TableDataRow();
                
                row.id = rule.getUUID();
                row.format = rule.getFormat();
                row.values = ASSET_LIST.getRow( rule );
                data.add( row );
                if (numRows != -1) {
                    if (data.size() == numRows) {
                        break;
                    }
                }
        }
        TableDataResult result = new TableDataResult();
        result.data = (TableDataRow[]) data.toArray( new TableDataRow[data.size()] );
        return result;        
    }
    

    
    public String formatDate(Calendar cal) {
        DateFormat localFormat = DateFormat.getDateInstance();
        
        return localFormat.format( cal.getTime() );
    }

    public TableConfig loadTableConfig(String listName) {
        final TableConfig config = new TableConfig();

//        config.headers = new String[]{"name", 
//                                      "last modified", 
//                                      "state", 
//                                      "version"};
        config.headers = ASSET_LIST.getHeaders();
        config.rowsPerPage = 30;
        return config;
    }
    

    
    static class RowLoader {
        
        
        private String[] headers;
        List extractors;
        
        public String[] getHeaders() {
            return headers;
        }
        
        public String[] getRow(AssetItem item)  {
            String[] row = new String[headers.length];
            for ( int i = 0; i < row.length; i++ ) {
                Method meth = (Method) extractors.get( i );
                try {
                    Object val = meth.invoke( item, (Object[]) null );
                    if (val instanceof String) {
                        String s = (String) val;
                        if (s.length() > 64) {
                            s = s.substring( 0, 61 ) + "...";
                        }
                        row[i] = s;
                        
                    } else if (val instanceof Calendar) {
                        row[i] = DateFormat.getDateInstance().format( (( Calendar ) val).getTime() );
                    } else {
                        row[i] = val.toString();
                    }
                } catch (Exception e) {
                    if (e instanceof RuntimeException) throw (RuntimeException) e;
                    throw new RulesRepositoryException ( e );
                }
            }
            return row;
        }
        
        public RowLoader(InputStream in) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            List fields = new ArrayList();
            extractors = new ArrayList();
            String line;
            
            try {
            
            while ( (line = reader.readLine())  != null) {
                if (!line.startsWith( "#" ) && !line.trim().equals( "" )) {
                    StringTokenizer tok = new StringTokenizer(line, "=");
                    String field = tok.nextToken();
                    String method = tok.nextToken();
                    fields.add( field );
                    
                    final Method meth = AssetItem.class.getMethod( method, new Class[] {} );
                    
                    extractors.add( meth );
                    
                }
            }
            } catch (Exception e) {
                if (e instanceof RuntimeException) {
                    throw (RuntimeException) e;
                } 
                throw new RulesRepositoryException( e );
            }
            headers = (String[]) fields.toArray( new String[fields.size()] );            
        }
        
        
        
    }
    
    
}
