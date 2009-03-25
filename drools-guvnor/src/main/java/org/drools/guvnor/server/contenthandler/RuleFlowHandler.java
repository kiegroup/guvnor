package org.drools.guvnor.server.contenthandler;

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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.drools.compiler.DroolsParserException;
import org.drools.compiler.PackageBuilderConfiguration;
import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.guvnor.client.rpc.RuleFlowContentModel;
import org.drools.guvnor.server.builder.BRMSPackageBuilder;
import org.drools.guvnor.server.builder.RuleFlowContentModelBuilder;
import org.drools.guvnor.server.builder.RuleFlowProcessBuilder;
import org.drools.guvnor.server.builder.ContentPackageAssembler.ErrorLogger;
import org.drools.repository.AssetItem;
import org.drools.repository.PackageItem;
import org.drools.ruleflow.core.RuleFlowProcess;
import org.drools.xml.XmlProcessReader;
import org.drools.xml.XmlRuleFlowProcessDumper;

import com.google.gwt.user.client.rpc.SerializableException;

public class RuleFlowHandler extends ContentHandler
    implements
    IRuleAsset {

    public void retrieveAssetContent(RuleAsset asset,
                                     PackageItem pkg,
                                     AssetItem item) throws SerializableException {

        RuleFlowProcess process = readProcess( new ByteArrayInputStream( item.getContent().getBytes() ) );

        if ( process != null ) {
            RuleFlowContentModel content = RuleFlowContentModelBuilder.createModel( process );
            content.setXml( item.getContent() );
            asset.content = content;
        } else if ( process == null && !"".equals( item.getContent() ) ) {
            asset.content = new RuleFlowContentModel();
            //
            // 
            // Migrate v4 ruleflows to v5
            // All we can do is put the old drools 4 rfm back as the xml so 
            // that we can at least rebuild the package with it if the
            // migrate ruleflow system property is set true.
            //
            ((RuleFlowContentModel) asset.content).setXml( item.getContent() );
        }

    }

    protected RuleFlowProcess readProcess(InputStream is) {

        RuleFlowProcess process = null;

        try {
            InputStreamReader reader = new InputStreamReader( is );
            PackageBuilderConfiguration configuration = new PackageBuilderConfiguration();
            XmlProcessReader xmlReader = new XmlProcessReader( configuration.getSemanticModules() );

            try {
                process = (RuleFlowProcess) xmlReader.read( reader );

            } catch ( Exception e ) {
                reader.close();
                throw new Exception( "Unable to read rule flow XML." );
            }
            reader.close();
        } catch ( Exception e ) {
            return null;
        }

        return process;
    }

    public void storeAssetContent(RuleAsset asset,
                                  AssetItem repoAsset) throws SerializableException {

        RuleFlowContentModel content = (RuleFlowContentModel) asset.content;

        // 
        // Migrate v4 ruleflows to v5
        // Added guards to check for nulls in the case where the ruleflows
        // have not been migrated from drools 4 to 5.
        //
        if ( content != null ) {
            if ( content.getXml() != null ) {
                RuleFlowProcess process = readProcess( new ByteArrayInputStream( content.getXml().getBytes() ) );

                if ( process != null ) {
                    RuleFlowProcessBuilder.updateProcess( process,
                                                          content.getNodes() );

                    XmlRuleFlowProcessDumper dumper = XmlRuleFlowProcessDumper.INSTANCE;
                    String out = dumper.dump( process );

                    repoAsset.updateContent( out );
                } else {
                    //
                    // Migrate v4 ruleflows to v5
                    // Put the old contents back as there is no updating possible
                    //
                    repoAsset.updateContent( content.getXml() );
                }
            }
        }
    }

    public void assembleDRL(BRMSPackageBuilder builder,
                            AssetItem asset,
                            StringBuffer buf) {
        // do nothing... as no change to source.
    }

    public void compile(BRMSPackageBuilder builder,
                        AssetItem asset,
                        ErrorLogger logger) throws DroolsParserException,
                                           IOException {
        InputStream ins = asset.getBinaryContentAttachment();
        if (ins != null) {
            builder.addRuleFlow( new InputStreamReader( asset.getBinaryContentAttachment() ) );
        }
    }
}