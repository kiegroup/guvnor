

package org.drools.guvnor.server.contenthandler;

import java.io.InputStream;
import static org.junit.Assert.*;

import org.junit.Test;

public class SpringContextValidatorTest {

    @Test
    public void testValidContext(){
        SpringContextValidator validator = new SpringContextValidator();
        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream("org/drools/guvnor/server/contenthandler/valid-spring-context.xml");
        validator.setContent(resourceAsStream);
        assertEquals("",validator.validate());
    }

    @Test
    public void testInvalidContext(){
        SpringContextValidator validator = new SpringContextValidator();
        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream("org/drools/guvnor/server/contenthandler/invalid-spring-context.xml");
        validator.setContent(resourceAsStream);
        assertFalse(validator.validate().length() ==0);
    }

    @Test
    public void testMalformedContext(){
        SpringContextValidator validator = new SpringContextValidator();
        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream("org/drools/guvnor/server/contenthandler/malformed-spring-context.xml");
        validator.setContent(resourceAsStream);
        assertFalse(validator.validate().length() ==0);
    }
        
}
