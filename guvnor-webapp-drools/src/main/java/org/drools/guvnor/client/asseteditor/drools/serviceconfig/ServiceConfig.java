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

    private int pollingFrequency;
    private Protocol protocol;
    private Collection<AssetReference> resources;
    private Collection<AssetReference> models;
    private Collection<MavenArtifact> excludedArtifacts;

    public ServiceConfig(){
    }

    public ServiceConfig(final String assetContent) {
        checkNotNull("assetContent", assetContent);
        this.resources = new ArrayList<AssetReference>();
        this.models = new ArrayList<AssetReference>();
        this.excludedArtifacts = new ArrayList<MavenArtifact>();
        int localPollingFrequency = 60;
        Protocol localProtocol = Protocol.REST;

        final String[] lines = assetContent.split("\n");
        for (final String line : lines) {
            if (line.startsWith("polling=")) {
                localPollingFrequency = Integer.valueOf(line.substring(8));
            } else if (line.startsWith("protocol=")) {
                localProtocol = convertToProtocol(line.substring(9));
            } else if (line.startsWith("resource=")) {
                this.resources.add(new AssetReference(line.substring(9)));
            } else if (line.startsWith("model=")) {
                this.models.add(new AssetReference(line.substring(6)));
            } else if (line.startsWith("excluded.artifact=")) {
                this.excludedArtifacts.add(new MavenArtifact(line.substring(18)));
            }
        }

        this.pollingFrequency = localPollingFrequency;
        this.protocol = localProtocol;
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

    public synchronized void setExcludedArtifacts(Collection<MavenArtifact> excludedItems) {
        this.excludedArtifacts.clear();
        this.excludedArtifacts.addAll(excludedItems);
    }

    public String toContent() {
        final StringBuilder sb = new StringBuilder();
        sb.append("polling=")
                .append(pollingFrequency)
                .append("\nprotocol=")
                .append(protocol.toString())
                .append('\n');

        for (AssetReference resource : resources) {
            sb.append("resource=").append(resource.toValue()).append('\n');
        }

        for (AssetReference model : models) {
            sb.append("model=").append(model.toValue()).append('\n');
        }

        for (MavenArtifact artifact : excludedArtifacts) {
            sb.append("excluded.artifact=").append(artifact.toValue()).append('\n');
        }

        return sb.toString();
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

        public AssetReference(){
        }

        public AssetReference(final String value) {
            checkNotEmpty("value", value);
            final String[] values = value.split("\\|");
            checkCondition("invalid string format", values.length == 5);

            this.pkg = values[0];
            this.name = values[1];
            this.format = values[2];
            this.url = values[3];
            this.uuid = values[4];
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

        public String toValue() {
            return pkg + "|" + name + "|" + format + "|" + url + "|" + uuid;
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
