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

package org.drools.guvnor.server.standalonededitor;

import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.rpc.DetailedSerializationException;
import org.drools.guvnor.client.rpc.MetaData;
import org.drools.guvnor.client.rpc.RuleAsset;
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

    public BRLRuleAssetProvider(String packageName,
                                String[] initialBRLs) {
        this.packageName = packageName;
        this.initialBRLs = initialBRLs;
    }

    public RuleAsset[] getRuleAssets() throws DetailedSerializationException {

        List<RuleModel> models = new ArrayList<RuleModel>(initialBRLs.length);
        List<RuleAsset> assets = new ArrayList<RuleAsset>(initialBRLs.length);

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
            for (RuleAsset ruleAsset : assets) {
                this.getAssetService().removeAsset(ruleAsset.getUuid());
            }

            if (e instanceof DetailedSerializationException) {
                throw (DetailedSerializationException) e;
            }

            throw new DetailedSerializationException("Error creating assets",
                    e.getMessage());
        }

        return assets.toArray(new RuleAsset[assets.size()]);
    }

    private RuleAsset createAsset(RuleModel ruleModel) {
        RuleAsset asset = new RuleAsset();

        asset.setUuid("mock-"+UUID.randomUUID().toString());
        asset.setContent(ruleModel);
        asset.setName(ruleModel.name);
        asset.setMetaData(createMetaData());

        return asset;
    }

    private MetaData createMetaData() {
        MetaData metaData = new MetaData();

        metaData.setPackageName(packageName);
        metaData.setFormat(AssetFormats.BUSINESS_RULE);

        metaData.setPackageUUID("mock");

        return metaData;
    }

    private RepositoryAssetService getAssetService() {
        return RepositoryServiceServlet.getAssetService();
    }

}
