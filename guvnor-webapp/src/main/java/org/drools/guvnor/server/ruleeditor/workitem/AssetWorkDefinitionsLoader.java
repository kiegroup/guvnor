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

import java.util.Arrays;

import javax.inject.Inject;

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

    @Inject
    private RepositoryAssetService      repositoryAssetService;
    
    private String packageUUID;

    public AssetWorkDefinitionsLoader() {}
    
    public AssetWorkDefinitionsLoader(String packageUUID) {
        this.packageUUID = packageUUID;
    }

    //Load file into a String
    public String loadWorkDefinitions() throws SerializationException {
        StringBuffer sb = new StringBuffer();
        AssetPageRequest workDefinitionAssetRequest = new AssetPageRequest( packageUUID,
                                                                            Arrays.asList( new String[]{"wid"} ),
                                                                            null,
                                                                            0,
                                                                            null );
        PageResponse<AssetPageRow> assetWorkDefinitions = repositoryAssetService.findAssetPage( workDefinitionAssetRequest );
        for ( AssetPageRow row : assetWorkDefinitions.getPageRowList() ) {
            RuleAsset asset = repositoryAssetService.loadRuleAsset( row.getUuid() );
            RuleContentText content = (RuleContentText) asset.getContent();
            sb.append( content.content );
            sb.append( NEW_LINE );
            sb.append( NEW_LINE );
        }
        return sb.toString();
    }

}
