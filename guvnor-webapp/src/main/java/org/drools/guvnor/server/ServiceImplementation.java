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

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import javax.jcr.ItemExistsException;
import javax.jcr.RepositoryException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.drools.ClockType;
import org.drools.RuleBase;
import org.drools.RuleBaseConfiguration;
import org.drools.RuleBaseFactory;
import org.drools.SessionConfiguration;
import org.drools.base.ClassTypeResolver;
import org.drools.common.AbstractRuleBase;
import org.drools.common.InternalRuleBase;
import org.drools.common.InternalWorkingMemory;
import org.drools.compiler.DrlParser;
import org.drools.compiler.DroolsParserException;
import org.drools.core.util.DroolsStreamUtils;
import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.explorer.ExplorerNodeConfig;
import org.drools.guvnor.client.rpc.AbstractAssetPageRow;
import org.drools.guvnor.client.rpc.BuilderResult;
import org.drools.guvnor.client.rpc.BulkTestRunResult;
import org.drools.guvnor.client.rpc.CategoryPageRequest;
import org.drools.guvnor.client.rpc.CategoryPageRow;
import org.drools.guvnor.client.rpc.DetailedSerializationException;
import org.drools.guvnor.client.rpc.DiscussionRecord;
import org.drools.guvnor.client.rpc.InboxIncomingPageRow;
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
import org.drools.guvnor.client.rpc.ScenarioResultSummary;
import org.drools.guvnor.client.rpc.ScenarioRunResult;
import org.drools.guvnor.client.rpc.SingleScenarioResult;
import org.drools.guvnor.client.rpc.SnapshotComparisonPageRequest;
import org.drools.guvnor.client.rpc.SnapshotComparisonPageResponse;
import org.drools.guvnor.client.rpc.SnapshotComparisonPageRow;
import org.drools.guvnor.client.rpc.SnapshotDiff;
import org.drools.guvnor.client.rpc.SnapshotDiffs;
import org.drools.guvnor.client.rpc.SnapshotInfo;
import org.drools.guvnor.client.rpc.StatePageRequest;
import org.drools.guvnor.client.rpc.StatePageRow;
import org.drools.guvnor.client.rpc.TableConfig;
import org.drools.guvnor.client.rpc.TableDataResult;
import org.drools.guvnor.client.rpc.ValidatedResponse;
import org.drools.guvnor.client.widgets.tables.AbstractPagedTable;
import org.drools.guvnor.server.builder.AuditLogReporter;
import org.drools.guvnor.server.builder.BRMSPackageBuilder;
import org.drools.guvnor.server.contenthandler.ContentHandler;
import org.drools.guvnor.server.contenthandler.ContentManager;
import org.drools.guvnor.server.contenthandler.ModelContentHandler;
import org.drools.guvnor.server.repository.MailboxService;
import org.drools.guvnor.server.repository.UserInbox;
import org.drools.guvnor.server.ruleeditor.springcontext.SpringContextElementsManager;
import org.drools.guvnor.server.security.AdminType;
import org.drools.guvnor.server.security.CategoryPathType;
import org.drools.guvnor.server.security.PackageNameType;
import org.drools.guvnor.server.security.PackageUUIDType;
import org.drools.guvnor.server.security.RoleTypes;
import org.drools.guvnor.server.selector.SelectorManager;
import org.drools.guvnor.server.util.BRMSSuggestionCompletionLoader;
import org.drools.guvnor.server.util.Discussion;
import org.drools.guvnor.server.util.ISO8601;
import org.drools.guvnor.server.util.LoggingHelper;
import org.drools.guvnor.server.util.MetaDataMapper;
import org.drools.guvnor.server.util.QueryPageRowFactory;
import org.drools.guvnor.server.util.ServiceRowSizeHelper;
import org.drools.guvnor.server.util.TableDisplayHandler;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.testing.Scenario;
import org.drools.lang.descr.PackageDescr;
import org.drools.lang.descr.TypeDeclarationDescr;
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
import org.drools.rule.Package;
import org.drools.runtime.rule.ConsequenceException;
import org.drools.testframework.RuleCoverageListener;
import org.drools.testframework.ScenarioRunner;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.remoting.WebRemote;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.security.Identity;
import org.jboss.seam.web.Session;
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
     * Used for a simple cache of binary packages to avoid serialization from
     * the database - for test scenarios.
     */
    public static Map<String, RuleBase> ruleBaseCache               = Collections.synchronizedMap( new HashMap<String, RuleBase>() );

    /**
     * This is used for pushing messages back to the client.
     */
    private static Backchannel          backchannel                 = new Backchannel();
    private ServiceSecurity             serviceSecurity             = new ServiceSecurity();

    private RepositoryAssetOperations   repositoryAssetOperations   = new RepositoryAssetOperations();
    private RepositoryPackageOperations repositoryPackageOperations = new RepositoryPackageOperations();

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

    public static Backchannel getBackchannel() {
        return backchannel;
    }

    /**
     * Role-based Authorization check: This method only returns packages that
     * the user has permission to access. User has permission to access the
     * particular package when: The user has a package.readonly role or higher
     * (i.e., package.admin, package.developer) to this package.
     */
    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public PackageConfigData[] listPackages() {
        return listPackages( null );
    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public PackageConfigData[] listPackages(String workspace) {
        RepositoryFilter pf = new PackageFilter();
        return repositoryPackageOperations.listPackages( false,
                                                         workspace,
                                                         pf );
    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public PackageConfigData[] listArchivedPackages() {
        return listArchivedPackages( null );
    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public PackageConfigData[] listArchivedPackages(String workspace) {
        RepositoryFilter pf = new PackageFilter();
        return repositoryPackageOperations.listPackages( true,
                                                         workspace,
                                                         pf );
    }

    public PackageConfigData loadGlobalPackage() {
        return repositoryPackageOperations.loadGlobalPackage();
    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public void rebuildPackages() throws SerializationException {
        Iterator<PackageItem> pkit = getRulesRepository().listPackages();
        StringBuilder errs = new StringBuilder();
        while ( pkit.hasNext() ) {
            PackageItem pkg = pkit.next();
            try {
                BuilderResult res = this.buildPackage( pkg.getUUID(),
                                                       true );
                if ( res != null ) {
                    errs.append( "Unable to build package name [" + pkg.getName() + "]\n" );
                    StringBuilder buf = new StringBuilder();
                    for ( int i = 0; i < res.getLines().size(); i++ ) {
                        buf.append( res.getLines().get( i ).toString() );
                        buf.append( '\n' );
                    }
                    log.warn( buf.toString() );
                }
            } catch ( Exception e ) {
                log.error( "An error occurred building package [" + pkg.getName() + "]\n" );
                errs.append( "An error occurred building package [" + pkg.getName() + "]\n" );
            }
        }

        if ( errs.toString().length() > 0 ) {
            throw new DetailedSerializationException( "Unable to rebuild all packages.",
                                                      errs.toString() );
        }
    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public String buildPackageSource(String packageUUID) throws SerializationException {
        serviceSecurity.checkSecurityIsPackageDeveloper( packageUUID );
        return repositoryPackageOperations.buildPackageSource( packageUUID );
    }

    @WebRemote
    public void copyPackage(String sourcePackageName,
                            String destPackageName) throws SerializationException {
        serviceSecurity.checkSecurityIsAdmin();
        repositoryPackageOperations.copyPackage( sourcePackageName,
                                                 destPackageName );
    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public void removePackage(String uuid) {
        serviceSecurity.checkSecurityIsPackageAdmin( uuid );
        repositoryPackageOperations.removePackage( uuid );
    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public String renamePackage(String uuid,
                                String newName) {
        serviceSecurity.checkSecurityIsPackageAdmin( uuid );

        return repositoryPackageOperations.renamePackage( uuid,
                                                          newName );
    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public byte[] exportPackages(String packageName) {
        serviceSecurity.checkSecurityIsPackageNameTypeAdmin( packageName );
        return repositoryPackageOperations.exportPackages( packageName );
    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    // TODO: Not working. GUVNOR-475
    public void importPackages(byte[] byteArray,
                               boolean importAsNew) {
        repositoryPackageOperations.importPackages( byteArray,
                                                    importAsNew );
    }

    @WebRemote
    public String createPackage(String name,
                                String description,
                                String[] workspace) throws RulesRepositoryException {
        serviceSecurity.checkSecurityIsAdmin();
        return repositoryPackageOperations.createPackage( name,
                                                          description,
                                                          workspace );
    }

    @WebRemote
    public String createPackage(String name,
                                String description) throws RulesRepositoryException {
        return createPackage( name,
                              description,
                              new String[]{} );
    }

    @WebRemote
    public String createSubPackage(String name,
                                   String description,
                                   String parentNode) throws SerializationException {
        serviceSecurity.checkSecurityIsAdmin();
        return repositoryPackageOperations.createSubPackage( name,
                                                             description,
                                                             parentNode );
    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public PackageConfigData loadPackageConfig(String uuid) {
        PackageItem packageItem = getRulesRepository().loadPackageByUUID( uuid );
        // the uuid passed in is the uuid of that deployment bundle, not the
        // package uudi.
        // we have to figure out the package name.
        serviceSecurity.checkSecurityNameTypePackageReadOnly( packageItem );
        return repositoryPackageOperations.loadPackageConfig( packageItem );
    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public ValidatedResponse savePackage(PackageConfigData data) throws SerializationException {
        serviceSecurity.checkSecurityIsPackageDeveloper( data.uuid );
        return repositoryPackageOperations.savePackage( data );

    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public BuilderResult buildPackage(String packageUUID,
                                      boolean force) throws SerializationException {
        return buildPackage( packageUUID,
                             force,
                             null,
                             null,
                             null,
                             false,
                             null,
                             null,
                             false,
                             null );
    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public BuilderResult buildPackage(String packageUUID,
                                      boolean force,
                                      String buildMode,
                                      String statusOperator,
                                      String statusDescriptionValue,
                                      boolean enableStatusSelector,
                                      String categoryOperator,
                                      String category,
                                      boolean enableCategorySelector,
                                      String customSelectorName) throws SerializationException {
        serviceSecurity.checkSecurityIsPackageDeveloper( packageUUID );
        return repositoryPackageOperations.buildPackage( packageUUID,
                                                         force,
                                                         buildMode,
                                                         statusOperator,
                                                         statusDescriptionValue,
                                                         enableStatusSelector,
                                                         categoryOperator,
                                                         category,
                                                         enableCategorySelector,
                                                         customSelectorName );
    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public void createPackageSnapshot(String packageName,
                                      String snapshotName,
                                      boolean replaceExisting,
                                      String comment) {
        serviceSecurity.checkSecurityIsPackageNameTypeAdmin( packageName );
        repositoryPackageOperations.createPackageSnapshot( packageName,
                                                           snapshotName,
                                                           replaceExisting,
                                                           comment );

    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public void copyOrRemoveSnapshot(String packageName,
                                     String snapshotName,
                                     boolean delete,
                                     String newSnapshotName) throws SerializationException {
        serviceSecurity.checkSecurityIsPackageNameTypeAdmin( packageName );
        repositoryPackageOperations.copyOrRemoveSnapshot( packageName,
                                                          snapshotName,
                                                          delete,
                                                          newSnapshotName );
    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public String[] listRulesInPackage(String packageName) throws SerializationException {
        serviceSecurity.checkSecurityIsPackageReadOnly( packageName );
        return repositoryPackageOperations.listRulesInPackage( packageName );
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

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public String[] loadChildCategories(String categoryPath) {
        List<String> resultList = new ArrayList<String>();
        CategoryFilter filter = new CategoryFilter();

        CategoryItem item = getRulesRepository().loadCategory( categoryPath );
        List children = item.getChildTags();
        for ( int i = 0; i < children.size(); i++ ) {
            String childCategoryName = ((CategoryItem) children.get( i )).getName();
            if ( filter.acceptNavigate( categoryPath,
                                        childCategoryName ) ) {
                resultList.add( childCategoryName );
            }
        }

        String[] resultArr = resultList.toArray( new String[resultList.size()] );
        return resultArr;
    }

    @WebRemote
    public Boolean createCategory(String path,
                                  String name,
                                  String description) {
        serviceSecurity.checkSecurityIsAdmin();

        log.info( "USER:" + getCurrentUserName() + " CREATING cateogory: [" + name + "] in path [" + path + "]" );

        if ( path == null || "".equals( path ) ) {
            path = "/";
        }
        path = cleanHTML( path );

        getRulesRepository().loadCategory( path ).addCategory( name,
                                                               description );
        getRulesRepository().save();
        return Boolean.TRUE;
    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public void renameCategory(String fullPathAndName,
                               String newName) {
        getRulesRepository().renameCategory( fullPathAndName,
                                             newName );
    }

    /**
     * loadRuleListForCategories
     *
     * Role-based Authorization check: This method only returns rules that the user has
     * permission to access. The user is considered to has permission to access the particular category when:
     * The user has ANALYST_READ role or higher (i.e., ANALYST) to this category
     * 
     * @deprecated in favour of {@link loadRuleListForCategories(CategoryPageRequest)}
     */
    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public TableDataResult loadRuleListForCategories(String categoryPath,
                                                     int skip,
                                                     int numRows,
                                                     String tableConfig) throws SerializationException {

        // First check the user has permission to access this categoryPath.
        if ( Contexts.isSessionContextActive() ) {
            if ( !Identity.instance().hasPermission( new CategoryPathType( categoryPath ),
                                                     RoleTypes.ANALYST_READ ) ) {

                TableDisplayHandler handler = new TableDisplayHandler( tableConfig );
                return handler.loadRuleListTable( new AssetItemPageResult() );
            }
        }

        AssetItemPageResult result = getRulesRepository().findAssetsByCategory( categoryPath,
                                                                                false,
                                                                                skip,
                                                                                numRows );
        TableDisplayHandler handler = new TableDisplayHandler( tableConfig );
        return handler.loadRuleListTable( result );

    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public PageResponse<CategoryPageRow> loadRuleListForCategories(CategoryPageRequest request) throws SerializationException {
        if ( request == null ) {
            throw new IllegalArgumentException( "request cannot be null" );
        }
        if ( request.getPageSize() != null && request.getPageSize() < 0 ) {
            throw new IllegalArgumentException( "pageSize cannot be less than zero." );
        }

        PageResponse<CategoryPageRow> response = new PageResponse<CategoryPageRow>();

        // Role-based Authorization check: This method only returns rules that
        // the user has permission to access. The user is considered to has
        // permission to access the particular category when: The user has
        // ANALYST_READ role or higher (i.e., ANALYST) to this category
        if ( Contexts.isSessionContextActive() ) {
            if ( !Identity.instance().hasPermission( new CategoryPathType( request.getCategoryPath() ),
                                                     RoleTypes.ANALYST_READ ) ) {
                return response;
            }
        }

        // Do query
        long start = System.currentTimeMillis();

        // NOTE: Filtering is handled in repository.findAssetsByCategory()
        int numRowsToReturn = (request.getPageSize() == null ? -1 : request.getPageSize());
        AssetItemPageResult result = getRulesRepository().findAssetsByCategory( request.getCategoryPath(),
                                                                                false,
                                                                                request.getStartRowIndex(),
                                                                                numRowsToReturn );
        log.debug( "Search time: " + (System.currentTimeMillis() - start) );

        // Populate response
        boolean bHasMoreRows = result.hasNext;
        List<CategoryPageRow> rowList = fillCategoryPageRows( request,
                                                              result );
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
        log.debug( "Searched for Assest with Category (" + request.getCategoryPath() + ") in " + methodDuration + " ms." );
        return response;
    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public void removeCategory(String categoryPath) throws SerializationException {
        log.info( "USER:" + getCurrentUserName() + " REMOVING CATEGORY path: [" + categoryPath + "]" );

        try {
            getRulesRepository().loadCategory( categoryPath ).remove();
            getRulesRepository().save();
        } catch ( RulesRepositoryException e ) {
            log.info( "Unable to remove category [" + categoryPath + "]. It is probably still used: " + e.getMessage() );

            throw new DetailedSerializationException( "Unable to remove category. It is probably still used.",
                                                      e.getMessage() );
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

            applyPreBuiltTemplates( ruleName,
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
     * For some format types, we add some sugar by adding a new template.
     */
    private void applyPreBuiltTemplates(String ruleName,
                                        String format,
                                        AssetItem asset) {
        if ( format.equals( AssetFormats.DSL_TEMPLATE_RULE ) ) {
            asset.updateContent( "when\n\nthen\n" );
        } else if ( format.equals( AssetFormats.FUNCTION ) ) {
            asset.updateContent( "function <returnType> " + ruleName + "(<args here>) {\n\n\n}" );
        } else if ( format.equals( AssetFormats.DSL ) ) {
            asset.updateContent( "[when]Condition sentence template {var}=" + "rule language mapping {var}\n" + "[then]Action sentence template=rule language mapping" );
        } else if ( format.equals( AssetFormats.DECISION_SPREADSHEET_XLS ) ) {
            asset.updateBinaryContentAttachment( this.getClass().getResourceAsStream( "/SampleDecisionTable.xls" ) );
            asset.updateBinaryContentAttachmentFileName( "SampleDecisionTable.xls" );
        } else if ( format.equals( AssetFormats.DRL ) ) {
            asset.updateContent( "when\n\t#conditions\nthen\n\t#actions" );
        } else if ( format.equals( AssetFormats.ENUMERATION ) ) {

        } else if ( format.equals( AssetFormats.SPRING_CONTEXT ) ) {
            try {
                ByteArrayOutputStream outContent = new ByteArrayOutputStream();
                BufferedInputStream inContent = new BufferedInputStream( this.getClass().getClassLoader().getResourceAsStream( "spring-context-sample.xml" ) );
                IOUtils.copy( inContent,
                              outContent );

                asset.updateContent( outContent.toString() );
            } catch ( IOException ex ) {
                log.error( "Error reading spring-context-sample.xml",
                           ex );
                throw new IllegalArgumentException( "Error reading spring-context-sample.xml" );
            }
        }
    }

    public void updateDependency(String uuid,
                                 String dependencyPath) {
        PackageItem item = getRulesRepository().loadPackageByUUID( uuid );
        item.updateDependency( dependencyPath );
        item.checkin( "Update dependency" );
    }

    public String[] getDependencies(String uuid) {
        PackageItem item = getRulesRepository().loadPackageByUUID( uuid );
        return item.getDependencies();
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

        log.info( "USER:" + getCurrentUserName() + " CHECKING IN asset: [" + asset.metaData.name + "] UUID: [" + asset.uuid + "] " );

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
            ServiceImplementation.ruleBaseCache.remove( pkg.getUUID() );
        }
        repoAsset.checkin( meta.checkinComment );

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
                for ( int i = 0; i < qr.length; i++ ) {
                    String vals = (qr[i].valueList == null) ? "" : qr[i].valueList.trim();
                    if ( vals.length() > 0 ) {
                        put( qr[i].attribute,
                             vals.split( ",\\s?" ) );
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

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public String createState(String name) throws SerializationException {
        log.info( "USER:" + getCurrentUserName() + " CREATING state: [" + name + "]" );
        try {
            name = cleanHTML( name );
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

    /**
     * 
     * Role-based Authorization check: This method can be accessed if user has
     * following permissions: 1. The user has a Analyst role and this role has
     * permission to access the category which the asset belongs to. Or. 2. The
     * user has a package.developer role or higher (i.e., package.admin) and
     * this role has permission to access the package which the asset belongs
     * to.
     */
    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public void changeState(String uuid,
                            String newState,
                            boolean wholePackage) {

        if ( !wholePackage ) {
            AssetItem asset = getRulesRepository().loadAssetByUUID( uuid );

            // Verify if the user has permission to access the asset through
            // package based permission.
            // If failed, then verify if the user has permission to access the
            // asset through category
            // based permission
            if ( Contexts.isSessionContextActive() ) {
                boolean passed = false;

                try {
                    Identity.instance().checkPermission( new PackageUUIDType( asset.getPackage().getUUID() ),
                                                         RoleTypes.PACKAGE_DEVELOPER );
                } catch ( RuntimeException e ) {
                    if ( asset.getCategories().size() == 0 ) {
                        Identity.instance().checkPermission( new CategoryPathType( null ),
                                                             RoleTypes.ANALYST );
                    } else {
                        RuntimeException exception = null;

                        for ( CategoryItem cat : asset.getCategories() ) {
                            try {
                                Identity.instance().checkPermission( new CategoryPathType( cat.getName() ),
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

            log.info( "USER:" + getCurrentUserName() + " CHANGING ASSET STATUS. Asset name, uuid: " + "[" + asset.getName() + ", " + asset.getUUID() + "]" + " to [" + newState + "]" );
            String oldState = asset.getStateDescription();
            asset.updateState( newState );

            push( "statusChange",
                  oldState );
            push( "statusChange",
                  newState );

            addToDiscussionForAsset( asset.getUUID(),
                                     oldState + " -> " + newState );
        } else {
            serviceSecurity.checkSecurityIsPackageDeveloper( uuid );

            PackageItem pkg = getRulesRepository().loadPackageByUUID( uuid );
            log.info( "USER:" + getCurrentUserName() + " CHANGING Package STATUS. Asset name, uuid: " + "[" + pkg.getName() + ", " + pkg.getUUID() + "]" + " to [" + newState + "]" );
            pkg.changeStatus( newState );
        }
        getRulesRepository().save();
    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public SnapshotInfo[] listSnapshots(String packageName) {
        serviceSecurity.checkSecurityIsPackageDeveloper( packageName );

        String[] snaps = getRulesRepository().listPackageSnapshots( packageName );
        SnapshotInfo[] res = new SnapshotInfo[snaps.length];
        for ( int i = 0; i < snaps.length; i++ ) {
            PackageItem snap = getRulesRepository().loadPackageSnapshot( packageName,
                                                                         snaps[i] );
            SnapshotInfo info = new SnapshotInfo();
            res[i] = info;
            info.comment = snap.getCheckinComment();
            info.name = snaps[i];
            info.uuid = snap.getUUID();
        }
        return res;
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

        SuggestionCompletionEngine result = null;
        ClassLoader originalCL = Thread.currentThread().getContextClassLoader();
        try {
            PackageItem pkg = getRulesRepository().loadPackage( packageName );
            BRMSSuggestionCompletionLoader loader = null;
            List<JarInputStream> jars = BRMSPackageBuilder.getJars( pkg );
            if ( jars != null && !jars.isEmpty() ) {
                ClassLoader cl = BRMSPackageBuilder.createClassLoader( jars );

                Thread.currentThread().setContextClassLoader( cl );

                loader = new BRMSSuggestionCompletionLoader( cl );
            } else {
                loader = new BRMSSuggestionCompletionLoader();
            }

            result = loader.getSuggestionEngine( pkg );

        } catch ( RulesRepositoryException e ) {
            log.error( "An error occurred loadSuggestionCompletionEngine: " + e.getMessage() );
            throw new SerializationException( e.getMessage() );
        } finally {
            Thread.currentThread().setContextClassLoader( originalCL );
        }
        return result;
    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public String[] getCustomSelectors() throws SerializationException {
        return SelectorManager.getInstance().getCustomSelectors();
    }

    @WebRemote
    public void rebuildSnapshots() throws SerializationException {
        serviceSecurity.checkSecurityIsAdmin();

        Iterator<PackageItem> pkit = getRulesRepository().listPackages();
        while ( pkit.hasNext() ) {
            PackageItem pkg = pkit.next();
            String[] snaps = getRulesRepository().listPackageSnapshots( pkg.getName() );
            for ( String snapName : snaps ) {
                PackageItem snap = getRulesRepository().loadPackageSnapshot( pkg.getName(),
                                                                             snapName );
                BuilderResult res = this.buildPackage( snap.getUUID(),
                                                       true );
                if ( res != null ) {
                    StringBuilder stringBuilder = new StringBuilder();
                    for ( int i = 0; i < res.getLines().size(); i++ ) {
                        stringBuilder.append( res.getLines().get( i ).toString() );
                        stringBuilder.append( '\n' );
                    }
                    throw new DetailedSerializationException( "Unable to rebuild snapshot [" + snapName,
                                                              stringBuilder.toString() + "]" );
                }
            }
        }
    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public String[] listRulesInGlobalArea() throws SerializationException {
        return listRulesInPackage( RulesRepository.RULE_GLOBAL_AREA );
    }

    @Restrict("#{identity.loggedIn}")
    public List<DiscussionRecord> loadDiscussionForAsset(String assetId) {
        return new Discussion().fromString( getRulesRepository().loadAssetByUUID( assetId ).getStringProperty( Discussion.DISCUSSION_PROPERTY_KEY ) );
    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public SingleScenarioResult runScenario(String packageName,
                                            Scenario scenario) throws SerializationException {
        serviceSecurity.checkSecurityIsPackageDeveloper( packageName );

        return runScenario( packageName,
                            scenario,
                            null );

    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public BulkTestRunResult runScenariosInPackage(String packageUUID) throws SerializationException {
        serviceSecurity.checkSecurityIsPackageDeveloper( packageUUID );
        PackageItem item = getRulesRepository().loadPackageByUUID( packageUUID );
        return runScenariosInPackage( item );
    }

    public BulkTestRunResult runScenariosInPackage(PackageItem item) throws DetailedSerializationException,
                                                                    SerializationException {
        ClassLoader originalCL = Thread.currentThread().getContextClassLoader();
        ClassLoader cl = null;

        try {
            if ( item.isBinaryUpToDate() && ServiceImplementation.ruleBaseCache.containsKey( item.getUUID() ) ) {
                RuleBase rb = ServiceImplementation.ruleBaseCache.get( item.getUUID() );
                AbstractRuleBase arb = (AbstractRuleBase) rb;
                // load up the existing class loader from before
                cl = arb.getConfiguration().getClassLoader();
                Thread.currentThread().setContextClassLoader( cl );
            } else {
                // load up the classloader we are going to use
                List<JarInputStream> jars = BRMSPackageBuilder.getJars( item );
                cl = BRMSPackageBuilder.createClassLoader( jars );
                Thread.currentThread().setContextClassLoader( cl );

                // we have to build the package, and try again.
                if ( item.isBinaryUpToDate() ) {
                    ServiceImplementation.ruleBaseCache.put( item.getUUID(),
                                                             loadRuleBase( item,
                                                                           cl ) );
                } else {
                    BuilderResult result = repositoryPackageOperations.buildPackage( item,
                                                                                     false );
                    if ( result == null || result.getLines().size() == 0 ) {
                        ServiceImplementation.ruleBaseCache.put( item.getUUID(),
                                                                 loadRuleBase( item,
                                                                               cl ) );
                    } else {
                        return new BulkTestRunResult( result,
                                                      null,
                                                      0,
                                                      null );
                    }
                }
            }

            AssetItemIterator it = item.listAssetsByFormat( new String[]{AssetFormats.TEST_SCENARIO} );
            List<ScenarioResultSummary> resultSummaries = new ArrayList<ScenarioResultSummary>();
            RuleBase rb = ruleBaseCache.get( item.getUUID() );
            Package bin = rb.getPackages()[0];

            RuleCoverageListener coverage = new RuleCoverageListener( expectedRules( bin ) );

            while ( it.hasNext() ) {
                AssetItem as = it.next();
                if ( !as.getDisabled() ) {
                    RuleAsset asset = repositoryAssetOperations.loadAsset( as );
                    Scenario sc = (Scenario) asset.content;
                    runScenario( item.getName(),
                                 sc,
                                 coverage );// runScenario(sc, res,
                                            // workingMemory).scenario;

                    int[] totals = sc.countFailuresTotal();
                    resultSummaries.add( new ScenarioResultSummary( totals[0],
                                                                    totals[1],
                                                                    asset.metaData.name,
                                                                    asset.metaData.description,
                                                                    asset.uuid ) );
                }
            }

            ScenarioResultSummary[] summaries = resultSummaries.toArray( new ScenarioResultSummary[resultSummaries.size()] );

            BulkTestRunResult result = new BulkTestRunResult( null,
                                                              resultSummaries.toArray( summaries ),
                                                              coverage.getPercentCovered(),
                                                              coverage.getUnfiredRules() );
            return result;

        } finally {
            Thread.currentThread().setContextClassLoader( originalCL );
        }
    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public String[] listTypesInPackage(String packageUUID) throws SerializationException {
        if ( Contexts.isSessionContextActive() ) {
            Identity.instance().checkPermission( new PackageUUIDType( packageUUID ),
                                                 RoleTypes.PACKAGE_READONLY );
        }

        PackageItem pkg = this.getRulesRepository().loadPackageByUUID( packageUUID );
        List<String> res = new ArrayList<String>();
        AssetItemIterator it = pkg.listAssetsByFormat( new String[]{AssetFormats.MODEL, AssetFormats.DRL_MODEL} );

        JarInputStream jis = null;

        try {
            while ( it.hasNext() ) {
                AssetItem asset = (AssetItem) it.next();
                if ( !asset.isArchived() ) {
                    if ( asset.getFormat().equals( AssetFormats.MODEL ) ) {
                        jis = typesForModel( res,
                                             asset );
                    } else {
                        typesForOthers( res,
                                        asset );
                    }

                }
            }
            return res.toArray( new String[res.size()] );
        } catch ( IOException e ) {
            log.error( "Unable to read the jar files in the package: " + e.getMessage() );
            throw new DetailedSerializationException( "Unable to read the jar files in the package.",
                                                      e.getMessage() );
        } finally {
            IOUtils.closeQuietly( jis );
        }

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
        LogEntry[] entries = LoggingHelper.getMessages();
        log.debug( "Search time: " + (System.currentTimeMillis() - start) );

        // Populate response
        PageResponse<LogPageRow> response = new PageResponse<LogPageRow>();
        response.setStartRowIndex( request.getStartRowIndex() );
        response.setTotalRowSize( entries.length );
        response.setTotalRowSizeExact( true );

        int rowNumber = 0;
        int rowMinNumber = request.getStartRowIndex();
        int rowMaxNumber = request.getPageSize() == null ? entries.length : Math.min( rowMinNumber + request.getPageSize(),
                                                                                      entries.length );
        int resultsSize = (request.getPageSize() == null ? entries.length : request.getPageSize());
        List<LogPageRow> rowList = new ArrayList<LogPageRow>( resultsSize );
        for ( rowNumber = rowMinNumber; rowNumber < rowMaxNumber; rowNumber++ ) {
            LogEntry e = entries[rowNumber];
            LogPageRow row = new LogPageRow();
            row.setSeverity( e.severity );
            row.setMessage( e.message );
            row.setTimestamp( e.timestamp );
            rowList.add( row );
        }
        response.setPageRowList( rowList );
        response.setLastPage( rowNumber == entries.length );

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
        for ( int i = 0; i < valuePairs.length; i++ ) {
            if ( valuePairs[i] == null ) {
                return new String[0];
            }
            String[] pair = valuePairs[i].split( "=" );
            context.put( pair[0],
                         pair[1] );
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

        int rowNumber = 0;
        int rowMinNumber = request.getStartRowIndex();
        int rowMaxNumber = request.getPageSize() == null ? permissions.size() : rowMinNumber + request.getPageSize();
        int resultsSize = (request.getPageSize() == null ? permissions.size() : request.getPageSize());
        List<PermissionsPageRow> rowList = new ArrayList<PermissionsPageRow>( resultsSize );
        Iterator<String> mapItr = permissions.keySet().iterator();
        while ( mapItr.hasNext() && rowNumber < rowMaxNumber ) {
            String userName = mapItr.next();
            if ( rowNumber >= rowMinNumber ) {
                List<String> userPermissions = permissions.get( userName );
                PermissionsPageRow row = new PermissionsPageRow();
                row.setUserName( userName );
                row.setUserPermissions( userPermissions );
                rowList.add( row );
            }
            rowNumber++;
        }
        response.setPageRowList( rowList );
        response.setLastPage( !mapItr.hasNext() );

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

    @Restrict("#{identity.loggedIn}")
    public void installSampleRepository() throws SerializationException {
        checkIfADMIN();
        getRulesRepository().importRepository( this.getClass().getResourceAsStream( "/mortgage-sample-repository.xml" ) );
        this.rebuildPackages();
        this.rebuildSnapshots();
    }

    @Restrict("#{identity.loggedIn}")
    public List<DiscussionRecord> addToDiscussionForAsset(String assetId,
                                                          String comment) {
        RulesRepository repo = getRulesRepository();
        AssetItem asset = repo.loadAssetByUUID( assetId );
        Discussion dp = new Discussion();
        List<DiscussionRecord> discussion = dp.fromString( asset.getStringProperty( Discussion.DISCUSSION_PROPERTY_KEY ) );
        discussion.add( new DiscussionRecord( repo.getSession().getUserID(),
                                              StringEscapeUtils.escapeXml( comment ) ) );
        asset.updateStringProperty( dp.toString( discussion ),
                                    Discussion.DISCUSSION_PROPERTY_KEY,
                                    false );
        repo.save();

        push( "discussion",
              assetId );

        MailboxService.getInstance().recordItemUpdated( asset );

        return discussion;
    }

    @Restrict("#{identity.loggedIn}")
    public void clearAllDiscussionsForAsset(final String assetId) {
        checkIfADMIN();
        RulesRepository repo = getRulesRepository();
        AssetItem asset = repo.loadAssetByUUID( assetId );
        asset.updateStringProperty( "",
                                    "discussion" );
        repo.save();

        push( "discussion",
              assetId );

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
        UserInbox ib = new UserInbox( getRulesRepository() );
        List<InboxEntry> entries = new ArrayList<InboxEntry>();
        PageResponse<InboxPageRow> response = new PageResponse<InboxPageRow>();
        long start = System.currentTimeMillis();

        try {

            // Do applicable query
            if ( inboxName.equals( ExplorerNodeConfig.RECENT_VIEWED_ID ) ) {
                entries = ib.loadRecentOpened();
                log.debug( "Search time: " + (System.currentTimeMillis() - start) );

            } else if ( inboxName.equals( ExplorerNodeConfig.RECENT_EDITED_ID ) ) {
                entries = ib.loadRecentEdited();
                log.debug( "Search time: " + (System.currentTimeMillis() - start) );

            } else {
                entries = ib.loadIncoming();
                log.debug( "Search time: " + (System.currentTimeMillis() - start) );

            }

            // Populate response
            Iterator<InboxEntry> it = entries.iterator();
            List<InboxPageRow> rowList = fillInboxPageRows( request,
                                                            it );

            response.setStartRowIndex( request.getStartRowIndex() );
            response.setTotalRowSize( entries.size() );
            response.setTotalRowSizeExact( true );
            response.setPageRowList( rowList );
            response.setLastPage( !it.hasNext() );

            long methodDuration = System.currentTimeMillis() - start;
            log.debug( "Queried inbox ('" + inboxName + "') in " + methodDuration + " ms." );

        } catch ( Exception e ) {
            log.error( "Unable to load Inbox: " + e.getMessage() );
            throw new DetailedSerializationException( "Unable to load Inbox",
                                                      e.getMessage() );
        }
        return response;
    }

    public List<PushResponse> subscribe() {
        if ( Contexts.isApplicationContextActive() && !Session.instance().isInvalid() ) {
            try {
                return backchannel.await( getCurrentUserName() );
            } catch ( InterruptedException e ) {
                return new ArrayList<PushResponse>();
            }
        } else {
            return new ArrayList<PushResponse>();
        }
    }

    public String cleanHTML(String s) {
        return s.replace( "<",
                          "&lt;" ).replace( ">",
                                            "&gt;" );
    }

    /**
     * @deprecated in favour of {@link compareSnapshots(SnapshotComparisonPageRequest)}
     */
    public SnapshotDiffs compareSnapshots(String packageName,
                                          String firstSnapshotName,
                                          String secondSnapshotName) {
        SnapshotDiffs diffs = new SnapshotDiffs();
        List<SnapshotDiff> list = new ArrayList<SnapshotDiff>();

        PackageItem leftPackage = getRulesRepository().loadPackageSnapshot( packageName,
                                                                            firstSnapshotName );
        PackageItem rightPackage = getRulesRepository().loadPackageSnapshot( packageName,
                                                                             secondSnapshotName );

        // Older one has to be on the left.
        if ( isRightOlderThanLeft( leftPackage,
                                   rightPackage ) ) {
            PackageItem temp = leftPackage;
            leftPackage = rightPackage;
            rightPackage = temp;

            diffs.leftName = secondSnapshotName;
            diffs.rightName = firstSnapshotName;
        } else {
            diffs.leftName = firstSnapshotName;
            diffs.rightName = secondSnapshotName;
        }

        Iterator<AssetItem> leftExistingIter = leftPackage.getAssets();
        while ( leftExistingIter.hasNext() ) {
            AssetItem left = leftExistingIter.next();
            if ( isPackageItemDeleted( rightPackage,
                                       left ) ) {
                SnapshotDiff diff = new SnapshotDiff();

                diff.name = left.getName();
                diff.diffType = SnapshotDiff.TYPE_DELETED;
                diff.leftUuid = left.getUUID();

                list.add( diff );
            }
        }

        Iterator<AssetItem> rightExistingIter = rightPackage.getAssets();
        while ( rightExistingIter.hasNext() ) {
            AssetItem right = rightExistingIter.next();
            AssetItem left = null;
            if ( right != null && leftPackage.containsAsset( right.getName() ) ) {
                left = leftPackage.loadAsset( right.getName() );
            }

            // Asset is deleted or added
            if ( right == null || left == null ) {
                SnapshotDiff diff = new SnapshotDiff();

                if ( left == null ) {
                    diff.name = right.getName();
                    diff.diffType = SnapshotDiff.TYPE_ADDED;
                    diff.rightUuid = right.getUUID();
                }

                list.add( diff );
            } else if ( isAssetArchivedOrRestored( right,
                                                   left ) ) { // Has the asset
                                                              // been archived
                                                              // or restored
                SnapshotDiff diff = new SnapshotDiff();

                diff.name = right.getName();
                diff.leftUuid = left.getUUID();
                diff.rightUuid = right.getUUID();

                if ( left.isArchived() ) {
                    diff.diffType = SnapshotDiff.TYPE_RESTORED;
                } else {
                    diff.diffType = SnapshotDiff.TYPE_ARCHIVED;
                }

                list.add( diff );
            } else if ( isAssetItemUpdated( right,
                                            left ) ) { // Has the asset been
                                                       // updated
                SnapshotDiff diff = new SnapshotDiff();

                diff.name = right.getName();
                diff.leftUuid = left.getUUID();
                diff.rightUuid = right.getUUID();
                diff.diffType = SnapshotDiff.TYPE_UPDATED;

                list.add( diff );
            }
        }

        diffs.diffs = list.toArray( new SnapshotDiff[list.size()] );
        return diffs;
    }

    public SnapshotComparisonPageResponse compareSnapshots(SnapshotComparisonPageRequest request) {
        if ( request == null ) {
            throw new IllegalArgumentException( "request cannot be null" );
        }
        if ( request.getPageSize() != null && request.getPageSize() < 0 ) {
            throw new IllegalArgumentException( "pageSize cannot be less than zero." );
        }

        SnapshotComparisonPageResponse response = new SnapshotComparisonPageResponse();

        // Do query (bit of a cheat really!)
        long start = System.currentTimeMillis();
        SnapshotDiffs diffs = compareSnapshots( request.getPackageName(),
                                                request.getFirstSnapshotName(),
                                                request.getSecondSnapshotName() );
        log.debug( "Search time: " + (System.currentTimeMillis() - start) );

        // Populate response
        response.setLeftSnapshotName( diffs.leftName );
        response.setRightSnapshotName( diffs.rightName );
        List<SnapshotComparisonPageRow> rowList = new ArrayList<SnapshotComparisonPageRow>();

        int pageStart = request.getStartRowIndex();
        int numRowsToReturn = (request.getPageSize() == null ? diffs.diffs.length : request.getPageSize());
        int maxRow = Math.min( numRowsToReturn,
                               diffs.diffs.length - request.getStartRowIndex() );
        for ( int i = pageStart; i < pageStart + maxRow; i++ ) {
            SnapshotComparisonPageRow pr = new SnapshotComparisonPageRow();
            pr.setDiff( diffs.diffs[i] );
            rowList.add( pr );
        }
        response.setPageRowList( rowList );
        response.setStartRowIndex( request.getStartRowIndex() );
        response.setTotalRowSize( diffs.diffs.length );
        response.setTotalRowSizeExact( true );
        response.setLastPage( (pageStart + maxRow == diffs.diffs.length) );

        long methodDuration = System.currentTimeMillis() - start;
        log.debug( "Compared Snapshots ('" + request.getFirstSnapshotName() + "') and ('" + request.getSecondSnapshotName() + "') in package ('" + request.getPackageName() + "') in " + methodDuration + " ms." );

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
        Boolean hm = Contexts.isApplicationContextActive() ? Boolean.FALSE : Boolean.TRUE;
        return hm;
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
        List<QueryPageRow> rowList = fillQueryFullTextPageRows( request,
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
        Map<String, String[]> q = new HashMap<String, String[]>();
        for ( MetaDataQuery md : request.getMetadata() ) {
            String vals = (md.valueList == null) ? "" : md.valueList.trim();
            if ( vals.length() > 0 ) {
                q.put( md.attribute,
                       vals.split( ",\\s?" ) );
            }
        }

        DateQuery[] dates = new DateQuery[2];
        dates[0] = new DateQuery( "jcr:created",
                                  isoDate( request.getCreatedAfter() ),
                                  isoDate( request.getCreatedBefore() ) );
        dates[1] = new DateQuery( AssetItem.LAST_MODIFIED_PROPERTY_NAME,
                                  isoDate( request.getLastModifiedAfter() ),
                                  isoDate( request.getLastModifiedBefore() ) );

        // Do query
        long start = System.currentTimeMillis();
        AssetItemIterator it = getRulesRepository().query( q,
                                                           request.isSearchArchived(),
                                                           dates );
        log.debug( "Search time: " + (System.currentTimeMillis() - start) );

        // Populate response
        long totalRowsCount = it.getSize();
        PageResponse<QueryPageRow> response = new PageResponse<QueryPageRow>();
        List<QueryPageRow> rowList = fillQueryMetadataPageRows( request,
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
        List<StatePageRow> rowList = fillStatePageRows( request,
                                                        result );
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
        return asset.metaData.lastModifiedDate.before( repoAsset.getLastModified().getTime() );
    }

    private Calendar dateToCalendar(Date date) {
        if ( date == null ) {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime( date );
        return cal;
    }

    private boolean checkPackagePermissionHelper(RepositoryFilter filter,
                                                 AssetItem item,
                                                 String roleType) {
        return filter.accept( getConfigDataHelper( item.getPackage().getUUID() ),
                              roleType );
    }

    private boolean checkCategoryPermissionHelper(RepositoryFilter filter,
                                                  AssetItem item,
                                                  String roleType) {
        List<CategoryItem> tempCateList = item.getCategories();
        for ( Iterator<CategoryItem> i = tempCateList.iterator(); i.hasNext(); ) {
            CategoryItem categoryItem = i.next();

            if ( filter.accept( categoryItem.getName(),
                                roleType ) ) {
                return true;
            }
        }

        return false;
    }

    private PackageConfigData getConfigDataHelper(String uuidStr) {
        PackageConfigData data = new PackageConfigData();
        data.uuid = uuidStr;
        return data;
    }

    private String isoDate(Date d) {
        if ( d != null ) {
            Calendar cal = Calendar.getInstance();
            cal.setTime( d );
            return ISO8601.format( cal );
        }
        return null;
    }

    private SingleScenarioResult runScenario(String packageName,
                                             Scenario scenario,
                                             RuleCoverageListener coverage) throws SerializationException {
        PackageItem item = this.getRulesRepository().loadPackage( packageName );
        SingleScenarioResult result = null;
        // nasty classloader needed to make sure we use the same tree the whole
        // time.
        ClassLoader originalCL = Thread.currentThread().getContextClassLoader();
        try {
            final RuleBase rb = loadCacheRuleBase( item );

            ClassLoader cl = ((InternalRuleBase) ServiceImplementation.ruleBaseCache.get( item.getUUID() )).getRootClassLoader();
            Thread.currentThread().setContextClassLoader( cl );
            result = runScenario( scenario,
                                  item,
                                  cl,
                                  rb,
                                  coverage );
        } catch ( Exception e ) {
            if ( e instanceof DetailedSerializationException ) {
                DetailedSerializationException err = (DetailedSerializationException) e;
                result = new SingleScenarioResult();
                if ( err.getErrs() != null ) {
                    result.result = new ScenarioRunResult( err.getErrs(),
                                                           null );
                } else {
                    throw err;
                }
            }
        } finally {
            Thread.currentThread().setContextClassLoader( originalCL );
        }
        return result;
    }

    /*
     * Set the Rule base in a cache
     */
    private RuleBase loadCacheRuleBase(PackageItem item) throws DetailedSerializationException {
        RuleBase rb = null;
        if ( item.isBinaryUpToDate() && ServiceImplementation.ruleBaseCache.containsKey( item.getUUID() ) ) {
            rb = ServiceImplementation.ruleBaseCache.get( item.getUUID() );
        } else {
            // load up the classloader we are going to use
            List<JarInputStream> jars = BRMSPackageBuilder.getJars( item );
            ClassLoader buildCl = BRMSPackageBuilder.createClassLoader( jars );

            // we have to build the package, and try again.
            if ( item.isBinaryUpToDate() ) {
                rb = loadRuleBase( item,
                                   buildCl );
                ServiceImplementation.ruleBaseCache.put( item.getUUID(),
                                                         rb );
            } else {
                BuilderResult result = repositoryPackageOperations.buildPackage( item,
                                                                                 false );
                if ( result == null || result.getLines().size() == 0 ) {
                    rb = loadRuleBase( item,
                                       buildCl );
                    ServiceImplementation.ruleBaseCache.put( item.getUUID(),
                                                             rb );
                } else throw new DetailedSerializationException( "Build error",
                                                                 result.getLines() );
            }

        }
        return rb;
    }

    private RuleBase loadRuleBase(PackageItem item,
                                  ClassLoader cl) throws DetailedSerializationException {
        try {
            return deserKnowledgebase( item,
                                       cl );
        } catch ( ClassNotFoundException e ) {
            log.error( "Unable to load rule base.",
                       e );
            throw new DetailedSerializationException( "A required class was not found.",
                                                      e.getMessage() );
        } catch ( Exception e ) {
            log.error( "Unable to load rule base.",
                       e );
            log.info( "...but trying to rebuild binaries..." );
            try {
                BuilderResult res = repositoryPackageOperations.buildPackage( item,
                                                                              true );
                if ( res != null && res.getLines().size() > 0 ) {
                    log.error( "There were errors when rebuilding the knowledgebase." );
                    throw new DetailedSerializationException( "There were errors when rebuilding the knowledgebase.",
                                                              "" );
                }
            } catch ( Exception e1 ) {
                log.error( "Unable to rebuild the rulebase: " + e.getMessage() );
                throw new DetailedSerializationException( "Unable to rebuild the rulebase.",
                                                          e.getMessage() );
            }
            try {
                return deserKnowledgebase( item,
                                           cl );
            } catch ( Exception e2 ) {
                log.error( "Unable to reload knowledgebase: " + e.getMessage() );
                throw new DetailedSerializationException( "Unable to reload knowledgebase.",
                                                          e.getMessage() );
            }

        }
    }

    private RuleBase deserKnowledgebase(PackageItem item,
                                        ClassLoader cl) throws IOException,
                                                       ClassNotFoundException {
        RuleBase rb = RuleBaseFactory.newRuleBase( new RuleBaseConfiguration( cl ) );
        Package bin = (Package) DroolsStreamUtils.streamIn( item.getCompiledPackageBytes(),
                                                            cl );
        rb.addPackage( bin );
        return rb;
    }

    private SingleScenarioResult runScenario(Scenario scenario,
                                             PackageItem item,
                                             ClassLoader cl,
                                             RuleBase rb,
                                             RuleCoverageListener coverage) throws DetailedSerializationException {

        // RuleBase rb = ruleBaseCache.get(item.getUUID());
        Package bin = rb.getPackages()[0];

        Set<String> imps = bin.getImports().keySet();
        Set<String> allImps = new HashSet<String>( imps );
        if ( bin.getGlobals() != null ) {
            for ( Iterator iterator = bin.getGlobals().keySet().iterator(); iterator.hasNext(); ) {
                allImps.add( bin.getGlobals().get( iterator.next() ) );
            }
        }
        allImps.add( bin.getName() + ".*" ); // need this for Generated beans to
        // work

        ClassTypeResolver res = new ClassTypeResolver( allImps,
                                                       cl );
        SessionConfiguration sessionConfiguration = new SessionConfiguration();
        sessionConfiguration.setClockType( ClockType.PSEUDO_CLOCK );
        sessionConfiguration.setKeepReference( false );
        InternalWorkingMemory workingMemory = (InternalWorkingMemory) rb.newStatefulSession( sessionConfiguration,
                                                                                             null );
        if ( coverage != null ) workingMemory.addEventListener( coverage );
        try {
            AuditLogReporter logger = new AuditLogReporter( workingMemory );
            new ScenarioRunner( scenario,
                                res,
                                workingMemory );
            SingleScenarioResult r = new SingleScenarioResult();
            r.auditLog = logger.buildReport();
            r.result = new ScenarioRunResult( null,
                                              scenario );
            return r;
        } catch ( ClassNotFoundException e ) {
            log.error( "Unable to load a required class.",
                       e );
            throw new DetailedSerializationException( "Unable to load a required class.",
                                                      e.getMessage() );
        } catch ( ConsequenceException e ) {
            String messageShort = "There was an error executing the consequence of rule [" + e.getRule().getName() + "]";
            String messageLong = e.getMessage();
            if ( e.getCause() != null ) {
                messageLong += "\nCAUSED BY " + e.getCause().getMessage();
            }

            log.error( messageShort + ": " + messageLong,
                       e );
            throw new DetailedSerializationException( messageShort,
                                                      messageLong );
        } catch ( Exception e ) {
            log.error( "Unable to run the scenario.",
                       e );
            throw new DetailedSerializationException( "Unable to run the scenario.",
                                                      e.getMessage() );
        }
    }

    private HashSet<String> expectedRules(Package bin) {
        HashSet<String> h = new HashSet<String>();
        for ( int i = 0; i < bin.getRules().length; i++ ) {
            h.add( bin.getRules()[i].getName() );
        }
        return h;
    }

    private void typesForOthers(List<String> res,
                                AssetItem asset) {
        // its delcared model
        DrlParser parser = new DrlParser();
        try {
            PackageDescr desc = parser.parse( asset.getContent() );
            List<TypeDeclarationDescr> types = desc.getTypeDeclarations();
            for ( TypeDeclarationDescr typeDeclarationDescr : types ) {
                res.add( typeDeclarationDescr.getTypeName() );
            }
        } catch ( DroolsParserException e ) {
            log.error( "An error occurred parsing rule: " + e.getMessage() );

        }
    }

    private JarInputStream typesForModel(List<String> res,
                                         AssetItem asset) throws IOException {
        JarInputStream jis;
        jis = new JarInputStream( asset.getBinaryContentAttachment() );
        JarEntry entry = null;
        while ( (entry = jis.getNextJarEntry()) != null ) {
            if ( !entry.isDirectory() ) {
                if ( entry.getName().endsWith( ".class" ) ) {
                    res.add( ModelContentHandler.convertPathToName( entry.getName() ) );
                }
            }
        }
        return jis;
    }

    /**
     * Pushes a message back to (all) clients.
     */
    private void push(String messageType,
                      String message) {
        backchannel.publish( new PushResponse( messageType,
                                               message ) );
    }

    private String getCurrentUserName() {
        return getRulesRepository().getSession().getUserID();
    }

    private void checkIfADMIN() {
        if ( Contexts.isApplicationContextActive() ) {
            Identity.instance().checkPermission( new AdminType(),
                                                 RoleTypes.ADMIN );
        }
    }

    private boolean isAssetArchivedOrRestored(AssetItem right,
                                              AssetItem left) {
        return right.isArchived() != left.isArchived();
    }

    private boolean isAssetItemUpdated(AssetItem right,
                                       AssetItem left) {
        return right.getLastModified().compareTo( left.getLastModified() ) != 0;
    }

    private boolean isPackageItemDeleted(PackageItem rightPackage,
                                         AssetItem left) {
        return !rightPackage.containsAsset( left.getName() );
    }

    private boolean isRightOlderThanLeft(PackageItem leftPackage,
                                         PackageItem rightPackage) {
        return leftPackage.getLastModified().compareTo( rightPackage.getLastModified() ) > 0;
    }

    private List<InboxPageRow> fillInboxPageRows(InboxPageRequest request,
                                                 Iterator<InboxEntry> it) {
        int skipped = 0;
        Integer pageSize = request.getPageSize();
        int startRowIndex = request.getStartRowIndex();
        List<InboxPageRow> rowList = new ArrayList<InboxPageRow>();
        while ( it.hasNext() && (pageSize == null || rowList.size() < pageSize) ) {
            InboxEntry ie = (InboxEntry) it.next();

            if ( skipped >= startRowIndex ) {
                rowList.add( makeInboxPageRow( ie,
                                               request ) );
            }
            skipped++;
        }
        return rowList;
    }

    private List<QueryPageRow> fillQueryFullTextPageRows(QueryPageRequest request,
                                                         AssetItemIterator it) {
        int skipped = 0;
        Integer pageSize = request.getPageSize();
        int startRowIndex = request.getStartRowIndex();
        RepositoryFilter filter = new PackageFilter();

        List<QueryPageRow> rowList = new ArrayList<QueryPageRow>();

        while ( it.hasNext() && (pageSize == null || rowList.size() < pageSize) ) {
            AssetItem assetItem = (AssetItem) it.next();

            // Filter surplus assets
            if ( checkPackagePermissionHelper( filter,
                                               assetItem,
                                               RoleTypes.PACKAGE_READONLY ) ) {

                // Cannot use AssetItemIterator.skip() as it skips non-filtered
                // assets whereas startRowIndex is the index of the
                // first displayed asset (i.e. filtered)
                if ( skipped >= startRowIndex ) {
                    rowList.add( QueryPageRowFactory.makeQueryPageRow( assetItem ) );
                }
                skipped++;
            }
        }
        return rowList;
    }

    private List<QueryPageRow> fillQueryMetadataPageRows(QueryMetadataPageRequest request,
                                                         AssetItemIterator it) {
        int skipped = 0;
        Integer pageSize = request.getPageSize();
        int startRowIndex = request.getStartRowIndex();
        RepositoryFilter packageFilter = new PackageFilter();
        RepositoryFilter categoryFilter = new CategoryFilter();
        List<QueryPageRow> rowList = new ArrayList<QueryPageRow>();

        while ( it.hasNext() && (pageSize == null || rowList.size() < pageSize) ) {
            AssetItem assetItem = (AssetItem) it.next();

            // Filter surplus assets
            if ( checkPackagePermissionHelper( packageFilter,
                                               assetItem,
                                               RoleTypes.PACKAGE_READONLY ) || checkCategoryPermissionHelper( categoryFilter,
                                                                                                              assetItem,
                                                                                                              RoleTypes.ANALYST_READ ) ) {

                // Cannot use AssetItemIterator.skip() as it skips non-filtered
                // assets whereas startRowIndex is the index of the
                // first displayed asset (i.e. filtered)
                if ( skipped >= startRowIndex ) {
                    rowList.add( QueryPageRowFactory.makeQueryPageRow( assetItem ) );
                }
                skipped++;
            }
        }
        return rowList;
    }

    private InboxPageRow makeInboxPageRow(InboxEntry ie,
                                          InboxPageRequest request) {
        InboxPageRow row = null;
        if ( request.getInboxName().equals( ExplorerNodeConfig.INCOMING_ID ) ) {
            InboxIncomingPageRow tr = new InboxIncomingPageRow();
            tr.setUuid( ie.assetUUID );
            tr.setFormat( AssetFormats.BUSINESS_RULE );
            tr.setNote( ie.note );
            tr.setName( ie.note );
            tr.setTimestamp( new Date( ie.timestamp ) );
            tr.setFrom( ie.from );
            row = tr;

        } else {
            InboxPageRow tr = new InboxPageRow();
            tr.setUuid( ie.assetUUID );
            tr.setNote( ie.note );
            tr.setName( ie.note );
            tr.setTimestamp( new Date( ie.timestamp ) );
            row = tr;
        }
        return row;
    }

    private void populatePageRowBaseProperties(AssetItem assetItem,
                                               AbstractAssetPageRow row) {
        row.setUuid( assetItem.getUUID() );
        row.setFormat( assetItem.getFormat() );
        row.setName( assetItem.getName() );
    }

    private List<StatePageRow> fillStatePageRows(StatePageRequest request,
                                                 AssetItemPageResult result) {
        List<StatePageRow> rowList = new ArrayList<StatePageRow>();

        // Filtering and skipping records to the required page is handled in
        // repository.findAssetsByState() so we only need to simply copy
        Iterator<AssetItem> it = result.assets.iterator();
        while ( it.hasNext() ) {
            AssetItem assetItem = (AssetItem) it.next();
            rowList.add( makeStatePageRow( assetItem ) );
        }
        return rowList;
    }

    private StatePageRow makeStatePageRow(AssetItem assetItem) {
        StatePageRow row = new StatePageRow();
        populatePageRowBaseProperties( assetItem,
                                       row );
        row.setDescription( assetItem.getDescription() );
        row.setAbbreviatedDescription( StringUtils.abbreviate( assetItem.getDescription(),
                                                               80 ) );
        row.setLastModified( assetItem.getLastModified().getTime() );
        row.setStateName( assetItem.getState().getName() );
        row.setPackageName( assetItem.getPackageName() );
        return row;
    }

    private List<CategoryPageRow> fillCategoryPageRows(CategoryPageRequest request,
                                                       AssetItemPageResult result) {
        List<CategoryPageRow> rowList = new ArrayList<CategoryPageRow>();

        // Filtering and skipping records to the required page is handled in
        // repository.findAssetsByState() so we only need to simply copy
        Iterator<AssetItem> it = result.assets.iterator();
        while ( it.hasNext() ) {
            AssetItem assetItem = (AssetItem) it.next();
            rowList.add( makeCategoryPageRow( assetItem ) );
        }
        return rowList;
    }

    private CategoryPageRow makeCategoryPageRow(AssetItem assetItem) {
        CategoryPageRow row = new CategoryPageRow();
        populatePageRowBaseProperties( assetItem,
                                       row );
        row.setDescription( assetItem.getDescription() );
        row.setAbbreviatedDescription( StringUtils.abbreviate( assetItem.getDescription(),
                                                               80 ) );
        row.setLastModified( assetItem.getLastModified().getTime() );
        row.setStateName( assetItem.getState().getName() );
        row.setPackageName( assetItem.getPackageName() );
        return row;
    }

}
