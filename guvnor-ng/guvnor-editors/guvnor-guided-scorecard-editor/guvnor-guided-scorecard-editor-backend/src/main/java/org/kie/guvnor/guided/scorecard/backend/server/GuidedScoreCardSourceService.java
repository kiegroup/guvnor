/*
 * Copyright 2013 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.guvnor.guided.scorecard.backend.server;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

import org.dmg.pmml.pmml_4_1.descr.Attribute;
import org.dmg.pmml.pmml_4_1.descr.Characteristic;
import org.dmg.pmml.pmml_4_1.descr.Characteristics;
import org.dmg.pmml.pmml_4_1.descr.Extension;
import org.dmg.pmml.pmml_4_1.descr.FIELDUSAGETYPE;
import org.dmg.pmml.pmml_4_1.descr.INVALIDVALUETREATMENTMETHOD;
import org.dmg.pmml.pmml_4_1.descr.MiningField;
import org.dmg.pmml.pmml_4_1.descr.MiningSchema;
import org.dmg.pmml.pmml_4_1.descr.Output;
import org.dmg.pmml.pmml_4_1.descr.PMML;
import org.dmg.pmml.pmml_4_1.descr.Scorecard;
import org.drools.core.util.ArrayUtils;
import org.drools.scorecards.ScorecardCompiler;
import org.drools.scorecards.parser.xls.XLSKeywords;
import org.drools.scorecards.pmml.PMMLExtensionNames;
import org.drools.scorecards.pmml.PMMLGenerator;
import org.drools.scorecards.pmml.ScorecardPMMLUtils;
import org.kie.commons.java.nio.file.Path;
import org.kie.guvnor.commons.service.source.BaseSourceService;
import org.kie.guvnor.commons.service.source.SourceContext;
import org.kie.guvnor.guided.scorecard.model.ScoreCardModel;
import org.kie.guvnor.guided.scorecard.service.GuidedScoreCardEditorService;
import org.uberfire.backend.server.util.Paths;

public class GuidedScoreCardSourceService
        extends BaseSourceService<ScoreCardModel> {

    private static final String PATTERN = ".scgd";

    @Inject
    private Paths paths;

    @Inject
    private GuidedScoreCardEditorService guidedScoreCardEditorService;

    protected GuidedScoreCardSourceService() {
        super( "/src/main/resources" );
    }

    @Override
    public String getPattern() {
        return PATTERN;
    }

    @Override
    public SourceContext getSource( final Path path ) {
        //Load model and convert to DRL
        final ScoreCardModel model = guidedScoreCardEditorService.loadModel( paths.convert( path ) );
        final String drl = getSource(path, model);

        //Construct Source context. If the resource has DSL Sentences it needs to be a .dslr file
        String destinationPath = stripProjectPrefix( path );
        destinationPath = correctFileName( destinationPath,
                                           ".drl" );
        final ByteArrayInputStream is = new ByteArrayInputStream( drl.getBytes() );
        final BufferedInputStream bis = new BufferedInputStream( is );
        final SourceContext context = new SourceContext( bis,
                                                         destinationPath );
        return context;
    }

    @Override
    public String getSource(Path path, ScoreCardModel model) {
        return new StringBuilder()
                .append(returnPackageDeclaration(path)).append("\n")
                .append(model.getImports().toString()).append("\n")
                .append(getDRLBody(model)).toString();
    }

    private String getDRLBody(ScoreCardModel model) {
        final PMML pmml = createPMMLDocument(model);
        return ScorecardCompiler.convertToDRL(pmml,
                ScorecardCompiler.DrlType.EXTERNAL_OBJECT_MODEL);
    }

    private PMML createPMMLDocument( final ScoreCardModel model ) {
        final Scorecard pmmlScorecard = ScorecardPMMLUtils.createScorecard();
        final Output output = new Output();
        final Characteristics characteristics = new Characteristics();
        final MiningSchema miningSchema = new MiningSchema();

        Extension extension = new Extension();
        extension.setName( PMMLExtensionNames.SCORECARD_RESULTANT_SCORE_CLASS );
        extension.setValue( model.getFactName() );

        pmmlScorecard.getExtensionsAndCharacteristicsAndMiningSchemas().add( extension );

        extension = new Extension();
        extension.setName( PMMLExtensionNames.SCORECARD_IMPORTS );
        pmmlScorecard.getExtensionsAndCharacteristicsAndMiningSchemas().add( extension );
        List<String> imports = new ArrayList<String>();
        imports.add( model.getFactName() );
        StringBuilder importBuilder = new StringBuilder();
        importBuilder.append( model.getFactName() );

        for ( final org.kie.guvnor.guided.scorecard.model.Characteristic characteristic : model.getCharacteristics() ) {
            if ( !imports.contains( characteristic.getFact() ) ) {
                imports.add( characteristic.getFact() );
                importBuilder.append( "," ).append( characteristic.getFact() );
            }
        }
        imports.clear();
        extension.setValue( importBuilder.toString() );

        extension = new Extension();
        extension.setName( PMMLExtensionNames.SCORECARD_RESULTANT_SCORE_FIELD );
        extension.setValue( model.getFieldName() );
        pmmlScorecard.getExtensionsAndCharacteristicsAndMiningSchemas().add( extension );

        extension = new Extension();
        extension.setName( PMMLExtensionNames.SCORECARD_PACKAGE );
        extension.setValue( model.getPackageName() );
        pmmlScorecard.getExtensionsAndCharacteristicsAndMiningSchemas().add( extension );

        final String modelName = convertToJavaIdentifier( model.getName() );
        pmmlScorecard.setModelName( modelName );
        pmmlScorecard.setInitialScore( model.getInitialScore() );
        pmmlScorecard.setUseReasonCodes( model.isUseReasonCodes() );

        if ( model.isUseReasonCodes() ) {
            pmmlScorecard.setBaselineScore( model.getBaselineScore() );
            pmmlScorecard.setReasonCodeAlgorithm( model.getReasonCodesAlgorithm() );
        }

        for ( final org.kie.guvnor.guided.scorecard.model.Characteristic characteristic : model.getCharacteristics() ) {
            final Characteristic _characteristic = new Characteristic();
            characteristics.getCharacteristics().add( _characteristic );

            extension = new Extension();
            extension.setName( PMMLExtensionNames.CHARACTERTISTIC_EXTERNAL_CLASS );
            extension.setValue( characteristic.getFact() );
            _characteristic.getExtensions().add( extension );

            extension = new Extension();
            extension.setName( PMMLExtensionNames.CHARACTERTISTIC_DATATYPE );
            if ( "string".equalsIgnoreCase( characteristic.getDataType() ) ) {
                extension.setValue( XLSKeywords.DATATYPE_TEXT );
            } else if ( "int".equalsIgnoreCase( characteristic.getDataType() ) || "double".equalsIgnoreCase( characteristic.getDataType() ) ) {
                extension.setValue( XLSKeywords.DATATYPE_NUMBER );
            } else if ( "boolean".equalsIgnoreCase( characteristic.getDataType() ) ) {
                extension.setValue( XLSKeywords.DATATYPE_BOOLEAN );
            } else {
                System.out.println( ">>>> Found unknown data type :: " + characteristic.getDataType() );
            }
            _characteristic.getExtensions().add( extension );

            if ( model.isUseReasonCodes() ) {
                _characteristic.setBaselineScore( characteristic.getBaselineScore() );
                _characteristic.setReasonCode( characteristic.getReasonCode() );
            }
            _characteristic.setName( characteristic.getName() );

            final MiningField miningField = new MiningField();
            miningField.setName( characteristic.getField() );
            miningField.setUsageType( FIELDUSAGETYPE.ACTIVE );
            miningField.setInvalidValueTreatment( INVALIDVALUETREATMENTMETHOD.RETURN_INVALID );
            miningSchema.getMiningFields().add( miningField );

            extension = new Extension();
            extension.setName( PMMLExtensionNames.CHARACTERTISTIC_EXTERNAL_CLASS );
            extension.setValue( characteristic.getFact() );
            miningField.getExtensions().add( extension );

            final String[] numericOperators = new String[]{ "=", ">", "<", ">=", "<=" };
            for ( final org.kie.guvnor.guided.scorecard.model.Attribute attribute : characteristic.getAttributes() ) {
                final Attribute _attribute = new Attribute();
                _characteristic.getAttributes().add( _attribute );

                extension = new Extension();
                extension.setName( PMMLExtensionNames.CHARACTERTISTIC_FIELD );
                extension.setValue( characteristic.getField() );
                _attribute.getExtensions().add( extension );

                if ( model.isUseReasonCodes() ) {
                    _attribute.setReasonCode( attribute.getReasonCode() );
                }
                _attribute.setPartialScore( attribute.getPartialScore() );

                final String operator = attribute.getOperator();
                final String dataType = characteristic.getDataType();
                String predicateResolver;
                if ( "boolean".equalsIgnoreCase( dataType ) ) {
                    predicateResolver = operator.toUpperCase();
                } else if ( "String".equalsIgnoreCase( dataType ) ) {
                    if ( operator.contains( "=" ) ) {
                        predicateResolver = operator + attribute.getValue();
                    } else {
                        predicateResolver = attribute.getValue() + ",";
                    }
                } else {
                    if ( ArrayUtils.contains(numericOperators, operator) ) {
                        predicateResolver = operator + " " + attribute.getValue();
                    } else {
                        predicateResolver = attribute.getValue().replace( ",", "-" );
                    }
                }
                extension = new Extension();
                extension.setName( "predicateResolver" );
                extension.setValue( predicateResolver );
                _attribute.getExtensions().add( extension );
            }
        }

        pmmlScorecard.getExtensionsAndCharacteristicsAndMiningSchemas().add( miningSchema );
        pmmlScorecard.getExtensionsAndCharacteristicsAndMiningSchemas().add( output );
        pmmlScorecard.getExtensionsAndCharacteristicsAndMiningSchemas().add( characteristics );
        return new PMMLGenerator().generateDocument( pmmlScorecard );
    }

    private String convertToJavaIdentifier( final String modelName ) {
        final StringBuilder sb = new StringBuilder();
        if ( !Character.isJavaIdentifierStart( modelName.charAt( 0 ) ) ) {
            sb.append( "_" );
        }
        for ( char c : modelName.toCharArray() ) {
            if ( !Character.isJavaIdentifierPart( c ) ) {
                sb.append( "_" );
            } else {
                sb.append( c );
            }
        }
        return sb.toString();
    }
}
