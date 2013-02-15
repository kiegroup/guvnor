package org.kie.guvnor.datamodel.backend.server.builder.packages;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.lang.dsl.DSLMappingEntry;
import org.drools.lang.dsl.DSLMappingParseException;
import org.drools.lang.dsl.DSLTokenizedMappingFile;
import org.kie.guvnor.datamodel.backend.server.builder.projects.DataEnumLoader;
import org.kie.guvnor.datamodel.model.DSLSentence;
import org.kie.guvnor.datamodel.oracle.DataModelOracle;
import org.kie.guvnor.datamodel.oracle.PackageDataModelOracle;
import org.kie.guvnor.datamodel.oracle.ProjectDefinition;

/**
 * Builder for PackageDataModelOracle
 */
public final class PackageDataModelOracleBuilder {

    private final String packageName;

    private PackageDataModelOracle oracle = new PackageDataModelOracle();
    private ProjectDefinition projectDefinition = new ProjectDefinition();

    private Map<String, String[]> factFieldEnums = new HashMap<String, String[]>();
    private List<DSLSentence> dslConditionSentences = new ArrayList<DSLSentence>();
    private List<DSLSentence> dslActionSentences = new ArrayList<DSLSentence>();
    //These are not used anywhere in Guvnor 5.5.x, but have been retained for future scope
    private List<DSLSentence> dslKeywordItems = new ArrayList<DSLSentence>();
    private List<DSLSentence> dslAnyScopeItems = new ArrayList<DSLSentence>();

    private List<String> errors = new ArrayList<String>();

    public static PackageDataModelOracleBuilder newDataModelBuilder() {
        return new PackageDataModelOracleBuilder( "" );
    }

    public static PackageDataModelOracleBuilder newDataModelBuilder( final String packageName ) {
        return new PackageDataModelOracleBuilder( packageName );
    }

    private PackageDataModelOracleBuilder( final String packageName ) {
        this.packageName = packageName;
    }

    public PackageDataModelOracleBuilder setProjectDefinition( final ProjectDefinition projectDefinition ) {
        this.projectDefinition = projectDefinition;
        return this;
    }

    public PackageDataModelOracleBuilder addEnum( final String factType,
                                                  final String fieldName,
                                                  final String[] values ) {
        final String qualifiedFactField = factType + "#" + fieldName;
        factFieldEnums.put( qualifiedFactField,
                            values );
        return this;
    }

    public PackageDataModelOracleBuilder addEnum( final String enumDefinition ) {
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

    public PackageDataModelOracleBuilder addDsl( final String dslDefinition ) {
        parseDslDefinition( dslDefinition );
        return this;
    }

    private void parseDslDefinition( final String dslDefinition ) {
        final DSLTokenizedMappingFile dslLoader = new DSLTokenizedMappingFile();
        try {
            if ( dslLoader.parseAndLoad( new StringReader( dslDefinition ) ) ) {
                populateDSLSentences( dslLoader );
            } else {
                logDslErrors( dslLoader );
            }
        } catch ( IOException e ) {
            errors.add( e.getMessage() );
        }
    }

    private void populateDSLSentences( final DSLTokenizedMappingFile dslLoader ) {
        for ( DSLMappingEntry entry : dslLoader.getMapping().getEntries() ) {
            if ( entry.getSection() == DSLMappingEntry.CONDITION ) {
                addDSLConditionSentence( entry.getMappingKey() );
            } else if ( entry.getSection() == DSLMappingEntry.CONSEQUENCE ) {
                addDSLActionSentence( entry.getMappingKey() );
            } else if ( entry.getSection() == DSLMappingEntry.KEYWORD ) {
                addDSLKeywordMapping( entry.getMappingKey() );
            } else if ( entry.getSection() == DSLMappingEntry.ANY ) {
                addDSLAnyScopeMapping( entry.getMappingKey() );
            }
        }
    }

    private void addDSLConditionSentence( final String definition ) {
        final DSLSentence sentence = new DSLSentence();
        sentence.setDefinition( definition );
        this.dslConditionSentences.add( sentence );
    }

    private void addDSLActionSentence( final String definition ) {
        final DSLSentence sentence = new DSLSentence();
        sentence.setDefinition( definition );
        this.dslActionSentences.add( sentence );
    }

    private void addDSLKeywordMapping( final String definition ) {
        final DSLSentence sentence = new DSLSentence();
        sentence.setDefinition( definition );
        this.dslKeywordItems.add( sentence );
    }

    private void addDSLAnyScopeMapping( final String definition ) {
        final DSLSentence sentence = new DSLSentence();
        sentence.setDefinition( definition );
        this.dslAnyScopeItems.add( sentence );
    }

    private void logDslErrors( final DSLTokenizedMappingFile dslLoader ) {
        for ( final Object o : dslLoader.getErrors() ) {
            if ( o instanceof DSLMappingParseException ) {
                final DSLMappingParseException dslMappingParseException = (DSLMappingParseException) o;
                errors.add( "Line " + dslMappingParseException.getLine() + " : " + dslMappingParseException.getMessage() );
            } else if ( o instanceof Exception ) {
                final Exception excp = (Exception) o;
                errors.add( "Exception " + excp.getClass() + " " + excp.getMessage() + " " + excp.getCause() );
            } else {
                errors.add( "Uncategorized error " + o );
            }
        }
    }

    public DataModelOracle build() {
        loadEnums();
        loadDsls();
        loadProjectDefinition();
        return oracle;
    }

    public List<String> getErrors() {
        return errors;
    }

    private void loadProjectDefinition() {
        oracle.setPackageName( packageName );
        oracle.setProjectDefinition( projectDefinition );
        oracle.filter();
    }

    private void loadEnums() {
        final Map<String, String[]> loadableEnums = new HashMap<String, String[]>();
        for ( Map.Entry<String, String[]> e : factFieldEnums.entrySet() ) {
            final String qualifiedFactField = e.getKey();
            loadableEnums.put( qualifiedFactField,
                               e.getValue() );
        }
        oracle.addPackageEnums( loadableEnums );
    }

    private void loadDsls() {
        oracle.addPackageDslConditionSentences( dslConditionSentences );
        oracle.addPackageDslActionSentences( dslActionSentences );
    }

}
