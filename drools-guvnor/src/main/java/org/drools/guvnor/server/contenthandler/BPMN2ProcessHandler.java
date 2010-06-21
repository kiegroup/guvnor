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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.apache.log4j.Logger;
import org.drools.bpmn2.xml.BPMNSemanticModule;
import org.drools.bpmn2.xml.XmlBPMNProcessDumper;
import org.drools.compiler.DroolsParserException;
import org.drools.compiler.PackageBuilderConfiguration;
import org.drools.compiler.xml.XmlProcessReader;
import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.guvnor.client.rpc.RuleFlowContentModel;
import org.drools.guvnor.server.GuvnorAPIServlet;
import org.drools.guvnor.server.builder.BRMSPackageBuilder;
import org.drools.guvnor.server.builder.RuleFlowContentModelBuilder;
import org.drools.guvnor.server.builder.RuleFlowProcessBuilder;
import org.drools.guvnor.server.builder.ContentPackageAssembler.ErrorLogger;
import org.drools.guvnor.server.util.LoggingHelper;
import org.drools.repository.AssetItem;
import org.drools.repository.PackageItem;
import org.drools.repository.RulesRepositoryException;
import org.drools.ruleflow.core.RuleFlowProcess;
import org.drools.compiler.xml.XmlProcessReader;
import org.drools.compiler.xml.XmlRuleFlowProcessDumper;

import com.google.gwt.user.client.rpc.SerializableException;

public class BPMN2ProcessHandler extends ContentHandler
    implements
    ICompilable {

    private static final Logger     log  = LoggingHelper.getLogger(BPMN2ProcessHandler.class);
    
    public void retrieveAssetContent(RuleAsset asset,
                                     PackageItem pkg,
                                     AssetItem item) throws SerializableException {
        RuleFlowProcess process = readProcess( new ByteArrayInputStream( item.getContent().getBytes() ) );
        if ( process != null ) {
            RuleFlowContentModel content = RuleFlowContentModelBuilder.createModel( process );
            content.setXml( item.getContent() );
            asset.content = content;
		} else {
			// we are very fault tolerant
			RuleFlowContentModel content = new RuleFlowContentModel();
			content.setXml(item.getContent());
			asset.content = content;
		}
    }

    protected RuleFlowProcess readProcess(InputStream is) {
        RuleFlowProcess process = null;
        try {
            InputStreamReader reader = new InputStreamReader( is );
            PackageBuilderConfiguration configuration = new PackageBuilderConfiguration();
            configuration.initSemanticModules();
            configuration.addSemanticModule( new BPMNSemanticModule() );
            XmlProcessReader xmlReader = new XmlProcessReader( configuration.getSemanticModules() );
            try {
                process = (RuleFlowProcess) xmlReader.read( reader );
            } catch ( Exception e ) {
                reader.close();
                throw new Exception( "Unable to read BPMN2 XML.",
                                     e );
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
    	System.out.println(content);
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
            if ( content.getJson() != null) {
            	try {
            		String xml = serialize( "http://localhost:8080/designer/bpmn2_0serialization", content.getJson());
            		System.out.println("xml = " + xml);
            		repoAsset.updateContent(xml);
            	} catch (Exception e) {
            		log.error(e.getMessage(), e);
            	}
            }
        }
    }

    public static String serialize(String serializeUrl, String modelJson) throws IOException {
    	OutputStream out = null;
		InputStream content = null;
		ByteArrayOutputStream bos = null;

		try {
			modelJson = "data=" + URLEncoder.encode(modelJson, "UTF-8")	+ "&xml=true";
			byte[] bytes = modelJson.getBytes("UTF-8");

			HttpURLConnection connection = (HttpURLConnection) new URL(serializeUrl).openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			connection.setFixedLengthStreamingMode(bytes.length);
			connection.setDoOutput(true);
			out = connection.getOutputStream();
			out.write(bytes);
			out.close();

			content = connection.getInputStream();

			bos = new ByteArrayOutputStream();
			int b = 0;
			while ((b = content.read()) > -1) {
				bos.write(b);
			}
			bytes = bos.toByteArray();
			content.close();
			bos.close();
			return new String(bytes);
		} finally {
			try {
				if (out != null) {
					out.close();
				}
				if (content != null) {
					content.close();
				}
				if (bos != null) {
					bos.close();
				}
			} catch (IOException e) {
			}
		}
	}

    /**
     * The rule flow can not be built if the package name is not the same as the
     * package that it exists in. This changes the package name.
     * 
     * @param item
     */
    public void ruleFlowAttached(AssetItem item) {
        String content = item.getContent();

        if ( content != null && !content.equals( "" ) ) {
            RuleFlowProcess process = readProcess( new ByteArrayInputStream( content.getBytes() ) );
            if ( process != null ) {
                String packageName = item.getPackageName();
                String originalPackageName = process.getPackageName();
                if ( !packageName.equals( originalPackageName ) ) {
                    process.setPackageName( packageName );
                    XmlBPMNProcessDumper dumper = XmlBPMNProcessDumper.INSTANCE;
                    String out = dumper.dump( process );
                    item.updateContent( out );
                    item.checkin( "Changed BPMN2 process package from " + originalPackageName + " to " + packageName );
                }
            }
        }
    }

    public void compile(BRMSPackageBuilder builder,
                        AssetItem asset,
                        ErrorLogger logger) throws DroolsParserException,
                                           IOException {
        InputStream ins = asset.getBinaryContentAttachment();
        if ( ins != null ) {
            builder.addProcessFromXml( new InputStreamReader( asset.getBinaryContentAttachment() ) );
        }
    }
}