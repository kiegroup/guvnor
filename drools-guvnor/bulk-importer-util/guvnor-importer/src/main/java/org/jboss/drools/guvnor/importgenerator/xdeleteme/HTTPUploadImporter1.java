package org.jboss.drools.guvnor.importgenerator.xdeleteme;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
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

//import sun.misc.BASE64Encoder;

/**
 * my own HTTP POST impl
 * @author mallen
 *
 */
public class HTTPUploadImporter1 {
	// "http://localhost:8080/drools-guvnor/org.drools.guvnor.Guvnor/package"
	String protocol = "http";
	String server = "localhost";
	String port = "8080";
	String path = "drools-guvnor/org.drools.guvnor.Guvnor";
	String page = "package";
	String fullPath = protocol + "://" + server + ":" + port + "/" + path + "/" + page;
	String INPUT_FILE_LOCATION = "/home/mallen/workspace/drools-importer/rules/approval/route/refundcasestatus/1.0.0.SNAPSHOT/refundCaseStatus.drl";

	private byte[] getFileData() throws FileNotFoundException, IOException {
		File f = new File(INPUT_FILE_LOCATION);
		byte[] contents = new byte[(int) f.length()]; // assuming no file will
		// break the integer
		// file size
		BufferedInputStream is = new BufferedInputStream(new FileInputStream(f));
		is.read(contents);
		return contents;
	}
	
	private String getFileContent() throws IOException, FileNotFoundException{
		File f=new File(INPUT_FILE_LOCATION);
		BufferedReader br=new BufferedReader(new FileReader(f));
		StringBuffer sb=new StringBuffer();
		String line="";
		while ((line=br.readLine())!=null){
			sb.append(line).append("\r\n");
		}
		return sb.toString();
	}

	public void run() {
		try {

			URL url = new URL(fullPath);
			URLConnection cnn = url.openConnection();
			//String data = URLEncoder.encode("classicDRLFile", "UTF-8") + "=" + URLEncoder.encode(getFileData().toString(), "UTF-8");
			String fileContent=getFileContent();
			String boundary = "---------------------------29772313742742";
			cnn.setRequestProperty("POST", path+page +" HTTP/1.0");
			cnn.setRequestProperty("Connection", "keep-alive");
			cnn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
			cnn.setRequestProperty("Content-length", String.valueOf(fileContent.length()));
			
			cnn.setDoOutput(true);
			cnn.setUseCaches(false);
			DataOutputStream out = new DataOutputStream(cnn.getOutputStream());
			
			out.writeBytes(boundary);
			String CRLF="\r\n";
			out.writeBytes("Content-Disposition: form-data; name=\"classicDRLFile\"; filename=\"classicDRLFile\""+CRLF);
			out.writeBytes("Content-type: text/plain"+CRLF);
			out.writeBytes(fileContent+CRLF);
			out.writeBytes(boundary +"--");
			out.flush();
			out.close();
			
			BufferedReader rd = new BufferedReader(new InputStreamReader(cnn.getInputStream()));
			String line;
			while ((line = rd.readLine()) != null) {
				// do something with the response line
				System.out.println(line);
			}
			//wr.close();
			rd.close();
			System.out.println("Done");

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
		HTTPUploadImporter1 i = new HTTPUploadImporter1();
		try {
			i.run();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
