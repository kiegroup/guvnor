package org.drools.brms.server.util;

import java.lang.reflect.Method;

import junit.framework.TestCase;

import org.drools.brms.server.util.TableDisplayHandler.RowLoader;

public class TableDisplayHandlerTest extends TestCase {

    public void testRowLoaders() throws Exception {
        TableDisplayHandler handler = new TableDisplayHandler();
        RowLoader loader = new TableDisplayHandler.RowLoader(this.getClass().getResourceAsStream( "/AssetListTable.properties" ));
        
        assertEquals(4, loader.getHeaders().length);
        String[] headers = loader.getHeaders();
        assertEquals("Name", headers[0]);
        assertEquals("Last modified", headers[1]);
        assertEquals("Status", headers[2]);
        assertEquals("Version", headers[3]);
        
        assertEquals(4, loader.extractors.size());
        assertTrue(loader.extractors.get( 0 ) instanceof Method);
        assertEquals(((Method)loader.extractors.get( 2 )).getName(), "getStateDescription");
        
    }
    
    
}
