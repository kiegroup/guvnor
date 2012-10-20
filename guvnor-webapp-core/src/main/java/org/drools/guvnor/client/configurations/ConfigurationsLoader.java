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

package org.drools.guvnor.client.configurations;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.rpc.ConfigurationService;
import org.drools.guvnor.client.rpc.ConfigurationServiceAsync;

import java.util.Map;

public class ConfigurationsLoader {

    public static void loadPreferences(final Command command) {
        ConfigurationServiceAsync configurationService = GWT.create(ConfigurationService.class);
        configurationService.loadPreferences(new GenericCallback<Map<String, String>>() {
            public void onSuccess(Map<String, String> map) {
                ApplicationPreferences.setUp(map);
                executeCommand(command);
            }
        });
    }

    private static void executeCommand(Command command) {
        if (command != null) {
            command.execute();
        }
    }
}
