package org.jboss.drools.guvnor.importgenerator.xdeleteme;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Properties;

import org.apache.commons.codec.binary.Base64;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.StatefulSession;
import org.drools.StatelessSession;
import org.drools.StatelessSessionResult;
import org.drools.agent.RuleAgent;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.common.InternalRuleBase;
import org.drools.common.WorkingMemoryAction;
import org.drools.commons.jci.compilers.CompilationResult;
import org.drools.commons.jci.compilers.JaninoJavaCompiler;
import org.drools.commons.jci.compilers.JaninoJavaCompilerSettings;
import org.drools.commons.jci.problems.CompilationProblem;
import org.drools.commons.jci.readers.MemoryResourceReader;
import org.drools.commons.jci.stores.ResourceStore;
import org.drools.compiler.DroolsParserException;
import org.drools.compiler.PackageBuilder;
import org.drools.compiler.PackageBuilderConfiguration;
import org.drools.compiler.RuleBaseLoader;
import org.drools.io.Resource;
import org.drools.io.ResourceFactory;
import org.drools.io.impl.ClassPathResource;
import org.drools.repository.AssetItem;
import org.drools.reteoo.ReteooStatefulSession;
import org.drools.reteoo.ReteooWorkingMemory;
import org.drools.reteoo.ReteooWorkingMemory.WorkingMemoryReteAssertAction;
import org.drools.rule.Package;
import org.drools.rule.builder.RuleBuildContext;
import org.drools.rule.builder.RuleBuilder;
import org.drools.rule.builder.dialect.java.JavaDialectConfiguration;
import org.drools.runtime.rule.FactHandle;
import org.drools.runtime.rule.WorkingMemory;
import org.jboss.drools.guvnor.importgenerator.utils.FileIO;

public class TestRuleCompilation2 {
  public static void main(String[] args) {
    new TestRuleCompilation2().run();
  }
  
  private void writeExternal(String name, java.io.Externalizable o) throws IOException{
    ByteArrayOutputStream baout = new ByteArrayOutputStream();
    ObjectOutputStream out = new ObjectOutputStream(baout);
    o.writeExternal(out);
    byte[] base64bytes = Base64.encodeBase64(baout.toByteArray());
    String base64String = new String(base64bytes, "utf-8");
    System.out.println(base64String.substring(0, 75)+" - "+name);
    System.out.println(FileIO.fromBase64(base64bytes).substring(0, 500));
  }
  
  private void buildDRLtoPKG(String path, String outputPackageFile){
    try {
      KnowledgeBase kBase = KnowledgeBaseFactory.newKnowledgeBase();
      KnowledgeBuilder kBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
      Resource resource=ResourceFactory.newFileResource(path);
      kBuilder.add(resource, ResourceType.DRL);
      if (kBuilder.hasErrors()) {
        for (KnowledgeBuilderError err : kBuilder.getErrors()) {
          System.out.println(err.toString());
        }
        throw new IllegalStateException("DRL errors");
      }
      OutputStream os = new FileOutputStream(outputPackageFile);
      ObjectOutputStream oos = new ObjectOutputStream(os);

      oos.writeObject(kBuilder.getKnowledgePackages());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void run() {
    try {
      String fileLocation = "rules/approval/determine/initialize/1.0.0.SNAPSHOT/resetCaseApproval.drl";
      String pkgFileLocation = "rules/approval/determine/initialize/1.0.0.SNAPSHOT/resetCaseApproval.pkg";
      String originalFileLocation = "binaryfile.bin";
      PackageBuilder builder = new PackageBuilder();
      
      buildDRLtoPKG(fileLocation, pkgFileLocation);
      if (true) return;
//      byte[] b=FileIO.readAll(new File(originalFileLocation));
//      ObjectInputStream in=new ObjectInputStream(new ByteArrayInputStream(Base64.decodeBase64(b)));
//      int x=in.available();
//      Object o=in.readObject();
//      System.out.println(o);
      
      //builder.addPackageFromDrl(new InputStreamReader(new FileInputStream(new File(fileLocation))));
      
      //Package
      writeExternal("package object", builder.getPackage());
      
//      //RuleBase
//      RuleBase rb=RuleBaseFactory.newRuleBase();
//      rb.addPackage(builder.getPackage());
//      writeExternal("ruleBase object", rb);
      
      //original file
      byte[] compfile=FileIO.readAll(new File("binaryfile.bin"));
      System.out.println(new String(compfile, "utf-8").substring(0, 75) +" - original base64 file");
      
      System.out.println(FileIO.fromBase64(compfile).substring(0, 500) + " - original raw file");
      
      //ruleBase.writeExternal(out);
      
      //RuleBase ruleBase = RuleBaseFactory.newRuleBase();
//      ReteooStatefulSession m=(ReteooStatefulSession)ruleBase.newStatefulSession();
//      m.insert(new String("say "));
      //ruleBase.addPackage(pkg);
      
//      JavaDialectConfiguration javaDialectConf = new JavaDialectConfiguration();
//      javaDialectConf.setCompiler(JavaDialectConfiguration.JANINO);
//      javaDialectConf.setJavaLanguageLevel("1.5");
//      PackageBuilderConfiguration conf = javaDialectConf.getPackageBuilderConfiguration();
//      PackageBuilder builder = new PackageBuilder(conf);
      
//      try {
//        builder.addPackageFromDrl(new InputStreamReader(new FileInputStream(new File(fileLocation))));
//        builder.compileAll();
//        if (builder.hasErrors()) {
//          System.out.println(builder.getErrors().toString());
//          throw new RuntimeException("Unable to compile \""+new File(fileLocation).getName()+"\".");
//        }
//
//        // get the compiled package (which is serializable)
//        org.drools.rule.Package pkg = builder.getPackage();
//
//        // add the package to a rulebase (deploy the rule package).
//        RuleBase ruleBase = RuleBaseFactory.newRuleBase();
//        ruleBase.addPackage(pkg);
//
//        StatefulSession session = ruleBase.newStatefulSession();
//
//        session.insert(new String("say "));
//
//        session.fireAllRules();
//      } catch (Exception e) {
//        e.printStackTrace();
//      }

      // ruleBase.addPackage(pkg);
      // ByteArrayOutputStream baout = new ByteArrayOutputStream();
      // ObjectOutputStream out = new ObjectOutputStream(baout);
      // ruleBase.writeExternal(out);
      // byte[] bytes = baout.toByteArray();
      // byte[] base64bytes = Base64.encodeBase64(bytes);
      // String base64String = new String(base64bytes, "utf-8");
      // System.out.println(base64String.substring(0, 100));

    } catch (Exception e) {
      e.printStackTrace();
    }

  }
//
//  public void testCompile() throws Exception {
//    WorkingMemory localWorkingMemory = loadDrlRulesFromFile("myDrlFile.drl").newWorkingMemory();
//    new WorkingMemoryReteAssertAction().
//
//    // Place the Java Bean in the working memory , keep a handle to it
//    FactHandle currentHandle = localWorkingMemory.assertObject(new SearchModel());
//
//    // use Drools *without* any Java intervention
//    localWorkingMemory.fireAllRules();
//    localWorkingMemory.retract(currentHandle);
//    System.out.println("SUCCESS");
//  }
//
//  private RuleBase loadDrlRulesFromFile(String drlFileName) {
//    RuleBase returnRuleBase = null;
//    InputStream iStreamRules = getFileIfExists(drlFileName);
//
//    // Convert the Stream into a reader that Drools uses to parse files
//    InputStreamReader drl = new InputStreamReader(iStreamRules);
//
//    System.out.println("Reading Rules from drl:" + drlFileName);
//    PackageBuilder builder = new PackageBuilder();
//    builder.addPackageFromDrl(drl);
//    Package pkg = builder.getPackage();
//
//    if (pkg != null) {
//      returnRuleBase = RuleBaseFactory.newRuleBase();
//      returnRuleBase.addPackage(pkg);
//    }
//    return returnRuleBase;
//  }
//
//  private InputStream getFileIfExists(String fileName) throws Exception {
//    InputStream rIStream = null;
//    System.out.println("Searching for File name:" + fileName);
//    try {
//      rIStream = new FileInputStream(fileName);
//    } catch (FileNotFoundException fnfe1) {
//      // log.debug("Could not find resource as file - attempting the classpath");
//      try {
//        ClassPathResource myClassPathReader = new ClassPathResource(fileName);
//        rIStream = myClassPathReader.getInputStream();
//        // log.debug("Found File via Classpath at path:" +
//        // myClassPathReader.getPath() + " file:" +
//        // myClassPathReader.getFile());
//      } catch (Exception fnfe2) {
//        fnfe2.printStackTrace();
//
//      }
//    }
//
//    return rIStream;
//
//  }

}
