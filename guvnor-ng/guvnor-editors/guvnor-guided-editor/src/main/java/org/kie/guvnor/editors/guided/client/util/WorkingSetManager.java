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

package org.kie.guvnor.editors.guided.client.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kie.guvnor.widgets.factconstraints.client.widget.customform.CustomFormConfiguration;
import org.kie.guvnor.widgets.factconstraints.client.widget.helper.CustomFormsContainer;
import org.uberfire.backend.vfs.Path;

/**
 *
 */
public class WorkingSetManager {

    private static WorkingSetManager INSTANCE = new WorkingSetManager();

    private Map<Path, Set<WorkingSetConfigData>> activeWorkingSets = new HashMap<Path, Set<WorkingSetConfigData>>();

    /**
     * This attribute should be sever side. Maybe in some FactConstraintConfig
     * object.
     */
    private boolean autoVerifierEnabled = false;

    public synchronized static WorkingSetManager getInstance() {
        return INSTANCE;
    }

    /**
     * Returns the associated CustomFormConfiguration for a given FactType and FieldName.
     * Because CustomFormConfiguration is stored inside a WorkingSet, the
     * packageName attribute is used to retrieve all the active WorkingSets.
     * If more than one active WorkingSet contain a CustomFormConfiguration for
     * the given FactType and FieldName the first to be found (in any specific
     * nor deterministic order) will be returned.
     * @param path the path. Used to get the active working sets
     * @param factType The short class name of the Fact Type
     * @param fieldName The field name
     * @return the associated CustomFormConfiguration for the given FactType and
     *         FieldName in the active working sets or null if any.
     */
    public CustomFormConfiguration getCustomFormConfiguration( Path path,
                                                               String factType,
                                                               String fieldName ) {
        Set<WorkingSetConfigData> packageWorkingSets = this.getActiveWorkingSets( path );
        if ( packageWorkingSets != null ) {
            List<CustomFormConfiguration> configs = new ArrayList<CustomFormConfiguration>();
            for ( WorkingSetConfigData workingSetConfigData : packageWorkingSets ) {
                if ( workingSetConfigData.customForms != null && !workingSetConfigData.customForms.isEmpty() ) {
                    configs.addAll( workingSetConfigData.customForms );
                }
            }
            CustomFormsContainer cfc = new CustomFormsContainer( configs );

            if ( cfc.containsCustomFormFor( factType, fieldName ) ) {
                return cfc.getCustomForm( factType, fieldName );
            }
        }

        return null;

    }

    /**
     * Returns the active WorkingSets for a package, or null if any.
     * @param path the path
     * @return the active WorkingSets for a package, or null if any.
     */
    public Set<WorkingSetConfigData> getActiveWorkingSets( Path path ) {
        return this.activeWorkingSets.get( path );
    }

    /**
     * TODO: We need to store/retrieve this value from repository
     * @return
     */
    public boolean isAutoVerifierEnabled() {
        return autoVerifierEnabled;
    }

    /**
     * TODO: We need to store/retrieve this value from repository
     */
    public void setAutoVerifierEnabled( boolean autoVerifierEnabled ) {
        this.autoVerifierEnabled = autoVerifierEnabled;
    }

}
