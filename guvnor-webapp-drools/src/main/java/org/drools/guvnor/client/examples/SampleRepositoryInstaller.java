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

import com.google.gwt.core.client.GWT;
import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;

public class SampleRepositoryInstaller {

    //Only ask for installation for the first time. 
    public static void askToInstall() {
        RepositoryServiceFactory.getService().isDoNotInstallSample(
                new GenericCallback<Boolean>() {
                    public void onSuccess(Boolean isDoNotInstallSample) {
                        if(!isDoNotInstallSample) {
                            RepositoryServiceFactory.getService().setDoNotInstallSample(
                                    new GenericCallback<Void>() {
                                        public void onSuccess(Void v) {                                        
                                        }
                                    } );
                            new NewRepositoryDialog().show();
                        }

                    }
                } );
    }

}
