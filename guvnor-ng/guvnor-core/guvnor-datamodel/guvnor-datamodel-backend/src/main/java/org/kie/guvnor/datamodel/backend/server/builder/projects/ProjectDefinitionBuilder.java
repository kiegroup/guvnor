package org.kie.guvnor.datamodel.backend.server.builder.projects;

import org.kie.guvnor.datamodel.backend.server.builder.util.DataEnumLoader;
import org.kie.guvnor.datamodel.oracle.ProjectDefinition;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Builder for DataModelOracle
 */
public final class ProjectDefinitionBuilder {

    private ProjectDefinition projectDefinitions = new ProjectDefinition();

    private List<FactBuilder> factTypeBuilders = new ArrayList<FactBuilder>();
    private Map<String, String[]> factFieldEnums = new HashMap<String, String[]>();

    private List<String> errors = new ArrayList<String>();

    public static ProjectDefinitionBuilder newProjectDefinitionBuilder() {
        return new ProjectDefinitionBuilder();
    }

    private ProjectDefinitionBuilder() {
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

    public ProjectDefinitionBuilder addClass( final Class clazz ) throws IOException {
        return addClass( clazz,
                         false );
    }

    public ProjectDefinitionBuilder addClass( final Class clazz,
                                              final boolean isEvent ) throws IOException {
        final FactBuilder builder = new ClassFactBuilder( this,
                                                          clazz,
                                                          isEvent );
        factTypeBuilders.add( builder );
        return this;
    }

    public ProjectDefinitionBuilder addEnum( final String factType,
                                             final String fieldName,
                                             final String[] values ) {
        final String qualifiedFactField = factType + "#" + fieldName;
        factFieldEnums.put( qualifiedFactField,
                            values );
        return this;
    }

    public ProjectDefinitionBuilder addEnum( final String enumDefinition ) {
        parseEnumDefinition( enumDefinition );
        return this;
    }

    private void parseEnumDefinition( final String enumDefinition ) {
        final DataEnumLoader enumLoader = new DataEnumLoader( enumDefinition );
        if ( enumLoader.hasErrors() ) {
            logEnumErrors( enumLoader );
        } else {
            factFieldEnums.putAll( enumLoader.getData() );
        }
    }

    private void logEnumErrors( final DataEnumLoader enumLoader ) {
        errors.addAll( enumLoader.getErrors() );
    }

    public ProjectDefinition build() {
        loadFactTypes();
        loadEnums();
        return projectDefinitions;
    }

    private void loadFactTypes() {
        for ( final FactBuilder factBuilder : this.factTypeBuilders ) {
            factBuilder.build( projectDefinitions );
        }
    }

    private void loadEnums() {
        final Map<String, String[]> loadableEnums = new HashMap<String, String[]>();
        for ( Map.Entry<String, String[]> e : factFieldEnums.entrySet() ) {
            final String qualifiedFactField = e.getKey();
            loadableEnums.put( qualifiedFactField,
                               e.getValue() );
        }
        projectDefinitions.addEnumDefinitions( loadableEnums );
    }

}
