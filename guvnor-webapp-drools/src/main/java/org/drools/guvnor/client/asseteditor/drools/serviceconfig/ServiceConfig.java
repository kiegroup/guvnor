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

import java.util.ArrayList;
import java.util.Collection;

import org.drools.guvnor.client.rpc.MavenArtifact;
import org.drools.ide.common.client.modeldriven.brl.PortableObject;

import static java.util.Collections.*;
import static org.drools.guvnor.client.util.Preconditions.*;

public class ServiceConfig
        implements PortableObject {

    private static final long serialVersionUID = -660354431823570247L;

    public enum Protocol {
        REST, WEB_SERVICE;
    }

    final String version = "1.0";
    int pollingFrequency = 60;
    Protocol protocol = Protocol.REST;
    Collection<AssetReference> resources = new ArrayList<AssetReference>();
    Collection<AssetReference> models = new ArrayList<AssetReference>();
    Collection<MavenArtifact> excludedArtifacts = new ArrayList<MavenArtifact>();

    public ServiceConfig() {
    }

    public ServiceConfig(final ServiceConfig source) {
        checkNotNull("source", source);
        this.pollingFrequency = source.pollingFrequency;
        this.protocol = source.protocol;
        this.resources.addAll(source.resources);
        this.models.addAll(source.models);
        this.excludedArtifacts.addAll(source.excludedArtifacts);
    }

    public ServiceConfig(final String pollingFrequency, final String protocol,
            final Collection<AssetReference> resources, final Collection<AssetReference> models,
            final Collection<MavenArtifact> excludedArtifacts) {
        checkCondition("pollingFrequency must be numeric", isNumeric(pollingFrequency));

        this.pollingFrequency = Integer.valueOf(pollingFrequency);
        this.protocol = convertToProtocol(protocol);
        if (resources == null) {
            this.resources = emptyList();
        } else {
            this.resources = new ArrayList<AssetReference>(resources);
        }
        if (models == null) {
            this.models = emptyList();
        } else {
            this.models = new ArrayList<AssetReference>(models);
        }
        if (excludedArtifacts == null) {
            this.excludedArtifacts = emptyList();
        } else {
            this.excludedArtifacts = new ArrayList<MavenArtifact>(excludedArtifacts);
        }
    }

    private boolean isNumeric(final String value) {
        try {
            Integer.valueOf(value);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    private Protocol convertToProtocol(final String value) {
        if (value.toLowerCase().equals("ws") || value.toLowerCase().equals("web_service")) {
            return Protocol.WEB_SERVICE;
        }
        return Protocol.REST;
    }

    public int getPollingFrequency() {
        return pollingFrequency;
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public Collection<AssetReference> getResources() {
        return resources;
    }

    public Collection<AssetReference> getModels() {
        return models;
    }

    public Collection<MavenArtifact> getExcludedArtifacts() {
        return excludedArtifacts;
    }

    public synchronized void setExcludedArtifacts(final Collection<MavenArtifact> excludedItems) {
        this.excludedArtifacts.clear();
        this.excludedArtifacts.addAll(excludedItems);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ServiceConfig config = (ServiceConfig) o;

        if (pollingFrequency != config.pollingFrequency) {
            return false;
        }
        if (!excludedArtifacts.equals(config.excludedArtifacts)) {
            return false;
        }
        if (!models.equals(config.models)) {
            return false;
        }
        if (protocol != config.protocol) {
            return false;
        }
        if (!resources.equals(config.resources)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = pollingFrequency;
        result = 31 * result + protocol.hashCode();
        result = 31 * result + resources.hashCode();
        result = 31 * result + models.hashCode();
        result = 31 * result + excludedArtifacts.hashCode();
        return result;
    }

    public static class AssetReference
            implements PortableObject {

        private static final long serialVersionUID = 6831529719441561353L;

        private String pkg;
        private String name;
        private String format;
        private String url;
        private String uuid;

        public AssetReference() {
        }

        public AssetReference(final AssetReference source) {
            checkNotNull("source", source);

            this.pkg = source.pkg;
            this.name = source.name;
            this.format = source.format;
            this.url = source.url;
            this.uuid = source.uuid;
        }

        public AssetReference(final String packageRef, final String name, final String format, final String url, final String uuid) {
            this.pkg = checkNotEmpty("packageRef", packageRef);
            this.name = checkNotEmpty("name", name);
            this.format = checkNotEmpty("format", format);
            this.url = checkNotEmpty("format", url);
            this.uuid = checkNotEmpty("format", uuid);
        }

        public String getUuid() {
            return uuid;
        }

        public String getPkg() {
            return pkg;
        }

        public String getName() {
            return name;
        }

        public String getFormat() {
            return format;
        }

        public String getUrl() {
            return url;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            AssetReference that = (AssetReference) o;

            if (!pkg.equals(that.pkg)) {
                return false;
            }
            if (!format.equals(that.format)) {
                return false;
            }
            if (!format.equals(that.format)) {
                return false;
            }
            if (!url.equals(that.url)) {
                return false;
            }
            if (!uuid.equals(that.uuid)) {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode() {
            int result = pkg.hashCode();
            result = 31 * result + format.hashCode();
            result = 31 * result + format.hashCode();
            result = 31 * result + url.hashCode();
            result = 31 * result + uuid.hashCode();
            return result;
        }
    }
}
