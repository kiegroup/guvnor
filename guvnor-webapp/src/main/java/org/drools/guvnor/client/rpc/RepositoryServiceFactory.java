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

package org.drools.guvnor.client.rpc;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;

/**
 * Creates instances of the repository service for the client code to use.
 */
public class RepositoryServiceFactory {

    private static RepositoryServiceAsync SERVICE;
    private static AssetServiceAsync ASSET_SERVICE;
    
    public static RepositoryServiceAsync getService() {
        if (SERVICE == null) {
            loadService();
        }
        return SERVICE;

    }
    
    public static AssetServiceAsync getAssetService() {
        if (ASSET_SERVICE == null) {
            loadAssetService();
        }
        return ASSET_SERVICE;

    }

    private static void loadService() {
        SERVICE = getRealService();
    }
    
    private static void loadAssetService() {
        ASSET_SERVICE = getRealAssetService();
    }

    private static RepositoryServiceAsync getRealService() {
        // define the service you want to call
        RepositoryServiceAsync svc = (RepositoryServiceAsync) GWT.create(RepositoryService.class);
        ServiceDefTarget endpoint = (ServiceDefTarget) svc;

        String endpointURL = GWT.getModuleBaseURL() + "guvnorService";

        endpoint.setServiceEntryPoint(endpointURL);
        return svc;
    }

    private static AssetServiceAsync getRealAssetService() {
        // define the service you want to call
        AssetServiceAsync assetServiceAsync = (AssetServiceAsync) GWT.create(AssetService.class);
        ServiceDefTarget endpoint = (ServiceDefTarget) assetServiceAsync;

        String endpointURL = GWT.getModuleBaseURL() + "guvnorService";

        endpoint.setServiceEntryPoint(endpointURL);
        return assetServiceAsync;
    }
    
    /**
     * Perform the login.
     */
    public static void login(String userName, String password, AsyncCallback cb) {
        SecurityServiceAsync svc = getSecurityService();
        svc.login( userName, password, cb );
    }

    public static SecurityServiceAsync getSecurityService() {
        SecurityServiceAsync svc =
            (SecurityServiceAsync) GWT.create(SecurityService.class);
        ServiceDefTarget endpoint = (ServiceDefTarget) svc;
        String endpointURL = GWT.getModuleBaseURL() + "securityService";
        endpoint.setServiceEntryPoint(endpointURL);
        return svc;
    }

}
