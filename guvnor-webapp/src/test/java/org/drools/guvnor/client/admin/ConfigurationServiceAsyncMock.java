package org.drools.guvnor.client.admin;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.SerializationException;
import org.drools.guvnor.client.rpc.ConfigurationServiceAsync;
import org.drools.guvnor.client.rpc.IFramePerspectiveConfiguration;

import java.util.ArrayList;
import java.util.Collection;

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
