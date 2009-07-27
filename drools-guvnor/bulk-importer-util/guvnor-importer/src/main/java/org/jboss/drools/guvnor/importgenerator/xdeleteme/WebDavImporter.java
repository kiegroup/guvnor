package org.jboss.drools.guvnor.importgenerator.xdeleteme;


import java.util.HashMap;
import java.util.Map;

//import org.apache.commons.vfs.CacheStrategy;
//import org.apache.commons.vfs.FileObject;
//import org.apache.commons.vfs.FileSelector;
//import org.apache.commons.vfs.FileSystemManager;
//import org.apache.commons.vfs.FileSystemOptions;
//import org.apache.commons.vfs.VFS;
//import org.apache.commons.vfs.impl.StandardFileSystemManager;
//import org.apache.commons.vfs.impl.VirtualFileSystem;
//import org.apache.commons.vfs.util.FileObjectUtils;

/**
 * attempts at using apache webdav library
 * @author mallen
 *
 */
public class WebDavImporter {
	String protocol = "webdav";
	String server = "admin:admin@localhost";
	String port = "8080";
	String path = "drools-guvnor/org.drools.guvnor.Guvnor/webdav";
	String fullPath = protocol + "://" + server + ":" + port + "/" + path;
	
	public void run() {
		try {
//			StandardFileSystemManager fs = new StandardFileSystemManager();
//
//			fs.setCacheStrategy(CacheStrategy.ON_RESOLVE);
//			fs.init();
//
//			FileSystemOptions fileoptions = new FileSystemOptions();
//			FileObject root = fs.resolveFile(fullPath, fileoptions);
//			FileObject packages = fs.resolveFile(fullPath + "/packages", fileoptions);
//			FileObject packages2=root.getChildren()[0];
//			
//			File packageName = new File(packages.getName() + "/gov.tfl.rules");
//			packageName.createNewFile();
//			
//			File drl = new File(packageName.getName() +"/mjatest.drl");
//			FileObject drlFO=fs.toFileObject(drl);
//			drlFO.createFile();
//
//			Writer w = new FileWriter(drl);
//			w.write("import java.util.Date");
//			w.flush();
//			w.close();
//
//			long size = drl.length();
//			System.out.println(size);
//			drlFO.close();
//			
//			System.out.println("Done.");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	




	private class RuleDetails {
		private String title;
		private String content;

		public RuleDetails(String title, String content) {
			this.title = title;
			this.content = content;
		}

		public String getTitle() {
			return title;
		}

		public String getContent() {
			return content;
		}

		public void setContent(String content) {
			this.content = content;
		}
	}

	private class PackageDetails {
		private String creator;
		private String description;
		private String imports;
		private Map<String, RuleDetails> rules = new HashMap<String, RuleDetails>();

		public PackageDetails(String creator, String imports, String description) {
			this.creator = creator;
			this.imports = imports;
			this.description = description;

		}

		public String getCreator() {
			return creator;
		}

		public String getDescription() {
			return description;
		}

		public String getImports() {
			return imports;
		}

		public Map<String, RuleDetails> getRules() {
			return rules;
		}

		public void setRules(Map<String, RuleDetails> rules) {
			this.rules = rules;
		}
	}

	public static void main(String[] args) {
		WebDavImporter i = new WebDavImporter();
		try {
			i.run();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
