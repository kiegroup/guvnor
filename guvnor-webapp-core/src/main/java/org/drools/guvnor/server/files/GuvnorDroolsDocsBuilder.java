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

package org.drools.guvnor.server.files;

import org.drools.compiler.DroolsParserException;
import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.server.builder.BRMSPackageBuilder;
import org.drools.guvnor.server.builder.DSLLoader;
import org.drools.guvnor.server.contenthandler.ContentHandler;
import org.drools.guvnor.server.contenthandler.ContentManager;
import org.drools.guvnor.server.contenthandler.IRuleAsset;
import org.drools.guvnor.server.util.DroolsHeader;
import org.drools.repository.AssetItem;
import org.drools.repository.CategoryItem;
import org.drools.repository.PackageItem;
import org.drools.repository.VersionableItem;
import org.drools.verifier.doc.DroolsDocsBuilder;
import org.drools.verifier.misc.DrlPackageParser;
import org.drools.verifier.misc.DrlRuleParser;

import java.text.Format;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class GuvnorDroolsDocsBuilder extends DroolsDocsBuilder {

    private static final List<String> formats = new ArrayList<String>();

    static {
        formats.add(AssetFormats.DRL);
        formats.add(AssetFormats.BUSINESS_RULE);
        formats.add(AssetFormats.DECISION_SPREADSHEET_XLS);
        formats.add(AssetFormats.RULE_TEMPLATE);
        formats.add(AssetFormats.DECISION_TABLE_GUIDED);
    }

    private GuvnorDroolsDocsBuilder(PackageItem packageItem) throws DroolsParserException {
        super(createDrlPackageData(packageItem));
    }

    protected static DrlPackageParser createDrlPackageData(PackageItem packageItem) {

        List<DrlRuleParser> rules = new ArrayList<DrlRuleParser>();

        // Get And Fill Rule Data
        Iterator<AssetItem> assets = packageItem.getAssets();
        while (assets.hasNext()) {

            AssetItem assetItem = assets.next();

            if (formats.contains(assetItem.getFormat()) && !assetItem.getDisabled() && !assetItem.isArchived()) {

                String drl = getDRL(assetItem);

                if (drl != null) {

                    List<String> categories = new ArrayList<String>();

                    for (CategoryItem categoryItem : assetItem.getCategories()) {
                        categories.add(categoryItem.getName());
                    }

                    List<DrlRuleParser> ruleDataList = DrlRuleParser.findRulesDataFromDrl(drl);

                    for (DrlRuleParser ruleData : ruleDataList) {
                        ruleData.getOtherInformation().put("Categories",
                                categories);
                        ruleData.getMetadata().addAll(createMetaData(assetItem));
                        rules.add(ruleData);
                    }
                }
            }
        }

        String header = DroolsHeader.getDroolsHeader(packageItem);
        List<String> globals = DrlPackageParser.findGlobals(header);

        // Get And Fill Package Data
        return new DrlPackageParser(packageItem.getName(),
                packageItem.getDescription(),
                rules,
                globals,
                createMetaData(packageItem),
                new HashMap<String, List<String>>());

    }

    private static List<String> createMetaData(VersionableItem versionableItem) {
        List<String> list = new ArrayList<String>();

        Format formatter = getFormatter();

        list.add("Creator :" + versionableItem.getCreator());
        list.add("Created date :" + formatter.format(versionableItem.getCreatedDate().getTime()));
        list.add("Last contributor :" + versionableItem.getLastContributor());
        list.add("Last modified :" + formatter.format(versionableItem.getLastModified().getTime()));
        list.add("Description :" + versionableItem.getDescription());

        return list;
    }

    public static GuvnorDroolsDocsBuilder getInstance(PackageItem packageItem) throws DroolsParserException {
        return new GuvnorDroolsDocsBuilder(packageItem);
    }

    private static String getDRL(AssetItem item) {
        ContentHandler handler = ContentManager.getHandler(item.getFormat());

        if (!handler.isRuleAsset()) {
            return null;
        }

        StringBuilder stringBuilder = new StringBuilder();
        BRMSPackageBuilder builder = new BRMSPackageBuilder();
        builder.setDSLFiles(DSLLoader.loadDSLMappingFiles(item.getPackage()));
        ((IRuleAsset) handler).assembleDRL(builder,
                item,
                stringBuilder);

        return stringBuilder.toString();
    }
}
