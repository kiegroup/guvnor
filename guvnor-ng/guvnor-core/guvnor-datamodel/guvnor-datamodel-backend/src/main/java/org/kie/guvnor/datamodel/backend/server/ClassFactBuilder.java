package org.kie.guvnor.datamodel.backend.server;

import java.beans.Introspector;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.drools.core.util.asm.ClassFieldInspector;
import org.kie.guvnor.datamodel.model.ClassMethodInspector;
import org.kie.guvnor.datamodel.model.ClassToGenericClassConverter;
import org.kie.guvnor.datamodel.model.DefaultDataModel;
import org.kie.guvnor.datamodel.model.FieldAccessorsAndMutators;
import org.kie.guvnor.datamodel.model.MethodInfo;
import org.kie.guvnor.datamodel.model.ModelField;

/**
 * Builder for Fact Types originating from a .class
 */
public class ClassFactBuilder extends BaseFactBuilder {

    private final ClassToGenericClassConverter typeSystemConverter = new JavaTypeSystemTranslator();

    private final Map<String, List<MethodInfo>> methodInformation = new HashMap<String, List<MethodInfo>>();
    private final Map<String, String> fieldParametersType = new HashMap<String, String>();

    public ClassFactBuilder( final DataModelBuilder builder,
                             final Class<?> clazz ) throws IOException {
        this( builder,
              clazz,
              false );
    }

    public ClassFactBuilder( final DataModelBuilder builder,
                             final Class<?> clazz,
                             final boolean isEvent ) throws IOException {
        super( builder,
               clazz.getSimpleName(),
               isEvent );
        loadClassFields( clazz );
    }

    @Override
    public void build( final DefaultDataModel oracle ) {
        super.build( oracle );
        oracle.addMethodInformation( buildMethodInformation() );
        oracle.addFieldParametersType( buildFieldParametersType() );
    }

    private Map<String, List<MethodInfo>> buildMethodInformation() {
        final Map<String, List<MethodInfo>> loadableMethodInformation = new HashMap<String, List<MethodInfo>>();
        if ( methodInformation.size() > 0 ) {
            loadableMethodInformation.putAll( methodInformation );
        }
        return loadableMethodInformation;
    }

    private Map<String, String> buildFieldParametersType() {
        final Map<String, String> loadableFieldParametersType = new HashMap<String, String>();
        if ( fieldParametersType.size() > 0 ) {
            loadableFieldParametersType.putAll( fieldParametersType );
        }
        return loadableFieldParametersType;
    }

    private void loadClassFields( final Class<?> clazz ) throws IOException {
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
                    final String genericReturnType = typeSystemConverter.translateClassToGenericType( returnType );

                    addField( new ModelField( fieldName,
                                              genericReturnType,
                                              ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                              methodSignatures.get( qualifiedName ).accessorAndMutator,
                                              genericReturnType ) );
                }
            } else {

                //Otherwise we can use the results of ClassFieldInspector
                final Class<?> returnType = inspector.getFieldTypes().get( fieldName );
                final String genericReturnType = typeSystemConverter.translateClassToGenericType( returnType );

                addField( new ModelField( fieldName,
                                          genericReturnType,
                                          ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                          methodSignatures.get( qualifiedName ).accessorAndMutator,
                                          genericReturnType ) );
            }
        }

        //Methods for use in ActionCallMethod's
        ClassMethodInspector methodInspector = new ClassMethodInspector( clazz,
                                                                         typeSystemConverter );

        final List<MethodInfo> methodInformation = methodInspector.getMethodInfos();
        for ( final MethodInfo mi : methodInformation ) {
            final String genericType = mi.getParametricReturnType();
            if ( genericType != null ) {
                final String qualifiedFactFieldName = factType + "." + mi.getNameWithParameters();
                this.fieldParametersType.put( qualifiedFactFieldName,
                                              genericType );
            }
        }
        this.methodInformation.put( factType,
                                    methodInformation );
    }

    // Remove the unneeded "fields" that come from java.lang.Object
    private List<String> removeIrrelevantFields( Collection<String> fields ) {
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
