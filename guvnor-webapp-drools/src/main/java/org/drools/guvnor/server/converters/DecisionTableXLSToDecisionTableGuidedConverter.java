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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.drools.core.util.DateUtils;
import org.drools.decisiontable.parser.xls.ExcelParser;
import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.rpc.Asset;
import org.drools.guvnor.client.rpc.ConversionResult;
import org.drools.guvnor.client.rpc.ConversionResult.ConversionMessageType;
import org.drools.guvnor.client.rpc.NewAssetConfiguration;
import org.drools.guvnor.server.RepositoryAssetService;
import org.drools.guvnor.server.ServiceImplementation;
import org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52;
import org.drools.repository.AssetItem;
import org.drools.template.parser.DataListener;

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

        ConversionResult result = new ConversionResult();

        //Check Asset has binary content
        if ( !item.isBinary() ) {
            result.addMessage( "Asset has no binary content.",
                               ConversionMessageType.ERROR );
            return result;
        }

        //Perform conversion!
        GuidedDecisionTable52 dtable = createGuidedDecisionTable( item,
                                                                  result );

        //Create new asset from Guided Decision Table
        final String assetName = makeNewAssetName( item );
        final String packageName = item.getModule().getName();
        final String packageUUID = item.getModule().getUUID();
        final String description = "Converted from XLS Decision Table '" + item.getName() + "'.";
        final NewAssetConfiguration config = new NewAssetConfiguration( assetName,
                                                                        packageName,
                                                                        packageUUID,
                                                                        description,
                                                                        null,
                                                                        FORMAT );
        createNewAsset( item,
                        config,
                        dtable,
                        result );
        return result;
    }

    private GuidedDecisionTable52 createGuidedDecisionTable(AssetItem item,
                                                            ConversionResult result) {

        final List<DataListener> listeners = new ArrayList<DataListener>();
        final GuidedDecisionTableGeneratorListener listener = new GuidedDecisionTableGeneratorListener();
        listeners.add( listener );

        final ExcelParser parser = new ExcelParser( listeners );
        final InputStream stream = item.getBinaryContentAttachment();
        try {
            parser.parseFile( stream );
        } finally {
            try {
                stream.close();
            } catch ( IOException ioe ) {
                result.addMessage( ioe.getMessage(),
                                   ConversionMessageType.ERROR );
            }
        }
        //TODO {manstis} Handle multiple Decision Tables
        List<GuidedDecisionTable52> dtables = listener.getGuidedDecisionTable();
        GuidedDecisionTable52 dtable = dtables.get(0);
        return dtable;
    }

    private String makeNewAssetName(AssetItem item) {
        Calendar now = Calendar.getInstance();
        StringBuilder sb = new StringBuilder( item.getName() );
        sb.append( " (converted on " );
        sb.append( DateUtils.format( now.getTime() ) );
        sb.append( " " );
        sb.append( now.get( Calendar.HOUR_OF_DAY ) );
        sb.append( ":" );
        sb.append( now.get( Calendar.MINUTE ) );
        sb.append( ":" );
        sb.append( now.get( Calendar.SECOND ) );
        sb.append( ")" );
        return sb.toString();
    }

    protected void createNewAsset(final AssetItem item,
                                  final NewAssetConfiguration config,
                                  final GuidedDecisionTable52 content,
                                  final ConversionResult result) {

        try {

            //Create new asset
            String uuid = serviceImplementation.createNewRule( config );

            //If there was an error creating the asset return
            if ( uuid.startsWith( "DUPLICATE" ) ) {
                result.addMessage( uuid,
                                   ConversionMessageType.ERROR );
                return;
            }

            //Check-in asset with content
            Asset newAsset = repositoryAssetService.loadRuleAsset( uuid );
            newAsset.setContent( content );
            newAsset.setCheckinComment( "Converted from '" + item.getName() + "'." );
            uuid = repositoryAssetService.checkinVersion( newAsset );

            //If there was an error checking-in new asset return
            if ( uuid.startsWith( "ERR" ) ) {
                result.addMessage( uuid,
                                   ConversionMessageType.ERROR );
                return;
            }

            result.setUUID( uuid );

        } catch ( SerializationException se ) {
            result.addMessage( se.getMessage(),
                               ConversionMessageType.ERROR );
        }
    }

}
