package org.drools.brms.server.util;

import junit.framework.TestCase;

import org.drools.brms.client.rpc.MetaData;

public class MetaDataMapperTest extends TestCase {

    public void testMapping() {
        MetaData data = new MetaData();
        assertEquals("", data.coverage);
        TestBean bean = new TestBean();
        
        MetaDataMapper mapper = new MetaDataMapper();
        mapper.copyToMetaData( data, bean );
        
        assertEquals("42", data.publisher);
        assertEquals("42", data.creator);
        assertEquals("", data.coverage);
        
        data.publisher = "abc";
        data.creator = "def";
        
        mapper.copyFromMetaData( data, bean );
        
        assertEquals("abc", bean.getPublisher());
        assertEquals("def", bean.getCreator());
        
        assertFalse(data.dirty);
        
    }
    

    

}
