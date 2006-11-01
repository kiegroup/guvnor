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
     */
    public Boolean createNewRule(String ruleName, String description, String initialCategory, String initialPackage) throws SerializableException;
    
    /**
     * This returns a list of packages where rules may be added.
     */
    public String[] listRulePackages();
    
    /**
     * This loads up all the stuff for a 
     * rule asset based on the UUID (always latest and editable version).
     */
    public RuleAsset loadRuleAsset(String UUID) throws SerializableException;     
    
    
}
