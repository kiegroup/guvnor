/**
 * Copyright 2010 JBoss Inc
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
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.drools.guvnor.client.rpc.BuilderResult;
import org.drools.guvnor.client.rpc.BuilderResultLine;
import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.guvnor.client.rpc.RuleContentText;
import org.drools.guvnor.client.ruleeditor.SpringContextValidator;
import org.drools.repository.AssetItem;
import org.drools.repository.PackageItem;

import com.google.gwt.user.client.rpc.SerializationException;


/**
 *
 */
public class SpringContextContentHandler extends PlainTextContentHandler implements IValidating {
    public void retrieveAssetContent(RuleAsset asset, PackageItem pkg, AssetItem item)
            throws SerializationException {
        if (item.getContent() != null) {
            RuleContentText text = new RuleContentText();
            text.content = item.getContent();
            asset.content = text;
        }
    }

    public void storeAssetContent(RuleAsset asset, AssetItem repoAsset) throws SerializationException {

        RuleContentText text = (RuleContentText) asset.content;

        try {
            InputStream input = new ByteArrayInputStream(text.content.getBytes("UTF-8"));
            repoAsset.updateBinaryContentAttachment(input);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new RuntimeException(e);     
        }
    }

	public BuilderResult validateAsset(AssetItem asset) {

		String message = validate(asset.getContent());
		
		return createBuilderResult(message, asset.getName(), asset.getFormat(), asset.getUUID());
	}

	public BuilderResult validateAsset(RuleAsset asset) {
		
		String message = validate(((RuleContentText) asset.content).content);
	
		return createBuilderResult(message, asset.metaData.name, asset.metaData.format, asset.uuid);
	}
	
	private BuilderResult createBuilderResult(String message, String name, String format, String uuid) {

		if(message.isEmpty()){
			return new BuilderResult();		
		}else{
			List<BuilderResultLine> errors = new ArrayList<BuilderResultLine>();

			BuilderResultLine result = new BuilderResultLine();
			result.assetName = name;
			result.assetFormat = format;
			result.uuid = uuid;
			result.message = message;
			errors.add( result );

			BuilderResult builderResult = new BuilderResult();
			builderResult.setLines( errors.toArray( new BuilderResultLine[errors.size()] ) );	

			return builderResult;
		}
	}
	
	private String validate(String content) {
		
		SpringContextValidator contextValidator = new SpringContextValidator();
		contextValidator.setContent(content);
		
		return contextValidator.validate();
	}
}
