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

package org.drools.guvnor.server.standalonededitor;

import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.rpc.DetailedSerializationException;
import org.drools.guvnor.client.rpc.MetaData;
import org.drools.guvnor.client.rpc.Asset;
import org.drools.guvnor.server.RepositoryAssetService;
import org.drools.guvnor.server.RepositoryServiceServlet;
import org.drools.ide.common.client.modeldriven.brl.RuleModel;
import org.drools.ide.common.server.util.BRXMLPersistence;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * BRL -> RuleAsset converter used by standalone editor.
 * For each brl provided, a new RuleAsset is created. The name of the RuleAsset
 * will be the name present in the brl concatenated with a unique number.
 */
public class BRLRuleAssetProvider
        implements
        RuleAssetProvider {

    private final String packageName;
    private final String[] initialBRLs;

    private final RepositoryAssetService repositoryAssetService;

    public BRLRuleAssetProvider(String packageName,
                                String[] initialBRLs, RepositoryAssetService repositoryAssetService) {
        this.packageName = packageName;
        this.initialBRLs = initialBRLs;
        this.repositoryAssetService = repositoryAssetService;
    }

    public Asset[] getRuleAssets() throws DetailedSerializationException {

        List<RuleModel> models = new ArrayList<RuleModel>(initialBRLs.length);
        List<Asset> assets = new ArrayList<Asset>(initialBRLs.length);

        //We wan't to avoid inconsistent states, that is why we first unmarshal
        //each brl and then (if nothing fails) create each rule
        for (String brl : initialBRLs) {
            //convert the BRL to RuleModel
            models.add(BRXMLPersistence.getInstance().unmarshal(brl));
        }

        //no unmarshal errors, it's time to create the rules
        try {
            for (RuleModel ruleModel : models) {
                assets.add(this.createAsset(ruleModel));
            }
        } catch (Exception e) {
            //if something failed, delete the generated assets
            for (Asset ruleAsset : assets) {
                repositoryAssetService.removeAsset(ruleAsset.getUuid());
            }

            if (e instanceof DetailedSerializationException) {
                throw (DetailedSerializationException) e;
            }

            throw new DetailedSerializationException("Error creating assets",
                    e.getMessage());
        }

        return assets.toArray(new Asset[assets.size()]);
    }

    private Asset createAsset(RuleModel ruleModel) {
        Asset asset = new Asset();

        asset.setUuid("mock-" + UUID.randomUUID().toString());
        asset.setContent(ruleModel);
        asset.setName(ruleModel.name);
        asset.setFormat(AssetFormats.BUSINESS_RULE);
        asset.setMetaData(createMetaData());
        asset.setState("temporal");

        return asset;
    }

    private MetaData createMetaData() {
        MetaData metaData = new MetaData();

        metaData.setModuleName(packageName);

        metaData.setModuleUUID("mock");

        return metaData;
    }

}
