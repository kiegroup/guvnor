/*
 * Copyright 2011 JBoss Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package org.drools.guvnor.client.admin;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.SerializationException;
import org.drools.guvnor.client.rpc.ConfigurationServiceAsync;
import org.drools.guvnor.client.rpc.IFramePerspectiveConfiguration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class ConfigurationServiceAsyncMock implements ConfigurationServiceAsync {

    private ArrayList<IFramePerspectiveConfiguration> result = new ArrayList<IFramePerspectiveConfiguration>();
    private IFramePerspectiveConfiguration newConfiguration;
    private IFramePerspectiveConfiguration loadedConfiguration;
    private String removedUuid;

    public ArrayList<IFramePerspectiveConfiguration> getResult() {
        return result;
    }

    public void save(IFramePerspectiveConfiguration newConfiguration, AsyncCallback<String> async) {
        this.newConfiguration = newConfiguration;
        if (newConfiguration.getUuid() == null) {
            async.onSuccess("mock-uuid");
        } else {
            async.onSuccess(newConfiguration.getUuid());
        }
    }

    public void load(String uuid, AsyncCallback<IFramePerspectiveConfiguration> async) throws SerializationException {
        async.onSuccess(loadedConfiguration);
    }

    public void loadPerspectiveConfigurations(AsyncCallback<Collection<IFramePerspectiveConfiguration>> async) {
        async.onSuccess(result);
    }

    public void loadPreferences(AsyncCallback<Map<String, String>> preferences) {
        //TODO: Generated code -Rikkola-
    }

    public void remove(String uuid, AsyncCallback<Void> async) {
        removedUuid = uuid;
        async.onSuccess(null);
    }

    public IFramePerspectiveConfiguration getSaved() {
        return newConfiguration;
    }

    public void setUpLoad(IFramePerspectiveConfiguration loadedConfiguration) {
        this.loadedConfiguration = loadedConfiguration;
    }

    public String getRemovedUuid() {
        return removedUuid;
    }
}
