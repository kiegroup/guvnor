/*
 * Copyright 2013 JBoss Inc
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

package org.guvnor.udc.client.event;


public enum EventsUsageData {
    
    HUMAN_TASKS_CREATED("TASK", "CREATE",  Modules.HUMAN_TASKS),
    HUMAN_TASKS_CREATED_STARTED("TASK", "CREATE-START", Modules.HUMAN_TASKS),
    HUMAN_TASKS_STARTED("TASK", "START", Modules.HUMAN_TASKS),
    HUMAN_TASKS_RELEASED("TASK", "RELEASED", Modules.HUMAN_TASKS),
    HUMAN_TASKS_COMPLETED("TASK", "COMPLETED", Modules.HUMAN_TASKS),
    HUMAN_TASKS_CLAIMED("TASK", "CLAIMED", Modules.HUMAN_TASKS),
    HUMAN_TASKS_CSC("TASK", "CREATE-START-CLAIM", Modules.HUMAN_TASKS),
    UDC_SEARCH("USABILITY", "SEARCH", Modules.UDC );
    
    
    private String component;
    private String action;
    private String module;
    
    EventsUsageData(String component, String type, Modules module){
        this.component = component;
        this.action = type;
        this.module = module.getNameModule();
    }

	public String getAction() {
		return action;
	}

    public String getComponent() {
        return component;
    }

    public String getModule() {
        return module;
    }
    
}