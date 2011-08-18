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
import org.drools.guvnor.server.security.AdminType;
import org.drools.guvnor.server.security.RoleType;
import org.drools.guvnor.server.security.WebDavPackageNameType;
import org.drools.repository.AssetItem;
import org.drools.repository.PackageItem;
import org.drools.repository.RulesRepository;
import org.drools.repository.VersionableItem;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.security.Identity;

import java.io.*;
import java.security.Principal;
import java.util.*;

public class WebDAVImpl
        implements
        IWebdavStore {

    private static final String SNAPSHOTS = "snapshots";

    private static final String PACKAGES = "packages";

    private static final String GLOBALAREA = "globalarea";

    /**
     * for the rubbish OSX double data (the ._ rubbish)
     */
    private static final Map<String, byte[]> osxDoubleData = Collections.synchronizedMap(new WeakHashMap<String, byte[]>());

    private final ThreadLocal<RulesRepository> tlRepo = new ThreadLocal<RulesRepository>();

    public WebDAVImpl(File f) {

    }

    public WebDAVImpl() {
    }

    public WebDAVImpl(RulesRepository testRepo) {
        tlRepo.set(testRepo);
    }

    RulesRepository getRepo() {
        return tlRepo.get();
    }

    public ITransaction begin(final Principal principal) {
        tlRepo.set(RestAPIServlet.getRepository());

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
        getRepo().save();
        tlRepo.set(null);
    }

    public void createFolder(ITransaction iTransaction,
                             String uri) {
        String[] path = getPath(uri);
        if (isPackages(path) && isAdmin()) {
            if (path.length > 2) {
                throw new UnsupportedOperationException("Can't nest packages.");
            }
            RulesRepository repository = getRepo();
            if (repository.containsPackage(path[1])) {
                PackageItem pkg = loadPackageFromRepository(repository,
                        path[1]);
                pkg.archiveItem(false);
                pkg.checkin("<restored by webdav>");
            } else {
                repository.createPackage(path[1],
                        "<from webdav>");
            }
        } else {
            throw new UnsupportedOperationException("Not able to create folders here...");
        }
    }

    public void createResource(ITransaction iTransaction,
                               String uri) {
        //for mac OSX, ignore these annoying things
        if (uri.endsWith(".DS_Store")) return;
        String[] path = getPath(uri);
        if (isPackages(path) && checkPackagePermission(path[1],
                RoleType.PACKAGE_ADMIN.getName())) {
            if (path.length > 3) {
                throw new UnsupportedOperationException("Can't do nested packages.");
            }
            String[] resource = AssetItem.getAssetNameFromFileName(path[2]);
            PackageItem packageItem = loadPackageFromRepository(getRepo(),
                    path[1]);

            //for mac OSX, ignore these resource fork files
            if (path[2].startsWith("._")) {
                WebDAVImpl.osxDoubleData.put(uri,
                        null);
                return;
            }
            if (packageItem.containsAsset(resource[0])) {
                AssetItem lazarus = packageItem.loadAsset(resource[0]);
                lazarus.archiveItem(false);
            } else {
                AssetItem asset = packageItem.addAsset(resource[0],
                        "");
                asset.updateFormat(resource[1]);
            }
        } else if (isGlobalAreas(path)) {
            String[] resource = AssetItem.getAssetNameFromFileName(path[1]);
            PackageItem packageItem = loadGlobalAreaFromRepository(getRepo());

            //for mac OSX, ignore these resource fork files
            if (path[1].startsWith("._")) {
                WebDAVImpl.osxDoubleData.put(uri,
                        null);
                return;
            }
            if (packageItem.containsAsset(resource[0])) {
                AssetItem lazarus = packageItem.loadAsset(resource[0]);
                lazarus.archiveItem(false);
            } else {
                AssetItem asset = packageItem.addAsset(resource[0],
                        "");
                asset.updateFormat(resource[1]);
            }
        } else {
            throw new UnsupportedOperationException("Can't add assets here.");
        }
    }

    public String[] getChildrenNames(ITransaction iTransaction,
                                     String uri) {
        RulesRepository repository = getRepo();
        String[] path = getPath(uri);
        List<String> result = new ArrayList<String>();
        if (path.length == 0) {
            return new String[]{PACKAGES, SNAPSHOTS, GLOBALAREA};
        }
        if (isPackages(path)) {
            if (path.length > 2) {
                return null;
            }
            if (path.length == 1) {
                listPackages(repository,
                        result);
            } else if (checkPackagePermissionIfReadOnly(path)) {
                handleReadOnlyPackages(repository,
                        path,
                        result);
            }

        } else if (isSnaphosts(path)) {
            if (path.length > 3) {
                return null;
            }
            if (path.length == 1) {
                listPackages(repository,
                        result);
            } else if (isPermission(path,
                    2)) {
                return repository.listPackageSnapshots(path[1]);
            } else if (isPermission(path,
                    3)) {
                handleReadOnlySnapshotPackages(repository,
                        path,
                        result);
            } else {
                throw new IllegalArgumentException();
            }

        } else if (isGlobalAreas(path)) {
            if (path.length > 2) {
                return null;
            }
            if (path.length == 1) {
                // no packages under global area. show contents
                handleReadOnlyGlobalAreaPackages(repository,
                        path,
                        result);
            }
        } else {
            throw new UnsupportedOperationException("Not a valid path : " + path[0]);
        }
        return result.toArray(new String[result.size()]);
    }

    private void handleReadOnlySnapshotPackages(RulesRepository repository,
                                                String[] path,
                                                List<String> result) {
        Iterator<AssetItem> it = loadPackageSnapshotFromRepository(repository,
                path).getAssets();
        while (it.hasNext()) {
            AssetItem asset = it.next();
            if (!asset.isArchived()) {
                addNameAndFormat(result,
                        asset);
            }
        }
    }

    private void handleReadOnlyGlobalAreaPackages(RulesRepository repository,
                                                  String[] path,
                                                  List<String> result) {
        Iterator<AssetItem> it = loadGlobalAreaFromRepository(repository).getAssets();
        while (it.hasNext()) {
            AssetItem asset = it.next();
            if (!asset.isArchived()) {
                addNameAndFormat(result,
                        asset);
            }
        }
    }

    private void handleReadOnlyPackages(RulesRepository repository,
                                        String[] path,
                                        List<String> result) {
        PackageItem pkg = loadPackageFromRepository(repository,
                path[1]);
        Iterator<AssetItem> it = pkg.getAssets();
        while (it.hasNext()) {
            AssetItem asset = it.next();
            if (!asset.isArchived()) {
                addNameAndFormat(result,
                        asset);
            }
        }
    }

    private boolean addNameAndFormat(List<String> result,
                                     AssetItem asset) {
        return result.add(asset.getName() + "." + asset.getFormat());
    }

    private void listPackages(RulesRepository repository,
                              List<String> result) {
        Iterator<PackageItem> it = repository.listPackages();
        while (it.hasNext()) {
            PackageItem pkg = it.next();
            String packageName = pkg.getName();
            if (!pkg.isArchived() && checkPackagePermission(packageName,
                    RoleType.PACKAGE_READONLY.getName())) {
                result.add(packageName);
            }
        }
    }

    public Date getCreationDate(String uri) {

        RulesRepository repository = getRepo();
        String[] path = getPath(uri);
        if (path.length < 2) {
            return new Date();
        }

        if (isPackages(path) && checkPackagePermissionIfReadOnly(path)) {
            return getCreationDateForPackage(repository,
                    path);
        }

        if (isSnaphosts(path) && checkPackagePermissionIfReadOnly(path)) {
            return getCreationTimeForSnapshotPackage(repository,
                    path);
        }

        if (isGlobalAreas(path)) {
            return getCreationTimeForGlobalAreaPackage(repository,
                    path);
        }

        throw new UnsupportedOperationException();
    }

    private Date getCreationTimeForSnapshotPackage(RulesRepository repository,
                                                   String[] path) {
        if (path.length == 2) {
            return new Date();
        } else if (path.length == 3) {
            return loadPackageSnapshotFromRepository(repository,
                    path).getCreatedDate().getTime();
        } else if (path.length == 4) {
            return loadAssetItemFromPackageItem(loadPackageSnapshotFromRepository(repository,
                    path),
                    path[3]).getCreatedDate().getTime();
        }
        throw new UnsupportedOperationException();
    }

    private Date getCreationDateForPackage(RulesRepository repository,
                                           String[] path) {
        PackageItem packageItem = loadPackageFromRepository(repository,
                path[1]);
        if (path.length == 2) {
            return packageItem.getCreatedDate().getTime();
        }
        return loadAssetItemFromPackageItem(packageItem,
                path[2]).getCreatedDate().getTime();
    }

    private Date getCreationTimeForGlobalAreaPackage(RulesRepository repository,
                                                     String[] path) {
        PackageItem packageItem = loadGlobalAreaFromRepository(repository);
        if (path.length == 2) {
            return packageItem.getCreatedDate().getTime();
        }
        return loadAssetItemFromPackageItem(packageItem,
                path[2]).getCreatedDate().getTime();
    }

    public Date getLastModified(String uri) {
        RulesRepository repository = getRepo();
        String[] path = getPath(uri);
        if (path.length < 2) {
            return new Date();
        }
        if (isPackages(path) && checkPackagePermissionIfReadOnly(path)) {
            return getLastModifiedForPackage(repository,
                    path);
        }

        if (isSnaphosts(path) && checkPackagePermissionIfReadOnly(path)) {
            return getLastModifiedForSnaphotPackage(repository,
                    path);
        }

        if (isGlobalAreas(path)) {
            return getLastModifiedForGlobalAreaPackage(repository,
                    path);
        }

        throw new UnsupportedOperationException();
    }

    private Date getLastModifiedForSnaphotPackage(RulesRepository repository,
                                                  String[] path) {
        if (path.length == 2) {
            return new Date();
        } else if (path.length == 3) {
            return loadPackageSnapshotFromRepository(repository,
                    path).getLastModified().getTime();
        } else if (path.length == 4) {
            PackageItem pkg = loadPackageSnapshotFromRepository(repository,
                    path);
            return getLastModifiedFromPackageAssetItem(pkg,
                    path[3]);
        }
        throw new UnsupportedOperationException();
    }

    private Date getLastModifiedForPackage(RulesRepository repository,
                                           String[] path) {
        PackageItem pkg = loadPackageFromRepository(repository,
                path[1]);
        if (path.length == 2) {
            return pkg.getLastModified().getTime();
        }
        return getLastModifiedFromPackageAssetItem(pkg,
                path[2]);

    }

    private Date getLastModifiedForGlobalAreaPackage(RulesRepository repository,
                                                     String[] path) {
        PackageItem pkg = loadGlobalAreaFromRepository(repository);
        if (path.length == 2) {
            return pkg.getLastModified().getTime();
        }
        return getLastModifiedFromPackageAssetItem(pkg,
                path[2]);

    }

    private Date getLastModifiedFromPackageAssetItem(PackageItem packageItem,
                                                     String path) {
        return loadAssetItemFromPackageItem(packageItem,
                path).getLastModified().getTime();
    }

    public InputStream getResourceContent(ITransaction iTransaction,
                                          String uri) {
        return getContent(uri);
    }

    public StoredObject getStoredObject(ITransaction iTransaction,
                                        String uri) {
        try {
            RulesRepository repository = getRepo();
            String[] path = getPath(uri);
            if (path.length < 2) {
                return createStoredObject(uri);
            }

            if (isPackages(path) && checkPackagePermissionIfReadOnly(path)) {
                return getStoredObjectForReadOnlyPackages(uri,
                        repository,
                        path);
            }

            if (isSnaphosts(path) && checkPackagePermissionIfReadOnly(path)) {
                return getStoredObjectForReadOnlySnapshots(uri,
                        repository,
                        path);
            }

            if (isGlobalAreas(path)) {
                return getStoredObjectForReadOnlyGlobalArea(uri,
                        repository,
                        path);
            }

            throw new UnsupportedOperationException();
        } catch (Exception e) {
            throw new UnsupportedOperationException(e.getMessage());
        }
    }

    private StoredObject createStoredObject(String uri) {
        StoredObject so = new StoredObject();
        so.setCreationDate(new Date());
        so.setFolder(isFolder(uri));
        so.setLastModified(new Date());
        so.setResourceLength(0);
        return so;
    }

    private StoredObject getStoredObjectForReadOnlySnapshots(String uri,
                                                             RulesRepository repository,
                                                             String[] path) {
        if (path.length == 2) {
            StoredObject so = createStoredObject(uri,
                    loadPackageFromRepository(repository,
                            path[1]),
                    0);
            so.setFolder(isFolder(uri));
            return so;
        } else if (path.length == 3) {
            return createStoredObject(uri,
                    loadPackageSnapshotFromRepository(repository,
                            path),
                    0);
        } else if (path.length == 4) {
            PackageItem pkg = loadPackageSnapshotFromRepository(repository,
                    path);
            AssetItem asset;
            try {
                asset = loadAssetItemFromPackageItem(pkg,
                        path[3]);
            } catch (Exception e) {
                return null;
            }
            return createStoredObject(uri,
                    asset,
                    asset.getContentLength());
        }
        throw new UnsupportedOperationException();

    }

    private StoredObject getStoredObjectForReadOnlyPackages(String uri,
                                                            RulesRepository repository,
                                                            String[] path) {
        PackageItem packageItem = loadPackageFromRepository(repository,
                path[1]);
        if (path.length == 2) {
            return createStoredObject(uri,
                    packageItem,
                    0);
        }

        AssetItem asset;
        try {
            asset = loadAssetItemFromPackageItem(packageItem,
                    path[2]);
        } catch (Exception e) {
            return null;
        }
        return createStoredObject(uri,
                asset,
                asset.getContentLength());
    }

    private StoredObject getStoredObjectForReadOnlyGlobalArea(String uri,
                                                              RulesRepository repository,
                                                              String[] path) {
        if (path.length == 1) {
            StoredObject so = createStoredObject(uri,
                    loadGlobalAreaFromRepository(repository),
                    0);
            so.setFolder(isFolder(uri));
            return so;
        } else if (path.length == 2) {
            AssetItem asset;
            try {
                asset = loadAssetItemFromGlobalArea(repository,
                        path);
            } catch (Exception e) {
                return null;
            }
            return createStoredObject(uri,
                    asset,
                    asset.getContentLength());
        } else if (path.length == 3) {
            AssetItem asset;
            try {
                asset = loadAssetItemFromGlobalArea(repository,
                        path);
            } catch (Exception e) {
                return null;
            }
            return createStoredObject(uri,
                    asset,
                    asset.getContentLength());
        }
        throw new UnsupportedOperationException();
    }

    private StoredObject createStoredObject(String uri,
                                            VersionableItem versionableItem,
                                            long resourceLength) {
        StoredObject so = new StoredObject();
        so.setCreationDate(versionableItem.getCreatedDate().getTime());
        so.setFolder(isFolder(uri));
        so.setLastModified(versionableItem.getLastModified().getTime());
        so.setResourceLength(resourceLength);

        return so;
    }

    private InputStream getContent(String uri) {
        RulesRepository repository = getRepo();
        String[] path = getPath(uri);
        if (isPackages(path) && checkPackagePermissionIfReadOnly(path)) {
            return getAssetData(loadAssetItemFromPackage(repository,
                    path));
        }

        if (isSnaphosts(path) && checkPackagePermissionIfReadOnly(path)) {
            return getAssetData(loadAssetItemFromPackageSnaphot(repository,
                    path));
        }

        if (isGlobalAreas(path)) {
            return getAssetData(loadAssetItemFromGlobalArea(repository,
                    path));
        }

        throw new UnsupportedOperationException();

    }

    private InputStream getAssetData(AssetItem assetItem) {
        if (assetItem.isBinary()) {
            return assetItem.getBinaryContentAttachment();
        }
        return new ByteArrayInputStream(assetItem.getContent().getBytes());
    }

    public long getResourceLength(ITransaction iTransaction,
                                  String uri) {
        String[] path = getPath(uri);
        try {
            RulesRepository repository = getRepo();
            if (path.length == 3 && isPackages(path) && checkPackagePermissionIfReadOnly(path)) {
                return loadAssetItemFromPackage(repository,
                        path).getContentLength();
            }

            if (path.length == 3 && isGlobalAreas(path)) {
                return loadAssetItemFromPackage(repository,
                        path).getContentLength();
            }

            if (path.length == 4 && isSnaphosts(path) && checkPackagePermissionIfReadOnly(path)) {
                return loadAssetItemFromPackageSnaphot(repository,
                        path).getContentLength();
            }

            return 0;
        } catch (Exception e) {
            System.err.println("Not able to get content length");
            return 0;
        }

    }

    boolean isFolder(String uri) {
        RulesRepository repository = getRepo();
        String[] path = getPath(uri);
        if (path.length == 0) {
            return true;
        }
        if (path.length == 1 && (isPackages(path) || isSnaphosts(path) || isGlobalAreas(path))) {
            return true;
        }

        if (path.length == 2) {
            return repository.containsPackage(path[1]);
        }

        if (path.length == 3 && isSnaphosts(path)) {
            return repository.containsPackage(path[1]);
        }
        return false;
    }

    boolean isResource(String uri) {
        RulesRepository repository = getRepo();
        String[] path = getPath(uri);

        if (path.length < 3) {
            return false;
        }
        if (!(isPackages(path) || isSnaphosts(path) || isGlobalAreas(path))) {
            return false;
        }

        if (repository.containsPackage(path[1])) {
            if (isPackages(path)) {
                PackageItem pkg = loadPackageFromRepository(repository,
                        path[1]);
                if (path[2].startsWith("._")) {
                    return osxDoubleData.containsKey(uri);
                }
                return pkg.containsAsset(AssetItem.getAssetNameFromFileName(path[2])[0]);
            }

            if (path.length == 4) {
                return isAssetItemInPackage(repository,
                        path);
            }
            return false;
        }
        return false;
    }

    boolean objectExists(String uri) {
        if (uri.indexOf(" copy ") > 0) {
            throw new IllegalArgumentException("OSX is not capable of copy and pasting without breaking the file extension.");
        }
        return internalObjectExists(uri);
    }

    private boolean internalObjectExists(String uri) {
        RulesRepository repository = getRepo();
        if (uri.endsWith(".DS_Store")) {
            return false;
        }
        String[] path = getPath(uri);

        if (path.length == 0 || (path.length == 1 && (isPackages(path) || isSnaphosts(path) || isGlobalAreas(path)))) {
            return true;
        }

        if (path.length == 1 || !repository.containsPackage(path[1])) {
            return false;
        }

        if (isPackages(path)) {
            return handlePackagesInternalObjectExists(uri,
                    repository,
                    path);
        }

        if (isSnaphosts(path)) {
            return handleSnapshotsInternalObjectExists(repository,
                    path);
        }

        if (isGlobalAreas(path)) {
            return handlePackagesInternalObjectExists(uri,
                    repository,
                    path);
        }

        throw new IllegalStateException();
    }

    private boolean handleSnapshotsInternalObjectExists(RulesRepository repository,
                                                        String[] path) {
        if (path.length == 2) {
            return repository.containsPackage(path[1]);
        }

        if (path.length == 3) {
            return repository.containsSnapshot(path[1],
                    path[2]);
        }

        if (path.length == 4) {
            return isAssetItemInPackage(repository,
                    path);
        }
        return false;

    }

    private boolean handlePackagesInternalObjectExists(String uri,
                                                       RulesRepository repository,
                                                       String[] path) {
        if (path.length == 2) {
            PackageItem pkg = loadPackageFromRepository(repository,
                    path[1]);
            return !pkg.isArchived();
        }
        PackageItem pkg = loadPackageFromRepository(repository,
                path[1]);
        if (path[2].startsWith("._")) {
            return WebDAVImpl.osxDoubleData.containsKey(uri);
        }
        String assetName = AssetItem.getAssetNameFromFileName(path[2])[0];
        return pkg.containsAsset(assetName) && !pkg.loadAsset(assetName).isArchived();
    }

    public void removeObject(ITransaction iTransaction,
                             String uri) {
        RulesRepository repository = getRepo();
        String[] path = getPath(uri);
        if (path.length == 0 || path.length == 1) {
            throw new IllegalArgumentException();
        }
        if (isPackages(path) && checkPackagePermissionIfDeveloper(path)) {
            PackageItem packageItem = loadPackageFromRepository(repository,
                    path[1]);
            if (path.length == 3) {
                //delete asset
                if (path[2].startsWith("._")) {
                    WebDAVImpl.osxDoubleData.remove(uri);
                    return;
                }
                AssetItem item = loadAssetItemFromPackageItem(packageItem,
                        path[2]);
                item.archiveItem(true);
                item.checkin("");
            } else {
                //delete package
                packageItem.archiveItem(true);
                packageItem.checkin("");
            }
        } else if (isGlobalAreas(path)) {
            if (path.length == 2) {
                //delete asset
                if (path[1].startsWith("._")) {
                    WebDAVImpl.osxDoubleData.remove(uri);
                    return;
                }
                AssetItem item = loadAssetItemFromGlobalArea(repository,
                        path);
                item.archiveItem(true);
                item.checkin("");
            }
        } else {
            throw new IllegalArgumentException("Not allowed to remove this file.");
        }

    }

    public void rollback(ITransaction iTransaction) {
        RulesRepository repository = getRepo();
        repository.getSession().logout();
    }

    public long setResourceContent(ITransaction iTransaction,
                                   String uri,
                                   InputStream content,
                                   String contentType,
                                   String characterEncoding) {
        RulesRepository repository = getRepo();
        if (uri.endsWith(".DS_Store")) {
            return 0;
        }
        String[] path = getPath(uri);
        if (isPackages(path) && checkPackagePermissionIfDeveloper(path)) {
            if (path.length != 3) {
                throw new IllegalArgumentException("Not a valid resource path " + uri);
            }

            if (path[2].startsWith("._")) {
                try {
                    WebDAVImpl.osxDoubleData.put(uri,
                            IOUtils.toByteArray(content));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                return 0;
            }
            AssetItem asset = loadAssetItemFromPackage(repository,
                    path);
            if (asset.getFormat().equals("drl")) {
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                    StringBuilder sb = new StringBuilder();
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        if (!line.startsWith("package ")) {
                            sb.append(line).append("\n");
                        }
                    }
                    asset.updateBinaryContentAttachment(new ByteArrayInputStream(sb.toString().getBytes("UTF-8")));
                } catch (Exception e) {
                    //default
                    asset.updateBinaryContentAttachment(content);
                }
            } else {
                asset.updateBinaryContentAttachment(content);
            }
            //here we could save, or check in, depending on if enough time has passed to justify
            //a new version. Otherwise we will pollute the version history with lots of trivial versions.
            //if (shouldCreateNewVersion(asset.getLastModified())) {
            asset.checkin("<content from webdav>");
            //}
        } else if (isGlobalAreas(path)) {
            if (path[1].startsWith("._")) {
                try {
                    WebDAVImpl.osxDoubleData.put(uri,
                            IOUtils.toByteArray(content));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                return 0;
            }
            AssetItem asset = loadAssetItemFromGlobalArea(repository,
                    path);
            if (asset.getFormat().equals("drl")) {
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                    StringBuilder sb = new StringBuilder();
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        if (!line.startsWith("package ")) {
                            sb.append(line).append("\n");
                        }
                    }
                    asset.updateBinaryContentAttachment(new ByteArrayInputStream(sb.toString().getBytes("UTF-8")));
                } catch (Exception e) {
                    //default
                    asset.updateBinaryContentAttachment(content);
                }
            } else {
                asset.updateBinaryContentAttachment(content);
            }
            //here we could save, or check in, depending on if enough time has passed to justify
            //a new version. Otherwise we will pollute the version history with lots of trivial versions.
            //if (shouldCreateNewVersion(asset.getLastModified())) {
            asset.checkin("<content from webdav>");
            //}
        } else {
            throw new UnsupportedOperationException("Unable to save content to this location.");
        }

        return 0;
    }

    //REVISIT: We should never reach this code which is using webdav as regex,
    //i.e., input uri is sth like /webdav/packages/mypackage
    String[] getPath(String uri) {
        if (Contexts.isSessionContextActive()) {
            return getPath(uri,
                    false);
        } else {
            return getPath(uri,
                    true);
        }

    }

    String[] getPath(String uri,
                     boolean usingWebdavAsRegex) {
        if (uri.equals("/")) {
            return new String[0];
        }

        if (usingWebdavAsRegex) {
            if (uri.endsWith("webdav") || uri.endsWith("webdav/")) {
                return new String[0];
            }
            if (uri.indexOf("webdav/") > -1) {
                return uri.split("webdav/",
                        2)[1].split("/");
            }
        }

        return uri.substring(1).split("/");
    }

    private boolean isAdmin() {
        if (Contexts.isSessionContextActive()) {
            try {
                Identity.instance().checkPermission(new AdminType(),
                        RoleType.ADMIN.getName());
                return true;
            } catch (Exception e) {
                return false;
            }
        } else {
            return true;
        }
    }

    private boolean checkPackagePermission(String packageName,
                                           String type) {
        if (Contexts.isSessionContextActive()) {
            try {
                Identity.instance().checkPermission(new WebDavPackageNameType(packageName),
                        type);
                return true;
            } catch (Exception e) {
                return false;
            }
        } else {
            return true;
        }
    }

    private AssetItem loadAssetItemFromPackage(RulesRepository repository,
                                               String[] path) {
        return loadAssetItemFromPackageItem(loadPackageFromRepository(repository,
                path[1]),
                path[2]);
    }

    private AssetItem loadAssetItemFromPackageSnaphot(RulesRepository repository,
                                                      String[] path) {
        return loadAssetItemFromPackageItem(loadPackageSnapshotFromRepository(repository,
                path),
                path[3]);
    }

    private AssetItem loadAssetItemFromGlobalArea(RulesRepository repository,
                                                  String[] path) {
        return loadAssetItemFromPackageItem(loadGlobalAreaFromRepository(repository),
                path[1]);
    }

    private AssetItem loadAssetItemFromPackageItem(PackageItem pkg,
                                                   String path) {
        return pkg.loadAsset(AssetItem.getAssetNameFromFileName(path)[0]);
    }

    private boolean isAssetItemInPackage(RulesRepository repository,
                                         String[] path) {
        return loadPackageSnapshotFromRepository(repository,
                path).containsAsset(AssetItem.getAssetNameFromFileName(path[3])[0]);
    }

    private PackageItem loadPackageFromRepository(RulesRepository repository,
                                                  String path) {
        return repository.loadPackage(path);
    }

    private PackageItem loadPackageSnapshotFromRepository(RulesRepository repository,
                                                          String[] path) {
        return repository.loadPackageSnapshot(path[1],
                path[2]);
    }

    private PackageItem loadGlobalAreaFromRepository(RulesRepository repository) {
        return repository.loadGlobalArea();
    }

    private boolean isPermission(String[] path,
                                 int pathIndex) {
        return path.length == pathIndex && checkPackagePermissionIfReadOnly(path);
    }

    private boolean checkPackagePermissionIfReadOnly(String[] path) {
        return checkPackagePermission(path[1],
                RoleType.PACKAGE_READONLY.getName());
    }

    private boolean checkPackagePermissionIfDeveloper(String[] path) {
        return checkPackagePermission(path[1],
                RoleType.PACKAGE_DEVELOPER.getName());
    }

    private boolean isPackages(String[] path) {
        return path[0].equals(PACKAGES);
    }

    private boolean isSnaphosts(String[] path) {
        return path[0].equals(SNAPSHOTS);
    }

    private boolean isGlobalAreas(String[] path) {
        return path[0].equals(GLOBALAREA);
    }

}
