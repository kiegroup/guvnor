package org.kie.guvnor.datamodel.backend.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kie.guvnor.datamodel.model.DefaultDataModel;
import org.kie.guvnor.datamodel.model.FieldAccessorsAndMutators;
import org.kie.guvnor.datamodel.model.ModelAnnotation;
import org.kie.guvnor.datamodel.model.ModelField;
import org.kie.guvnor.datamodel.oracle.DataType;

/**
 * Base FactBuilder containing common code
 */
public abstract class BaseFactBuilder implements FactBuilder {

    private final DataModelBuilder builder;
    private final String factType;
    private final List<ModelField> fields = new ArrayList<ModelField>();
    private final List<ModelAnnotation> annotations = new ArrayList<ModelAnnotation>();

    public BaseFactBuilder( final DataModelBuilder builder,
                            final String factType ) {
        this.builder = builder;
        this.factType = factType;
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

    protected FactBuilder addAnnotation( final ModelAnnotation annotation ) {
        this.annotations.add( annotation );
        return this;
    }

    @Override
    public DataModelBuilder end() {
        return builder;
    }

    @Override
    public void build( final DefaultDataModel oracle ) {
        oracle.addFactsAndFields( buildFactsAndFields() );
        oracle.addFactAnnotations( buildFactAnnotations() );
    }

    private Map<String, ModelField[]> buildFactsAndFields() {
        final Map<String, ModelField[]> loadableFactsAndFields = new HashMap<String, ModelField[]>();
        final ModelField[] loadableFields = new ModelField[ fields.size() ];
        fields.toArray( loadableFields );
        loadableFactsAndFields.put( factType,
                                    loadableFields );
        return loadableFactsAndFields;
    }

    private Map<String, List<ModelAnnotation>> buildFactAnnotations() {
        final Map<String, List<ModelAnnotation>> loadableFactAnnotations = new HashMap<String, List<ModelAnnotation>>();
        if ( annotations.size() > 0 ) {
            loadableFactAnnotations.put( factType,
                                         annotations );
        }
        return loadableFactAnnotations;
    }

}
