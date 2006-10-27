package org.drools.brms.client.rulelist;

import org.drools.brms.client.rpc.TableDataRow;

import junit.framework.TestCase;

public class TableDataRowTest extends TestCase {

    public void testRow() {
        TableDataRow row = new TableDataRow();
        row.id = "HJKHFKJHFDJS";
        row.format = "rule";
        row.values = new String[]{"name", "x"};
        
        assertEquals("name", row.getDisplayName());
        
        assertEquals(row.id + "," + row.format, row.getKeyValue());
        
        assertEquals(row.id, TableDataRow.getId( row.getKeyValue() ));
        assertEquals(row.format, TableDataRow.getFormat( row.getKeyValue()));
    }
    
}
