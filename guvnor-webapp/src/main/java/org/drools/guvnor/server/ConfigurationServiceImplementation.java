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

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import org.drools.guvnor.client.rpc.ConfigurationService;
import org.drools.guvnor.client.rpc.IFramePerspectiveConfiguration;
import org.drools.guvnor.server.configurations.ApplicationPreferencesInitializer;
import org.drools.guvnor.server.configurations.ApplicationPreferencesLoader;
import org.drools.guvnor.server.util.BeanManagerUtils;
import org.drools.guvnor.server.util.TestEnvironmentSessionHelper;
import org.drools.repository.IFramePerspectiveConfigurationItem;
import org.drools.repository.RulesRepository;
import org.jboss.seam.solder.beanManager.BeanManagerLocator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

public class ConfigurationServiceImplementation
        extends RemoteServiceServlet
        implements ConfigurationService {

    private final ServiceSecurity serviceSecurity = new ServiceSecurity();

    @Inject
    private RulesRepository repository;

    public String save(IFramePerspectiveConfiguration configuration) {
        serviceSecurity.checkSecurityIsAdmin();

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

        IFramePerspectiveConfigurationItem perspectiveConfigurationItem = repository.loadPerspectivesConfiguration(uuid);

        return prepareResult(perspectiveConfigurationItem);
    }

    public Collection<IFramePerspectiveConfiguration> loadPerspectiveConfigurations() {
        Collection<IFramePerspectiveConfigurationItem> perspectiveConfigurationItems = repository.listPerspectiveConfigurations();
        Collection<IFramePerspectiveConfiguration> result = new ArrayList<IFramePerspectiveConfiguration>(perspectiveConfigurationItems.size());
        for (IFramePerspectiveConfigurationItem perspectiveConfigurationItem : perspectiveConfigurationItems) {
            result.add(configurationItemToConfiguration(perspectiveConfigurationItem));
        }

        return result;
    }

    public void remove(String uuid) {
        serviceSecurity.checkSecurityIsAdmin();
        IFramePerspectiveConfigurationItem perspectiveConfigurationItem = repository.loadPerspectivesConfiguration(uuid);
        perspectiveConfigurationItem.remove();
        repository.save();
    }

    public Map<String, String> loadPreferences() {
        Map<String, String> preferences = ApplicationPreferencesLoader.load();
        ApplicationPreferencesInitializer.setSystemProperties(preferences);
        return preferences;
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

//    protected RulesRepository getRepository() {
//        BeanManager beanManager = (BeanManager) getServletContext()
//                .getAttribute("org.jboss.weld.environment.servlet.javax.enterprise.inject.spi.BeanManager");
//        return (RulesRepository) beanManager.getBeans("repository").iterator().next();
//        BeanManagerLocator beanManagerLocator = new BeanManagerLocator();
//        if (beanManagerLocator.isBeanManagerAvailable()) {
//            return (RulesRepository) BeanManagerUtils.getInstance("repository");
//        } else {
//            try {
//                return new RulesRepository(TestEnvironmentSessionHelper.getSession(false));
//            } catch (Exception e) {
//                throw new IllegalStateException("Unable to get repo to run tests", e);
//            }
//
//        }
//    }

    private IFramePerspectiveConfiguration configurationItemToConfiguration(IFramePerspectiveConfigurationItem perspectiveConfigurationItem) {
        IFramePerspectiveConfiguration configuration = new IFramePerspectiveConfiguration();
        configuration.setUuid(perspectiveConfigurationItem.getUuid());
        configuration.setName(perspectiveConfigurationItem.getName());
        configuration.setUrl(perspectiveConfigurationItem.getUrl());
        return configuration;
    }
}
