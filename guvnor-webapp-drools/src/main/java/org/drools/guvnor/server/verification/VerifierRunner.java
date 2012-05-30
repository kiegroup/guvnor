/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.guvnor.server.verification;

import org.drools.builder.DecisionTableConfiguration;
import org.drools.builder.DecisionTableInputType;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.rpc.AnalysisReport;
import org.drools.guvnor.server.builder.AssetValidationIterator;
import org.drools.guvnor.server.contenthandler.ContentHandler;
import org.drools.guvnor.server.contenthandler.ContentManager;
import org.drools.guvnor.server.contenthandler.IRuleAsset;
import org.drools.guvnor.server.util.DroolsHeader;
import org.drools.io.ResourceFactory;
import org.drools.repository.AssetItem;
import org.drools.repository.ModuleItem;
import org.drools.verifier.Verifier;
import org.drools.verifier.VerifierError;
import org.drools.verifier.builder.ScopesAgendaFilter;
import org.drools.verifier.data.VerifierReport;

import java.io.StringReader;

public abstract class VerifierRunner {

    private final Verifier verifier;
    protected final ModuleItem packageItem;

    public VerifierRunner(Verifier verifier,
                          ModuleItem packageItem) {
        this.verifier = verifier;
        this.packageItem = packageItem;
    }

    public AnalysisReport verify() {

        addHeaderToVerifier();

        addToVerifier(listAssetsByFormat(AssetFormats.DSL),
                ResourceType.DSL);

        // TODO: Model JARS

        addToVerifier(listAssetsByFormat(AssetFormats.DRL_MODEL),
                ResourceType.DRL);

        addToVerifier(listAssetsByFormat(AssetFormats.FUNCTION),
                ResourceType.DRL);

        addToVerifier(listAssetsByFormat(AssetFormats.DSL_TEMPLATE_RULE),
                ResourceType.DSLR);

        addToVerifier(listAssetsByFormat(AssetFormats.DECISION_SPREADSHEET_XLS),
                ResourceType.DTABLE);

        addGuidedDecisionTablesToVerifier();

        addRuleTemplatesToVerifier();

        addDRLRulesToVerifier();

        addToVerifier(listAssetsByFormat(AssetFormats.BUSINESS_RULE),
                ResourceType.BRL);

        fireAnalysis();

        VerifierReport report = verifier.getResult();

        return VerifierReportCreator.doReport(report);
    }

    protected void fireAnalysis() throws RuntimeException {

        verifier.fireAnalysis(getScopesAgendaFilter());

        if (verifier.hasErrors()) {
            StringBuilder message = new StringBuilder("Verifier Errors:\n");
            for (VerifierError verifierError : verifier.getErrors()) {
                message.append("\t");
                message.append(verifierError.getMessage());
                message.append("\n");
            }
            throw new RuntimeException(message.toString());
        }
    }

    protected abstract ScopesAgendaFilter getScopesAgendaFilter();

    protected void addHeaderToVerifier() {
        StringBuilder header = new StringBuilder();
        header.append("package ").append(packageItem.getName()).append("\n");
        header.append(DroolsHeader.getDroolsHeader(packageItem)).append("\n");

        verifier.addResourcesToVerify(
                ResourceFactory.newReaderResource(new StringReader(header.toString())),
                ResourceType.DRL);

    }

    private void addGuidedDecisionTablesToVerifier() {
        addToVerifier(listAssetsByFormat(AssetFormats.DECISION_TABLE_GUIDED));
    }

    private void addDRLRulesToVerifier() {
        addToVerifier(listAssetsByFormat(AssetFormats.DRL));
    }

    private void addRuleTemplatesToVerifier() {
        addToVerifier(listAssetsByFormat(AssetFormats.RULE_TEMPLATE));
    }

    protected abstract AssetValidationIterator listAssetsByFormat(String format);

    private void addToVerifier(AssetValidationIterator assets,
                               ResourceType resourceType) {
        while (assets.hasNext()) {
            AssetItem asset = assets.next();
            if (!asset.isArchived() && !asset.getDisabled()) {
                if (resourceType == ResourceType.DTABLE) {
                    DecisionTableConfiguration dtableconfiguration = KnowledgeBuilderFactory.newDecisionTableConfiguration();
                    dtableconfiguration.setInputType(DecisionTableInputType.XLS);

                    verifier.addResourcesToVerify(
                            ResourceFactory.newByteArrayResource(asset.getBinaryContentAsBytes()),
                            resourceType,
                            dtableconfiguration);
                } else {
                    verifier.addResourcesToVerify(
                            ResourceFactory.newReaderResource(new StringReader(asset.getContent())),
                            resourceType);
                }
            }
        }
    }

    private void addToVerifier(AssetValidationIterator assetItemIterator) {
        while (assetItemIterator.hasNext()) {
            AssetItem assetItem = assetItemIterator.next();

            ContentHandler contentHandler = ContentManager.getHandler(assetItem.getFormat());
            if (contentHandler.isRuleAsset()) {
                IRuleAsset ruleAsset = (IRuleAsset) contentHandler;
                String drl = ruleAsset.getRawDRL(assetItem);
                verifier.addResourcesToVerify(
                        ResourceFactory.newReaderResource(new StringReader(drl)),
                        ResourceType.DRL);

            }
        }
    }
}
