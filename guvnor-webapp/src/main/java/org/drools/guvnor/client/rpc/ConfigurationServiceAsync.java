package org.drools.guvnor.client.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.SerializationException;

import java.util.Collection;

public interface ConfigurationServiceAsync {
    void save(IFramePerspectiveConfiguration newConfiguration, AsyncCallback<String> async);

    void load(String uuid, AsyncCallback<IFramePerspectiveConfiguration> async) throws SerializationException;

    void loadPerspectiveConfigurations(AsyncCallback<Collection<IFramePerspectiveConfiguration>> async);

    void remove(String uuid, AsyncCallback<Void> async);
}
