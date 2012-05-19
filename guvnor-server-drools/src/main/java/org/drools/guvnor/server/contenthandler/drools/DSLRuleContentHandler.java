/*
 * Copyright 2005 JBoss Inc
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

package org.drools.guvnor.server.contenthandler.drools;

import com.google.gwt.user.client.rpc.SerializationException;
import org.drools.compiler.DroolsParserException;
import org.drools.guvnor.client.rpc.Asset;
import org.drools.guvnor.client.rpc.RuleContentText;
import org.drools.guvnor.server.builder.AssemblyErrorLogger;
import org.drools.guvnor.server.builder.BRMSPackageBuilder;
import org.drools.guvnor.server.builder.ContentAssemblyError;
import org.drools.guvnor.server.contenthandler.ContentHandler;
import org.drools.guvnor.server.contenthandler.IRuleAsset;
import org.drools.lang.ExpanderException;
import org.drools.lang.dsl.DefaultExpander;
import org.drools.repository.AssetItem;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

public class DSLRuleContentHandler extends ContentHandler
        implements
        IRuleAsset {

    public void retrieveAssetContent(Asset asset,
                                     AssetItem item) throws SerializationException {
        RuleContentText text = new RuleContentText();
        text.content = item.getContent();

        asset.setContent(text);

    }

    public void storeAssetContent(Asset asset,
                                  AssetItem repoAsset) throws SerializationException {

        RuleContentText text = (RuleContentText) asset.getContent();
        repoAsset.updateContent(text.content);

    }

    public void compile(BRMSPackageBuilder builder,
                        AssetItem asset,
                        AssemblyErrorLogger logger) throws DroolsParserException,
            IOException {
        DefaultExpander expander = getExpander(builder,
                asset,
                logger);

        String source = getRawDRL(asset);

        //expand and check for errors
        String drl = expander.expand(source);

        if (expander.hasErrors()) {
            List<ExpanderException> exErrs = expander.getErrors();
            for (ExpanderException ex : exErrs) {
                logger.logError(new ContentAssemblyError(
                        ex.getMessage(), asset.getFormat(), asset.getName(), asset.getUUID(), false, true));
            }
            return;
        }

        builder.addPackageFromDrl(new StringReader(drl));
    }

    private DefaultExpander getExpander(BRMSPackageBuilder builder,
                                        AssetItem asset,
                                        AssemblyErrorLogger logger) {

        if (!builder.hasDSL()) {
            logger.logError(new ContentAssemblyError(
                    "This rule asset requires a DSL, yet none were configured in the package.", asset.getFormat(), asset.getName(), asset.getUUID(), false, true));
        }

        return builder.getDSLExpander();
    }

    public void assembleDRL(BRMSPackageBuilder builder,
                            Asset asset,
                            StringBuilder stringBuilder) {
        RuleContentText text = (RuleContentText) asset.getContent();
        String source = text.content;

        source = getDRL(source,
                asset.getName(),
                null);

        DefaultExpander expander = builder.getDSLExpander();
        stringBuilder.append(expander.expand(source));
    }

    public void assembleDRL(BRMSPackageBuilder builder,
                            AssetItem asset,
                            StringBuilder stringBuilder) {
        //add the rule keyword if its 'stand alone'
        String source = getRawDRL(asset);

        DefaultExpander expander = builder.getDSLExpander();
        stringBuilder.append(expander.expand(source));

    }

    private String wrapRule(String assetName,
                            String parentName,
                            String source) {
        if (parentName == null || "".equals(parentName)) {
            return "rule '" + assetName + "' \n" + source + "\nend";
        } else {
            return "rule '" + assetName + "' extends " + parentName + " \n" + source + "\nend";

        }
    }

    public String getRawDRL(AssetItem asset) {
        String source = asset.getContent();
        String parentName = this.parentNameFromCategory(asset,
                "");
        source = getDRL(source,
                asset.getName(),
                parentName);

        return source;
    }

    public String getDRL(String source,
                         String assetName,
                         String parentName) {
        if (DRLFileContentHandler.isStandAloneRule(source)) {
            source = wrapRule(assetName,
                    parentName,
                    source);
        }

        return source;
    }

}
