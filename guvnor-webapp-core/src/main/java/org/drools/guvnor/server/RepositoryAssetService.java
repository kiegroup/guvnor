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
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.drools.guvnor.client.rpc.AdminArchivedPageRow;
import org.drools.guvnor.client.rpc.Asset;
import org.drools.guvnor.client.rpc.AssetPageRequest;
import org.drools.guvnor.client.rpc.AssetPageRow;
import org.drools.guvnor.client.rpc.AssetService;
import org.drools.guvnor.client.rpc.BuilderResult;
import org.drools.guvnor.client.rpc.ConversionResult;
import org.drools.guvnor.client.rpc.DetailedSerializationException;
import org.drools.guvnor.client.rpc.DiscussionRecord;
import org.drools.guvnor.client.rpc.PageRequest;
import org.drools.guvnor.client.rpc.PageResponse;
import org.drools.guvnor.client.rpc.PushResponse;
import org.drools.guvnor.client.rpc.QueryPageRequest;
import org.drools.guvnor.client.rpc.QueryPageRow;
import org.drools.guvnor.client.rpc.TableDataResult;
import org.drools.guvnor.server.cache.RuleBaseCache;
import org.drools.guvnor.server.contenthandler.ContentHandler;
import org.drools.guvnor.server.contenthandler.ContentManager;
import org.drools.guvnor.server.contenthandler.ICanHasAttachment;
import org.drools.guvnor.server.converters.ConversionService;
import org.drools.guvnor.server.repository.Preferred;
import org.drools.guvnor.server.repository.UserInbox;
import org.drools.guvnor.server.util.AssetPopulator;
import org.drools.guvnor.server.util.Discussion;
import org.drools.guvnor.server.util.LoggingHelper;
import org.drools.repository.AssetItem;
import org.drools.repository.ModuleItem;
import org.drools.repository.RulesRepository;
import org.drools.repository.RulesRepositoryException;
import org.drools.repository.VersionableItem;
import org.jboss.seam.remoting.annotations.WebRemote;
import org.jboss.seam.security.Identity;
import org.jboss.seam.security.annotations.LoggedIn;

import com.google.gwt.user.client.rpc.SerializationException;

@ApplicationScoped
@Named("org.drools.guvnor.client.rpc.AssetService")
public class RepositoryAssetService
        implements
        AssetService {

    private static final long           serialVersionUID = 90111;

    private static final LoggingHelper  log              = LoggingHelper.getLogger( RepositoryAssetService.class );

    @Inject @Preferred
    protected RulesRepository           rulesRepository;

    @Inject
    protected RepositoryAssetOperations repositoryAssetOperations;

    @Inject
    protected ServiceSecurity           serviceSecurity;

    @Inject
    protected Backchannel               backchannel;

    @Inject
    protected Identity                  identity;

    @Inject
    private ConversionService           conversionService;

    public RulesRepository getRulesRepository() {
        return rulesRepository;
    }

    /**
     * This actually does the hard work of loading up an asset based on its
     * format.
     * <p/>
     * Role-based Authorization check: This method can be accessed if user has
     * following permissions: 1. The user has a ANALYST_READ role or higher
     * (i.e., ANALYST) and this role has permission to access the category which
     * the asset belongs to. Or. 2. The user has a package.readonly role or
     * higher (i.e., package.admin, package.developer) and this role has
     * permission to access the package which the asset belongs to.
     */
    @WebRemote
    @LoggedIn
    public Asset loadRuleAsset(String uuid) throws SerializationException {

        long time = System.currentTimeMillis();

        AssetItem item = rulesRepository.loadAssetByUUID( uuid );
        Asset asset = new AssetPopulator().populateFrom( item );

        asset.setMetaData( repositoryAssetOperations.populateMetaData( item ) );


        serviceSecurity.checkIsPackageReadOnlyOrAnalystReadOnly( asset );
        ModuleItem pkgItem = handlePackageItem( item,
                                                asset );

        log.debug( "Package: " + pkgItem.getName() + ", asset: " + item.getName() + ". Load time taken for asset: " + (System.currentTimeMillis() - time) );
        UserInbox.recordOpeningEvent( item );
        return asset;

    }

    private ModuleItem handlePackageItem(AssetItem item,
                                         Asset asset) throws SerializationException {
        ModuleItem packageItem = item.getModule();

        ContentHandler handler = ContentManager.getHandler( asset.getFormat() );
        handler.retrieveAssetContent( asset,
                                      item );

        asset.setReadonly( asset.getMetaData().isHasSucceedingVersion() || asset.isArchived() );

        if ( packageItem.isSnapshot() ) {
            asset.setReadonly( true );
        }
        return packageItem;
    }

    @WebRemote
    @LoggedIn
    public Asset[] loadRuleAssets(String[] uuids) throws SerializationException {
        return loadRuleAssets( Arrays.asList( uuids ) );
    }

    @WebRemote
    @LoggedIn
    public String checkinVersion(Asset asset) throws SerializationException {
        serviceSecurity.checkIsPackageDeveloperOrAnalyst( asset );

        log.info( "USER:" + getCurrentUserName() + " CHECKING IN asset: [" + asset.getName() + "] UUID: [" + asset.getUuid() + "] " );
        return repositoryAssetOperations.checkinVersion( asset );
    }

    @WebRemote
    @LoggedIn
    public void restoreVersion(String versionUUID,
                               String assetUUID,
                               String comment) {
        repositoryAssetOperations.restoreVersion( versionUUID,
                                                  assetUUID,
                                                  comment );
    }

    @WebRemote
    @LoggedIn
    public TableDataResult loadItemHistory(String uuid) throws SerializationException {
        //VersionableItem assetItem = rulesRepository.loadAssetByUUID( uuid );
        VersionableItem assetItem = rulesRepository.loadItemByUUID( uuid );

        //serviceSecurity.checkSecurityAssetPackagePackageReadOnly( assetItem );
        return repositoryAssetOperations.loadItemHistory( assetItem );
    }

    /**
     * @deprecated in favour of {@link #loadArchivedAssets(PageRequest)}
     */
    @WebRemote
    @LoggedIn
    public TableDataResult loadAssetHistory(String packageUUID,
                                            String assetName) throws SerializationException {
        ModuleItem pi = rulesRepository.loadModuleByUUID( packageUUID );
        AssetItem assetItem = pi.loadAsset( assetName );
        serviceSecurity.checkSecurityPackageReadOnlyWithPackageUuid( assetItem.getModule().getUUID() );

        return repositoryAssetOperations.loadItemHistory( assetItem );
    }

    @WebRemote
    @LoggedIn
    @Deprecated
    public TableDataResult loadArchivedAssets(int skip,
                                              int numRows) throws SerializationException {
        return repositoryAssetOperations.loadArchivedAssets( skip,
                                                             numRows );
    }

    @WebRemote
    @LoggedIn
    public PageResponse<AdminArchivedPageRow> loadArchivedAssets(PageRequest request) throws SerializationException {
        if ( request == null ) {
            throw new IllegalArgumentException( "request cannot be null" );
        }
        if ( request.getPageSize() != null && request.getPageSize() < 0 ) {
            throw new IllegalArgumentException( "pageSize cannot be less than zero." );
        }

        return repositoryAssetOperations.loadArchivedAssets( request );
    }

    /**
     * @deprecated in favour of {@link #findAssetPage(AssetPageRequest)}
     */
    @WebRemote
    @LoggedIn
    public TableDataResult listAssetsWithPackageName(String packageName,
                                                     String formats[],
                                                     int skip,
                                                     int numRows,
                                                     String tableConfig) throws SerializationException {
        ModuleItem pkg = rulesRepository.loadModule( packageName );
        return listAssets( pkg.getUUID(),
                           formats,
                           skip,
                           numRows,
                           tableConfig );
    }

    /**
     * @deprecated in favour of {@link #findAssetPage(AssetPageRequest)}
     */
    @WebRemote
    @LoggedIn
    public TableDataResult listAssets(String packageUuid,
                                      String formats[],
                                      int skip,
                                      int numRows,
                                      String tableConfig) throws SerializationException {
        log.debug( "Loading asset list for [" + packageUuid + "]" );
        if ( numRows == 0 ) {
            throw new DetailedSerializationException( "Unable to return zero results (bug)",
                                                      "probably have the parameters around the wrong way, sigh..." );
        }
        return repositoryAssetOperations.listAssets( packageUuid,
                                                     formats,
                                                     skip,
                                                     numRows,
                                                     tableConfig );
    }

    @WebRemote
    @LoggedIn
    public String copyAsset(String assetUUID,
                            String newPackage,
                            String newName) {
        serviceSecurity.checkSecurityIsPackageDeveloperWithPackageName( newPackage );

        log.info( "USER:" + getCurrentUserName() + " COPYING asset: [" + assetUUID + "] to [" + newName + "] in PACKAGE [" + newPackage + "]" );
        return rulesRepository.copyAsset( assetUUID,
                                          newPackage,
                                          newName );
    }

    @WebRemote
    @LoggedIn
    public void changeAssetPackage(String uuid,
                                   String newPackage,
                                   String comment) {
        serviceSecurity.checkSecurityIsPackageDeveloperWithPackageName( newPackage );

        AssetItem item = rulesRepository.loadAssetByUUID( uuid );

        //Perform house-keeping for when an Asset is removed from a package
        attachmentRemoved( item );

        log.info( "USER:" + getCurrentUserName() + " CHANGING PACKAGE OF asset: [" + uuid + "] to [" + newPackage + "]" );
        rulesRepository.moveRuleItemModule( newPackage,
                                            uuid,
                                            comment );

        //Perform house-keeping for when an Asset is added to a package
        attachmentAdded( item );

    }

    @WebRemote
    @LoggedIn
    public void promoteAssetToGlobalArea(String uuid) {
        serviceSecurity.checkSecurityIsPackageDeveloperWithPackageName( RulesRepository.GLOBAL_AREA );
        AssetItem item = rulesRepository.loadAssetByUUID( uuid );
        serviceSecurity.checkSecurityIsPackageDeveloperWithPackageName( item.getModuleName() );

        //Perform house-keeping for when an Asset is removed from a module
        attachmentRemoved( item );

        log.info( "USER:" + getCurrentUserName() + " CHANGING MODULE OF asset: [" + uuid + "] to [ globalArea ]" );
        rulesRepository.moveRuleItemModule( RulesRepository.GLOBAL_AREA,
                                            uuid,
                                            "promote asset to globalArea" );

        //Perform house-keeping for when an Asset is added to a module
        attachmentAdded( item );

    }

    @WebRemote
    @LoggedIn
    public String buildAssetSource(Asset asset) throws SerializationException {
        serviceSecurity.checkIsPackageDeveloperOrAnalyst( asset );
        return repositoryAssetOperations.buildAssetSource( asset );
    }

    @WebRemote
    @LoggedIn
    public String renameAsset(String uuid,
                              String newName) {
        AssetItem item = rulesRepository.loadAssetByUUID( uuid );
        serviceSecurity.checkIsPackageDeveloperOrAnalyst( item );

        return repositoryAssetOperations.renameAsset( uuid,
                                                      newName );
    }

    @WebRemote
    @LoggedIn
    public void archiveAsset(String uuid) {
        archiveOrUnarchiveAsset( uuid,
                                 true );
    }

    @WebRemote
    @LoggedIn
    public BuilderResult validateAsset(Asset asset) throws SerializationException {
        serviceSecurity.checkIsPackageDeveloperOrAnalyst( asset );
        return repositoryAssetOperations.validateAsset( asset );
    }

    public void unArchiveAsset(String uuid) {
        archiveOrUnarchiveAsset( uuid,
                                 false );
    }

    @WebRemote
    @LoggedIn
    public void archiveAssets(String[] uuids,
                              boolean value) {
        for ( String uuid : uuids ) {
            archiveOrUnarchiveAsset( uuid,
                                     value );
        }
    }

    @WebRemote
    @LoggedIn
    public void removeAsset(String uuid) {
        try {
            AssetItem item = rulesRepository.loadAssetByUUID( uuid );
            serviceSecurity.checkSecurityIsPackageDeveloperWithPackageUuid( item.getModule().getUUID() );

            item.remove();
            rulesRepository.save();
        } catch ( RulesRepositoryException e ) {
            log.error( "Unable to remove asset.",
                       e );
            throw e;
        }
    }

    @WebRemote
    @LoggedIn
    public void removeAssets(String[] uuids) {
        for ( String uuid : uuids ) {
            removeAsset( uuid );
        }
    }

    @WebRemote
    @LoggedIn
    public PageResponse<AssetPageRow> findAssetPage(AssetPageRequest request) throws SerializationException {
        if ( request == null ) {
            throw new IllegalArgumentException( "request cannot be null" );
        }
        if ( request.getPageSize() != null && request.getPageSize() < 0 ) {
            throw new IllegalArgumentException( "pageSize cannot be less than zero." );
        }

        return repositoryAssetOperations.findAssetPage( request );
    }

    @WebRemote
    @LoggedIn
    public PageResponse<QueryPageRow> quickFindAsset(QueryPageRequest request) throws SerializationException {
        if ( request == null ) {
            throw new IllegalArgumentException( "request cannot be null" );
        }
        if ( request.getPageSize() != null && request.getPageSize() < 0 ) {
            throw new IllegalArgumentException( "pageSize cannot be less than zero." );
        }

        return repositoryAssetOperations.quickFindAsset( request );
    }

    /**
     * @deprecated in favour of {@link #quickFindAsset(QueryPageRequest)}
     */
    @WebRemote
    @LoggedIn
    public TableDataResult quickFindAsset(String searchText,
                                          boolean searchArchived,
                                          int skip,
                                          int numRows) throws SerializationException {
        return repositoryAssetOperations.quickFindAsset( searchText,
                                                         searchArchived,
                                                         skip,
                                                         numRows );
    }

    /*
     * (non-Javadoc)
     * @see
     * org.drools.guvnor.client.rpc.RepositoryService#lockAsset(java.lang.String
     * )
     */
    @LoggedIn
    public void lockAsset(String uuid) {
        repositoryAssetOperations.lockAsset( uuid );
    }

    /*
     * (non-Javadoc)
     * @see
     * org.drools.guvnor.client.rpc.RepositoryService#unLockAsset(java.lang.
     * String)
     */
    @LoggedIn
    public void unLockAsset(String uuid) {
        repositoryAssetOperations.unLockAsset( uuid );
    }

    /**
     * @deprecated in favour of
     *             {@link ServiceImplementation#queryFullText(QueryPageRequest)}
     */
    @WebRemote
    @LoggedIn
    public TableDataResult queryFullText(String text,
                                         boolean seekArchived,
                                         int skip,
                                         int numRows) throws SerializationException {
        if ( numRows == 0 ) {
            throw new DetailedSerializationException( "Unable to return zero results (bug)",
                                                      "probably have the parameters around the wrong way, sigh..." );
        }
        return repositoryAssetOperations.queryFullText( text,
                                                        seekArchived,
                                                        skip,
                                                        numRows );
    }

    Asset[] loadRuleAssets(Collection<String> uuids) throws SerializationException {
        if ( uuids == null ) {
            return null;
        }
        Collection<Asset> assets = new HashSet<Asset>();

        for ( String uuid : uuids ) {
            assets.add( loadRuleAsset( uuid ) );
        }

        return assets.toArray( new Asset[assets.size()] );
    }

    private void archiveOrUnarchiveAsset(String uuid,
                                         boolean archive) {
        AssetItem item = rulesRepository.loadAssetByUUID( uuid );
        serviceSecurity.checkIsPackageDeveloperOrAnalyst( item );
        if ( item.getModule().isArchived() ) {
            throw new RulesRepositoryException( "The package [" + item.getModuleName() + "] that asset [" + item.getName() + "] belongs to is archived. You need to unarchive it first." );
        }
        log.info( "USER:" + getCurrentUserName() + " ARCHIVING asset: [" + item.getName() + "] UUID: [" + item.getUUID() + "] " );
        try {
            ContentHandler handler = getContentHandler( item );
            if ( handler instanceof ICanHasAttachment ) {
                ((ICanHasAttachment) handler).onAttachmentRemoved( item );
            }
        } catch ( IOException e ) {
            log.error( "Unable to remove asset attachment",
                       e );
        }
        item.archiveItem( archive );
        ModuleItem pkg = item.getModule();
        pkg.updateBinaryUpToDate( false );
        RuleBaseCache.getInstance().remove( pkg.getUUID() );
        if ( archive ) {
            item.checkin( "archived" );
        } else {
            item.checkin( "unarchived" );
        }
        push( "packageChange",
                pkg.getName() );
    }

    @LoggedIn
    public List<DiscussionRecord> addToDiscussionForAsset(String assetId,
                                                          String comment) {
        return repositoryAssetOperations.addToDiscussionForAsset( assetId,
                                                                  comment );
    }

    @LoggedIn
    public void clearAllDiscussionsForAsset(final String assetId) {
        serviceSecurity.checkSecurityIsAdmin();
        repositoryAssetOperations.clearAllDiscussionsForAsset( assetId );
    }

    @LoggedIn
    public List<DiscussionRecord> loadDiscussionForAsset(String assetId) {
        return new Discussion().fromString( rulesRepository.loadAssetByUUID( assetId ).getStringProperty( Discussion.DISCUSSION_PROPERTY_KEY ) );
    }

    /**
     * Role-based Authorization check: This method can be accessed if user has
     * following permissions: 1. The user has a Analyst role and this role has
     * permission to access the category which the asset belongs to. Or. 2. The
     * user has a package.developer role or higher (i.e., package.admin) and
     * this role has permission to access the package which the asset belongs
     * to.
     */
    @WebRemote
    @LoggedIn
    public void changeState(String uuid,
                            String newState) {
        AssetItem asset = rulesRepository.loadAssetByUUID( uuid );
        serviceSecurity.checkIsPackageDeveloperOrAnalyst( asset );

        log.info( "USER:" + getCurrentUserName() + " CHANGING ASSET STATUS. Asset name, uuid: " + "[" + asset.getName() + ", " + asset.getUUID() + "]" + " to [" + newState + "]" );
        String oldState = asset.getStateDescription();
        asset.updateState( newState );

        push( "statusChange",
                oldState );
        push( "statusChange",
                newState );

        addToDiscussionForAsset( asset.getUUID(),
                                 oldState + " -> " + newState );

        rulesRepository.save();
    }

    @WebRemote
    @LoggedIn
    public void changePackageState(String uuid,
                                   String newState) {

        serviceSecurity.checkSecurityIsPackageDeveloperWithPackageUuid( uuid );

        ModuleItem pkg = rulesRepository.loadModuleByUUID( uuid );
        log.info( "USER:" + getCurrentUserName() + " CHANGING Package STATUS. Asset name, uuid: " + "[" + pkg.getName() + ", " + pkg.getUUID() + "]" + " to [" + newState + "]" );
        pkg.changeStatus( newState );

        rulesRepository.save();
    }

    /*
     * (non-Javadoc)
     * @see
     * org.drools.guvnor.client.rpc.RepositoryService#getAssetLockerUserName
     * (java.lang.String)
     */
    @LoggedIn
    public String getAssetLockerUserName(String uuid) {
        return repositoryAssetOperations.getAssetLockerUserName( uuid );
    }

    @LoggedIn
    public long getAssetCount(AssetPageRequest request) throws SerializationException {
        if ( request == null ) {
            throw new IllegalArgumentException( "request cannot be null" );
        }
        return repositoryAssetOperations.getAssetCount( request );
    }

    private void push(String messageType,
                      String message) {
        backchannel.publish( new PushResponse( messageType,
                                               message ) );
    }

    private ContentHandler getContentHandler(AssetItem repoAsset) {
        return ContentManager.getHandler( repoAsset.getFormat() );
    }

    private String getCurrentUserName() {
        return rulesRepository.getSession().getUserID();
    }

    //The Asset is being effectively deleted from the source package so treat as though the Content is being deleted
    private void attachmentRemoved(AssetItem item) {
        ICanHasAttachment attachmentHandler = null;
        ContentHandler contentHandler = ContentManager.getHandler( item.getFormat() );
        if ( contentHandler instanceof ICanHasAttachment ) {
            attachmentHandler = (ICanHasAttachment) contentHandler;
            try {
                attachmentHandler.onAttachmentRemoved( item );
            } catch ( IOException ioe ) {
                log.error( "Unable to remove asset attachment",
                           ioe );
            }
        }
    }

    //The Asset is being effectively added to the target package so treat as though the Content is being added
    private void attachmentAdded(AssetItem item) {
        ICanHasAttachment attachmentHandler = null;
        ContentHandler contentHandler = ContentManager.getHandler( item.getFormat() );
        if ( contentHandler instanceof ICanHasAttachment ) {
            attachmentHandler = (ICanHasAttachment) contentHandler;
            try {
                attachmentHandler.onAttachmentAdded( item );
            } catch ( IOException ioe ) {
                log.error( "Unable to remove asset attachment",
                           ioe );
            }
        }
    }

    @LoggedIn
    public ConversionResult convertAsset(String uuid,
                                         String targetFormat) throws SerializationException {
        AssetItem item = rulesRepository.loadAssetByUUID( uuid );
        serviceSecurity.checkIsPackageDeveloperOrAnalyst( item );
        return conversionService.convert( item,
                                          targetFormat );
    }

}
