package org.kie.guvnor.datamodel.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kie.guvnor.datamodel.oracle.DataModelOracle;

/**
 * Builder for DataModelOracle
 */
public final class DataModelBuilder {

    private DefaultDataModel oracle = new DefaultDataModel();

    private Map<String, List<ModelField>> factsAndFields = new HashMap<String, List<ModelField>>();

    private Map<String, List<String>> factFieldEnums = new HashMap<String, List<String>>();

    private Map<String, FieldAccessorsAndMutators> accessorsAndMutators = new HashMap<String, FieldAccessorsAndMutators>();

    public static DataModelBuilder newDataModelBuilder() {
        return new DataModelBuilder();
    }

    private DataModelBuilder() {
    }

    public DataModelBuilder addFactField( final String factType,
                                          final ModelField field ) {
        addFactField( factType,
                      field,
                      FieldAccessorsAndMutators.BOTH );
        return this;
    }

    public DataModelBuilder addFactField( final String factType,
                                          final ModelField field,
                                          final FieldAccessorsAndMutators accessorsAndMutator ) {
        List<ModelField> fields = factsAndFields.get( factType );
        if ( fields == null ) {
            fields = new ArrayList<ModelField>();
            factsAndFields.put( factType,
                                fields );
        }
        fields.add( field );

        final String qualifiedFieldName = factType + "." + field.getName();
        accessorsAndMutators.put( qualifiedFieldName,
                                  accessorsAndMutator );
        return this;
    }

    public DataModelBuilder addEnum( final String factType,
                                     final String fieldName,
                                     final String[] values ) {
        final String qualifiedFactField = factType + "." + fieldName;
        factFieldEnums.put( qualifiedFactField,
                            Arrays.asList( values ) );
        return this;
    }

    public DataModelOracle build() {
        loadFactsAndFields();
        loadEnums();
        return oracle;
    }

    private void loadFactsAndFields() {
        final Map<String, ModelField[]> loadableFactsAndFields = new HashMap<String, ModelField[]>();
        for ( Map.Entry<String, List<ModelField>> e : factsAndFields.entrySet() ) {
            final String factType = e.getKey();
            final ModelField[] loadableFactAndFields = new ModelField[ e.getValue().size() ];
            e.getValue().toArray( loadableFactAndFields );
            loadableFactsAndFields.put( factType,
                                        loadableFactAndFields );
        }
        oracle.setModelFields( loadableFactsAndFields );
        oracle.setFactFieldAccessorsAndMutators( accessorsAndMutators );
    }

    private void loadEnums() {
        final Map<String, String[]> loadableEnums = new HashMap<String, String[]>();
        for ( Map.Entry<String, List<String>> e : factFieldEnums.entrySet() ) {
            final String qualifiedFactField = e.getKey();
            final String[] loadableEnum = new String[ e.getValue().size() ];
            e.getValue().toArray( loadableEnum );
            loadableEnums.put( qualifiedFactField,
                               loadableEnum );
        }
        oracle.setDataEnumLists( loadableEnums );
    }

}
