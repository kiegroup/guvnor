package org.jboss.drools.guvnor.importgenerator.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;

import org.drools.common.DroolsObjectOutputStream;
import org.drools.compiler.DroolsError;
import org.drools.compiler.DroolsParserException;
import org.drools.compiler.PackageBuilder;
import org.drools.decisiontable.InputType;
import org.drools.decisiontable.SpreadsheetCompiler;
import org.drools.rule.Package;
import org.jboss.drools.guvnor.importgenerator.PackageFile;

/**
 * drools helper class that provides functions to build drl and decision table to objects 
 * 
 * @author <a href="mailto:mallen@redhat.com">Mat Allen</a>
 */
public class DroolsHelper {
  private static final String FUNCTIONS_FILE="functions.drl";
  
  /**
   * returns an object byte array of a drl package file
   * @param drlFile
   * @return
   * @throws IOException
   * @throws DroolsParserException
   */
  public static byte[] compileRuletoPKG(PackageFile file) throws IOException, DroolsParserException {
    PackageBuilder pb = new PackageBuilder();
    String xtn=FileIO.getExtension(file.getFile()).toLowerCase();
    if (xtn.equals("drl")){
      File functionsFile=new File(file.getFile().getParentFile().getPath(), FUNCTIONS_FILE);
      if (functionsFile.exists()){
        pb.addPackageFromDrl(new FileReader(functionsFile));
      }
      pb.addPackageFromDrl(new FileReader(file.getFile()));
    }else if (xtn.equals("xls")){
      pb.addPackageFromDrl(new StringReader(compileDTabletoDRL(file.getFile(), InputType.XLS)));
    }
    
    Package pkg = pb.getPackage();
    if (pkg == null) { // compilation error - the rule is syntactically incorrect
      for (int i = 0; i < pb.getErrors().getErrors().length; i++) {
        DroolsError msg = pb.getErrors().getErrors()[i];
        file.addCompilationError(msg.getMessage());
      }
    } else if (pkg != null && !pkg.isValid()) { // dependency missing
      file.addDependencyError(pkg.getErrorSummary());
    }
    
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    DroolsObjectOutputStream doos = new DroolsObjectOutputStream(baos);
    doos.writeObject(pkg);
    return baos.toByteArray();
  }
  
  /**
   * compiles an xls or csv decision table into a rule (drl) language string
   * @param file
   * @return
   * @throws FileNotFoundException
   */
  public static String compileDTabletoDRL(File file, InputType type) throws FileNotFoundException{
    SpreadsheetCompiler compiler=new SpreadsheetCompiler();
    return compiler.compile(new FileInputStream(file), type);
  }
}
