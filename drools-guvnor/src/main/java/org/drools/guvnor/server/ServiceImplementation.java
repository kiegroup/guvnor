package org.drools.guvnor.server;

/*
 * Copyright 2005 JBoss Inc
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.jcr.ItemExistsException;
import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.commons.io.IOUtils;
import org.apache.jackrabbit.util.ISO8601;
import org.apache.log4j.Logger;
import org.drools.RuleBase;
import org.drools.RuleBaseConfiguration;
import org.drools.RuleBaseFactory;
import org.drools.SessionConfiguration;
import org.drools.base.ClassTypeResolver;
import org.drools.common.AbstractRuleBase;
import org.drools.common.DroolsObjectOutputStream;
import org.drools.common.InternalRuleBase;
import org.drools.common.InternalWorkingMemory;
import org.drools.compiler.DrlParser;
import org.drools.compiler.DroolsParserException;
import org.drools.compiler.PackageBuilderConfiguration;
import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.modeldriven.SuggestionCompletionEngine;
import org.drools.guvnor.client.modeldriven.testing.Scenario;
import org.drools.guvnor.client.rpc.AnalysisReport;
import org.drools.guvnor.client.rpc.BuilderResult;
import org.drools.guvnor.client.rpc.BulkTestRunResult;
import org.drools.guvnor.client.rpc.DetailedSerializableException;
import org.drools.guvnor.client.rpc.LogEntry;
import org.drools.guvnor.client.rpc.MetaData;
import org.drools.guvnor.client.rpc.MetaDataQuery;
import org.drools.guvnor.client.rpc.PackageConfigData;
import org.drools.guvnor.client.rpc.RepositoryService;
import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.guvnor.client.rpc.ScenarioResultSummary;
import org.drools.guvnor.client.rpc.ScenarioRunResult;
import org.drools.guvnor.client.rpc.SingleScenarioResult;
import org.drools.guvnor.client.rpc.SnapshotInfo;
import org.drools.guvnor.client.rpc.TableConfig;
import org.drools.guvnor.client.rpc.TableDataResult;
import org.drools.guvnor.client.rpc.TableDataRow;
import org.drools.guvnor.client.rpc.ValidatedResponse;
import org.drools.guvnor.server.builder.AuditLogReporter;
import org.drools.guvnor.server.builder.BRMSPackageBuilder;
import org.drools.guvnor.server.builder.ContentAssemblyError;
import org.drools.guvnor.server.builder.ContentPackageAssembler;
import org.drools.guvnor.server.contenthandler.ContentHandler;
import org.drools.guvnor.server.contenthandler.ContentManager;
import org.drools.guvnor.server.contenthandler.IRuleAsset;
import org.drools.guvnor.server.contenthandler.IValidating;
import org.drools.guvnor.server.contenthandler.ModelContentHandler;
import org.drools.guvnor.server.security.AdminType;
import org.drools.guvnor.server.security.CategoryPathType;
import org.drools.guvnor.server.security.PackageNameType;
import org.drools.guvnor.server.security.PackageUUIDType;
import org.drools.guvnor.server.security.RoleTypes;
import org.drools.guvnor.server.util.AssetFormatHelper;
import org.drools.guvnor.server.util.AssetLockManager;
import org.drools.guvnor.server.util.BRMSSuggestionCompletionLoader;
import org.drools.guvnor.server.util.LoggingHelper;
import org.drools.guvnor.server.util.MetaDataMapper;
import org.drools.guvnor.server.util.TableDisplayHandler;
import org.drools.guvnor.server.util.VerifierRunner;
import org.drools.lang.descr.PackageDescr;
import org.drools.lang.descr.RuleDescr;
import org.drools.lang.descr.TypeDeclarationDescr;
import org.drools.repository.AssetHistoryIterator;
import org.drools.repository.AssetItem;
import org.drools.repository.AssetItemIterator;
import org.drools.repository.AssetPageList;
import org.drools.repository.CategoryItem;
import org.drools.repository.PackageItem;
import org.drools.repository.PackageIterator;
import org.drools.repository.RepositoryFilter;
import org.drools.repository.RulesRepository;
import org.drools.repository.RulesRepositoryAdministrator;
import org.drools.repository.RulesRepositoryException;
import org.drools.repository.StateItem;
import org.drools.repository.VersionableItem;
import org.drools.repository.RulesRepository.DateQuery;
import org.drools.repository.security.PermissionManager;
import org.drools.rule.Package;
import org.drools.testframework.RuleCoverageListener;
import org.drools.testframework.ScenarioRunner;
import org.drools.util.DroolsStreamUtils;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.remoting.WebRemote;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.security.Identity;
import org.mvel2.MVEL;
import org.mvel2.templates.TemplateRuntime;

import com.google.gwt.user.client.rpc.SerializableException;

/**
 * This is the implementation of the repository service to drive the GWT based
 * front end.
 *
 * @author Michael Neale
 */
@Name("org.drools.guvnor.client.rpc.RepositoryService")
@AutoCreate
public class ServiceImplementation
    implements
    RepositoryService {

	/**
	 * Maximum number of rules to display in "list rules in package" method
	 */
	private static final int MAX_RULES_TO_SHOW_IN_PACKAGE_LIST = 5000;

    @In
    public RulesRepository          repository;

    private static final long       serialVersionUID = 400L;

    private static final DateFormat dateFormatter    = DateFormat.getInstance();

    private static final Logger     log              = LoggingHelper.getLogger();

    private MetaDataMapper          metaDataMapper   = new MetaDataMapper();

    /**
     * Used for a simple cache of binary packages to avoid serialization from
     * the database - for test scenarios.
     */
    static Map<String, RuleBase>    ruleBaseCache    = Collections.synchronizedMap( new HashMap<String, RuleBase>() );

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public String[] loadChildCategories(String categoryPath) {
        List<String> resultList = new ArrayList<String>();
        CategoryFilter filter = new CategoryFilter();

        CategoryItem item = repository.loadCategory( categoryPath );
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
        if ( Contexts.isSessionContextActive() ) {
            Identity.instance().checkPermission( new AdminType(),
                                                 RoleTypes.ADMIN );
        }

        log.info( "USER:" + repository.getSession().getUserID() + " CREATING cateogory: [" + name + "] in path [" + path + "]" );

        if ( path == null || "".equals( path ) ) {
            path = "/";
        }

        CategoryItem item = repository.loadCategory( path );
        item.addCategory( name,
                          description );
        repository.save();
        return Boolean.TRUE;
    }

    /**
     * This will create a new asset. It will be saved, but not checked in. The
     * initial state will be the draft state.
     */
    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public String createNewRule(String ruleName,
                                String description,
                                String initialCategory,
                                String initialPackage,
                                String format) throws SerializableException {
        if ( Contexts.isSessionContextActive() ) {
            Identity.instance().checkPermission( new PackageNameType( initialPackage ),
                                                 RoleTypes.PACKAGE_DEVELOPER );
        }

        log.info( "USER:" + repository.getSession().getUserID() + " CREATING new asset name [" + ruleName + "] in package [" + initialPackage + "]" );

        try {

            PackageItem pkg = repository.loadPackage( initialPackage );
            AssetItem asset = pkg.addAsset( ruleName,
                                            description,
                                            initialCategory,
                                            format );

            applyPreBuiltTemplates( ruleName,
                                    format,
                                    asset );
            repository.save();

            return asset.getUUID();
        } catch ( RulesRepositoryException e ) {
            if ( e.getCause() instanceof ItemExistsException ) {
                return "DUPLICATE";
            } else {
                log.error( e );
                throw new SerializableException( e.getMessage() );
            }
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

        AssetItem asset = repository.loadAssetByUUID( uuid );
        asset.remove();
        repository.save();
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

        }
    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public PackageConfigData[] listPackages() {
        RepositoryFilter pf = new PackageFilter();
        return listPackages( false,
                             pf );
    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public PackageConfigData[] listArchivedPackages() {
        RepositoryFilter pf = new PackageFilter();
        return listPackages( true,
                             pf );
    }

    private PackageConfigData[] listPackages(boolean archive,
                                             RepositoryFilter filter) {
        List<PackageConfigData> result = new ArrayList<PackageConfigData>();
        PackageIterator pkgs = repository.listPackages();
        pkgs.setArchivedIterator( archive );
        while ( pkgs.hasNext() ) {
            PackageItem pkg = (PackageItem) pkgs.next();

            PackageConfigData data = new PackageConfigData();
            data.uuid = pkg.getUUID();
            data.name = pkg.getName();
            data.archived = pkg.isArchived();
            if ( !archive && (filter == null || filter.accept( data,
                                                               RoleTypes.PACKAGE_READONLY )) ) {
                result.add( data );
            } else if ( archive && data.archived && (filter == null || filter.accept( data,
                                                                                      RoleTypes.PACKAGE_READONLY )) ) {
                result.add( data );
            }
        }

        sortPackages( result );
        PackageConfigData[] resultArr = result.toArray( new PackageConfigData[result.size()] );
        return resultArr;
    }

    void sortPackages(List<PackageConfigData> result) {
        Collections.sort( result,
                          new Comparator<Object>() {

                              public int compare(final Object o1,
                                                 final Object o2) {
                                  final PackageConfigData d1 = (PackageConfigData) o1;
                                  final PackageConfigData d2 = (PackageConfigData) o2;
                                  return d1.name.compareTo( d2.name );
                              }

                          } );
    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    /**
     * loadRuleListForCategories
     *
     * Role-based Authorization check: This method can be accessed if user has
     * following permissions:
     * 1. The user has Analyst role and this role has permission to access the category
     * Or.
     * 2. The user has one of the following roles: package.readonly|package.admin|package.developer.
     * In this case, this method only returns assets that belong to packages the role has at least
     * package.readonly permission to access.
     */
    public TableDataResult loadRuleListForCategories(String categoryPath,
                                                     int skip,
                                                     int numRows,
                                                     String tableConfig) throws SerializableException {
        // love you
        // long time = System.currentTimeMillis();

        // First check the user has permission to access this categoryPath.
        if ( Contexts.isSessionContextActive() ) {
            if ( !Identity.instance().hasPermission( new CategoryPathType( categoryPath ),
                                                     RoleTypes.ANALYST_READ ) ) {

                TableDisplayHandler handler = new TableDisplayHandler( tableConfig );
                return handler.loadRuleListTable( new AssetPageList() );
            }
        }

        //use AssetItemFilter to enforce package-based permissions.
//        RepositoryFilter filter = new AssetItemFilter();
        // Filter is null since the permission is checked on category level.
        RepositoryFilter filter = null;
        AssetPageList list = repository.findAssetsByCategory( categoryPath,
                                                              false,
                                                              skip,
                                                              numRows,
                                                              filter );
        TableDisplayHandler handler = new TableDisplayHandler( tableConfig );
        // log.debug("time for load: " + (System.currentTimeMillis() - time) );
        return handler.loadRuleListTable( list );

    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public TableDataResult loadRuleListForState(String stateName,
                                                int skip,
                                                int numRows,
                                                String tableConfig) throws SerializableException {
        // love you
        // long time = System.currentTimeMillis();

        RepositoryFilter filter = new AssetItemFilter();
        AssetPageList list = repository.findAssetsByState( stateName,
                                                           false,
                                                           skip,
                                                           numRows,
                                                           filter );
        TableDisplayHandler handler = new TableDisplayHandler( tableConfig );
        // log.debug("time for load: " + (System.currentTimeMillis() - time) );
        return handler.loadRuleListTable( list );

    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public TableConfig loadTableConfig(String listName) {
        TableDisplayHandler handler = new TableDisplayHandler( listName );
        return handler.loadTableConfig();
    }

    /**
     * This actually does the hard work of loading up an asset based on its
     * format.
     *
     * Role-based Authorization check: This method can be accessed if user has
     * following permissions:
     * 1. The user has Analyst role and this role has permission to access the category
     * which the asset belongs to.
     * Or.
     * 2. The user has package.readonly role (or package.admin, package.developer)
     * and this role has permission to access the package which the asset belongs to.
     */
    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public RuleAsset loadRuleAsset(String uuid) throws SerializableException {
        AssetItem item = repository.loadAssetByUUID( uuid );
        RuleAsset asset = new RuleAsset();
        boolean hasRightsToEdit = true;

        asset.uuid = uuid;

        // load standard meta data
        asset.metaData = populateMetaData( item );

        if ( Contexts.isSessionContextActive() ) {
            Identity.instance().checkPermission( new PackageNameType( asset.metaData.packageName ),
                                                 RoleTypes.PACKAGE_READONLY );

            // TODO: What about package read only, does is it really read only?
            
            if ( asset.metaData.categories.length == 0 ) {
                Identity.instance().checkPermission( new CategoryPathType( null ),
                                                     RoleTypes.ANALYST_READ );
            } else {
                boolean passed = false;
                RuntimeException exception = null;

                for ( String cat : asset.metaData.categories ) {
                	// Check if user has a permission to read this asset.
                    try {
                        Identity.instance().checkPermission( new CategoryPathType( cat ),
                                                             RoleTypes.ANALYST_READ );
                        passed = true;
                    } catch ( RuntimeException e ) {
                        exception = e;
                    }
                    // Check if user has permission to edit this asset
                    try {
                    	Identity.instance().checkPermission( new CategoryPathType( cat ),
                    			RoleTypes.ANALYST );
                    } catch ( RuntimeException e ) {
                    	hasRightsToEdit = false;
                    }
                }
                if ( !passed ) {
                    throw exception;
                }
            }
        }

        // get package header

        //		PackageItem pkgItem = repository
        //				.loadPackage(asset.metaData.packageName);
        PackageItem pkgItem = item.getPackage();

        // load the content
        ContentHandler handler = ContentManager.getHandler( asset.metaData.format );
        handler.retrieveAssetContent( asset,
                                      pkgItem,
                                      item );
        if ( pkgItem.isSnapshot() || !hasRightsToEdit ) {
            asset.isreadonly = true;
        }
        return asset;

    }

    private RuleAsset loadAsset(AssetItem item) throws SerializableException {
        RuleAsset asset = new RuleAsset();
        asset.uuid = item.getUUID();
        // load standard meta data
        asset.metaData = populateMetaData( item );
        // get package header
        PackageItem pkgItem = item.getPackage();
        // load the content
        ContentHandler handler = ContentManager.getHandler( asset.metaData.format );
        handler.retrieveAssetContent( asset,
                                      pkgItem,
                                      item );
        return asset;
    }

    /**
     * read in the meta data, populating all dublin core and versioning stuff.
     */
    MetaData populateMetaData(VersionableItem item) {
        MetaData meta = new MetaData();

        meta.status = (item.getState() != null) ? item.getState().getName() : "";

        metaDataMapper.copyToMetaData( meta,
                                       item );

        meta.createdDate = calendarToDate( item.getCreatedDate() );
        meta.lastModifiedDate = calendarToDate( item.getLastModified() );

        return meta;
    }

    /**
     * Populate meta data with asset specific info.
     */
    MetaData populateMetaData(AssetItem item) {
        MetaData meta = populateMetaData( (VersionableItem) item );
        meta.packageName = item.getPackageName();

        List cats = item.getCategories();
        meta.categories = new String[cats.size()];
        for ( int i = 0; i < meta.categories.length; i++ ) {
            CategoryItem cat = (CategoryItem) cats.get( i );
            meta.categories[i] = cat.getFullPath();
        }
        meta.dateEffective = calendarToDate( item.getDateEffective() );
        meta.dateExpired = calendarToDate( item.getDateExpired() );
        return meta;

    }

    private Date calendarToDate(Calendar createdDate) {
        if ( createdDate == null ) return null;
        return createdDate.getTime();
    }

    private Calendar dateToCalendar(Date date) {
        if ( date == null ) return null;
        Calendar cal = Calendar.getInstance();
        cal.setTime( date );
        return cal;
    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    /**
     *
     * Role-based Authorization check: This method can be accessed if user has
     * following permissions:
     * 1. The user has Analyst role and this role has permission to access the category
     * which the asset belongs to.
     * Or.
     * 2. The user has package.readonly role (or package.admin, package.developer)
     * and this role has permission to access the package which the asset belongs to.
     */
    public String checkinVersion(RuleAsset asset) throws SerializableException {
        if ( Contexts.isSessionContextActive() ) {
            Identity.instance().checkPermission( new PackageNameType( asset.metaData.packageName ),
                                                 RoleTypes.PACKAGE_DEVELOPER );

            if ( asset.metaData.categories.length == 0 ) {
                Identity.instance().checkPermission( new CategoryPathType( null ),
                                                     RoleTypes.ANALYST );
            } else {
                boolean passed = false;
                RuntimeException exception = null;

                for ( String cat : asset.metaData.categories ) {
                    try {
                        Identity.instance().checkPermission( new CategoryPathType( cat ),
                                                             RoleTypes.ANALYST );
                        passed = true;
                    } catch ( RuntimeException e ) {
                        exception = e;
                    }
                }
                if ( !passed ) {
                    throw exception;
                }
            }
        }

        log.info( "USER:" + repository.getSession().getUserID() + " CHECKING IN asset: [" + asset.metaData.name + "] UUID: [" + asset.uuid + "]  ARCHIVED [" + asset.archived + "]" );

        AssetItem repoAsset = repository.loadAssetByUUID( asset.uuid );
        if ( asset.metaData.lastModifiedDate.before( repoAsset.getLastModified().getTime() ) ) {
            return "ERR: Unable to save this asset, as it has been recently updated by [" + repoAsset.getLastContributor() + "]";
        }

        repoAsset.archiveItem( asset.archived );
        MetaData meta = asset.metaData;

        metaDataMapper.copyFromMetaData( meta,
                                         repoAsset );

        repoAsset.updateDateEffective( dateToCalendar( meta.dateEffective ) );
        repoAsset.updateDateExpired( dateToCalendar( meta.dateExpired ) );

        repoAsset.updateCategoryList( meta.categories );
        ContentHandler handler = ContentManager.getHandler( repoAsset.getFormat() );// new AssetContentFormatHandler();
        handler.storeAssetContent( asset,
                                   repoAsset );

        if ( !(asset.metaData.format.equals( AssetFormats.TEST_SCENARIO )) || asset.metaData.format.equals( AssetFormats.ENUMERATION ) ) {
            PackageItem pkg = repoAsset.getPackage();
            pkg.updateBinaryUpToDate( false );
            this.ruleBaseCache.remove( pkg.getUUID() );

        }

        repoAsset.checkin( meta.checkinComment );

        return repoAsset.getUUID();
    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public TableDataResult loadAssetHistory(String uuid) throws SerializableException {

        List<TableDataRow> result = new ArrayList<TableDataRow>();

        AssetItem item = repository.loadAssetByUUID( uuid );

        if ( Contexts.isSessionContextActive() ) {
            Identity.instance().checkPermission( new PackageUUIDType( item.getPackage().getUUID() ),
                                                 RoleTypes.PACKAGE_READONLY );
        }

        AssetHistoryIterator it = item.getHistory();

        // MN Note: this uses the lazy iterator, but then loads the whole lot
        // up, and returns it.
        // The reason for this is that the GUI needs to show things in numeric
        // order by the version number.
        // When a version is restored, its previous version is NOT what you
        // thought it was - due to how JCR works
        // (its more like CVS then SVN). So to get a linear progression of
        // versions, we use the incrementing version number,
        // and load it all up and sort it. This is not ideal.
        // In future, we may do a "restore" instead just by copying content into
        // a new version, not restoring a node,
        // in which case the iterator will be in order (or you can just walk all
        // the way back).
        // So if there are performance problems with looking at lots of
        // historical versions, look at this nasty bit of code.
        while ( it.hasNext() ) {
            AssetItem historical = (AssetItem) it.next();// new
            // AssetItem(repo,
            // element);
            long versionNumber = historical.getVersionNumber();
            if ( !(versionNumber == 0) && !(versionNumber == item.getVersionNumber()) ) {
                TableDataRow row = new TableDataRow();
                row.id = historical.getVersionSnapshotUUID();
                row.values = new String[4];
                row.values[0] = Long.toString( historical.getVersionNumber() );
                row.values[1] = historical.getCheckinComment();
                row.values[2] = dateFormatter.format( historical.getLastModified().getTime() );
                row.values[3] = historical.getStateDescription();
                result.add( row );
            }
        }

        if ( result.size() == 0 ) return null;
        TableDataResult table = new TableDataResult();
        table.data = result.toArray( new TableDataRow[result.size()] );

        return table;
    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public TableDataResult loadArchivedAssets(int skip,
                                              int numRows) throws SerializableException {
        List<TableDataRow> result = new ArrayList<TableDataRow>();
        RepositoryFilter filter = new AssetItemFilter();

        AssetItemIterator it = repository.findArchivedAssets();
        it.skip( skip );
        int count = 0;
        while ( it.hasNext() ) {

            AssetItem archived = (AssetItem) it.next();

            if ( filter.accept( archived,
                                "read" ) ) {
                TableDataRow row = new TableDataRow();
                row.id = archived.getUUID();
                row.values = new String[5];

                row.values[0] = archived.getFormat();
                row.values[1] = archived.getPackageName();
                row.values[2] = archived.getName();
                row.values[3] = archived.getLastContributor();
                row.values[4] = archived.getLastModified().getTime().toLocaleString();

                result.add( row );
                count++;
            }
            if ( count == numRows ) {
                break;
            }
        }

        TableDataResult table = new TableDataResult();
        table.data = result.toArray( new TableDataRow[result.size()] );
        table.currentPosition = it.getPosition();
        return table;
    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public void restoreVersion(String versionUUID,
                               String assetUUID,
                               String comment) {
        AssetItem old = repository.loadAssetByUUID( versionUUID );
        AssetItem head = repository.loadAssetByUUID( assetUUID );

        log.info( "USER:" + repository.getSession().getUserID() + " RESTORE of asset: [" + head.getName() + "] UUID: [" + head.getUUID() + "] with historical version number: [" + old.getVersionNumber() );

        repository.restoreHistoricalAsset( old,
                                           head,
                                           comment );

    }

    @WebRemote
    public String createPackage(String name,
                                String description) throws SerializableException {
        if ( Contexts.isSessionContextActive() ) {
            Identity.instance().checkPermission( new AdminType(),
                                                 RoleTypes.ADMIN );
        }

        log.info( "USER:" + repository.getSession().getUserID() + " CREATING package [" + name + "]" );
        PackageItem item = repository.createPackage( name,
                                                     description );

        return item.getUUID();
    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public PackageConfigData loadPackageConfig(String uuid) {
        PackageItem item = repository.loadPackageByUUID( uuid );
        // the uuid passed in is the uuid of that deployment bundle, not the
        // package uudi.
        // we have to figure out the package name.
        if ( Contexts.isSessionContextActive() ) {
            Identity.instance().checkPermission( new PackageNameType( item.getName() ),
                                                 RoleTypes.PACKAGE_READONLY );
        }

        PackageConfigData data = new PackageConfigData();
        data.uuid = item.getUUID();
        data.header = getDroolsHeader( item );
        data.externalURI = item.getExternalURI();
        data.catRules = item.getCategoryRules();
        //System.out.println("Cat Rules: " + data.catRules.toString());
        data.description = item.getDescription();
        data.name = item.getName();
        data.lastModified = item.getLastModified().getTime();
        data.dateCreated = item.getCreatedDate().getTime();
        data.checkinComment = item.getCheckinComment();
        data.lasContributor = item.getLastContributor();
        data.state = item.getStateDescription();
        data.isSnapshot = item.isSnapshot();
        if ( data.isSnapshot ) {
            data.snapshotName = item.getSnapshotName();
        }
        return data;
    }

    //make sure this stays the same order
    private static String[] convertToObjectGraph(final Map map,
                                                 boolean getKeys) {
        List list = new ArrayList();

        for ( Iterator i = map.entrySet().iterator(); i.hasNext(); ) {
            Map.Entry entry = (Map.Entry) i.next();

            if ( getKeys ) {
                list.add( entry.getKey() );
            } else {
                list.add( entry.getValue() );
            }
        }
        //System.out.println("(convertToObjectGraph)list: " + list.toString());
        return (String[]) list.toArray( new String[0] );
    }

    private static String convertMapToString(final Map map,
                                             boolean getKeys) {
        //System.out.println("(convertMapToString)map: " + map.toString());
        String[] sArray = convertToObjectGraph( map,
                                                getKeys );
        String returnVal = new String();
        for ( String string : sArray ) {
            if ( returnVal.length() > 0 ) {
                returnVal += ",";
            }
            returnVal += string;
        }
        //System.out.println("(convertMapToString)returnVal: " + returnVal);
        return returnVal;
    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public ValidatedResponse savePackage(PackageConfigData data) throws SerializableException {
        if ( Contexts.isSessionContextActive() ) {
            Identity.instance().checkPermission( new PackageUUIDType( data.uuid ),
                                                 RoleTypes.PACKAGE_DEVELOPER );
        }

        log.info( "USER:" + repository.getSession().getUserID() + " SAVING package [" + data.name + "]" );

        PackageItem item = repository.loadPackage( data.name );

        updateDroolsHeader( data.header,
                            item );
        item.updateCategoryRules( convertMapToString( data.catRules,
                                                      true ),
                                  convertMapToString( data.catRules,
                                                      false ) );

        item.updateExternalURI( data.externalURI );
        item.updateDescription( data.description );
        item.archiveItem( data.archived );
        item.updateBinaryUpToDate( false );
        this.ruleBaseCache.remove( data.uuid );
        item.checkin( data.description );

        BRMSSuggestionCompletionLoader loader = new BRMSSuggestionCompletionLoader();
        loader.getSuggestionEngine( item );

        ValidatedResponse res = new ValidatedResponse();
        if ( loader.hasErrors() ) {
            res.hasErrors = true;
            String err = "";
            for ( Iterator iter = loader.getErrors().iterator(); iter.hasNext(); ) {
                err += (String) iter.next();
                if ( iter.hasNext() ) err += "\n";
            }
            res.errorHeader = "Package validation errors";
            res.errorMessage = err;
        }

        return res;
    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public TableDataResult listAssets(String uuid,
                                      String formats[],
                                      int skip,
                                      int numRows,
                                      String tableConfig) throws SerializableException {
        // TODO: This does not work for package snapshot. package snspshot's
        // UUID is different
        // from its corresponding package. However we seem to expect to get same
        // assets using the
        // package snapshot UUID here
        // Identity.instance().checkPermission("ignoredanyway", "read", uuid);

        if ( numRows == 0 ) {
            throw new DetailedSerializableException( "Unable to return zero results (bug)",
                                                     "probably have the parameters around the wrong way, sigh..." );
        }
        long start = System.currentTimeMillis();
        PackageItem pkg = repository.loadPackageByUUID( uuid );
        AssetItemIterator it;
        if ( formats.length > 0 ) {
            it = pkg.listAssetsByFormat( formats );
        } else {
            it = pkg.listAssetsNotOfFormat( AssetFormatHelper.listRegisteredTypes() );
        }
        TableDisplayHandler handler = new TableDisplayHandler( tableConfig );
        log.debug( "time for asset list load: " + (System.currentTimeMillis() - start) );
        return handler.loadRuleListTable( it,
                                          skip,
                                          numRows );
    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public TableDataResult queryFullText(String text,
                                         boolean seekArchived,
                                         int skip,
                                         int numRows) throws SerializableException {
        if ( numRows == 0 ) {
            throw new DetailedSerializableException( "Unable to return zero results (bug)",
                                                     "probably have the parameters around the wrong way, sigh..." );
        }
        AssetItemIterator it = repository.queryFullText( text,
                                                         seekArchived );

        // Add filter for READONLY permission
        List<AssetItem> resultList = new ArrayList<AssetItem>();
        RepositoryFilter filter = new PackageFilter();

        while ( it.hasNext() ) {
            AssetItem ai = it.next();
            if ( checkPackagePermissionHelper( filter,
                                               ai,
                                               RoleTypes.PACKAGE_READONLY ) ) {
                resultList.add( ai );
            }
        }

        TableDisplayHandler handler = new TableDisplayHandler( "searchresults" );
        return handler.loadRuleListTable( resultList,
                                          skip,
                                          numRows );
    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public TableDataResult queryMetaData(final MetaDataQuery[] qr,
                                         Date createdAfter,
                                         Date createdBefore,
                                         Date modifiedAfter,
                                         Date modifiedBefore,
                                         boolean seekArchived,
                                         int skip,
                                         int numRows) throws SerializableException {
        if ( numRows == 0 ) {
            throw new DetailedSerializableException( "Unable to return zero results (bug)",
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
        AssetItemIterator it = repository.query( q,
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

        TableDisplayHandler handler = new TableDisplayHandler( "searchresults" );
        return handler.loadRuleListTable( resultList,
                                          skip,
                                          numRows );
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

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public String createState(String name) throws SerializableException {
        log.info( "USER:" + repository.getSession().getUserID() + " CREATING state: [" + name + "]" );
        try {
            String uuid = repository.createState( name ).getNode().getUUID();
            repository.save();
            return uuid;
        } catch ( RepositoryException e ) {
            throw new SerializableException( "Unable to create the status." );
        }
    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public void removeState(String name) throws SerializableException {
        log.info( "USER:" + repository.getSession().getUserID() + " REMOVING state: [" + name + "]" );

        try {
            repository.loadState( name ).remove();
            repository.save();

        } catch ( RulesRepositoryException e ) {
            throw new DetailedSerializableException( "Unable to remove status. It is probably still used (even by archived items).",
                                                     e.getMessage() );
        }
    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public void renameState(String oldName,
                            String newName) throws SerializableException {
        log.info( "USER:" + repository.getSession().getUserID() + " RENAMING state: [" + oldName + "] to [" + newName + "]" );
        repository.renameState( oldName,
                                newName );

    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public String[] listStates() throws SerializableException {
        StateItem[] states = repository.listStates();
        String[] result = new String[states.length];
        for ( int i = 0; i < states.length; i++ ) {
            result[i] = states[i].getName();
        }
        return result;
    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public void changeState(String uuid,
                            String newState,
                            boolean wholePackage) {

        if ( !wholePackage ) {

            AssetItem asset = repository.loadAssetByUUID( uuid );
            log.info( "USER:" + repository.getSession().getUserID() + " CHANGING ASSET STATUS. Asset name, uuid: " + "[" + asset.getName() + ", " + asset.getUUID() + "]" + " to [" + newState + "]" );

            if ( Contexts.isSessionContextActive() ) {
                Identity.instance().checkPermission( new PackageUUIDType( asset.getPackage().getUUID() ),
                                                     RoleTypes.PACKAGE_DEVELOPER );
            }

            asset.updateState( newState );
        } else {
            if ( Contexts.isSessionContextActive() ) {
                Identity.instance().checkPermission( new PackageUUIDType( uuid ),
                                                     RoleTypes.PACKAGE_DEVELOPER );
            }

            PackageItem pkg = repository.loadPackageByUUID( uuid );
            log.info( "USER:" + repository.getSession().getUserID() + " CHANGING Package STATUS. Asset name, uuid: " + "[" + pkg.getName() + ", " + pkg.getUUID() + "]" + " to [" + newState + "]" );
            pkg.changeStatus( newState );
        }
        repository.save();
    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public void changeAssetPackage(String uuid,
                                   String newPackage,
                                   String comment) {
        if ( Contexts.isSessionContextActive() ) {
            Identity.instance().checkPermission( new PackageNameType( newPackage ),
                                                 RoleTypes.PACKAGE_DEVELOPER );
        }

        log.info( "USER:" + repository.getSession().getUserID() + " CHANGING PACKAGE OF asset: [" + uuid + "] to [" + newPackage + "]" );
        repository.moveRuleItemPackage( newPackage,
                                        uuid,
                                        comment );

    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public String copyAsset(String assetUUID,
                            String newPackage,
                            String newName) {
        if ( Contexts.isSessionContextActive() ) {
            Identity.instance().checkPermission( new PackageNameType( newPackage ),
                                                 RoleTypes.PACKAGE_DEVELOPER );
        }

        return repository.copyAsset( assetUUID,
                                     newPackage,
                                     newName );
    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public SnapshotInfo[] listSnapshots(String packageName) {
        if ( Contexts.isSessionContextActive() ) {
            Identity.instance().checkPermission( new PackageNameType( packageName ),
                                                 RoleTypes.PACKAGE_DEVELOPER );
        }

        String[] snaps = repository.listPackageSnapshots( packageName );
        SnapshotInfo[] res = new SnapshotInfo[snaps.length];
        for ( int i = 0; i < snaps.length; i++ ) {
            PackageItem snap = repository.loadPackageSnapshot( packageName,
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
    @Restrict("#{identity.loggedIn}")
    public void createPackageSnapshot(String packageName,
                                      String snapshotName,
                                      boolean replaceExisting,
                                      String comment) {
        if ( Contexts.isSessionContextActive() ) {
            Identity.instance().checkPermission( new PackageNameType( packageName ),
                                                 RoleTypes.PACKAGE_ADMIN );
        }

        log.info( "USER:" + repository.getSession().getUserID() + " CREATING PACKAGE SNAPSHOT for package: [" + packageName + "] snapshot name: [" + snapshotName );

        if ( replaceExisting ) {
            repository.removePackageSnapshot( packageName,
                                              snapshotName );
        }

        repository.createPackageSnapshot( packageName,
                                          snapshotName );
        PackageItem item = repository.loadPackageSnapshot( packageName,
                                                           snapshotName );
        item.updateCheckinComment( comment );
        repository.save();

    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public void copyOrRemoveSnapshot(String packageName,
                                     String snapshotName,
                                     boolean delete,
                                     String newSnapshotName) throws SerializableException {
        if ( Contexts.isSessionContextActive() ) {
            Identity.instance().checkPermission( new PackageNameType( packageName ),
                                                 RoleTypes.PACKAGE_ADMIN );
        }

        if ( delete ) {
            log.info( "USER:" + repository.getSession().getUserID() + " REMOVING SNAPSHOT for package: [" + packageName + "] snapshot: [" + snapshotName + "]" );
            repository.removePackageSnapshot( packageName,
                                              snapshotName );
        } else {
            if ( newSnapshotName.equals( "" ) ) {
                throw new SerializableException( "Need to have a new snapshot name." );
            }
            log.info( "USER:" + repository.getSession().getUserID() + " COPYING SNAPSHOT for package: [" + packageName + "] snapshot: [" + snapshotName + "] to [" + newSnapshotName + "]" );

            repository.copyPackageSnapshot( packageName,
                                            snapshotName,
                                            newSnapshotName );
        }

    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public TableDataResult quickFindAsset(String searchText,
                                          int max,
                                          boolean searchArchived) {

        String search = searchText.replace('*', '%');

        if ( !search.endsWith( "%" ) ) {
            search += "%";
        }

        TableDataResult result = new TableDataResult();

        List<TableDataRow> resultList = new ArrayList<TableDataRow>();

        long start = System.currentTimeMillis();
        AssetItemIterator it = repository.findAssetsByName( search,
                                                            searchArchived ); // search for archived items
        log.debug( "Search time: " + (System.currentTimeMillis() - start) );

        RepositoryFilter filter = new AssetItemFilter();
        for ( int i = 0; i < max; i++ ) {
            if ( !it.hasNext() ) {
                break;
            }
            AssetItem item = (AssetItem) it.next();
            try {
                System.err.println( "jcr:path=" + item.getNode().getPath() );
            } catch ( RepositoryException e ) {
                e.printStackTrace();
            }
            if ( filter.accept( item,
                                RoleTypes.PACKAGE_READONLY ) ) {
                TableDataRow row = new TableDataRow();
                row.id = item.getUUID();
                String desc = item.getDescription() + "";
                row.values = new String[]{item.getName(), desc.substring( 0,
                                                                          Math.min( 32,
                                                                                    desc.length() ) )};

                resultList.add( row );
            }
        }

        while ( it.hasNext() ) {
            if ( filter.accept( (AssetItem) it.next(),
                                RoleTypes.PACKAGE_READONLY ) ) {
                TableDataRow empty = new TableDataRow();
                empty.id = "MORE";
                resultList.add( empty );
                break;
            }
        }

        result.data = resultList.toArray( new TableDataRow[resultList.size()] );
        return result;

    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public void removeCategory(String categoryPath) throws SerializableException {
        log.info( "USER:" + repository.getSession().getUserID() + " REMOVING CATEGORY path: [" + categoryPath + "]" );

        try {
            repository.loadCategory( categoryPath ).remove();
            repository.save();
        } catch ( RulesRepositoryException e ) {
            throw new DetailedSerializableException( "Unable to remove category. It is probably still used (even by archived items).",
                                                     e.getMessage() );
        }
    }

    @WebRemote
    public void clearRulesRepository() {
        if ( Contexts.isSessionContextActive() ) {
            Identity.instance().checkPermission( new AdminType(),
                                                 RoleTypes.ADMIN );
        }

        RulesRepositoryAdministrator admin = new RulesRepositoryAdministrator( repository.getSession() );
        admin.clearRulesRepository();
    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public SuggestionCompletionEngine loadSuggestionCompletionEngine(String packageName) throws SerializableException {
        if ( Contexts.isSessionContextActive() ) {
            Identity.instance().checkPermission( new PackageNameType( packageName ),
                                                 RoleTypes.PACKAGE_READONLY );
        }
        try {

            PackageItem pkg = repository.loadPackage( packageName );
            BRMSSuggestionCompletionLoader loader = new BRMSSuggestionCompletionLoader();
            return loader.getSuggestionEngine( pkg );
        } catch ( RulesRepositoryException e ) {
            log.error( e );
            throw new SerializableException( e.getMessage() );
        }

    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public BuilderResult[] buildPackage(String packageUUID,
                                        String selectorConfigName,
                                        boolean force) throws SerializableException {
        if ( Contexts.isSessionContextActive() ) {
            Identity.instance().checkPermission( new PackageUUIDType( packageUUID ),
                                                 RoleTypes.PACKAGE_DEVELOPER );
        }
        PackageItem item = repository.loadPackageByUUID( packageUUID );
        try {
            return buildPackage( selectorConfigName,
                                 force,
                                 item );
        } catch ( NoClassDefFoundError e ) {
            throw new DetailedSerializableException( "Unable to find a class that was needed when building the package  [" + e.getMessage() + "]",
                                                     "Perhaps you are missing them from the model jars, or from the BRMS itself (lib directory)." );
        }
    }

    private BuilderResult[] buildPackage(String selectorConfigName,
                                         boolean force,
                                         PackageItem item) throws DetailedSerializableException {
        if ( !force && item.isBinaryUpToDate() ) {
            // we can just return all OK if its up to date.
            return null;
        }
        ContentPackageAssembler asm = new ContentPackageAssembler( item,
                                                                   selectorConfigName );
        if ( asm.hasErrors() ) {
            BuilderResult[] result = generateBuilderResults( asm );
            return result;
        } else {
            try {
                ByteArrayOutputStream bout = new ByteArrayOutputStream();
                ObjectOutput out = new DroolsObjectOutputStream( bout );
                out.writeObject( asm.getBinaryPackage() );

                item.updateCompiledPackage( new ByteArrayInputStream( bout.toByteArray() ) );
                out.flush();
                out.close();

                updateBinaryPackage( item,
                                     asm );
                repository.save();
            } catch ( Exception e ) {
                e.printStackTrace();
                log.error( e );
                throw new DetailedSerializableException( "An error occurred building the package.",
                                                         e.getMessage() );
            }

            return null;

        }
    }

    private void updateBinaryPackage(PackageItem item,
                                     ContentPackageAssembler asm) throws Exception {
        item.updateBinaryUpToDate( true );
        RuleBaseConfiguration conf = new RuleBaseConfiguration();
        // setting the MapBackedClassloader that is the parent of the builder classloader as the parent
        // of the rulebase classloader
        conf.setClassLoader( asm.getBuilder().getRootClassLoader().getParent() );
        RuleBase rb = RuleBaseFactory.newRuleBase( conf );
        rb.addPackage( asm.getBinaryPackage() );
        // this.ruleBaseCache.put(item.getUUID(), rb);
    }

    private BuilderResult[] generateBuilderResults(ContentPackageAssembler asm) {
        BuilderResult[] result = new BuilderResult[asm.getErrors().size()];
        for ( int i = 0; i < result.length; i++ ) {
            ContentAssemblyError err = asm.getErrors().get( i );
            BuilderResult res = new BuilderResult();
            res.assetName = err.itemInError.getName();
            res.assetFormat = err.itemInError.getFormat();
            res.message = err.errorReport;
            res.uuid = err.itemInError.getUUID();
            result[i] = res;
        }
        return result;
    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public String buildPackageSource(String packageUUID) throws SerializableException {
        if ( Contexts.isSessionContextActive() ) {
            Identity.instance().checkPermission( new PackageUUIDType( packageUUID ),
                                                 RoleTypes.PACKAGE_DEVELOPER );
        }

        PackageItem item = repository.loadPackageByUUID( packageUUID );
        ContentPackageAssembler asm = new ContentPackageAssembler( item,
                                                                   false );
        return asm.getDRL();
    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public String buildAssetSource(RuleAsset asset) throws SerializableException {
        if ( Contexts.isSessionContextActive() ) {
            Identity.instance().checkPermission( new PackageNameType( asset.metaData.packageName ),
                                                 RoleTypes.PACKAGE_DEVELOPER );
        }

        AssetItem item = repository.loadAssetByUUID( asset.uuid );

        ContentHandler handler = ContentManager.getHandler( item.getFormat() );// new
        // AssetContentFormatHandler();
        handler.storeAssetContent( asset,
                                   item );
        StringBuffer buf = new StringBuffer();
        if ( handler.isRuleAsset() ) {

            BRMSPackageBuilder builder = new BRMSPackageBuilder( new PackageBuilderConfiguration() );
            // now we load up the DSL files
            builder.setDSLFiles( BRMSPackageBuilder.getDSLMappingFiles( item.getPackage(),
                                                                        new BRMSPackageBuilder.DSLErrorEvent() {
                                                                            public void recordError(AssetItem asset,
                                                                                                    String message) {
                                                                                // ignore at this point...
                                                                            }
                                                                        } ) );
            ((IRuleAsset) handler).assembleDRL( builder,
                                                item,
                                                buf );
        } else {
            return item.getContent();
        }

        return buf.toString();
    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public BuilderResult[] buildAsset(RuleAsset asset) throws SerializableException {
        if ( Contexts.isSessionContextActive() ) {
            Identity.instance().checkPermission( new PackageNameType( asset.metaData.packageName ),
                                                 RoleTypes.PACKAGE_DEVELOPER );
        }

        try {

            AssetItem item = repository.loadAssetByUUID( asset.uuid );

            ContentHandler handler = ContentManager.getHandler( item.getFormat() );// new
            // AssetContentFormatHandler();
            handler.storeAssetContent( asset,
                                       item );

            if ( handler instanceof IValidating ) {
                return ((IValidating) handler).validateAsset( item );
            } else {

                ContentPackageAssembler asm = new ContentPackageAssembler( item );
                if ( !asm.hasErrors() ) {
                    return null;
                } else {
                    return generateBuilderResults( asm );
                }
            }
        } catch ( Exception e ) {
            log.error( e );
            BuilderResult[] result = new BuilderResult[1];

            BuilderResult res = new BuilderResult();
            res.assetName = asset.metaData.name;
            res.assetFormat = asset.metaData.format;
            res.message = "Unable to validate this asset. (Check log for detailed messages).";
            res.uuid = asset.uuid;
            result[0] = res;

            return result;
        }

    }

    @WebRemote
    public void copyPackage(String sourcePackageName,
                            String destPackageName) throws SerializableException {
        if ( Contexts.isSessionContextActive() ) {
            Identity.instance().checkPermission( new AdminType(),
                                                 RoleTypes.ADMIN );
        }

        try {
            repository.copyPackage( sourcePackageName,
                                    destPackageName );
        } catch ( RulesRepositoryException e ) {
            log.error( e );
            throw e;
        }

        // If we allow package owner to copy package, we will have to update the
        // permission store
        // for the newly copied package.
        // Update permission store
        /*
         * String copiedUuid = ""; try { PackageItem source =
         * repository.loadPackage( destPackageName ); copiedUuid =
         * source.getUUID(); } catch (RulesRepositoryException e) { log.error( e ); }
         * PackageBasedPermissionStore pbps = new PackageBasedPermissionStore();
         * pbps.addPackageBasedPermission(new PackageBasedPermission(copiedUuid,
         * Identity.instance().getPrincipal().getName(),
         * RoleTypes.PACKAGE_ADMIN));
         */
    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public String renameAsset(String uuid,
                              String newName) {
        AssetItem item = repository.loadAssetByUUID( uuid );
        if ( Contexts.isSessionContextActive() ) {
            Identity.instance().checkPermission( new PackageUUIDType( item.getPackage().getUUID() ),
                                                 RoleTypes.PACKAGE_DEVELOPER );
        }

        return repository.renameAsset( uuid,
                                       newName );
    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public void archiveAsset(String uuid,
                             boolean value) {
        try {
            AssetItem item = repository.loadAssetByUUID( uuid );

            if ( Contexts.isSessionContextActive() ) {
                Identity.instance().checkPermission( new PackageUUIDType( item.getPackage().getUUID() ),
                                                     RoleTypes.PACKAGE_DEVELOPER );
            }

            item.archiveItem( value );
            PackageItem pkg = item.getPackage();
            pkg.updateBinaryUpToDate( false );
            this.ruleBaseCache.remove( pkg.getUUID() );
            item.checkin( "unarchived" );

        } catch ( RulesRepositoryException e ) {
            log.error( e );
            throw e;
        }
    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public void removeAsset(String uuid) {
        try {
            AssetItem item = repository.loadAssetByUUID( uuid );
            if ( Contexts.isSessionContextActive() ) {
                Identity.instance().checkPermission( new PackageUUIDType( item.getPackage().getUUID() ),
                                                     RoleTypes.PACKAGE_DEVELOPER );
            }

            item.remove();
            repository.save();
        } catch ( RulesRepositoryException e ) {
            log.error( e );
            throw e;
        }
    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public void removePackage(String uuid) {
        if ( Contexts.isSessionContextActive() ) {
            Identity.instance().checkPermission( new PackageUUIDType( uuid ),
                                                 RoleTypes.PACKAGE_ADMIN );
        }
        try {
            PackageItem item = repository.loadPackageByUUID( uuid );
            item.remove();
            repository.save();
        } catch ( RulesRepositoryException e ) {
            log.error( e );
            throw e;
        }
    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public String renamePackage(String uuid,
                                String newName) {
        if ( Contexts.isSessionContextActive() ) {
            Identity.instance().checkPermission( new PackageUUIDType( uuid ),
                                                 RoleTypes.PACKAGE_ADMIN );
        }

        return repository.renamePackage( uuid,
                                         newName );
    }

    @WebRemote
    public void rebuildSnapshots() throws SerializableException {
        if ( Contexts.isSessionContextActive() ) {
            Identity.instance().checkPermission( new AdminType(),
                                                 RoleTypes.ADMIN );
        }

        Iterator pkit = repository.listPackages();
        while ( pkit.hasNext() ) {
            PackageItem pkg = (PackageItem) pkit.next();
            String[] snaps = repository.listPackageSnapshots( pkg.getName() );
            for ( String snapName : snaps ) {
                PackageItem snap = repository.loadPackageSnapshot( pkg.getName(),
                                                                   snapName );
                BuilderResult[] res = this.buildPackage( snap.getUUID(),
                                                         "",
                                                         true );
                if ( res != null ) {
                    StringBuffer buf = new StringBuffer();
                    for ( int i = 0; i < res.length; i++ ) {
                        buf.append( res[i].toString() );
                        buf.append( '\n' );
                    }
                    throw new DetailedSerializableException( "Unable to rebuild snapshot [" + snapName,
                                                             buf.toString() + "]" );
                }
            }
        }
    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public String[] listRulesInPackage(String packageName) throws SerializableException {

    	// check security
        if ( Contexts.isSessionContextActive() ) {
            Identity.instance().checkPermission( new PackageNameType( packageName ),
                                                 RoleTypes.PACKAGE_READONLY );
        }

        // load package
        PackageItem item = repository.loadPackage( packageName );

        ContentPackageAssembler asm = new ContentPackageAssembler( item,
                                                                   false );
        List<String> result = new ArrayList<String>();
        DrlParser p = new DrlParser();
        try {
            PackageDescr pkg = p.parse( asm.getDRL() );
            int count = 0;
            if ( pkg != null ) {
                for ( Iterator iterator = pkg.getRules().iterator(); iterator.hasNext(); ) {
                    RuleDescr r = (RuleDescr) iterator.next();
                    result.add( r.getName() );
                    count++;
					if (count == MAX_RULES_TO_SHOW_IN_PACKAGE_LIST) {
						result.add("More then " + MAX_RULES_TO_SHOW_IN_PACKAGE_LIST + " rules.");
                        break;
                    }
                }
            }
            return result.toArray( new String[result.size()] );
        } catch ( DroolsParserException e ) {
            log.error( e );
            return new String[0];
        }
    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public SingleScenarioResult runScenario(String packageName,
                                            Scenario scenario) throws SerializableException {
        if ( Contexts.isSessionContextActive() ) {
            Identity.instance().checkPermission( new PackageNameType( packageName ),
                                                 RoleTypes.PACKAGE_DEVELOPER );
        }

        return runScenario( packageName,
                            scenario,
                            null );

    }

    private SingleScenarioResult runScenario(String packageName,
                                             Scenario scenario,
                                             RuleCoverageListener coverage) throws SerializableException {
        PackageItem item = this.repository.loadPackage( packageName );

        // nasty classloader needed to make sure we use the same tree the whole
        // time.
        ClassLoader originalCL = Thread.currentThread().getContextClassLoader();

        final RuleBase rb;

        try {
            if ( item.isBinaryUpToDate() && this.ruleBaseCache.containsKey( item.getUUID() ) ) {
                rb = this.ruleBaseCache.get( item.getUUID() );
            } else {
                // load up the classloader we are going to use
                List<JarInputStream> jars = BRMSPackageBuilder.getJars( item );
                ClassLoader buildCl = BRMSPackageBuilder.createClassLoader( jars );

                // we have to build the package, and try again.
                if ( item.isBinaryUpToDate() ) {
                    rb = loadRuleBase( item,
                                       buildCl );
                    this.ruleBaseCache.put( item.getUUID(),
                                            rb );
                } else {
                    BuilderResult[] errs = this.buildPackage( null,
                                                              false,
                                                              item );
                    if ( errs == null || errs.length == 0 ) {
                        rb = loadRuleBase( item,
                                           buildCl );
                        this.ruleBaseCache.put( item.getUUID(),
                                                rb );
                    } else {
                        SingleScenarioResult r = new SingleScenarioResult();
                        r.result = new ScenarioRunResult( errs,
                                                          null );
                        return r;
                    }
                }
            }

            ClassLoader cl = ((InternalRuleBase) this.ruleBaseCache.get( item.getUUID() )).getRootClassLoader();
            Thread.currentThread().setContextClassLoader( cl );
            return runScenario( scenario,
                                item,
                                cl,
                                rb,
                                coverage );

        } finally {
            Thread.currentThread().setContextClassLoader( originalCL );
        }

    }

    private RuleBase loadRuleBase(PackageItem item,
                                  ClassLoader cl) throws DetailedSerializableException {
        try {
            RuleBase rb = RuleBaseFactory.newRuleBase( new RuleBaseConfiguration( cl ) );
            Package bin = (Package) DroolsStreamUtils.streamIn( item.getCompiledPackageBytes(),
                                                                cl );
            rb.addPackage( bin );
            return rb;
        } catch ( ClassNotFoundException e ) {
            log.error( e );
            throw new DetailedSerializableException( "A required class was not found.",
                                                     e.getMessage() );
        } catch ( Exception e ) {
            log.error( e );
            throw new DetailedSerializableException( "Unable to load a rulebase.",
                                                     e.getMessage() );
        }
    }

    private SingleScenarioResult runScenario(Scenario scenario,
                                             PackageItem item,
                                             ClassLoader cl,
                                             RuleBase rb,
                                             RuleCoverageListener coverage) throws DetailedSerializableException {

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
        sessionConfiguration.setKeepReference( false );
        InternalWorkingMemory workingMemory = (InternalWorkingMemory) rb.newStatefulSession( sessionConfiguration );
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
            log.error( e );
            throw new DetailedSerializableException( "Unable to load a required class.",
                                                     e.getMessage() );
        } finally {

        }
    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public BulkTestRunResult runScenariosInPackage(String packageUUID) throws SerializableException {
        if ( Contexts.isSessionContextActive() ) {
            Identity.instance().checkPermission( new PackageUUIDType( packageUUID ),
                                                 RoleTypes.PACKAGE_DEVELOPER );
        }

        PackageItem item = repository.loadPackageByUUID( packageUUID );

        ClassLoader originalCL = Thread.currentThread().getContextClassLoader();
        ClassLoader cl = null;

        try {
            if ( item.isBinaryUpToDate() && this.ruleBaseCache.containsKey( item.getUUID() ) ) {
                RuleBase rb = this.ruleBaseCache.get( item.getUUID() );
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
                    this.ruleBaseCache.put( item.getUUID(),
                                            loadRuleBase( item,
                                                          cl ) );
                } else {
                    BuilderResult[] errs = this.buildPackage( null,
                                                              false,
                                                              item );
                    if ( errs == null || errs.length == 0 ) {
                        this.ruleBaseCache.put( item.getUUID(),
                                                loadRuleBase( item,
                                                              cl ) );
                    } else {
                        return new BulkTestRunResult( errs,
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
                RuleAsset asset = loadAsset( (AssetItem) it.next() );
                Scenario sc = (Scenario) asset.content;
                runScenario( item.getName(),
                             sc,
                             coverage );//runScenario(sc, res, workingMemory).scenario;

                int[] totals = sc.countFailuresTotal();
                resultSummaries.add( new ScenarioResultSummary( totals[0],
                                                                totals[1],
                                                                asset.metaData.name,
                                                                asset.metaData.description,
                                                                asset.uuid ) );
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

    private HashSet<String> expectedRules(Package bin) {
        HashSet<String> h = new HashSet<String>();
        for ( int i = 0; i < bin.getRules().length; i++ ) {
            h.add( bin.getRules()[i].getName() );
        }
        return h;
    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public AnalysisReport analysePackage(String packageUUID) throws SerializableException {
        if ( Contexts.isSessionContextActive() ) {
            Identity.instance().checkPermission( new PackageUUIDType( packageUUID ),
                                                 RoleTypes.PACKAGE_DEVELOPER );
        }

        String drl = this.buildPackageSource( packageUUID );
        VerifierRunner runner = new VerifierRunner();
        try {
            return runner.analyse( drl );
        } catch ( DroolsParserException e ) {
            log.error( e );
            throw new DetailedSerializableException( "Unable to parse the rules.",
                                                     e.getMessage() );
        }
    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public String[] listTypesInPackage(String packageUUID) throws SerializableException {
        if ( Contexts.isSessionContextActive() ) {
            Identity.instance().checkPermission( new PackageUUIDType( packageUUID ),
                                                 "package.readoly" );
        }

        PackageItem pkg = this.repository.loadPackageByUUID( packageUUID );
        List<String> res = new ArrayList<String>();
        AssetItemIterator it = pkg.listAssetsByFormat( new String[]{AssetFormats.MODEL, AssetFormats.DRL_MODEL} );

        JarInputStream jis = null;

        try {
            while ( it.hasNext() ) {
                AssetItem asset = (AssetItem) it.next();
                if ( !asset.isArchived() ) {
                    if ( asset.getFormat().equals( AssetFormats.MODEL ) ) {
                        jis = new JarInputStream( asset.getBinaryContentAttachment() );
                        JarEntry entry = null;
                        while ( (entry = jis.getNextJarEntry()) != null ) {
                            if ( !entry.isDirectory() ) {
                                if ( entry.getName().endsWith( ".class" ) ) {
                                    res.add( ModelContentHandler.convertPathToName( entry.getName() ) );
                                }
                            }
                        }
                    } else {
                        // its delcared model
                        DrlParser parser = new DrlParser();
                        try {
                            PackageDescr desc = parser.parse( asset.getContent() );
                            List<TypeDeclarationDescr> types = desc.getTypeDeclarations();
                            for ( TypeDeclarationDescr typeDeclarationDescr : types ) {
                                res.add( typeDeclarationDescr.getTypeName() );
                            }
                        } catch ( DroolsParserException e ) {
                            log.error( e );
                        }

                    }

                }
            }
            return res.toArray( new String[res.size()] );
        } catch ( IOException e ) {
            log.error( e );
            throw new DetailedSerializableException( "Unable to read the jar files in the package.",
                                                     e.getMessage() );
        } finally {
            IOUtils.closeQuietly( jis );
        }

    }

    @WebRemote
    public LogEntry[] showLog() {
        if ( Contexts.isSessionContextActive() ) {
            Identity.instance().checkPermission( new AdminType(),
                                                 RoleTypes.ADMIN );
        }

        return LoggingHelper.getMessages();

    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public void renameCategory(String fullPathAndName,
                               String newName) {
        repository.renameCategory( fullPathAndName,
                                   newName );
    }

    public static String getDroolsHeader(PackageItem pkg) {
        if ( pkg.containsAsset( "drools" ) ) {
            return pkg.loadAsset( "drools" ).getContent();
        } else {
            return "";
        }
    }

    public static void updateDroolsHeader(String string,
                                          PackageItem pkg) {
        pkg.checkout();
        AssetItem conf;
        if ( pkg.containsAsset( "drools" ) ) {
            conf = pkg.loadAsset( "drools" );
            conf.updateContent( string );
            conf.checkin( "" );
        } else {
            conf = pkg.addAsset( "drools",
                                 "" );
            conf.updateFormat( "package" );
            conf.updateContent( string );
            conf.checkin( "" );
        }

    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public String[] loadDropDownExpression(String[] valuePairs,
                                           String expression) {
        Map<String, String> context = new HashMap<String, String>();
        for ( int i = 0; i < valuePairs.length; i++ ) {
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

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public void rebuildPackages() throws SerializableException {
        Iterator pkit = repository.listPackages();
        StringBuffer errs = new StringBuffer();
        while ( pkit.hasNext() ) {
            PackageItem pkg = (PackageItem) pkit.next();
            try {
                BuilderResult[] res = this.buildPackage( pkg.getUUID(),
                                                         "",
                                                         true );
                if ( res != null ) {
                    errs.append( "Unable to build package name [" + pkg.getName() + "]\n" );
                    StringBuffer buf = new StringBuffer();
                    for ( int i = 0; i < res.length; i++ ) {
                        buf.append( res[i].toString() );
                        buf.append( '\n' );
                    }
                    log.warn( buf.toString() );

                }
            } catch ( Exception e ) {
                log.error( e );
                errs.append( "An error occurred building package [" + pkg.getName() + "]\n" );
            }
        }

        if ( errs.toString().length() > 0 ) {
            throw new DetailedSerializableException( "Unable to rebuild all packages.",
                                                     errs.toString() );
        }
    }

    public Map<String, List<String>> listUserPermissions() {
        if ( Contexts.isSessionContextActive() ) {
            Identity.instance().checkPermission( new AdminType(),
                                                 RoleTypes.ADMIN );
        }

        PermissionManager pm = new PermissionManager( repository );
        return pm.listUsers();
    }

    public Map<String, List<String>> retrieveUserPermissions(String userName) {
        if ( Contexts.isSessionContextActive() ) {
            Identity.instance().checkPermission( new AdminType(),
                                                 RoleTypes.ADMIN );
        }

        PermissionManager pm = new PermissionManager( repository );
        return pm.retrieveUserPermissions( userName );
    }

    public void updateUserPermissions(String userName,
                                      Map<String, List<String>> perms) {
        if ( Contexts.isSessionContextActive() ) {
            Identity.instance().checkPermission( new AdminType(),
                                                 RoleTypes.ADMIN );
        }

        PermissionManager pm = new PermissionManager( repository );
        System.err.println( perms );
        log.info( "Updating user permissions for userName [" + userName + "] to [" + perms + "]" );
        pm.updateUserPermissions( userName,
                                  perms );
        repository.save();
    }

    public String[] listAvailablePermissionTypes() {
        if ( Contexts.isSessionContextActive() ) {
            Identity.instance().checkPermission( new AdminType(),
                                                 RoleTypes.ADMIN );
        }

        return RoleTypes.listAvailableTypes();
    }

    public void deleteUser(String userName) {
        log.info( "Removing user permissions for user name [" + userName + "]" );
        PermissionManager pm = new PermissionManager( repository );
        pm.removeUserPermissions( userName );
        repository.save();
    }

    /* (non-Javadoc)
    * @see org.drools.guvnor.client.rpc.RepositoryService#getAssetLockerUserName(java.lang.String)
    */
    public String getAssetLockerUserName(String uuid) {
        AssetLockManager alm = AssetLockManager.instance();

        String userName = alm.getAssetLockerUserName( uuid );

        log.info( "Asset locked by [" + userName + "]" );

        return userName;
    }

    /* (non-Javadoc)
     * @see org.drools.guvnor.client.rpc.RepositoryService#lockAsset(java.lang.String)
     */
    public void lockAsset(String uuid) {
        AssetLockManager alm = AssetLockManager.instance();

        String userName;
        if ( Contexts.isApplicationContextActive() ) {
            userName = Identity.instance().getUsername();
        } else {
            userName = "anonymous";
        }

        log.info( "Locking asset uuid=" + uuid + " for user [" + userName + "]" );

        alm.lockAsset( uuid,
                       userName );
    }

    /* (non-Javadoc)
     * @see org.drools.guvnor.client.rpc.RepositoryService#unLockAsset(java.lang.String)
     */
    public void unLockAsset(String uuid) {
        AssetLockManager alm = AssetLockManager.instance();

        log.info( "Unlocking asset [" + uuid + "]" );

        alm.unLockAsset( uuid );
    }

}
