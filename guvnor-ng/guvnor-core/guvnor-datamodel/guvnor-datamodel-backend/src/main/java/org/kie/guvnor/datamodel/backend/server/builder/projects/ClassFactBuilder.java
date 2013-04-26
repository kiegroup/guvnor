package org.kie.guvnor.datamodel.backend.server.builder.projects;

import java.beans.Introspector;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.drools.core.util.asm.ClassFieldInspector;
import org.kie.guvnor.datamodel.model.ClassToGenericClassConverter;
import org.kie.guvnor.datamodel.model.FieldAccessorsAndMutators;
import org.kie.guvnor.datamodel.model.MethodInfo;
import org.kie.guvnor.datamodel.model.ModelField;
import org.kie.guvnor.datamodel.oracle.ProjectDataModelOracleImpl;

/**
 * Builder for Fact Types originating from a .class
 */
public class ClassFactBuilder extends BaseFactBuilder {

    private final ClassToGenericClassConverter typeSystemConverter = new JavaTypeSystemTranslator();

    private final Map<String, List<MethodInfo>> methodInformation = new HashMap<String, List<MethodInfo>>();
    private final Map<String, String> fieldParametersType = new HashMap<String, String>();

    public ClassFactBuilder( final ProjectDataModelOracleBuilder builder,
                             final Class<?> clazz ) throws IOException {
        this( builder,
              clazz,
              false );
    }

    public ClassFactBuilder( final ProjectDataModelOracleBuilder builder,
                             final Class<?> clazz,
                             final boolean isEvent ) throws IOException {
        super( builder,
               clazz,
               isEvent );
        loadClassFields( clazz );
    }

    @Override
    public void build( final ProjectDataModelOracleImpl oracle ) {
        super.build( oracle );
        oracle.addMethodInformation( methodInformation );
        oracle.addFieldParametersType( fieldParametersType );
    }

    private void loadClassFields( final Class<?> clazz ) throws IOException {
        if ( clazz == null ) {
            return;
        }

        final String factType = getFactType( clazz );

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
                    addParametricTypeForField( factType,
                                               fieldName,
                                               m.genericType );

                    final Class<?> returnType = m.returnType;
                    final String genericReturnType = typeSystemConverter.translateClassToGenericType( returnType );

                    addField( new ModelField( fieldName,
                                              returnType.getName(),
                                              ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                              methodSignatures.get( qualifiedName ).accessorAndMutator,
                                              genericReturnType ) );

                    addEnumsForField( factType,
                                      fieldName,
                                      returnType );

                }
            } else {

                //Otherwise we can use the results of ClassFieldInspector
                final Field field = inspector.getFieldTypesField().get( fieldName );
                addParametricTypeForField( factType,
                                           fieldName,
                                           field.getGenericType() );

                final Class<?> returnType = field.getType();
                final String genericReturnType = typeSystemConverter.translateClassToGenericType( returnType );

                addField( new ModelField( fieldName,
                                          returnType.getName(),
                                          ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                          methodSignatures.get( qualifiedName ).accessorAndMutator,
                                          genericReturnType ) );

                addEnumsForField( factType,
                                  fieldName,
                                  returnType );
            }

        }

        //Methods for use in ActionCallMethod's
        ClassMethodInspector methodInspector = new ClassMethodInspector( clazz,
                                                                         typeSystemConverter );

        final List<MethodInfo> methodInformation = methodInspector.getMethodInfos();
        for ( final MethodInfo mi : methodInformation ) {
            final String genericType = mi.getParametricReturnType();
            if ( genericType != null ) {
                final String qualifiedFactFieldName = factType + "#" + mi.getNameWithParameters();
                this.fieldParametersType.put( qualifiedFactFieldName,
                                              genericType );
            }
        }
        this.methodInformation.put( factType,
                                    methodInformation );
    }

    // Remove the unneeded "fields" that come from java.lang.Object
    private List<String> removeIrrelevantFields( final Collection<String> fields ) {
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
    private Map<String, MethodSignature> removeIrrelevantMethods( final Map<String, MethodSignature> methods ) {
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

    private static class MethodSignature {

        private MethodSignature( final FieldAccessorsAndMutators accessorAndMutator,
                                 final Type genericType,
                                 final Class<?> returnType ) {
            this.accessorAndMutator = accessorAndMutator;
            this.genericType = genericType;
            this.returnType = returnType;
        }

        private FieldAccessorsAndMutators accessorAndMutator;
        private Type genericType;
        private Class<?> returnType;

    }

    private void addEnumsForField( final String className,
                                   final String fieldName,
                                   final Class<?> fieldClazz ) {
        if ( fieldClazz.isEnum() ) {
            final Field[] enumFields = fieldClazz.getDeclaredFields();
            final List<String> enumValues = new ArrayList<String>();
            for ( final Field enumField : enumFields ) {
                if ( enumField.isEnumConstant() ) {
                    final String shortName = fieldClazz.getName().substring( fieldClazz.getName().lastIndexOf( "." ) + 1 ) + "." + enumField.getName();
                    enumValues.add( shortName + "=" + shortName );
                }
            }
            final String a[] = new String[ enumValues.size() ];
            enumValues.toArray( a );
            getDataModelBuilder().addEnum( className,
                                           fieldName,
                                           a );
        }
    }

    private void addParametricTypeForField( final String className,
                                            final String fieldName,
                                            final Type type ) {
        final String qualifiedFactFieldName = className + "#" + fieldName;
        final String parametricType = getParametricType( type );
        if ( parametricType != null ) {
            fieldParametersType.put( qualifiedFactFieldName,
                                     parametricType );
        }
    }

    private String getParametricType( final Type type ) {
        if ( type instanceof ParameterizedType ) {
            final ParameterizedType pt = (ParameterizedType) type;
            Type goodType = null;
            for ( final Type t : pt.getActualTypeArguments() ) {
                goodType = t;
            }
            if ( goodType != null ) {
                int index = goodType.toString().lastIndexOf( "." );
                return goodType.toString().substring( index + 1 );
            } else {
                return null;
            }
        }
        return null;
    }

}
