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
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;
import javax.jcr.ItemExistsException;
import javax.jcr.RepositoryException;

import org.apache.commons.lang.StringEscapeUtils;
import org.drools.guvnor.client.explorer.ExplorerNodeConfig;
import org.drools.guvnor.client.rpc.DetailedSerializationException;
import org.drools.guvnor.client.rpc.InboxPageRequest;
import org.drools.guvnor.client.rpc.InboxPageRow;
import org.drools.guvnor.client.rpc.LogEntry;
import org.drools.guvnor.client.rpc.LogPageRow;
import org.drools.guvnor.client.rpc.MetaDataQuery;
import org.drools.guvnor.client.rpc.NewAssetConfiguration;
import org.drools.guvnor.client.rpc.NewGuidedDecisionTableAssetConfiguration;
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
import org.drools.guvnor.server.builder.PageResponseBuilder;
import org.drools.guvnor.server.builder.pagerow.InboxPageRowBuilder;
import org.drools.guvnor.server.builder.pagerow.LogPageRowBuilder;
import org.drools.guvnor.server.builder.pagerow.PermissionPageRowBuilder;
import org.drools.guvnor.server.builder.pagerow.QueryFullTextPageRowBuilder;
import org.drools.guvnor.server.builder.pagerow.QueryMetadataPageRowBuilder;
import org.drools.guvnor.server.builder.pagerow.StatePageRowBuilder;
import org.drools.guvnor.server.repository.UserInbox;
import org.drools.guvnor.server.ruleeditor.springcontext.SpringContextElementsManager;
import org.drools.guvnor.server.ruleeditor.workitem.AssetWorkDefinitionsLoader;
import org.drools.guvnor.server.ruleeditor.workitem.ConfigFileWorkDefinitionsLoader;
import org.drools.guvnor.server.ruleeditor.workitem.WorkDefinitionsLoader;
import org.drools.guvnor.server.ruleeditor.workitem.WorkitemDefinitionElementsManager;
import org.drools.guvnor.server.security.RoleType;
import org.drools.guvnor.server.security.RoleTypes;
import org.drools.guvnor.server.selector.SelectorManager;
import org.drools.guvnor.server.util.DateUtil;
import org.drools.guvnor.server.util.HtmlCleaner;
import org.drools.guvnor.server.util.LoggingHelper;
import org.drools.guvnor.server.util.TableDisplayHandler;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52;
import org.drools.ide.common.shared.workitems.PortableBooleanParameterDefinition;
import org.drools.ide.common.shared.workitems.PortableEnumParameterDefinition;
import org.drools.ide.common.shared.workitems.PortableFloatParameterDefinition;
import org.drools.ide.common.shared.workitems.PortableIntegerParameterDefinition;
import org.drools.ide.common.shared.workitems.PortableListParameterDefinition;
import org.drools.ide.common.shared.workitems.PortableObjectParameterDefinition;
import org.drools.ide.common.shared.workitems.PortableParameterDefinition;
import org.drools.ide.common.shared.workitems.PortableStringParameterDefinition;
import org.drools.ide.common.shared.workitems.PortableWorkDefinition;
import org.drools.process.core.ParameterDefinition;
import org.drools.process.core.WorkDefinition;
import org.drools.process.core.datatype.DataType;
import org.drools.process.core.datatype.impl.type.BooleanDataType;
import org.drools.process.core.datatype.impl.type.EnumDataType;
import org.drools.process.core.datatype.impl.type.FloatDataType;
import org.drools.process.core.datatype.impl.type.IntegerDataType;
import org.drools.process.core.datatype.impl.type.ListDataType;
import org.drools.process.core.datatype.impl.type.ObjectDataType;
import org.drools.process.core.datatype.impl.type.StringDataType;
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
import org.jboss.seam.remoting.annotations.WebRemote;
import org.jboss.seam.security.Identity;
import org.jboss.seam.security.annotations.LoggedIn;
import org.jbpm.process.workitem.WorkDefinitionImpl;
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
@Named("org.drools.guvnor.client.rpc.RepositoryService")
public class ServiceImplementation
    implements
    RepositoryService {

    private static final long           serialVersionUID = 510l;

    private static final LoggingHelper  log              = LoggingHelper.getLogger( ServiceImplementation.class );

    @Inject
    private RulesRepository             rulesRepository;

    @Inject
    private ServiceSecurity             serviceSecurity;

    @Inject
    private RepositoryAssetOperations   repositoryAssetOperations;

    @Inject
    private RepositoryAssetService      repositoryAssetService;

    @Inject
    private RepositoryPackageOperations repositoryPackageOperations;

    @Inject
    private Backchannel                 backchannel;

    @Inject
    private Identity                    identity;

    @WebRemote
    @LoggedIn
    public String[] listWorkspaces() {
        return rulesRepository.listWorkspaces();
    }

    @WebRemote
    @LoggedIn
    public void createWorkspace(String workspace) {
        rulesRepository.createWorkspace( workspace );
    }

    @WebRemote
    @LoggedIn
    public void removeWorkspace(String workspace) {
        rulesRepository.removeWorkspace( workspace );
    }

    /**
     * For the time being, module == package
     */
    @WebRemote
    @LoggedIn
    public void updateWorkspace(String workspace,
                                String[] selectedModules,
                                String[] unselectedModules) {
        for ( String moduleName : selectedModules ) {
            PackageItem module = rulesRepository.loadPackage( moduleName );
            module.addWorkspace( workspace );
            module.checkin( "Add workspace" );
        }
        for ( String moduleName : unselectedModules ) {
            PackageItem module = rulesRepository.loadPackage( moduleName );
            module.removeWorkspace( workspace );
            module.checkin( "Remove workspace" );
        }
    }

    /**
     * This will create a new asset. It will be saved, but not checked in. The
     * initial state will be the draft state. Returns the UUID of the asset.
     */
    @WebRemote
    @LoggedIn
    //@Restrict("#{identity.checkPermission(new PackageNameType( packageName ),initialPackage)}")
    public String createNewRule(String ruleName,
                                String description,
                                String initialCategory,
                                String initialPackage,
                                String format) throws SerializationException {
        serviceSecurity.checkSecurityIsPackageDeveloperWithPackageName( initialPackage );

        log.info( "USER:" + getCurrentUserName() + " CREATING new asset name [" + ruleName + "] in package [" + initialPackage + "]" );

        try {

            PackageItem pkg = rulesRepository.loadPackage( initialPackage );
            AssetItem asset = pkg.addAsset( ruleName,
                                            description,
                                            initialCategory,
                                            format );

            new AssetTemplateCreator().applyPreBuiltTemplates( ruleName,
                                                               format,
                                                               asset );
            rulesRepository.save();

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
     * This will create a new asset. It will be saved, but not checked in. The
     * initial state will be the draft state. Returns the UUID of the asset.
     */
    @WebRemote
    @LoggedIn
    //@Restrict("#{identity.checkPermission(new PackageNameType( packageName ),initialPackage)}")
    public String createNewRule(NewAssetConfiguration configuration) throws SerializationException {
        String assetName = configuration.getAssetName();
        String description = configuration.getDescription();
        String initialCategory = configuration.getInitialCategory();
        String packageName = configuration.getPackageName();
        String format = configuration.getFormat();
        return createNewRule( assetName,
                              description,
                              initialCategory,
                              packageName,
                              format );
    }

    /**
     * This will create a new Guided Decision Table asset. The initial state
     * will be the draft state. Returns the UUID of the asset. The new Asset
     * will be SAVED and CHECKED-IN.
     */
    @WebRemote
    @LoggedIn
    //@Restrict("#{identity.checkPermission(new PackageNameType( packageName ),initialPackage)}")
    public String createNewRule(NewGuidedDecisionTableAssetConfiguration configuration) throws SerializationException {
        String assetName = configuration.getAssetName();
        String description = configuration.getDescription();
        String initialCategory = configuration.getInitialCategory();
        String packageName = configuration.getPackageName();
        String format = configuration.getFormat();

        //Create the asset
        String uuid = createNewRule( assetName,
                                     description,
                                     initialCategory,
                                     packageName,
                                     format );

        //Set the Table Format and check-in
        //TODO Is it possible to alter the content and save without checking-in?
        RuleAsset asset = repositoryAssetService.loadRuleAsset( uuid );
        GuidedDecisionTable52 content = (GuidedDecisionTable52) asset.getContent();
        content.setTableFormat( configuration.getTableFormat() );
        asset.setCheckinComment( "Table Format automatically set to [" + configuration.getTableFormat().toString() + "]" );
        repositoryAssetService.checkinVersion( asset );

        return uuid;
    }

    /**
     * This will create a new asset which refers to an existing asset
     */
    @WebRemote
    @LoggedIn
    public String createNewImportedRule(String sharedAssetName,
                                        String initialPackage) throws SerializationException {
        serviceSecurity.checkSecurityIsPackageDeveloperWithPackageName( initialPackage );

        log.info( "USER:" + rulesRepository.getSession().getUserID() + " CREATING shared asset imported from global area named [" + sharedAssetName + "] in package [" + initialPackage + "]" );

        try {
            PackageItem packageItem = rulesRepository.loadPackage( initialPackage );
            AssetItem asset = packageItem.addAssetImportedFromGlobalArea( sharedAssetName );
            rulesRepository.save();

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
    @LoggedIn
    public void deleteUncheckedRule(String uuid) {
        serviceSecurity.checkSecurityIsPackageAdminWithAdminType();

        AssetItem asset = rulesRepository.loadAssetByUUID( uuid );

        PackageItem packageItem = asset.getPackage();
        packageItem.updateBinaryUpToDate( false );

        asset.remove();

        rulesRepository.save();
        push( "packageChange",
              packageItem.getName() );
    }

    /**
     * @deprecated in favour of {@link #loadRuleListForState(StatePageRequest)}
     */
    @WebRemote
    @LoggedIn
    public TableDataResult loadRuleListForState(String stateName,
                                                int skip,
                                                int numRows,
                                                String tableConfig) throws SerializationException {

        // TODO: May need to use a filter that acts on both package based and
        // category based.
        RepositoryFilter filter = new AssetItemFilter( identity );
        AssetItemPageResult result = rulesRepository.findAssetsByState( stateName,
                                                                             false,
                                                                             skip,
                                                                             numRows,
                                                                             filter );
        return new TableDisplayHandler( tableConfig ).loadRuleListTable( result );
    }

    /**
     * @deprecated in favour of {@link AbstractPagedTable}
     */
    @WebRemote
    @LoggedIn
    public TableConfig loadTableConfig(String listName) {
        TableDisplayHandler handler = new TableDisplayHandler( listName );
        return handler.loadTableConfig();
    }

    /**
     * @deprecated in favour of {@link #queryMetaData(QueryMetadataPageRequest)}
     */
    @WebRemote
    @LoggedIn
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
                for ( MetaDataQuery aQr : qr ) {
                    String vals = (aQr.valueList == null) ? "" : aQr.valueList.trim();
                    if ( vals.length() > 0 ) {
                        put( aQr.attribute,
                                vals.split( ",\\s?" ) );
                    }
                }
            }
        };

        DateQuery[] dates = new DateQuery[2];

        dates[0] = new DateQuery( "jcr:created",
                                  DateUtil.isoDate( createdAfter ),
                                  DateUtil.isoDate( createdBefore ) );
        dates[1] = new DateQuery( AssetItem.LAST_MODIFIED_PROPERTY_NAME,
                                  DateUtil.isoDate( modifiedAfter ),
                                  DateUtil.isoDate( modifiedBefore ) );
        AssetItemIterator it = rulesRepository.query( q,
                                                           seekArchived,
                                                           dates );
        // Add Filter to check Permission
        List<AssetItem> resultList = new ArrayList<AssetItem>();

        RepositoryFilter packageFilter = new PackageFilter( identity );
        RepositoryFilter categoryFilter = new CategoryFilter( identity );

        while ( it.hasNext() ) {
            AssetItem ai = it.next();
            if ( checkPackagePermissionHelper( packageFilter,
                                               ai,
                                               RoleType.PACKAGE_READONLY.getName() ) || checkCategoryPermissionHelper( categoryFilter,
                                                                                                                       ai,
                                                                                                                       RoleType.ANALYST_READ.getName() ) ) {
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
        data.setUuid( uuidStr );
        return data;
    }

    @WebRemote
    @LoggedIn
    public String createState(String name) throws SerializationException {
        log.info( "USER:" + getCurrentUserName() + " CREATING state: [" + name + "]" );
        try {
            name = HtmlCleaner.cleanHTML( name );
            String uuid = rulesRepository.createState( name ).getNode().getUUID();
            rulesRepository.save();
            return uuid;
        } catch ( RepositoryException e ) {
            throw new SerializationException( "Unable to create the status." );
        }
    }

    @WebRemote
    @LoggedIn
    public void removeState(String name) throws SerializationException {
        log.info( "USER:" + getCurrentUserName() + " REMOVING state: [" + name + "]" );

        try {
            rulesRepository.loadState( name ).remove();
            rulesRepository.save();

        } catch ( RulesRepositoryException e ) {
            throw new DetailedSerializationException( "Unable to remove status. It is probably still used (even by archived items).",
                                                      e.getMessage() );
        }
    }

    @WebRemote
    @LoggedIn
    public void renameState(String oldName,
                            String newName) throws SerializationException {
        log.info( "USER:" + getCurrentUserName() + " RENAMING state: [" + oldName + "] to [" + newName + "]" );
        rulesRepository.renameState( oldName,
                                          newName );

    }

    @WebRemote
    @LoggedIn
    public String[] listStates() throws SerializationException {
        StateItem[] states = rulesRepository.listStates();
        String[] result = new String[states.length];
        for ( int i = 0; i < states.length; i++ ) {
            result[i] = states[i].getName();
        }
        return result;
    }

    @WebRemote
    public void clearRulesRepository() {
        serviceSecurity.checkSecurityIsAdmin();

        RulesRepositoryAdministrator admin = new RulesRepositoryAdministrator( rulesRepository.getSession() );
        admin.clearRulesRepository();
    }

    @WebRemote
    @LoggedIn
    public SuggestionCompletionEngine loadSuggestionCompletionEngine(String packageName) throws SerializationException {
        //No need to check role based permission here. Package auto completion suggestion should be available to everybody. 
        //serviceSecurity.checkSecurityIsPackageReadOnlyWithPackageName( packageName );
        SuggestionCompletionEngine suggestionCompletionEngine = null;
        try {
            PackageItem packageItem = rulesRepository.loadPackage( packageName );
            suggestionCompletionEngine = new SuggestionCompletionEngineLoaderInitializer().loadFor( packageItem );
        } catch ( RulesRepositoryException e ) {
            log.error( "An error occurred loadSuggestionCompletionEngine: " + e.getMessage() );
            throw new SerializationException( e.getMessage() );
        }
        return suggestionCompletionEngine;
    }

    @WebRemote
    @LoggedIn
    public String[] getCustomSelectors() throws SerializationException {
        return SelectorManager.getInstance().getCustomSelectors();
    }

    @WebRemote
    @LoggedIn
    public String[] listRulesInGlobalArea() throws SerializationException {
        serviceSecurity.checkSecurityIsPackageReadOnlyWithPackageName( RulesRepository.RULE_GLOBAL_AREA );
        return repositoryPackageOperations.listRulesInPackage( RulesRepository.RULE_GLOBAL_AREA );
    }

    @WebRemote
    @LoggedIn
    public String[] listImagesInGlobalArea() throws SerializationException {
        serviceSecurity.checkSecurityIsPackageReadOnlyWithPackageName( RulesRepository.RULE_GLOBAL_AREA );
        return repositoryPackageOperations.listImagesInPackage( RulesRepository.RULE_GLOBAL_AREA );
    }

    /**
     * @deprecated in favour of {@link #showLog(PageRequest)}
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

        long start = System.currentTimeMillis();
        LogEntry[] logEntries = LoggingHelper.getMessages();
        log.debug( "Search time: " + (System.currentTimeMillis() - start) );

        List<LogPageRow> rowList = new LogPageRowBuilder()
                                       .withPageRequest( request )
                                        .withIdentity( identity )
                                       .withContent( logEntries )
                                           .build();

        PageResponse<LogPageRow> response = new PageResponseBuilder<LogPageRow>()
                                                .withStartRowIndex( request.getStartRowIndex() )
                                                .withPageRowList( rowList )
                                                .withTotalRowSizeExact()
                                                .withLastPage( (rowList.size() + request.getStartRowIndex()) == logEntries.length )
                                                .withTotalRowSize( logEntries.length )
                                                    .build();
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
    @LoggedIn
    public String[] loadDropDownExpression(String[] valuePairs,
                                           String expression) {
        Map<String, String> context = new HashMap<String, String>();

        for ( String valuePair : valuePairs ) {
            if ( valuePair == null ) {
                return new String[0];
            }
            String[] pair = valuePair.split( "=" );
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
     * @deprecated in favour of {@link #listUserPermissions(PageRequest)}
     */
    @LoggedIn
    public Map<String, List<String>> listUserPermissions() {
        serviceSecurity.checkSecurityIsAdmin();
        return new PermissionManager( rulesRepository ).listUsers();
    }

    @LoggedIn
    public PageResponse<PermissionsPageRow> listUserPermissions(PageRequest request) {
        if ( request == null ) {
            throw new IllegalArgumentException( "request cannot be null" );
        }
        if ( request.getPageSize() != null && request.getPageSize() < 0 ) {
            throw new IllegalArgumentException( "pageSize cannot be less than zero." );
        }

        serviceSecurity.checkSecurityIsAdmin();

        long start = System.currentTimeMillis();
        Map<String, List<String>> permissions = new PermissionManager( rulesRepository ).listUsers();

        log.debug( "Search time: " + (System.currentTimeMillis() - start) );

        List<PermissionsPageRow> rowList = new PermissionPageRowBuilder()
                                                .withPageRequest( request )
                                                .withIdentity( identity )
                                                .withContent( permissions )
                                                    .build();

        PageResponse<PermissionsPageRow> response = new PageResponseBuilder<PermissionsPageRow>()
                                                        .withStartRowIndex( request.getStartRowIndex() )
                                                        .withTotalRowSize( permissions.size() )
                                                        .withTotalRowSizeExact()
                                                        .withPageRowList( rowList )
                                                        .withLastPage( (rowList.size() + request.getStartRowIndex()) == permissions.size() )
                                                            .build();
        long methodDuration = System.currentTimeMillis() - start;
        log.debug( "Retrieved Log Entries in " + methodDuration + " ms." );
        return response;
    }

    @LoggedIn
    public Map<String, List<String>> retrieveUserPermissions(String userName) {
        serviceSecurity.checkSecurityIsAdmin();

        PermissionManager pm = new PermissionManager( rulesRepository );
        return pm.retrieveUserPermissions( userName );
    }

    @LoggedIn
    public void updateUserPermissions(String userName,
                                      Map<String, List<String>> perms) {
        serviceSecurity.checkSecurityIsAdmin();

        PermissionManager pm = new PermissionManager( rulesRepository );

        log.info( "Updating user permissions for userName [" + userName + "] to [" + perms + "]" );
        pm.updateUserPermissions( userName,
                                  perms );
        rulesRepository.save();
    }

    @Deprecated
    @LoggedIn
    public String[] listAvailablePermissionTypes() {
        serviceSecurity.checkSecurityIsAdmin();
        return RoleTypes.listAvailableTypes();
    }

    @LoggedIn
    public List<String> listAvailablePermissionRoleTypes() {
        serviceSecurity.checkSecurityIsAdmin();
        RoleType[] roleTypes = RoleType.values();
        List<String> values = new ArrayList<String>();
        for ( RoleType roleType : roleTypes ) {
            values.add( roleType.getName() );
        }
        return values;
    }

    @LoggedIn
    public void deleteUser(String userName) {
        log.info( "Removing user permissions for user name [" + userName + "]" );
        PermissionManager pm = new PermissionManager( rulesRepository );
        pm.removeUserPermissions( userName );
        rulesRepository.save();
    }

    @LoggedIn
    public void createUser(String userName) {
        log.info( "Creating user permissions, user name [" + userName + "]" );
        PermissionManager pm = new PermissionManager( rulesRepository );
        pm.createUser( userName );
        rulesRepository.save();
    }

    /**
     * @deprecated in favour of {@link #loadInbox(InboxPageRequest)}
     */
    @LoggedIn
    public TableDataResult loadInbox(String inboxName) throws DetailedSerializationException {
        try {
            UserInbox ib = new UserInbox( rulesRepository );
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

    @LoggedIn
    public PageResponse<InboxPageRow> loadInbox(InboxPageRequest request) throws DetailedSerializationException {
        if ( request == null ) {
            throw new IllegalArgumentException( "request cannot be null" );
        }
        if ( request.getPageSize() != null && request.getPageSize() < 0 ) {
            throw new IllegalArgumentException( "pageSize cannot be less than zero." );
        }

        String inboxName = request.getInboxName();
        PageResponse<InboxPageRow> response = new PageResponse<InboxPageRow>();
        long start = System.currentTimeMillis();

        try {

            List<InboxEntry> entries = new UserInbox( rulesRepository ).loadEntries( inboxName );

            log.debug( "Search time: " + (System.currentTimeMillis() - start) );

            Iterator<InboxEntry> iterator = entries.iterator();
            List<InboxPageRow> rowList = new InboxPageRowBuilder()
                                            .withPageRequest( request )
                                            .withIdentity( identity )
                                            .withContent( iterator )
                                                .build();

            response = new PageResponseBuilder<InboxPageRow>()
                            .withStartRowIndex( request.getStartRowIndex() )
                            .withTotalRowSize( entries.size() )
                            .withTotalRowSizeExact()
                            .withPageRowList( rowList )
                            .withLastPage( !iterator.hasNext() )
                                .build();
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
            Configuration configuration = new Configuration();
            configuration.setObjectWrapper( new DefaultObjectWrapper() );
            configuration.setTemplateUpdateDelay( 0 );

            Template template = new Template( name,
                                              new InputStreamReader( ServiceImplementation.class.getResourceAsStream( "/repoconfig/" + name + ".xml" ) ),
                                              configuration );
            StringWriter stringwriter = new StringWriter();
            template.process( data,
                              stringwriter );
            return StringEscapeUtils.escapeXml( stringwriter.toString() );
        } catch ( Exception e ) {
            return "";
        }
    }

    /**
     * Returns the Spring context elements specified by
     * SpringContextElementsManager
     * 
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
     * Returns the Workitem Definition elements specified by
     * WorkitemDefinitionElementsManager
     * 
     * @return a Map containing the key,value pairs of data.
     * @throws DetailedSerializationException
     */
    public Map<String, String> loadWorkitemDefinitionElementData() throws DetailedSerializationException {
        try {
            return WorkitemDefinitionElementsManager.getInstance().getElements();
        } catch ( IOException ex ) {
            log.error( "Error loading Workitem Definition Elements",
                       ex );
            throw new DetailedSerializationException( "Error loading Workitem Definition Elements",
                                                      "View server logs for more information" );
        }
    }

    /**
     * Load and return a Map of all parsed Work Definitions. The source of such
     * Work Definitions is Assets defined in Guvnor and those defined in
     * /workitem-definitions.xml
     * 
     * @param packageUUID
     *            The Package from which to load Work Items
     * @return
     * @throws DetailedSerializationException
     */
    @LoggedIn
    public Set<PortableWorkDefinition> loadWorkItemDefinitions(String packageUUID) throws DetailedSerializationException {
        Map<String, org.drools.process.core.WorkDefinition> workDefinitions = new HashMap<String, org.drools.process.core.WorkDefinition>();
        //Load WorkDefinitions from different sources

        try {
            // - Assets
            WorkDefinitionsLoader loader = new AssetWorkDefinitionsLoader( repositoryAssetService,
                                                                           packageUUID );
            Map<String, org.drools.process.core.WorkDefinition> assetWorkDefinitions = loader.getWorkDefinitions();
            for ( Map.Entry<String, org.drools.process.core.WorkDefinition> entry : assetWorkDefinitions.entrySet() ) {
                if ( !workDefinitions.containsKey( entry.getKey() ) ) {
                    workDefinitions.put( entry.getKey(),
                                         entry.getValue() );
                }
            }

            // - workitem-definitions.xml
            Map<String, org.drools.process.core.WorkDefinition> configuredWorkDefinitions = ConfigFileWorkDefinitionsLoader.getInstance().getWorkDefinitions();
            for ( Map.Entry<String, org.drools.process.core.WorkDefinition> entry : configuredWorkDefinitions.entrySet() ) {
                if ( !workDefinitions.containsKey( entry.getKey() ) ) {
                    workDefinitions.put( entry.getKey(),
                                         entry.getValue() );
                }
            }
        } catch ( Exception e ) {
            log.error( "Error loading Workitem Definitions",
                       e );
            throw new DetailedSerializationException( "Error loading Workitem Definitions",
                                                      "View server logs for more information" );
        }

        //Copy the Work Items into Structures suitable for GWT
        Set<PortableWorkDefinition> workItems = new HashSet<PortableWorkDefinition>();
        for ( Map.Entry<String, WorkDefinition> entry : workDefinitions.entrySet() ) {
            PortableWorkDefinition wid = new PortableWorkDefinition();
            WorkDefinitionImpl wd = (WorkDefinitionImpl) entry.getValue();
            wid.setName( wd.getName() );
            wid.setDisplayName( wd.getDisplayName() );
            wid.setParameters( convertWorkItemParameters( entry.getValue().getParameters() ) );
            wid.setResults( convertWorkItemParameters( entry.getValue().getResults() ) );
            workItems.add( wid );
        }
        return workItems;
    }

    private Set<PortableParameterDefinition> convertWorkItemParameters(Set<ParameterDefinition> parameters) {
        Set<PortableParameterDefinition> pps = new HashSet<PortableParameterDefinition>();
        for ( ParameterDefinition pd : parameters ) {
            DataType pdt = pd.getType();
            PortableParameterDefinition ppd = null;
            if ( pdt instanceof BooleanDataType ) {
                ppd = new PortableBooleanParameterDefinition();
            } else if ( pdt instanceof FloatDataType ) {
                ppd = new PortableFloatParameterDefinition();
            } else if ( pdt instanceof IntegerDataType ) {
                ppd = new PortableIntegerParameterDefinition();
            } else if ( pdt instanceof ListDataType ) {
                //TODO ListDataType
                //ppd = new PortableListParameterDefinition();
            } else if ( pdt instanceof ObjectDataType ) {
                ppd = new PortableObjectParameterDefinition();
                PortableObjectParameterDefinition oppd = (PortableObjectParameterDefinition) ppd;
                ObjectDataType odt = (ObjectDataType) pdt;
                oppd.setClassName( odt.getClassName() );
            } else if ( pd.getType() instanceof StringDataType ) {
                ppd = new PortableStringParameterDefinition();
            } else if ( pdt instanceof EnumDataType ) {
                //TODO EnumDataType
                //ppd = new PortableEnumParameterDefinition();
                //PortableEnumParameterDefinition eppd = (PortableEnumParameterDefinition) ppd;
                //EnumDataType epdt = (EnumDataType) pdt;
                //eppd.setClassName( epdt.getClassName() );
                //if ( epdt.getValueMap() != null ) {
                //    eppd.setValues( epdt.getValueNames() );
                //}
            }
            if ( ppd != null ) {
                ppd.setName( pd.getName() );
                pps.add( ppd );
            }
        }
        return pps;
    }

    @WebRemote
    @LoggedIn
    public PageResponse<QueryPageRow> queryFullText(QueryPageRequest request) throws SerializationException {
        if ( request == null ) {
            throw new IllegalArgumentException( "request cannot be null" );
        }
        if ( request.getPageSize() != null && request.getPageSize() < 0 ) {
            throw new IllegalArgumentException( "pageSize cannot be less than zero." );
        }

        long start = System.currentTimeMillis();
        AssetItemIterator iterator = rulesRepository.queryFullText( request.getSearchText(),
                                                                         request.isSearchArchived() );
        log.debug( "Search time: " + (System.currentTimeMillis() - start) );

        List<QueryPageRow> rowList = new QueryFullTextPageRowBuilder()
                                            .withPageRequest( request )
                                            .withIdentity( identity )
                                            .withContent( iterator )
                                                .build();
        boolean bHasMoreRows = iterator.hasNext();
        PageResponse<QueryPageRow> response = new PageResponseBuilder<QueryPageRow>()
                                                      .withStartRowIndex( request.getStartRowIndex() )
                                                      .withPageRowList( rowList )
                                                      .withLastPage( !bHasMoreRows )
                                                          .buildWithTotalRowCount( -1 );

        long methodDuration = System.currentTimeMillis() - start;
        log.debug( "Queried repository (Full Text) for (" + request.getSearchText() + ") in " + methodDuration + " ms." );
        return response;
    }

    @WebRemote
    @LoggedIn
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

        long start = System.currentTimeMillis();
        AssetItemIterator iterator = rulesRepository.query( queryMap,
                                                                 request.isSearchArchived(),
                                                                 dates );
        log.debug( "Search time: " + (System.currentTimeMillis() - start) );

        List<QueryPageRow> rowList = new QueryMetadataPageRowBuilder()
                                            .withPageRequest( request )
                                            .withIdentity( identity )
                                            .withContent( iterator )
                                            .build();
        boolean bHasMoreRows = iterator.hasNext();
        PageResponse<QueryPageRow> response = new PageResponseBuilder<QueryPageRow>()
                                                .withStartRowIndex( request.getStartRowIndex() )
                                                .withPageRowList( rowList )
                                                .withLastPage( !bHasMoreRows )
                                                .buildWithTotalRowCount( -1 );//its impossible to know the exact selected count until we'v reached
                                                                              //the end of iterator
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
                                  DateUtil.isoDate( request.getCreatedAfter() ),
                                  DateUtil.isoDate( request.getCreatedBefore() ) );
        dates[1] = new DateQuery( AssetItem.LAST_MODIFIED_PROPERTY_NAME,
                                  DateUtil.isoDate( request.getLastModifiedAfter() ),
                                  DateUtil.isoDate( request.getLastModifiedBefore() ) );
        return dates;
    }

    @WebRemote
    @LoggedIn
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
        // NOTE: Filtering is handled in repository.findAssetsByState()
        int numRowsToReturn = (request.getPageSize() == null ? -1 : request.getPageSize());
        AssetItemPageResult result = rulesRepository.findAssetsByState( request.getStateName(),
                                                                             false,
                                                                             request.getStartRowIndex(),
                                                                             numRowsToReturn,
                                                                             new AssetItemFilter( identity ) );
        log.debug( "Search time: " + (System.currentTimeMillis() - start) );

        // Populate response
        boolean bHasMoreRows = result.hasNext;

        List<StatePageRow> rowList = new StatePageRowBuilder()
                                            .withPageRequest( request )
                                            .withIdentity( identity )
                                            .withContent( result.assets.iterator() )
                                                .build();

        PageResponse<StatePageRow> response = new PageResponseBuilder<StatePageRow>()
                                                    .withStartRowIndex( request.getStartRowIndex() )
                                                    .withPageRowList( rowList )
                                                    .withLastPage( !bHasMoreRows )
                                                        .buildWithTotalRowCount( -1 );

        long methodDuration = System.currentTimeMillis() - start;
        log.debug( "Searched for Assest with State (" + request.getStateName() + ") in " + methodDuration + " ms." );
        return response;
    }

    private boolean checkCategoryPermissionHelper(RepositoryFilter filter,
                                                  AssetItem item,
                                                  String roleType) {
        List<CategoryItem> tempCateList = item.getCategories();
        for ( CategoryItem categoryItem : tempCateList ) {
            if ( filter.accept( categoryItem.getName(),
                                roleType ) ) {
                return true;
            }
        }

        return false;
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
        return rulesRepository.getSession().getUserID();
    }

    public List<PushResponse> subscribe() {
        return backchannel.subscribe();
    }

    /**
     * Check whether an asset exists in a package
     * 
     * @param assetName
     * @param packageName
     * @return True if the asset already exists in the package
     * @throws SerializationException
     */
    @WebRemote
    @LoggedIn
    public boolean doesAssetExistInPackage(String assetName,
                                           String packageName) throws SerializationException {
        serviceSecurity.checkSecurityIsPackageDeveloperWithPackageName( packageName );

        try {

            PackageItem pkg = rulesRepository.loadPackage( packageName );
            return pkg.containsAsset( assetName );

        } catch ( RulesRepositoryException e ) {
            log.error( "An error occurred checking if asset [" + assetName + "] exists in package [" + packageName + "]: ",
                       e );
            throw new SerializationException( e.getMessage() );
        }
    }

}
