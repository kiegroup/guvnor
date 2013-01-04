package org.kie.guvnor.datamodel.backend.server;

import java.beans.Introspector;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.drools.core.util.asm.ClassFieldInspector;
import org.kie.guvnor.datamodel.model.DefaultDataModel;
import org.kie.guvnor.datamodel.model.FieldAccessorsAndMutators;
import org.kie.guvnor.datamodel.model.ModelAnnotation;
import org.kie.guvnor.datamodel.model.ModelField;
import org.kie.guvnor.datamodel.oracle.DataModelOracle;
import org.kie.guvnor.datamodel.oracle.DataType;

/**
 * Builder for DataModelOracle
 */
public final class DataModelBuilder {

    private DefaultDataModel oracle = new DefaultDataModel();

    private List<FactBuilder> factTypeBuilders = new ArrayList<FactBuilder>();

    private Map<String, String[]> factFieldEnums = new HashMap<String, String[]>();

    private List<String> errors = new ArrayList<String>();

    public static DataModelBuilder newDataModelBuilder() {
        return new DataModelBuilder();
    }

    private DataModelBuilder() {
    }

    public FactBuilder addFact( final String factType ) {
        final FactBuilder builder = new FactBuilder( this,
                                                     factType );
        factTypeBuilders.add( builder );
        return builder;
    }

    public DataModelBuilder addClass( final Class clazz ) throws IOException {
        final String factType = clazz.getSimpleName();
        final FactBuilder builder = new FactBuilder( this,
                                                     factType );
        factTypeBuilders.add( builder );
        loadClassAnnotations( clazz,
                              builder );
        loadClassFields( clazz,
                         builder );

        return this;
    }

    public DataModelBuilder addEnum( final String factType,
                                     final String fieldName,
                                     final String[] values ) {
        final String qualifiedFactField = factType + "." + fieldName;
        addEnum( qualifiedFactField,
                 values );
        return this;
    }

    public DataModelBuilder addEnum( final String qualifiedFactField,
                                     final String[] values ) {
        factFieldEnums.put( qualifiedFactField,
                            values );
        return this;
    }

    public DataModelBuilder addEnum( final String enumDefinition ) {
        parseEnumDefinition( enumDefinition );
        return this;
    }

    private void parseEnumDefinition( final String enumDefinition ) {
        DataEnumLoader enumLoader = new DataEnumLoader( enumDefinition );
        if ( enumLoader.hasErrors() ) {
            errors.addAll( enumLoader.getErrors() );
        } else {
            factFieldEnums.putAll( enumLoader.getData() );
        }
    }

    private void loadClassAnnotations( final Class<?> clazz,
                                       final FactBuilder builder ) {
        //TODO {manstis} Load annotations
    }

    private void loadClassFields( final Class<?> clazz,
                                  final FactBuilder builder ) throws IOException {
        if ( clazz == null ) {
            return;
        }

        final String factType = clazz.getSimpleName();

        //Get all getters and setters for the class. This does not handle delegated properties
        final ClassFieldInspector inspector = new ClassFieldInspector( clazz );
        final Set<String> fieldNamesSet = new TreeSet<String>( inspector.getFieldNames().keySet() );
        final List<String> fieldNames = removeIrrelevantFields( fieldNamesSet );

        //Consolidate methods into those with getters or setters
        final Method[] methods = clazz.getMethods();
        final Map<String, MethodSignature> methodSignatures = removeIrrelevantMethods( getMethodSignatures( factType,
                                                                                                            methods ) );

        //Add Fields from ClassFieldInspector which provides a list of "reasonable" methods
        for ( final String fieldName : fieldNames ) {
            final String qualifiedName = factType + "." + fieldName;
            final Field f = inspector.getFieldTypesField().get( fieldName );
            if ( f == null ) {

                //If a Field cannot be found is is really a delegated property so use the Method return type
                if ( methodSignatures.containsKey( qualifiedName ) ) {
                    final MethodSignature m = methodSignatures.get( qualifiedName );
                    final Class<?> returnType = m.returnType;
                    final String genericReturnType = translateClassToGenericType( returnType );

                    builder.addField( new ModelField( fieldName,
                                                      genericReturnType,
                                                      ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                                      methodSignatures.get( qualifiedName ).accessorAndMutator,
                                                      genericReturnType ) );
                }
            } else {

                //Otherwise we can use the results of ClassFieldInspector
                final Class<?> returnType = inspector.getFieldTypes().get( fieldName );
                final String genericReturnType = translateClassToGenericType( returnType );

                builder.addField( new ModelField( fieldName,
                                                  genericReturnType,
                                                  ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                                  methodSignatures.get( qualifiedName ).accessorAndMutator,
                                                  genericReturnType ) );
            }
        }

        //Methods for use in ActionCallMethod's
//        ClassMethodInspector methodInspector = new ClassMethodInspector( clazz,
//                                                                         this );
//
//        final List<MethodInfo> methodInfos = methodInspector.getMethodInfos();
//        for ( final MethodInfo mi : methodInfos ) {
//            final String genericType = mi.getParametricReturnType();
//            if ( genericType != null ) {
//                this.builder.putParametricFieldType( factType + "." + mi.getNameWithParameters(),
//                                                     genericType );
//            }
//        }
//        this.builder.getInstance().addMethodInfo( factType,
//                                                  methodInfos );
    }

    // Remove the unneeded "fields" that come from java.lang.Object
    public List<String> removeIrrelevantFields( Collection<String> fields ) {
        final List<String> result = new ArrayList<String>();
        for ( String field : fields ) {
            //clone, empty, iterator, listIterator, size, toArray
            if ( !( field.equals( "class" ) || field.equals( "hashCode" ) || field.equals( "toString" ) ) ) {
                result.add( field );
            }
        }
        return result;
    }

    // Remove the unneeded "methods" that come from java.lang.Object
    private Map<String, MethodSignature> removeIrrelevantMethods( Map<String, MethodSignature> methods ) {
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

    private Map<String, MethodSignature> getMethodSignatures( final String factType,
                                                              final Method[] methods ) {

        Map<String, MethodSignature> methodSignatures = new HashMap<String, MethodSignature>();

        //Determine accessors for methods
        for ( Method method : methods ) {
            String name = method.getName();
            if ( method.getParameterTypes().length > 0 ) {

                //Strip bare mutator name
                if ( name.startsWith( "set" ) ) {
                    name = Introspector.decapitalize( name.substring( 3 ) );
                } else {
                    name = Introspector.decapitalize( name );
                }

                final String factField = factType + "." + name;
                if ( !methodSignatures.containsKey( factField ) ) {
                    methodSignatures.put( factField,
                                          new MethodSignature( FieldAccessorsAndMutators.MUTATOR,
                                                               void.class.getGenericSuperclass(),
                                                               void.class ) );
                } else if ( methodSignatures.get( factField ).accessorAndMutator == FieldAccessorsAndMutators.ACCESSOR ) {
                    MethodSignature signature = methodSignatures.get( factField );
                    signature.accessorAndMutator = FieldAccessorsAndMutators.BOTH;
                }

            } else if ( !method.getReturnType().getName().equals( "void" ) ) {

                //Strip bare accessor name
                if ( name.startsWith( "get" ) ) {
                    name = Introspector.decapitalize( name.substring( 3 ) );
                } else if ( name.startsWith( "is" ) ) {
                    name = Introspector.decapitalize( name.substring( 2 ) );
                } else {
                    name = Introspector.decapitalize( name );
                }

                final String factField = factType + "." + name;
                if ( !methodSignatures.containsKey( factField ) ) {
                    methodSignatures.put( factField,
                                          new MethodSignature( FieldAccessorsAndMutators.ACCESSOR,
                                                               method.getGenericReturnType(),
                                                               method.getReturnType() ) );
                } else if ( methodSignatures.get( factField ).accessorAndMutator == FieldAccessorsAndMutators.MUTATOR ) {
                    MethodSignature signature = methodSignatures.get( factField );
                    signature.accessorAndMutator = FieldAccessorsAndMutators.BOTH;
                    signature.genericType = method.getGenericReturnType();
                    signature.returnType = method.getReturnType();
                }
            }
        }
        return methodSignatures;
    }

    //Convert Java's Type system into a the portable Type system used by Guvnor (that is GWT friendly)
    private String translateClassToGenericType( final Class<?> type ) {
        String fieldType = null; // if null, will use standard operators
        if ( type != null ) {
            if ( type.isPrimitive() ) {
                if ( type == byte.class ) {
                    fieldType = DataType.TYPE_NUMERIC_BYTE;
                } else if ( type == double.class ) {
                    fieldType = DataType.TYPE_NUMERIC_DOUBLE;
                } else if ( type == float.class ) {
                    fieldType = DataType.TYPE_NUMERIC_FLOAT;
                } else if ( type == int.class ) {
                    fieldType = DataType.TYPE_NUMERIC_INTEGER;
                } else if ( type == long.class ) {
                    fieldType = DataType.TYPE_NUMERIC_LONG;
                } else if ( type == short.class ) {
                    fieldType = DataType.TYPE_NUMERIC_SHORT;
                } else if ( type == boolean.class ) {
                    fieldType = DataType.TYPE_BOOLEAN;
                }
            } else if ( BigDecimal.class.isAssignableFrom( type ) ) {
                fieldType = DataType.TYPE_NUMERIC_BIGDECIMAL;
            } else if ( BigInteger.class.isAssignableFrom( type ) ) {
                fieldType = DataType.TYPE_NUMERIC_BIGINTEGER;
            } else if ( Byte.class.isAssignableFrom( type ) ) {
                fieldType = DataType.TYPE_NUMERIC_BYTE;
            } else if ( Double.class.isAssignableFrom( type ) ) {
                fieldType = DataType.TYPE_NUMERIC_DOUBLE;
            } else if ( Float.class.isAssignableFrom( type ) ) {
                fieldType = DataType.TYPE_NUMERIC_FLOAT;
            } else if ( Integer.class.isAssignableFrom( type ) ) {
                fieldType = DataType.TYPE_NUMERIC_INTEGER;
            } else if ( Long.class.isAssignableFrom( type ) ) {
                fieldType = DataType.TYPE_NUMERIC_LONG;
            } else if ( Short.class.isAssignableFrom( type ) ) {
                fieldType = DataType.TYPE_NUMERIC_SHORT;
            } else if ( Boolean.class.isAssignableFrom( type ) ) {
                fieldType = DataType.TYPE_BOOLEAN;
            } else if ( String.class.isAssignableFrom( type ) ) {
                fieldType = DataType.TYPE_STRING;
            } else if ( Collection.class.isAssignableFrom( type ) ) {
                fieldType = DataType.TYPE_COLLECTION;
            } else if ( Date.class.isAssignableFrom( type ) ) {
                fieldType = DataType.TYPE_DATE;
            } else if ( Comparable.class.isAssignableFrom( type ) ) {
                fieldType = DataType.TYPE_COMPARABLE;
            } else {
                fieldType = type.getSimpleName();
            }
        }
        return fieldType;
    }

    public DataModelOracle build() {
        loadFactsAndFields();
        loadFactAnnotations();
        loadEnums();
        return oracle;
    }

    private void loadFactsAndFields() {
        final Map<String, ModelField[]> loadableFactsAndFields = new HashMap<String, ModelField[]>();
        for ( FactBuilder factTypeBuilder : factTypeBuilders ) {
            factTypeBuilder.buildFactsAndFields( loadableFactsAndFields );
        }
        oracle.setFactsAndFields( loadableFactsAndFields );
    }

    private void loadFactAnnotations() {
        final Map<String, List<ModelAnnotation>> loadableFactAnnotations = new HashMap<String, List<ModelAnnotation>>();
        for ( FactBuilder factTypeBuilder : factTypeBuilders ) {
            factTypeBuilder.buildFactAnnotations( loadableFactAnnotations );
        }
        oracle.setFactAnnotations( loadableFactAnnotations );
    }

    private void loadEnums() {
        final Map<String, String[]> loadableEnums = new HashMap<String, String[]>();
        for ( Map.Entry<String, String[]> e : factFieldEnums.entrySet() ) {
            final String qualifiedFactField = e.getKey();
            loadableEnums.put( qualifiedFactField,
                               e.getValue() );
        }
        oracle.setEnums( loadableEnums );
    }

    /**
     * Builder for Fact Types
     */
    public static class FactBuilder {

        private final DataModelBuilder builder;
        private final String factType;
        private final List<ModelField> fields = new ArrayList<ModelField>();
        private final List<ModelAnnotation> annotations = new ArrayList<ModelAnnotation>();

        public FactBuilder( final DataModelBuilder builder,
                            final String factType ) {
            this.builder = builder;
            this.factType = factType;
            addField( new ModelField( DataType.TYPE_THIS,
                                      factType,
                                      ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                      FieldAccessorsAndMutators.ACCESSOR,
                                      DataType.TYPE_THIS ) );
        }

        public FactBuilder addField( final ModelField field ) {
            this.fields.add( field );
            return this;
        }

        public FactBuilder addAnnotation( final ModelAnnotation annotation ) {
            this.annotations.add( annotation );
            return this;
        }

        public DataModelBuilder end() {
            return builder;
        }

        private void buildFactsAndFields( final Map<String, ModelField[]> loadableFactsAndFields ) {
            final ModelField[] loadableFields = new ModelField[ fields.size() ];
            fields.toArray( loadableFields );
            loadableFactsAndFields.put( factType,
                                        loadableFields );
        }

        private void buildFactAnnotations( final Map<String, List<ModelAnnotation>> loadableFactAnnotations ) {
            if ( annotations.size() > 0 ) {
                loadableFactAnnotations.put( factType,
                                             annotations );
            }
        }

    }

    private static class MethodSignature {

        MethodSignature( final FieldAccessorsAndMutators accessorAndMutator,
                         final Type genericType,
                         final Class<?> returnType ) {
            this.accessorAndMutator = accessorAndMutator;
            this.genericType = genericType;
            this.returnType = returnType;
        }

        FieldAccessorsAndMutators accessorAndMutator;
        Type genericType;
        Class<?> returnType;

    }

}
