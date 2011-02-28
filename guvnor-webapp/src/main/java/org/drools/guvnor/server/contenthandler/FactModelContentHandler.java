/*
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.drools.compiler.DrlParser;
import org.drools.compiler.DroolsParserException;
import org.drools.guvnor.client.factmodel.FactMetaModel;
import org.drools.guvnor.client.factmodel.FactModels;
import org.drools.guvnor.client.factmodel.FieldMetaModel;
import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.guvnor.client.rpc.RuleContentText;
import org.drools.lang.descr.PackageDescr;
import org.drools.lang.descr.TypeDeclarationDescr;
import org.drools.lang.descr.TypeFieldDescr;
import org.drools.repository.AssetItem;
import org.drools.repository.PackageItem;

import com.google.gwt.user.client.rpc.SerializationException;


public class FactModelContentHandler extends ContentHandler {

    @Override
    public void retrieveAssetContent(RuleAsset asset, PackageItem pkg,
            AssetItem item) throws SerializationException {
        try {
            List<FactMetaModel> models = toModel(item.getContent());
            FactModels ms = new FactModels();
            ms.models = models;
            asset.content = ms;
        } catch (DroolsParserException e) {
            System.err.println("Unable to parse the DRL for the model - falling back to text (" + e.getMessage() + ")");
            RuleContentText text = new RuleContentText();
            text.content = item.getContent();
            asset.content = text;
        }

    }

    @Override
    public void storeAssetContent(RuleAsset asset, AssetItem repoAsset)
            throws SerializationException {
        if (asset.content instanceof FactModels) {
            FactModels fm = (FactModels) asset.content;
            repoAsset.updateContent(toDRL(fm.models));
        } else {
            RuleContentText text = (RuleContentText) asset.content;
            repoAsset.updateContent(text.content);
        }

    }


    String toDRL(FactMetaModel mm) {
        StringBuilder sb = new StringBuilder();
        sb.append("declare " + mm.name);
        for (int i = 0; i < mm.fields.size(); i++) {
            FieldMetaModel f = (FieldMetaModel) mm.fields.get(i);
            sb.append("\n\t");
            sb.append(f.name + ": " + f.type);
        }
        sb.append("\nend");
        return sb.toString();
    }

    List<FactMetaModel> toModel(String drl) throws DroolsParserException {
        if (drl != null && drl.startsWith("#advanced")) {
            throw new DroolsParserException("Using advanced editor");
        }
        DrlParser parser = new DrlParser();
        PackageDescr pkg = parser.parse(drl);
        if (parser.hasErrors()) {
            throw new DroolsParserException("The model drl " + drl + " is not valid");
        }

        if (pkg == null) return new ArrayList<FactMetaModel>();
        List<TypeDeclarationDescr> types = pkg.getTypeDeclarations();
        List<FactMetaModel> list = new ArrayList<FactMetaModel>(types.size());
        for (TypeDeclarationDescr td : types) {
            FactMetaModel mm = new FactMetaModel();
            mm.name = td.getTypeName();

            Map<String, TypeFieldDescr> fields = td.getFields();
            for (Iterator<Map.Entry<String, TypeFieldDescr>> iterator = fields.entrySet().iterator(); iterator.hasNext();) {
                Map.Entry<String, TypeFieldDescr> en = iterator.next();
                String fieldName = en.getKey();
                TypeFieldDescr descr = en.getValue();
                FieldMetaModel fm = new FieldMetaModel(fieldName, descr.getPattern().getObjectType());

                mm.fields.add(fm);
            }
            list.add(mm);
        }
        return list;
    }

    String toDRL(List<FactMetaModel> models) {
        StringBuilder sb = new StringBuilder();
        for (FactMetaModel factMetaModel : models) {
            String drl = toDRL(factMetaModel);
            sb.append(drl + "\n\n");
        }
        return sb.toString().trim();
    }


}
