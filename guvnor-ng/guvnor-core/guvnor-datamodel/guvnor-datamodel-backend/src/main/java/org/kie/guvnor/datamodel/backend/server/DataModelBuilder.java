package org.kie.guvnor.datamodel.backend.server;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.lang.dsl.DSLMappingEntry;
import org.drools.lang.dsl.DSLMappingParseException;
import org.drools.lang.dsl.DSLTokenizedMappingFile;
import org.kie.guvnor.datamodel.model.DSLSentence;
import org.kie.guvnor.datamodel.model.DefaultDataModel;
import org.kie.guvnor.datamodel.oracle.DataModelOracle;

/**
 * Builder for DataModelOracle
 */
public final class DataModelBuilder {

    private DefaultDataModel oracle = new DefaultDataModel();

    private List<FactBuilder> factTypeBuilders = new ArrayList<FactBuilder>();
    private Map<String, String[]> factFieldEnums = new HashMap<String, String[]>();

    private List<DSLSentence> dslConditionSentences = new ArrayList<DSLSentence>();
    private List<DSLSentence> dslActionSentences = new ArrayList<DSLSentence>();

    //These are not used anywhere in Guvnor 5.5.x, but have been retained for future scope
    private List<DSLSentence> dslKeywordItems = new ArrayList<DSLSentence>();
    private List<DSLSentence> dslAnyScopeItems = new ArrayList<DSLSentence>();

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
                         false );
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

    public DataModelBuilder addDsl( final String dslDefinition ) {
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
        loadFactTypes();
        loadEnums();
        loadDsls();
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

    private void loadDsls() {
        oracle.addDslConditionSentences( dslConditionSentences );
        oracle.addDslActionSentences( dslActionSentences );
    }

}
