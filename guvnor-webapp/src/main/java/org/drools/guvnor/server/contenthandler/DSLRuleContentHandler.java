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

package org.drools.guvnor.server.contenthandler;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import org.drools.compiler.DroolsParserException;
import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.guvnor.client.rpc.RuleContentText;
import org.drools.guvnor.server.builder.BRMSPackageBuilder;
import org.drools.guvnor.server.builder.ContentAssemblyError;
import org.drools.guvnor.server.builder.ContentPackageAssembler;
import org.drools.guvnor.server.builder.ContentPackageAssembler.ErrorLogger;
import org.drools.lang.ExpanderException;
import org.drools.lang.dsl.DefaultExpander;
import org.drools.repository.AssetItem;
import org.drools.repository.PackageItem;

import com.google.gwt.user.client.rpc.SerializationException;

public class DSLRuleContentHandler extends ContentHandler
    implements
    IRuleAsset {

    public void retrieveAssetContent(RuleAsset asset,
                                     PackageItem pkg,
                                     AssetItem item) throws SerializationException {
        RuleContentText text = new RuleContentText();
        text.content = item.getContent();

        asset.content = text;

    }

    public void storeAssetContent(RuleAsset asset,
                                  AssetItem repoAsset) throws SerializationException {

        RuleContentText text = (RuleContentText) asset.content;
        repoAsset.updateContent( text.content );

    }

    public void compile(BRMSPackageBuilder builder,
                        AssetItem asset,
                        ContentPackageAssembler.ErrorLogger logger) throws DroolsParserException,
                                                                   IOException {
        DefaultExpander expander = getExpander( builder,
                                                asset,
                                                logger );

        String source = getRawDRL( asset );

        //expand and check for errors
        String drl = expander.expand( source );

        if ( expander.hasErrors() ) {
            List<ExpanderException> exErrs = expander.getErrors();
            for ( ExpanderException ex : exErrs ) {
                logger.logError( new ContentAssemblyError( asset,
                                                           ex.getMessage() ) );
            }
            return;
        }

        builder.addPackageFromDrl( new StringReader( drl ) );
    }

    public void compile(BRMSPackageBuilder builder,
                        RuleAsset asset,
                        ErrorLogger logger) throws DroolsParserException,
                                           IOException {
        DefaultExpander expander = getExpander( builder,
                                                asset,
                                                logger );

        RuleContentText text = (RuleContentText) asset.content;
        String source = getDRL( text.content,
                                asset.metaData.name,
                                null );

        //expand and check for errors
        String drl = expander.expand( source );

        if ( expander.hasErrors() ) {
            List<ExpanderException> exErrs = expander.getErrors();
            for ( ExpanderException ex : exErrs ) {
                logger.logError( new ContentAssemblyError( asset,
                                                           ex.getMessage() ) );
            }
            return;
        }

        builder.addPackageFromDrl( new StringReader( drl ) );
    }

    private DefaultExpander getExpander(BRMSPackageBuilder builder,
                                        AssetItem asset,
                                        ContentPackageAssembler.ErrorLogger logger) {

        if ( !builder.hasDSL() ) {
            logger.logError( new ContentAssemblyError( asset,
                                                       "This rule asset requires a DSL, yet none were configured in the package." ) );
        }

        return builder.getDSLExpander();
    }

    private DefaultExpander getExpander(BRMSPackageBuilder builder,
                                        RuleAsset asset,
                                        ContentPackageAssembler.ErrorLogger logger) {

        if ( !builder.hasDSL() ) {
            logger.logError( new ContentAssemblyError( asset,
                                                       "This rule asset requires a DSL, yet none were configured in the package." ) );
        }

        return builder.getDSLExpander();
    }

    public void assembleDRL(BRMSPackageBuilder builder,
                            RuleAsset asset,
                            StringBuilder stringBuilder) {
        RuleContentText text = (RuleContentText) asset.content;
        String source = text.content;

        source = getDRL( source,
                         asset.metaData.name,
                         null );

        DefaultExpander expander = builder.getDSLExpander();
        stringBuilder.append( expander.expand( source ) );
    }

    public void assembleDRL(BRMSPackageBuilder builder,
                            AssetItem asset,
                            StringBuilder stringBuilder) {
        //add the rule keyword if its 'stand alone'
        String source = getRawDRL( asset );

        DefaultExpander expander = builder.getDSLExpander();
        stringBuilder.append( expander.expand( source ) );

    }

    private String wrapRule(String assetName,
                            String parentName,
                            String source) {
        if ( parentName == null || "".equals( parentName ) ) {
            return "rule '" + assetName + "' \n" + source + "\nend";
        } else {
            return "rule '" + assetName + "' extends " + parentName + " \n" + source + "\nend";

        }
    }

    public String getRawDRL(AssetItem asset) {
        String source = asset.getContent();
        String parentName = this.parentNameFromCategory( asset,
                                                         "" );
        source = getDRL( source,
                         asset.getName(),
                         parentName );

        return source;
    }

    public String getDRL(String source,
                         String assetName,
                         String parentName) {
        if ( DRLFileContentHandler.isStandAloneRule( source ) ) {
            source = wrapRule( assetName,
                               parentName,
                               source );
        }

        return source;
    }

}
