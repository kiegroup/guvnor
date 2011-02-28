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

package org.drools.guvnor.server.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.jar.JarInputStream;

import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.server.ServiceImplementation;
import org.drools.guvnor.server.builder.BRMSPackageBuilder;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.server.rules.SuggestionCompletionLoader;
import org.drools.lang.descr.ImportDescr;
import org.drools.lang.dsl.DSLTokenizedMappingFile;
import org.drools.repository.AssetItem;
import org.drools.repository.AssetItemIterator;
import org.drools.repository.PackageItem;

/**
 * This decorates the suggestion completion loader with BRMS specific stuff.
 */
public class BRMSSuggestionCompletionLoader extends SuggestionCompletionLoader {

    private final Set<ImportDescr> extraImports = new HashSet<ImportDescr>();

    public BRMSSuggestionCompletionLoader() {
        super();
        initAndAttachExtraImportsProvider();
    }

    public BRMSSuggestionCompletionLoader(ClassLoader classLoader) {
        super(classLoader);
        initAndAttachExtraImportsProvider();
    }

    /**
     * Initialize the extra Imports and creates a Provider to use them.
     */
    private void initAndAttachExtraImportsProvider() {
        this.extraImports.add(new ImportDescr("java.util.Set"));
        this.extraImports.add(new ImportDescr("java.util.List"));
        this.extraImports.add(new ImportDescr("java.util.Collection"));
        this.extraImports.add(new ImportDescr("java.lang.Number"));

        this.addExternalImportDescrProvider(new ExternalImportDescrProvider() {

            public Set<ImportDescr> getImportDescrs() {
                return extraImports;
            }
        });
    }

    public SuggestionCompletionEngine getSuggestionEngine(PackageItem pkg) {

        StringBuilder buf = new StringBuilder();
        AssetItemIterator it = pkg.listAssetsByFormat(new String[]{AssetFormats.DRL_MODEL});
        while (it.hasNext()) {
            AssetItem as = it.next();
            buf.append(as.getContent());
            buf.append('\n');
        }

        return super.getSuggestionEngine(DroolsHeader.getDroolsHeader(pkg) + "\n" + buf.toString(),
                getJars(pkg),
                getDSLMappingFiles(pkg),
                getDataEnums(pkg));
    }

    private List<String> getDataEnums(PackageItem pkg) {
        Iterator it = pkg.listAssetsByFormat(new String[]{AssetFormats.ENUMERATION});
        List<String> list = new ArrayList<String>();
        while (it.hasNext()) {
            AssetItem item = (AssetItem) it.next();
            list.add(item.getContent());
        }
        return list;
    }

    private List<DSLTokenizedMappingFile> getDSLMappingFiles(PackageItem pkg) {
        return BRMSPackageBuilder.getDSLMappingFiles(pkg, new BRMSPackageBuilder.DSLErrorEvent() {

            public void recordError(AssetItem asset, String message) {
                getErrors().add(asset.getName() + " : " + message);
            }
        });
    }

    private List<JarInputStream> getJars(PackageItem pkg) {
        return BRMSPackageBuilder.getJars(pkg);
    }
}
