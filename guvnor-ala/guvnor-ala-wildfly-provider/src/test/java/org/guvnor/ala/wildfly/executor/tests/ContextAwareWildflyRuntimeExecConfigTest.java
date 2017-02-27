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

package org.guvnor.ala.wildfly.executor.tests;

import java.util.HashMap;
import java.util.Map;

import org.guvnor.ala.build.maven.model.MavenBinary;
import org.guvnor.ala.wildfly.config.WildflyRuntimeExecConfig;
import org.guvnor.ala.wildfly.config.impl.ContextAwareWildflyRuntimeExecConfig;
import org.guvnor.ala.wildfly.model.WildflyProvider;
import org.junit.Test;
import org.uberfire.java.nio.file.Path;

import static java.util.Collections.singletonMap;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.guvnor.ala.util.VariableInterpolation.interpolate;

public class ContextAwareWildflyRuntimeExecConfigTest {

    @Test
    public void testDefaultExpression() {
        assertEquals("${input.war-path}", new ContextAwareWildflyRuntimeExecConfig().getWarPath());
    }

    @Test
    public void testContextUsingMavenBinary() {
        final ContextAwareWildflyRuntimeExecConfig config = new ContextAwareWildflyRuntimeExecConfig();
        final Map<String, Object> context = new HashMap<>();
        final WildflyProvider provider = mock(WildflyProvider.class);
        context.put("wildfly-provider", provider);
        final MavenBinary binary = mock(MavenBinary.class);
        final Path path = mock(Path.class);
        when(binary.getPath()).thenReturn(path);
        final String filePath = "/path/to/file.war";
        when(path.toString()).thenReturn(filePath);
        context.put("binary", binary);

        config.setContext(context);

        assertEquals(provider, config.getProviderId());
        assertEquals(filePath, config.getWarPath());

        final WildflyRuntimeExecConfig configClone = config.asNewClone(config);
        assertEquals(provider, configClone.getProviderId());
        assertEquals(filePath, configClone.getWarPath());
    }

    @Test
    public void testContextUsingPath() {
        final ContextAwareWildflyRuntimeExecConfig config = new ContextAwareWildflyRuntimeExecConfig();
        final WildflyProvider provider = mock(WildflyProvider.class);
        final Map<String, Object> context = singletonMap("wildfly-provider", provider);

        config.setContext(context);

        assertEquals(provider, config.getProviderId());
        assertEquals("${input.war-path}", config.getWarPath());

        final WildflyRuntimeExecConfig configClone = config.asNewClone(config);
        assertEquals(provider, configClone.getProviderId());
        assertEquals("${input.war-path}", configClone.getWarPath());
    }

    @Test
    public void testVariablesResolution() {
        final String filePath = "/path/to/file.war";
        final String redeploy = "none";

        Map<String, String> values = new HashMap<>();
        values.put("war-path", filePath);
        values.put("redeploy", redeploy);

        final ContextAwareWildflyRuntimeExecConfig config = new ContextAwareWildflyRuntimeExecConfig();
        final ContextAwareWildflyRuntimeExecConfig varConfig = interpolate(singletonMap("input", values), config);
        assertEquals(filePath, varConfig.getWarPath());
        assertEquals(redeploy, varConfig.getRedeployStrategy());
    }

}