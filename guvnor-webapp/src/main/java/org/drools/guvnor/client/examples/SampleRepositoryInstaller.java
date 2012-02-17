/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.guvnor.client.examples;

import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.rpc.PackageConfigData;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;

public class SampleRepositoryInstaller {
    public static void askToInstall() {
        RepositoryServiceFactory.getPackageService().listPackages(createGenericCallbackForListPackages());
    }

    private static GenericCallback<PackageConfigData[]> createGenericCallbackForListPackages() {
        return new GenericCallback<PackageConfigData[]>() {
            public void onSuccess(PackageConfigData[] result) {
                if (result.length == 1) {
                    new NewRepositoryDialog().show();
                }
            }
        };
    }
}
