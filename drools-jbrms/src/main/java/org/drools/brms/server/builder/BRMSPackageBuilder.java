package org.drools.brms.server.builder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.drools.compiler.PackageBuilder;
import org.drools.compiler.PackageBuilderConfiguration;
import org.drools.resource.util.ByteArrayClassLoader;

/**
 * This decorates the drools-compiler PackageBuilder
 * with some functionality needed for the BRMS.
 * This can use the BRMS repo as a classpath.
 * 
 * @author Michael Neale
 */
public class BRMSPackageBuilder extends PackageBuilder {

    /**
     * This will give you a fresh new PackageBuilder 
     * using the given classpath.
     */
    public static BRMSPackageBuilder getInstance(JarInputStream[] classpath) throws IOException {

        ByteArrayClassLoader loader = new ByteArrayClassLoader( BRMSPackageBuilder.class.getClassLoader() );

        for ( int i = 0; i < classpath.length; i++ ) {

            JarInputStream jis = classpath[i];
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

        }
        
        PackageBuilderConfiguration config = new PackageBuilderConfiguration();
        config.setClassLoader( loader );        

        return new BRMSPackageBuilder(config);

    }
    
    /**
     * In the BRMS you should not need to use this, use the getInstance factory method instead.
     * @param config
     */
    public BRMSPackageBuilder(PackageBuilderConfiguration config) {
        super(config);
    }

    /**
     * This will reset the errors.
     */
    public void clearErrors() {
        super.resetErrors();
    }

}
