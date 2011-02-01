/**
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
package org.drools.guvnor.server;

import org.drools.guvnor.client.rpc.BuilderResult;
import org.drools.guvnor.client.rpc.BuilderResultLine;
import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.guvnor.server.builder.ContentPackageAssembler;
import org.drools.guvnor.server.contenthandler.ContentHandler;
import org.drools.guvnor.server.contenthandler.ContentManager;
import org.drools.guvnor.server.contenthandler.IValidating;
import org.drools.guvnor.server.util.BuilderResultHelper;
import org.drools.guvnor.server.util.LoggingHelper;
import org.drools.repository.AssetItem;
import org.drools.repository.PackageItem;
import org.drools.repository.RulesRepository;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import com.google.gwt.user.client.rpc.SerializationException;

/**
 * Handles operations for Assets
 * @author Jari Timonen
 *
 */
@Name("org.drools.guvnor.server.RepositoryAssetOperations")
@AutoCreate
public class RepositoryAssetOperations {
    @In
    private RulesRepository repository;
    
    private static final LoggingHelper log = LoggingHelper.getLogger( RepositoryAssetOperations.class );


    public String renameAsset(String uuid, String newName) {
        return getRepository().renameAsset( uuid, newName );
    }

    public BuilderResult buildAsset(RuleAsset asset) throws SerializationException {
        BuilderResult result = new BuilderResult();

        try {

            ContentHandler handler = ContentManager.getHandler( asset.metaData.format );
            BuilderResultHelper builderResultHelper = new BuilderResultHelper(); 
            if ( asset.metaData.isBinary() ) {
                AssetItem item = getRepository().loadAssetByUUID( asset.uuid );

                handler.storeAssetContent( asset, item );

                if ( handler instanceof IValidating ) {
                    return ((IValidating) handler).validateAsset( item );
                }

                ContentPackageAssembler asm = new ContentPackageAssembler( item );
                if ( !asm.hasErrors() ) {
                    return null;
                }
                result.setLines( builderResultHelper.generateBuilderResults( asm ) );

            } else {
                if ( handler instanceof IValidating ) {
                    return ((IValidating) handler).validateAsset( asset );
                }

                PackageItem packageItem = getRepository().loadPackageByUUID( asset.metaData.packageUUID );

                ContentPackageAssembler asm = new ContentPackageAssembler( asset, packageItem );
                if ( !asm.hasErrors() ) {
                    return null;
                }
                result.setLines( builderResultHelper.generateBuilderResults( asm ) );
            }
        } catch ( Exception e ) {
            log.error( "Unable to build asset.", e );
            result = new BuilderResult();

            BuilderResultLine res = new BuilderResultLine();
            res.assetName = asset.metaData.name;
            res.assetFormat = asset.metaData.format;
            res.message = "Unable to validate this asset. (Check log for detailed messages).";
            res.uuid = asset.uuid;
            result.getLines()[0] = res;

            return result;

        }
        return result;
    }

    public void setRepository(RulesRepository repository) {
        this.repository = repository;
    }

    public RulesRepository getRepository() {
        return repository;
    }

   

}
