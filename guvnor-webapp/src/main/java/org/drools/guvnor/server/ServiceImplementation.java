/*
 * Copyright 2011 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.drools.guvnor.server;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jcr.ItemExistsException;
import javax.jcr.RepositoryException;

import org.apache.commons.lang.StringEscapeUtils;
import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.explorer.ExplorerNodeConfig;
import org.drools.guvnor.client.rpc.DetailedSerializationException;
import org.drools.guvnor.client.rpc.InboxPageRequest;
import org.drools.guvnor.client.rpc.InboxPageRow;
import org.drools.guvnor.client.rpc.LogEntry;
import org.drools.guvnor.client.rpc.LogPageRow;
import org.drools.guvnor.client.rpc.MetaData;
import org.drools.guvnor.client.rpc.MetaDataQuery;
import org.drools.guvnor.client.rpc.PackageConfigData;
import org.drools.guvnor.client.rpc.PageRequest;
import org.drools.guvnor.client.rpc.PageResponse;
import org.drools.guvnor.client.rpc.PermissionsPageRow;
import org.drools.guvnor.client.rpc.PushResponse;
import org.drools.guvnor.client.rpc.QueryMetadataPageRequest;
import org.drools.guvnor.client.rpc.QueryPageRequest;
import org.drools.guvnor.client.rpc.QueryPageRow;
import org.drools.guvnor.client.rpc.RepositoryService;
import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.guvnor.client.rpc.StatePageRequest;
import org.drools.guvnor.client.rpc.StatePageRow;
import org.drools.guvnor.client.rpc.TableConfig;
import org.drools.guvnor.client.rpc.TableDataResult;
import org.drools.guvnor.client.widgets.tables.AbstractPagedTable;
import org.drools.guvnor.server.builder.pagerow.InboxPageRowBuilder;
import org.drools.guvnor.server.builder.pagerow.LogPageRowBuilder;
import org.drools.guvnor.server.builder.pagerow.PermissionPageRowBuilder;
import org.drools.guvnor.server.builder.pagerow.QueryFullTextPageRowBuilder;
import org.drools.guvnor.server.builder.pagerow.QueryMetadataPageRowBuilder;
import org.drools.guvnor.server.builder.pagerow.StatePageRowBuilder;
import org.drools.guvnor.server.cache.RuleBaseCache;
import org.drools.guvnor.server.contenthandler.ContentHandler;
import org.drools.guvnor.server.contenthandler.ContentManager;
import org.drools.guvnor.server.repository.UserInbox;
import org.drools.guvnor.server.ruleeditor.springcontext.SpringContextElementsManager;
import org.drools.guvnor.server.security.AdminType;
import org.drools.guvnor.server.security.CategoryPathType;
import org.drools.guvnor.server.security.PackageNameType;
import org.drools.guvnor.server.security.RoleTypes;
import org.drools.guvnor.server.selector.SelectorManager;
import org.drools.guvnor.server.util.HtmlCleaner;
import org.drools.guvnor.server.util.ISO8601;
import org.drools.guvnor.server.util.LoggingHelper;
import org.drools.guvnor.server.util.MetaDataMapper;
import org.drools.guvnor.server.util.ServiceRowSizeHelper;
import org.drools.guvnor.server.util.TableDisplayHandler;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.repository.AssetItem;
import org.drools.repository.AssetItemIterator;
import org.drools.repository.AssetItemPageResult;
import org.drools.repository.CategoryItem;
import org.drools.repository.PackageItem;
import org.drools.repository.RepositoryFilter;
import org.drools.repository.RulesRepository;
import org.drools.repository.RulesRepository.DateQuery;
import org.drools.repository.RulesRepositoryAdministrator;
import org.drools.repository.RulesRepositoryException;
import org.drools.repository.StateItem;
import org.drools.repository.UserInfo.InboxEntry;
import org.drools.repository.security.PermissionManager;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.remoting.WebRemote;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.security.Identity;
import org.mvel2.MVEL;
import org.mvel2.templates.TemplateRuntime;

import com.google.gwt.user.client.rpc.SerializationException;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;

/**
 * This is the implementation of the repository service to drive the GWT based
 * front end. Generally requests for this are passed through from
 * RepositoryServiceServlet - and Seam manages instances of this.
 */
@Name("org.drools.guvnor.client.rpc.RepositoryService")
@AutoCreate
public class ServiceImplementation
    implements
    RepositoryService {

    @In
    private RulesRepository             repository;

    private static final long           serialVersionUID            = 510l;

    private static final LoggingHelper  log                         = LoggingHelper.getLogger( ServiceImplementation.class );

    /**
     * This is used for pushing messages back to the client.
     */
    private final ServiceSecurity             serviceSecurity             = new ServiceSecurity();

    private final RepositoryAssetOperations   repositoryAssetOperations   = new RepositoryAssetOperations();
    private final RepositoryPackageOperations repositoryPackageOperations = new RepositoryPackageOperations();

    /* This is called also by Seam AND Hosted mode */
    @Create
    public void create() {
        repositoryAssetOperations.setRulesRepository( getRulesRepository() );
        repositoryPackageOperations.setRulesRepository( getRulesRepository() );
    }

    /* This is called in hosted mode when creating "by hand" */
    public void setRulesRepository(RulesRepository repository) {
        this.repository = repository;
        create();
    }

    public RulesRepository getRulesRepository() {
        return repository;
    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public String[] listWorkspaces() {
        return getRulesRepository().listWorkspaces();
    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public void createWorkspace(String workspace) {
        getRulesRepository().createWorkspace( workspace );
    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public void removeWorkspace(String workspace) {
        getRulesRepository().removeWorkspace( workspace );
    }

    /**
     * For the time being, module == package
     */
    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public void updateWorkspace(String workspace,
                                String[] selectedModules,
                                String[] unselectedModules) {
        for ( String moduleName : selectedModules ) {
            PackageItem module = getRulesRepository().loadPackage( moduleName );
            module.addWorkspace( workspace );
            module.checkin( "Add workspace" );
        }
        for ( String moduleName : unselectedModules ) {
            PackageItem module = getRulesRepository().loadPackage( moduleName );
            module.removeWorkspace( workspace );
            module.checkin( "Remove workspace" );
        }
    }

    /**
     * This will create a new asset. It will be saved, but not checked in. The
     * initial state will be the draft state. Returns the UUID of the asset.
     */
    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public String createNewRule(String ruleName,
                                String description,
                                String initialCategory,
                                String initialPackage,
                                String format) throws SerializationException {
        serviceSecurity.checkSecurityIsPackageDeveloper( initialPackage );

        log.info( "USER:" + getCurrentUserName() + " CREATING new asset name [" + ruleName + "] in package [" + initialPackage + "]" );

        try {

            PackageItem pkg = getRulesRepository().loadPackage( initialPackage );
            AssetItem asset = pkg.addAsset( ruleName,
                                            description,
                                            initialCategory,
                                            format );
            AssetTemplateCreator assetTemplateCreator = new AssetTemplateCreator();
            assetTemplateCreator.applyPreBuiltTemplates( ruleName,
                                                         format,
                                                         asset );
            getRulesRepository().save();

            push( "categoryChange",
                  initialCategory );
            push( "packageChange",
                  pkg.getName() );

            return asset.getUUID();
        } catch ( RulesRepositoryException e ) {
            if ( e.getCause() instanceof ItemExistsException ) {
                return "DUPLICATE";
            }
            log.error( "An error occurred creating new asset" + ruleName + "] in package [" + initialPackage + "]: ",
                       e );
            throw new SerializationException( e.getMessage() );

        }

    }

    /**
     * This will create a new asset which refers to an existing asset
     */
    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public String createNewImportedRule(String sharedAssetName,
                                        String initialPackage) throws SerializationException {
        serviceSecurity.checkSecurityIsPackageDeveloperForName( initialPackage );

        log.info( "USER:" + getRulesRepository().getSession().getUserID() + " CREATING shared asset imported from global area named [" + sharedAssetName + "] in package [" + initialPackage + "]" );

        try {
            PackageItem pkg = getRulesRepository().loadPackage( initialPackage );
            AssetItem asset = pkg.addAssetImportedFromGlobalArea( sharedAssetName );
            getRulesRepository().save();

            return asset.getUUID();
        } catch ( RulesRepositoryException e ) {
            if ( e.getCause() instanceof ItemExistsException ) {
                return "DUPLICATE";
            }
            log.error( "An error occurred creating shared asset" + sharedAssetName + "] in package [" + initialPackage + "]: ",
                       e );
            throw new SerializationException( e.getMessage() );

        }

    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public void deleteUncheckedRule(String uuid,
                                    String initialPackage) {
        if ( Contexts.isSessionContextActive() ) {
            Identity.instance().checkPermission( new AdminType(),
                                                 RoleTypes.PACKAGE_ADMIN );
        }

        AssetItem asset = getRulesRepository().loadAssetByUUID( uuid );

        String pkgName = asset.getPackageName();

        asset.remove();

        getRulesRepository().save();
        push( "packageChange",
              pkgName );
    }

    /**
     * @deprecated in favour of {@link loadRuleListForState(StatePageRequest)}
     */
    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public TableDataResult loadRuleListForState(String stateName,
                                                int skip,
                                                int numRows,
                                                String tableConfig) throws SerializationException {

        // TODO: May need to use a filter that acts on both package based and
        // category based.
        RepositoryFilter filter = new AssetItemFilter();
        AssetItemPageResult result = getRulesRepository().findAssetsByState( stateName,
                                                                             false,
                                                                             skip,
                                                                             numRows,
                                                                             filter );
        TableDisplayHandler handler = new TableDisplayHandler( tableConfig );
        return handler.loadRuleListTable( result );
    }

    /**
     * @deprecated in favour of {@link AbstractPagedTable}
     */
    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public TableConfig loadTableConfig(String listName) {
        TableDisplayHandler handler = new TableDisplayHandler( listName );
        return handler.loadTableConfig();
    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    /**
     *
     * Role-based Authorization check: This method can be accessed if user has
     * following permissions:
     * 1. The user has a Analyst role and this role has permission to access the category
     * which the asset belongs to.
     * Or.
     * 2. The user has a package.developer role or higher (i.e., package.admin)
     * and this role has permission to access the package which the asset belongs to.
     */
    public String checkinVersion(RuleAsset asset) throws SerializationException {

        // Verify if the user has permission to access the asset through package
        // based permission.
        // If failed, then verify if the user has permission to access the asset
        // through category
        // based permission
        if ( Contexts.isSessionContextActive() ) {
            boolean passed = false;

            try {
                Identity.instance().checkPermission( new PackageNameType( asset.metaData.packageName ),
                                                     RoleTypes.PACKAGE_DEVELOPER );
            } catch ( RuntimeException e ) {
                if ( asset.metaData.categories.length == 0 ) {
                    Identity.instance().checkPermission( new CategoryPathType( null ),
                                                         RoleTypes.ANALYST );
                } else {
                    RuntimeException exception = null;

                    for ( String cat : asset.metaData.categories ) {
                        try {
                            Identity.instance().checkPermission( new CategoryPathType( cat ),
                                                                 RoleTypes.ANALYST );
                            passed = true;
                        } catch ( RuntimeException re ) {
                            exception = re;
                        }
                    }
                    if ( !passed ) {
                        throw exception;
                    }
                }
            }
        }

        log.info( "USER:" + getCurrentUserName() + " CHECKING IN asset: [" + asset.name + "] UUID: [" + asset.uuid + "] " );

        AssetItem repoAsset = getRulesRepository().loadAssetByUUID( asset.uuid );
        if ( isAssetUpdatedInRepository( asset,
                                         repoAsset ) ) {
            return "ERR: Unable to save this asset, as it has been recently updated by [" + repoAsset.getLastContributor() + "]";
        }

        MetaData meta = asset.metaData;
        MetaDataMapper metaDataMapper = MetaDataMapper.getInstance();
        metaDataMapper.copyFromMetaData( meta,
                                         repoAsset );

        updateEffectiveAndExpiredDate( repoAsset,
                                       meta );

        repoAsset.updateCategoryList( meta.categories );

        ContentHandler handler = ContentManager.getHandler( repoAsset.getFormat() );
        handler.storeAssetContent( asset,
                                   repoAsset );

        if ( !(asset.metaData.format.equals( AssetFormats.TEST_SCENARIO )) || asset.metaData.format.equals( AssetFormats.ENUMERATION ) ) {
            PackageItem pkg = repoAsset.getPackage();
            pkg.updateBinaryUpToDate( false );
            RuleBaseCache.getInstance().remove( pkg.getUUID() );
        }
        repoAsset.checkin( asset.checkinComment );

        return repoAsset.getUUID();
    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public void restoreVersion(String versionUUID,
                               String assetUUID,
                               String comment) {
        AssetItem old = getRulesRepository().loadAssetByUUID( versionUUID );
        AssetItem head = getRulesRepository().loadAssetByUUID( assetUUID );

        log.info( "USER:" + getCurrentUserName() + " RESTORE of asset: [" + head.getName() + "] UUID: [" + head.getUUID() + "] with historical version number: [" + old.getVersionNumber() );

        getRulesRepository().restoreHistoricalAsset( old,
                                                     head,
                                                     comment );

    }

    /**
     * @deprecated in favour of {@link queryMetaData(QueryPageRequest)}
     */
    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public TableDataResult queryMetaData(final MetaDataQuery[] qr,
                                         Date createdAfter,
                                         Date createdBefore,
                                         Date modifiedAfter,
                                         Date modifiedBefore,
                                         boolean seekArchived,
                                         int skip,
                                         int numRows) throws SerializationException {
        if ( numRows == 0 ) {
            throw new DetailedSerializationException( "Unable to return zero results (bug)",
                                                      "probably have the parameters around the wrong way, sigh..." );
        }

        Map<String, String[]> q = new HashMap<String, String[]>() {
            {
                for (MetaDataQuery aQr : qr) {
                    String vals = (aQr.valueList == null) ? "" : aQr.valueList.trim();
                    if (vals.length() > 0) {
                        put(aQr.attribute,
                                vals.split(",\\s?"));
                    }
                }
            }
        };

        DateQuery[] dates = new DateQuery[2];

        dates[0] = new DateQuery( "jcr:created",
                                  isoDate( createdAfter ),
                                  isoDate( createdBefore ) );
        dates[1] = new DateQuery( AssetItem.LAST_MODIFIED_PROPERTY_NAME,
                                  isoDate( modifiedAfter ),
                                  isoDate( modifiedBefore ) );
        AssetItemIterator it = getRulesRepository().query( q,
                                                           seekArchived,
                                                           dates );
        // Add Filter to check Permission
        List<AssetItem> resultList = new ArrayList<AssetItem>();

        RepositoryFilter packageFilter = new PackageFilter();
        RepositoryFilter categoryFilter = new CategoryFilter();

        while ( it.hasNext() ) {
            AssetItem ai = it.next();
            if ( checkPackagePermissionHelper( packageFilter,
                                               ai,
                                               RoleTypes.PACKAGE_READONLY ) || checkCategoryPermissionHelper( categoryFilter,
                                                                                                              ai,
                                                                                                              RoleTypes.ANALYST_READ ) ) {
                resultList.add( ai );
            }
        }

        return new TableDisplayHandler( "searchresults" ).loadRuleListTable( resultList,
                                                                             skip,
                                                                             numRows );
    }

    private boolean checkPackagePermissionHelper(RepositoryFilter filter,
                                                 AssetItem item,
                                                 String roleType) {
        return filter.accept( getConfigDataHelper( item.getPackage().getUUID() ),
                              roleType );
    }

    private PackageConfigData getConfigDataHelper(String uuidStr) {
        PackageConfigData data = new PackageConfigData();
        data.uuid = uuidStr;
        return data;
    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public String createState(String name) throws SerializationException {
        log.info( "USER:" + getCurrentUserName() + " CREATING state: [" + name + "]" );
        try {
            name = HtmlCleaner.cleanHTML( name );
            String uuid = getRulesRepository().createState( name ).getNode().getUUID();
            getRulesRepository().save();
            return uuid;
        } catch ( RepositoryException e ) {
            throw new SerializationException( "Unable to create the status." );
        }
    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public void removeState(String name) throws SerializationException {
        log.info( "USER:" + getCurrentUserName() + " REMOVING state: [" + name + "]" );

        try {
            getRulesRepository().loadState( name ).remove();
            getRulesRepository().save();

        } catch ( RulesRepositoryException e ) {
            throw new DetailedSerializationException( "Unable to remove status. It is probably still used (even by archived items).",
                                                      e.getMessage() );
        }
    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public void renameState(String oldName,
                            String newName) throws SerializationException {
        log.info( "USER:" + getCurrentUserName() + " RENAMING state: [" + oldName + "] to [" + newName + "]" );
        getRulesRepository().renameState( oldName,
                                          newName );

    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public String[] listStates() throws SerializationException {
        StateItem[] states = getRulesRepository().listStates();
        String[] result = new String[states.length];
        for ( int i = 0; i < states.length; i++ ) {
            result[i] = states[i].getName();
        }
        return result;
    }

    @WebRemote
    public void clearRulesRepository() {
        serviceSecurity.checkSecurityIsAdmin();

        RulesRepositoryAdministrator admin = new RulesRepositoryAdministrator( getRulesRepository().getSession() );
        admin.clearRulesRepository();
    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public SuggestionCompletionEngine loadSuggestionCompletionEngine(String packageName) throws SerializationException {
        serviceSecurity.checkSecurityIsPackageReadOnly( packageName );
        SuggestionCompletionEngine suggestionCompletionEngine = null;
        try {
            PackageItem packageItem = getRulesRepository().loadPackage( packageName );
            SuggestionCompletionEngineLoaderInitializer suggestionCompletionEngineLoader = new SuggestionCompletionEngineLoaderInitializer();
            suggestionCompletionEngine = suggestionCompletionEngineLoader.loadFor( packageItem );
        } catch ( RulesRepositoryException e ) {
            log.error( "An error occurred loadSuggestionCompletionEngine: " + e.getMessage() );
            throw new SerializationException( e.getMessage() );
        }
        return suggestionCompletionEngine;
    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public String[] getCustomSelectors() throws SerializationException {
        return SelectorManager.getInstance().getCustomSelectors();
    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public String[] listRulesInGlobalArea() throws SerializationException {
        serviceSecurity.checkSecurityIsPackageReadOnly( RulesRepository.RULE_GLOBAL_AREA );
        return repositoryPackageOperations.listRulesInPackage( RulesRepository.RULE_GLOBAL_AREA );
    }

    /**
     * @deprecated in favour of {@link showLog(PageRequest)}
     */
    @WebRemote
    public LogEntry[] showLog() {
        serviceSecurity.checkSecurityIsAdmin();

        return LoggingHelper.getMessages();
    }

    @WebRemote
    public PageResponse<LogPageRow> showLog(PageRequest request) {
        if ( request == null ) {
            throw new IllegalArgumentException( "request cannot be null" );
        }
        if ( request.getPageSize() != null && request.getPageSize() < 0 ) {
            throw new IllegalArgumentException( "pageSize cannot be less than zero." );
        }

        serviceSecurity.checkSecurityIsAdmin();

        // Do query
        long start = System.currentTimeMillis();
        LogEntry[] logEntries = LoggingHelper.getMessages();
        log.debug( "Search time: " + (System.currentTimeMillis() - start) );

        // Populate response
        PageResponse<LogPageRow> response = new PageResponse<LogPageRow>();
        response.setStartRowIndex( request.getStartRowIndex() );
        response.setTotalRowSize( logEntries.length );
        response.setTotalRowSizeExact( true );
        LogPageRowBuilder logPageRowBuilder = new LogPageRowBuilder();
        List<LogPageRow> rowList = logPageRowBuilder.createRows( request,
                                                                 logEntries );

        response.setPageRowList( rowList );
        response.setLastPage( (rowList.size() + request.getStartRowIndex()) == logEntries.length );

        long methodDuration = System.currentTimeMillis() - start;
        log.debug( "Retrieved Log Entries in " + methodDuration + " ms." );
        return response;
    }

    @WebRemote
    public void cleanLog() {
        serviceSecurity.checkSecurityIsAdmin();

        LoggingHelper.cleanLog();
    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public String[] loadDropDownExpression(String[] valuePairs,
                                           String expression) {
        Map<String, String> context = new HashMap<String, String>();

        for (String valuePair : valuePairs) {
            if (valuePair == null) {
                return new String[0];
            }
            String[] pair = valuePair.split("=");
            context.put(pair[0],
                    pair[1]);
        }
        // first interpolate the pairs
        expression = (String) TemplateRuntime.eval( expression,
                                                    context );

        // now we can eval it for real...
        Object result = MVEL.eval( expression );
        if ( result instanceof String[] ) {
            return (String[]) result;
        } else if ( result instanceof List ) {
            List l = (List) result;
            String[] xs = new String[l.size()];
            for ( int i = 0; i < xs.length; i++ ) {
                Object el = l.get( i );
                xs[i] = el.toString();
            }
            return xs;
        } else {
            return null;
        }
    }

    /**
     * @deprecated in favour of {@link listUserPermissions(PageRequest)}
     */
    @Restrict("#{identity.loggedIn}")
    public Map<String, List<String>> listUserPermissions() {
        serviceSecurity.checkSecurityIsAdmin();

        PermissionManager pm = new PermissionManager( getRulesRepository() );
        return pm.listUsers();
    }

    @Restrict("#{identity.loggedIn}")
    public PageResponse<PermissionsPageRow> listUserPermissions(PageRequest request) {
        if ( request == null ) {
            throw new IllegalArgumentException( "request cannot be null" );
        }
        if ( request.getPageSize() != null && request.getPageSize() < 0 ) {
            throw new IllegalArgumentException( "pageSize cannot be less than zero." );
        }

        serviceSecurity.checkSecurityIsAdmin();

        // Do query
        long start = System.currentTimeMillis();
        PermissionManager pm = new PermissionManager( getRulesRepository() );
        Map<String, List<String>> permissions = pm.listUsers();
        log.debug( "Search time: " + (System.currentTimeMillis() - start) );

        // Populate response
        PageResponse<PermissionsPageRow> response = new PageResponse<PermissionsPageRow>();
        response.setStartRowIndex( request.getStartRowIndex() );
        response.setTotalRowSize( permissions.size() );
        response.setTotalRowSizeExact( true );

        PermissionPageRowBuilder permissionPageRowBuilder = new PermissionPageRowBuilder();
        List<PermissionsPageRow> rowList = permissionPageRowBuilder.createRows( request,
                                                                                permissions );
        response.setPageRowList( rowList );
        response.setLastPage( (rowList.size() + request.getStartRowIndex()) == permissions.size() );

        long methodDuration = System.currentTimeMillis() - start;
        log.debug( "Retrieved Log Entries in " + methodDuration + " ms." );
        return response;
    }

    @Restrict("#{identity.loggedIn}")
    public Map<String, List<String>> retrieveUserPermissions(String userName) {
        serviceSecurity.checkSecurityIsAdmin();

        PermissionManager pm = new PermissionManager( getRulesRepository() );
        return pm.retrieveUserPermissions( userName );
    }

    @Restrict("#{identity.loggedIn}")
    public void updateUserPermissions(String userName,
                                      Map<String, List<String>> perms) {
        serviceSecurity.checkSecurityIsAdmin();

        PermissionManager pm = new PermissionManager( getRulesRepository() );

        log.info( "Updating user permissions for userName [" + userName + "] to [" + perms + "]" );
        pm.updateUserPermissions( userName,
                                  perms );
        getRulesRepository().save();
    }

    @Restrict("#{identity.loggedIn}")
    public String[] listAvailablePermissionTypes() {
        serviceSecurity.checkSecurityIsAdmin();

        return RoleTypes.listAvailableTypes();
    }

    @Restrict("#{identity.loggedIn}")
    public void deleteUser(String userName) {
        log.info( "Removing user permissions for user name [" + userName + "]" );
        PermissionManager pm = new PermissionManager( getRulesRepository() );
        pm.removeUserPermissions( userName );
        getRulesRepository().save();
    }

    @Restrict("#{identity.loggedIn}")
    public void createUser(String userName) {
        log.info( "Creating user permissions, user name [" + userName + "]" );
        PermissionManager pm = new PermissionManager( getRulesRepository() );
        pm.createUser( userName );
        getRulesRepository().save();
    }

    /**
     * @deprecated in favour of {@link loadInbox(InboxPageRequest)}
     */
    @Restrict("#{identity.loggedIn}")
    public TableDataResult loadInbox(String inboxName) throws DetailedSerializationException {
        try {
            UserInbox ib = new UserInbox( getRulesRepository() );
            if ( inboxName.equals( ExplorerNodeConfig.RECENT_VIEWED_ID ) ) {
                return UserInbox.toTable( ib.loadRecentOpened(),
                                          false );
            } else if ( inboxName.equals( ExplorerNodeConfig.RECENT_EDITED_ID ) ) {
                return UserInbox.toTable( ib.loadRecentEdited(),
                                          false );
            } else {
                return UserInbox.toTable( ib.loadIncoming(),
                                          true );
            }
        } catch ( Exception e ) {
            log.error( "Unable to load Inbox: " + e.getMessage() );
            throw new DetailedSerializationException( "Unable to load Inbox",
                                                      e.getMessage() );
        }
    }

    @Restrict("#{identity.loggedIn}")
    public PageResponse<InboxPageRow> loadInbox(InboxPageRequest request) throws DetailedSerializationException {
        if ( request == null ) {
            throw new IllegalArgumentException( "request cannot be null" );
        }
        if ( request.getPageSize() != null && request.getPageSize() < 0 ) {
            throw new IllegalArgumentException( "pageSize cannot be less than zero." );
        }

        String inboxName = request.getInboxName();
        UserInbox userInbox = new UserInbox( getRulesRepository() );
        PageResponse<InboxPageRow> response = new PageResponse<InboxPageRow>();
        long start = System.currentTimeMillis();

        try {

            // Do applicable query
            List<InboxEntry> entries = userInbox.loadEntries( inboxName );
            log.debug( "Search time: " + (System.currentTimeMillis() - start) );

            // Populate response
            Iterator<InboxEntry> iterator = entries.iterator();
            InboxPageRowBuilder inboxPageRowBuilder = new InboxPageRowBuilder();
            List<InboxPageRow> rowList = inboxPageRowBuilder.createRows( request,
                                                                             iterator );

            response.setStartRowIndex( request.getStartRowIndex() );
            response.setTotalRowSize( entries.size() );
            response.setTotalRowSizeExact( true );
            response.setPageRowList( rowList );
            response.setLastPage( !iterator.hasNext() );

            long methodDuration = System.currentTimeMillis() - start;
            log.debug( "Queried inbox ('" + inboxName + "') in " + methodDuration + " ms." );

        } catch ( Exception e ) {
            log.error( "Unable to load Inbox: " + e.getMessage() );
            throw new DetailedSerializationException( "Unable to load Inbox",
                                                      e.getMessage() );
        }
        return response;
    }

    /**
     * Load and process the repository configuration templates.
     */
    public String processTemplate(String name,
                                  Map<String, Object> data) {
        try {
            Configuration cfg = new Configuration();
            cfg.setObjectWrapper( new DefaultObjectWrapper() );
            cfg.setTemplateUpdateDelay( 0 );

            Template temp = new Template( name,
                                          new InputStreamReader( ServiceImplementation.class.getResourceAsStream( "/repoconfig/" + name + ".xml" ) ),
                                          cfg );
            StringWriter strw = new StringWriter();
            temp.process( data,
                          strw );
            return StringEscapeUtils.escapeXml( strw.toString() );
        } catch ( Exception e ) {
            return "";
        }
    }

    /**
     * Returns the Spring context elements specified by SpringContextElementsManager
     * @return a Map containing the key,value pairs of data.
     * @throws DetailedSerializationException 
     */
    public Map<String, String> loadSpringContextElementData() throws DetailedSerializationException {
        try {
            return SpringContextElementsManager.getInstance().getElements();
        } catch ( IOException ex ) {
            log.error( "Error loading Spring Context Elements",
                       ex );
            throw new DetailedSerializationException( "Error loading Spring Context Elements",
                                                      "View server logs for more information" );
        }
    }

    /**
     * Check to see if app context is active (not in hosted)
     */
    public Boolean isHostedMode() {
        return Contexts.isApplicationContextActive() ? Boolean.FALSE : Boolean.TRUE;
    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public PageResponse<QueryPageRow> queryFullText(QueryPageRequest request) throws SerializationException {
        if ( request == null ) {
            throw new IllegalArgumentException( "request cannot be null" );
        }
        if ( request.getPageSize() != null && request.getPageSize() < 0 ) {
            throw new IllegalArgumentException( "pageSize cannot be less than zero." );
        }

        // Do query
        long start = System.currentTimeMillis();
        AssetItemIterator it = getRulesRepository().queryFullText( request.getSearchText(),
                                                                   request.isSearchArchived() );
        log.debug( "Search time: " + (System.currentTimeMillis() - start) );

        // Populate response
        long totalRowsCount = it.getSize();
        PageResponse<QueryPageRow> response = new PageResponse<QueryPageRow>();
        QueryFullTextPageRowBuilder queryFullTextPageRowBuilder = new QueryFullTextPageRowBuilder();
        List<QueryPageRow> rowList = queryFullTextPageRowBuilder.createRows( request,
                                                                                 it );
        boolean bHasMoreRows = it.hasNext();
        response.setStartRowIndex( request.getStartRowIndex() );
        response.setPageRowList( rowList );
        response.setLastPage( !bHasMoreRows );

        // Fix Total Row Size
        ServiceRowSizeHelper serviceRowSizeHelper = new ServiceRowSizeHelper();
        serviceRowSizeHelper.fixTotalRowSize( request,
                                              response,
                                              totalRowsCount,
                                              rowList.size(),
                                              bHasMoreRows );

        long methodDuration = System.currentTimeMillis() - start;
        log.debug( "Queried repository (Full Text) for (" + request.getSearchText() + ") in " + methodDuration + " ms." );
        return response;
    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public PageResponse<QueryPageRow> queryMetaData(QueryMetadataPageRequest request) throws SerializationException {
        if ( request == null ) {
            throw new IllegalArgumentException( "request cannot be null" );
        }
        if ( request.getPageSize() != null && request.getPageSize() < 0 ) {
            throw new IllegalArgumentException( "pageSize cannot be less than zero." );
        }

        // Setup parameters for generic repository query
        Map<String, String[]> queryMap = createQueryMap( request.getMetadata() );

        DateQuery[] dates = createDateQueryForRepository( request );

        // Do query
        long start = System.currentTimeMillis();
        AssetItemIterator it = getRulesRepository().query( queryMap,
                                                           request.isSearchArchived(),
                                                           dates );
        log.debug( "Search time: " + (System.currentTimeMillis() - start) );

        // Populate response
        long totalRowsCount = it.getSize();
        PageResponse<QueryPageRow> response = new PageResponse<QueryPageRow>();
        QueryMetadataPageRowBuilder queryMetadataPageRowBuilder = new QueryMetadataPageRowBuilder();
        List<QueryPageRow> rowList = queryMetadataPageRowBuilder.createRows( request,
                                                                                 it );
        boolean bHasMoreRows = it.hasNext();
        response.setStartRowIndex( request.getStartRowIndex() );
        response.setPageRowList( rowList );
        response.setLastPage( !bHasMoreRows );

        // Fix Total Row Size
        ServiceRowSizeHelper serviceRowSizeHelper = new ServiceRowSizeHelper();
        serviceRowSizeHelper.fixTotalRowSize( request,
                                              response,
                                              totalRowsCount,
                                              rowList.size(),
                                              bHasMoreRows );

        long methodDuration = System.currentTimeMillis() - start;
        log.debug( "Queried repository (Metadata) in " + methodDuration + " ms." );
        return response;

    }

    private Map<String, String[]> createQueryMap(final List<MetaDataQuery> metaDataQuerys) {
        Map<String, String[]> queryMap = new HashMap<String, String[]>();
        for ( MetaDataQuery metaDataQuery : metaDataQuerys ) {
            String vals = (metaDataQuery.valueList == null) ? "" : metaDataQuery.valueList.trim();
            if ( vals.length() > 0 ) {
                queryMap.put( metaDataQuery.attribute,
                              vals.split( ",\\s?" ) );
            }
        }
        return queryMap;
    }

    private DateQuery[] createDateQueryForRepository(QueryMetadataPageRequest request) {
        DateQuery[] dates = new DateQuery[2];
        dates[0] = new DateQuery( "jcr:created",
                                  isoDate( request.getCreatedAfter() ),
                                  isoDate( request.getCreatedBefore() ) );
        dates[1] = new DateQuery( AssetItem.LAST_MODIFIED_PROPERTY_NAME,
                                  isoDate( request.getLastModifiedAfter() ),
                                  isoDate( request.getLastModifiedBefore() ) );
        return dates;
    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public PageResponse<StatePageRow> loadRuleListForState(StatePageRequest request) throws SerializationException {
        if ( request == null ) {
            throw new IllegalArgumentException( "request cannot be null" );
        }
        if ( request.getPageSize() != null && request.getPageSize() < 0 ) {
            throw new IllegalArgumentException( "pageSize cannot be less than zero." );
        }

        // Do query
        long start = System.currentTimeMillis();

        // TODO: May need to use a filter for both package and categories
        RepositoryFilter filter = new AssetItemFilter();

        // NOTE: Filtering is handled in repository.findAssetsByState()
        int numRowsToReturn = (request.getPageSize() == null ? -1 : request.getPageSize());
        AssetItemPageResult result = getRulesRepository().findAssetsByState( request.getStateName(),
                                                                             false,
                                                                             request.getStartRowIndex(),
                                                                             numRowsToReturn,
                                                                             filter );
        log.debug( "Search time: " + (System.currentTimeMillis() - start) );

        // Populate response
        boolean bHasMoreRows = result.hasNext;
        PageResponse<StatePageRow> response = new PageResponse<StatePageRow>();
        StatePageRowBuilder statePageRowBuilder = new StatePageRowBuilder();
        List<StatePageRow> rowList = statePageRowBuilder.createRows( request,
                                                                     result.assets.iterator() );

        response.setStartRowIndex( request.getStartRowIndex() );
        response.setPageRowList( rowList );
        response.setLastPage( !bHasMoreRows );

        // Fix Total Row Size
        ServiceRowSizeHelper serviceRowSizeHelper = new ServiceRowSizeHelper();
        serviceRowSizeHelper.fixTotalRowSize( request,
                                              response,
                                              -1,
                                              rowList.size(),
                                              bHasMoreRows );

        long methodDuration = System.currentTimeMillis() - start;
        log.debug( "Searched for Assest with State (" + request.getStateName() + ") in " + methodDuration + " ms." );
        return response;
    }

    private void updateEffectiveAndExpiredDate(AssetItem repoAsset,
                                               MetaData meta) {
        repoAsset.updateDateEffective( dateToCalendar( meta.dateEffective ) );
        repoAsset.updateDateExpired( dateToCalendar( meta.dateExpired ) );
    }

    private boolean isAssetUpdatedInRepository(RuleAsset asset,
                                               AssetItem repoAsset) {
        return asset.lastModified.before( repoAsset.getLastModified().getTime() );
    }

    private Calendar dateToCalendar(Date date) {
        if ( date == null ) {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime( date );
        return cal;
    }

    private boolean checkCategoryPermissionHelper(RepositoryFilter filter,
                                                  AssetItem item,
                                                  String roleType) {
        List<CategoryItem> tempCateList = item.getCategories();
        for (CategoryItem categoryItem : tempCateList) {
            if (filter.accept(categoryItem.getName(),
                    roleType)) {
                return true;
            }
        }

        return false;
    }

    private String isoDate(Date d) {
        if ( d != null ) {
            Calendar cal = Calendar.getInstance();
            cal.setTime( d );
            return ISO8601.format( cal );
        }
        return null;
    }

    /**
     * Pushes a message back to (all) clients.
     */
    private void push(String messageType,
                      String message) {
        Backchannel.getInstance().publish( new PushResponse( messageType,
                                                             message ) );
    }

    private String getCurrentUserName() {
        return getRulesRepository().getSession().getUserID();
    }

    public List<PushResponse> subscribe() {
        return Backchannel.getInstance().subscribe();
    }

}
