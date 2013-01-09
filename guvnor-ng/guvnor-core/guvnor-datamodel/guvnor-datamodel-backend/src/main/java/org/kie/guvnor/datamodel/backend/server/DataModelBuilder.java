package org.kie.guvnor.datamodel.backend.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kie.guvnor.datamodel.model.DefaultDataModel;
import org.kie.guvnor.datamodel.oracle.DataModelOracle;

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

    public SimpleFactBuilder addFact( final String factType ) {
        return addFact( factType,
                        false );
    }

    public SimpleFactBuilder addFact( final String factType,
                                      final boolean isEvent ) {
        final SimpleFactBuilder builder = new SimpleFactBuilder( this,
                                                                 factType,
                                                                 isEvent );
        factTypeBuilders.add( builder );
        return builder;
    }

    public DataModelBuilder addClass( final Class clazz ) throws IOException {
        return addClass( clazz,
                         false);
    }

    public DataModelBuilder addClass( final Class clazz,
                                      final boolean isEvent ) throws IOException {
        final FactBuilder builder = new ClassFactBuilder( this,
                                                          clazz,
                                                          isEvent );
        factTypeBuilders.add( builder );
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

    public DataModelOracle build() {
        loadFactTypes();
        loadEnums();
        return oracle;
    }

    private void loadFactTypes() {
        for ( final FactBuilder factBuilder : this.factTypeBuilders ) {
            factBuilder.build( oracle );
        }
    }

    private void loadEnums() {
        final Map<String, String[]> loadableEnums = new HashMap<String, String[]>();
        for ( Map.Entry<String, String[]> e : factFieldEnums.entrySet() ) {
            final String qualifiedFactField = e.getKey();
            loadableEnums.put( qualifiedFactField,
                               e.getValue() );
        }
        oracle.addEnums( loadableEnums );
    }

}
