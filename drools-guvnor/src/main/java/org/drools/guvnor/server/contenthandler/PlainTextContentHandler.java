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



import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.guvnor.client.rpc.RuleContentText;
import org.drools.repository.AssetItem;
import org.drools.repository.PackageItem;

import com.google.gwt.user.client.rpc.SerializableException;

public abstract class PlainTextContentHandler extends ContentHandler {


    public void retrieveAssetContent(RuleAsset asset,
                                     PackageItem pkg, 
                                     AssetItem item) throws SerializableException {
        //default to text, goode olde texte, just like mum used to make.
        RuleContentText text = new RuleContentText();
        text.content = item.getContent();
        asset.content = text;

    }

    public void storeAssetContent(RuleAsset asset,
                                  AssetItem repoAsset) throws SerializableException {
        repoAsset.updateContent( ((RuleContentText)asset.content).content ); 

    }

}