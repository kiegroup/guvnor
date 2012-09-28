/*
 * Copyright 2010 JBoss Inc
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

package org.drools.guvnor.server.files;

import net.sf.webdav.ITransaction;
import net.sf.webdav.IWebdavStore;
import net.sf.webdav.StoredObject;
import org.apache.commons.io.IOUtils;
import org.drools.guvnor.server.repository.Preferred;
import org.drools.guvnor.server.security.AdminType;
import org.drools.guvnor.server.security.RoleType;
import org.drools.guvnor.server.security.WebDavPackageNameType;
import org.drools.repository.AssetItem;
import org.drools.repository.ModuleItem;
import org.drools.repository.RulesRepository;
import org.drools.repository.VersionableItem;
import org.drools.repository.utils.AssetValidator;
import org.jboss.seam.security.Identity;

import java.io.*;
import java.security.Principal;
import java.util.*;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class WebDAVImpl
        implements
        IWebdavStore {

    private static final String              SNAPSHOTS     = "snapshots";

    private static final String              PACKAGES      = "packages";

    private static final String              GLOBALAREA    = "globalarea";

    /**
     * for the rubbish OSX double data (the ._ rubbish)
     */
    private static final Map<String, byte[]> osxDoubleData = Collections.synchronizedMap( new WeakHashMap<String, byte[]>() );

    // Note that a RulesRepository is @RequestScoped, so there's no need to put it into a thread local
    @Inject @Preferred
    protected RulesRepository                rulesRepository;

    @Inject
    protected Identity                       identity;

    @Inject
    protected AssetValidator                 assetValidator;

    public ITransaction begin(final Principal principal) {
        return new ITransaction() {
            public Principal getPrincipal() {
                return principal;
            }
        };
    }

    public void checkAuthentication(ITransaction arg0) {
        //already done
    }

    public void commit(ITransaction iTransaction) {
        rulesRepository.save();
    }

    public void createFolder(ITransaction iTransaction,
                             String uri) {
        String[] path = getPath( uri );
        if ( isPackages( path ) && isAdmin() ) {
            if ( path.length > 2 ) {
                throw new UnsupportedOperationException( "Can't nest packages." );
            }
            if ( rulesRepository.containsModule( path[1] ) ) {
                ModuleItem pkg = loadPackageFromRepository( path[1] );
                pkg.archiveItem( false );
                pkg.checkin( "restored by webdav" );
            } else {
                rulesRepository.createModule( path[1],
                                              "from webdav" );
            }
        } else {
            throw new UnsupportedOperationException( "Not able to create folders here..." );
        }
    }

    public void createResource(ITransaction iTransaction,
                               String uri) {
        //for mac OSX, ignore these annoying things
        if ( uri.endsWith( ".DS_Store" ) ) return;
        String[] path = getPath( uri );
        if ( isPackages( path ) && checkPackagePermission( path[1],
                                                           RoleType.PACKAGE_ADMIN.getName() ) ) {
            if ( path.length > 3 ) {
                throw new UnsupportedOperationException( "Can't do nested packages." );
            }
            String[] resource = AssetItem.getAssetNameFromFileName( path[2] );
            ModuleItem packageItem = loadPackageFromRepository( path[1] );

            //for mac OSX, ignore these resource fork files
            if ( path[2].startsWith( "._" ) ) {
                WebDAVImpl.osxDoubleData.put( uri,
                                              null );
                return;
            }
            if ( packageItem.containsAsset( resource[0] ) ) {
                AssetItem lazarus = packageItem.loadAsset( resource[0] );
                lazarus.archiveItem( false );
                lazarus.checkin( "restored by webdav" );
            } else {
                AssetItem asset = packageItem.addAsset( resource[0],
                                                        "" );
                asset.updateFormat( resource[1] );
                asset.updateValid(assetValidator.validate(asset));
                asset.checkin( "from webdav" );
            }
        } else if ( isGlobalAreas( path ) ) {
            String[] resource = AssetItem.getAssetNameFromFileName( path[1] );
            ModuleItem packageItem = loadGlobalAreaFromRepository();

            //for mac OSX, ignore these resource fork files
            if ( path[1].startsWith( "._" ) ) {
                WebDAVImpl.osxDoubleData.put( uri,
                                              null );
                return;
            }
            if ( packageItem.containsAsset( resource[0] ) ) {
                AssetItem lazarus = packageItem.loadAsset( resource[0] );
                lazarus.archiveItem( false );
                lazarus.checkin( "restored by webdav" );
            } else {
                AssetItem asset = packageItem.addAsset( resource[0],
                                                        "" );
                asset.updateFormat( resource[1] );
                asset.updateValid(assetValidator.validate(asset));
                asset.checkin( "from webdav" );
            }
        } else {
            throw new UnsupportedOperationException( "Can't add assets here." );
        }
    }

    public String[] getChildrenNames(ITransaction iTransaction,
                                     String uri) {
        String[] path = getPath( uri );
        List<String> result = new ArrayList<String>();
        if ( path.length == 0 ) {
            return new String[]{PACKAGES, SNAPSHOTS, GLOBALAREA};
        }
        if ( isPackages( path ) ) {
            if ( path.length > 2 ) {
                return null;
            }
            if ( path.length == 1 ) {
                listPackages( rulesRepository,
                              result );
            } else if ( checkPackagePermissionIfReadOnly( path ) ) {
                handleReadOnlyPackages( rulesRepository,
                                        path,
                                        result );
            }

        } else if ( isSnaphosts( path ) ) {
            if ( path.length > 3 ) {
                return null;
            }
            if ( path.length == 1 ) {
                listPackages( rulesRepository,
                              result );
            } else if ( isPermission( path,
                                      2 ) ) {
                return rulesRepository.listModuleSnapshots( path[1] );
            } else if ( isPermission( path,
                                      3 ) ) {
                handleReadOnlySnapshotPackages( rulesRepository,
                                                path,
                                                result );
            } else {
                throw new IllegalArgumentException();
            }

        } else if ( isGlobalAreas( path ) ) {
            if ( path.length > 2 ) {
                return null;
            }
            if ( path.length == 1 ) {
                // no packages under global area. show contents
                handleReadOnlyGlobalAreaPackages( rulesRepository,
                                                  path,
                                                  result );
            }
        } else {
            throw new UnsupportedOperationException( "Not a valid path : " + path[0] );
        }
        return result.toArray( new String[result.size()] );
    }

    private void handleReadOnlySnapshotPackages(RulesRepository repository,
                                                String[] path,
                                                List<String> result) {
        Iterator<AssetItem> it = loadPackageSnapshotFromRepository( path ).getAssets();
        while ( it.hasNext() ) {
            AssetItem asset = it.next();
            if ( !asset.isArchived() ) {
                addNameAndFormat( result,
                                  asset );
            }
        }
    }

    private void handleReadOnlyGlobalAreaPackages(RulesRepository repository,
                                                  String[] path,
                                                  List<String> result) {
        Iterator<AssetItem> it = loadGlobalAreaFromRepository().getAssets();
        while ( it.hasNext() ) {
            AssetItem asset = it.next();
            if ( !asset.isArchived() ) {
                addNameAndFormat( result,
                                  asset );
            }
        }
    }

    private void handleReadOnlyPackages(RulesRepository repository,
                                        String[] path,
                                        List<String> result) {
        ModuleItem pkg = loadPackageFromRepository(
                path[1] );
        Iterator<AssetItem> it = pkg.getAssets();
        while ( it.hasNext() ) {
            AssetItem asset = it.next();
            if ( !asset.isArchived() ) {
                addNameAndFormat( result,
                                  asset );
            }
        }
    }

    private void addNameAndFormat(List<String> result,
                                     AssetItem asset) {
        result.add( asset.getName() + "." + asset.getFormat() );
    }

    private void listPackages(RulesRepository repository,
                              List<String> result) {
        Iterator<ModuleItem> it = repository.listModules();
        while ( it.hasNext() ) {
            ModuleItem pkg = it.next();
            String packageName = pkg.getName();
            if ( !pkg.isArchived() && checkPackagePermission( packageName,
                                                              RoleType.PACKAGE_READONLY.getName() ) ) {
                result.add( packageName );
            }
        }
    }

    public Date getCreationDate(String uri) {

        String[] path = getPath( uri );
        if ( path.length < 2 ) {
            return new Date();
        }

        if ( isPackages( path ) && checkPackagePermissionIfReadOnly( path ) ) {
            return getCreationDateForPackage( rulesRepository,
                                              path );
        }

        if ( isSnaphosts( path ) && checkPackagePermissionIfReadOnly( path ) ) {
            return getCreationTimeForSnapshotPackage( rulesRepository,
                                                      path );
        }

        if ( isGlobalAreas( path ) ) {
            return getCreationTimeForGlobalAreaPackage( rulesRepository,
                                                        path );
        }

        throw new UnsupportedOperationException();
    }

    private Date getCreationTimeForSnapshotPackage(RulesRepository repository,
                                                   String[] path) {
        if ( path.length == 2 ) {
            return new Date();
        } else if ( path.length == 3 ) {
            return loadPackageSnapshotFromRepository( path ).getCreatedDate().getTime();
        } else if ( path.length == 4 ) {
            return loadAssetItemFromPackageItem( loadPackageSnapshotFromRepository(
                                                 path ),
                                                 path[3] ).getCreatedDate().getTime();
        }
        throw new UnsupportedOperationException();
    }

    private Date getCreationDateForPackage(RulesRepository repository,
                                           String[] path) {
        ModuleItem packageItem = loadPackageFromRepository( path[1] );
        if ( path.length == 2 ) {
            return packageItem.getCreatedDate().getTime();
        }
        return loadAssetItemFromPackageItem( packageItem,
                                             path[2] ).getCreatedDate().getTime();
    }

    private Date getCreationTimeForGlobalAreaPackage(RulesRepository repository,
                                                     String[] path) {
        ModuleItem packageItem = loadGlobalAreaFromRepository();
        if ( path.length == 2 ) {
            return packageItem.getCreatedDate().getTime();
        }
        return loadAssetItemFromPackageItem( packageItem,
                                             path[2] ).getCreatedDate().getTime();
    }

    public Date getLastModified(String uri) {
        String[] path = getPath( uri );
        if ( path.length < 2 ) {
            return new Date();
        }
        if ( isPackages( path ) && checkPackagePermissionIfReadOnly( path ) ) {
            return getLastModifiedForPackage( rulesRepository,
                                              path );
        }

        if ( isSnaphosts( path ) && checkPackagePermissionIfReadOnly( path ) ) {
            return getLastModifiedForSnaphotPackage( rulesRepository,
                                                     path );
        }

        if ( isGlobalAreas( path ) ) {
            return getLastModifiedForGlobalAreaPackage( rulesRepository,
                                                        path );
        }

        throw new UnsupportedOperationException();
    }

    private Date getLastModifiedForSnaphotPackage(RulesRepository repository,
                                                  String[] path) {
        if ( path.length == 2 ) {
            return new Date();
        } else if ( path.length == 3 ) {
            return loadPackageSnapshotFromRepository( path ).getLastModified().getTime();
        } else if ( path.length == 4 ) {
            ModuleItem pkg = loadPackageSnapshotFromRepository( path );
            return getLastModifiedFromPackageAssetItem( pkg,
                                                        path[3] );
        }
        throw new UnsupportedOperationException();
    }

    private Date getLastModifiedForPackage(RulesRepository repository,
                                           String[] path) {
        ModuleItem pkg = loadPackageFromRepository( path[1] );
        if ( path.length == 2 ) {
            return pkg.getLastModified().getTime();
        }
        return getLastModifiedFromPackageAssetItem( pkg,
                                                    path[2] );

    }

    private Date getLastModifiedForGlobalAreaPackage(RulesRepository repository,
                                                     String[] path) {
        ModuleItem pkg = loadGlobalAreaFromRepository();
        if ( path.length == 2 ) {
            return pkg.getLastModified().getTime();
        }
        return getLastModifiedFromPackageAssetItem( pkg,
                                                    path[2] );

    }

    private Date getLastModifiedFromPackageAssetItem(ModuleItem packageItem,
                                                     String path) {
        return loadAssetItemFromPackageItem( packageItem,
                                             path ).getLastModified().getTime();
    }

    public InputStream getResourceContent(ITransaction iTransaction,
                                          String uri) {
        return getContent( uri );
    }

    public StoredObject getStoredObject(ITransaction iTransaction,
                                        String uri) {
        String[] path = getPath( uri );
        if ( path.length < 2 ) {
            return createStoredObject( uri );
        }

        if ( isPackages( path ) && checkPackagePermissionIfReadOnly( path ) ) {
            return getStoredObjectForReadOnlyPackages( uri,
                                                       rulesRepository,
                                                       path );
        }

        if ( isSnaphosts( path ) && checkPackagePermissionIfReadOnly( path ) ) {
            return getStoredObjectForReadOnlySnapshots( uri,
                                                        rulesRepository,
                                                        path );
        }

        if ( isGlobalAreas( path ) ) {
            return getStoredObjectForReadOnlyGlobalArea( uri,
                                                         rulesRepository,
                                                         path );
        }

        throw new UnsupportedOperationException();
    }

    private StoredObject createStoredObject(String uri) {
        StoredObject so = new StoredObject();
        so.setCreationDate( new Date() );
        so.setFolder( isFolder( uri ) );
        so.setLastModified( new Date() );
        so.setResourceLength( 0 );
        return so;
    }

    private StoredObject getStoredObjectForReadOnlySnapshots(String uri,
                                                             RulesRepository repository,
                                                             String[] path) {
        if ( path.length == 2 ) {
            StoredObject so = createStoredObject( uri,
                                                  loadPackageFromRepository(
                                                  path[1] ),
                                                  0 );
            so.setFolder( isFolder( uri ) );
            return so;
        } else if ( path.length == 3 ) {
            return createStoredObject( uri,
                                       loadPackageSnapshotFromRepository(
                                       path ),
                                       0 );
        } else if ( path.length == 4 ) {
            ModuleItem pkg = loadPackageSnapshotFromRepository( path );
            AssetItem asset;
            try {
                asset = loadAssetItemFromPackageItem( pkg,
                                                      path[3] );
            } catch ( Exception e ) {
                return null;
            }
            return createStoredObject( uri,
                                       asset,
                                       asset.getContentLength() );
        }
        throw new UnsupportedOperationException();

    }

    private StoredObject getStoredObjectForReadOnlyPackages(String uri,
                                                            RulesRepository repository,
                                                            String[] path) {
        ModuleItem packageItem = loadPackageFromRepository( path[1] );
        if ( path.length == 2 ) {
            return createStoredObject( uri,
                                       packageItem,
                                       0 );
        }

        AssetItem asset;
        try {
            asset = loadAssetItemFromPackageItem( packageItem,
                                                  path[2] );
        } catch ( Exception e ) {
            return null;
        }
        return createStoredObject( uri,
                                   asset,
                                   asset.getContentLength() );
    }

    private StoredObject getStoredObjectForReadOnlyGlobalArea(String uri,
                                                              RulesRepository repository,
                                                              String[] path) {
        if ( path.length == 1 ) {
            StoredObject so = createStoredObject( uri,
                                                  loadGlobalAreaFromRepository(),
                                                  0 );
            so.setFolder( isFolder( uri ) );
            return so;
        } else if ( path.length == 2 ) {
            AssetItem asset;
            try {
                asset = loadAssetItemFromGlobalArea( path );
            } catch ( Exception e ) {
                return null;
            }
            return createStoredObject( uri,
                                       asset,
                                       asset.getContentLength() );
        } else if ( path.length == 3 ) {
            AssetItem asset;
            try {
                asset = loadAssetItemFromGlobalArea( path );
            } catch ( Exception e ) {
                return null;
            }
            return createStoredObject( uri,
                                       asset,
                                       asset.getContentLength() );
        }
        throw new UnsupportedOperationException();
    }

    private StoredObject createStoredObject(String uri,
                                            VersionableItem versionableItem,
                                            long resourceLength) {
        StoredObject so = new StoredObject();
        so.setCreationDate( versionableItem.getCreatedDate().getTime() );
        so.setFolder( isFolder( uri ) );
        so.setLastModified( versionableItem.getLastModified().getTime() );
        so.setResourceLength( resourceLength );

        return so;
    }

    private InputStream getContent(String uri) {
        String[] path = getPath( uri );
        if ( isPackages( path ) && checkPackagePermissionIfReadOnly( path ) ) {
            return getAssetData( loadAssetItemFromPackage( path ) );
        }

        if ( isSnaphosts( path ) && checkPackagePermissionIfReadOnly( path ) ) {
            return getAssetData( loadAssetItemFromPackageSnaphot( path ) );
        }

        if ( isGlobalAreas( path ) ) {
            return getAssetData( loadAssetItemFromGlobalArea( path ) );
        }

        throw new UnsupportedOperationException();

    }

    private InputStream getAssetData(AssetItem assetItem) {
        if ( assetItem.isBinary() ) {
            return assetItem.getBinaryContentAttachment();
        }
        return new ByteArrayInputStream( assetItem.getContent().getBytes() );
    }

    public long getResourceLength(ITransaction iTransaction,
                                  String uri) {
        String[] path = getPath( uri );
        try {
            if ( path.length == 3 && isPackages( path ) && checkPackagePermissionIfReadOnly( path ) ) {
                return loadAssetItemFromPackage( path ).getContentLength();
            }

            if ( path.length == 3 && isGlobalAreas( path ) ) {
                return loadAssetItemFromPackage( path ).getContentLength();
            }

            if ( path.length == 4 && isSnaphosts( path ) && checkPackagePermissionIfReadOnly( path ) ) {
                return loadAssetItemFromPackageSnaphot( path ).getContentLength();
            }

            return 0;
        } catch ( Exception e ) {
            System.err.println( "Not able to get content length" );
            return 0;
        }

    }

    boolean isFolder(String uri) {
        String[] path = getPath( uri );
        if ( path.length == 0 ) {
            return true;
        }
        if ( path.length == 1 && (isPackages( path ) || isSnaphosts( path ) || isGlobalAreas( path )) ) {
            return true;
        }

        if ( path.length == 2 ) {
            return rulesRepository.containsModule( path[1] );
        }

        if ( path.length == 3 && isSnaphosts( path ) ) {
            return rulesRepository.containsModule( path[1] );
        }
        return false;
    }

    boolean isResource(String uri) {
        String[] path = getPath( uri );

        if ( path.length < 3 ) {
            return false;
        }
        if ( !(isPackages( path ) || isSnaphosts( path ) || isGlobalAreas( path )) ) {
            return false;
        }

        if ( rulesRepository.containsModule( path[1] ) ) {
            if ( isPackages( path ) ) {
                ModuleItem pkg = loadPackageFromRepository( path[1] );
                if ( path[2].startsWith( "._" ) ) {
                    return osxDoubleData.containsKey( uri );
                }
                return pkg.containsAsset( AssetItem.getAssetNameFromFileName( path[2] )[0] );
            }

            if ( path.length == 4 ) {
                return isAssetItemInPackage( rulesRepository,
                                             path );
            }
            return false;
        }
        return false;
    }

    boolean objectExists(String uri) {
        if ( uri.indexOf( " copy " ) > 0 ) {
            throw new IllegalArgumentException( "OSX is not capable of copy and pasting without breaking the file extension." );
        }
        return internalObjectExists( uri );
    }

    private boolean internalObjectExists(String uri) {
        if ( uri.endsWith( ".DS_Store" ) ) {
            return false;
        }
        String[] path = getPath( uri );

        if ( path.length == 0 || (path.length == 1 && (isPackages( path ) || isSnaphosts( path ) || isGlobalAreas( path ))) ) {
            return true;
        }

        if ( path.length == 1 || !rulesRepository.containsModule( path[1] ) ) {
            return false;
        }

        if ( isPackages( path ) ) {
            return handlePackagesInternalObjectExists( uri,
                                                       rulesRepository,
                                                       path );
        }

        if ( isSnaphosts( path ) ) {
            return handleSnapshotsInternalObjectExists( rulesRepository,
                                                        path );
        }

        if ( isGlobalAreas( path ) ) {
            return handlePackagesInternalObjectExists( uri,
                                                       rulesRepository,
                                                       path );
        }

        throw new IllegalStateException();
    }

    private boolean handleSnapshotsInternalObjectExists(RulesRepository repository,
                                                        String[] path) {
        if ( path.length == 2 ) {
            return repository.containsModule( path[1] );
        }

        if ( path.length == 3 ) {
            return repository.containsSnapshot( path[1],
                                                path[2] );
        }

        if ( path.length == 4 ) {
            return isAssetItemInPackage( repository,
                                         path );
        }
        return false;

    }

    private boolean handlePackagesInternalObjectExists(String uri,
                                                       RulesRepository repository,
                                                       String[] path) {
        if ( path.length == 2 ) {
            ModuleItem pkg = loadPackageFromRepository( path[1] );
            return !pkg.isArchived();
        }
        ModuleItem pkg = loadPackageFromRepository( path[1] );
        if ( path[2].startsWith( "._" ) ) {
            return WebDAVImpl.osxDoubleData.containsKey( uri );
        }
        String assetName = AssetItem.getAssetNameFromFileName( path[2] )[0];
        return pkg.containsAsset( assetName ) && !pkg.loadAsset( assetName ).isArchived();
    }

    public void removeObject(ITransaction iTransaction,
                             String uri) {
        String[] path = getPath( uri );
        if ( path.length == 0 || path.length == 1 ) {
            throw new IllegalArgumentException();
        }
        if ( isPackages( path ) && checkPackagePermissionIfDeveloper( path ) ) {
            ModuleItem packageItem = loadPackageFromRepository( path[1] );
            if ( path.length == 3 ) {
                //delete asset
                if ( path[2].startsWith( "._" ) ) {
                    WebDAVImpl.osxDoubleData.remove( uri );
                    return;
                }
                AssetItem item = loadAssetItemFromPackageItem( packageItem,
                                                               path[2] );
                item.archiveItem( true );
                item.checkin( "" );
            } else {
                //delete package
                packageItem.archiveItem( true );
                packageItem.checkin( "" );
            }
        } else if ( isGlobalAreas( path ) ) {
            if ( path.length == 2 ) {
                //delete asset
                if ( path[1].startsWith( "._" ) ) {
                    WebDAVImpl.osxDoubleData.remove( uri );
                    return;
                }
                AssetItem item = loadAssetItemFromGlobalArea( path );
                item.archiveItem( true );
                item.checkin( "" );
            }
        } else {
            throw new IllegalArgumentException( "Not allowed to remove this file." );
        }

    }

    public void rollback(ITransaction iTransaction) {
        rulesRepository.getSession().logout();
    }

    public long setResourceContent(ITransaction iTransaction,
                                   String uri,
                                   InputStream content,
                                   String contentType,
                                   String characterEncoding) {
        if ( uri.endsWith( ".DS_Store" ) ) {
            return 0;
        }
        String[] path = getPath( uri );
        if ( isPackages( path ) && checkPackagePermissionIfDeveloper( path ) ) {
            if ( path.length != 3 ) {
                throw new IllegalArgumentException( "Not a valid resource path " + uri );
            }

            if ( path[2].startsWith( "._" ) ) {
                try {
                    WebDAVImpl.osxDoubleData.put( uri,
                                                  IOUtils.toByteArray( content ) );
                } catch ( IOException e ) {
                    throw new RuntimeException( e );
                }
                return 0;
            }
            AssetItem asset = loadAssetItemFromPackage( path );
            if ( asset.getFormat().equals( "drl" ) ) {
                try {
                    BufferedReader reader = new BufferedReader( new InputStreamReader( content ) );
                    StringBuilder sb = new StringBuilder();
                    String line = null;
                    while ( (line = reader.readLine()) != null ) {
                        if ( !line.startsWith( "package " ) ) {
                            sb.append( line ).append( "\n" );
                        }
                    }
                    asset.updateBinaryContentAttachment( new ByteArrayInputStream( sb.toString().getBytes( "UTF-8" ) ) );
                } catch ( Exception e ) {
                    //default
                    asset.updateBinaryContentAttachment( content );
                }
            } else {
                asset.updateBinaryContentAttachment( content );
            }
            //here we could save, or check in, depending on if enough time has passed to justify
            //a new version. Otherwise we will pollute the version history with lots of trivial versions.
            //if (shouldCreateNewVersion(asset.getLastModified())) {
            asset.updateValid(assetValidator.validate(asset));
            asset.checkin( "content from webdav" );
            //}
        } else if ( isGlobalAreas( path ) ) {
            if ( path[1].startsWith( "._" ) ) {
                try {
                    WebDAVImpl.osxDoubleData.put( uri,
                                                  IOUtils.toByteArray( content ) );
                } catch ( IOException e ) {
                    throw new RuntimeException( e );
                }
                return 0;
            }
            AssetItem asset = loadAssetItemFromGlobalArea( path );
            if ( asset.getFormat().equals( "drl" ) ) {
                try {
                    BufferedReader reader = new BufferedReader( new InputStreamReader( content ) );
                    StringBuilder sb = new StringBuilder();
                    String line = null;
                    while ( (line = reader.readLine()) != null ) {
                        if ( !line.startsWith( "package " ) ) {
                            sb.append( line ).append( "\n" );
                        }
                    }
                    asset.updateBinaryContentAttachment( new ByteArrayInputStream( sb.toString().getBytes( "UTF-8" ) ) );
                } catch ( Exception e ) {
                    //default
                    asset.updateBinaryContentAttachment( content );
                }
            } else {
                asset.updateBinaryContentAttachment( content );
            }
            //here we could save, or check in, depending on if enough time has passed to justify
            //a new version. Otherwise we will pollute the version history with lots of trivial versions.
            //if (shouldCreateNewVersion(asset.getLastModified())) {
            asset.updateValid(assetValidator.validate(asset));
            asset.checkin( "content from webdav" );
            //}
        } else {
            throw new UnsupportedOperationException( "Unable to save content to this location." );
        }

        return 0;
    }

    //REVISIT: We should never reach this code which is using webdav as regex,
    //i.e., input uri is sth like /webdav/packages/mypackage
    String[] getPath(String uri) {
        //        if (beanManagerLocator.isBeanManagerAvailable()) {
        return getPath( uri,
                        false );
        //        } else {
        //            return getPath(uri,
        //                    true);
        //        }
    }

    String[] getPath(String uri,
                     boolean usingWebdavAsRegex) {
        if ( uri.equals( "/" ) ) {
            return new String[0];
        }

        if ( usingWebdavAsRegex ) {
            if ( uri.endsWith( "webdav" ) || uri.endsWith( "webdav/" ) ) {
                return new String[0];
            }
            if ( uri.contains( "webdav/" ) ) {
                return uri.split( "webdav/",
                                  2 )[1].split( "/" );
            }
        }

        return uri.substring( 1 ).split( "/" );
    }

    private boolean isAdmin() {
        return identity.hasPermission( new AdminType(),
                                       RoleType.ADMIN.getName() );
    }

    private boolean checkPackagePermission(String packageName,
                                           String type) {
        return identity.hasPermission( new WebDavPackageNameType( packageName ),
                                       type );
    }

    private AssetItem loadAssetItemFromPackage(String[] path) {
        return loadAssetItemFromPackageItem( loadPackageFromRepository(
                                             path[1] ),
                                             path[2] );
    }

    private AssetItem loadAssetItemFromPackageSnaphot(String[] path) {
        return loadAssetItemFromPackageItem( loadPackageSnapshotFromRepository(
                                             path ),
                                             path[3] );
    }

    private AssetItem loadAssetItemFromGlobalArea(String[] path) {
        return loadAssetItemFromPackageItem( loadGlobalAreaFromRepository(),
                                             path[1] );
    }

    private AssetItem loadAssetItemFromPackageItem(ModuleItem pkg,
                                                   String path) {
        return pkg.loadAsset( AssetItem.getAssetNameFromFileName( path )[0] );
    }

    private boolean isAssetItemInPackage(RulesRepository repository,
                                         String[] path) {
        return loadPackageSnapshotFromRepository( path ).containsAsset( AssetItem.getAssetNameFromFileName( path[3] )[0] );
    }

    private ModuleItem loadPackageFromRepository(String path) {
        return rulesRepository.loadModule( path );
    }

    private ModuleItem loadPackageSnapshotFromRepository(String[] path) {
        return rulesRepository.loadModuleSnapshot( path[1],
                                                   path[2] );
    }

    private ModuleItem loadGlobalAreaFromRepository() {
        return rulesRepository.loadGlobalArea();
    }

    private boolean isPermission(String[] path,
                                 int pathIndex) {
        return path.length == pathIndex && checkPackagePermissionIfReadOnly( path );
    }

    private boolean checkPackagePermissionIfReadOnly(String[] path) {
        return checkPackagePermission( path[1],
                                       RoleType.PACKAGE_READONLY.getName() );
    }

    private boolean checkPackagePermissionIfDeveloper(String[] path) {
        return checkPackagePermission( path[1],
                                       RoleType.PACKAGE_DEVELOPER.getName() );
    }

    private boolean isPackages(String[] path) {
        return path[0].equals( PACKAGES );
    }

    private boolean isSnaphosts(String[] path) {
        return path[0].equals( SNAPSHOTS );
    }

    private boolean isGlobalAreas(String[] path) {
        return path[0].equals( GLOBALAREA );
    }

}
