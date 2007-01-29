package org.drools.brms.client.rpc;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.SerializableException;

/**
 * This is what the remote service will implement, as a servlet.
 * (in hosted/debug mode, you could also use an implementation that was in-process).
 */
public interface RepositoryService extends RemoteService {

    /**
     * @param categoryPath A "/" delimited path to a category. 
     * @param callback
     */
    public String[] loadChildCategories(String categoryPath);
 
    
    /**
     * Return a a 2d array/grid of results for rules.
     * @param A "/" delimited path to a category.
     */
    public TableDataResult loadRuleListForCategories(String categoryPath) throws SerializableException;
    
    /**
     * This will return a TableConfig of header names.
     * @param listName The name of the list that we are going to render.
     */
    public TableConfig loadTableConfig(String listName);
    
    /**
     * This will create a new category at the specified path.
     */
    public Boolean createCategory(String path, String name, String description);
    
    /**
     * Creates a brand new rule with the initial category.
     * Return the UUID of the item created.
     */
    public String createNewRule(String ruleName, String description, String initialCategory, String initialPackage, String format) throws SerializableException;
    
    /**
     * This returns a list of packages where rules may be added.
     */
    public String[] listRulePackages();
    
    /**
     * This loads up all the stuff for a 
     * rule asset based on the UUID (always latest and editable version).
     */
    public RuleAsset loadRuleAsset(String UUID) throws SerializableException;     
    
    
    /**
     * This will load the history of the given asset, in a summary format suitable
     * for display in a table.
     */
    public TableDataResult loadAssetHistory(String uuid) throws SerializableException;
    
    /**
     * This checks in a new version of an asset. 
     * @return the UUID of the asset you are checking in, 
     * null if there was some problem (and an exception was not thrown).
     */
    public String checkinVersion(RuleAsset asset) throws SerializableException;
    
    
    /**
     * This will restore the specified version in the repository, saving, and creating
     * a new version (with all the restored content).
     */
    public void restoreVersion(String versionUUID, String assetUUID, String comment);
    
    /**
     * This creates a package of the given name.
     * @return UUID of the created item.
     */
    public String createPackage(String name, String description) throws SerializableException;
    
}
