package org.drools.repository.remoteapi;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import org.drools.repository.AssetItem;
import org.drools.repository.PackageItem;
import org.drools.repository.RulesRepository;
import org.drools.repository.remoteapi.Response.Binary;
import org.drools.repository.remoteapi.Response.Text;

/**
 * This provides a simple REST style remote friendly API.
 *
 * @author Michael Neale
 *
 */
public class RestAPI {

	private final RulesRepository repo;

	public RestAPI(RulesRepository repo) {
		this.repo = repo;
	}

	public Response get(String path) throws UnsupportedEncodingException {
		String[] bits = split(path);
		if (bits[0].equals("packages")) {
			String pkgName = bits[1];
			if (bits.length == 2) {
				return listPackage(pkgName);
			} else {
				String resourceFile = bits[2];
				return loadContent(pkgName, resourceFile);
			}
		} else {
			throw new IllegalArgumentException("Unable to deal with " + path);
		}

	}

	String[] split(String path) {
		if (path.startsWith("/")) path = path.substring(1);
		String[] bits = path.split("/");
		return bits;
	}

	private Response loadContent(String pkgName, String resourceFile) throws UnsupportedEncodingException {
		PackageItem pkg = repo.loadPackage(pkgName);
		if (resourceFile.equals(".package")) {
			Text r = new Response.Text();
			r.lastModified = pkg.getLastModified();
			r.data = pkg.getHeader();
			return r;
		} else {
			String assetName = URLDecoder.decode(resourceFile, "UTF-8").split("\\.")[0];

			AssetItem asset = pkg.loadAsset(assetName);
			if (asset.isBinary()) {
				Binary r = new Response.Binary();
				r.lastModified = asset.getLastModified();
				r.stream = asset.getBinaryContentAttachment();
				return r;
			} else {
				Text r = new Response.Text();
				r.lastModified = pkg.getLastModified();
				r.data = asset.getContent();
				return r;
			}

		}

	}

	private Response listPackage(String pkgName) throws UnsupportedEncodingException {
		PackageItem pkg = repo.loadPackage(URLDecoder.decode(pkgName, "UTF-8"));
		StringBuilder sb = new StringBuilder();
		Iterator<AssetItem> it = pkg.getAssets();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

		while (it.hasNext()) {
			AssetItem a = it.next();
			sb.append(a.getName() + "." + a.getFormat() + "=" + sdf.format(a.getLastModified().getTime()));
			sb.append('\n');
		}

		Text r = new Response.Text();
		r.lastModified = pkg.getLastModified();
		r.data = sb.toString();
		return r;
	}

	/** post is for new content. */
	public void post(String path, InputStream in, boolean binary, String comment) {
		String[] bits = split(path);
		if (bits[0].equals("packages")) {
			String fileName = bits[2];
			String[] a = fileName.split("\\.");
			PackageItem pkg = repo.loadPackage(bits[1]);
			AssetItem asset = pkg.addAsset(a[0], "added remotely");
			asset.updateFormat(a[1]);
			if (binary) asset.updateBinaryContentAttachment(in);
			else {
				//FAIL !
			}
			asset.checkin(comment);

		} else {
			throw new IllegalArgumentException("Unknown rest path for post.");
		}
	}

	/** put is for updating content. It will cause a new revision to be created. */
	public String put(String path, Date lastModified, InputStream in) {
		return null;
	}

	/**
	 * Should be pretty obvious what this is for.
	 */
	public String delete(String path) {
		return null;
	}



}
