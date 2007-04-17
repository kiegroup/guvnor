package org.drools.brms.server.builder;

import java.io.StringReader;
import java.util.jar.JarInputStream;

import junit.framework.TestCase;

import org.drools.rule.Package;

public class PackageAssemblerTest extends TestCase {

    public void testDummy() {}
    
    public void testPartialPackage() throws Exception {

        JarInputStream jis = new JarInputStream( this.getClass().getResourceAsStream( "/billasurf.jar" ) );
        BRMSPackageBuilder builder = BRMSPackageBuilder.getInstance(  new JarInputStream[] {jis} );
        
        String header = "package foo.bar\n import com.billasurf.Person\n import com.billasurf.Board";
        builder.addPackageFromDrl( new StringReader(header) );
        assertFalse(builder.hasErrors());
        

        
        String ruleAtom = "package foo.bar rule foo \n when \n Person() \n then \n System.out.println(42); end";
        builder.addPackageFromDrl( new StringReader(ruleAtom) );
        if (builder.hasErrors()) {            
            System.err.println(builder.getErrors()[0].getMessage());
        }
        assertFalse(builder.hasErrors());

        ruleAtom = "rule foo2 \n when \n Person() \n then \n System.out.println(42); end";
        builder.addPackageFromDrl( new StringReader(ruleAtom) );
        if (builder.hasErrors()) {            
            System.err.println(builder.getErrors()[0].getMessage());
        }
        assertFalse(builder.hasErrors());
        
        assertEquals("foo.bar", builder.getPackage().getName());
        
        
        String functionAtom = "function int fooBar(String x) { return 42; }";
        builder.addPackageFromDrl( new StringReader(functionAtom) );
        if (builder.hasErrors()) {            
            System.err.println(builder.getErrors()[0].getMessage());
        }
        assertFalse(builder.hasErrors());
        
        Package p = builder.getPackage();
        assertEquals(2, p.getRules().length);
        assertEquals(1, p.getFunctions().size());
        
        
        functionAtom = "xxx";
        builder.addPackageFromDrl( new StringReader(functionAtom) );
        assertTrue(builder.hasErrors());
        builder.clearErrors();
        assertFalse(builder.hasErrors());
        
    }

}
