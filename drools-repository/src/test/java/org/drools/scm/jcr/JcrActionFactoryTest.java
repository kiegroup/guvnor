package org.drools.scm.jcr;

import junit.framework.TestCase;

public class JcrActionFactoryTest extends TestCase {

    public void testMapPathNameToPackage() {
        JcrActionFactory fact = new JcrActionFactory();
        assertEquals("org.foo.bar", fact.toPackageName("org/foo/bar"));
        assertEquals("foo", fact.toPackageName("foo"));
        assertEquals("FooBar", fact.toPackageName("FooBar"));
        
    
        assertEquals("org/foo/bar", fact.toDirectoryName("org.foo.bar"));
        assertEquals("foo", fact.toDirectoryName("foo"));
    }
    
    
    
}
