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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.drools.guvnor.client.rpc.MavenArtifact;
import org.drools.ide.common.client.modeldriven.brl.PortableObject;

import static org.drools.guvnor.client.util.Preconditions.*;

public class ServiceConfig
        implements PortableObject {

    private static final long serialVersionUID = -5738821650999392917L;

    private final String version = "1.0";

    private Integer pollingFrequency;
    private Set<MavenArtifact> excludedArtifacts;
    private Map<String, ServiceKBaseConfig> kbases;

    public ServiceConfig() {
        this.pollingFrequency = null;
        this.excludedArtifacts = new HashSet<MavenArtifact>();
        this.kbases = new HashMap<String, ServiceKBaseConfig>();
    }

    public ServiceConfig(final ServiceConfig source) {
        checkNotNull("source", source);
        this.pollingFrequency = source.pollingFrequency;
        if (source.excludedArtifacts == null) {
            this.excludedArtifacts = new HashSet<MavenArtifact>();
        } else {
            this.excludedArtifacts = new HashSet<MavenArtifact>(source.excludedArtifacts.size());
            for (final MavenArtifact excludedArtifact : source.excludedArtifacts) {
                this.excludedArtifacts.add(new MavenArtifact(excludedArtifact));
            }
        }

        if (source.kbases == null) {
            this.kbases = new HashMap<String, ServiceKBaseConfig>();
        } else {
            this.kbases = new HashMap<String, ServiceKBaseConfig>(source.kbases.size());
            for (Map.Entry<String, ServiceKBaseConfig> activeKBase : source.kbases.entrySet()) {
                kbases.put(activeKBase.getKey().toLowerCase(), new ServiceKBaseConfig(activeKBase.getValue()));
            }
        }
    }

    public ServiceConfig(final String pollingFrequency,
            final Collection<MavenArtifact> excludedArtifacts,
            final Collection<ServiceKBaseConfig> kbases) {
        if (pollingFrequency != null) {
            checkCondition("pollingFrequency must be numeric", isNumeric(pollingFrequency));
            this.pollingFrequency = Integer.valueOf(pollingFrequency);
        }

        if (excludedArtifacts == null) {
            this.excludedArtifacts = new HashSet<MavenArtifact>();
        } else {
            this.excludedArtifacts = new HashSet<MavenArtifact>(excludedArtifacts.size());
            for (final MavenArtifact excludedArtifact : excludedArtifacts) {
                this.excludedArtifacts.add(new MavenArtifact(excludedArtifact));
            }
        }

        if (kbases == null) {
            this.kbases = new HashMap<String, ServiceKBaseConfig>();
        } else {
            this.kbases = new HashMap<String, ServiceKBaseConfig>(kbases.size());
            for (ServiceKBaseConfig activeKBase : kbases) {
                this.kbases.put(activeKBase.getName().toLowerCase(), new ServiceKBaseConfig(activeKBase));
            }
        }
    }

    public Collection<AssetReference> getModels() {
        final Set<AssetReference> result = new HashSet<AssetReference>();
        for (final ServiceKBaseConfig kbase : kbases.values()) {
            result.addAll(kbase.getModels());
        }
        return result;
    }

    public boolean hasProtocolReference(final ProtocolOption protocol) {
        checkNotNull("protocol", protocol);
        for (final ServiceKBaseConfig kbase : kbases.values()) {
            for (final ServiceKSessionConfig ksession : kbase.getKsessions()) {
                if (ksession.getProtocol().equals(protocol)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isNumeric(final String value) {
        try {
            Integer.valueOf(value);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    public Integer getPollingFrequency() {
        return pollingFrequency;
    }

    public void setPollingFrequency(int pollingFrequency) {
        this.pollingFrequency = pollingFrequency;
    }

    public ServiceKBaseConfig getKbase(final String kbaseName) {
        if (kbaseName == null || kbaseName.trim().length() == 0) {
            return null;
        }
        return kbases.get(kbaseName.toLowerCase());
    }

    public Collection<ServiceKBaseConfig> getKbases() {
        return kbases.values();
    }

    public void addKBase(final ServiceKBaseConfig kbase) {
        if (kbase == null) {
            return;
        }

        if (kbases.containsKey(kbase.getName().toLowerCase())) {
            throw new IllegalArgumentException("KBase already exists.");
        }
        kbases.put(kbase.getName().toLowerCase(), kbase);
    }

    public void removeKBase(final String kbase) {
        if (kbase == null || kbase.trim().length() == 0) {
            return;
        }
        kbases.remove(kbase.toLowerCase());
    }

    public Collection<MavenArtifact> getExcludedArtifacts() {
        return excludedArtifacts;
    }

    public void setExcludedArtifacts(final Collection<MavenArtifact> excludedItems) {
        if (excludedItems == null) {
            return;
        }
        excludedArtifacts = new HashSet<MavenArtifact>(excludedItems);
    }

    public void addExcludedArtifacts(final Collection<MavenArtifact> excludedItems) {
        if (excludedItems == null || excludedItems.size() == 0) {
            return;
        }
        excludedArtifacts.addAll(excludedItems);
    }

    public void addExcludedArtifact(final MavenArtifact artifact) {
        if (artifact == null) {
            return;
        }
        excludedArtifacts.add(artifact);
    }

    public void removeExcludedArtifacts(final Collection<MavenArtifact> excludedItems) {
        if (excludedItems == null || excludedItems.size() == 0) {
            return;
        }
        excludedArtifacts.removeAll(excludedItems);
    }

    public void removeExcludedArtifact(final MavenArtifact artifact) {
        if (artifact == null) {
            return;
        }
        excludedArtifacts.remove(artifact);
    }

    public String getNextKBaseName() {
        int i = 0;
        while (true) {
            i++;
            final String name = "kbase" + i;
            if (!kbases.containsKey(name)) {
                return name;
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ServiceConfig that = (ServiceConfig) o;

        if (!excludedArtifacts.equals(that.excludedArtifacts)) {
            return false;
        }
        if (!kbases.equals(that.kbases)) {
            return false;
        }
        if (pollingFrequency != null ? !pollingFrequency.equals(that.pollingFrequency) : that.pollingFrequency != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = pollingFrequency != null ? pollingFrequency.hashCode() : 0;
        result = 31 * result + excludedArtifacts.hashCode();
        result = 31 * result + kbases.hashCode();
        return result;
    }
}
