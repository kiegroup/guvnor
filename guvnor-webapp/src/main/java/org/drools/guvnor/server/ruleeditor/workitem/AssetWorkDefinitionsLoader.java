/*
 * Copyright 2011 JBoss Inc
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

package org.drools.guvnor.server.ruleeditor.workitem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.drools.guvnor.client.rpc.AssetPageRequest;
import org.drools.guvnor.client.rpc.AssetPageRow;
import org.drools.guvnor.client.rpc.PageResponse;
import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.guvnor.client.rpc.RuleContentText;
import org.drools.guvnor.server.RepositoryAssetService;

import com.google.gwt.user.client.rpc.SerializationException;

/**
 * Class to load Work Definitions from a Guvnor Package
 */
public class AssetWorkDefinitionsLoader extends AbstractWorkDefinitionsLoader {

    private RepositoryAssetService repositoryAssetService;

    private String                 packageUUID;

    public AssetWorkDefinitionsLoader(RepositoryAssetService repositoryAssetService,
                                      String packageUUID) {
        this.repositoryAssetService = repositoryAssetService;
        this.packageUUID = packageUUID;
    }

    public List<String> loadWorkDefinitions() throws SerializationException {

        //Load assets from package
        AssetPageRequest workDefinitionAssetRequest = new AssetPageRequest( packageUUID,
                                                                            Arrays.asList( new String[]{"wid"} ),
                                                                            null,
                                                                            0,
                                                                            null );
        PageResponse<AssetPageRow> assetWorkDefinitions = repositoryAssetService.findAssetPage( workDefinitionAssetRequest );

        //Add individual assets to definitions list
        List<String> definitions = new ArrayList<String>();
        for ( AssetPageRow row : assetWorkDefinitions.getPageRowList() ) {
            RuleAsset asset = repositoryAssetService.loadRuleAsset( row.getUuid() );
            RuleContentText content = (RuleContentText) asset.getContent();
            definitions.add( content.content );
        }

        return definitions;
    }

}
