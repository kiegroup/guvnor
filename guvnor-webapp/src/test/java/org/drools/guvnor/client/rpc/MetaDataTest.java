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

package org.drools.guvnor.client.rpc;



import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MetaDataTest {

    @Test
    public void testAddCats() {
        MetaData data = new MetaData();
        data.addCategory( "new cat" );
        assertEquals(1, data.getCategories().length);
        assertEquals("new cat", data.getCategories()[0]);
        
        data.addCategory( "another" );
        assertEquals(2, data.getCategories().length);
        assertEquals("another", data.getCategories()[1]);
        
        data.addCategory( "another" );
        assertEquals(2, data.getCategories().length);
    }
    
    @Test
    public void testRemoveCats() {
        MetaData data = new MetaData();
        data.setCategories( new String[] {"wa", "la"} );
        data.removeCategory( 0 );
        assertEquals(1, data.getCategories().length);
        assertEquals("la", data.getCategories()[0]);
    }
    
}
