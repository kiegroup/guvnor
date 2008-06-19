package org.drools.guvnor.client.rpc;
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



import org.drools.guvnor.client.rpc.MetaData;

import junit.framework.TestCase;

public class MetaDataTest extends TestCase {

    public void testAddCats() {
        MetaData data = new MetaData();
        data.addCategory( "new cat" );
        assertEquals(1, data.categories.length);
        assertEquals("new cat", data.categories[0]);
        
        data.addCategory( "another" );
        assertEquals(2, data.categories.length);
        assertEquals("another", data.categories[1]);
        
        data.addCategory( "another" );
        assertEquals(2, data.categories.length);
    }
    
    public void testRemoveCats() {
        MetaData data = new MetaData();
        data.categories = new String[] {"wa", "la"};
        data.removeCategory( 0 );
        assertEquals(1, data.categories.length);
        assertEquals("la", data.categories[0]);
    }
    
}