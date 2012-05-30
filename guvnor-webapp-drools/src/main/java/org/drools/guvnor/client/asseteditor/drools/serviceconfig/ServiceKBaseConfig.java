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

package org.drools.guvnor.client.asseteditor.drools.serviceconfig;

import org.drools.guvnor.shared.api.PortableObject;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


import static org.drools.guvnor.client.util.Preconditions.*;

public class ServiceKBaseConfig
        implements PortableObject {

    private static final long serialVersionUID = -543908047056220915L;

    private String name;
    private Integer maxThreads;
    private Boolean mbeans;
    private EventProcessingOption eventProcessingMode;
    private AssertBehaviorOption assertBehavior;
    private String assetsUser;
    private String assetsPassword;
    private Set<AssetReference> resources;
    private Set<AssetReference> models;

    private Map<String, ServiceKSessionConfig> ksessions;
    private Map<String, ServiceKAgentConfig> kagents;

    public ServiceKBaseConfig() {
        //necessary for serialization
    }

    public ServiceKBaseConfig(final String name) {
        setupNewInstance(name, null, null, null, null, null, null, null, null, null, null);
    }

    public ServiceKBaseConfig(final ServiceKBaseConfig value) {
        checkNotNull("value", value);
        setupNewInstance(value.name, value.maxThreads, value.mbeans, value.eventProcessingMode,
                value.assertBehavior, value.assetsUser, value.assetsPassword,
                value.resources, value.models, value.ksessions.values(),
                value.kagents.values());
    }

    public ServiceKBaseConfig(final String name,
            final Integer maxThreads,
            final Boolean mbeans,
            final EventProcessingOption eventProcessingMode,
            final AssertBehaviorOption assertBehavior,
            final String assetsUser,
            final String assetsPassword,
            final Collection<AssetReference> resources,
            final Collection<AssetReference> models,
            final Collection<ServiceKSessionConfig> ksessions,
            final Collection<ServiceKAgentConfig> kagents) {
        setupNewInstance(name, maxThreads, mbeans, eventProcessingMode,
                assertBehavior, assetsUser, assetsPassword,
                resources, models, ksessions, kagents);
    }

    public ServiceKBaseConfig(final String newName, final ServiceKBaseConfig value) {
        checkNotNull("value", value);
        checkNotEmpty("name", newName);
        setupNewInstance(newName, value.maxThreads, value.mbeans, value.eventProcessingMode,
                value.assertBehavior, value.assetsUser, value.assetsPassword,
                value.resources, value.models, value.ksessions.values(),
                value.kagents.values());
    }

    private void setupNewInstance(final String name,
            final Integer maxThreads,
            final Boolean mbeans,
            final EventProcessingOption eventProcessingMode,
            final AssertBehaviorOption assertBehavior,
            final String assetsUser,
            final String assetsPassword,
            final Collection<AssetReference> resources,
            final Collection<AssetReference> models,
            final Collection<ServiceKSessionConfig> ksessions,
            final Collection<ServiceKAgentConfig> kagents) {
        this.name = checkNotEmpty("name", name);
        this.maxThreads = maxThreads;
        this.mbeans = mbeans;
        this.eventProcessingMode = eventProcessingMode;
        this.assertBehavior = assertBehavior;
        this.assetsUser = assetsUser;
        this.assetsPassword = assetsPassword;
        if (resources != null && resources.size() > 0) {
            this.resources = new HashSet<AssetReference>(resources.size());
            for (final AssetReference activeResource : resources) {
                this.resources.add(new AssetReference(activeResource));
            }
        } else {
            this.resources = new HashSet<AssetReference>();
        }
        if (models != null && models.size() > 0) {
            this.models = new HashSet<AssetReference>(models.size());
            for (final AssetReference activeModel : models) {
                this.models.add(new AssetReference(activeModel));
            }
        } else {
            this.models = new HashSet<AssetReference>();
        }

        if (ksessions != null && ksessions.size() > 0) {
            this.ksessions = new HashMap<String, ServiceKSessionConfig>(ksessions.size());
            for (final ServiceKSessionConfig activeKSession : ksessions) {
                this.ksessions.put(activeKSession.getName(), new ServiceKSessionConfig(activeKSession));
            }
        } else {
            this.ksessions = new HashMap<String, ServiceKSessionConfig>();
        }

        if (kagents != null && kagents.size() > 0) {
            this.kagents = new HashMap<String, ServiceKAgentConfig>(kagents.size());
            for (final ServiceKAgentConfig activeKAgent : kagents) {
                this.kagents.put(activeKAgent.getName(), new ServiceKAgentConfig(activeKAgent));
            }
        } else {
            this.kagents = new HashMap<String, ServiceKAgentConfig>();
        }
    }

    public boolean hasConfig() {
        if (mbeans != null || eventProcessingMode != null ||
                assertBehavior != null || maxThreads != null) {
            return true;
        }
        return false;
    }

    public String getName() {
        return name;
    }

    public Integer getMaxThreads() {
        return maxThreads;
    }

    public Boolean getMbeans() {
        return mbeans;
    }

    public EventProcessingOption getEventProcessingMode() {
        return eventProcessingMode;
    }

    public AssertBehaviorOption getAssertBehavior() {
        return assertBehavior;
    }

    public String getAssetsUser() {
        return assetsUser;
    }

    public String getAssetsPassword() {
        return assetsPassword;
    }

    public Collection<AssetReference> getResources() {
        return resources;
    }

    public Collection<AssetReference> getModels() {
        return models;
    }

    public ServiceKSessionConfig getKsession(final String name) {
        return ksessions.get(name);
    }

    public Collection<ServiceKSessionConfig> getKsessions() {
        return ksessions.values();
    }

    public Collection<ServiceKAgentConfig> getKagents() {
        return kagents.values();
    }

    public void setMaxThreads(final int maxThreads) {
        this.maxThreads = maxThreads;
    }

    public void setMbeans(final boolean mbeans) {
        this.mbeans = mbeans;
    }

    public void setMbeansToNull() {
        this.mbeans = null;
    }

    public void setEventProcessingMode(final EventProcessingOption eventProcessingMode) {
        this.eventProcessingMode = checkNotNull("eventProcessingMode", eventProcessingMode);
    }

    public void setEventProcessingModeToNull() {
        this.eventProcessingMode = null;
    }

    public void setAssertBehavior(final AssertBehaviorOption assertBehavior) {
        this.assertBehavior = checkNotNull("assertBehavior", assertBehavior);
    }

    public void setAssertBehaviorToNull() {
        this.assertBehavior = null;
    }

    public void setAssetsUser(final String assetsUser) {
        this.assetsUser = checkNotEmpty("assetsUser", assetsUser);
    }

    public void setAssetsUserToNull() {
        this.assetsUser = null;
    }

    public void setAssetsPassword(final String assetsPassword) {
        this.assetsPassword = checkNotNull("assetsPassword", assetsPassword);
    }

    public void setAssetsPasswordToNull() {
        this.assetsPassword = null;
    }

    public void addResource(final AssetReference resource) {
        if (resource == null) {
            return;
        }
        this.resources.add(resource);
    }

    public void addResources(final Collection<AssetReference> resources) {
        if (resources == null || resources.size() == 0) {
            return;
        }
        this.resources.addAll(resources);
    }

    public void setResources(Collection<AssetReference> resources) {
        this.resources.clear();
        if (resources == null || resources.size() == 0) {
            return;
        }

        this.resources.addAll(resources);
    }

    public void removeResource(final AssetReference resource) {
        if (resource == null) {
            return;
        }
        this.resources.remove(resource);
    }

    public void addModel(final AssetReference model) {
        if (model == null) {
            return;
        }
        this.models.add(model);
    }

    public void addModels(final Collection<AssetReference> models) {
        if (models == null || models.size() == 0) {
            return;
        }
        this.models.addAll(models);
    }

    public void setModels(final Collection<AssetReference> models) {
        this.models.clear();
        if (models == null || models.size() == 0) {
            return;
        }

        this.models.addAll(models);
    }

    public void removeModel(final AssetReference model) {
        if (model == null) {
            return;
        }
        this.models.remove(model);
    }

    public String getNextKSessionName() {
        int i = 0;
        while (true) {
            i++;
            final String name = "ksession" + i;
            if (!ksessions.containsKey(name)) {
                return name;
            }
        }
    }

    public void addKsession(final ServiceKSessionConfig ksession) {
        if (ksession == null) {
            return;
        }

        if (ksessions.containsKey(ksession.getName())) {
            throw new IllegalArgumentException("Session already exists");
        }

        ksessions.put(ksession.getName(), ksession);
    }

    public void removeKsession(final String ksessionName) {
        if (ksessionName == null || ksessionName.trim().length() == 0) {
            return;
        }

        ksessions.remove(ksessionName);
    }

    public String getNextKAgentName() {
        int i = 0;
        while (true) {
            i++;
            final String name = "kagent" + i;
            if (!kagents.containsKey(name)) {
                return name;
            }
        }
    }

    public void addKagent(final ServiceKAgentConfig kagent) {
        if (kagent == null) {
            return;
        }

        if (kagents.containsKey(kagent.getName())) {
            throw new IllegalArgumentException("Agent already exists");
        }

        kagents.put(kagent.getName(), kagent);
    }

    public void removeKagent(final String kagentName) {
        if (kagentName == null || kagentName.trim().length() == 0) {
            return;
        }

        kagents.remove(kagentName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final ServiceKBaseConfig that = (ServiceKBaseConfig) o;

        if (!name.equals(that.name)) {
            return false;
        }
        if (assertBehavior != that.assertBehavior) {
            return false;
        }
        if (assetsPassword != null ? !assetsPassword.equals(that.assetsPassword) : that.assetsPassword != null) {
            return false;
        }
        if (assetsUser != null ? !assetsUser.equals(that.assetsUser) : that.assetsUser != null) {
            return false;
        }
        if (eventProcessingMode != that.eventProcessingMode) {
            return false;
        }
        if (maxThreads != null ? !maxThreads.equals(that.maxThreads) : that.maxThreads != null) {
            return false;
        }
        if (mbeans != null ? !mbeans.equals(that.mbeans) : that.mbeans != null) {
            return false;
        }
        if (!models.equals(that.models)) {
            return false;
        }
        if (!resources.equals(that.resources)) {
            return false;
        }
        if (!kagents.equals(that.kagents)) {
            return false;
        }
        if (!ksessions.equals(that.ksessions)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + (maxThreads != null ? maxThreads.hashCode() : 0);
        result = 31 * result + (mbeans != null ? mbeans.hashCode() : 0);
        result = 31 * result + (eventProcessingMode != null ? eventProcessingMode.hashCode() : 0);
        result = 31 * result + (assertBehavior != null ? assertBehavior.hashCode() : 0);
        result = 31 * result + (assetsUser != null ? assetsUser.hashCode() : 0);
        result = 31 * result + (assetsPassword != null ? assetsPassword.hashCode() : 0);
        result = 31 * result + resources.hashCode();
        result = 31 * result + models.hashCode();
        result = 31 * result + ksessions.hashCode();
        result = 31 * result + kagents.hashCode();
        return result;
    }
}
