package org.drools.guvnor.client.packages;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.drools.factconstraints.client.ConstraintConfiguration;
import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.modeldriven.SetFactTypeFilter;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;
import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.guvnor.client.rpc.WorkingSetConfigData;

import com.google.gwt.user.client.Command;

/**
 *
 * @author esteban.aliverti@gmail.com
 */
public class WorkingSetManager {

    private static WorkingSetManager INSTANCE = new WorkingSetManager();
    private Map<String, Set<RuleAsset>> activeWorkingSets = new HashMap<String, Set<RuleAsset>>();

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
        RepositoryServiceFactory.getService().loadRuleAssets(wsUUIDs, new GenericCallback<RuleAsset[]>() {

            public void onSuccess(RuleAsset[] result) {
                final Set<RuleAsset> wss = new HashSet<RuleAsset>();
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
    public void applyWorkingSets(final String packageName, final Set<RuleAsset> wss, final Command done) {

        Command cmd = new Command() {

            public void execute() {
                //update the map
                activeWorkingSets.remove(packageName);
                if (wss != null && !wss.isEmpty()){
                    activeWorkingSets.put(packageName, wss);
                }
                
                if (done != null) {
                    done.execute();
                }
            }
        };

        if (wss == null || wss.isEmpty()) {
            //if no WS, we refresh the SCE (release any filter)
            SuggestionCompletionCache.getInstance().refreshPackage(packageName, cmd);
            //update the map
            this.activeWorkingSets.remove(packageName);
            return;
        } else {

            final Set<String> validFacts = new HashSet<String>();
            for (RuleAsset asset : wss) {
                WorkingSetConfigData wsConfig = (WorkingSetConfigData) asset.content;
                validFacts.addAll(Arrays.asList(wsConfig.validFacts));
            }

            SuggestionCompletionCache.getInstance().applyFactFilter(packageName, 
            		new SetFactTypeFilter(validFacts), cmd);
        }

    }

    /**
     * Returns the active WorkingSets for a package (as RuleAsset), or null if any.
     * @param packageName the package name
     * @return the active WorkingSets for a package (as RuleAsset), or null if any.
     */
    public Set<RuleAsset> getActiveAssets(String packageName){
        return this.activeWorkingSets.get(packageName);
    }
    
    public Set<String> getActiveAssetUUIDs(String packageName){
    	Set<RuleAsset> assets = this.activeWorkingSets.get(packageName);
    	if (assets == null) {
    		return null;
    	}
    	Set<String> uuids = new HashSet<String>(assets.size());
        for (RuleAsset asset : assets) {
			uuids.add(asset.uuid);
		}
		return uuids;
    }

    /**
     * Returns the active WorkingSets for a package, or null if any.
     * @param packageName the package name
     * @return the active WorkingSets for a package, or null if any.
     */
    public Set<WorkingSetConfigData> getActiveWorkingSets(String packageName){
        Set<RuleAsset> assets = this.activeWorkingSets.get(packageName);
        if (assets == null){
            return null;
        }

        Set<WorkingSetConfigData> result = new HashSet<WorkingSetConfigData>();
        for (RuleAsset ruleAsset : assets) {
            result.add((WorkingSetConfigData) ruleAsset.content);
        }

        return result;
    }

    /**
     * Returns whether the given (WorkingSet) RuleSet is active in a package or not.
     * @param packageName the package name.
     * @param workingSetAsset the (WorkingSet) RuleSet
     * @return whether the given (WorkingSet) RuleSet is active in a package or not.
     */
    public boolean isWorkingSetActive(String packageName, RuleAsset workingSetAsset){
        return this.isWorkingSetActive(packageName, workingSetAsset.uuid);
    }

    /**
     * Returns whether the given (WorkingSet) RuleSet is active in a package or not.
     * @param packageName the package name.
     * @param workingSetAsset the (WorkingSet) RuleSet
     * @return whether the given (WorkingSet) RuleSet is active in a package or not.
     */
    public boolean isWorkingSetActive(String packageName, String ruleAssetUUID){
        if (!this.activeWorkingSets.containsKey(packageName)){
            return false;
        }

        Set<RuleAsset> wss = this.activeWorkingSets.get(packageName);
        for (RuleAsset asset : wss) {
            if (asset.uuid.equals(ruleAssetUUID)){
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
    public Set<ConstraintConfiguration> getFieldContraints(String packageName, String factType, String fieldName ){

        Set<ConstraintConfiguration> result = new HashSet<ConstraintConfiguration>();

        //TODO: Change this with a centralized way of Constraint Administration.
        Set<RuleAsset> activeAssets = this.getActiveAssets(packageName);
        if (activeAssets != null){
            for (RuleAsset ruleAsset : activeAssets) {
                List<ConstraintConfiguration> constraints = ((WorkingSetConfigData)ruleAsset.content).constraints;
                if (constraints != null) {
                	for (ConstraintConfiguration constraint : constraints) {
                		if (constraint.getFactType().equals(factType) && constraint.getFieldName().equals(fieldName)){
                			result.add(constraint);
                		}
                	}
                }
            }
        }

        return result;
    }
}
