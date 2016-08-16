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

package org.guvnor.ala.wildfly.config.impl;

import org.guvnor.ala.config.CloneableConfig;
import org.guvnor.ala.wildfly.config.WildflyProviderConfig;

public class WildflyProviderConfigImpl implements WildflyProviderConfig,
                                                  CloneableConfig<WildflyProviderConfig> {

    private String name;
    private String hostIp;
    private String port;
    private String managementPort;
    private String user;
    private String password;

    public WildflyProviderConfigImpl() {
        this.name = WildflyProviderConfig.super.getName();
        this.hostIp = WildflyProviderConfig.super.getHostIp();
        this.port = WildflyProviderConfig.super.getPort();
        this.managementPort = WildflyProviderConfig.super.getManagementPort();
        this.user = WildflyProviderConfig.super.getUser();
        this.password = WildflyProviderConfig.super.getPassword();

    }

    public WildflyProviderConfigImpl( final String name,
                                      final String hostIp,
                                      final String port,
                                      final String managementPort,
                                      final String user,
                                      final String password ) {
        this.name = name;
        this.hostIp = hostIp;
        this.port = port;
        this.managementPort = managementPort;
        this.user = user;
        this.password = password;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getHostIp() {
        return hostIp;
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
    public WildflyProviderConfig asNewClone( final WildflyProviderConfig origin ) {
        return new WildflyProviderConfigImpl( origin.getName(),
                                              origin.getHostIp(),
                                              origin.getPort(),
                                              origin.getManagementPort(),
                                              origin.getUser(),
                                              origin.getPassword() );
    }
}