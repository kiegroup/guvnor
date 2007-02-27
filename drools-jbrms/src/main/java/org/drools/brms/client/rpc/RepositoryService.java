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
     * This will not check in the rule, but just leave it as saved in the repo.
     */
    public String createNewRule(String ruleName, String description, String initialCategory, String initialPackage, String format) throws SerializableException;
    
    /**
     * This returns a list of packages where rules may be added.
     * Only the UUID and the name need to be populated.
     */
    public PackageConfigData[] listPackages();
    
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
     * This creates a package of the given name, and checks it in.
     * @return UUID of the created item.
     */
    public String createPackage(String name, String description) throws SerializableException;
    
    /**
     * Loads a package by its uuid.
     * @return Well, its pretty obvious if you think about it for a minute. Really.
     */
    public PackageConfigData loadPackageConfig(String uuid);
    
    /**
     * Saves the package config data in place (does not create a new version of anything).
     * @return The UUID of the saved item.
     */
    public String savePackage(PackageConfigData data) throws SerializableException;
        
    
    /**
     * Given a format, this will return assets that match.
     * It can also be used for "pagination" by passing in start and 
     * finish row numbers.
     * @param packageUUID The package uuid to search inside.
     * @param format The format to filter on.
     * @param numRows The number of rows to return. -1 means all.
     * @param startRow The starting row number if paging - if numRows is -1 then this is ignored.
     */
    public TableDataResult listAssets(String packageUUID, String formats[], int numRows, int startRow) throws SerializableException;
    
    /**
     * Returns a list of valid states.
     */
    public String[] listStates() throws SerializableException;

    /**
     * Create a state (status).
     * @return the UUID of the created StateItem.
     */
    public String createState(String name) throws SerializableException;
    
    
    /**
     * This will change the state of an asset or package.
     * @param uuid The UUID of the asset we are tweaking.
     * @param newState The new state to set. It must be valid in the repo.
     * @param wholePackage true if it is a package we are setting the state of. 
     * If this is true, UUID must be the status of a package, if false, it must be an asset.
     */
    public void changeState(String uuid, String newState, boolean wholePackage);
    
    /**
     * This moves an asset to the given target package.
     */
    public void changeAssetPackage(String uuid, String newPackage, String comment);
    
    /**
     * Copies an asset into a new destination package.
     * @param assetUUID The source assetID. 
     * @param newPackage The destination package (may be the same as the current source package, but
     * in that case the asset has to have a different name). 
     * @param newName The new name of the asset.
     */
    public String copyAsset(String assetUUID, String newPackage, String newName);
    
    
    /**
     * This will load a list of snapshots for the given package. Snapshots are created 
     * by taking a labelled copy of a package, at a point in time, for instance for deployment.
     */
    public SnapshotInfo[] listSnapshots(String packageName);
    

    /**
     * Create a package snapshot for deployment.
     * @param packageName THe name of the package to copy.
     * @param snapshotName The name of the snapshot. Has to be unique unless existing one is to be replaced.
     * @param replaceExisting Replace the existing one (must be true to replace an existing snapshot of the same name).
     * @param comment A comment to be added to the copied one.
     */
    public void createPackageSnapshot(String packageName, String snapshotName, boolean replaceExisting, String comment);
    
    /**
     * This alters an existing snapshot, it can be used to copy or delete it. 
     * @param packageName The package name that we are dealing with. 
     * @param snapshotName The snapshot name (this must exist)
     * @param delete true if the snapshotName is to be removed. 
     * @param newSnapshotName The name of the target snapshot that the contents will be copied to.
     */
    public void copyOrRemoveSnapshot(String packageName, String snapshotName, boolean delete, String newSnapshotName) throws SerializableException;
    
}
