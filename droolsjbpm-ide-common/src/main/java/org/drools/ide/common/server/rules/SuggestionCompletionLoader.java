/*
 * Copyright 2010 JBoss Inc
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

package org.drools.ide.common.server.rules;

import java.beans.Introspector;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.drools.base.ClassTypeResolver;
import org.drools.compiler.DrlParser;
import org.drools.compiler.DroolsError;
import org.drools.compiler.DroolsParserException;
import org.drools.core.util.asm.ClassFieldInspector;
import org.drools.ide.common.client.modeldriven.FieldAccessorsAndMutators;
import org.drools.ide.common.client.modeldriven.MethodInfo;
import org.drools.ide.common.client.modeldriven.ModelField.FIELD_CLASS_TYPE;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.server.util.ClassMethodInspector;
import org.drools.ide.common.server.util.DataEnumLoader;
import org.drools.ide.common.server.util.SuggestionCompletionEngineBuilder;
import org.drools.lang.descr.AnnotationDescr;
import org.drools.lang.descr.GlobalDescr;
import org.drools.lang.descr.ImportDescr;
import org.drools.lang.descr.PackageDescr;
import org.drools.lang.descr.PatternDescr;
import org.drools.lang.descr.TypeDeclarationDescr;
import org.drools.lang.descr.TypeFieldDescr;
import org.drools.lang.dsl.DSLMappingEntry;
import org.drools.lang.dsl.DSLMappingFile;
import org.drools.lang.dsl.DSLTokenizedMappingFile;
import org.drools.rule.MapBackedClassLoader;

/**
 * This utility class loads suggestion completion stuff for the package
 * configuration, introspecting from models, templates etc.
 * <p/>
 * This also includes DSL stuff, basically, everything you need to get started
 * with a package. It also validates the package configuration, and can provide
 * errors.
 * <p/>
 * This does NOT validate assets in the package, other then to load up DSLs,
 * models etc as needed.
 * <p/>
 * FYI: the tests for this are in the BRMS module, in context of a full BRMS.
 */
public class SuggestionCompletionLoader
        implements
        ClassToGenericClassConverter {

    private final SuggestionCompletionEngineBuilder builder                      = new SuggestionCompletionEngineBuilder();

    private final MapBackedClassLoader              loader;

    private final List<String>                      errors                       = new ArrayList<String>();

    // iterating over the import list
    private final ClassTypeResolver                 resolver;
    private PackageDescr                            pkgDescr;

    /**
     * List of external ImportDescr providers.
     */
    private List<ExternalImportDescrProvider>       externalImportDescrProviders = new ArrayList<ExternalImportDescrProvider>();

    /**
     * Interface used for add external ImportDescr added to
     * SuggestionCompletionEngine Use this to add Fact Types that are not
     * imported by the package.
     */
    public static interface ExternalImportDescrProvider {
        public Set<ImportDescr> getImportDescrs();
    }

    /**
     * This uses the current classes classloader as a base, and jars can be
     * added.
     */
    public SuggestionCompletionLoader() {
        this( null );
    }

    /**
     * This allows a pre existing classloader to be used (and preferred) for
     * resolving types.
     */
    public SuggestionCompletionLoader(ClassLoader classLoader) {
        loader = getMapBackedClassLoader( classLoader );
        resolver = new ClassTypeResolver( new HashSet<String>(),
                                          loader );
    }

    private MapBackedClassLoader getMapBackedClassLoader(ClassLoader classLoader) {
        MapBackedClassLoader mapBackedClassLoader = new MapBackedClassLoader( createClassLoader( classLoader ) );

        return mapBackedClassLoader;
    }

    private ClassLoader createClassLoader(ClassLoader classLoader) {
        if ( classLoader == null ) {
            classLoader = Thread.currentThread().getContextClassLoader();
            if ( classLoader == null ) {
                classLoader = this.getClass().getClassLoader();
            }
        }
        return classLoader;
    }

    /**
     * This will validate, and generate a new engine, ready to go. If there are
     * errors, you can get them by doing getMissingClasses();
     * 
     * @param header
     *            The package configuration file content.
     * @param jars
     *            a list of jars to look inside (pass in empty array if not
     *            needed) this is a list of {@link JarInputStream}
     * @param dsls
     *            any dsl files. This is a list of {@link DSLMappingFile}.
     * @return A SuggestionCompletionEngine ready to be used in anger.
     */
    public SuggestionCompletionEngine getSuggestionEngine(final String header,
                                                          final List<JarInputStream> jars,
                                                          final List<DSLTokenizedMappingFile> dsls) {
        return this.getSuggestionEngine( header,
                                         jars,
                                         dsls,
                                         Collections.<String> emptyList() );
    }

    /**
     * This will validate, and generate a new engine, ready to go. If there are
     * errors, you can get them by doing getMissingClasses();
     * 
     * @param header
     *            The package configuration file content.
     * @param jars
     *            a list of jars to look inside (pass in empty array if not
     *            needed) this is a list of {@link JarInputStream}
     * @param dsls
     *            any dsl files. This is a list of {@link DSLMappingFile}.
     * @param dataEnums
     *            this is a list of String's which hold data enum definitions.
     *            (normally will be just one, but for completeness can load
     *            multiple).
     * @return A SuggestionCompletionEngine ready to be used in anger.
     */
    public SuggestionCompletionEngine getSuggestionEngine(final String header,
                                                          final List<JarInputStream> jars,
                                                          final List<DSLTokenizedMappingFile> dsls,
                                                          final List<String> dataEnums) {
        builder.newCompletionEngine();

        if ( headerNotEmpty( header ) ) {
            processPackageHeader( header,
                                  jars );
        }

        populateDSLSentences( dsls );

        populateDateEnums( dataEnums );

        return builder.getInstance();
    }

    private void populateDateEnums(List<String> dataEnums) {
        for ( String enumFile : dataEnums ) {
            DataEnumLoader enumLoader = new DataEnumLoader( enumFile );
            if ( enumLoader.hasErrors() ) {
                errors.addAll( enumLoader.getErrors() );
            } else {
                builder.addAllDataEnumsList( enumLoader.getData() );
            }
        }
    }

    private boolean headerNotEmpty(String header) {
        return !header.trim().equals( "" );
    }

    private void processPackageHeader(final String header,
                                      final List jars) {
        DrlParser parser = getParser( header );

        logErrors( parser );

        populateEngineBuilder( jars );
    }

    private void populateEngineBuilder(List jars) {
        if ( thereWasNoErrorsAndPackageDescrWasCreated() ) {

            populateModelInfo( jars );

            populateDeclaredFactTypes( jars );

            populateGlobalInfo( jars );

        }
    }

    private void logErrors(DrlParser parser) {
        if ( parser.hasErrors() ) {
            for ( DroolsError droolsError : parser.getErrors() ) {
                this.errors.add( droolsError.getMessage() );
            }
        }
    }

    private DrlParser getParser(String header) {
        DrlParser parser = new DrlParser();
        try {
            pkgDescr = parser.parse( header );
        } catch ( final DroolsParserException e1 ) {
            throw new IllegalStateException( "Serious error, unable to validate package." );
        }
        return parser;
    }

    private boolean thereWasNoErrorsAndPackageDescrWasCreated() {
        return pkgDescr != null;
    }

    private void populateDSLSentences(final List<DSLTokenizedMappingFile> dsls) {

        for ( DSLTokenizedMappingFile file : dsls ) {
            for ( DSLMappingEntry entry : file.getMapping().getEntries() ) {
                if ( entry.getSection() == DSLMappingEntry.CONDITION ) {
                    builder.addDSLConditionSentence( entry.getMappingKey() );
                } else if ( entry.getSection() == DSLMappingEntry.CONSEQUENCE ) {
                    builder.addDSLActionSentence( entry.getMappingKey() );
                } else if ( entry.getSection() == DSLMappingEntry.KEYWORD ) {
                    builder.addDSLMapping( entry );
                } else if ( entry.getSection() == DSLMappingEntry.ANY ) {
                    builder.addDSLConditionSentence( entry.getMappingKey() );
                    builder.addDSLActionSentence( entry.getMappingKey() );
                }
            }
        }

    }

    private void populateGlobalInfo(final List jars) {

        // populating information for the globals
        for ( final Iterator it = pkgDescr.getGlobals().iterator(); it.hasNext(); ) {
            final GlobalDescr global = (GlobalDescr) it.next();
            try {
                final String shortTypeName = getShortNameOfClass( global.getType() );
                final Class< ? > clazz = loadClass( global.getType(),
                                                    jars );
                if ( !this.builder.hasFieldsForType( shortTypeName ) ) {

                    loadClassFields( clazz,
                                     shortTypeName );

                    this.builder.addGlobalType( global.getIdentifier(),
                                                shortTypeName );

                }
                if ( implementsCollection( clazz ) ) {
                    this.builder.addGlobalCollection( global.getIdentifier() );
                }
                this.builder.addGlobalType( global.getIdentifier(),
                                            shortTypeName );
            } catch ( final IOException e ) {
                this.errors.add( "Error while inspecting class for global: " + global.getType() + " error message: " + e.getMessage() );
            }

        }
    }

    private boolean implementsCollection(Class< ? > clazz) {
        return clazz != null && Collection.class.isAssignableFrom( clazz );
    }

    /**
     * Populate the fact type data.
     */
    private void populateModelInfo(final List< ? > jars) {
        List<ImportDescr> imports = new ArrayList<ImportDescr>( pkgDescr.getImports() );

        addAnyExternalImports( imports );

        for ( ImportDescr importDescr : imports ) {
            String className = importDescr.getTarget();
            try {
                addImport( className );
                addFactType( jars,
                             className );
            } catch ( WildCardException e ) {
                this.errors.add( String.format( "Unable to introspect model for wild card imports (%s). Please explicitly import each fact type you require.",
                                                className ) );
            }
        }
    }

    private void populateDeclaredFactTypes(List< ? > jars) {
        for ( TypeDeclarationDescr baseType : pkgDescr.getTypeDeclarations() ) {
            List<TypeDeclarationDescr> th = getDeclaredTypeHierachy( baseType,
                                                                     jars );
            populateDeclaredFactType( th );
        }
    }

    private List<TypeDeclarationDescr> getDeclaredTypeHierachy(TypeDeclarationDescr td,
                                                               List< ? > jars) {
        List<TypeDeclarationDescr> th = new ArrayList<TypeDeclarationDescr>();
        th.add( td );
        TypeDeclarationDescr std;
        while ( (std = getDeclaredSuperType( td )) != null ) {
            th.add( std );
            td = std;
        }

        //If the super-most class has been imported attempt to make a pseudo TypeDeclaration for the imported class
        if ( this.pkgDescr.getImports().size() > 0 ) {
            for ( ImportDescr imp : this.pkgDescr.getImports() ) {
                if ( imp.getTarget().endsWith( "." + td.getTypeName() ) ) {
                    TypeDeclarationDescr pseudoTypeDeclr = makePseudoTypeDeclarationDescrFromSuperClassType( imp.getTarget(),
                                                                                                             jars );
                    if ( pseudoTypeDeclr != null ) {
                        th.add( pseudoTypeDeclr );
                    }
                }
            }

        }
        return th;
    }

    private TypeDeclarationDescr getDeclaredSuperType(TypeDeclarationDescr td) {
        String declaredSuperTypeName = td.getSuperTypeName();
        if ( declaredSuperTypeName == null ) {
            return null;
        } else {
            for ( TypeDeclarationDescr std : pkgDescr.getTypeDeclarations() ) {
                if ( declaredSuperTypeName.equals( std.getTypeName() ) ) {
                    return std;
                }
            }
        }
        return null;
    }

    private TypeDeclarationDescr makePseudoTypeDeclarationDescrFromSuperClassType(String className,
                                                                                  List< ? > jars) {

        Class< ? > clazz = loadClass( className,
                                      jars );

        if ( clazz != null ) {

            Method[] methods = clazz.getMethods();
            Map<String, MethodSignature> methodSignatures = getMethodSignatures( className,
                                                                                 methods );

            TypeDeclarationDescr td = new TypeDeclarationDescr();
            td.setTypeName( className );

            for ( Map.Entry<String, MethodSignature> e : methodSignatures.entrySet() ) {
                if ( e.getValue().accessorAndMutator == FieldAccessorsAndMutators.BOTH ) {
                    String fieldShortName = getShortNameOfClass( e.getKey() );
                    TypeFieldDescr fieldDescr = new TypeFieldDescr( fieldShortName );
                    PatternDescr patternDescr = new PatternDescr( e.getValue().returnType.getName() );
                    fieldDescr.setPattern( patternDescr );
                    td.addField( fieldDescr );
                }
            }
            return td;

        }

        return null;
    }

    private void populateDeclaredFactType(List<TypeDeclarationDescr> th) {

        String declaredType = th.get( 0 ).getTypeName();
        Set<String> declaredTypes = getDeclaredTypes( pkgDescr );
        Map<String, FieldAccessorsAndMutators> accessorsAndMutators = new HashMap<String, FieldAccessorsAndMutators>();
        Map<String, Map<String, String>> annotations = new HashMap<String, Map<String, String>>();
        List<String> fieldNames = new ArrayList<String>();

        //'this' is a special case
        fieldNames.add( SuggestionCompletionEngine.TYPE_THIS );
        this.builder.addFieldType( declaredType + "." + SuggestionCompletionEngine.TYPE_THIS,
                                   declaredType,
                                   null );
        accessorsAndMutators.put( declaredType + "." + SuggestionCompletionEngine.TYPE_THIS,
                                  FieldAccessorsAndMutators.ACCESSOR );

        //Other facts and fields in the type hierarchy
        for ( TypeDeclarationDescr typeDeclarationDescr : th ) {

            //Configure annotations
            for ( String annotationName : typeDeclarationDescr.getAnnotationNames() ) {
                AnnotationDescr annotation = typeDeclarationDescr.getAnnotation( annotationName );
                annotations.put( annotationName,
                                 annotation.getValues() );
            }

            //Configure fields
            if ( typeDeclarationDescrHasFields( typeDeclarationDescr ) ) {

                this.builder.addFactType( declaredType,
                                          FIELD_CLASS_TYPE.TYPE_DECLARATION_CLASS );

                //Other fields
                for ( Map.Entry<String, TypeFieldDescr> f : typeDeclarationDescr.getFields().entrySet() ) {
                    String fieldName = f.getKey();
                    fieldNames.add( fieldName );

                    String factField = declaredType + "." + fieldName;
                    accessorsAndMutators.put( factField,
                                              FieldAccessorsAndMutators.BOTH );
                    String fieldClass = f.getValue().getPattern().getObjectType();

                    if ( declaredTypes.contains( fieldClass ) ) {
                        this.builder.addFieldType( declaredType + "." + fieldName,
                                                   fieldClass,
                                                   null );//SuggestionCompletionEngine.TYPE_OBJECT );
                    } else {
                        try {
                            Class< ? > clz = resolver.resolveType( fieldClass );
                            this.builder.addFieldType( declaredType + "." + fieldName,
                                                       translateClassToGenericType( clz ),
                                                       clz );
                        } catch ( ClassNotFoundException e ) {
                            this.errors.add( "Class of field not found: " + fieldClass );
                        }
                    }
                }
            }
        }

        this.builder.addAnnotationsForType( declaredType,
                                            annotations );
        this.builder.addFieldsForType( declaredType,
                                       fieldNames.toArray( new String[fieldNames.size()] ) );
        this.builder.addFieldAccessorsAndMutatorsForField( accessorsAndMutators );

    }

    private Map<String, MethodSignature> getMethodSignatures(String className,
                                                             Method[] methods) {

        Map<String, MethodSignature> methodSignatures = new HashMap<String, MethodSignature>();

        //Determine accessors for methods
        for ( Method method : methods ) {
            boolean addMethod = false;
            String name = method.getName();
            if ( method.getParameterTypes().length > 0 ) {

                //Strip bare mutator name
                if ( name.startsWith( "set" ) ) {
                    addMethod = true;
                    name = Introspector.decapitalize( name.substring( 3 ) );
                }

                if ( addMethod ) {
                    String factField = className + "." + name;
                    if ( !methodSignatures.containsKey( factField ) ) {
                        methodSignatures.put( factField,
                                              new MethodSignature( FieldAccessorsAndMutators.MUTATOR,
                                                                   void.class.getGenericSuperclass(),
                                                                   void.class ) );
                    } else if ( methodSignatures.get( factField ).accessorAndMutator == FieldAccessorsAndMutators.ACCESSOR ) {
                        MethodSignature signature = methodSignatures.get( factField );
                        signature.accessorAndMutator = FieldAccessorsAndMutators.BOTH;
                    }
                }

            } else if ( !method.getReturnType().equals( "void" ) ) {

                //Strip bare accessor name
                if ( name.startsWith( "get" ) ) {
                    addMethod = true;
                    name = Introspector.decapitalize( name.substring( 3 ) );
                } else if ( name.startsWith( "is" ) ) {
                    addMethod = true;
                    name = Introspector.decapitalize( name.substring( 2 ) );
                }

                if ( addMethod ) {
                    String factField = className + "." + name;
                    if ( !methodSignatures.containsKey( factField ) ) {
                        methodSignatures.put( factField,
                                              new MethodSignature( FieldAccessorsAndMutators.ACCESSOR,
                                                                   method.getGenericReturnType(),
                                                                   method.getReturnType() ) );
                    } else if ( methodSignatures.get( factField ).accessorAndMutator == FieldAccessorsAndMutators.MUTATOR ) {
                        MethodSignature signature = methodSignatures.get( factField );
                        signature.accessorAndMutator = FieldAccessorsAndMutators.BOTH;
                    }
                }
            }
        }
        return methodSignatures;
    }

    private Map<String, FieldAccessorsAndMutators> extractFieldAccessorsAndMutators(Map<String, MethodSignature> methodSignatures) {
        Map<String, FieldAccessorsAndMutators> accessorsAndMutators = new HashMap<String, FieldAccessorsAndMutators>();
        for ( Map.Entry<String, MethodSignature> e : methodSignatures.entrySet() ) {
            accessorsAndMutators.put( e.getKey(),
                                      e.getValue().accessorAndMutator );
        }
        return accessorsAndMutators;
    }

    private static class MethodSignature {

        MethodSignature(FieldAccessorsAndMutators accessorAndMutator,
                        Type genericType,
                        Class< ? > returnType) {
            this.accessorAndMutator = accessorAndMutator;
            this.genericType = genericType;
            this.returnType = returnType;
        }

        FieldAccessorsAndMutators accessorAndMutator;
        Type                      genericType;
        Class< ? >                returnType;

    }

    /**
     * This represents a generalisation of java.lang.reflect.Field that is also
     * used for Method return types. It contains enough information for use by a
     * SuggestionCompletionLoader but should not be considered a general
     * replacement for Field
     */
    public static class FieldInfo {

        private Type       genericType;
        private Class< ? > type;

        public FieldInfo(Type genericType,
                         Class< ? > type) {
            this.genericType = genericType;
            this.type = type;
        }

        public Type getGenericType() {
            return genericType;
        }

        public Class< ? > getType() {
            return type;
        }
    }

    private boolean typeDeclarationDescrHasFields(TypeDeclarationDescr typeDeclarationDescr) {
        return typeDeclarationDescr.getFields().size() > 0;
    }

    private Set<String> getDeclaredTypes(PackageDescr pkgDescr) {
        Set<String> declaredTypes = new HashSet<String>();

        for ( TypeDeclarationDescr typeDeclarationDescr : pkgDescr.getTypeDeclarations() ) {
            declaredTypes.add( typeDeclarationDescr.getTypeName() );
        }
        return declaredTypes;
    }

    private void addImport(String className) throws WildCardException {
        if ( isWildCardImport( className ) ) {
            throw new WildCardException();
        } else {
            resolver.addImport( className );
        }
    }

    private void addFactType(List jars,
                             String className) {
        final Class clazz = loadClass( className,
                                       jars );
        if ( clazz != null ) {
            try {

                final String shortTypeName = getShortNameOfClass( clazz.getName() );
                this.builder.addFactType( shortTypeName,
                                          FIELD_CLASS_TYPE.REGULAR_CLASS );
                loadClassFields( clazz,
                                 shortTypeName );

            } catch ( final IOException e ) {
                this.errors.add( String.format( "Error while inspecting the class: %s. The error was: %s",
                                                className,
                                                e.getMessage() ) );
            } catch ( NoClassDefFoundError e ) {
                this.errors.add( String.format( "Unable to find the class: %s which is required by: %s. You may need to add more classes to the model.",
                                                e.getMessage().replace( '/',
                                                                        '.' ),
                                                className ) );
            }

        }
    }

    private boolean isWildCardImport(String className) {
        return className.endsWith( "*" );
    }

    private void addAnyExternalImports(List<ImportDescr> imports) {
        if ( this.externalImportDescrProviders != null ) {
            for ( ExternalImportDescrProvider externalImportDescrProvider : this.externalImportDescrProviders ) {
                imports.addAll( externalImportDescrProvider.getImportDescrs() );
            }
        }
    }

    private Class loadClass(String className,
                            List jars) {
        Class clazz = null;
        try {
            clazz = resolver.resolveType( className );
        } catch ( ClassFormatError e1 ) {
            clazz = loadClass( className,
                               jars,
                               clazz );
        } catch ( ClassNotFoundException e1 ) {
            clazz = loadClass( className,
                               jars,
                               clazz );
        }
        return clazz;
    }

    private Class loadClass(String className,
                            List jars,
                            Class clazz) {
        try {
            addJars( jars );
            clazz = resolver.resolveType( className );
        } catch ( Exception e ) {
            this.errors.add( "Class not found: " + className );
        }
        return clazz;
    }

    private void loadClassFields(final Class< ? > clazz,
                                 final String shortTypeName) throws IOException {
        if ( clazz == null ) {
            return;
        }

        //Get all getters and setters for the class. This does not handle delegated properties
        final ClassFieldInspector inspector = new ClassFieldInspector( clazz );
        Set<String> fieldsSet = new TreeSet<String>( inspector.getFieldNames().keySet() );
        List<String> fields = removeIrrelevantFields( fieldsSet );

        //Consolidate methods into those with getters or setters
        Method[] methods = clazz.getMethods();
        Map<String, MethodSignature> methodSignatures = removeIrrelevantMethods( getMethodSignatures( shortTypeName,
                                                                                                      methods ) );

        //Add Fields from ClassFieldInspector which provides a list of "reasonable" methods
        for ( String field : fields ) {
            Field f = inspector.getFieldTypesField().get( field );
            if ( f == null ) {

                //If a Field cannot be found is is really a delegated property so use the Method return type
                final String qualifiedName = shortTypeName + "." + field;
                if ( methodSignatures.containsKey( qualifiedName ) ) {
                    final MethodSignature m = methodSignatures.get( qualifiedName );
                    final Class< ? > returnType = m.returnType;
                    final String genericType = translateClassToGenericType( returnType );
                    this.builder.addFieldType( qualifiedName,
                                               genericType,
                                               returnType );
                    final FieldInfo fi = new FieldInfo( m.genericType,
                                                        m.returnType );
                    this.builder.addFieldTypeField( qualifiedName,
                                                    fi );
                }
            } else {

                //Otherwise we can use the results of ClassFieldInspector
                final Class< ? > returnType = inspector.getFieldTypes().get( field );
                final String genericType = translateClassToGenericType( returnType );
                this.builder.addFieldType( shortTypeName + "." + field,
                                           genericType,
                                           returnType );
                final FieldInfo fi = new FieldInfo( f.getGenericType(),
                                                    f.getType() );
                this.builder.addFieldTypeField( shortTypeName + "." + field,
                                                fi );
            }
        }

        //'this' is a special case
        fields.add( 0,
                    SuggestionCompletionEngine.TYPE_THIS );
        methodSignatures.put( shortTypeName + "." + SuggestionCompletionEngine.TYPE_THIS,
                              new MethodSignature( FieldAccessorsAndMutators.ACCESSOR,
                                                   clazz.getGenericSuperclass(),
                                                   clazz ) );
        this.builder.addFieldType( shortTypeName + "." + SuggestionCompletionEngine.TYPE_THIS,
                                   shortTypeName,
                                   clazz );

        this.builder.addFieldAccessorsAndMutatorsForField( extractFieldAccessorsAndMutators( methodSignatures ) );

        this.builder.addFieldsForType( shortTypeName,
                                       fields.toArray( new String[fields.size()] ) );

        //Methods for use in ActionCallMethod's
        ClassMethodInspector methodInspector = new ClassMethodInspector( clazz,
                                                                         this );

        List<MethodInfo> methodInfos = methodInspector.getMethodInfos();
        for ( MethodInfo mi : methodInfos ) {
            String genericType = mi.getParametricReturnType();
            if ( genericType != null ) {
                this.builder.putParametricFieldType( shortTypeName + "." + mi.getNameWithParameters(),
                                                     genericType );
            }
        }
        this.builder.getInstance().addMethodInfo( shortTypeName,
                                                  methodInfos );
    }

    public String getShortNameOfClass(final String clazz) {
        return clazz.substring( clazz.lastIndexOf( '.' ) + 1 );
    }

    /**
     * This will remove the unneeded "fields" that come from java.lang.Object
     * these are really not needed for the modeller.
     */
    public List<String> removeIrrelevantFields(Collection<String> fields) {
        final List<String> result = new ArrayList<String>();
        for ( String field : fields ) {
            //clone, empty, iterator, listIterator, size, toArray
            if ( !(field.equals( "class" ) || field.equals( "hashCode" ) || field.equals( "toString" )) ) {
                result.add( field );
            }
        }
        return result;
    }

    /**
     * This will remove the unneeded "methods" that come from java.lang.Object
     */
    public Map<String, MethodSignature> removeIrrelevantMethods(Map<String, MethodSignature> methods) {
        final Map<String, MethodSignature> result = new HashMap<String, MethodSignature>();
        for ( Map.Entry<String, MethodSignature> methodSignature : methods.entrySet() ) {
            String methodName = methodSignature.getKey();
            methodName = methodName.substring( methodName.lastIndexOf( "." ) + 1 );
            if ( !methodName.equals( "class" ) ) {
                result.put( methodSignature.getKey(),
                                methodSignature.getValue() );
            }
        }
        return result;
    }

    /**
     * This will add the given jars to the classloader.
     */
    private void addJars(final List<JarInputStream> jars) throws IOException {
        for ( final Iterator<JarInputStream> it = jars.iterator(); it.hasNext(); ) {
            final JarInputStream jis = it.next();
            JarEntry entry;
            final byte[] buf = new byte[1024];
            int len;
            while ( (entry = jis.getNextJarEntry()) != null ) {
                if ( !entry.isDirectory() && entry.getName().endsWith( ".class" ) ) {
                    final ByteArrayOutputStream out = new ByteArrayOutputStream();
                    while ( (len = jis.read( buf )) >= 0 ) {
                        out.write( buf,
                                   0,
                                   len );
                    }
                    this.loader.addResource( entry.getName(),
                                             out.toByteArray() );
                }
            }

        }
    }

    /*
     * (non-Javadoc)
     * @see org.drools.ide.common.server.rules.ClassToGenericClassConverter#
     * translateClassToGenericType(java.lang.Class)
     */
    //XXX {bauna} field type
    public String translateClassToGenericType(final Class< ? > type) {
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
            } else if ( Boolean.class.isAssignableFrom( type ) || boolean.class.isAssignableFrom( type ) ) {
                fieldType = SuggestionCompletionEngine.TYPE_BOOLEAN;
            } else if ( Date.class.isAssignableFrom( type ) ) {
                fieldType = SuggestionCompletionEngine.TYPE_DATE; // MN: wait until we support it.
            } else if ( Comparable.class.isAssignableFrom( type ) ) {
                fieldType = SuggestionCompletionEngine.TYPE_COMPARABLE;
            } else {
                try {
                    Class clazz = resolver.resolveType( type.getName() );
                    fieldType = clazz.getSimpleName();
                } catch ( ClassNotFoundException e ) {
                    fieldType = SuggestionCompletionEngine.TYPE_OBJECT;
                }
            }
        }
        return fieldType;
    }

    public void addExternalImportDescrProvider(ExternalImportDescrProvider provider) {
        this.externalImportDescrProviders.add( provider );
    }

    public Set<ImportDescr> getExternalImportDescrs() {
        Set<ImportDescr> result = new HashSet<ImportDescr>();
        for ( ExternalImportDescrProvider externalImportDescrProvider : this.externalImportDescrProviders ) {
            result.addAll( externalImportDescrProvider.getImportDescrs() );
        }

        return result;
    }

    /**
     * @return true if there were errors when processing the package.
     */
    public boolean hasErrors() {
        return (this.errors.size() > 0);
    }

    /**
     * Returns a list of String errors.
     */
    public List<String> getErrors() {
        return this.errors;
    }

    class WildCardException extends Exception {

    }
}
