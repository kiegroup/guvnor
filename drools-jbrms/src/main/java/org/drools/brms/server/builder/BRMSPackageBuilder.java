package org.drools.brms.server.builder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.drools.brms.client.common.AssetFormats;
import org.drools.compiler.PackageBuilder;
import org.drools.compiler.PackageBuilderConfiguration;
import org.drools.lang.dsl.DSLMappingFile;
import org.drools.lang.dsl.DSLMappingParseException;
import org.drools.repository.AssetItem;
import org.drools.repository.AssetItemIterator;
import org.drools.repository.PackageItem;
import org.drools.repository.RulesRepositoryException;
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
    public static BRMSPackageBuilder getInstance(List<JarInputStream> classpath) {

        ByteArrayClassLoader loader = new ByteArrayClassLoader( BRMSPackageBuilder.class.getClassLoader() );
        try {
            for ( JarInputStream jis : classpath ) {
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
        } catch ( IOException e ) {
            throw new RulesRepositoryException( e );
        }

        PackageBuilderConfiguration config = new PackageBuilderConfiguration();
        config.setClassLoader( loader );

        return new BRMSPackageBuilder( config );

    }

    /**
     * In the BRMS you should not need to use this, use the getInstance factory method instead.
     * @param config
     */
    public BRMSPackageBuilder(
                              PackageBuilderConfiguration config) {
        super( config );
    }

    /**
     * This will reset the errors.
     */
    public void clearErrors() {
        super.resetErrors();
    }

    /**
     * Load up all the DSL mappping files for the given package.
     */
    public static List<DSLMappingFile> getDSLMappingFiles(PackageItem pkg, ErrorEvent err) {
        List<DSLMappingFile> result = new ArrayList<DSLMappingFile>();
        AssetItemIterator it = pkg.listAssetsByFormat( new String[]{AssetFormats.DSL} );
        while ( it.hasNext() ) {
            AssetItem item = (AssetItem) it.next();
            String dslData = item.getContent();
            DSLMappingFile file = new DSLMappingFile();
            try {
                if ( file.parseAndLoad( new StringReader( dslData ) ) ) {
                    result.add( file );
                } else {
                    List errs = file.getErrors();
                    for ( Iterator iter = errs.iterator(); iter.hasNext(); ) {
                        DSLMappingParseException e = (DSLMappingParseException) iter.next();
                        err.logError( "An error occurred loading DSL configuration called: " + item.getName() + " line number " + e.getLine() + " : " + e.getMessage() );
                    }
                }

            } catch ( IOException e ) {
                err.logError( e.getMessage() );
            }

        }

        return result;
    }

    /**
     * Load up all the Jars for the given package.
     */
    public static List<JarInputStream> getJars(PackageItem pkg) {
        List<JarInputStream> result = new ArrayList<JarInputStream>();
        AssetItemIterator ait = pkg.listAssetsByFormat( new String[]{AssetFormats.MODEL} );
        while ( ait.hasNext() ) {
            AssetItem item = (AssetItem) ait.next();
            if ( item.getBinaryContentAttachment() != null ) {
                try {
                    result.add( new JarInputStream( item.getBinaryContentAttachment(),
                                                    false ) );
                } catch ( IOException e ) {
                    throw new RulesRepositoryException(e);
                }
            }
        }
        return result;
    }

    /**
     * This is used when loading Jars, DSLs etc to report errors.
     */
    public static interface ErrorEvent {
        public void logError(String message);
    }

}
