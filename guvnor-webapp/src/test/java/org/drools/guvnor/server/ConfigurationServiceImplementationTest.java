/*
 * Copyright 2011 JBoss Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package org.drools.guvnor.server;

import java.util.Map;

import org.drools.guvnor.client.rpc.ConfigurationService;
import org.drools.repository.RulesRepository;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ConfigurationServiceImplementationTest extends GuvnorTestBase {

    private ConfigurationService configurationService;

    @Before
    public void setUpConfigurationService() {
        configurationService = new ConfigurationServiceImplementation();
    }

    @Test
    public void testLoadApplicationPreferences() throws Exception {
        Map<String, String> preferences = configurationService.loadPreferences();

        assertNotNull(preferences);
    }

}
