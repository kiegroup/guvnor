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

package org.drools.guvnor.server;

import org.drools.guvnor.client.rpc.DetailedSerializationException;
import org.drools.guvnor.client.rpc.WorkItemService;
import org.drools.guvnor.server.ruleeditor.workitem.AssetWorkDefinitionsLoader;
import org.drools.guvnor.server.ruleeditor.workitem.ConfigFileWorkDefinitionsLoader;
import org.drools.guvnor.server.ruleeditor.workitem.WorkDefinitionsLoader;
import org.drools.guvnor.server.ruleeditor.workitem.WorkitemDefinitionElementsManager;
import org.drools.guvnor.server.util.LoggingHelper;
import org.drools.ide.common.shared.workitems.*;
import org.drools.process.core.ParameterDefinition;
import org.drools.process.core.WorkDefinition;
import org.drools.process.core.datatype.DataType;
import org.drools.process.core.datatype.impl.type.*;
import org.jboss.seam.security.annotations.LoggedIn;
import org.jbpm.process.workitem.WorkDefinitionImpl;

import javax.inject.Inject;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class WorkItemServiceImplementation
        implements WorkItemService {


    private static final LoggingHelper log = LoggingHelper.getLogger(WorkItemService.class);

    @Inject
    private RepositoryAssetService repositoryAssetService;


    /**
     * Returns the Workitem Definition elements specified by
     * WorkitemDefinitionElementsManager
     *
     * @return a Map containing the key,value pairs of data.
     * @throws DetailedSerializationException
     */
    public Map<String, String> loadWorkitemDefinitionElementData() throws DetailedSerializationException {
        try {
            return WorkitemDefinitionElementsManager.getInstance().getElements();
        } catch (IOException ex) {
            log.error("Error loading Workitem Definition Elements",
                    ex);
            throw new DetailedSerializationException("Error loading Workitem Definition Elements",
                    "View server logs for more information");
        }
    }

    /**
     * Load and return a Map of all parsed Work Definitions. The source of such
     * Work Definitions is Assets defined in Guvnor and those defined in
     * /workitem-definitions.xml
     *
     * @param packageUUID The Package from which to load Work Items
     * @return
     * @throws org.drools.guvnor.client.rpc.DetailedSerializationException
     *
     */
    @LoggedIn
    public Set<PortableWorkDefinition> loadWorkItemDefinitions(String packageUUID) throws DetailedSerializationException {
        Map<String, WorkDefinition> workDefinitions = new HashMap<String, WorkDefinition>();
        //Load WorkDefinitions from different sources

        try {
            // - Assets
            WorkDefinitionsLoader loader = new AssetWorkDefinitionsLoader(repositoryAssetService,
                    packageUUID);
            Map<String, org.drools.process.core.WorkDefinition> assetWorkDefinitions = loader.getWorkDefinitions();
            for (Map.Entry<String, org.drools.process.core.WorkDefinition> entry : assetWorkDefinitions.entrySet()) {
                if (!workDefinitions.containsKey(entry.getKey())) {
                    workDefinitions.put(entry.getKey(),
                            entry.getValue());
                }
            }
        } catch (Exception e) {
            log.error("Error loading Workitem Definitions for package [" + packageUUID + "]",
                    e);
            throw new DetailedSerializationException("Error loading Workitem Definitions for package [" + packageUUID + "]",
                    "View server logs for more information");
        }

        try {
            // - workitem-definitions.xml
            Map<String, org.drools.process.core.WorkDefinition> configuredWorkDefinitions = ConfigFileWorkDefinitionsLoader.getInstance().getWorkDefinitions();
            for (Map.Entry<String, org.drools.process.core.WorkDefinition> entry : configuredWorkDefinitions.entrySet()) {
                if (!workDefinitions.containsKey(entry.getKey())) {
                    workDefinitions.put(entry.getKey(),
                            entry.getValue());
                }
            }
        } catch (Exception e) {
            log.error("Error loading Workitem Definitions from configuration file",
                    e);
            throw new DetailedSerializationException("Error loading Workitem Definitions from configuration file",
                    "View server logs for more information");
        }

        //Copy the Work Items into Structures suitable for GWT
        Set<PortableWorkDefinition> workItems = new HashSet<PortableWorkDefinition>();
        for (Map.Entry<String, WorkDefinition> entry : workDefinitions.entrySet()) {
            PortableWorkDefinition wid = new PortableWorkDefinition();
            WorkDefinitionImpl wd = (WorkDefinitionImpl) entry.getValue();
            wid.setName(wd.getName());
            wid.setDisplayName(wd.getDisplayName());
            wid.setParameters(convertWorkItemParameters(entry.getValue().getParameters()));
            wid.setResults(convertWorkItemParameters(entry.getValue().getResults()));
            workItems.add(wid);
        }
        return workItems;
    }

    private Set<PortableParameterDefinition> convertWorkItemParameters(Set<ParameterDefinition> parameters) {
        Set<PortableParameterDefinition> pps = new HashSet<PortableParameterDefinition>();
        for (ParameterDefinition pd : parameters) {
            DataType pdt = pd.getType();
            PortableParameterDefinition ppd = null;
            if (pdt instanceof BooleanDataType) {
                ppd = new PortableBooleanParameterDefinition();
            } else if (pdt instanceof FloatDataType) {
                ppd = new PortableFloatParameterDefinition();
            } else if (pdt instanceof IntegerDataType) {
                ppd = new PortableIntegerParameterDefinition();
            } else if (pdt instanceof ListDataType) {
                //TODO ListDataType
                //ppd = new PortableListParameterDefinition();
            } else if (pdt instanceof ObjectDataType) {
                ppd = new PortableObjectParameterDefinition();
                PortableObjectParameterDefinition oppd = (PortableObjectParameterDefinition) ppd;
                ObjectDataType odt = (ObjectDataType) pdt;
                oppd.setClassName(odt.getClassName());
            } else if (pd.getType() instanceof StringDataType) {
                ppd = new PortableStringParameterDefinition();
            } else if (pdt instanceof EnumDataType) {
                //TODO EnumDataType
                //ppd = new PortableEnumParameterDefinition();
                //PortableEnumParameterDefinition eppd = (PortableEnumParameterDefinition) ppd;
                //EnumDataType epdt = (EnumDataType) pdt;
                //eppd.setClassName( epdt.getClassName() );
                //if ( epdt.getValueMap() != null ) {
                //    eppd.setValues( epdt.getValueNames() );
                //}
            }
            if (ppd != null) {
                ppd.setName(pd.getName());
                pps.add(ppd);
            }
        }
        return pps;
    }
}
