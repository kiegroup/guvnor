/*
 * Copyright 2012 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.drools.guvnor.server.converters;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.rpc.Asset;
import org.drools.guvnor.client.rpc.ConversionResult;
import org.drools.guvnor.client.rpc.ConversionResultDuplicate;
import org.drools.guvnor.client.rpc.ConversionResultErrorCreating;
import org.drools.guvnor.client.rpc.NewAssetConfiguration;
import org.drools.guvnor.server.RepositoryAssetService;
import org.drools.guvnor.server.ServiceImplementation;
import org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52;
import org.drools.repository.AssetItem;

import com.google.gwt.user.client.rpc.SerializationException;

/**
 * Converter from a XLS Decision Table to a Guided Decision Table
 */
@ApplicationScoped
public class DecisionTableXLSToDecisionTableGuidedConverter extends AbstractConverter {

    private static final String    FORMAT = AssetFormats.DECISION_TABLE_GUIDED;

    @Inject
    private ServiceImplementation  serviceImplementation;

    @Inject
    private RepositoryAssetService repositoryAssetService;

    public DecisionTableXLSToDecisionTableGuidedConverter() {
        super( FORMAT );
    }

    @Override
    ConversionResult convert(final AssetItem item) {
        final String assetName = "Import - " + item.getName();
        final String packageName = item.getModule().getName();
        final String packageUUID = item.getModule().getUUID();
        final String description = "Converted from XLS Decision Table '" + item.getName() + "'.";
        final String initialCategory = item.getCategorySummary();
        final NewAssetConfiguration config = new NewAssetConfiguration( assetName,
                                                                        packageName,
                                                                        packageUUID,
                                                                        description,
                                                                        initialCategory,
                                                                        FORMAT );
        GuidedDecisionTable52 dtable = new GuidedDecisionTable52();
        return createNewAsset( item,
                               config,
                               dtable );
    }

    protected ConversionResult createNewAsset(final AssetItem item,
                                              final NewAssetConfiguration config,
                                              final GuidedDecisionTable52 content) {

        try {

            String uuid = serviceImplementation.createNewRule( config );
            if ( uuid.startsWith( "DUPLICATE" ) ) {
                return new ConversionResultDuplicate();
            } else {
                Asset newAsset = repositoryAssetService.loadRuleAsset( uuid );
                newAsset.setContent( content );
                newAsset.setCheckinComment( "Converted from '" + item.getName() + "'." );
                uuid = repositoryAssetService.checkinVersion( newAsset );
                if ( uuid == null ) {
                    return new ConversionResultErrorCreating();
                }
                if ( uuid.startsWith( "ERR" ) ) {
                    final String message = uuid.substring( 3 );
                    return new ConversionResultErrorCreating( message );
                }
            }
            return new ConversionResult( uuid );

        } catch ( SerializationException se ) {
            return new ConversionResultErrorCreating( se.getMessage() );
        }
    }

}
