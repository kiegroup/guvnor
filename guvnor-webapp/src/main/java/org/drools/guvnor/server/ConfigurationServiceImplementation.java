package org.drools.guvnor.server;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import org.drools.guvnor.client.rpc.ConfigurationService;
import org.drools.guvnor.client.rpc.IFramePerspectiveConfiguration;
import org.drools.repository.IFramePerspectiveConfigurationItem;
import org.drools.repository.RulesRepository;
import org.jboss.seam.Component;

import java.util.ArrayList;
import java.util.Collection;

public class ConfigurationServiceImplementation
        extends RemoteServiceServlet
        implements ConfigurationService {

    private ServiceSecurity serviceSecurity = new ServiceSecurity();

    public String save(IFramePerspectiveConfiguration configuration) {
        serviceSecurity.checkSecurityIsAdmin();

        RulesRepository repository = getRepository();
        if (isNewConfiguration(configuration)) {
            IFramePerspectiveConfigurationItem perspectiveConfigurationItem = repository.createPerspectivesConfiguration(configuration.getName(), configuration.getUrl());
            return perspectiveConfigurationItem.getUuid();
        } else {
            String uuid = configuration.getUuid();
            IFramePerspectiveConfigurationItem perspectiveConfigurationItem = repository.loadPerspectivesConfiguration(uuid);
            perspectiveConfigurationItem.setName(configuration.getName());
            perspectiveConfigurationItem.setUrl(configuration.getUrl());
            repository.save();
            return uuid;
        }
    }

    private boolean isNewConfiguration(IFramePerspectiveConfiguration configuration) {
        return configuration.getUuid() == null;
    }

    public IFramePerspectiveConfiguration load(String uuid) throws SerializationException {
        serviceSecurity.checkSecurityIsAdmin();

        validateUuid(uuid);

        RulesRepository repository = getRepository();
        IFramePerspectiveConfigurationItem perspectiveConfigurationItem = repository.loadPerspectivesConfiguration(uuid);

        return prepareResult(perspectiveConfigurationItem);
    }

    public Collection<IFramePerspectiveConfiguration> loadPerspectiveConfigurations() {
        RulesRepository repository = getRepository();
        Collection<IFramePerspectiveConfigurationItem> perspectiveConfigurationItems = repository.listPerspectiveConfigurations();
        Collection<IFramePerspectiveConfiguration> result = new ArrayList<IFramePerspectiveConfiguration>(perspectiveConfigurationItems.size());
        for (IFramePerspectiveConfigurationItem perspectiveConfigurationItem : perspectiveConfigurationItems) {
            result.add(configurationItemToConfiguration(perspectiveConfigurationItem));
        }

        return result;
    }

    public void remove(String uuid) {
        serviceSecurity.checkSecurityIsAdmin();
        RulesRepository repository = getRepository();
        IFramePerspectiveConfigurationItem perspectiveConfigurationItem = repository.loadPerspectivesConfiguration(uuid);
        perspectiveConfigurationItem.remove();
        repository.save();
    }

    private IFramePerspectiveConfiguration prepareResult(IFramePerspectiveConfigurationItem perspectiveConfigurationItem) {
        if (perspectiveConfigurationItem == null) {
            return null;
        } else {
            return configurationItemToConfiguration(perspectiveConfigurationItem);
        }
    }

    private void validateUuid(String uuid) throws SerializationException {
        if (uuid == null) {
            throw new SerializationException("Invalid parameter: Uuid for the perspective was null");
        }
    }

    protected RulesRepository getRepository() {
        return (RulesRepository) Component.getInstance("repository");
    }

    private IFramePerspectiveConfiguration configurationItemToConfiguration(IFramePerspectiveConfigurationItem perspectiveConfigurationItem) {
        IFramePerspectiveConfiguration configuration = new IFramePerspectiveConfiguration();
        configuration.setUuid(perspectiveConfigurationItem.getUuid());
        configuration.setName(perspectiveConfigurationItem.getName());
        configuration.setUrl(perspectiveConfigurationItem.getUrl());
        return configuration;
    }
}
