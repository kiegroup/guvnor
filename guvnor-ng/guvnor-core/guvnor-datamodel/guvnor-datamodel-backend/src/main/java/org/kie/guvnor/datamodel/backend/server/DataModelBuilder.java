package org.kie.guvnor.datamodel.backend.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

}
