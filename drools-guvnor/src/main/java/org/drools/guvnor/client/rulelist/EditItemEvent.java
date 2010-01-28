package org.drools.guvnor.client.rulelist;
/*
 * Copyright 2005 JBoss Inc
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



/**
 * This is used by the list view to "open" an item.
 * @author Michael Neale
 */
public interface EditItemEvent {
    
    /**
     * @param key - the UUID to open.
     * @param type - the resource type.
     */
    public void open(String key);
    
    /**
     * Open several assets into the same tab.
     * 
     * @param keys - the UUIDs to open.
     */
    public void open(String[] keys);
}