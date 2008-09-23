package org.drools.guvnor.server.builder;
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



import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.drools.compiler.PackageBuilder;
import org.drools.compiler.PackageBuilderConfiguration;
import org.drools.guvnor.client.common.AssetFormats;
import org.drools.lang.dsl.DSLMappingFile;
import org.drools.lang.dsl.DSLMappingParseException;
import org.drools.lang.dsl.DefaultExpander;
import org.drools.repository.AssetItem;
import org.drools.repository.AssetItemIterator;
import org.drools.repository.PackageItem;
import org.drools.repository.RulesRepositoryException;
import org.drools.rule.MapBackedClassLoader;
import org.drools.rule.builder.dialect.java.JavaDialectConfiguration;
import org.drools.util.ChainedProperties;

/**
 * This decorates the drools-compiler PackageBuilder
 * with some functionality needed for the BRMS.
 * This can use the BRMS repo as a classpath.
 *
 * @author Michael Neale
 */
public class BRMSPackageBuilder extends PackageBuilder {

    private List<DSLMappingFile> dslFiles;
    private DefaultExpander expander;
    
    /**
     * This will give you a fresh new PackageBuilder
     * using the given classpath.
     */
    public static BRMSPackageBuilder getInstance(List<JarInputStream> classpath) {
        MapBackedClassLoader loader = createClassLoader(classpath);


        // See if we can find a packagebuilder.conf
        // We do this manually here, as we cannot rely on PackageBuilder doing this correctly
        // note this chainedProperties already checks System properties too
        ChainedProperties chainedProperties = new ChainedProperties( BRMSPackageBuilder.class.getClassLoader(), // pass this as it searches currentThread anyway
                                                                     "packagebuilder.conf",
                                                                     false ); // false means it ignores any default values

        // the default compiler. This is nominally JANINO but can be overridden by setting drools.dialect.java.compiler to ECLIPSE
        Properties properties = new Properties();
        properties.setProperty( "drools.dialect.java.compiler",
                                chainedProperties.getProperty( "drools.dialect.java.compiler", "ECLIPSE" ) );
        PackageBuilderConfiguration pkgConf = new PackageBuilderConfiguration( properties );
        pkgConf.setAllowMultipleNamespaces(false);
        pkgConf.setClassLoader( loader );

        return new BRMSPackageBuilder( pkgConf );

    }

    /**
     * For a given list of Jars, create a class loader.
     */
	public static MapBackedClassLoader createClassLoader(
			List<JarInputStream> classpath) {
		ClassLoader parentClassLoader = Thread.currentThread().getContextClassLoader();
        if ( parentClassLoader == null ) {
            parentClassLoader = BRMSPackageBuilder.class.getClassLoader();
        }



        final ClassLoader p = parentClassLoader;

        MapBackedClassLoader loader = AccessController.doPrivileged( new PrivilegedAction<MapBackedClassLoader>() {
            public MapBackedClassLoader run() {
                return new MapBackedClassLoader( p );
            }
        });


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

                        loader.addResource( entry.getName() , out.toByteArray() );
                    }
                }

            }
        } catch ( IOException e ) {
            throw new RulesRepositoryException( e );
        }
		return loader;
	}

    /**
     * This will return the preferred compiler, according to the System property
     * drools.compiler (JANINO|ECLIPSE) - default is JANINO due to classpath issues
     * mainly in tomcat, grrr...
     */
    static int getPreferredBRMSCompiler() {
        if (System.getProperty( "drools.dialect.java.compiler", "JANINO" ).equals( "ECLIPSE" )) {
            return JavaDialectConfiguration.ECLIPSE;
        } else {
            return JavaDialectConfiguration.JANINO;
        }
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

    public void setDSLFiles(List<DSLMappingFile> files) {
        this.dslFiles = files;
    }

    public List<DSLMappingFile> getDSLMappingFiles() {
        return Collections.unmodifiableList( this.dslFiles );
    }

    /**
     * Load up all the DSL mappping files for the given package.
     */
    public static List<DSLMappingFile> getDSLMappingFiles(PackageItem pkg, DSLErrorEvent err) {
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
                        err.recordError( item, "Line " + e.getLine() + " : " + e.getMessage() );
                    }
                }

            } catch ( IOException e ) {
                throw new RulesRepositoryException(e);
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
    public static interface DSLErrorEvent {
        public void recordError(AssetItem asset, String message);
    }

    /**
     * Returns true if this package uses a DSL.
     */
    public boolean hasDSL() {
        return this.dslFiles != null && this.dslFiles.size() > 0;
    }

    /**
     * Returns an expander for DSLs (only if there is a DSL configured for this package).
     */
    public DefaultExpander getDSLExpander() {
        if (this.expander == null) {
            expander = new DefaultExpander();
            for ( DSLMappingFile file : this.dslFiles ) {
                expander.addDSLMapping( file.getMapping() );
            }
        }
        return expander;
    }

}