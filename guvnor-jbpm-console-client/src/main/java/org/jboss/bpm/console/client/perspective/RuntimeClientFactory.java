/*
 * Copyright 2011 JBoss Inc
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

package org.jboss.bpm.console.client.perspective;

import com.google.gwt.event.shared.EventBus;
import com.mvc4g.client.Controller;
import org.jboss.bpm.console.client.ApplicationContext;
import org.jboss.bpm.console.client.BpmConsoleClientFactory;
import org.jboss.bpm.console.client.ClientFactoryImpl;
import org.jboss.bpm.console.client.URLBuilder;

public class RuntimeClientFactory extends ClientFactoryImpl
        implements BpmConsoleClientFactory {

    private Controller controller;
    private RuntimeApplicationContext runtimeApplicationContext = new RuntimeApplicationContext();

    public RuntimeClientFactory(EventBus eventBus) {
        super(eventBus);
        URLBuilder.configureInstance(runtimeApplicationContext.getConfig());
    }

    public ApplicationContext getApplicationContext() {
        return runtimeApplicationContext;
    }

    public Controller getController() {
        if (controller == null) {
            controller = new Controller();
        }

        return controller;
    }

}
