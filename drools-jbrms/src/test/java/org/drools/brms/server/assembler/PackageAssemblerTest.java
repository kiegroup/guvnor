package org.drools.brms.server.assembler;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.util.Iterator;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.drools.brms.client.rpc.PackageConfigData;
import org.drools.compiler.PackageBuilder;
import org.drools.compiler.PackageBuilderConfiguration;
import org.drools.resource.util.ByteArrayClassLoader;

import junit.framework.TestCase;

public class PackageAssemblerTest extends TestCase {

    public void testDummy() {}
    
    public void testPartialPackage() throws Exception {
        ByteArrayClassLoader loader = new ByteArrayClassLoader( this.getClass().getClassLoader() );

        JarInputStream jis = new JarInputStream( this.getClass().getResourceAsStream( "/billasurf.jar" ) );
        JarEntry entry = null;
        byte[] buf = new byte[1024];
        int len = 0;
        while ( (entry = jis.getNextJarEntry()) != null ) {
            if ( !entry.isDirectory() ) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                while ( (len = jis.read( buf )) >= 0 ) {
                    out.write( buf, 0, len );
                }
                loader.addResource( entry.getName(), out.toByteArray() );
            }
        }
        
        //now we have a loader
        
        PackageBuilderConfiguration config = new PackageBuilderConfiguration();
        config.setClassLoader( loader );
        
        assertSame(loader, config.getClassLoader());
        
        
        PackageBuilder builder = new PackageBuilder(config);
        
        
        
        String header = "package foo.bar\n import com.billasurf.Person\n import com.billasurf.Board";
        builder.addPackageFromDrl( new StringReader(header) );
        assertFalse(builder.hasErrors());
        

        
        String ruleAtom = "package foo.bar rule foo \n when \n Person() \n then \n System.out.println(42); end";
        builder.addPackageFromDrl( new StringReader(ruleAtom) );
        if (builder.hasErrors()) {            
            System.err.println(builder.getErrors()[0].getMessage());
        }
        assertFalse(builder.hasErrors());

        ruleAtom = "package foo.bar2 rule foo2 \n when \n Person() \n then \n System.out.println(42); end";
        builder.addPackageFromDrl( new StringReader(ruleAtom) );
        if (builder.hasErrors()) {            
            System.err.println(builder.getErrors()[0].getMessage());
        }
        assertFalse(builder.hasErrors());
        
        
        
        
    }

}
