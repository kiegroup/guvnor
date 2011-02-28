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

package org.drools.guvnor.server.util;

import static org.junit.Assert.assertEquals;

import org.drools.guvnor.client.rpc.MetaData;
import org.junit.Test;

public class MetaDataMapperTest {

    @Test
    public void testMapping() {
        MetaData data = new MetaData();
        assertEquals("", data.coverage);
        TestBean bean = new TestBean();
        
        MetaDataMapper mapper = MetaDataMapper.getInstance();
        mapper.copyToMetaData( data, bean );
        
        assertEquals("42", data.publisher);
        assertEquals("42", data.creator);
        assertEquals("", data.coverage);
        
        data.publisher = "abc";
        data.creator = "def";
        
        mapper.copyFromMetaData( data, bean );
        
        assertEquals("abc", bean.getPublisher());
        assertEquals("def", bean.getCreator());
        
    }
    

    

}
