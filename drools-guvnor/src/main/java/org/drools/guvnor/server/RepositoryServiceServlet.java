package org.drools.guvnor.server;

import java.io.IOException;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.drools.guvnor.client.rpc.AnalysisReport;
import org.drools.guvnor.client.rpc.RepositoryService;
import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.guvnor.server.repository.MailboxService;
import org.drools.guvnor.server.repository.RepositoryStartupService;
import org.drools.guvnor.server.util.LoggingHelper;
import org.drools.guvnor.server.util.TestEnvironmentSessionHelper;
import org.drools.repository.RulesRepository;
import org.drools.repository.RulesRepositoryException;
import org.jboss.seam.Component;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.security.AuthorizationException;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * GWT RPC service endpoint for Repository service. A place to hang some exception handling mainly.
 * This passes on all requests unmolested to the underlying ServiceImplemention class. 
 *
 * @author Michael Neale
 */
public class RepositoryServiceServlet extends RemoteServiceServlet implements RepositoryService {

    private static final LoggingHelper     log              = LoggingHelper.getLogger(RepositoryServiceServlet.class);
    private static boolean testListenerInit = false;
	/**
	 * This is used by the pass through methods below.
	 * Michael got tired of trying to read other peoples overly abstracted code, so its just generated dumb code to
	 * reduce dependencies on libraries.
	 */
	public static ServiceImplementation getService() {
		if (Contexts.isApplicationContextActive()) {
			return (ServiceImplementation) Component.getInstance("org.drools.guvnor.client.rpc.RepositoryService");
		} else {
			//this is only for out of container hosted mode in GWT
			ServiceImplementation impl = new ServiceImplementation();
			impl.repository = new RulesRepository(TestEnvironmentSessionHelper.getSession(false));

            synchronized(RepositoryServiceServlet.class) {
                if (!testListenerInit) {
                    MailboxService.getInstance().init(new RulesRepository(TestEnvironmentSessionHelper.getSession(false)));
                    RepositoryStartupService.registerCheckinListener();
                    testListenerInit = true;
                }
            }

			return impl;
		}
	}

	@Override
	protected void doUnexpectedFailure(Throwable e) {
		if (e.getCause() instanceof AuthorizationException) {
			HttpServletResponse response = getThreadLocalResponse();
		    try {
		      log.error(e.getMessage(),e.getCause());
		      e.printStackTrace();
		      response.setContentType("text/plain");
		      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		      response.getWriter().write("Sorry, insufficient permissions to perform this action.");
		    } catch (IOException ex) {
		      getServletContext().log(
		          "respondWithUnexpectedFailure failed while sending the previous failure to the client",
		          ex);
		    }
		} else if (e.getCause() instanceof RulesRepositoryException) {
			log.error(e.getMessage(), e.getCause());
            sendErrorMessage(e.getCause().getMessage());
		} else {
            if (e.getCause() != null) {
			    log.error(e.getMessage(),e.getCause());
                e.printStackTrace();
            } else {
                log.error(e.getMessage(),e);
                e.printStackTrace();
            }
			sendErrorMessage("Sorry, a technical error occurred. Please contact a system administrator.");
		}
	}

    private void sendErrorMessage(String msg) {
        HttpServletResponse response = getThreadLocalResponse();
        response.setContentType("text/plain");
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        try {
            response.getWriter().write(msg);
        } catch (IOException ex) {
           getServletContext().log(
                   "respondWithUnexpectedFailure failed while sending the previous failure to the client",
                   ex);
        }
    }


    /** PLACE THE FOLLOWING IN RepositoryServiceServlet.java **/

    public java.lang.String[] loadChildCategories(java.lang.String p0)  {
         return getService().loadChildCategories( p0);
    }
    public org.drools.guvnor.client.rpc.TableDataResult loadRuleListForCategories(java.lang.String p0, int p1, int p2, java.lang.String p3) throws SerializationException {
         return getService().loadRuleListForCategories( p0,  p1,  p2,  p3);
    }
    public org.drools.guvnor.client.rpc.TableDataResult loadRuleListForState(java.lang.String p0, int p1, int p2, java.lang.String p3) throws SerializationException {
         return getService().loadRuleListForState( p0,  p1,  p2,  p3);
    }
    public org.drools.guvnor.client.rpc.TableConfig loadTableConfig(java.lang.String p0)  {
         return getService().loadTableConfig( p0);
    }
    public java.lang.Boolean createCategory(java.lang.String p0, java.lang.String p1, java.lang.String p2)  {
         return getService().createCategory( p0,  p1,  p2);
    }
    public java.lang.String createNewRule(java.lang.String p0, java.lang.String p1, java.lang.String p2, java.lang.String p3, java.lang.String p4) throws SerializationException {
         return getService().createNewRule( p0,  p1,  p2,  p3,  p4);
    }
    public java.lang.String createNewImportedRule(java.lang.String p0, java.lang.String p1) throws SerializationException {
         return getService().createNewImportedRule( p0,  p1);
    }
    public void deleteUncheckedRule(java.lang.String p0, java.lang.String p1)  {
        getService().deleteUncheckedRule( p0,  p1);
    }
    public void clearRulesRepository()  {
        getService().clearRulesRepository();
    }
    public org.drools.guvnor.client.rpc.PackageConfigData[] listPackages()  {
         return getService().listPackages();
    }
    public org.drools.guvnor.client.rpc.PackageConfigData loadGlobalPackage()  {
         return getService().loadGlobalPackage();
    }
    public org.drools.guvnor.client.rpc.PackageConfigData[] listArchivedPackages()  {
         return getService().listArchivedPackages();
    }
    public org.drools.guvnor.client.rpc.RuleAsset loadRuleAsset(java.lang.String p0) throws SerializationException {
         return getService().loadRuleAsset( p0);
    }
    public org.drools.guvnor.client.rpc.RuleAsset[] loadRuleAssets(java.lang.String[] p0) throws SerializationException {
         return getService().loadRuleAssets( p0);
    }
    public org.drools.guvnor.client.rpc.TableDataResult loadAssetHistory(java.lang.String p0) throws SerializationException {
         return getService().loadAssetHistory( p0);
    }
    public org.drools.guvnor.client.rpc.TableDataResult loadArchivedAssets(int p0, int p1) throws SerializationException {
         return getService().loadArchivedAssets( p0,  p1);
    }
    public java.lang.String checkinVersion(org.drools.guvnor.client.rpc.RuleAsset p0) throws SerializationException {
         return getService().checkinVersion( p0);
    }
    public void restoreVersion(java.lang.String p0, java.lang.String p1, java.lang.String p2)  {
        getService().restoreVersion( p0,  p1,  p2);
    }
    public java.lang.String createPackage(java.lang.String p0, java.lang.String p1) throws SerializationException {
         return getService().createPackage( p0,  p1);
    }
    
	public java.lang.String createSubPackage(java.lang.String p0, java.lang.String p1, String parentPackage)
			throws SerializationException {
		return getService().createSubPackage(p0, p1, parentPackage);
	}

    public org.drools.guvnor.client.rpc.PackageConfigData loadPackageConfig(java.lang.String p0)  {
         return getService().loadPackageConfig( p0);
    }
    public org.drools.guvnor.client.rpc.ValidatedResponse savePackage(org.drools.guvnor.client.rpc.PackageConfigData p0) throws SerializationException {
         return getService().savePackage( p0);
    }
    public org.drools.guvnor.client.rpc.TableDataResult listAssets(java.lang.String p0, java.lang.String[] p1, int p2, int p3, java.lang.String p4) throws SerializationException {
         return getService().listAssets( p0,  p1,  p2,  p3,  p4);
    }
    public java.lang.String[] listStates() throws SerializationException {
         return getService().listStates();
    }
    public java.lang.String createState(java.lang.String p0) throws SerializationException {
         return getService().createState( p0);
    }
    public void renameState(java.lang.String p0, java.lang.String p1) throws SerializationException {
        getService().renameState( p0,  p1);
    }
    public void removeState(java.lang.String p0) throws SerializationException {
        getService().removeState( p0);
    }
    public void changeState(java.lang.String p0, java.lang.String p1, boolean p2)  {
        getService().changeState( p0,  p1,  p2);
    }
    public void changeAssetPackage(java.lang.String p0, java.lang.String p1, java.lang.String p2)  {
        getService().changeAssetPackage( p0,  p1,  p2);
    }
    public void promoteAssetToGlobalArea(java.lang.String p0) {
        getService().promoteAssetToGlobalArea( p0);
    }
    public java.lang.String copyAsset(java.lang.String p0, java.lang.String p1, java.lang.String p2)  {
         return getService().copyAsset( p0,  p1,  p2);
    }
    public void copyPackage(java.lang.String p0, java.lang.String p1) throws SerializationException {
        getService().copyPackage( p0,  p1);
    }
    public org.drools.guvnor.client.rpc.SnapshotInfo[] listSnapshots(java.lang.String p0)  {
         return getService().listSnapshots( p0);
    }
    public void createPackageSnapshot(java.lang.String p0, java.lang.String p1, boolean p2, java.lang.String p3)  {
        getService().createPackageSnapshot( p0,  p1,  p2,  p3);
    }
    public void copyOrRemoveSnapshot(java.lang.String p0, java.lang.String p1, boolean p2, java.lang.String p3) throws SerializationException {
        getService().copyOrRemoveSnapshot( p0,  p1,  p2,  p3);
    }
    public org.drools.guvnor.client.rpc.TableDataResult quickFindAsset(java.lang.String p0, boolean p1, int p2, int p3) throws SerializationException {
         return getService().quickFindAsset( p0,  p1,  p2,  p3);
    }
    public void removeCategory(java.lang.String p0) throws SerializationException {
        getService().removeCategory( p0);
    }
    public org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine loadSuggestionCompletionEngine(java.lang.String p0) throws SerializationException {
         return getService().loadSuggestionCompletionEngine( p0);
    }
    public org.drools.guvnor.client.rpc.BuilderResult buildPackage(java.lang.String p0, boolean p1, java.lang.String p2, java.lang.String p3, java.lang.String p4, boolean p5, java.lang.String p6, java.lang.String p7, boolean p8, java.lang.String p9) throws SerializationException {
         return getService().buildPackage( p0,  p1,  p2,  p3,  p4,  p5,  p6,  p7,  p8,  p9);
    }
    public java.lang.String[] getCustomSelectors() throws SerializationException {
         return getService().getCustomSelectors();
    }
    public java.lang.String buildPackageSource(java.lang.String p0) throws SerializationException {
         return getService().buildPackageSource( p0);
    }
    public java.lang.String buildAssetSource(org.drools.guvnor.client.rpc.RuleAsset p0) throws SerializationException {
         return getService().buildAssetSource( p0);
    }
    public org.drools.guvnor.client.rpc.BuilderResult buildAsset(org.drools.guvnor.client.rpc.RuleAsset p0) throws SerializationException {
         return getService().buildAsset( p0);
    }
    public java.lang.String renameAsset(java.lang.String p0, java.lang.String p1)  {
         return getService().renameAsset( p0,  p1);
    }
    public void renameCategory(java.lang.String p0, java.lang.String p1)  {
        getService().renameCategory( p0,  p1);
    }
    public void archiveAsset(java.lang.String p0)  {
        getService().archiveAsset( p0);
    }
    public void unArchiveAsset(java.lang.String p0)  {
        getService().unArchiveAsset( p0);
    }
    public void archiveAssets(java.lang.String[] p0, boolean p1)  {
        getService().archiveAssets( p0,  p1);
    }
    public void removeAsset(java.lang.String p0)  {
        getService().removeAsset( p0);
    }
    public void removeAssets(java.lang.String[] p0)  {
        getService().removeAssets( p0);
    }
    public void removePackage(java.lang.String p0)  {
        getService().removePackage( p0);
    }
    public java.lang.String renamePackage(java.lang.String p0, java.lang.String p1)  {
         return getService().renamePackage( p0,  p1);
    }
    public void rebuildSnapshots() throws SerializationException {
        getService().rebuildSnapshots();
    }
    public void rebuildPackages() throws SerializationException {
        getService().rebuildPackages();
    }
    public java.lang.String[] listRulesInPackage(java.lang.String p0) throws SerializationException {
         return getService().listRulesInPackage( p0);
    }
    public org.drools.guvnor.client.rpc.SingleScenarioResult runScenario(java.lang.String p0, org.drools.ide.common.client.modeldriven.testing.Scenario p1) throws SerializationException {
         return getService().runScenario( p0,  p1);
    }
    public org.drools.guvnor.client.rpc.BulkTestRunResult runScenariosInPackage(java.lang.String p0) throws SerializationException {
         return getService().runScenariosInPackage( p0);
    }
    public java.lang.String[] listTypesInPackage(java.lang.String p0) throws SerializationException {
         return getService().listTypesInPackage( p0);
    }
    public org.drools.guvnor.client.rpc.LogEntry[] showLog()  {
         return getService().showLog();
    }
    public void cleanLog()  {
        getService().cleanLog();
    }
    public java.lang.String[] loadDropDownExpression(java.lang.String[] p0, java.lang.String p1)  {
         return getService().loadDropDownExpression( p0,  p1);
    }
    public org.drools.guvnor.client.rpc.TableDataResult queryFullText(java.lang.String p0, boolean p1, int p2, int p3) throws SerializationException {
         return getService().queryFullText( p0,  p1,  p2,  p3);
    }
    public org.drools.guvnor.client.rpc.TableDataResult queryMetaData(org.drools.guvnor.client.rpc.MetaDataQuery[] p0, java.util.Date p1, java.util.Date p2, java.util.Date p3, java.util.Date p4, boolean p5, int p6, int p7) throws SerializationException {
         return getService().queryMetaData( p0,  p1,  p2,  p3,  p4,  p5,  p6,  p7);
    }
    public java.util.Map listUserPermissions() throws org.drools.guvnor.client.rpc.DetailedSerializationException {
         return getService().listUserPermissions();
    }
    public java.util.Map retrieveUserPermissions(java.lang.String p0)  {
         return getService().retrieveUserPermissions( p0);
    }
    public void updateUserPermissions(java.lang.String p0, java.util.Map p1)  {
        getService().updateUserPermissions( p0,  p1);
    }
    public java.lang.String[] listAvailablePermissionTypes()  {
         return getService().listAvailablePermissionTypes();
    }
    public void deleteUser(java.lang.String p0)  {
        getService().deleteUser( p0);
    }
    public void createUser(java.lang.String p0)  {
        getService().createUser( p0);
    }
    public java.lang.String getAssetLockerUserName(java.lang.String p0)  {
         return getService().getAssetLockerUserName( p0);
    }
    public void lockAsset(java.lang.String p0)  {
        getService().lockAsset( p0);
    }
    public void unLockAsset(java.lang.String p0)  {
        getService().unLockAsset( p0);
    }
    public void installSampleRepository() throws SerializationException {
        getService().installSampleRepository();
    }
    public java.util.List loadDiscussionForAsset(java.lang.String p0)  {
         return getService().loadDiscussionForAsset( p0);
    }
    public java.util.List addToDiscussionForAsset(java.lang.String p0, java.lang.String p1)  {
         return getService().addToDiscussionForAsset( p0,  p1);
    }
    public void clearAllDiscussionsForAsset(java.lang.String p0)  {
        getService().clearAllDiscussionsForAsset( p0);
    }
    public java.util.List subscribe()  {
         return getService().subscribe();
    }
    public org.drools.guvnor.client.rpc.TableDataResult loadInbox(java.lang.String p0) throws org.drools.guvnor.client.rpc.DetailedSerializationException {
         return getService().loadInbox( p0);
    }
    public org.drools.guvnor.client.rpc.SnapshotDiffs compareSnapshots(java.lang.String p0, java.lang.String p1, java.lang.String p2)  {
         return getService().compareSnapshots( p0,  p1,  p2);
    }
    


}
