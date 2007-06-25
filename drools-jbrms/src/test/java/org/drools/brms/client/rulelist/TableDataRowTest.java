package org.drools.brms.client.rulelist;
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



import junit.framework.TestCase;

import org.drools.brms.client.rpc.TableDataRow;

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