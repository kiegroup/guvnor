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

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

import org.drools.guvnor.client.rpc.BuilderResult;
import org.drools.guvnor.client.rpc.BuilderResultLine;
import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.guvnor.client.rpc.TableDataResult;
import org.drools.guvnor.client.rpc.TableDataRow;
import org.drools.guvnor.server.builder.ContentPackageAssembler;
import org.drools.guvnor.server.contenthandler.ContentHandler;
import org.drools.guvnor.server.contenthandler.ContentManager;
import org.drools.guvnor.server.contenthandler.IValidating;
import org.drools.guvnor.server.util.BuilderResultHelper;
import org.drools.guvnor.server.util.LoggingHelper;
import org.drools.repository.AssetHistoryIterator;
import org.drools.repository.AssetItem;
import org.drools.repository.PackageItem;
import org.drools.repository.RulesRepository;
import org.jboss.seam.annotations.AutoCreate;
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

    private RulesRepository            repository;

    private static final LoggingHelper log = LoggingHelper.getLogger( RepositoryAssetOperations.class );

    public String renameAsset(String uuid, String newName) {
        return getRepository().renameAsset( uuid, newName );
    }

    protected BuilderResult buildAsset(RuleAsset asset) throws SerializationException {
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

    protected TableDataResult loadAssetHistory(final AssetItem assetItem) throws SerializationException {
        AssetHistoryIterator it = assetItem.getHistory();

        // MN Note: this uses the lazy iterator, but then loads the whole lot
        // up, and returns it.
        // The reason for this is that the GUI needs to show things in numeric
        // order by the version number.
        // When a version is restored, its previous version is NOT what you
        // thought it was - due to how JCR works
        // (its more like CVS then SVN). So to get a linear progression of
        // versions, we use the incrementing version number,
        // and load it all up and sort it. This is not ideal.
        // In future, we may do a "restore" instead just by copying content into
        // a new version, not restoring a node,
        // in which case the iterator will be in order (or you can just walk all
        // the way back).
        // So if there are performance problems with looking at lots of
        // historical versions, look at this nasty bit of code.
        List<TableDataRow> result = new ArrayList<TableDataRow>();
        while ( it.hasNext() ) {
            AssetItem historical = (AssetItem) it.next();
            long versionNumber = historical.getVersionNumber();
            if ( isHistory( assetItem, versionNumber ) ) {
                result.add( createHistoricalRow( result, historical ) );
            }
        }

        if ( result.size() == 0 ) {
            return null;
        }
        TableDataResult table = new TableDataResult();
        table.data = result.toArray( new TableDataRow[result.size()] );

        return table;
    }

    private boolean isHistory(AssetItem item, long versionNumber) {
        return versionNumber != 0 && versionNumber != item.getVersionNumber();
    }

    private TableDataRow createHistoricalRow(List<TableDataRow> result, AssetItem historical) {
        final DateFormat dateFormatter = DateFormat.getInstance();
        TableDataRow tableDataRow = new TableDataRow();
        tableDataRow.id = historical.getVersionSnapshotUUID();
        tableDataRow.values = new String[4];
        tableDataRow.values[0] = Long.toString( historical.getVersionNumber() );
        tableDataRow.values[1] = historical.getCheckinComment();
        tableDataRow.values[2] = dateFormatter.format( historical.getLastModified().getTime() );
        tableDataRow.values[3] = historical.getStateDescription();
        return tableDataRow;
    }

    public void setRepository(RulesRepository repository) {
        this.repository = repository;
    }

    public RulesRepository getRepository() {
        return repository;
    }

}
