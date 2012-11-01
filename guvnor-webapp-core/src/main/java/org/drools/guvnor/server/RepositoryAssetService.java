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

import com.google.gwt.user.client.rpc.SerializationException;

import org.drools.guvnor.client.rpc.Path;
import org.drools.guvnor.client.rpc.PathImpl;
import org.uberfire.security.annotations.Roles;

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
    protected Backchannel               backchannel;

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
    public Asset loadRuleAsset(Path assetPath) throws SerializationException {    	
        long time = System.currentTimeMillis();

        AssetItem item = rulesRepository.loadAssetByUUID( assetPath.getUUID() );
        Asset asset = new AssetPopulator().populateFrom( item );

        asset.setMetaData( repositoryAssetOperations.populateMetaData( item ) );


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

    public Asset[] loadRuleAssets(Path[] assetPaths) throws SerializationException {
        return loadRuleAssets( Arrays.asList( assetPaths ) );
    }

    public String checkinVersion(Asset asset) throws SerializationException {
        log.info( "USER:" + getCurrentUserName() + " CHECKING IN asset: [" + asset.getName() + "] UUID: [" + asset.getUuid() + "] " );
        return repositoryAssetOperations.checkinVersion( asset );
    }
    public void restoreVersion(Path versionPath,
                               Path assetPath,
                               String comment) {
        repositoryAssetOperations.restoreVersion( versionPath,
        		                                  assetPath,
                                                  comment );
    }

    public TableDataResult loadItemHistory(Path path) throws SerializationException {
        //VersionableItem assetItem = rulesRepository.loadAssetByUUID( uuid );
        VersionableItem assetItem = rulesRepository.loadItemByUUID( path.getUUID() );

        //serviceSecurity.checkSecurityAssetPackagePackageReadOnly( assetItem );
        return repositoryAssetOperations.loadItemHistory( assetItem );
    }

    /**
     * @deprecated in favour of {@link #loadArchivedAssets(PageRequest)}
     */
    public TableDataResult loadAssetHistory(String packageUUID,
                                            String assetName) throws SerializationException {
        ModuleItem pi = rulesRepository.loadModuleByUUID( packageUUID );
        AssetItem assetItem = pi.loadAsset( assetName );

        return repositoryAssetOperations.loadItemHistory( assetItem );
    }

    @Deprecated
    public TableDataResult loadArchivedAssets(int skip,
                                              int numRows) throws SerializationException {
        return repositoryAssetOperations.loadArchivedAssets( skip,
                                                             numRows );
    }

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
    public TableDataResult listAssetsWithPackageName(String packageName,
                                                     String formats[],
                                                     int skip,
                                                     int numRows,
                                                     String tableConfig) throws SerializationException {
        ModuleItem pkg = rulesRepository.loadModule( packageName );
        Path path = new PathImpl();
        path.setUUID(pkg.getUUID());
        return listAssets( path,
                           formats,
                           skip,
                           numRows,
                           tableConfig );
    }

    /**
     * @deprecated in favour of {@link #findAssetPage(AssetPageRequest)}
     */
    public TableDataResult listAssets(Path modulePath,
                                      String formats[],
                                      int skip,
                                      int numRows,
                                      String tableConfig) throws SerializationException {
        log.debug( "Loading asset list for [" + modulePath + "]" );
        if ( numRows == 0 ) {
            throw new DetailedSerializationException( "Unable to return zero results (bug)",
                                                      "probably have the parameters around the wrong way, sigh..." );
        }
        return repositoryAssetOperations.listAssets( modulePath,
                                                     formats,
                                                     skip,
                                                     numRows,
                                                     tableConfig );
    }

    public Path copyAsset(Path assetPath,
                            String newPackage,
                            String newName) {
        log.info( "USER:" + getCurrentUserName() + " COPYING asset: [" + assetPath + "] to [" + newName + "] in PACKAGE [" + newPackage + "]" );
        String copiedAssetUUID = rulesRepository.copyAsset( assetPath.getUUID(),
                                          newPackage,
                                          newName );
        Path path = new PathImpl();
        path.setUUID(copiedAssetUUID);
        return path;
    }

    public void changeAssetPackage(Path assetPath,
                                   String newPackage,
                                   String comment) {
        AssetItem item = rulesRepository.loadAssetByUUID( assetPath.getUUID() );

        //Perform house-keeping for when an Asset is removed from a package
        attachmentRemoved( item );

        log.info( "USER:" + getCurrentUserName() + " CHANGING PACKAGE OF asset: [" + assetPath + "] to [" + newPackage + "]" );
        rulesRepository.moveRuleItemModule( newPackage,
                                            assetPath.getUUID(),
                                            comment );

        //Perform house-keeping for when an Asset is added to a package
        attachmentAdded( item );

    }

    public void promoteAssetToGlobalArea(Path assetPath) {
        AssetItem item = rulesRepository.loadAssetByUUID( assetPath.getUUID() );

        //Perform house-keeping for when an Asset is removed from a module
        attachmentRemoved( item );

        log.info( "USER:" + getCurrentUserName() + " CHANGING MODULE OF asset: [" + assetPath + "] to [ globalArea ]" );
        rulesRepository.moveRuleItemModule( RulesRepository.GLOBAL_AREA,
                                            assetPath.getUUID(),
                                            "promote asset to globalArea" );

        //Perform house-keeping for when an Asset is added to a module
        attachmentAdded( item );

    }

    public String buildAssetSource(Asset asset) throws SerializationException {
        return repositoryAssetOperations.buildAssetSource( asset );
    }

    public Path renameAsset(Path uuid,
                              String newName) {
        AssetItem item = rulesRepository.loadAssetByUUID( uuid.getUUID() );

        return repositoryAssetOperations.renameAsset( uuid,
                                                      newName );
    }

    public void archiveAsset(Path path) {
        archiveOrUnarchiveAsset( path,
                                 true );
    }

    public BuilderResult validateAsset(Asset asset) throws SerializationException {
        return repositoryAssetOperations.validateAsset( asset );
    }

    public void unArchiveAsset(Path path) {
        archiveOrUnarchiveAsset( path,
                                 false );
    }

    public void archiveAssets(Path[] paths,
                              boolean value) {
        for ( Path uuid : paths ) {
            archiveOrUnarchiveAsset( uuid,
                                     value );
        }
    }

    public void removeAsset(Path path) {
        try {
            AssetItem item = rulesRepository.loadAssetByUUID( path.getUUID() );

            item.remove();
            rulesRepository.save();
        } catch ( RulesRepositoryException e ) {
            log.error( "Unable to remove asset.",
                       e );
            throw e;
        }
    }

    public void removeAssets(Path[] paths) {
        for ( Path path : paths ) {
            removeAsset( path );
        }
    }

    public PageResponse<AssetPageRow> findAssetPage(AssetPageRequest request) throws SerializationException {
        if ( request == null ) {
            throw new IllegalArgumentException( "request cannot be null" );
        }
        if ( request.getPageSize() != null && request.getPageSize() < 0 ) {
            throw new IllegalArgumentException( "pageSize cannot be less than zero." );
        }

        return repositoryAssetOperations.findAssetPage( request );
    }

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
    public void lockAsset(Path assetPath) {
        repositoryAssetOperations.lockAsset( assetPath );
    }

    /*
     * (non-Javadoc)
     * @see
     * org.drools.guvnor.client.rpc.RepositoryService#unLockAsset(java.lang.
     * String)
     */
    public void unLockAsset(Path assetPath) {
        repositoryAssetOperations.unLockAsset( assetPath );
    }

    /**
     * @deprecated in favour of
     *             {@link ServiceImplementation#queryFullText(QueryPageRequest)}
     */
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

    Asset[] loadRuleAssets(Collection<Path> paths) throws SerializationException {
        if ( paths == null ) {
            return null;
        }
        Collection<Asset> assets = new HashSet<Asset>();

        for ( Path path : paths ) {
            assets.add( loadRuleAsset( path ) );
        }

        return assets.toArray( new Asset[assets.size()] );
    }

    private void archiveOrUnarchiveAsset(Path path,
                                         boolean archive) {
        AssetItem item = rulesRepository.loadAssetByUUID( path.getUUID() );
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

    public List<DiscussionRecord> addToDiscussionForAsset(Path assetId,
                                                          String comment) {
        return repositoryAssetOperations.addToDiscussionForAsset( assetId,
                                                                  comment );
    }

    @Roles({"ADMIN"})
    public void clearAllDiscussionsForAsset(final Path assetId) {
        repositoryAssetOperations.clearAllDiscussionsForAsset( assetId );
    }

    public List<DiscussionRecord> loadDiscussionForAsset(Path assetPath) {
        return new Discussion().fromString( rulesRepository.loadAssetByUUID( assetPath.getUUID() ).getStringProperty( Discussion.DISCUSSION_PROPERTY_KEY ) );
    }

    /**
     * Role-based Authorization check: This method can be accessed if user has
     * following permissions: 1. The user has a Analyst role and this role has
     * permission to access the category which the asset belongs to. Or. 2. The
     * user has a package.developer role or higher (i.e., package.admin) and
     * this role has permission to access the package which the asset belongs
     * to.
     */
    public void changeState(Path assetPath,
                            String newState) {
        AssetItem asset = rulesRepository.loadAssetByUUID( assetPath.getUUID() );

        log.info( "USER:" + getCurrentUserName() + " CHANGING ASSET STATUS. Asset name, uuid: " + "[" + asset.getName() + ", " + asset.getUUID() + "]" + " to [" + newState + "]" );
        String oldState = asset.getStateDescription();
        asset.updateState( newState );

        push( "statusChange",
                oldState );
        push( "statusChange",
                newState );

        Path path = new PathImpl();
        path.setUUID(asset.getUUID());
        addToDiscussionForAsset( path,
                                 oldState + " -> " + newState );

        rulesRepository.save();
    }

    public void changePackageState(String uuid,
                                   String newState) {
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
    public String getAssetLockerUserName(Path assetPath) {
        return repositoryAssetOperations.getAssetLockerUserName( assetPath );
    }

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

    public ConversionResult convertAsset(Path convertAsset,
                                         String targetFormat) throws SerializationException {
        AssetItem item = rulesRepository.loadAssetByUUID( convertAsset.getUUID() );
        return conversionService.convert( item,
                                          targetFormat );
    }

}
