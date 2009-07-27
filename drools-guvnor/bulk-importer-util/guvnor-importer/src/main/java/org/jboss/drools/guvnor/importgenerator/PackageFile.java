package org.jboss.drools.guvnor.importgenerator;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.drools.common.DroolsObjectOutputStream;
import org.drools.compiler.DroolsError;
import org.drools.compiler.DroolsParserException;
import org.drools.compiler.PackageBuilder;
import org.drools.decisiontable.InputType;
import org.drools.rule.Package;
import org.jboss.drools.guvnor.importgenerator.CmdArgsParser.Parameters;
import org.jboss.drools.guvnor.importgenerator.utils.DroolsHelper;
import org.jboss.drools.guvnor.importgenerator.utils.FileIO;

/**
 * Represents a drl package file found in the file system
 * 
 * @author <a href="mailto:mallen@redhat.com">Mat Allen</a>
 */
public class PackageFile {
  private static final String PH_RULE_START="rule ";
  private static final String PH_PACKAGE_START="package ";
  private static final String PH_NEWLINE="\n";
  private static final String[] RULE_START_MATCHERS=new String[]{"rule \"", "rule\""};
  private static final String[] RULE_END_MATCHERS=new String[] { "end\n", "\nend" };
  private static final String PACKAGE_DELIMETER = ".";
  private static String FUNCTIONS_FILE=null;
  private Package pkg;
	private File file;
	private String imports=""; //default to no imports
  private String dependencyErrors="";
  private String compilationErrors="";
  private Map<String, Rule> rules=new HashMap<String, Rule>();
  private List<File> ruleFiles=new ArrayList<File>();
  private String name;
  
  private enum Format{
    DRL(".drl"),
    XLS(".xls");
    String value;
    Format(String value){
      this.value=value;
    }
  }
  
  	/**
	 * goes through the file system calling extract to build a list of PackageFile objects
	 * @param options
	 * @return
	 * @throws Exception
	 */
	public static Map<String, PackageFile> buildPackages(CmdArgsParser options) throws Exception{
		String path=options.getOption(Parameters.OPTIONS_PATH);
		FUNCTIONS_FILE=options.getOption(Parameters.OPTIONS_FUNCTIONS_FILE);
		Map<String, PackageFile> result = new HashMap<String, PackageFile>();
		File location=new File(path);
		if (!location.isDirectory())
			throw new Exception("path must be a directory");
		
		buildPackageForDirectory(result, location, options);
		return result;
	}
	
	/**
	 * Populates the <param>packages</param> parameter with PackageFile objects representing files within the specified <param>directory</param>
	 * @param packages
	 * @param directory
	 * @param options
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 */
	private static void buildPackageForDirectory(Map<String, PackageFile> packages, File directory, CmdArgsParser options) throws FileNotFoundException, UnsupportedEncodingException, DroolsParserException, IOException{
		boolean recurse="true".equals(options.getOption(Parameters.OPTIONS_RECURSIVE));

		File[] files=directory.listFiles(
				new FilenameFilter(){
						public boolean accept(File dir, String name){
							return !name.startsWith(".");
						}}
				);
		for (int i = 0; i < files.length; i++) {
		  //if it's a directory with files then build a package
		  if (files[i].isDirectory()){
		    File[] ruleFiles=getRuleFiles(files[i], options);
		    if (ruleFiles.length>0){
		      PackageFile packageFile=parseRuleFiles(ruleFiles, options);
		      packageFile.setName(getPackageName(files[i], options));
		      packages.put(packageFile.getName(), packageFile);
		    }else{
		      if (recurse)
		        buildPackageForDirectory(packages, files[i], options);
		    }
		  }
		}
	}
	

  private static File[] getRuleFiles(File directory, CmdArgsParser options){
	  if (directory.isDirectory()){
	    final String extensionList = options.getOption(Parameters.OPTIONS_EXTENSIONS);
	    File[] files=directory.listFiles(
        new FilenameFilter(){
            public boolean accept(File dir, String name){
              return !name.startsWith(".") && name.matches(buildRE(extensionList));
            }}
        );
	    List<File> result=new ArrayList<File>();
	    for (int i = 0; i < files.length; i++) {
        File f = files[i];
        if (f.isFile())
          result.add(f);
      }
	    return result.toArray(new File[result.size()]);
	  }
	  return new File[]{};
	}
	
	private static PackageFile parseRuleFiles(File[] ruleFiles, CmdArgsParser options) throws IOException, DroolsParserException {
    PackageFile result=new PackageFile();
    for (int i = 0; i < ruleFiles.length; i++) {
      File file = ruleFiles[i];
      if (file.getName().endsWith(".drl")) {
        parseDrlFile(file, result, options);
        result.addRuleFile(file);
      } else if (file.getName().endsWith(".xls")) {
        if (result.getRuleFiles().size()>1){
          //this is because the binary data needs a filename associated in the xml, and if there's multiple files when which one do you use?
          throw new DroolsParserException("Can't parse more than one .xls decision table file in a single directory ["+ file.getParentFile().getPath() +"]");
        }
        parseXlsFile(file, result, options);
        result.addRuleFile(file);
      }
    }
    return result;
  }
	
	private static void parseXlsFile(File file, PackageFile packageFile, CmdArgsParser options) throws FileNotFoundException, UnsupportedEncodingException{
	  String content=FileIO.readAllAsBase64(file);
	  packageFile.setName(getPackageName(file, options));
	  packageFile.setFile(file);
	  packageFile.getRules().put(file.getName(), new Rule(file.getName(), content));
	}
	
  private static void parseDrlFile(File file, PackageFile packageFile, CmdArgsParser options) throws FileNotFoundException{
    String content = FileIO.readAll(new FileInputStream(file));
    int packageLoc = content.indexOf(PH_PACKAGE_START); // usually 0
    int ruleLoc = getRuleStart(content, 0);// variable
    //packageFile.setPackageName(getPackageName(file, options));
    if (ruleLoc < 0)
      return; // there are no rule's in this file (perhaps functions or other?)
    String imports = content.substring(packageLoc, ruleLoc);
    packageFile.addImports(imports);

    try {
      boolean moreRules = true;
      while (moreRules) {
        int endLoc = getLoc(content, ruleLoc, RULE_END_MATCHERS) + 4;
        String ruleContents = content.substring(ruleLoc, endLoc);
        ruleLoc = getRuleStart(content, endLoc);
        moreRules = ruleLoc >= 0;
        Rule rule = new Rule(findRuleName(ruleContents, options), ruleContents);
        packageFile.getRules().put(rule.getRuleName(), rule);
      }
    } catch (StringIndexOutOfBoundsException e) {
      System.err.print("Error with file: " + file.getName() + "\n");
    }
  }
	
  /**
   * compiles the rule files into a package and generates any error details
   * @throws IOException
   * @throws DroolsParserException
   */
	public void buildPackage() throws IOException, DroolsParserException{
	  PackageBuilder pb = new PackageBuilder();
	  for (File file : getRuleFiles()) {
	    if (FUNCTIONS_FILE!=null){
        File functionsFile=new File(file.getParentFile().getPath(), FUNCTIONS_FILE);
        if (functionsFile.exists()){
          pb.addPackageFromDrl(new FileReader(functionsFile));
        }
	    }
	    if (isFormat(Format.DRL)){
	      pb.addPackageFromDrl(new FileReader(file));
	    }else if (isFormat(Format.DRL)){
	      pb.addPackageFromDrl(new StringReader(DroolsHelper.compileDTabletoDRL(file, InputType.XLS)));
	    }
	  }
    this.pkg=pb.getPackage();
    if (pkg==null) { // compilation error - the rule is syntactically incorrect
      for (int i = 0; i < pb.getErrors().getErrors().length; i++) {
        DroolsError msg = pb.getErrors().getErrors()[i];
        addCompilationError(msg.getMessage());
      }
    } else if (pkg!=null && !pkg.isValid()) {
      addDependencyError(pkg.getErrorSummary());
    }
	}
	/** impl that determines whether you have dependency errors 
	 * - this is not completed - unsure how to display/count error packages if you get one comp error and one dep error in a simple package
	 */
  public void buildPackageWithAccurateDependencyErrorDetection() throws IOException, DroolsParserException {
    PackageBuilder resBuilder = new PackageBuilder();
    for (File file : getRuleFiles()) {
      PackageBuilder pb = new PackageBuilder();
      if (FUNCTIONS_FILE != null) {
        File functionsFile = new File(file.getParentFile().getPath(), FUNCTIONS_FILE);
        if (functionsFile.exists()) {
          pb.addPackageFromDrl(new FileReader(functionsFile));
        }
      }
      if (isFormat(Format.DRL)){
        pb.addPackageFromDrl(new FileReader(file));
      }else if (isFormat(Format.DRL)){
        pb.addPackageFromDrl(new StringReader(DroolsHelper.compileDTabletoDRL(file, InputType.XLS)));
      }
      Package check=pb.getPackage();
      if (check == null) { // compilation error - the rule is syntactically incorrect
        for (int i = 0; i < pb.getErrors().getErrors().length; i++) {
          DroolsError msg = pb.getErrors().getErrors()[i];
          addCompilationError(msg.getMessage());
        }
      } else if (check != null && !check.isValid()) {
        addDependencyError(check.getErrorSummary());
        resBuilder.addPackage(pb.getPackage());
      }
      resBuilder.addPackage(check);
    }
    this.pkg=resBuilder.getPackage();
  }	
	/**
	 * 
	 * @return
	 * @throws IOException
	 */
	public byte[] toByteArray() throws IOException{
	  if (pkg!=null){
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      DroolsObjectOutputStream doos = new DroolsObjectOutputStream(baos);
      doos.writeObject(pkg);
      return baos.toByteArray();
	  }else{
	    return new byte[]{};
//	    throw new IOException("Package not built yet");
	  }
	}
	
	public void addRuleFile(File ruleFile){
	  ruleFiles.add(ruleFile);
	}
	
	public List<File> getRuleFiles() {
    return ruleFiles;
  }

//  public String getCompiledPackage() throws UnsupportedEncodingException, DroolsParserException, IOException{
//	  return FileIO.toBase64(DroolsHelper.compileRuletoPKG(this));
//	}
  

	
	/**
	 * Given a comma separated list of file extensions, this method returns a regular expression to match them
	 * @param extensions
	 * @return
	 */
	private static String buildRE(String extensions){
		//String RE="[a-zA-Z0-9-_]+\\.({0})$";
	  String RE=".+\\.({0})$";
		String[] xtns=extensions.split(",");
		for (int i = 0; i < xtns.length; i++) {
			String xtn = "("+xtns[i]+")";
			if (i<xtns.length-1)
				xtn+="|{0}";
			RE=MessageFormat.format(RE, xtn);
		}
		return RE;
	}
	
	/**
	 * Reads the contents of a single file into a useful internal object structure 
	 * @param file
	 * @return
	 * @throws FileNotFoundException
	 */
//	public static PackageFile extract(File file, CmdArgsParser options) throws FileNotFoundException, UnsupportedEncodingException{
//		PackageFile result=new PackageFile();
//		result.setFormat(FileIO.getExtension(file));
//		result.setBinary(!result.getFormat().endsWith(DRL_FILE_EXTENSION));
//		if (!result.isBinary()){
//			String content=FileIO.readAll(new FileInputStream(file));
//			int packageLoc=content.indexOf(PH_PACKAGE_START); //usually 0
//			int ruleLoc=getRuleStart(content, 0);// variable
//      result.setPackageName(getPackageName(file, options)); //get package name from directory structure
//			if (ruleLoc<0) return result; // there are no rule's in this file (perhaps functions or other?)
//			String imports=content.substring(packageLoc, ruleLoc);
//			result.setImports(imports);
//			
//			try {
//				boolean moreRules = true;
//				while (moreRules) {
//					int endLoc = getLoc(content, ruleLoc, RULE_END_MATCHERS)+4;
//					String ruleContents = content.substring(ruleLoc, endLoc);
//					ruleLoc = getRuleStart(content, endLoc);
//					moreRules = ruleLoc >= 0;
//					Rule rule = new Rule(findRuleName(ruleContents, options), ruleContents);
//					result.getRules().put(rule.getRuleName(), rule);
//				}
//			} catch (StringIndexOutOfBoundsException e) {
//				System.err.print("Error with file: " + file.getName() + "\n");
//			}
//		}else{
//			//binary format (ie. xls)
//			String content=FileIO.readAllAsBase64(file);
//			result.setPackageName(getPackageName(file, options));
//			result.getRules().put(file.getName(), new Rule(file.getName(), content));
//		}
//		
//		return result;
//	}
	
	 /**
   * returns "approval.determine" where path is /home/mallen/workspace/rules/approval/determine/1.0.0.SNAPSHOT/file.xls
   * and options "start" is "rules" and end is "[0-9|.]*[SNAPSHOT]+[0-9|.]*" ie. any number, dot and word SNAPSHOT
   * @param file
   * @param options
   * @return
   */
	 private static String getPackageName(File directory, CmdArgsParser options) {
    String startPath = directory.getPath();
    Matcher m = Pattern.compile("([^/]+)").matcher(startPath);
    List<String> lpath = new ArrayList<String>();
    while (m.find())
      lpath.add(m.group());
    String[] path = lpath.toArray(new String[lpath.size()]);
    StringBuffer sb = new StringBuffer();
    for (int i = path.length - 1; i >= 0; i--) {
      String dir = path[i];
      if ((dir.matches(options.getOption(Parameters.OPTIONS_PACKAGE_EXCLUDE))))
        continue;
      if ((dir.equals(options.getOption(Parameters.OPTIONS_PACKAGE_START))))
        break; //since we are working in reverse, it's time to exit
      sb.insert(0, PACKAGE_DELIMETER).insert(0, dir);
    }
    if (sb.substring(sb.length() - 1).equals(PACKAGE_DELIMETER))
      sb.delete(sb.length() - 1, sb.length());

    return sb.toString();
  }
	
	/**
	 * Gets the start position of the next rule in the package (<param>contents</param>)
	 * @param contents
	 * @param startLoc
	 * @return
	 */
	private static int getRuleStart(String contents, int startLoc){
		return getLoc(contents, startLoc, RULE_START_MATCHERS);
	}
	
	private static int getLoc(String contents, int startLoc, String[] markers){
		int[] a=new int[markers.length];
		for (int i = 0; i < markers.length; i++) {
			String marker = markers[i];
			a[i]=contents.indexOf(marker, startLoc);
		}
		
		//sort
		int i,j,tmp;
		for (int x=0;x<a.length;x++){
			i = x;
			for (j=x+1;j<a.length;j++){ 
				if (a[j] < a[i]) 
					i =j; 
			}
			tmp = a[x]; 
			a[x]=a[i]; 
			a[i]=tmp; 
		}
		for (int k = 0; k < a.length; k++) {
			if (a[k]>=0)
			  return a[k]; //return the lowest non-negative number
		}
		return -1;
	}
	
	/**
	 * returns the rule name given the entire rule content
	 * @param ruleContents
	 * @return
	 */
	private static String findRuleName(String ruleContents, CmdArgsParser options){
		//TODO: this is incorrect - what if a rule starts 'rule"rule1"'??? use the getRuleStart method to find the beginning
	  String name=ruleContents.substring(ruleContents.indexOf(PH_RULE_START)+PH_RULE_START.length(), ruleContents.indexOf(PH_NEWLINE)).replaceAll("\"", "").trim();
		if (!name.matches("[^'^/^<^>.]+")){ //Guvnor seems to not like some characters
		  if ("true".equals(options.getOption(Parameters.OPTIONS_VERBOSE)))
			  System.out.println("WARNING: fixing invalid rule name [old name="+name+"]");
			name=name.replaceAll("'", ""); //remove all ' chars since they are not valid in rule names
			name=name.replaceAll("/", "-"); //remove all / chars since they are not valid in rule names
			name=name.replaceAll("<", "&lt;"); //remove all < chars since they are not valid in rule names
			name=name.replaceAll(">", "&gt;"); //remove all > chars since they are not valid in rule names
		}
		return name;
	}
	
	// GETTERS/SETTERS for PackageFile object
	
	public boolean isFormat(Format isFormat){
    if (ruleFiles!=null && ruleFiles.size()>0){
      String name=ruleFiles.get(0).getName().toLowerCase();
	    return name.endsWith(isFormat.value);
    }
    return false;
	}
	public String getFormat() {
	  if (ruleFiles!=null && ruleFiles.size()>0){
	    String name=ruleFiles.get(0).getName().toLowerCase();
	    if (name.endsWith("drl")){
	      return "drl";
	    }else if (name.endsWith("xls")){
	      return "xls";
	    }
	  }
		return "";
	}
//	public void setFormat(String format) {
//		this.format = format;
//	}
	public File getFile() {
		return file;
	}
	public void setFile(File file) {
		this.file = file;
	}
  public String getDependencyErrors() {
    return dependencyErrors;
  }
  public void setDependencyErrors(String dependencyErrors) {
    this.dependencyErrors = dependencyErrors;
  }
  public boolean hasDependencyErrors() {
    return dependencyErrors.length()>0;
  }
  public void addDependencyError(String dependencyError) {
    this.dependencyErrors+=dependencyError+"\n";
  }
  public String getCompilationErrors() {
    return compilationErrors;
  }
  public void setCompilationErrors(String compilationErrors) {
    this.compilationErrors = compilationErrors;
  }
  public boolean hasCompilationErrors() {
    return compilationErrors.length()>0;
  }
  public void addCompilationError(String compilationError) {
    this.compilationErrors+=compilationError+"\n";
  }
  public boolean hasErrors(){
    return hasCompilationErrors() || hasDependencyErrors();
  }

  public Package getPkg() {
    return pkg;
  }

  public void setPkg(Package pkg) {
    this.pkg = pkg;
  }

  public String getImports() {
    return imports;
  }

  public void addImports(String imports) {
    //strip out any "package " lines from additional imports
    StringBuffer sb=new StringBuffer(imports);
    if (imports.length()>0){
      int posPackage=imports.indexOf("package ");
      sb.delete(posPackage, imports.indexOf("\n", posPackage));
    }
    this.imports=new StringBuffer().append(imports).append("\n").append(sb).toString();
  }

  public Map<String, Rule> getRules() {
    return rules;
  }

  public void setRules(Map<String, Rule> rules) {
    this.rules = rules;
  }
 
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
 
  public String toString(){
    return "PackageFile[name="+name+",format="+getFormat()+"]";
  }
}