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

import com.google.gwt.user.client.rpc.SerializationException;
import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.rpc.DetailedSerializationException;
import org.drools.guvnor.client.rpc.Asset;
import org.drools.guvnor.server.RepositoryAssetService;
import org.drools.guvnor.server.RepositoryServiceServlet;
import org.drools.guvnor.server.ServiceImplementation;

/**
 * Creates a new RuleAsset.
 */
public class NewRuleAssetProvider implements RuleAssetProvider {

    private final String packageName;
    private final String categoryName;
    private final String assetName;
    private final String assetFormat;

    private final ServiceImplementation serviceImplementation;
    private final RepositoryAssetService repositoryAssetService;

    public NewRuleAssetProvider(String packageName, String categoryName, String assetName, String assetFormat,
            ServiceImplementation serviceImplementation, RepositoryAssetService repositoryAssetService) {
        this.packageName = packageName;
        this.categoryName = categoryName;
        this.assetName = assetName;
        this.assetFormat = assetFormat != null ? assetFormat : AssetFormats.BUSINESS_RULE;
        this.serviceImplementation = serviceImplementation;
        this.repositoryAssetService = repositoryAssetService;
    }

    public Asset[] getRuleAssets() throws DetailedSerializationException {
        try {
            //creates a new empty asset with the given name and format in the
            //given package.
            String ruleUUID = serviceImplementation.createNewRule(assetName, "created by standalone editor", categoryName, packageName, this.assetFormat);
            Asset newRule = repositoryAssetService.loadRuleAsset(ruleUUID);

            return new Asset[]{newRule};
        } catch (SerializationException ex) {
            throw new DetailedSerializationException("Error creating rule asset", ex.getMessage());
        }

    }

}
