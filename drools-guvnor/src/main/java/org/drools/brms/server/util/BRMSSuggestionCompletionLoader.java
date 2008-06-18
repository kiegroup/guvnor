package org.drools.brms.server.util;
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



import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.jar.JarInputStream;

import org.drools.brms.client.common.AssetFormats;
import org.drools.brms.client.modeldriven.SuggestionCompletionEngine;
import org.drools.brms.server.ServiceImplementation;
import org.drools.brms.server.builder.BRMSPackageBuilder;
import org.drools.brms.server.rules.SuggestionCompletionLoader;
import org.drools.lang.dsl.DSLMappingFile;
import org.drools.repository.AssetItem;
import org.drools.repository.PackageItem;

/**
 * This decorates the suggestion completion loader with BRMS specific stuff.
 *
 * @author Michael Neale
 */
public class BRMSSuggestionCompletionLoader extends SuggestionCompletionLoader {

    public SuggestionCompletionEngine getSuggestionEngine(PackageItem pkg) {
            return super.getSuggestionEngine( ServiceImplementation.getDroolsHeader(pkg), getJars( pkg ), getDSLMappingFiles( pkg ), getDataEnums( pkg ));
    }


    private List<String> getDataEnums(PackageItem pkg) {
        Iterator it = pkg.listAssetsByFormat( new String[] {AssetFormats.ENUMERATION} );
        List<String> list = new ArrayList<String>();
        while(it.hasNext()) {
            AssetItem item = (AssetItem) it.next();
            list.add( item.getContent() );
        }
        return list;
    }

    private List<DSLMappingFile> getDSLMappingFiles(PackageItem pkg) {
        return BRMSPackageBuilder.getDSLMappingFiles( pkg, new BRMSPackageBuilder.DSLErrorEvent() {
            public void recordError(AssetItem asset, String message) {
                errors.add( asset.getName() + " : " + message );
            }
        });
    }

    private List<JarInputStream> getJars(PackageItem pkg) {
        return BRMSPackageBuilder.getJars( pkg );
    }

}