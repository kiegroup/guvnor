package org.jboss.drools.guvnor.importgenerator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.drools.compiler.DroolsParserException;
import org.jboss.drools.guvnor.importgenerator.CmdArgsParser.Parameters;
import org.jboss.drools.guvnor.importgenerator.utils.FileIO;
import org.jboss.drools.guvnor.importgenerator.utils.Logger;
import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.joda.time.Seconds;

/**
 * a BRMS import file generator for drl and xml decision table files
 * 
 * @author <a href="mailto:mallen@redhat.com">Mat Allen</a>
 */
public class ImportFileGenerator implements Constants{
  private Logger logger=null;
	private CmdArgsParser options=null;
	private String BASE_DIR=System.getProperty("user.dir");
	public enum PackageObjectType{ PACKAGE, PACKAGE_SNAPSHOT }
  public enum RuleObjectType{ RULE, SNAPSHOT_RULE }
  
	/**
	 * The main action method
	 * @param packages
	 * @return
	 * @throws Exception
	 */
	public String generateImportFile(Map<String, PackageFile> packages) throws Exception {
		// go thru each replacer definition creating drl template replacements
		//TODO: what is the org.drools.io.RuleSetReader ??? is this what Guvnor uses this to read the .drl file parts?
		String draftStateReferenceUUID=GeneratedData.generateUUID();
		String categoryReferenceUUID=GeneratedData.generateUUID();
		
		//reporting only
		int cok=0, cerror=0, derror=0, terror=0, total=0;
		
		StringBuffer packageContents = new StringBuffer();
		StringBuffer snapshotContents = new StringBuffer();
		double i=0,pct=0;
		for (Iterator<String> it = packages.keySet().iterator(); it.hasNext();) {
		  double newPct=(int)(++i/(double)packages.size()*100);
		  pct=newPct;
			String packageName = (String) it.next();
			logger.debug(new DecimalFormat("##0").format(pct)+"% - "+packageName);
      PackageFile packageFile=packages.get(packageName);
			
			Map<String, Object> context=new HashMap<String, Object>();
			context.put("file", packageFile.getFile());
			context.put("draftStateReferenceUUID", draftStateReferenceUUID);
			context.put("categoryReferenceUUID", categoryReferenceUUID);
			context.put("packageFile", packageFile);
			
			//extract the rule contents
			StringBuffer ruleContents = new StringBuffer();
			StringBuffer snapshotRuleContents = new StringBuffer();
			Map<String, Rule> rules=packageFile.getRules();
			packageFile.buildPackage();
			
			for (Iterator<String> it2 = rules.keySet().iterator(); it2.hasNext();) {
				String ruleName=(String)it2.next();
				Rule rule=(Rule)rules.get(ruleName);
				context.put("rule", rule);
				//inject the rule values into the rule template
				ruleContents.append(MessageFormat.format(readTemplate(MessageFormat.format(TEMPLATES_RULE, packageFile.getFormat())), getRuleObjects(context/*, RuleObjectType.RULE*/)));
				
				//inject the snapshot rule values in the the snapshot rule template
				snapshotRuleContents.append(MessageFormat.format(readTemplate(MessageFormat.format(TEMPLATES_SNAPSHOT_RULE, packageFile.getFormat())), getRuleObjects(context/*, RuleObjectType.SNAPSHOT_RULE*/)));
			}
			
			//inject the rule(s) into the package into the package contents
			String packageTemplate = readTemplate(TEMPLATES_PACKAGE);// FileIO.readAll(new FileInputStream(new File(TEMPLATES_FOLDER, TEMPLATES_PACKAGE)));
			packageContents.append(MessageFormat.format(packageTemplate, getPackageObjects(context, ruleContents, PackageObjectType.PACKAGE)));
			
			//inject the snapshot values into the snapshot contents
			if (options.getOption(Parameters.OPTIONS_SNAPSHOT_NAME)!=null){
			  snapshotContents.append(MessageFormat.format(readTemplate(TEMPLATES_SNAPSHOT), getPackageObjects(context, snapshotRuleContents, PackageObjectType.PACKAGE_SNAPSHOT)));
			}
			
			//display status of each packageFile
			total++;
      if (packageFile.hasErrors()) {
        terror++;
        if (packageFile.hasCompilationErrors()){
          cerror++;
          logger.debugln(" - [COMPILATION/DEPENDENCY ERRORS]");
          if ("true".equals(options.getOption(Parameters.OPTIONS_VERY_VERBOSE))){
            logger.debugln(packageFile.getCompilationErrors().trim());
            logger.debugln(packageFile.getDependencyErrors().trim());
          }
        } else if (packageFile.hasDependencyErrors()) {
          derror++;
          logger.debugln(" - [DEPENDENCY ERRORS]");
          if ("true".equals(options.getOption(Parameters.OPTIONS_VERY_VERBOSE))){
            logger.debugln(packageFile.getDependencyErrors().trim());
          }
        }
      } else{
        cok++; //increment the "total rules compiled successfully"
        logger.debugln(" - [OK]");
      }
    }
		
		//replace the placemarkers with the package data
		String parentContents = MessageFormat.format(readTemplate(TEMPLATES_PARENT), new Object[]{ 
				packageContents
				,categoryReferenceUUID
				,draftStateReferenceUUID
				,GeneratedData.getTimestamp()
				,getSnapshotContents(snapshotContents)
				});
		
		//write a summary report
		logger.debugln("==========================");
		logger.debugln("===  PACKAGE SUMMARY   ===");
		logger.debugln("==========================");
		logger.debugln(" Rules compiled OK:   "+ NumberFormat.getInstance().format(cok));
		logger.debugln(" Errors:              "+ NumberFormat.getInstance().format(terror));
		//comp or dep errors can no longer be detected accurately since many drl file can be in a single package
//    logger.logln(" Compilation errors:  "+ NumberFormat.getInstance().format(cerror));
//    logger.logln(" Dependency errors:   "+ NumberFormat.getInstance().format(derror));
		logger.debugln("                      ____");
		logger.debugln(" Total:               "+ NumberFormat.getInstance().format(total));
		logger.debugln("==========================");
		
		return parentContents;
	}
	
  /**
   * returns a drools-5.0 formatted xml file for use with a drools 5.0 knowledge agent
   * @param packages
   * @return
   * @throws Exception
   */
  public String generateKnowledgeAgentInitFile(Map<String, PackageFile> packages) throws Exception {
      StringBuffer kagentInitContents = new StringBuffer();
      String kagentChildTemplate = readTemplate(TEMPLATES_KAGENT_CHILD_INIT);
      StringBuffer kagentChildContents=new StringBuffer();
      for (Iterator<String> it = packages.keySet().iterator(); it.hasNext();) {
          String packageName = (String) it.next();
          PackageFile packageFile=packages.get(packageName);
          kagentChildContents.append(MessageFormat.format(kagentChildTemplate, new Object[]{options.getOption(Parameters.OPTIONS_KAGENT_CHANGE_SET_SERVER), packageFile.getName()+"/"+options.getOption(Parameters.OPTIONS_SNAPSHOT_NAME), "PKG"}));
      }
      String kagentParentTemplate = readTemplate(TEMPLATES_KAGENT_PARENT_INIT);
      kagentInitContents.append(MessageFormat.format(kagentParentTemplate, new Object[]{kagentChildContents.toString()}));
      return kagentInitContents.toString();
  }
	
	
	private StringBuffer getSnapshotContents(StringBuffer snapshotContents){
	  if (options.getOption(Parameters.OPTIONS_SNAPSHOT_NAME)!=null){
	    return snapshotContents;
	  }
	  return new StringBuffer("");
	}
	
	private String readTemplate(String templateConst) throws FileNotFoundException{
		return FileIO.readAll(new FileInputStream(new File(new File(BASE_DIR, TEMPLATES_FOLDER), templateConst)));
	}

	private Object[] getPackageObjects(Map<String, Object> context, StringBuffer ruleContents, PackageObjectType type) throws UnsupportedEncodingException, DroolsParserException, IOException{
		List<String> objects=new LinkedList<String>();
		PackageFile packageFile=(PackageFile)context.get("packageFile");
		switch (type){
		case PACKAGE:
		  objects.add(packageFile.getName());
		  objects.add(getCreator());
		  objects.add(packageFile.getImports());
		  objects.add(ruleContents.toString());
		  objects.add(GeneratedData.generateUUID());
		  objects.add(GeneratedData.generateUUID());
		  objects.add(GeneratedData.generateUUID());
		  objects.add((String)context.get("draftStateReferenceUUID"));
		  objects.add(GeneratedData.getTimestamp());
		break;
		case PACKAGE_SNAPSHOT:
	    objects.add(packageFile.getName());
      objects.add(packageFile.getName().substring(packageFile.getName().lastIndexOf(".")+1));// //aka the title
      objects.add(options.getOption(Parameters.OPTIONS_SNAPSHOT_NAME));
      objects.add(getCreator()); //3
      objects.add(packageFile.getImports()); //4
      objects.add(ruleContents.toString()); //5
      objects.add((String)context.get("draftStateReferenceUUID"));
      objects.add(GeneratedData.getTimestamp()); //7
      //objects.add(FileIO.toBase64(DroolsHelper.compileRuletoPKG(packageFile))); //8
      objects.add(FileIO.toBase64(packageFile.toByteArray()));
      objects.add(GeneratedData.generateUUID()); //snapshot uuid
      objects.add(GeneratedData.generateUUID()); //snapshot base+predecessor uuid
      objects.add(GeneratedData.generateUUID()); //assets uuid
      objects.add(GeneratedData.generateUUID()); //assets base+predecessor uuid
      objects.add(GeneratedData.generateUUID()); //drools uuid
      objects.add(GeneratedData.generateUUID()); //drools base+predecessor uuid

		  break;
		}
		return objects.toArray(new Object[objects.size()]);
	}


	private Object[] getRuleObjects(Map<String, Object> context/*, RuleObjectType type*/){
		List<String> objects=new LinkedList<String>();
		PackageFile packageFile=(PackageFile)context.get("packageFile");
		Rule rule=(Rule)context.get("rule");
		
		objects.add(rule.getRuleName());
		objects.add(packageFile.getName());
		objects.add(rule.getContent());
		objects.add(GeneratedData.generateUUID()); //rule uuid
		objects.add((String)context.get("draftStateReferenceUUID"));
		objects.add((String)context.get("categoryReferenceUUID"));
		objects.add(getCreator());
		objects.add(GeneratedData.getTimestamp());
		objects.add(packageFile.getFormat());
		objects.add(GeneratedData.generateUUID()); //base version + predecessor (currently only used in snapshot)
		if (packageFile.getFormat().equals("xls")){
			objects.add(((File)context.get("file")).getName());
		}
		return objects.toArray(new Object[]{});
	}
	
	
	/**
	 * get command line arguments
	 * @return
	 */
	private String getCreator(){
		if (options.getOption(Parameters.OPTIONS_CREATOR)!=null)
			return options.getOption(Parameters.OPTIONS_CREATOR);
		return DEFAULT_CREATOR;
	}
	
	public void run(CmdArgsParser options) throws Exception{
    SimpleDateFormat fmt=new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
    Date startd=new Date();
    DateTime start=new DateTime(startd);
    this.options=options;
    BASE_DIR=options.getOption(Parameters.OPTIONS_BASE_DIR);
    logger=Logger.getLogger(ImportFileGenerator.class, options);
    logger.debugln("Running BRMS Import Generator (started "+ fmt.format(startd) +"):");
    
		logger.debugln("Scanning directories...");
		Map<String, PackageFile> details=PackageFile.buildPackages(options);
		
		logger.debugln("Generating 'Guvnor import data'...");
		String guvnorImport=generateImportFile(details);
		File guvnorImportFile=getFile(options.getOption(Parameters.OPTIONS_OUTPUT_FILE));
		logger.debugln("Writing 'Guvnor import data to disk' ("+ guvnorImportFile.getAbsolutePath() +")");
		FileIO.write(guvnorImport, guvnorImportFile);

		if (options.getOption(Parameters.OPTIONS_KAGENT_CHANGE_SET_FILE)!=null){
		  logger.debugln("Generating 'Knowledge agent changeset' data...");
      String kagentChangeSet=generateKnowledgeAgentInitFile(details);
      File kagentChangeSetFile=getFile(options.getOption(Parameters.OPTIONS_KAGENT_CHANGE_SET_FILE));
      logger.debugln("Writing 'Knowledge agent changeset' to disk ("+ kagentChangeSetFile.getAbsolutePath() +")");
      FileIO.write(kagentChangeSet, kagentChangeSetFile);
		}
		
    DateTime end=new DateTime(System.currentTimeMillis());
    int m=Minutes.minutesBetween(start, end).getMinutes();
    int s=Seconds.secondsBetween(start, end).getSeconds()-(m*60);
    logger.debugln("Finished in ("+m+"m"+s+"s)");
	}
	
	private File getFile(String fileLoc){
	  if (fileLoc.startsWith("/") || fileLoc.startsWith("~")){
	    return new File(fileLoc);
	  }else
	    return new File(BASE_DIR, fileLoc);
	}

	public static void main(String[] args) {
		ImportFileGenerator i = new ImportFileGenerator();
		try {
		  CmdArgsParser cmd=new CmdArgsParser();
		  cmd.parse(args);
			i.run(cmd);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
