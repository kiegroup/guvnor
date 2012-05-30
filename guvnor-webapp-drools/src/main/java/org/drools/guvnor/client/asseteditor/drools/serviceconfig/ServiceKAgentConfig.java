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
import java.util.HashSet;
import java.util.Set;


import static org.drools.guvnor.client.util.Preconditions.*;

public class ServiceKAgentConfig
        implements PortableObject {

    private static final long serialVersionUID = 3792549986118964155L;

    private String name;
    private Boolean newInstance;
    private Boolean useKBaseClassloader;
    private Set<AssetReference> resources;

    public ServiceKAgentConfig() {
        //necessary for serialization
    }

    public ServiceKAgentConfig(final String name,
            final Boolean newInstance,
            final Boolean useKBaseClassloader,
            final Collection<AssetReference> resources) {
        setupNewInstance(name, newInstance, useKBaseClassloader, resources);
    }

    public ServiceKAgentConfig(final ServiceKAgentConfig kagent) {
        checkNotNull("kagent", kagent);
        setupNewInstance(kagent.name, kagent.newInstance, kagent.useKBaseClassloader, kagent.resources);
    }

    public ServiceKAgentConfig(final String name) {
        checkNotEmpty("name", name);
        setupNewInstance(name, null, null, null);
    }

    private void setupNewInstance(final String name,
            final Boolean newInstance,
            final Boolean useKBaseClassloader,
            final Collection<AssetReference> resources) {
        this.name = checkNotEmpty("name", name);
        this.newInstance = newInstance;
        this.useKBaseClassloader = useKBaseClassloader;
        if (resources != null && resources.size() > 0) {
            this.resources = new HashSet<AssetReference>(resources.size());
            for (final AssetReference resource : resources) {
                this.resources.add(new AssetReference(resource));
            }
        } else {
            this.resources = new HashSet<AssetReference>();
        }
    }

    public String getName() {
        return name;
    }

    public Boolean getNewInstance() {
        return newInstance;
    }

    public Boolean getUseKBaseClassloader() {
        return useKBaseClassloader;
    }

    public Collection<AssetReference> getResources() {
        return resources;
    }

    public void setNewInstance(final boolean newInstance) {
        this.newInstance = newInstance;
    }

    public void setUseKBaseClassloader(final boolean useKBaseClassloader) {
        this.useKBaseClassloader = useKBaseClassloader;
    }

    public void addResources(final Collection<AssetReference> resources) {
        if (resources == null || resources.size() == 0) {
            return;
        }

        for (final AssetReference resource : resources) {
            this.resources.add(new AssetReference(resource));
        }
    }

    public void addResource(final AssetReference resource) {
        checkNotNull("resource", resource);
        this.resources.add(new AssetReference(resource));
    }

    public void removeResource(final AssetReference resource) {
        if (resource == null) {
            return;
        }
        this.resources.remove(resource);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final ServiceKAgentConfig that = (ServiceKAgentConfig) o;

        if (!name.equals(that.name)) {
            return false;
        }
        if (newInstance != null ? !newInstance.equals(that.newInstance) : that.newInstance != null) {
            return false;
        }
        if (useKBaseClassloader != null ? !useKBaseClassloader.equals(that.useKBaseClassloader) : that.useKBaseClassloader != null) {
            return false;
        }
        if (resources != null ? !resources.equals(that.resources) : that.resources != null) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + (newInstance != null ? newInstance.hashCode() : 0);
        result = 31 * result + (useKBaseClassloader != null ? useKBaseClassloader.hashCode() : 0);
        result = 31 * result + (resources != null ? resources.hashCode() : 0);
        return result;
    }
}
