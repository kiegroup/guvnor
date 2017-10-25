/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.common.services.project.backend.server.utils;

import org.guvnor.common.services.project.backend.server.utils.configuration.ConfigurationKey;
import org.guvnor.common.services.project.backend.server.utils.configuration.ConfigurationStrategy;
import org.guvnor.common.services.project.backend.server.utils.configuration.Order;

import javax.enterprise.context.ApplicationScoped;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


/**
 * Default implementation, this class can be extended to change the protected configuration Map
 */

public class ConfigurationStaticStrategy implements ConfigurationStrategy,
        Order {

    protected Map<ConfigurationKey, String> conf;

    private Boolean valid = Boolean.FALSE;

    public ConfigurationStaticStrategy() {
        conf = new HashMap<>();

        conf.put(ConfigurationKey.MAVEN_PLUGINS,
                 "org.apache.maven.plugins");
        conf.put(ConfigurationKey.MAVEN_COMPILER_PLUGIN,
                 "maven-compiler-plugin");
        conf.put(ConfigurationKey.MAVEN_COMPILER_PLUGIN_VERSION,
                 "3.6.1");

        conf.put(ConfigurationKey.ALTERNATIVE_COMPILER_PLUGINS,
                 "io.takari.maven.plugins");
        conf.put(ConfigurationKey.ALTERNATIVE_COMPILER_PLUGIN,
                 "takari-lifecycle-plugin");
        conf.put(ConfigurationKey.ALTERNATIVE_COMPILER_PLUGIN_VERSION,
                 "1.13.0");

        conf.put(ConfigurationKey.KIE_MAVEN_PLUGINS,
                 "org.kie");
        conf.put(ConfigurationKey.KIE_MAVEN_PLUGIN,
                 "kie-maven-plugin");
        conf.put(ConfigurationKey.KIE_TAKARI_PLUGIN,
                 "kie-takari-plugin");

        conf.put(ConfigurationKey.KIE_VERSION,
                "7.5.0-SNAPSHOT");

        valid = Boolean.TRUE;
    }

    @Override
    public Map<ConfigurationKey, String> loadConfiguration() {
        return Collections.unmodifiableMap(conf);
    }

    @Override
    public Boolean isValid() {
        return valid;
    }

    @Override
    public Integer getOrder() {
        return Integer.valueOf(1000);
    }
}
