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

import org.drools.bpmn2.xml.BPMN2SemanticModule;
import org.drools.bpmn2.xml.XmlBPMNProcessDumper;
import org.drools.compiler.DroolsParserException;
import org.drools.compiler.PackageBuilderConfiguration;
import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.guvnor.client.rpc.RuleFlowContentModel;
import org.drools.guvnor.server.builder.BRMSPackageBuilder;
import org.drools.guvnor.server.builder.RuleFlowContentModelBuilder;
import org.drools.guvnor.server.builder.ContentPackageAssembler.ErrorLogger;
import org.drools.repository.AssetItem;
import org.drools.repository.PackageItem;
import org.drools.ruleflow.core.RuleFlowProcess;
import org.drools.xml.XmlProcessReader;

import com.google.gwt.user.client.rpc.SerializableException;

public class BPMN2ProcessHandler extends ContentHandler implements IRuleAsset {

	public void retrieveAssetContent(RuleAsset asset, PackageItem pkg, AssetItem item) throws SerializableException {
		RuleFlowProcess process = readProcess(new ByteArrayInputStream(item.getContent().getBytes()));
		if (process != null) {
			RuleFlowContentModel content = RuleFlowContentModelBuilder.createModel(process);
			content.setXml(item.getContent());
			asset.content = content;
		}
	}

	protected RuleFlowProcess readProcess(InputStream is) {
		RuleFlowProcess process = null;
		try {
			InputStreamReader reader = new InputStreamReader(is);
			PackageBuilderConfiguration configuration = new PackageBuilderConfiguration();
			configuration.initSemanticModules();
			configuration.addSemanticModule(new BPMN2SemanticModule());
			XmlProcessReader xmlReader = new XmlProcessReader(configuration.getSemanticModules());
			try {
				process = (RuleFlowProcess) xmlReader.read(reader);
			} catch (Exception e) {
				reader.close();
				throw new Exception("Unable to read BPMN2 XML.", e);
			}
			reader.close();
		} catch (Exception e) {
			return null;
		}

		return process;
	}

	public void storeAssetContent(RuleAsset asset, AssetItem repoAsset) throws SerializableException {
	}

	/**
	 * The rule flow can not be built if the package name is not the same as the
	 * package that it exists in. This changes the package name.
	 * 
	 * @param item
	 */
	public void ruleFlowAttached(AssetItem item) {
		String content = item.getContent();

		if (content != null && !content.equals("")) {
			RuleFlowProcess process = readProcess(new ByteArrayInputStream(content.getBytes()));
			if (process != null) {
				String packageName = item.getPackageName();
				String originalPackageName = process.getPackageName();
				if (!packageName.equals(originalPackageName)) {
					process.setPackageName(packageName);
					XmlBPMNProcessDumper dumper = XmlBPMNProcessDumper.INSTANCE;
					String out = dumper.dump(process);
					item.updateContent(out);
					item.checkin("Changed BPMN2 process package from "
						+ originalPackageName + " to " + packageName);
				}
			}
		}
	}

	public void assembleDRL(BRMSPackageBuilder builder, AssetItem asset,
			StringBuffer buf) {
		// do nothing... as no change to source.
	}

	public void compile(BRMSPackageBuilder builder, AssetItem asset,
			ErrorLogger logger) throws DroolsParserException, IOException {
		InputStream ins = asset.getBinaryContentAttachment();
		if (ins != null) {
			builder.addProcessFromXml(new InputStreamReader(asset.getBinaryContentAttachment()));
		}
	}
}