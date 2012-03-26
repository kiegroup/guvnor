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

package org.jboss.drools.guvnor.importgenerator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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
import org.drools.repository.utils.IOUtils;
import org.jboss.drools.guvnor.importgenerator.CmdArgsParser.Parameters;
import org.jboss.drools.guvnor.importgenerator.utils.FileIO;
import org.jboss.drools.guvnor.importgenerator.utils.Logger;
import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.joda.time.Seconds;

/**
 * a BRMS import file generator for drl and xml decision table files
 */
public class ImportFileGenerator implements Constants {

    private Logger logger=null;
    private CmdArgsParser options=null;
    private String BASE_DIR=System.getProperty("user.dir");
    public enum PackageObjectType{ PACKAGE, PACKAGE_SNAPSHOT, MODEL }
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
          context.put("draftStateReferenceUUID", draftStateReferenceUUID);
          context.put("categoryReferenceUUID", categoryReferenceUUID);
          context.put("packageFile", packageFile);

          //extract the rule contents
          StringBuffer ruleContents = new StringBuffer();
          StringBuffer snapshotRuleContents = new StringBuffer();
          Map<String, Rule> rules=packageFile.getRules();
          packageFile.buildPackage();

          for(String ruleName:rules.keySet()){
                Rule rule=(Rule)rules.get(ruleName);
                context.put("file", rule.getFile());
                context.put("rule", rule);
                String format=FileIO.getExtension(rule.getFile());
                context.put("format", format);
                //inject the rule values into the rule template
                ruleContents.append(MessageFormat.format(readTemplate(MessageFormat.format(TEMPLATES_RULE, format)), getRuleObjects(context/*, RuleObjectType.RULE*/)));

                //inject the snapshot rule values in the the snapshot rule template
                snapshotRuleContents.append(MessageFormat.format(readTemplate(MessageFormat.format(TEMPLATES_SNAPSHOT_RULE, format)), getRuleObjects(context/*, RuleObjectType.SNAPSHOT_RULE*/)));
          }

          String modelTemplate = readTemplate(TEMPLATES_MODEL);
          for(Model model:packageFile.getModelFiles()){
              context.put("model", model);
              ruleContents.append(MessageFormat.format(modelTemplate, getPackageObjects(context, new StringBuffer(model.getContent()), PackageObjectType.MODEL)));
          }
          // If no models in directory but parameter specified then upload the parameterized model
          if (packageFile.getModelFiles().size()<=0 && options.getOption(Parameters.OPTIONS_MODEL)!=null){
              File modelFile=new File(options.getOption(Parameters.OPTIONS_MODEL));
              String modelFileContent=FileIO.readAllAsBase64(modelFile);
              context.put("model", new Model(modelFile, modelFileContent));
              ruleContents.append(MessageFormat.format(modelTemplate, getPackageObjects(context, new StringBuffer(modelFileContent), PackageObjectType.MODEL)));
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
      try{
        String path=TEMPLATES_FOLDER + "/" + templateConst;
        InputStream in=getClass().getClassLoader().getResourceAsStream(path);
        if (null!=in){
          return IOUtils.toString(in);
        }else
        return FileIO.readAll(new FileInputStream(new File(new File(BASE_DIR, TEMPLATES_FOLDER), templateConst)));
      }catch (IOException e){
        throw new FileNotFoundException(e.getMessage());
      }
    }

    private Object[] getPackageObjects(Map<String, Object> context, StringBuffer contents, PackageObjectType type) throws UnsupportedEncodingException, DroolsParserException, IOException{
        List<String> objects=new LinkedList<String>();
        PackageFile packageFile=(PackageFile)context.get("packageFile");
        switch (type){
        case MODEL:
          Model model=(Model)context.get("model");
          objects.add(model.getFile().getName().substring(0, model.getFile().getName().lastIndexOf(".")));//wrapper title
          objects.add(getCreator());//creator
          objects.add(contents.toString());// packageFile.getModelAsBase64());//content
          objects.add(GeneratedData.generateUUID());//uuid
          objects.add(model.getFile().getName());//filename
          objects.add((String)context.get("draftStateReferenceUUID"));//state
          objects.add(GeneratedData.getTimestamp());//timestamp
          objects.add(packageFile.getName()); //package name
          break;

        case PACKAGE:
          objects.add(packageFile.getName());
          objects.add(getCreator());
          objects.add(packageFile.getImports());
          objects.add(contents.toString());
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
          objects.add(contents.toString()); //5
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
        objects.add((String)context.get("format"));
        objects.add(GeneratedData.generateUUID()); //base version + predecessor (currently only used in snapshot)
        if ("xls".equalsIgnoreCase((String)context.get("format"))){
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
    SimpleDateFormat fmt=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
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
