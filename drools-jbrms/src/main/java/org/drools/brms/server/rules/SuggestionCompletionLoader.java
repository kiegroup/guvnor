package org.drools.brms.server.rules;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.codehaus.jfdi.interpreter.ClassTypeResolver;
import org.drools.RuntimeDroolsException;
import org.drools.brms.client.common.AssetFormats;
import org.drools.brms.client.modeldriven.SuggestionCompletionEngine;
import org.drools.brms.server.util.SuggestionCompletionEngineBuilder;
import org.drools.compiler.DrlParser;
import org.drools.compiler.DroolsParserException;
import org.drools.lang.descr.FactTemplateDescr;
import org.drools.lang.descr.FieldTemplateDescr;
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
    private final ByteArrayClassLoader        loader;

    public SuggestionCompletionLoader() {
        loader = new ByteArrayClassLoader( this.getClass().getClassLoader() );
    }

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
                                 pkgDescr,
                                 pkg );

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
                        if (entry.getSection() == DSLMappingEntry.CONDITION) {
                            builder.addDSLConditionSentence( entry.getMappingKey() );
                        } else if (entry.getSection() == DSLMappingEntry.CONSEQUENCE) {
                            builder.addDSLActionSentence( entry.getMappingKey() );
                        }
                        
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
     * Populate the global stuff.
     */
    private void populateGlobalInfo(StringBuffer errors,
                                    PackageDescr pkgDescr,
                                    PackageItem pkg) {

        // populating information for the globals
        for ( Iterator it = pkgDescr.getGlobals().iterator(); it.hasNext(); ) {
            GlobalDescr global = (GlobalDescr) it.next();
            try {
                String shortTypeName = global.getType();
                if ( !this.builder.hasFieldsForType( shortTypeName ) ) {
                    Class clazz = loadClass( pkg,
                                             global.getType(),
                                             errors );
                    loadClassFields( clazz,
                                     shortTypeName );

                    this.builder.addGlobalType( global.getIdentifier(),
                                                shortTypeName );
                }

                builder.addGlobalType( global.getIdentifier(),
                                       shortTypeName );
            } catch ( IOException e ) {
                errors.append( "\tError while inspecting class: " );
                errors.append( global.getType() );
                errors.append( " : " );
                errors.append( e.getMessage() );
                errors.append( "\n" );
            }

        }
    }

    /**
     * Populate the fact type data.
     */
    private void populateModelInfo(StringBuffer errors,
                                   PackageDescr pkgDescr,
                                   PackageItem pkg) {

        // iterating over the import list
        ClassTypeResolver resolver = new ClassTypeResolver();
        for ( Iterator it = pkgDescr.getImports().iterator(); it.hasNext(); ) {
            ImportDescr imp = (ImportDescr) it.next();
            String classname = imp.getTarget();
            resolver.addImport( classname );

            Class clazz = loadClass( pkg,
                                     classname,
                                     errors );
            if ( clazz != null ) {
                try {
                    String shortTypeName = getShortNameOfClass( clazz.getName() );
                    loadClassFields( clazz,
                                     shortTypeName );
                    builder.addFactType( shortTypeName );
                } catch ( IOException e ) {
                    errors.append( "\tError while inspecting class: " );
                    errors.append( classname );
                    errors.append( " : " );
                    errors.append( e.getMessage() );
                    errors.append( "\n" );
                }
            }
        }

        // iterating over templates
        populateFactTemplateTypes( pkgDescr,
                                   pkg,
                                   resolver,
                                   errors );
    }

    /**
     * Iterates over fact templates and add them to the model definition
     * 
     * @param pkgDescr
     */
    private void populateFactTemplateTypes(PackageDescr pkgDescr,
                                           PackageItem pkg,
                                           ClassTypeResolver resolver,
                                           StringBuffer errors) {
        for ( Iterator it = pkgDescr.getFactTemplates().iterator(); it.hasNext(); ) {
            FactTemplateDescr templ = (FactTemplateDescr) it.next();
            String factType = templ.getName();
            builder.addFactType( factType );

            String[] fields = new String[templ.getFields().size()];
            builder.addFieldsForType( factType,
                                      fields );

            int index = 0;
            for ( Iterator fieldsIt = templ.getFields().iterator(); fieldsIt.hasNext(); ) {
                FieldTemplateDescr fieldDescr = (FieldTemplateDescr) fieldsIt.next();
                fields[index++] = fieldDescr.getName();
                String fieldType = fieldDescr.getClassType();

                Class fieldTypeClass = null;
                try {
                    fieldTypeClass = resolver.resolveType( fieldType );
                } catch ( ClassNotFoundException e ) {
                    errors.append( "\tClass not found: " );
                    errors.append( fieldType );
                    errors.append( "\n" );
                }
                builder.addFieldType( factType + "." + fieldDescr.getName(),
                                      getFieldType( fieldTypeClass ) );
            }
        }
    }

    private void loadClassFields(Class clazz,
                                 String shortTypeName) throws IOException {
        ClassFieldInspector inspector = new ClassFieldInspector( clazz );
        String[] fields = (String[]) inspector.getFieldNames().keySet().toArray( new String[inspector.getFieldNames().size()] );

        fields = removeIrrelevantFields( fields );

        builder.addFieldsForType( shortTypeName,
                                  fields );
        for ( int i = 0; i < fields.length; i++ ) {
            Class type = (Class) inspector.getFieldTypes().get( fields[i] );
            String fieldType = getFieldType( type );
            builder.addFieldType( shortTypeName + "." + fields[i],
                                  fieldType );
        }
    }

    String getShortNameOfClass(String clazz) {
        return clazz.substring( clazz.lastIndexOf( '.' ) + 1 );
    }

    /**
     * This will remove the unneeded "fields" that come from java.lang.Object
     * these are really not needed for the modeller.
     */
    String[] removeIrrelevantFields(String[] fields) {
        List result = new ArrayList();
        for ( int i = 0; i < fields.length; i++ ) {
            String field = fields[i];
            if ( field.equals( "class" ) || field.equals( "hashCode" ) || field.equals( "toString" ) ) {
                //ignore
            } else {
                result.add( field );
            }
        }
        return (String[]) result.toArray( new String[result.size()] );
    }

    /**
     * @param pkg
     * @param classname
     * @param clazz
     * @return
     */
    private Class loadClass(PackageItem pkg,
                            String classname,
                            StringBuffer errors) {
        Class clazz = null;
        try {
            // check if it is already in the classpath
            clazz = loader.loadClass( classname );

        } catch ( ClassNotFoundException e1 ) {

            // not found in the classpath, so check if it
            // is in a package model
            try {

                AssetItemIterator ait = pkg.listAssetsByFormat( new String[]{AssetFormats.MODEL} );
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
                            while ( (len = jis.read( buf )) >= 0 ) {
                                out.write( buf,
                                           0,
                                           len );
                            }
                            loader.addResource( entry.getName(),
                                                out.toByteArray() );
                        }
                    }

                }
                clazz = loader.loadClass( classname );
            } catch ( IOException e ) {
                throw new RulesRepositoryException( e );
            } catch ( ClassNotFoundException e ) {
                errors.append( "\tClass not found: " );
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
        if ( type != null ) {
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
        }
        return fieldType;
    }

}
