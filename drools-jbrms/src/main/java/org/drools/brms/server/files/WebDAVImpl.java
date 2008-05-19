package org.drools.brms.server.files;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import net.sf.webdav.IWebdavStorage;

import org.drools.repository.AssetItem;
import org.drools.repository.PackageItem;
import org.drools.repository.RulesRepository;
import org.drools.rule.TimeMachine;

public class WebDAVImpl implements IWebdavStorage {

	final RulesRepository repository;

	TimeMachine time = new TimeMachine();

	public WebDAVImpl() {
		repository = RestAPIServlet.getRepository();
	}

	public WebDAVImpl(RulesRepository testRepo) {
		repository = testRepo;
	}

	public void begin(Principal pr, Hashtable params) throws Exception {
		//do nothing.
	}

	public void checkAuthentication() throws SecurityException {
		//already done
	}

	public void commit() throws IOException {
		repository.save();
	}

	public void createFolder(String uri) throws IOException {
		String[] path = getPath(uri);
		if (path[0].equals("packages")) {
			if (path.length > 2) {
				throw new UnsupportedOperationException("Can't nest packages.");
			}
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

	public void createResource(String uri) throws IOException {
		String[] path = getPath(uri);
		if (path[0].equals("packages")) {
			if (path.length > 3) {
				throw new UnsupportedOperationException("Can't do nested packages.");
			}
			String packageName = path[1];
			String[] resource = path[2].split("\\.");

			PackageItem pkg = repository.loadPackage(packageName);
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


	public String[] getChildrenNames(String uri) throws IOException {
		String[] path = getPath(uri);
		List<String> result = new ArrayList<String>();
		if (path.length == 0) {
			return new String[] {"packages"};
		}
		if (path[0].equals("packages")) {
			if (path.length > 2) {
				throw new UnsupportedOperationException("No nested package support");
			}
			if (path.length == 1) {
				Iterator<PackageItem> it = repository.listPackages();
				while(it.hasNext()) {
					PackageItem pkg = it.next();
					if (!pkg.isArchived()) {
						result.add(pkg.getName());
					}
				}
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
			return result.toArray(new String[result.size()]);
		} else {
			throw new UnsupportedOperationException("Not implemented yet");
		}
	}

	public Date getCreationDate(String uri) throws IOException {
		String[] path = getPath(uri);
		if (path[0].equals("packages")) {
			PackageItem pkg = repository.loadPackage(path[1]);
			if (path.length == 2) {
				//dealing with package
				return pkg.getCreatedDate().getTime();
			} else {
				String fileName = path[2];
				String assetName = fileName.split("\\.")[0];
				AssetItem asset = pkg.loadAsset(assetName);
				return asset.getCreatedDate().getTime();
			}
		} else {
			throw new UnsupportedOperationException();
		}
	}

	public Date getLastModified(String uri) throws IOException {
		String[] path = getPath(uri);
		if (path[0].equals("packages")) {
			PackageItem pkg = repository.loadPackage(path[1]);
			if (path.length == 2) {
				//dealing with package
				return pkg.getLastModified().getTime();
			} else {
				String fileName = path[2];
				String assetName = fileName.split("\\.")[0];
				AssetItem asset = pkg.loadAsset(assetName);
				return asset.getLastModified().getTime();
			}
		} else {
			throw new UnsupportedOperationException();
		}
	}



	public InputStream getResourceContent(String uri) throws IOException {
		String[] path = getPath(uri);
		if (path[0].equals("packages")) {
			String pkg = path[1];
			String asset = path[2].split("\\.")[0];
			AssetItem assetItem  = repository.loadPackage(pkg).loadAsset(asset);
			if (assetItem.isBinary()) {
				return assetItem.getBinaryContentAttachment();
			} else {
				return new ByteArrayInputStream(assetItem.getContent().getBytes());
			}
		} else {
			throw new UnsupportedOperationException();
		}
	}

	public long getResourceLength(String uri) throws IOException {
		//leave this as zero as we don't always know it.
		return 0;
	}

	public boolean isFolder(String uri) throws IOException {
		String[] path = getPath(uri);
		if (path.length == 0) return true;
		if (path.length == 1 && path[0].equals("packages")) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isResource(String uri) throws IOException {
		return !isFolder(uri);
	}

	public boolean objectExists(String uri) throws IOException {
		String[] path = getPath(uri);
		if (path.length == 0) return true;
		if (path.length == 1 && path[0].equals("packages")) {
			return true;
		} else {
			if (path.length == 1) return false;
			if (!repository.containsPackage(path[1])) {
				return false;
			}

			if (path.length == 2) {
				PackageItem pkg = repository.loadPackage(path[1]);
				return !pkg.isArchived();
			} else {
				PackageItem pkg = repository.loadPackage(path[1]);
				String assetName = path[2].split("\\.")[0];

				return pkg.containsAsset(assetName) && !pkg.loadAsset(assetName).isArchived();
			}
		}
	}

	public void removeObject(String uri) throws IOException {
		String[] path = getPath(uri);
		if (path.length == 0 || path.length == 1) {
			throw new IllegalArgumentException();
		}
		if (path[0].equals("packages")) {
			String packName = path[1];
			PackageItem pkg = repository.loadPackage(packName);
			if (path.length == 3) {
				//delete asset
				String asset = path[2].split("\\.")[0];
				AssetItem item = pkg.loadAsset(asset);
				item.archiveItem(true);
				item.checkin("");
			} else {
				//delete package
				pkg.archiveItem(true);
				pkg.checkin("");
			}
		} else {
			throw new IllegalArgumentException();
		}

	}

	public void rollback() throws IOException {
		repository.getSession().logout();
	}

    public void setResourceContent(String uri, InputStream content, String contentType, String characterEncoding)  throws IOException {
    	String[] path = getPath(uri);
    	if (path[0].equals("packages")) {
    		if (path.length != 3) {
    			throw new IllegalArgumentException("Not a valid resource path " + uri);
    		}
    		 String packageName = path[1];
    		 String[] assetName = path[2].split("\\.");
    		 PackageItem pkg = repository.loadPackage(packageName);
    		 AssetItem asset = pkg.loadAsset(assetName[0]);
    		 asset.updateBinaryContentAttachment(content);
    		 if (shouldCreateNewVersion(asset.getLastModified())) {
    			 asset.checkin("");
    		 }

    	} else {
    		throw new UnsupportedOperationException("Unable to save content to this location.");
    	}

		//here we could save, or check in, depending on if enough time has passed to justify
		//a new version. Otherwise we will pollute the version history with lots of trivial versions.
	}


    /**
     * If enough time has passed, we should create a new version.
     */
	boolean shouldCreateNewVersion(Calendar lastModified) {
		Calendar now = Calendar.getInstance();
		int diff = 86400000; //1 day
		if (now.getTimeInMillis() - lastModified.getTimeInMillis() > diff) {
			return true;
		} else {
			return false;
		}
	}

	String[] getPath(String uri) {
		if (uri.endsWith("webdav") || uri.endsWith("webdav/")) {
			return new String[0];
		}
		return uri.split("webdav/")[1].split("/");
	}


}


