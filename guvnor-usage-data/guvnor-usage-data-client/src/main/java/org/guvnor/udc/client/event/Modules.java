/*
 * Copyright 2013 JBoss by Red Hat.
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

package org.guvnor.udc.client.event;

public enum Modules {
    
    SHOWCASE("jbpm-console-ng-showcase"),
    HUMAN_TASKS("jbpm-console-ng-human-tasks"),
    UDC("jbpm-console-ng-usage-data");
    
    private String nameModule;
    
    Modules(String module){
        nameModule = module; 
    }

    public String getNameModule() {
        return nameModule;
    }

}
