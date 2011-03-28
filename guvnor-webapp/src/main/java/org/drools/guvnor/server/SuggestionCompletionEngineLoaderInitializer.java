/*
 * Copyright 2011 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.drools.guvnor.server;

import java.util.List;
import java.util.jar.JarInputStream;

import org.drools.guvnor.server.builder.BRMSPackageBuilder;
import org.drools.guvnor.server.util.BRMSSuggestionCompletionLoader;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.repository.PackageItem;

public class SuggestionCompletionEngineLoaderInitializer {

    protected SuggestionCompletionEngine loadFor(final PackageItem packageItem) {
        SuggestionCompletionEngine result = null;
        ClassLoader originalCL = Thread.currentThread().getContextClassLoader();
        try {
            BRMSSuggestionCompletionLoader loader = null;
            List<JarInputStream> jars = BRMSPackageBuilder.getJars( packageItem );
            if ( jars != null && !jars.isEmpty() ) {
                ClassLoader cl = BRMSPackageBuilder.createClassLoader( jars );

                Thread.currentThread().setContextClassLoader( cl );

                loader = new BRMSSuggestionCompletionLoader( cl );
            } else {
                loader = new BRMSSuggestionCompletionLoader();
            }

            result = loader.getSuggestionEngine( packageItem );

        } finally {
            Thread.currentThread().setContextClassLoader( originalCL );
        }
        return result;
    }
}
