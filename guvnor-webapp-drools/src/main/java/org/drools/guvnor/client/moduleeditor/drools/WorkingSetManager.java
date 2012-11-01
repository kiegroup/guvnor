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
package org.drools.guvnor.client.moduleeditor.drools;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.rpc.Asset;
import org.drools.guvnor.client.rpc.AssetService;
import org.drools.guvnor.client.rpc.AssetServiceAsync;
import org.drools.guvnor.client.rpc.WorkingSetConfigData;
import org.drools.ide.common.client.factconstraints.ConstraintConfiguration;
import org.drools.ide.common.client.factconstraints.customform.CustomFormConfiguration;
import org.drools.ide.common.client.factconstraints.helper.CustomFormsContainer;

import com.google.gwt.user.client.Command;
import java.util.ArrayList;
import org.drools.guvnor.client.asseteditor.drools.modeldriven.SetFactTypeFilter;
import org.drools.guvnor.client.rpc.Path;
import org.drools.guvnor.client.rpc.PathImpl;

public class WorkingSetManager {

    private static WorkingSetManager INSTANCE = new WorkingSetManager();
    private Map<String, Set<Asset>> activeWorkingSets = new HashMap<String, Set<Asset>>();
    /**
     * This attribute should be sever side. Maybe in some FactConstraintConfig
     * object.
     */
    private boolean autoVerifierEnabled = false;

    public synchronized static WorkingSetManager getInstance() {
        return INSTANCE;
    }

    /**
     * Convenient method to call {@link #applyWorkingSets(java.lang.String, java.util.Set, com.google.gwt.user.client.Command) }
     * when you have the WorkingSets' UUID instead of the WorkingSetConfigData
     * objects
     * @param packageName the package name.
     * @param wsUUIDs the set of WorkingSets' UUIDs
     * @param done the command to execute after the SCE and internal map are
     * refreshed.
     * @see #applyWorkingSets(java.lang.String, java.util.Set, com.google.gwt.user.client.Command)
     */
    public void applyWorkingSets(final String packageName, final String[] wsUUIDs, final Command done) {
        AssetServiceAsync assetService = GWT.create(AssetService.class);
        Path[] paths = new PathImpl[wsUUIDs.length];
        for(int i=0;i<wsUUIDs.length;i++) {
        	Path path = new PathImpl();
        	path.setUUID(wsUUIDs[i]);
        	paths[i] = path;
        }
        assetService.loadRuleAssets(paths, new GenericCallback<Asset[]>() {

            public void onSuccess(Asset[] result) {
                final Set<Asset> wss = new HashSet<Asset>();
                wss.addAll(Arrays.asList(result));

                applyWorkingSets(packageName, wss, done);
            }
        });

    }

    /**
     * Applies the workingSets' valid facts to SCE. This method updates the
     * internal activeWorkingSets map. If no workingSet is supplied, the
     * SCE is refreshed to remove any existing filter.
     * @param packageName the package name.
     * @param wss the WorkingSet' assets list
     * @param done the command to execute after the SCE and internal map are
     * refreshed.
     */
    public void applyWorkingSets(final String packageName, final Set<Asset> wss, final Command done) {

        if (wss == null || wss.isEmpty()) {
            //if no WS, we refresh the SCE (release any filter)
            SuggestionCompletionCache.getInstance().refreshPackage(packageName, done);
            //remove the WS from activeWorkingSets map
            this.activeWorkingSets.remove(packageName);
        } else {
            
            //get all the validFact from all the WSs
            final Set<String> validFacts = new HashSet<String>();
            for (Asset asset : wss) {
                WorkingSetConfigData wsConfig = (WorkingSetConfigData) asset.getContent();
                if (wsConfig.validFacts != null && wsConfig.validFacts.length > 0) {
                    validFacts.addAll(Arrays.asList(wsConfig.validFacts));
                }
            }
            
            //map the relation between package and WSs
            activeWorkingSets.put(packageName, wss);
            
            //apply a filter to SCE
            SuggestionCompletionCache.getInstance().applyFactFilter(packageName,
                    new SetFactTypeFilter(validFacts), done);
        }

    }

    /**
     * Returns the active WorkingSets for a package (as RuleAsset), or null if any.
     * @param packageName the package name
     * @return the active WorkingSets for a package (as RuleAsset), or null if any.
     */
    public Set<Asset> getActiveAssets(String packageName) {
        return this.activeWorkingSets.get(packageName);
    }

    public Set<String> getActiveAssetUUIDs(String packageName) {
        Set<Asset> assets = this.activeWorkingSets.get(packageName);
        if (assets == null) {
            return null;
        }
        Set<String> uuids = new HashSet<String>(assets.size());
        for (Asset asset : assets) {
            uuids.add(asset.getUuid());
        }
        return uuids;
    }

    /**
     * Returns the active WorkingSets for a package, or null if any.
     * @param packageName the package name
     * @return the active WorkingSets for a package, or null if any.
     */
    public Set<WorkingSetConfigData> getActiveWorkingSets(String packageName) {
        Set<Asset> assets = this.activeWorkingSets.get(packageName);
        if (assets == null) {
            return null;
        }

        Set<WorkingSetConfigData> result = new HashSet<WorkingSetConfigData>();
        for (Asset ruleAsset : assets) {
            result.add((WorkingSetConfigData) ruleAsset.getContent());
        }

        return result;
    }

    /**
     * Returns whether the given (WorkingSet) RuleSet is active in a package or not.
     * @param packageName the package name.
     * @param workingSetAsset the (WorkingSet) RuleSet
     * @return whether the given (WorkingSet) RuleSet is active in a package or not.
     */
    public boolean isWorkingSetActive(String packageName, Asset workingSetAsset) {
        return this.isWorkingSetActive(packageName, workingSetAsset.getUuid());
    }

    /**
     * Returns whether the given (WorkingSet) RuleSet is active in a package or not.
     * @param packageName the package name.
     * @param workingSetAsset the (WorkingSet) RuleSet
     * @return whether the given (WorkingSet) RuleSet is active in a package or not.
     */
    public boolean isWorkingSetActive(String packageName, String ruleAssetUUID) {
        if (!this.activeWorkingSets.containsKey(packageName)) {
            return false;
        }

        Set<Asset> wss = this.activeWorkingSets.get(packageName);
        for (Asset asset : wss) {
            if (asset.getUuid().equals(ruleAssetUUID)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns a Set of Constraints for a Fact Type's field. This method uses
     * the active Working Sets of the package in order to get the Constraints.
     * @param packageName the package name.
     * @param factType the Fact Type (Short class name)
     * @param fieldName the field name
     * @return a Set of Constraints for a Fact Type's field.
     */
    public Set<ConstraintConfiguration> getFieldContraints(String packageName, String factType, String fieldName) {

        Set<ConstraintConfiguration> result = new HashSet<ConstraintConfiguration>();

        //TODO: Change this with a centralized way of Constraint Administration.
        Set<Asset> activeAssets = this.getActiveAssets(packageName);
        if (activeAssets != null) {
            for (Asset ruleAsset : activeAssets) {
                List<ConstraintConfiguration> constraints = ((WorkingSetConfigData) ruleAsset.getContent()).constraints;
                if (constraints != null) {
                    for (ConstraintConfiguration constraint : constraints) {
                        if (constraint.getFactType().equals(factType) && constraint.getFieldName().equals(fieldName)) {
                            result.add(constraint);
                        }
                    }
                }
            }
        }

        return result;
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
    public void setAutoVerifierEnabled(boolean autoVerifierEnabled) {
        this.autoVerifierEnabled = autoVerifierEnabled;
    }

    /**
     * Returns the associated CustomFormConfiguration for a given FactType and FieldName.
     * Because CustomFormConfiguration is stored inside a WorkingSet, the
     * packageName attribute is used to retrieve all the active WorkingSets.
     * If more than one active WorkingSet contain a CustomFormConfiguration for
     * the given FactType and FieldName the first to be found (in any specific
     * nor deterministic order) will be returned.
     * @param packageName the name of the package. Used to get the active
     * working sets
     * @param factType The short class name of the Fact Type
     * @param fieldName The field name
     * @return the associated CustomFormConfiguration for the given FactType and
     * FieldName in the active working sets or null if any.
     */
    public CustomFormConfiguration getCustomFormConfiguration(String packageName, String factType, String fieldName) {
        Set<WorkingSetConfigData> packageWorkingSets = this.getActiveWorkingSets(packageName);
        if (packageWorkingSets != null) {
            List<CustomFormConfiguration> configs = new ArrayList<CustomFormConfiguration>();
            for (WorkingSetConfigData workingSetConfigData : packageWorkingSets) {
                if (workingSetConfigData.customForms != null && !workingSetConfigData.customForms.isEmpty()) {
                    configs.addAll(workingSetConfigData.customForms);
                }
            }
            CustomFormsContainer cfc = new CustomFormsContainer(configs);

            if (cfc.containsCustomFormFor(factType, fieldName)) {
                return cfc.getCustomForm(factType, fieldName);
            }
        }

        return null;

    }
}
