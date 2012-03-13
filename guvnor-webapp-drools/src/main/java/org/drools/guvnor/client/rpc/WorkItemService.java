/*
 * Copyright 2012 JBoss Inc
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

package org.drools.guvnor.client.rpc;

import com.google.gwt.user.client.rpc.RemoteService;
import org.drools.ide.common.shared.workitems.PortableWorkDefinition;

import java.util.Map;
import java.util.Set;

public interface WorkItemService
        extends
        RemoteService {

    /**
     * Returns the Workitem Definition elements specified by
     * WorkitemDefinitionElementsManager
     *
     * @return a Map containing the key,value pairs of data.
     * @throws DetailedSerializationException
     */
    public Map<String, String> loadWorkitemDefinitionElementData() throws DetailedSerializationException;

    /**
     * Load and return a List of all parsed Work Definitions. The source of such
     * Work Definitions is Assets defined in Guvnor and those defined in
     * /workitemDefinitionElements.properties.
     *
     * @param packageUUID The Package UUID for which Work Definitions should be loaded
     * @return
     * @throws DetailedSerializationException
     */
    public Set<PortableWorkDefinition> loadWorkItemDefinitions(String packageUUID) throws DetailedSerializationException;

}
