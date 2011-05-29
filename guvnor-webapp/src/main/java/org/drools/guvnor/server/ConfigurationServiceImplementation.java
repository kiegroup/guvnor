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

package org.drools.guvnor.server;

import java.util.ArrayList;
import java.util.Collection;

import org.drools.guvnor.client.rpc.ConfigurationService;
import org.drools.guvnor.client.rpc.IFramePerspectiveConfiguration;
import org.drools.guvnor.server.util.TestEnvironmentSessionHelper;
import org.drools.repository.IFramePerspectiveConfigurationItem;
import org.drools.repository.RulesRepository;
import org.jboss.seam.Component;
import org.jboss.seam.contexts.Contexts;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

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
        if (Contexts.isApplicationContextActive()) {
            RulesRepository repo = (RulesRepository) Component.getInstance("repository");
            return repo;
        } else {
            try {
                RulesRepository repo = new RulesRepository(TestEnvironmentSessionHelper.getSession(false));
                return repo;
            } catch (Exception e) {
                throw new IllegalStateException("Unable to get repo to run tests", e);
            }

        }
    }

    private IFramePerspectiveConfiguration configurationItemToConfiguration(IFramePerspectiveConfigurationItem perspectiveConfigurationItem) {
        IFramePerspectiveConfiguration configuration = new IFramePerspectiveConfiguration();
        configuration.setUuid(perspectiveConfigurationItem.getUuid());
        configuration.setName(perspectiveConfigurationItem.getName());
        configuration.setUrl(perspectiveConfigurationItem.getUrl());
        return configuration;
    }
}
