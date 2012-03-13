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
package org.drools.guvnor.server.converters.decisiontable;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.drools.core.util.DateUtils;
import org.drools.decisiontable.parser.xls.ExcelParser;
import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.rpc.Asset;
import org.drools.guvnor.client.rpc.ConversionResult;
import org.drools.guvnor.client.rpc.ConversionResult.ConversionAsset;
import org.drools.guvnor.client.rpc.ConversionResult.ConversionMessageType;
import org.drools.guvnor.client.rpc.Module;
import org.drools.guvnor.client.rpc.NewAssetConfiguration;
import org.drools.guvnor.client.rpc.RuleContentText;
import org.drools.guvnor.server.RepositoryAssetService;
import org.drools.guvnor.server.RepositoryModuleService;
import org.drools.guvnor.server.ServiceImplementation;
import org.drools.guvnor.server.converters.AbstractConverter;
import org.drools.guvnor.shared.modules.ModuleHeader;
import org.drools.guvnor.shared.modules.ModuleHeaderHelper;
import org.drools.ide.common.client.modeldriven.brl.PortableObject;
import org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52;
import org.drools.repository.AssetItem;
import org.drools.template.model.Global;
import org.drools.template.model.Import;
import org.drools.template.parser.DataListener;

import com.google.gwt.user.client.rpc.SerializationException;

/**
 * Converter from a XLS Decision Table to a Guided Decision Table
 */
@ApplicationScoped
public class DecisionTableXLSToDecisionTableGuidedConverter extends AbstractConverter {

    private static final String     FORMAT = AssetFormats.DECISION_TABLE_GUIDED;

    @Inject
    private ServiceImplementation   serviceImplementation;

    @Inject
    private RepositoryAssetService  repositoryAssetService;

    @Inject
    private RepositoryModuleService repositoryModuleService;

    public DecisionTableXLSToDecisionTableGuidedConverter() {
        super( FORMAT );
    }

    @Override
    public ConversionResult convert(final AssetItem item) {

        ConversionResult result = new ConversionResult();

        try {

            //Check Asset is of the correct format
            if ( !item.getFormat().equals( AssetFormats.DECISION_SPREADSHEET_XLS ) ) {
                result.addMessage( "Source Asset is not an XLS Decision Table.",
                                   ConversionMessageType.ERROR );
                return result;
            }

            //Check Asset has binary content
            if ( !item.isBinary() ) {
                result.addMessage( "Source Asset has no binary content.",
                                   ConversionMessageType.ERROR );
                return result;
            }

            //Perform conversion!
            GuidedDecisionTableGeneratorListener listener = parseAssets( item,
                                                                         result );

            //Add Ancillary assets
            createNewFunctions( listener.getFunctions(),
                                item,
                                result );
            createNewGlobalsAndImports( listener.getGlobals(),
                                        listener.getImports(),
                                        item,
                                        result );
            createNewQueries( listener.getQueries(),
                              item,
                              result );
            createNewDeclarativeTypes( listener.getTypeDeclarations(),
                                       item,
                                       result );

            //Add Web Guided Decision Tables
            createNewDecisionTables( listener.getGuidedDecisionTables(),
                                     item,
                                     result );

        } catch ( SerializationException se ) {
            result.addMessage( se.getMessage(),
                               ConversionMessageType.ERROR );
        }

        return result;
    }

    private GuidedDecisionTableGeneratorListener parseAssets(AssetItem item,
                                                             ConversionResult result) {

        final List<DataListener> listeners = new ArrayList<DataListener>();
        final GuidedDecisionTableGeneratorListener listener = new GuidedDecisionTableGeneratorListener( result );
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
        return listener;
    }

    private void createNewFunctions(List<String> functions,
                                    AssetItem item,
                                    ConversionResult result) throws SerializationException {

        if ( functions == null ) {
            return;
        }

        //Create new assets for Functions
        for ( int iCounter = 0; iCounter < functions.size(); iCounter++ ) {

            //Model should be text-based (as Guvnor does not support all DRL features)
            RuleContentText content = new RuleContentText();
            content.content = functions.get( iCounter );

            final String assetName = makeNewAssetName( "Function " + (iCounter + 1) );
            final String packageName = item.getModule().getName();
            final String packageUUID = item.getModule().getUUID();
            final String description = "Converted from XLS Decision Table '" + item.getName() + "'.";

            result.addMessage( "Created Function '" + assetName + "'",
                               ConversionMessageType.INFO );

            final NewAssetConfiguration config = new NewAssetConfiguration( assetName,
                                                                            packageName,
                                                                            packageUUID,
                                                                            description,
                                                                            null,
                                                                            AssetFormats.FUNCTION );
            createNewAsset( item,
                            config,
                            content,
                            result );
        }
    }

    private void createNewGlobalsAndImports(List<Global> globals,
                                            List<Import> imports,
                                            AssetItem item,
                                            ConversionResult result) throws SerializationException {

        if ( globals == null && imports == null ) {
            return;
        }
        boolean isModified = false;

        //Load Module header and globals and imports
        String moduleUUID = item.getModule().getUUID();
        Module module = repositoryModuleService.loadModule( moduleUUID );
        ModuleHeader mh = ModuleHeaderHelper.parseHeader( module.header );

        //Make collections of existing items so we don't duplicate them when adding the new
        Map<String, String> existingGlobals = new HashMap<String, String>();
        for ( ModuleHeader.Global g : mh.getGlobals() ) {
            existingGlobals.put( g.getName(),
                                 g.getType() );
        }
        List<String> existingImports = new ArrayList<String>();
        for ( ModuleHeader.Import i : mh.getImports() ) {
            existingImports.add( i.getType() );
        }

        //Add globals
        if ( globals != null ) {
            for ( Global g : globals ) {
                if ( !existingGlobals.containsKey( g.getIdentifier() ) ) {
                    isModified = true;
                    result.addMessage( "Created Global '" + g.getIdentifier() + "' of type '" + g.getClassName() + "'.",
                                       ConversionMessageType.INFO );
                    mh.getGlobals().add( new ModuleHeader.Global( g.getClassName(),
                                                                  g.getIdentifier() ) );
                } else {
                    if ( !existingGlobals.get( g.getIdentifier() ).equals( g.getClassName() ) ) {
                        result.addMessage( "Global '" + g.getIdentifier() + "' is already declared. Type '" + existingGlobals.get( g.getIdentifier() ) + "'. Cannot create from Worksheet.",
                                           ConversionMessageType.WARNING );
                    }
                }
            }
        }

        //Add imports
        if ( imports != null ) {
            for ( Import i : imports ) {
                if ( !existingImports.contains( i.getClassName() ) ) {
                    isModified = true;
                    result.addMessage( "Created Import for '" + i.getClassName() + "'.",
                                       ConversionMessageType.INFO );
                    mh.getImports().add( new ModuleHeader.Import( i.getClassName() ) );
                }
            }
        }

        //Save update
        if ( isModified ) {
            module.setHeader( ModuleHeaderHelper.renderModuleHeader( mh ) );
            repositoryModuleService.saveModule( module );
        }
    }

    private void createNewQueries(List<String> queries,
                                  AssetItem item,
                                  ConversionResult result) {
        if ( queries == null ) {
            return;
        }

        //Queries are not supported in Guvnor. See https://issues.jboss.org/browse/GUVNOR-1532
        for ( String query : queries ) {
            result.addMessage( "Queries are not supported in Guvnor. Query '" + query + "' will not be added.",
                               ConversionMessageType.WARNING );
        }
    }

    private void createNewDeclarativeTypes(List<String> declaredTypes,
                                           AssetItem item,
                                           ConversionResult result) throws SerializationException {

        if ( declaredTypes == null ) {
            return;
        }

        //Create new assets for Declared Types
        for ( int iCounter = 0; iCounter < declaredTypes.size(); iCounter++ ) {

            //Model should be text-based (as Guvnor does not support all DRL features)
            RuleContentText content = new RuleContentText();
            content.content = declaredTypes.get( iCounter );

            final String assetName = makeNewAssetName( "Declarative Model " + (iCounter + 1) );
            final String packageName = item.getModule().getName();
            final String packageUUID = item.getModule().getUUID();
            final String description = "Converted from XLS Decision Table '" + item.getName() + "'.";

            result.addMessage( "Created Declarative Model '" + assetName + "'.",
                               ConversionMessageType.INFO );

            final NewAssetConfiguration config = new NewAssetConfiguration( assetName,
                                                                            packageName,
                                                                            packageUUID,
                                                                            description,
                                                                            null,
                                                                            AssetFormats.DRL_MODEL );
            createNewAsset( item,
                            config,
                            content,
                            result );
        }
    }

    private void createNewDecisionTables(List<GuidedDecisionTable52> dtables,
                                         AssetItem item,
                                         ConversionResult result) throws SerializationException {

        if ( dtables == null ) {
            return;
        }

        for ( GuidedDecisionTable52 dtable : dtables ) {

            //Create new asset from Guided Decision Table
            final String assetName = makeNewAssetName( dtable.getTableName() );
            final String packageName = item.getModule().getName();
            final String packageUUID = item.getModule().getUUID();
            final String description = "Converted from XLS Decision Table '" + item.getName() + "'.";

            result.addMessage( "Created Guided Decision Table '" + assetName + "'.",
                               ConversionMessageType.INFO );

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
        }
    }

    private String makeNewAssetName(String baseName) {
        Calendar now = Calendar.getInstance();
        StringBuilder sb = new StringBuilder( baseName );
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
                                  final Serializable content,
                                  final ConversionResult result) throws SerializationException {

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

        result.addNewAsset( new ConversionAsset( uuid,
                                                 config.getFormat() ) );

    }

}
