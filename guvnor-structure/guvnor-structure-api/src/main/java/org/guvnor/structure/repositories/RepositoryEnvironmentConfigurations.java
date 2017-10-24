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

package org.guvnor.structure.repositories;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.errai.common.client.api.annotations.Portable;

import static org.guvnor.structure.repositories.EnvironmentParameters.INIT;
import static org.guvnor.structure.repositories.EnvironmentParameters.ORIGIN;
import static org.guvnor.structure.repositories.EnvironmentParameters.PASSWORD;
import static org.guvnor.structure.repositories.EnvironmentParameters.USER_NAME;

@Portable
public class RepositoryEnvironmentConfigurations {

    private Map<String, Object> configurationMap = new HashMap<>();

    public Map<String, Object> getConfigurationMap() {
        return configurationMap;
    }

    public List<RepositoryEnvironmentConfiguration> getConfigurationList() {

        List<RepositoryEnvironmentConfiguration> list = new ArrayList<>();

        for (Map.Entry<String, Object> entry : configurationMap.entrySet()) {
            list.add(new RepositoryEnvironmentConfiguration(entry.getKey(),
                                                            entry.getValue()));
        }

        return list;
    }

    public boolean containsConfiguration(final String configurationName) {
        return configurationMap.containsKey(configurationName);
    }

    public void setOrigin(final String origin) {
        configurationMap.put(ORIGIN,
                             origin);
    }

    public void setUserName(final String user) {
        configurationMap.put(USER_NAME,
                             user);
    }

    public void setPassword(final String password) {
        configurationMap.put(PASSWORD,
                             password);
    }

    public void setInit(final boolean init) {
        configurationMap.put(INIT,
                             init);
    }

    public Object getUserName() {
        return configurationMap.get(USER_NAME);
    }

    public Object getPassword() {
        return configurationMap.get(PASSWORD);
    }

    public Object getOrigin() {
        return configurationMap.get(ORIGIN);
    }
}
