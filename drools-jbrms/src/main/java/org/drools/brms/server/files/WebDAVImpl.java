package org.drools.brms.server.files;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import net.sf.webdav.WebdavStore;

import org.apache.commons.io.IOUtils;
import org.drools.repository.AssetItem;
import org.drools.repository.PackageItem;
import org.drools.repository.RulesRepository;

public class WebDAVImpl implements WebdavStore {


	/** for the rubbish OSX double data (the ._ rubbish) */
	static Map<String, byte[]> osxDoubleData = Collections.synchronizedMap(new WeakHashMap<String, byte[]>());

    final ThreadLocal<RulesRepository> tlRepo = new ThreadLocal<RulesRepository>();


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

    public void begin(Principal pr) {
    	tlRepo.set(RestAPIServlet.getRepository());
    }

    public void checkAuthentication() throws SecurityException {
        //already done
    }

    public void commit()  {
    	System.out.println("COMMIT");
        getRepo().save();
        tlRepo.set(null);
    }

    public void createFolder(String uri)  {
        System.out.println("creating folder:" + uri);
        String[] path = getPath(uri);
        if (path[0].equals("packages")) {
            if (path.length > 2) {
                throw new UnsupportedOperationException("Can't nest packages.");
            }
            RulesRepository repository = getRepo();
            if (repository.containsPackage(path[1])) {
                PackageItem pkg = repository.loadPackage(path[1]);
                pkg.archiveItem(false);
                pkg.checkin("<restored by webdav>");
            } else {
                repository.createPackage(path[1], "<from webdav>");
            }
        } else {
            throw new UnsupportedOperationException("Not able to create folders here...");
        }
    }

    public void createResource(String uri) {
        System.out.println("creating resource:" + uri);
        //for mac OSX, ignore these annoying things
        if (uri.endsWith(".DS_Store")) return;
        String[] path = getPath(uri);
        if (path[0].equals("packages")) {
            if (path.length > 3) {
                throw new UnsupportedOperationException("Can't do nested packages.");
            }
            String packageName = path[1];
            String[] resource = AssetItem.getAssetNameFromFileName(path[2]);
            RulesRepository repository = getRepo();
            PackageItem pkg = repository.loadPackage(packageName);

            //for mac OSX, ignore these resource fork files
            if (path[2].startsWith("._")) {
                this.osxDoubleData.put(uri, null);
                return;
            }
            if (pkg.containsAsset(resource[0])) {

                AssetItem lazarus = pkg.loadAsset(resource[0]);
                lazarus.archiveItem(false);
                lazarus.checkin("<from webdav>");
            } else {
                AssetItem asset = pkg.addAsset(resource[0], "");
                asset.updateFormat(resource[1]);
                asset.checkin("<from webdav>");
            }

        } else {
            throw new UnsupportedOperationException("Can't add assets here.");
        }
    }


    public String[] getChildrenNames(String uri) {
    	System.out.println("getChildrenNames :" + uri);

    	RulesRepository repository = getRepo();
        String[] path = getPath(uri);
        List<String> result = new ArrayList<String>();
        if (path.length == 0) {
            return new String[] {"packages", "snapshots"};
        }
        if (path[0].equals("packages")) {
            if (path.length > 2) {
                return null;
            }
            if (path.length == 1) {
                listPackages(repository, result);
            } else {
                PackageItem pkg = repository.loadPackage(path[1]);
                Iterator<AssetItem> it = pkg.getAssets();
                while(it.hasNext()) {
                    AssetItem asset = it.next();
                    if (!asset.isArchived()) {
                        result.add(asset.getName() + "." + asset.getFormat());
                    }
                }
            }

        } else if (path[0].equals("snapshots")) {
        	if (path.length > 3) {
        		return null;
        	}
        	if (path.length == 1) {
        		listPackages(repository, result);
        	} else if (path.length == 2){
        		String[] snaps = repository.listPackageSnapshots(path[1]);
        		return snaps;
        	} else if (path.length == 3){
        		Iterator<AssetItem> it  = repository.loadPackageSnapshot(path[1], path[2]).getAssets();
        		while(it.hasNext()) {
        		    AssetItem asset = it.next();
        		    if (!asset.isArchived()) {
        		    	result.add(asset.getName() + "." + asset.getFormat());
        		    }
        		}
        	} else {
        		throw new IllegalArgumentException();
        	}

        } else {
        	throw new UnsupportedOperationException("Not a valid path : " + path[0]);
        }
        return result.toArray(new String[result.size()]);
    }

	private void listPackages(RulesRepository repository, List<String> result) {
		Iterator<PackageItem> it = repository.listPackages();
		while(it.hasNext()) {
		    PackageItem pkg = it.next();
		    if (!pkg.isArchived()) {
		        result.add(pkg.getName());
		    }
		}
	}

    public Date getCreationDate(String uri) {
    	System.out.println("getCreationDate :" + uri);

    	RulesRepository repository = getRepo();
        String[] path = getPath(uri);
        if (path.length < 2) return new Date();
        if (path[0].equals("packages")) {
            PackageItem pkg = repository.loadPackage(path[1]);
            if (path.length == 2) {
                //dealing with package
                return pkg.getCreatedDate().getTime();
            } else {
                String fileName = path[2];
                String assetName = AssetItem.getAssetNameFromFileName(fileName)[0];
                AssetItem asset = pkg.loadAsset(assetName);
                return asset.getCreatedDate().getTime();
            }
        } else if (path[0].equals("snapshots")){
            if (path.length == 2) {
            	return new Date();
            } else if (path.length == 3) {
            	return repository.loadPackageSnapshot(path[1], path[2]).getCreatedDate().getTime();
            } else if (path.length == 4) {
            	PackageItem pkg = repository.loadPackageSnapshot(path[1], path[2]);
            	AssetItem asset = pkg.loadAsset(AssetItem.getAssetNameFromFileName(path[3])[0]);
            	return asset.getCreatedDate().getTime();
            } else {
            	throw new UnsupportedOperationException();
            }
        } else {
        	throw new UnsupportedOperationException();
        }
    }

    public Date getLastModified(String uri) {
    	System.out.println("getLastModified :" + uri);

    	RulesRepository repository = getRepo();
        String[] path = getPath(uri);
        if (path.length < 2) return new Date();
        if (path[0].equals("packages")) {
            PackageItem pkg = repository.loadPackage(path[1]);
            if (path.length == 2) {
                //dealing with package
                return pkg.getLastModified().getTime();
            } else {
                String fileName = path[2];
                String assetName = AssetItem.getAssetNameFromFileName(fileName)[0];
                AssetItem asset = pkg.loadAsset(assetName);
                return asset.getLastModified().getTime();
            }
        } else if (path[0].equals("snapshots")){
            if (path.length == 2) {
            	return new Date();
            } else if (path.length == 3) {
            	return repository.loadPackageSnapshot(path[1], path[2]).getLastModified().getTime();
            } else if (path.length == 4) {
            	PackageItem pkg = repository.loadPackageSnapshot(path[1], path[2]);
            	AssetItem asset = pkg.loadAsset(AssetItem.getAssetNameFromFileName(path[3])[0]);
            	return asset.getLastModified().getTime();
            } else {
            	throw new UnsupportedOperationException();
            }
        } else {
            throw new UnsupportedOperationException();
        }
    }



    public InputStream getResourceContent(String uri) {
        System.out.println("get resource content:" + uri);
        return getContent(uri);
    }

	private InputStream getContent(String uri) {
		RulesRepository repository = getRepo();
		String[] path = getPath(uri);
        if (path[0].equals("packages")) {
            String pkg = path[1];
            String asset = AssetItem.getAssetNameFromFileName(path[2])[0];
            AssetItem assetItem  = repository.loadPackage(pkg).loadAsset(asset);
            return getAssetData(assetItem);
        } else if (path[0].equals("snapshots")) {
        	String pkg = path[1];
        	String snap = path[2];
        	String asset = AssetItem.getAssetNameFromFileName(path[3])[0];
        	AssetItem assetItem = repository.loadPackageSnapshot(pkg, snap).loadAsset(asset);
        	return getAssetData(assetItem);

        }else {
            throw new UnsupportedOperationException();
        }
	}

	private InputStream getAssetData(AssetItem assetItem) {
		if (assetItem.isBinary()) {
		    return assetItem.getBinaryContentAttachment();
		} else {
		    return new ByteArrayInputStream(assetItem.getContent().getBytes());
		}
	}

    public long getResourceLength(String uri) {
    	System.out.println("get resource length :" + uri);
    	String[] path = getPath(uri);
    	try {
    		RulesRepository repo = getRepo();
    		if (path.length == 3 && path[0].equals("packages")) {
    			PackageItem pkg = repo.loadPackage(path[1]);
    			AssetItem asset = pkg.loadAsset(AssetItem.getAssetNameFromFileName(path[2])[0]);
    			return asset.getContentLength();
    		} else if (path.length == 4 && path[0].equals("snapshots")) {
    			PackageItem pkg = repo.loadPackageSnapshot(path[1], path[2]);
    			AssetItem asset = pkg.loadAsset(AssetItem.getAssetNameFromFileName(path[3])[0]);
    			return asset.getContentLength();
    		} else {
    			return 0;
    		}
		} catch (Exception e) {
			System.err.println("Not able to get content length");
			return 0;
		}

    }

    public boolean isFolder(String uri) {
    	System.out.println("is folder :" + uri);
    	RulesRepository repository = getRepo();
        String[] path = getPath(uri);
        if (path.length == 0) return true;
        if (path.length == 1 && (path[0].equals("packages") || path[0].equals("snapshots"))) {
            return true;
        } else if (path.length == 2) {
        	return repository.containsPackage(path[1]);
        } else if (path.length == 3 && path[0].equals("snapshots")) {
        	return repository.containsPackage(path[1]);
        } else {

            return false;
        }
    }

    public boolean isResource(String uri) {
    	RulesRepository repository = getRepo();
    	System.out.println("is resource :" + uri);
    	String[] path = getPath(uri);
    	if (path.length < 3) return false;
    	if (!(path[0].equals("packages") || path[0].equals("snapshots"))) return false;
        if (repository.containsPackage(path[1])) {
        	if (path[0].equals("packages")) {
	        	PackageItem pkg = repository.loadPackage(path[1]);
	        	if (path[2].startsWith("._")) {
	        		return osxDoubleData.containsKey(uri);
	        	}
	        	return pkg.containsAsset(AssetItem.getAssetNameFromFileName(path[2])[0]);
        	} else {
        		if (path.length == 4) {
	        		PackageItem pkg = repository.loadPackageSnapshot(path[1], path[2]);
	        		return pkg.containsAsset(AssetItem.getAssetNameFromFileName(path[3])[0]);
        		} else {
        			return false;
        		}
        	}
        } else {
        	return false;
        }
    }

    public boolean objectExists(String uri) {
    	if (uri.indexOf(" copy ") > 0) {
    		throw new IllegalArgumentException("OSX is not capable of copy and pasting without breaking the file extension.");
    	}
    	return internalObjectExists(uri);
    }

    private boolean internalObjectExists(String uri) {

    	RulesRepository repository = getRepo();
        System.out.println("object exist check :" + uri);
        if (uri.endsWith(".DS_Store")) return false;
        String[] path = getPath(uri);
        if (path.length == 0) return true;
        if (path.length == 1 && (path[0].equals("packages") || path[0].equals("snapshots"))) {
            return true;
        } else {
            if (path.length == 1) return false;
            if (!repository.containsPackage(path[1])) {
                return false;
            }

            if (path[0].equals("packages")) {
	            if (path.length == 2) {
	                PackageItem pkg = repository.loadPackage(path[1]);
	                return !pkg.isArchived();
	            } else {
	                PackageItem pkg = repository.loadPackage(path[1]);
	                if (path[2].startsWith("._")){
	                	return this.osxDoubleData.containsKey(uri);
	                }
	                String assetName = AssetItem.getAssetNameFromFileName(path[2])[0];

	                return pkg.containsAsset(assetName) && !pkg.loadAsset(assetName).isArchived();
	            }
            } else if (path[0].equals("snapshots")) {
            	if (path.length == 2) {
            		return repository.containsPackage(path[1]);
            	} else if (path.length == 3) {
            		return repository.containsSnapshot(path[1], path[2]);
            	} else if (path.length == 4) {
            		PackageItem pkg = repository.loadPackageSnapshot(path[1], path[2]);
            		return pkg.containsAsset(AssetItem.getAssetNameFromFileName(path[3])[0]);
            	} else {
            		return false;
            	}
            } else {
            	throw new IllegalStateException();
            }
        }
    }

    public void removeObject(String uri) {
    	RulesRepository repository = getRepo();
        System.out.println("remove object:" + uri);
        String[] path = getPath(uri);
        if (path.length == 0 || path.length == 1) {
            throw new IllegalArgumentException();
        }
        if (path[0].equals("packages")) {
            String packName = path[1];
            PackageItem pkg = repository.loadPackage(packName);
            if (path.length == 3) {
                //delete asset
            	if (path[2].startsWith("._")) {
            		osxDoubleData.remove(uri);
            		return;
            	}
                String asset = AssetItem.getAssetNameFromFileName(path[2])[0];
                AssetItem item = pkg.loadAsset(asset);
                item.archiveItem(true);
                item.checkin("");
            } else {
                //delete package
                pkg.archiveItem(true);
                pkg.checkin("");
            }
        } else {
            throw new IllegalArgumentException("Not allowed to remove this file.");
        }

    }

    public void rollback() {
    	System.out.println("ROLLBACK");

    	RulesRepository repository = getRepo();
        repository.getSession().logout();
    }

    public void setResourceContent(String uri, InputStream content, String contentType, String characterEncoding)  {
    	RulesRepository repository = getRepo();
        System.out.println("set resource content:" + uri);
        if (uri.endsWith(".DS_Store")) return;
        String[] path = getPath(uri);
        if (path[0].equals("packages")) {
            if (path.length != 3) {
                throw new IllegalArgumentException("Not a valid resource path " + uri);
            }

             String packageName = path[1];
             if (path[2].startsWith("._")) {
            	 try {
					this.osxDoubleData.put(uri, IOUtils.toByteArray(content));
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
            	 return;
             }
             String[] assetName = AssetItem.getAssetNameFromFileName(path[2]);
             PackageItem pkg = repository.loadPackage(packageName);
             AssetItem asset = pkg.loadAsset(assetName[0]);
             asset.updateBinaryContentAttachment(content);
             //here we could save, or check in, depending on if enough time has passed to justify
             //a new version. Otherwise we will pollute the version history with lots of trivial versions.
             if (shouldCreateNewVersion(asset.getLastModified())) {
                 asset.checkin("");
             }


        } else {
            throw new UnsupportedOperationException("Unable to save content to this location.");
        }

    }


    /**
     * If enough time has passed, we should create a new version.
     */
    boolean shouldCreateNewVersion(Calendar lastModified) {
        Calendar now = Calendar.getInstance();
        int diff = 3600000; //1 hour
        return (now.getTimeInMillis() - lastModified.getTimeInMillis()) > diff;
    }

    String[] getPath(String uri) {
        if (uri.endsWith("webdav") || uri.endsWith("webdav/")) {
            return new String[0];
        }
        return uri.split("webdav/")[1].split("/");
    }



}


