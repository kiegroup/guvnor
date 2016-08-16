/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.ala.docker.config.impl;

import org.guvnor.ala.config.CloneableConfig;
import org.guvnor.ala.docker.config.DockerProviderConfig;

public class DockerProviderConfigImpl implements DockerProviderConfig,
                                                 CloneableConfig<DockerProviderConfig> {

    private String name;
    private String hostIp;

    public DockerProviderConfigImpl() {
        this.name = DockerProviderConfig.super.getName();
        this.hostIp = DockerProviderConfig.super.getHostIp();

    }

    public DockerProviderConfigImpl( final String name,
                                     final String hostIp ) {
        this.name = name;
        this.hostIp = hostIp;
    }

    @Override
    public String getHostIp() {
        return hostIp;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public void setHostIp( String hostIp ) {
        this.hostIp = hostIp;
    }

    @Override
    public String toString() {
        return "DockerProviderConfigImpl{" + "name=" + name + ", hostIp=" + hostIp + '}';
    }

    @Override
    public DockerProviderConfig asNewClone( final DockerProviderConfig origin ) {
        return new DockerProviderConfigImpl( origin.getName(), origin.getHostIp() );
    }
}