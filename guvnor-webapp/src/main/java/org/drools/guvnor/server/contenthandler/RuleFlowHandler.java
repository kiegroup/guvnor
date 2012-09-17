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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.drools.compiler.DroolsParserException;
import org.drools.compiler.PackageBuilderConfiguration;
import org.drools.definition.process.Process;
import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.guvnor.client.rpc.RuleFlowContentModel;
import org.drools.guvnor.server.builder.AssemblyErrorLogger;
import org.drools.guvnor.server.builder.BRMSPackageBuilder;
import org.drools.guvnor.server.builder.RuleFlowContentModelBuilder;
import org.drools.guvnor.server.builder.RuleFlowProcessBuilder;
import org.drools.guvnor.server.util.LoggingHelper;
import org.drools.repository.AssetItem;
import org.jbpm.compiler.xml.ProcessSemanticModule;
import org.jbpm.compiler.xml.XmlProcessReader;
import org.jbpm.compiler.xml.XmlRuleFlowProcessDumper;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.xml.sax.SAXException;

import com.google.gwt.user.client.rpc.SerializationException;

public class RuleFlowHandler extends ContentHandler
    implements
    ICompilable,
    ICanHasAttachment {

    private static final LoggingHelper log = LoggingHelper.getLogger( RuleFlowHandler.class );
    
    public void retrieveAssetContent(RuleAsset asset,
                                     AssetItem item) throws SerializationException {

        RuleFlowProcess process = readProcess( new ByteArrayInputStream( item.getContent().getBytes() ) );

        if ( process != null ) {
	    RuleFlowContentModel content = new RuleFlowContentModel();
            content.setXml( item.getContent() );
            asset.setContent( content );
        } else if ( process == null && !"".equals( item.getContent() ) ) {
            asset.setContent( new RuleFlowContentModel() );
            //
            // 
            // Migrate v4 ruleflows to v5
            // All we can do is put the old drools 4 rfm back as the xml so 
            // that we can at least rebuild the package with it if the
            // migrate ruleflow system property is set true.
            //
            ((RuleFlowContentModel) asset.getContent()).setXml( item.getContent() );
        }

    }

    protected RuleFlowProcess readProcess(InputStream is) {

        List<Process> processes = null;
        RuleFlowProcess process = null;
        InputStreamReader reader = null;

        try {
            reader = new InputStreamReader( is );
            PackageBuilderConfiguration configuration = new PackageBuilderConfiguration();
            configuration.initSemanticModules();
            configuration.addSemanticModule( new ProcessSemanticModule() );
            XmlProcessReader xmlReader = new XmlProcessReader( configuration.getSemanticModules(),
                                                               getClassLoader() );

            //An asset can only contain one process (and a RuleFlowProcess at that)
            processes = xmlReader.read( reader );
            if ( processes.size() == 0 ) {
                final String message = "RuleFlowProcess not found.";
                log.error( message );
                throw new RuntimeException( message );
            }
            if ( processes.size() > 1 ) {
                final String message = "An asset can only contain one RuleFlowProcess. Multiple were detected.";
                log.error( message );
                throw new RuntimeException( message );
            }
            if ( processes.get( 0 ) instanceof RuleFlowProcess ) {
                process = (RuleFlowProcess) processes.get( 0 );
            } else {
                final String message = "The asset does not contain a RuleFlowProcess. Unable to process.";
                log.error( message );
                throw new RuntimeException( message );
            }

        } catch ( SAXException se ) {
            log.error( se.getMessage() );
        } catch ( IOException ioe ) {
            log.error( ioe.getMessage() );
        } finally {
            if ( reader != null ) {
                try {
                    reader.close();
                } catch ( IOException ioe ) {
                    log.error( ioe.getMessage() );
                }
            }
        }

        return process;
    }

    public void storeAssetContent(RuleAsset asset,
                                  AssetItem repoAsset) throws SerializationException {

        RuleFlowContentModel content = (RuleFlowContentModel) asset.getContent();

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

    /**
     * The rule flow can not be built if the package name is not the same as the
     * package that it exists in. This changes the package name.
     * 
     * @param item
     */
    public void onAttachmentAdded(AssetItem item) {
        String content = item.getContent();

        if ( content != null && !content.equals( "" ) ) {
            RuleFlowProcess process = readProcess( new ByteArrayInputStream( content.getBytes() ) );

            if ( process != null ) {
                String packageName = item.getPackageName();
                String originalPackageName = process.getPackageName();

                if ( !packageName.equals( originalPackageName ) ) {
                    process.setPackageName( packageName );

                    XmlRuleFlowProcessDumper dumper = XmlRuleFlowProcessDumper.INSTANCE;
                    String out = dumper.dump( process );

                    item.updateContent( out );

                    item.checkin( "Changed rule flow package from " + originalPackageName + " to " + packageName );
                }
            }
        }
    }

    public void onAttachmentRemoved(AssetItem item) throws IOException {
        // Nothing to do when this asset type is removed.
    }

    public void compile(BRMSPackageBuilder builder,
                        AssetItem asset,
                        AssemblyErrorLogger logger) throws DroolsParserException,
                                           IOException {
        InputStream ins = asset.getBinaryContentAttachment();
        if ( ins != null ) {
            builder.addRuleFlow( new InputStreamReader( asset.getBinaryContentAttachment() ) );
        }
    }

    private static ClassLoader getClassLoader() {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if ( cl == null ) {
            cl = RuleFlowHandler.class.getClassLoader();
        }
        return cl;
    }
}
