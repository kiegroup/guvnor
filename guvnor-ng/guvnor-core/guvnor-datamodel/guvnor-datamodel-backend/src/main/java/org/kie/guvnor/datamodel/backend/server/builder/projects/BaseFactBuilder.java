package org.kie.guvnor.datamodel.backend.server.builder.projects;

import org.drools.guvnor.models.commons.oracle.DataType;
import org.kie.guvnor.datamodel.model.FieldAccessorsAndMutators;
import org.kie.guvnor.datamodel.model.ModelField;
import org.kie.guvnor.datamodel.oracle.ProjectDefinition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Base FactBuilder containing common code
 */
public abstract class BaseFactBuilder implements FactBuilder {

    private final ProjectDefinitionBuilder builder;
    private final String factType;
    private final List<ModelField> fields = new ArrayList<ModelField>();
    private final boolean isCollection;
    private final boolean isEvent;

    public BaseFactBuilder( final ProjectDefinitionBuilder builder,
                            final Class<?> clazz ) {
        this( builder,
              clazz,
              false );
    }

    public BaseFactBuilder( final ProjectDefinitionBuilder builder,
                            final Class<?> clazz,
                            final boolean isEvent ) {
        this.builder = builder;
        this.factType = getFactType( clazz );
        this.isCollection = isCollectionType( clazz );
        this.isEvent = isEvent;
        addField( new ModelField( DataType.TYPE_THIS,
                                  factType,
                                  ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                  FieldAccessorsAndMutators.ACCESSOR,
                                  DataType.TYPE_THIS ) );
    }

    public BaseFactBuilder( final ProjectDefinitionBuilder builder,
                            final String factType ) {
        this( builder,
              factType,
              false,
              false );
    }

    public BaseFactBuilder( final ProjectDefinitionBuilder builder,
                            final String factType,
                            final boolean isCollection,
                            final boolean isEvent ) {
        this.builder = builder;
        this.factType = factType;
        this.isCollection = isCollection;
        this.isEvent = isEvent;
        addField( new ModelField( DataType.TYPE_THIS,
                                  factType,
                                  ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                  FieldAccessorsAndMutators.ACCESSOR,
                                  DataType.TYPE_THIS ) );
    }

    protected FactBuilder addField( final ModelField field ) {
        this.fields.add( field );
        return this;
    }

    @Override
    public ProjectDefinitionBuilder end() {
        return builder;
    }

    @Override
    public void build( final ProjectDefinition oracle ) {
        oracle.addFactsAndFields( buildFactsAndFields() );
        oracle.addCollectionType( buildCollectionTypes() );
        oracle.addEventType( buildEventTypes() );
    }

    public ProjectDefinitionBuilder getDataModelBuilder() {
        return this.builder;
    }

    private Map<String, ModelField[]> buildFactsAndFields() {
        final Map<String, ModelField[]> loadableFactsAndFields = new HashMap<String, ModelField[]>();
        final ModelField[] loadableFields = new ModelField[ fields.size() ];
        fields.toArray( loadableFields );
        loadableFactsAndFields.put( factType,
                                    loadableFields );
        return loadableFactsAndFields;
    }

    private Map<String, Boolean> buildCollectionTypes() {
        final Map<String, Boolean> loadableCollectionTypes = new HashMap<String, Boolean>();
        loadableCollectionTypes.put( factType,
                                     isCollection );
        return loadableCollectionTypes;
    }

    private Map<String, Boolean> buildEventTypes() {
        final Map<String, Boolean> loadableEventTypes = new HashMap<String, Boolean>();
        loadableEventTypes.put( factType,
                                isEvent );
        return loadableEventTypes;
    }

    protected String getFactType( final Class<?> clazz ) {
        return clazz.getName();
    }

    protected boolean isCollectionType( final Class<?> clazz ) {
        return clazz != null && Collection.class.isAssignableFrom( clazz );
    }

}
