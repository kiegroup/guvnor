/*
 * Copyright 2012 JBoss Inc
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
package org.drools.guvnor.server.contenthandler.drools;

import com.google.gwt.user.client.rpc.SerializationException;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.dmg.pmml.pmml_4_1.descr.*;
import org.drools.compiler.DroolsParserException;
import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.rpc.Asset;
import org.drools.guvnor.client.rpc.BuilderResult;
import org.drools.guvnor.client.rpc.BuilderResultLine;
import org.drools.guvnor.server.builder.AssemblyErrorLogger;
import org.drools.guvnor.server.builder.BRMSPackageBuilder;
import org.drools.guvnor.server.contenthandler.ContentHandler;
import org.drools.guvnor.server.contenthandler.IHasCustomValidator;
import org.drools.guvnor.server.contenthandler.IRuleAsset;
import org.drools.ide.common.client.modeldriven.scorecards.ScorecardModel;
import org.drools.ide.common.server.util.ScorecardsXMLPersistence;
import org.drools.repository.AssetItem;
import org.drools.scorecards.ScorecardCompiler;
import org.drools.scorecards.parser.xls.XLSKeywords;
import org.drools.scorecards.pmml.PMMLExtensionNames;
import org.drools.scorecards.pmml.PMMLGenerator;
import org.drools.scorecards.pmml.ScorecardPMMLUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ScorecardsContentHandler extends ContentHandler
        implements
        IRuleAsset, IHasCustomValidator {

    public void retrieveAssetContent(Asset asset, AssetItem item) throws SerializationException {
        ScorecardModel model = ScorecardsXMLPersistence.getInstance().unmarshall(item.getContent());
        asset.setContent(model);
    }

    public void storeAssetContent(Asset asset, AssetItem repoAsset) throws SerializationException {
        ScorecardModel model = (ScorecardModel) asset.getContent();
        repoAsset.updateContent(ScorecardsXMLPersistence.getInstance().marshal(model));
    }

    public void assembleDRL(BRMSPackageBuilder builder, AssetItem assetItem, StringBuilder stringBuilder) {
        ScorecardModel model = ScorecardsXMLPersistence.getInstance().unmarshall(assetItem.getContent());
        BuilderResult validationResult = validateScorecard(model);
        if ( !validationResult.hasLines() ) {
            String drl = getDrlFromScorecardModel(model);
            stringBuilder.append(drl);
        }
    }

    public void assembleDRL(BRMSPackageBuilder builder, Asset asset, StringBuilder stringBuilder) {
        //called by View Source
        ScorecardModel model = (ScorecardModel) asset.getContent();
        BuilderResult validationResult = validateScorecard(model);
        if ( validationResult.hasLines() ) {
            if (StringUtils.isBlank(model.getName())) {
                stringBuilder.append("//Guided Scorecard has errors, Click on Source->Validate to view exact errors!");
            } else {
                stringBuilder.append("//Guided Scorecard ('"+model.getName()+"') has errors, Click on Source->Validate to view exact errors!");
            }
            return;
        }
        String drl = getDrlFromScorecardModel(model);
        stringBuilder.append(drl);
    }

    public String getRawDRL(AssetItem assetItem) {
        ScorecardModel model = ScorecardsXMLPersistence.getInstance().unmarshall(assetItem.getContent());
        BuilderResult validationResult = validateScorecard(model);
        if ( !validationResult.hasLines() ) {
            return getDrlFromScorecardModel(model);
        } else {
            return "";
        }
    }

    public void compile(BRMSPackageBuilder builder, AssetItem asset, AssemblyErrorLogger logger) throws DroolsParserException, IOException {
        //should never get called as we implement IHasCustomValidator
        System.out.println(">>>compile() method called!");
    }

    protected String getDrlFromScorecardModel(ScorecardModel model) {
        PMML pmml = createPMMLDocument(model);
        return ScorecardCompiler.convertToDRL(pmml, ScorecardCompiler.DrlType.EXTERNAL_OBJECT_MODEL);
    }

    private PMML createPMMLDocument(ScorecardModel model) {
        Scorecard pmmlScorecard = ScorecardPMMLUtils.createScorecard();
        Output output = new Output();
        Characteristics characteristics = new Characteristics();
        MiningSchema miningSchema = new MiningSchema();

        Extension extension = new Extension();
        extension.setName(PMMLExtensionNames.SCORECARD_RESULTANT_SCORE_CLASS);
        extension.setValue(model.getFactName());

        pmmlScorecard.getExtensionsAndCharacteristicsAndMiningSchemas().add(extension);

        extension = new Extension();
        extension.setName(PMMLExtensionNames.SCORECARD_IMPORTS);
        pmmlScorecard.getExtensionsAndCharacteristicsAndMiningSchemas().add(extension);
        List<String> imports = new ArrayList<String>();
        imports.add(model.getFactName());
        StringBuilder importBuilder = new StringBuilder();
        importBuilder.append(model.getFactName());
        for (org.drools.ide.common.client.modeldriven.scorecards.Characteristic characteristic : model.getCharacteristics()){
            if (!imports.contains(characteristic.getFact())){
                imports.add(characteristic.getFact());
                importBuilder.append(",").append(characteristic.getFact());
            }
        }
        imports.clear();
        extension.setValue(importBuilder.toString());

        extension = new Extension();
        extension.setName(PMMLExtensionNames.SCORECARD_RESULTANT_SCORE_FIELD);
        extension.setValue(model.getFieldName());
        pmmlScorecard.getExtensionsAndCharacteristicsAndMiningSchemas().add(extension);


        extension = new Extension();
        extension.setName(PMMLExtensionNames.SCORECARD_PACKAGE);
        extension.setValue(model.getPackageName());
        pmmlScorecard.getExtensionsAndCharacteristicsAndMiningSchemas().add(extension);

        String modelName = convertToJavaIdentifier(model.getName());
        pmmlScorecard.setModelName(modelName);
        pmmlScorecard.setInitialScore(model.getInitialScore());
        pmmlScorecard.setUseReasonCodes(model.isUseReasonCodes());

        if (model.isUseReasonCodes()) {
            pmmlScorecard.setBaselineScore(model.getBaselineScore());
            pmmlScorecard.setReasonCodeAlgorithm(model.getReasonCodesAlgorithm());
        }

        for (org.drools.ide.common.client.modeldriven.scorecards.Characteristic characteristic : model.getCharacteristics()){
            Characteristic _characteristic = new Characteristic();
            characteristics.getCharacteristics().add(_characteristic);

            extension = new Extension();
            extension.setName(PMMLExtensionNames.CHARACTERTISTIC_EXTERNAL_CLASS);
            extension.setValue(characteristic.getFact());
            _characteristic.getExtensions().add(extension);

            extension = new Extension();
            extension.setName(PMMLExtensionNames.CHARACTERTISTIC_DATATYPE);
            if ("string".equalsIgnoreCase(characteristic.getDataType())) {
                extension.setValue(XLSKeywords.DATATYPE_TEXT);
            } else if ("int".equalsIgnoreCase(characteristic.getDataType()) || "double".equalsIgnoreCase(characteristic.getDataType())) {
                extension.setValue(XLSKeywords.DATATYPE_NUMBER);
            } else if ("boolean".equalsIgnoreCase(characteristic.getDataType())) {
                extension.setValue(XLSKeywords.DATATYPE_BOOLEAN);
            } else {
                System.out.println(">>>> Found unknown data type :: "+characteristic.getDataType());
            }
            _characteristic.getExtensions().add(extension);

            if (model.isUseReasonCodes() ) {
                _characteristic.setBaselineScore(characteristic.getBaselineScore());
                _characteristic.setReasonCode(characteristic.getReasonCode());
            }
            _characteristic.setName(characteristic.getName());

            MiningField miningField = new MiningField();
            miningField.setName(characteristic.getField());
            miningField.setUsageType(FIELDUSAGETYPE.ACTIVE);
            miningField.setInvalidValueTreatment(INVALIDVALUETREATMENTMETHOD.RETURN_INVALID);
            miningSchema.getMiningFields().add(miningField);

            extension = new Extension();
            extension.setName(PMMLExtensionNames.CHARACTERTISTIC_EXTERNAL_CLASS);
            extension.setValue(characteristic.getFact());
            miningField.getExtensions().add(extension);

            String[] numericOperators = new String[]{"=", ">", "<", ">=", "<="};
            for (org.drools.ide.common.client.modeldriven.scorecards.Attribute attribute : characteristic.getAttributes()) {
                Attribute _attribute = new Attribute();
                _characteristic.getAttributes().add(_attribute);

                extension = new Extension();
                extension.setName(PMMLExtensionNames.CHARACTERTISTIC_FIELD);
                extension.setValue(characteristic.getField());
                _attribute.getExtensions().add(extension);

                if ( model.isUseReasonCodes() ) {
                    _attribute.setReasonCode(attribute.getReasonCode());
                }
                _attribute.setPartialScore(attribute.getPartialScore());

                String operator = attribute.getOperator();
                String predicateResolver;
                String dataType = characteristic.getDataType();
                if ("boolean".equalsIgnoreCase(dataType)) {
                    predicateResolver = operator.toUpperCase();
                } else if ("String".equalsIgnoreCase(dataType)) {
                    if (operator.contains("=")) {
                        predicateResolver = operator+attribute.getValue();
                    } else {
                        predicateResolver = attribute.getValue()+",";
                    }
                } else {
                    if (ArrayUtils.contains(numericOperators, operator)) {
                        predicateResolver = operator+" "+attribute.getValue();
                    } else {
                        predicateResolver = attribute.getValue().replace(",","-");
                    }
                }
                extension = new Extension();
                extension.setName("predicateResolver");
                extension.setValue(predicateResolver);
                _attribute.getExtensions().add(extension);
            }
        }

        pmmlScorecard.getExtensionsAndCharacteristicsAndMiningSchemas().add(miningSchema);
        pmmlScorecard.getExtensionsAndCharacteristicsAndMiningSchemas().add(output);
        pmmlScorecard.getExtensionsAndCharacteristicsAndMiningSchemas().add(characteristics);
        return new PMMLGenerator().generateDocument(pmmlScorecard);
    }

    private String convertToJavaIdentifier(String modelName) {
        StringBuilder sb = new StringBuilder();
        if(!Character.isJavaIdentifierStart(modelName.charAt(0))) {
            sb.append("_");
        }
        for (char c : modelName.toCharArray()) {
            if(!Character.isJavaIdentifierPart(c)) {
                sb.append("_");
            } else {
                sb.append(c);
            }
        }
        modelName = sb.toString();
        return modelName;
    }

    public BuilderResult validateAsset(AssetItem assetItem) {
        System.out.println(">>> validateAsset() method.");
        ScorecardModel model = ScorecardsXMLPersistence.getInstance().unmarshall(assetItem.getContent());
        BuilderResult builderResult = validateScorecard(model);
        return builderResult;
    }

    private BuilderResult validateScorecard(ScorecardModel model) {
        BuilderResult builderResult = new BuilderResult();
        if ( StringUtils.isBlank(model.getFactName())){
            builderResult.addLine(createBuilderResultLine("Fact Name is empty.", "Setup Parameters"));
        }
        if ( StringUtils.isBlank(model.getFieldName())){
            builderResult.addLine(createBuilderResultLine("Resultant Score Field is empty.", "Setup Parameters"));
        }
        if (model.getCharacteristics().size() == 0 ) {
            builderResult.addLine(createBuilderResultLine("No Characteristics Found.", "Characteristics"));
        }
        int ctr = 1;
        for (org.drools.ide.common.client.modeldriven.scorecards.Characteristic c : model.getCharacteristics()) {
            String characteristicName = "Characteristic ('#"+ctr+"')";
            if (StringUtils.isBlank(c.getName())){
                builderResult.addLine(createBuilderResultLine("Name is empty.", characteristicName));
            } else {
                characteristicName = "Characteristic ('"+c.getName()+"')";
            }
            if ( StringUtils.isBlank(c.getFact())){
                builderResult.addLine(createBuilderResultLine("Fact is empty.", characteristicName));
            }
            if ( StringUtils.isBlank(c.getField())){
                builderResult.addLine(createBuilderResultLine("Characteristic Field is empty.", characteristicName));
            } else  if ( StringUtils.isBlank(c.getDataType())){
                builderResult.addLine(createBuilderResultLine("Internal Error (missing datatype).", characteristicName));
            }
            if (c.getAttributes().size() == 0 ) {
                builderResult.addLine(createBuilderResultLine("No Attributes Found.", characteristicName));
            }
            if (model.isUseReasonCodes()){
                if (StringUtils.isBlank(model.getReasonCodeField())){
                    builderResult.addLine(createBuilderResultLine("Resultant Reason Codes Field is empty.", characteristicName));
                }
                if (!"none".equalsIgnoreCase(model.getReasonCodesAlgorithm())){
                    builderResult.addLine(createBuilderResultLine("Baseline Score is not specified.", characteristicName));
                }
            }
            int attrCtr = 1;
            for (org.drools.ide.common.client.modeldriven.scorecards.Attribute attribute : c.getAttributes()){
                String attributeName = "Attribute ('#"+attrCtr+"')";
                if (StringUtils.isBlank(attribute.getOperator())){
                    builderResult.addLine(createBuilderResultLine("Attribute Operator is empty.", attributeName));
                }
                if (StringUtils.isBlank(attribute.getValue())){
                    builderResult.addLine(createBuilderResultLine("Attribute Value is empty.", attributeName));
                }
                if (model.isUseReasonCodes()){
                    if (StringUtils.isBlank(c.getReasonCode())){
                        if (StringUtils.isBlank(attribute.getReasonCode())){
                            builderResult.addLine(createBuilderResultLine("Reasoncode must be set at either attribute or characteristic.", attributeName));
                        }
                    }
                }
                attrCtr++;
            }
            ctr++;
        }
        return builderResult;
    }

    public boolean validate(AssetItem assetItem) {
        System.out.println(">>> validate() method;");
        return true;
    }

    public BuilderResultLine createBuilderResultLine(String msg, String name){
        return new BuilderResultLine().setMessage(msg).setAssetFormat(getFormat()).setAssetName(name);
    }

    public String getFormat() {
        return AssetFormats.SCORECARD_GUIDED;
    }
}
