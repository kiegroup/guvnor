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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.guvnor.ala.wildfly.model;

import org.guvnor.ala.config.CloneableConfig;
import org.guvnor.ala.runtime.providers.base.BaseProvider;
import org.guvnor.ala.wildfly.config.impl.WildflyProviderConfigImpl;

public class WildflyProviderImpl extends BaseProvider implements WildflyProvider,
                                                                 CloneableConfig<WildflyProvider> {

    private final String hostId;
    private final String port;
    private final String managementPort;
    private final String user;
    private final String password;

    public WildflyProviderImpl( final String name,
                                final String hostId,
                                String port,
                                String managementPort,
                                String user,
                                String password ) {
        super( name, WildflyProviderType.instance(), 
                new WildflyProviderConfigImpl(name, hostId, port, managementPort, user, password ) );
        this.hostId = hostId;
        this.port = port;
        this.managementPort = managementPort;
        this.user = user;
        this.password = password;

    }

    @Override
    public String getHostId() {
        return hostId;
    }

    @Override
    public String getPort() {
        return port;
    }

    @Override
    public String getManagementPort() {
        return managementPort;
    }

    @Override
    public String getUser() {
        return user;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public WildflyProvider asNewClone( final WildflyProvider origin ) {
        return new WildflyProviderImpl( origin.getId(),
                                        origin.getHostId(),
                                        origin.getPort(),
                                        origin.getManagementPort(),
                                        origin.getUser(),
                                        origin.getPassword() );
    }
}