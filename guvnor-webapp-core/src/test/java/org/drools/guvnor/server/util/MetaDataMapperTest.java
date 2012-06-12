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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.drools.guvnor.client.rpc.MetaData;
import org.drools.guvnor.shared.api.Valid;
import org.junit.Test;

public class MetaDataMapperTest {

    @Test
    public void testMapping() {
        MetaData data = new MetaData();
        assertEquals("", data.getCoverage());
        TestBean bean = new TestBean();
        
        MetaDataMapper mapper = MetaDataMapper.getInstance();
        mapper.copyToMetaData( data, bean );
        
        assertEquals("42", data.getPublisher());
        assertEquals("42", data.getCreator());
        assertEquals("", data.getCoverage());
        
        data.setPublisher( "abc" );
        data.setCreator( "def" );
        
        mapper.copyFromMetaData( data, bean );
        
        assertEquals("abc", bean.getPublisher());
        assertEquals("def", bean.getCreator());
        
    }

    @Test
    public void testMapValid() {
        MetaData data = new MetaData();
        assertEquals(Valid.UNDETERMINED,data.getValid());
        TestBean bean = new TestBean();

        MetaDataMapper mapper = MetaDataMapper.getInstance();
        mapper.copyToMetaData( data, bean );

        assertEquals(Valid.VALID, data.getValid());

        data.setValid(Valid.INVALID);

        mapper.copyFromMetaData( data, bean );

        assertEquals(Valid.INVALID, bean.getValid());
    }
    

    

}
