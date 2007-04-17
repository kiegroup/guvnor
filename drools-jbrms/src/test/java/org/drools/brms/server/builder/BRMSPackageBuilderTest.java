package org.drools.brms.server.builder;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarInputStream;

import junit.framework.TestCase;

import org.drools.lang.descr.AndDescr;
import org.drools.lang.descr.PackageDescr;
import org.drools.lang.descr.RuleDescr;
import org.drools.rule.Package;

public class BRMSPackageBuilderTest extends TestCase {

    public void testPartialPackage() throws Exception {

        JarInputStream jis = new JarInputStream( this.getClass().getResourceAsStream( "/billasurf.jar" ) );
        List<JarInputStream> l = new ArrayList<JarInputStream>();
        l.add( jis );
        BRMSPackageBuilder builder = BRMSPackageBuilder.getInstance( l );
        
        PackageDescr pc = new PackageDescr("foo.bar");
        builder.addPackage( pc );
        
        String header = "import com.billasurf.Person\n import com.billasurf.Board";
        builder.addPackageFromDrl( new StringReader(header) );
        assertFalse(builder.hasErrors());
        

        
        String ruleAtom = "rule foo \n when \n Person() \n then \n System.out.println(42); end";
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
        
        RuleDescr rule = new RuleDescr("abc");
        PackageDescr pkg = new PackageDescr(null);
        
        AndDescr and = new AndDescr();
        rule.setLhs( and );
        pkg.addRule( rule );
        
        builder.addPackage( pkg );
        assertFalse(builder.hasErrors());

        
        assertEquals(3, p.getRules().length);
        assertNotNull(p.getRule( "abc" ));
        
    }
    

    
    

}
