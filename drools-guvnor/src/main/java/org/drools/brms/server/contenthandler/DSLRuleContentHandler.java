package org.drools.brms.server.contenthandler;
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



import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;
import java.util.List;

import org.drools.brms.client.rpc.RuleAsset;
import org.drools.brms.client.rpc.RuleContentText;
import org.drools.brms.server.builder.BRMSPackageBuilder;
import org.drools.brms.server.builder.ContentAssemblyError;
import org.drools.brms.server.builder.ContentPackageAssembler;
import org.drools.compiler.DroolsParserException;
import org.drools.lang.ExpanderException;
import org.drools.lang.dsl.DSLMappingFile;
import org.drools.lang.dsl.DefaultExpander;
import org.drools.repository.AssetItem;
import org.drools.repository.PackageItem;

import com.google.gwt.user.client.rpc.SerializableException;

public class DSLRuleContentHandler extends ContentHandler implements IRuleAsset {


    public void retrieveAssetContent(RuleAsset asset,
                                     PackageItem pkg,
                                     AssetItem item) throws SerializableException {
        RuleContentText text = new RuleContentText();
        text.content = item.getContent();
        

        asset.content = text;
        
    }

    public void storeAssetContent(RuleAsset asset,
                                  AssetItem repoAsset) throws SerializableException {

        RuleContentText text = (RuleContentText) asset.content;
        repoAsset.updateContent( text.content );

    }

    public void compile(BRMSPackageBuilder builder, AssetItem asset, ContentPackageAssembler.ErrorLogger logger) throws DroolsParserException,
                                                                    IOException {
        DefaultExpander expander = getExpander( builder, asset, logger );
        
        //add the rule keyword if its 'stand alone'
        String source = asset.getContent();
        if (DRLFileContentHandler.isStandAloneRule(source)) {
            source = wrapRule( asset, source );
        }
        
        //expand and check for errors
        String drl = expander.expand( source );
        if (expander.hasErrors()) {
            List exErrs = expander.getErrors();
            for ( Iterator iter = exErrs.iterator(); iter.hasNext(); ) {
                ExpanderException ex = (ExpanderException) iter.next();
                logger.logError( new ContentAssemblyError(asset, ex.getMessage()));
            }
            return;
        }
        
        
        builder.addPackageFromDrl( new StringReader(drl) );
    }

    private DefaultExpander getExpander(BRMSPackageBuilder builder, AssetItem asset, ContentPackageAssembler.ErrorLogger logger) {
        
        if (!builder.hasDSL()) {
            logger.logError( new ContentAssemblyError(asset, "This rule asset requires a DSL, yet none were configured in the package.") );
        }
        

        return builder.getDSLExpander();
    }

    public void assembleDRL(BRMSPackageBuilder builder, AssetItem asset, StringBuffer buf) {
        //add the rule keyword if its 'stand alone'
        String source = asset.getContent();
        if (DRLFileContentHandler.isStandAloneRule(source)) {
            source = wrapRule( asset, source );
        }
        
        DefaultExpander expander = builder.getDSLExpander();    
        buf.append( expander.expand( source ) );
        
    }

    private String wrapRule(AssetItem asset, String source) {
        return "rule '" + asset.getName() + "' \n" + source + "\nend";
    }


    

}