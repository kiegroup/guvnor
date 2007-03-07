package org.drools.brms.server.rules;

import java.io.IOException;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.drools.brms.client.modeldriven.SuggestionCompletionEngine;
import org.drools.brms.server.util.SuggestionCompletionEngineBuilder;
import org.drools.repository.PackageItem;
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
    private static final Pattern imports = Pattern.compile( "import\\s*([\\.a-zA-Z0-9_]+)\\s*[;$]?", Pattern.MULTILINE );
    
    
    public SuggestionCompletionEngine getSuggestionEngine( PackageItem pkg ) {
        builder.newCompletionEngine();
        
        String header = pkg.getHeader();
        
        // get fact types from imports
        Matcher m = imports.matcher( header );
        while( m.find() ) {
            String classname = m.group( 1 );
            
            try {
                Class clazz = Class.forName( classname );
                String factType = clazz.getName().replace( clazz.getPackage().getName()+".", "" );
                
                ClassFieldInspector inspector = new ClassFieldInspector( clazz );
                String[] fields = (String[]) inspector.getFieldNames().keySet().toArray( new String[inspector.getFieldNames().size()] );
                
                builder.addFactType( factType );
                builder.addFieldsForType( factType, fields );
                for( int i = 0; i < fields.length; i++ ) {
                    // need to fix that
                    String fieldType = getFieldType( inspector,
                                                     fields[i] );
                    builder.addFieldType( factType+"."+fields[i], fieldType );
                }
            } catch ( ClassNotFoundException e ) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch ( IOException e ) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return builder.getInstance();
    }

    /**
     * @param inspector
     * @param fields
     * @param i
     * @return
     */
    private String getFieldType(ClassFieldInspector inspector,
                                String field ) {
        Class type = (Class) inspector.getFieldTypes().get( field );
        String fieldType = null; // if null, will use standard operators
        if( type.isPrimitive() && ( type != boolean.class ) ) {
            fieldType = SuggestionCompletionEngine.TYPE_NUMERIC;
        } else if( Number.class.isAssignableFrom( type ) ) {
            fieldType = SuggestionCompletionEngine.TYPE_NUMERIC;
        } else if( String.class.isAssignableFrom( type ) ) {
            fieldType = SuggestionCompletionEngine.TYPE_STRING;
        } else if( Collection.class.isAssignableFrom( type ) ) {
            fieldType = SuggestionCompletionEngine.TYPE_COLLECTION;
        } else if( Comparable.class.isAssignableFrom( type ) ) {
            fieldType = SuggestionCompletionEngine.TYPE_COMPARABLE;
        }
        return fieldType;
    }    

    
}
