package org.drools.brms.client.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.RemoteService;

/**

 * This is what the remote service will implement, as a servlet.

 * (in hosted/debug mode, you could also use an implementation that was in-process).

 */

public interface RepositoryServiceAsync
    extends
    RemoteService {

    /**

     * @param categoryPath A "/" delimited path to a category. 

     * @param callback

     */

    public void loadChildCategories(String categoryPath,
                                    AsyncCallback callback);

    /**

     * Return a a 2d array/grid of results for rules.

     * @param A "/" delimited path to a category.

     * @param status The status flag. Leave blank to be all.

     */

    public void loadRuleListForCategories(String categoryPath,
                                                     String status, AsyncCallback callback);

    /**

     * This will return a TableConfig of header names.

     * @param listName The name of the list that we are going to render.

     */

    public void loadTableConfig(String listName,
                                AsyncCallback callback);

    /**

     * This will create a new category at the specified path.

     */

    public void createCategory(String path,
                               String name,
                               String description,
                               AsyncCallback callback);

    /**

     * Creates a brand new rule with the initial category.

     */

    public void createNewRule(String ruleName,
                                 String description,
                                 String initialCategory,
                                 String initialPackage,
                                 AsyncCallback callBack);

    /**

     * This returns a list of packages where rules may be added.

     */

    public void listRulePackages(AsyncCallback callback);
}
