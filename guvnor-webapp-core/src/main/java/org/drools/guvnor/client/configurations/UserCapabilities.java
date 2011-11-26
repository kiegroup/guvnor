/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.guvnor.client.configurations;


import java.util.List;

/**
 * This is used to turn off GUI functionality. The server decides what should be visible
 * based on roles and permissions granted. This is essentially a security and permissions function.
 * (however the Capabilities do not enforce actions on the server - these are more for GUI convenience so elements are not displayed
 * that are not relevant to a given users role).
 */
public class UserCapabilities {

    public static UserCapabilities INSTANCE;

    private final List<Capability> capabilities;

    private UserCapabilities(List<Capability> capabilities) {
        this.capabilities = capabilities;
    }

    static void setUp(List<Capability> capabilities) {
        INSTANCE = new UserCapabilities(capabilities);
    }

    public boolean hasCapability(Capability... capabilities) {
        for (Capability capability : capabilities) {
            if (this.capabilities.contains(capability)) {
                return true;
            }
        }

        return false;
    }
}
