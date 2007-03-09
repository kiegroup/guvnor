package org.drools.brms.server.rules;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;
import java.util.Iterator;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.drools.RuntimeDroolsException;
import org.drools.brms.client.common.AssetFormats;
import org.drools.brms.client.modeldriven.SuggestionCompletionEngine;
import org.drools.brms.server.util.SuggestionCompletionEngineBuilder;
import org.drools.compiler.DrlParser;
import org.drools.compiler.DroolsParserException;
import org.drools.lang.descr.GlobalDescr;
import org.drools.lang.descr.ImportDescr;
import org.drools.lang.descr.PackageDescr;
import org.drools.lang.dsl.DSLMapping;
import org.drools.lang.dsl.DSLMappingEntry;
import org.drools.lang.dsl.DSLMappingFile;
import org.drools.repository.AssetItem;
import org.drools.repository.AssetItemIterator;
import org.drools.repository.PackageItem;
import org.drools.repository.RulesRepositoryException;
import org.drools.resource.util.ByteArrayClassLoader;
import org.drools.util.asm.ClassFieldInspector;

/**
 * This utility class loads suggestion completion stuff for the package, 
 * introspecting from models, templates etc. 
 * 
 * This also includes DSL stuff. 
 * 
 * @author Michael Neale
 *
 */
public class SuggestionCompletionLoader {

    private SuggestionCompletionEngineBuilder builder = new SuggestionCompletionEngineBuilder();
    private DrlParser                         parser  = new DrlParser();

    public SuggestionCompletionEngine getSuggestionEngine(PackageItem pkg) {
        StringBuffer errors = new StringBuffer();
        builder.newCompletionEngine();

        String header = pkg.getHeader();

        // get fact types from imports
        PackageDescr pkgDescr;
        try {
            pkgDescr = parser.parse( header );
        } catch ( DroolsParserException e1 ) {
            throw new RuntimeDroolsException( "Error parsing header for package " + pkg.getName(),
                                              e1 );
        }

        // populating information for the model itself
        this.populateModelInfo( errors,
                                pkgDescr,
                                pkg );

        // populating globals
        this.populateGlobalInfo( errors,
                                 pkgDescr );

        // populating DSL sentences
        this.populateDSLSentences( pkg,
                                   errors );

        if ( errors.length() > 0 ) {
            throw new RuntimeDroolsException( "Error(s) while loading suggestion completion engine: \n" + errors.toString() );
        }
        return builder.getInstance();
    }

    /**
     * @param pkg
     * @param errors
     */
    private void populateDSLSentences(PackageItem pkg,
                                      StringBuffer errors) {
        AssetItemIterator it = pkg.listAssetsByFormat( new String[]{AssetFormats.DSL} );
        while ( it.hasNext() ) {
            AssetItem item = (AssetItem) it.next();
            DSLMappingFile file = new DSLMappingFile();
            try {
                if ( file.parseAndLoad( new StringReader( item.getContent() ) ) ) {
                    DSLMapping mapping = file.getMapping();
                    for ( Iterator entries = mapping.getEntries().iterator(); entries.hasNext(); ) {
                        DSLMappingEntry entry = (DSLMappingEntry) entries.next();
                        builder.addDSLSentence( entry.getMappingKey() );
                    }
                } else {
                    errors.append( file.getErrors().toString() );
                }
            } catch ( IOException e ) {
                errors.append( "\tError while loading DSL mapping " );
                errors.append( item.getBinaryContentAttachmentFileName() );
                errors.append( " : " );
                errors.append( e.getMessage() );
                errors.append( "\n" );
            }
        }
    }

    /**
     * @param errors
     * @param pkgDescr
     */
    private void populateGlobalInfo(StringBuffer errors,
                                    PackageDescr pkgDescr) {
        // populating information for the globals
        for ( Iterator it = pkgDescr.getGlobals().iterator(); it.hasNext(); ) {
            GlobalDescr global = (GlobalDescr) it.next();

            try {
                Class clazz = Class.forName( global.getType() );

                builder.addGlobalType( global.getIdentifier(),
                                       this.getFieldType( clazz ) );
            } catch ( ClassNotFoundException e ) {
                errors.append( "\tClass " );
                errors.append( global.getType() );
                errors.append( " not found for global " );
                errors.append( global.getIdentifier() );
                errors.append( "\n" );
            }
        }
    }

    /**
     * @param errors
     * @param pkgDescr
     */
    private void populateModelInfo(StringBuffer errors,
                                   PackageDescr pkgDescr,
                                   PackageItem pkg ) {

        ByteArrayClassLoader loader = new ByteArrayClassLoader( this.getClass().getClassLoader() );

        // iterating over the import list
        for ( Iterator it = pkgDescr.getImports().iterator(); it.hasNext(); ) {
            ImportDescr imp = (ImportDescr) it.next();
            String classname = imp.getTarget();

            Class clazz = loadClass( pkg,
                                     classname, 
                                     loader,
                                     errors );
            if ( clazz != null ) {
                try {
                    String factType = clazz.getName().substring( clazz.getName().lastIndexOf('.')+1 );

                    ClassFieldInspector inspector = new ClassFieldInspector( clazz );
                    String[] fields = (String[]) inspector.getFieldNames().keySet().toArray( new String[inspector.getFieldNames().size()] );

                    builder.addFactType( factType );
                    builder.addFieldsForType( factType,
                                              fields );
                    for ( int i = 0; i < fields.length; i++ ) {
                        Class type = (Class) inspector.getFieldTypes().get( fields[i] );
                        String fieldType = getFieldType( type );
                        builder.addFieldType( factType + "." + fields[i],
                                              fieldType );
                    }
                } catch ( IOException e ) {
                    errors.append( "\tError while inspecting class: " );
                    errors.append( classname );
                    errors.append( " : " );
                    errors.append( e.getMessage() );
                    errors.append( "\n" );
                }
            }
        }
    }

    /**
     * @param pkg
     * @param classname
     * @param clazz
     * @return
     */
    private Class loadClass(PackageItem pkg,
                            String classname,
                            ByteArrayClassLoader loader,
                            StringBuffer errors ) {
        Class clazz = null;
        try {
            // check if it is already in the classpath
            clazz = loader.loadClass( classname );
            
        } catch ( ClassNotFoundException e1 ) {
            
            // not found in the classpath, so check if it
            // is in a package model
            try {
                // try to load the package model
                PackageItem importedPkg = null;
                String pkgName = classname;
                do {
                    pkgName = classname.substring( 0,
                                                   Math.max( classname.lastIndexOf( '.' ),
                                                             0 ) );
                    importedPkg = pkg.getRulesRepository().loadPackage( pkgName );
                } while ( importedPkg == null && pkgName.length() > 0 );

                if ( importedPkg != null ) {
                    // a package was found, so, try it out
                    AssetItemIterator ait = importedPkg.listAssetsByFormat( new String[]{AssetFormats.MODEL} );
                    while ( ait.hasNext() ) {
                        AssetItem item = (AssetItem) ait.next();
                        JarInputStream jis = new JarInputStream( item.getBinaryContentAttachment(),
                                                                 false );
                        JarEntry entry = null;
                        byte[] buf = new byte[1024];
                        int len = 0;
                        while ( (entry = jis.getNextJarEntry()) != null ) {
                            if ( !entry.isDirectory() ) {
                                ByteArrayOutputStream out = new ByteArrayOutputStream();
                                while( (len = jis.read( buf ) ) >=0 ) {
                                    out.write( buf, 0, len );
                                }
                                loader.addResource( entry.getName(),
                                                    out.toByteArray() );
                            }
                        }
                    }
                }
                clazz = loader.loadClass( classname );
            } catch ( RulesRepositoryException e ) {
                // TODO: mic_hat, what do we do here?
            } catch ( IOException e ) {
                // TODO: mic_hat, what do we do here?
            } catch ( ClassNotFoundException e ) {
                errors.append( "\tImported class not found: " );
                errors.append( classname );
                errors.append( "\n" );
            }
        }
        return clazz;
    }

    /**
     * @param inspector
     * @param fields
     * @param i
     * @return
     */
    private String getFieldType(Class type) {
        String fieldType = null; // if null, will use standard operators
        if ( type.isPrimitive() && (type != boolean.class) ) {
            fieldType = SuggestionCompletionEngine.TYPE_NUMERIC;
        } else if ( Number.class.isAssignableFrom( type ) ) {
            fieldType = SuggestionCompletionEngine.TYPE_NUMERIC;
        } else if ( String.class.isAssignableFrom( type ) ) {
            fieldType = SuggestionCompletionEngine.TYPE_STRING;
        } else if ( Collection.class.isAssignableFrom( type ) ) {
            fieldType = SuggestionCompletionEngine.TYPE_COLLECTION;
        } else if ( Comparable.class.isAssignableFrom( type ) ) {
            fieldType = SuggestionCompletionEngine.TYPE_COMPARABLE;
        }
        return fieldType;
    }

}
