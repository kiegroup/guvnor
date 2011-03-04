package org.drools.guvnor.client.rpc;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.client.rpc.SerializationException;

import java.util.Collection;


@RemoteServiceRelativePath("configurationService")
public interface ConfigurationService extends RemoteService {

    String save(IFramePerspectiveConfiguration newConfiguration);

    IFramePerspectiveConfiguration load(String uuid) throws SerializationException;

    Collection<IFramePerspectiveConfiguration> loadPerspectiveConfigurations();

    void remove(String uuid);
}
