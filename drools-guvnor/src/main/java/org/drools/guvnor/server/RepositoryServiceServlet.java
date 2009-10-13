package org.drools.guvnor.server;

import com.google.gwt.user.client.rpc.SerializableException;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import org.apache.log4j.Logger;
import org.drools.guvnor.client.rpc.*;
import org.drools.guvnor.server.util.LoggingHelper;
import org.drools.guvnor.server.util.TestEnvironmentSessionHelper;
import org.drools.guvnor.server.repository.MailboxService;
import org.drools.guvnor.server.repository.RepositoryStartupService;
import org.drools.repository.RulesRepository;
import org.drools.repository.RulesRepositoryException;
import org.jboss.seam.Component;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.security.AuthorizationException;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * GWT RPC service endpoint for Repository service. A place to hang some exception handling mainly.
 * This passes on all requests unmolested to the underlying ServiceImplemention class. 
 *
 * @author Michael Neale
 */
public class RepositoryServiceServlet extends RemoteServiceServlet implements RepositoryService {

    private static final Logger     log              = LoggingHelper.getLogger(RepositoryServiceServlet.class);
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
		      log.error(e.getCause());
		      response.setContentType("text/plain");
		      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		      response.getWriter().write("Sorry, insufficient permissions to perform this action.");
		    } catch (IOException ex) {
		      getServletContext().log(
		          "respondWithUnexpectedFailure failed while sending the previous failure to the client",
		          ex);
		    }
		} else if (e.getCause() instanceof RulesRepositoryException) {
			log.error(e.getCause());
            sendErrorMessage(e.getCause().getMessage());
		} else {
            if (e.getCause() != null) {
			    log.error(e.getCause());
                e.printStackTrace();
            } else {
                log.error(e);
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


    /**
	 * START GENERATED CODE SECTION. DO NOT MODIFY WHAT IS BELOW
	 */

	/** PLACE THE FOLLOWING IN RepositoryServiceServlet.java **/

	public java.lang.String[] loadChildCategories(java.lang.String p0)  {
		 return getService().loadChildCategories( p0);
	}
	public org.drools.guvnor.client.rpc.TableDataResult loadRuleListForCategories(java.lang.String p0, int p1, int p2, java.lang.String p3) throws com.google.gwt.user.client.rpc.SerializableException {
		 return getService().loadRuleListForCategories( p0,  p1,  p2,  p3);
	}
	public org.drools.guvnor.client.rpc.TableDataResult loadRuleListForState(java.lang.String p0, int p1, int p2, java.lang.String p3) throws com.google.gwt.user.client.rpc.SerializableException {
		 return getService().loadRuleListForState( p0,  p1,  p2,  p3);
	}
	public org.drools.guvnor.client.rpc.TableConfig loadTableConfig(java.lang.String p0)  {
		 return getService().loadTableConfig( p0);
	}
	public java.lang.Boolean createCategory(java.lang.String p0, java.lang.String p1, java.lang.String p2)  {
		 return getService().createCategory( p0,  p1,  p2);
	}
	public java.lang.String createNewRule(java.lang.String p0, java.lang.String p1, java.lang.String p2, java.lang.String p3, java.lang.String p4) throws com.google.gwt.user.client.rpc.SerializableException {
		 return getService().createNewRule( p0,  p1,  p2,  p3,  p4);
	}
	public java.lang.String createNewLinkedRule(java.lang.String p0, java.lang.String p1, java.lang.String p2, java.lang.String p3) throws com.google.gwt.user.client.rpc.SerializableException {
		 return getService().createNewLinkedRule( p0,  p1,  p2,  p3);
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
	public org.drools.guvnor.client.rpc.PackageConfigData[] listArchivedPackages()  {
		 return getService().listArchivedPackages();
	}
	public org.drools.guvnor.client.rpc.RuleAsset loadRuleAsset(java.lang.String p0) throws com.google.gwt.user.client.rpc.SerializableException {
		 return getService().loadRuleAsset( p0);
	}
	public org.drools.guvnor.client.rpc.TableDataResult loadAssetHistory(java.lang.String p0) throws com.google.gwt.user.client.rpc.SerializableException {
		 return getService().loadAssetHistory( p0);
	}
	public org.drools.guvnor.client.rpc.TableDataResult loadArchivedAssets(int p0, int p1) throws com.google.gwt.user.client.rpc.SerializableException {
		 return getService().loadArchivedAssets( p0,  p1);
	}
	public java.lang.String checkinVersion(org.drools.guvnor.client.rpc.RuleAsset p0) throws com.google.gwt.user.client.rpc.SerializableException {
		 return getService().checkinVersion( p0);
	}
	public void restoreVersion(java.lang.String p0, java.lang.String p1, java.lang.String p2)  {
		getService().restoreVersion( p0,  p1,  p2);
	}
	public java.lang.String createPackage(java.lang.String p0, java.lang.String p1) throws com.google.gwt.user.client.rpc.SerializableException {
		 return getService().createPackage( p0,  p1);
	}
	public org.drools.guvnor.client.rpc.PackageConfigData loadPackageConfig(java.lang.String p0)  {
		 return getService().loadPackageConfig( p0);
	}
	public org.drools.guvnor.client.rpc.ValidatedResponse savePackage(org.drools.guvnor.client.rpc.PackageConfigData p0) throws com.google.gwt.user.client.rpc.SerializableException {
		 return getService().savePackage( p0);
	}
	public org.drools.guvnor.client.rpc.TableDataResult listAssets(java.lang.String p0, java.lang.String[] p1, int p2, int p3, java.lang.String p4) throws com.google.gwt.user.client.rpc.SerializableException {
		 return getService().listAssets( p0,  p1,  p2,  p3,  p4);
	}
	public java.lang.String[] listStates() throws com.google.gwt.user.client.rpc.SerializableException {
		 return getService().listStates();
	}
	public java.lang.String createState(java.lang.String p0) throws com.google.gwt.user.client.rpc.SerializableException {
		 return getService().createState( p0);
	}
    public void removeState(String name) throws SerializableException {
        getService().removeState( name );
    }
    public void renameState(String oldName, String newName) throws SerializableException {
        getService().renameState( oldName, newName );
    }
	public void changeState(java.lang.String p0, java.lang.String p1, boolean p2)  {
		getService().changeState( p0,  p1,  p2);
	}
	public void changeAssetPackage(java.lang.String p0, java.lang.String p1, java.lang.String p2)  {
		getService().changeAssetPackage( p0,  p1,  p2);
	}
	public java.lang.String copyAsset(java.lang.String p0, java.lang.String p1, java.lang.String p2)  {
		 return getService().copyAsset( p0,  p1,  p2);
	}
	public void copyPackage(java.lang.String p0, java.lang.String p1) throws com.google.gwt.user.client.rpc.SerializableException {
		getService().copyPackage( p0,  p1);
	}
	public org.drools.guvnor.client.rpc.SnapshotInfo[] listSnapshots(java.lang.String p0)  {
		 return getService().listSnapshots( p0);
	}
	public void createPackageSnapshot(java.lang.String p0, java.lang.String p1, boolean p2, java.lang.String p3)  {
		getService().createPackageSnapshot( p0,  p1,  p2,  p3);
	}
	public void copyOrRemoveSnapshot(java.lang.String p0, java.lang.String p1, boolean p2, java.lang.String p3) throws com.google.gwt.user.client.rpc.SerializableException {
		getService().copyOrRemoveSnapshot( p0,  p1,  p2,  p3);
	}
	public org.drools.guvnor.client.rpc.TableDataResult quickFindAsset(java.lang.String p0, int p1, boolean p2)  {
		 return getService().quickFindAsset( p0,  p1,  p2);
	}
	public void removeCategory(java.lang.String p0) throws com.google.gwt.user.client.rpc.SerializableException {
		getService().removeCategory( p0);
	}
	public org.drools.guvnor.client.modeldriven.SuggestionCompletionEngine loadSuggestionCompletionEngine(java.lang.String p0) throws com.google.gwt.user.client.rpc.SerializableException {
		 return getService().loadSuggestionCompletionEngine( p0);
	}
	public org.drools.guvnor.client.rpc.BuilderResult[] buildPackage(java.lang.String p0, boolean p1, java.lang.String p2, java.lang.String p3 ,java.lang.String p4, java.lang.String p5) throws com.google.gwt.user.client.rpc.SerializableException {
		 return getService().buildPackage( p0,  p1,  p2, p3, p4, p5);
	}
	public java.lang.String[] getCustomSelectors() throws com.google.gwt.user.client.rpc.SerializableException {
		 return getService().getCustomSelectors();
	}
	public java.lang.String buildPackageSource(java.lang.String p0) throws com.google.gwt.user.client.rpc.SerializableException {
		 return getService().buildPackageSource( p0);
	}
	public java.lang.String buildAssetSource(org.drools.guvnor.client.rpc.RuleAsset p0) throws com.google.gwt.user.client.rpc.SerializableException {
		 return getService().buildAssetSource( p0);
	}
	public org.drools.guvnor.client.rpc.BuilderResult[] buildAsset(org.drools.guvnor.client.rpc.RuleAsset p0) throws com.google.gwt.user.client.rpc.SerializableException {
		 return getService().buildAsset( p0);
	}
	public java.lang.String renameAsset(java.lang.String p0, java.lang.String p1)  {
		 return getService().renameAsset( p0,  p1);
	}
	public void renameCategory(java.lang.String p0, java.lang.String p1)  {
		getService().renameCategory( p0,  p1);
	}
	public void archiveAsset(java.lang.String p0, boolean p1)  {
		getService().archiveAsset( p0,  p1);
	}
	public void removeAsset(java.lang.String p0)  {
		getService().removeAsset( p0);
	}
	public void removePackage(java.lang.String p0)  {
		getService().removePackage( p0);
	}
	public java.lang.String renamePackage(java.lang.String p0, java.lang.String p1)  {
		 return getService().renamePackage( p0,  p1);
	}
	public void rebuildSnapshots() throws com.google.gwt.user.client.rpc.SerializableException {
		getService().rebuildSnapshots();
	}
	public void rebuildPackages() throws com.google.gwt.user.client.rpc.SerializableException {
		getService().rebuildPackages();
	}
	public java.lang.String[] listRulesInPackage(java.lang.String p0) throws com.google.gwt.user.client.rpc.SerializableException {
		 return getService().listRulesInPackage( p0);
	}
	public org.drools.guvnor.client.rpc.SingleScenarioResult runScenario(java.lang.String p0, org.drools.guvnor.client.modeldriven.testing.Scenario p1) throws com.google.gwt.user.client.rpc.SerializableException {
		 return getService().runScenario( p0,  p1);
	}
	public org.drools.guvnor.client.rpc.BulkTestRunResult runScenariosInPackage(java.lang.String p0) throws com.google.gwt.user.client.rpc.SerializableException {
		 return getService().runScenariosInPackage( p0);
	}
	public org.drools.guvnor.client.rpc.AnalysisReport analysePackage(java.lang.String p0) throws com.google.gwt.user.client.rpc.SerializableException {
		 return getService().analysePackage( p0);
	}
	public java.lang.String[] listTypesInPackage(java.lang.String p0) throws com.google.gwt.user.client.rpc.SerializableException {
		 return getService().listTypesInPackage( p0);
	}
	public org.drools.guvnor.client.rpc.LogEntry[] showLog()  {
		 return getService().showLog();
	}
	public java.lang.String[] loadDropDownExpression(java.lang.String[] p0, java.lang.String p1)  {
		 return getService().loadDropDownExpression( p0,  p1);
	}
	public org.drools.guvnor.client.rpc.TableDataResult queryFullText(java.lang.String p0, boolean p1, int p2, int p3) throws com.google.gwt.user.client.rpc.SerializableException {
		 return getService().queryFullText( p0,  p1,  p2,  p3);
	}
	public org.drools.guvnor.client.rpc.TableDataResult queryMetaData(org.drools.guvnor.client.rpc.MetaDataQuery[] p0, java.util.Date p1, java.util.Date p2, java.util.Date p3, java.util.Date p4, boolean p5, int p6, int p7) throws com.google.gwt.user.client.rpc.SerializableException {
		 return getService().queryMetaData( p0,  p1,  p2,  p3,  p4,  p5,  p6,  p7);
	}

	public Map<String, List<String>> listUserPermissions()
			throws DetailedSerializableException {
		return getService().listUserPermissions();
	}

	public Map<String, List<String>> retrieveUserPermissions(String userName) {
		return getService().retrieveUserPermissions(userName);
	}

	public void updateUserPermissions(String userName,
			Map<String, List<String>> perms) {
		getService().updateUserPermissions(userName, perms);
	}

	public String[] listAvailablePermissionTypes() {
		return getService().listAvailablePermissionTypes();
	}

	public void deleteUser(String userName) {
		getService().deleteUser(userName);
	}

	/* (non-Javadoc)
     * @see org.drools.guvnor.client.rpc.RepositoryService#getAssetLockerUserName(java.lang.String)
     */
    public String getAssetLockerUserName(String uuid) {
        return getService().getAssetLockerUserName( uuid );
    }

    /* (non-Javadoc)
     * @see org.drools.guvnor.client.rpc.RepositoryService#lockAsset(java.lang.String)
     */
    public void lockAsset(String uuid) {
        getService().lockAsset( uuid );
    }

    /* (non-Javadoc)
     * @see org.drools.guvnor.client.rpc.RepositoryService#unLockAsset(java.lang.String)
     */
    public void unLockAsset(String uuid) {
        getService().unLockAsset( uuid );
    }

    public void installSampleRepository() throws SerializableException {
        getService().installSampleRepository();
    }

    public List<DiscussionRecord> loadDiscussionForAsset(String assetId) {
        return getService().loadDiscussionForAsset(assetId);
    }

    public List<DiscussionRecord> addToDiscussionForAsset(String assetId, String comment) {
        return getService().addToDiscussionForAsset(assetId, comment);
    }

    public void clearAllDiscussionsForAsset(String assetId) {
        getService().clearAllDiscussionsForAsset(assetId);
    }

    public List<PushResponse> subscribe() {
        return getService().subscribe();
    }

    public TableDataResult loadInbox(String inboxName) throws DetailedSerializableException {
        return getService().loadInbox(inboxName);
    }


}
