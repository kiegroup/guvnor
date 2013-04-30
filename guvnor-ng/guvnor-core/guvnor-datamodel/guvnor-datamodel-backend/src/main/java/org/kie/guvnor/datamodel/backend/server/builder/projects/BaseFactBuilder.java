package org.kie.guvnor.datamodel.backend.server.builder.projects;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.drools.guvnor.models.commons.shared.oracle.DataType;
import org.kie.guvnor.datamodel.backend.server.builder.util.AnnotationUtils;
import org.kie.guvnor.datamodel.model.FieldAccessorsAndMutators;
import org.kie.guvnor.datamodel.model.ModelField;
import org.kie.guvnor.datamodel.oracle.ProjectDataModelOracleImpl;

/**
 * Base FactBuilder containing common code
 */
public abstract class BaseFactBuilder implements FactBuilder {

    private final ProjectDataModelOracleBuilder builder;

    private final String type;
    private final String superType;
    private final List<ModelField> fields = new ArrayList<ModelField>();
    private final Set<org.kie.guvnor.datamodel.model.Annotation> annotations = new LinkedHashSet<org.kie.guvnor.datamodel.model.Annotation>();

    private final boolean isCollection;
    private final boolean isEvent;
    private final boolean isDeclaredType;

    public BaseFactBuilder( final ProjectDataModelOracleBuilder builder,
                            final Class<?> clazz,
                            final boolean isEvent,
                            final boolean isDeclaredType ) {
        this.builder = builder;
        this.type = getType( clazz );
        this.superType = getSuperType( clazz );
        this.isCollection = isCollectionType( clazz );
        this.isEvent = isEvent;
        this.isDeclaredType = isDeclaredType;
        this.annotations.addAll( getAnnotations( clazz ) );

        addField( new ModelField( DataType.TYPE_THIS,
                                  type,
                                  ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                  FieldAccessorsAndMutators.ACCESSOR,
                                  DataType.TYPE_THIS ) );
    }

    public BaseFactBuilder( final ProjectDataModelOracleBuilder builder,
                            final String type,
                            final boolean isCollection,
                            final boolean isEvent,
                            final boolean isDeclaredType ) {
        this.builder = builder;
        this.type = type;
        this.superType = null;
        this.isCollection = isCollection;
        this.isEvent = isEvent;
        this.isDeclaredType = isDeclaredType;

        addField( new ModelField( DataType.TYPE_THIS,
                                  type,
                                  ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                  FieldAccessorsAndMutators.ACCESSOR,
                                  DataType.TYPE_THIS ) );
    }

    protected String getType( final Class<?> clazz ) {
        return clazz.getName();
    }

    protected String getSuperType( final Class<?> clazz ) {
        final Class<?> superType = clazz.getSuperclass();
        return ( superType == null || Object.class.equals( superType ) ? null : superType.getName() );
    }

    protected boolean isCollectionType( final Class<?> clazz ) {
        return clazz != null && Collection.class.isAssignableFrom( clazz );
    }

    protected Set<org.kie.guvnor.datamodel.model.Annotation> getAnnotations( final Class<?> clazz ) {
        final Set<org.kie.guvnor.datamodel.model.Annotation> dmoAnnotations = new LinkedHashSet<org.kie.guvnor.datamodel.model.Annotation>();
        final Annotation annotations[] = clazz.getAnnotations();
        for ( Annotation a : annotations ) {
            final org.kie.guvnor.datamodel.model.Annotation dmoa = new org.kie.guvnor.datamodel.model.Annotation( a.annotationType().getName() );
            for ( Method m : a.annotationType().getDeclaredMethods() ) {
                final String methodName = m.getName();
                dmoa.addAttribute( methodName,
                                   AnnotationUtils.getAnnotationAttributeValue( a,
                                                                                methodName ) );
            }
            dmoAnnotations.add( dmoa );
        }
        return dmoAnnotations;
    }

    protected FactBuilder addField( final ModelField field ) {
        this.fields.add( field );
        return this;
    }

    @Override
    public ProjectDataModelOracleBuilder end() {
        return builder;
    }

    @Override
    public void build( final ProjectDataModelOracleImpl oracle ) {
        oracle.addFactsAndFields( buildFactsAndFields() );
        oracle.addCollectionTypes( buildCollectionTypes() );
        oracle.addEventTypes( buildEventTypes() );
        oracle.addDeclaredTypes( buildDeclaredTypes() );
        oracle.addSuperTypes( buildSuperTypes() );
        oracle.addTypeAnnotations( buildTypeAnnotations() );
    }

    public ProjectDataModelOracleBuilder getDataModelBuilder() {
        return this.builder;
    }

    private Map<String, ModelField[]> buildFactsAndFields() {
        final Map<String, ModelField[]> loadableFactsAndFields = new HashMap<String, ModelField[]>();
        final ModelField[] loadableFields = new ModelField[ fields.size() ];
        fields.toArray( loadableFields );
        loadableFactsAndFields.put( type,
                                    loadableFields );
        return loadableFactsAndFields;
    }

    private Map<String, Boolean> buildCollectionTypes() {
        final Map<String, Boolean> loadableCollectionTypes = new HashMap<String, Boolean>();
        loadableCollectionTypes.put( type,
                                     isCollection );
        return loadableCollectionTypes;
    }

    private Map<String, Boolean> buildEventTypes() {
        final Map<String, Boolean> loadableEventTypes = new HashMap<String, Boolean>();
        loadableEventTypes.put( type,
                                isEvent );
        return loadableEventTypes;
    }

    private Map<String, Boolean> buildDeclaredTypes() {
        final Map<String, Boolean> loadableDeclaredTypes = new HashMap<String, Boolean>();
        loadableDeclaredTypes.put( type,
                                   isDeclaredType );
        return loadableDeclaredTypes;
    }

    private Map<String, String> buildSuperTypes() {
        final Map<String, String> loadableSuperTypes = new HashMap<String, String>();
        loadableSuperTypes.put( type,
                                superType );
        return loadableSuperTypes;
    }

    private Map<String, Set<org.kie.guvnor.datamodel.model.Annotation>> buildTypeAnnotations() {
        final Map<String, Set<org.kie.guvnor.datamodel.model.Annotation>> loadableTypeAnnotations = new HashMap<String, Set<org.kie.guvnor.datamodel.model.Annotation>>();
        loadableTypeAnnotations.put( type,
                                     annotations );
        return loadableTypeAnnotations;
    }

}
