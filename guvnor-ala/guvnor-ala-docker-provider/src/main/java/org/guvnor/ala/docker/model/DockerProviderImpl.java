/*
 * Copyright 2016 JBoss, by Red Hat, Inc
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
package org.guvnor.ala.docker.model;

import org.guvnor.ala.config.CloneableConfig;
import org.guvnor.ala.config.ProviderConfig;
import org.guvnor.ala.runtime.providers.base.BaseProvider;

public class DockerProviderImpl extends BaseProvider implements DockerProvider,
                                                                CloneableConfig<DockerProvider> {

    private String hostId;

    public DockerProviderImpl() {
    }

    public DockerProviderImpl( final String name,
                               final String hostId, ProviderConfig config ) {
        super( name, DockerProviderType.instance(), config );
        this.hostId = hostId;
    }

    public String getHostId() {
        return hostId;
    }

    public void setHostId( String hostId ) {
        this.hostId = hostId;
    }

    @Override
    public DockerProvider asNewClone( final DockerProvider source ) {
        return new DockerProviderImpl( source.getId(), source.getHostId(), source.getConfig() );
    }
}