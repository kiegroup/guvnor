package org.jboss.drools.guvnor.importgenerator.xdeleteme;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;



/**
 * another HTTP POST - this time it works, now hitting gtw security issues
 * @author mallen
 *
 */
public class HTTPUploadImporter2 {
	String protocol = "http";
	String server = "localhost";
	String port = "8080";
	String path = "drools-guvnor/org.drools.guvnor.Guvnor";
	String page = "package";
	String fullPath = protocol + "://" + server + ":" + port + "/" + path + "/" + page;
	String XINPUT_FILE_LOCATION = "/home/mallen/workspace/drools-importer/rules/approval/route/firststage/1.0.0.SNAPSHOT/firstStageApproval.drl";
	String INPUT_FILE_LOCATION = "/home/mallen/workspace/drools-importer/rules/approval/determine/valueapprovallimits/1.0.0.SNAPSHOT/valueApprovalLimits.xls";
//
//	private byte[] getFileData() throws FileNotFoundException, IOException {
//		File f = new File(INPUT_FILE_LOCATION);
//		byte[] contents = new byte[(int) f.length()]; // assuming no file will
//		// break the integer
//		// file size
//		BufferedInputStream is = new BufferedInputStream(new FileInputStream(f));
//		is.read(contents);
//		return contents;
//	}
//	
//	private String getFileContent() throws IOException, FileNotFoundException{
//		File f=new File(INPUT_FILE_LOCATION);
//		BufferedReader br=new BufferedReader(new FileReader(f));
//		StringBuffer sb=new StringBuffer();
//		String line="";
//		while ((line=br.readLine())!=null){
//			sb.append(line).append("\r\n");
//		}
//		return sb.toString();
//	}

	public void run() {
		try {
			String fullPath = protocol + "://" + server + ":" + port + "/" + path + "/" + page;
			//ClientHttpRequest req=new ClientHttpRequest(new URL(fullPath).openConnection());
			
//			//login???
//			String fullPathSec = protocol + "://" + server + ":" + port + "/drools-guvnor/org.drools.guvnor.Guvnor/securityService";
//			ClientHttpRequest sec=new ClientHttpRequest(new URL(fullPathSec).openConnection(), ContentType.TEXT_X_GWT_RPC);
//			sec.post(new Object[]{"userName", "admin", "pswd", "admin"});
			
			//new com.google.gwt.user.server.rpc.RemoteServiceServlet().
			
			URL url = new URL(fullPath);
			URLConnection cnn = url.openConnection();
			//upload file
			ClientHttpRequest http=new ClientHttpRequest(cnn);
			InputStream in=http.post(new Object[]{"fred.drl", new File(INPUT_FILE_LOCATION)});
			//in.read();
			BufferedReader rd = new BufferedReader(new InputStreamReader(in));
			String line;
			while ((line = rd.readLine()) != null) {
				// do something with the response line
				System.out.println(line);
			}
			
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
		HTTPUploadImporter2 i = new HTTPUploadImporter2();
		try {
			i.run();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
