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
import org.drools.guvnor.client.rpc.DetailedSerializationException;
import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.guvnor.server.RepositoryAssetService;
import org.drools.guvnor.server.RepositoryServiceServlet;

/**
 * Creates a new RuleAsset.
 */
public class UUIDRuleAssetProvider
        implements
        RuleAssetProvider {

    private final String[] assetsUUIDs;

    private final RepositoryAssetService repositoryAssetService;

    public UUIDRuleAssetProvider(String[] assetsUUIDs, RepositoryAssetService repositoryAssetService) {
        this.assetsUUIDs = assetsUUIDs;
        this.repositoryAssetService = repositoryAssetService;
    }

    public RuleAsset[] getRuleAssets() throws DetailedSerializationException {
        try {

            RuleAsset[] assets = new RuleAsset[assetsUUIDs.length];

            for (int i = 0; i < assetsUUIDs.length; i++) {
                String uuid = assetsUUIDs[i];
                assets[i] = repositoryAssetService.loadRuleAsset(uuid);
            }

            return assets;
        } catch (SerializationException ex) {
            throw new DetailedSerializationException("Error creating rule asset",
                    ex.getMessage());
        }

    }

}
