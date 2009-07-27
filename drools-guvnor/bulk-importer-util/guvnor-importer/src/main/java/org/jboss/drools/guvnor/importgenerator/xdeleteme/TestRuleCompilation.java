package org.jboss.drools.guvnor.importgenerator.xdeleteme;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Properties;

import org.apache.commons.codec.binary.Base64;
import org.codehaus.janino.util.resource.FileResource;
import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.StatelessSession;
import org.drools.StatelessSessionResult;
import org.drools.agent.RuleAgent;
import org.drools.commons.jci.compilers.CompilationResult;
import org.drools.commons.jci.compilers.EclipseJavaCompiler;
import org.drools.commons.jci.compilers.EclipseJavaCompilerSettings;
import org.drools.commons.jci.compilers.JaninoCompilationProblem;
import org.drools.commons.jci.compilers.JaninoJavaCompiler;
import org.drools.commons.jci.compilers.JaninoJavaCompilerSettings;
import org.drools.commons.jci.compilers.JavaCompiler;
import org.drools.commons.jci.compilers.JavaCompilerFactory;
import org.drools.commons.jci.problems.CompilationProblem;
import org.drools.commons.jci.readers.MemoryResourceReader;
import org.drools.commons.jci.readers.ResourceReader;
import org.drools.commons.jci.stores.ResourceStore;
import org.drools.compiler.PackageBuilder;
//import org.drools.guvnor.server.builder.BRMSPackageBuilder;
//import org.drools.guvnor.server.contenthandler.ContentHandler;
//import org.drools.guvnor.server.contenthandler.ContentManager;
//import org.drools.guvnor.server.contenthandler.DRLFileContentHandler;
import org.drools.repository.AssetItem;
import org.drools.template.model.DRLOutput;
import org.jboss.drools.guvnor.importgenerator.utils.FileIO;

public class TestRuleCompilation {
	public static void main(String[] args){
		new TestRuleCompilation().run();
	}
	
	public void run(){
		try {
			String fileLocation = "rules/approval/determine/initialize/1.0.0.SNAPSHOT/resetCaseApproval.drl";
			PackageBuilder builder = new PackageBuilder();
			builder.addPackageFromDrl(new InputStreamReader(new FileInputStream(new File(fileLocation))));
			org.drools.rule.Package pkg = builder.getPackage();
			RuleBase ruleBase = RuleBaseFactory.newRuleBase();
			ruleBase.addPackage(pkg);
			ByteArrayOutputStream baout=new ByteArrayOutputStream();
			ObjectOutputStream out=new ObjectOutputStream(baout);
			ruleBase.writeExternal(out);
			byte[] bytes=baout.toByteArray();
		  byte[] base64bytes=Base64.encodeBase64(bytes);
		  String base64String=new String(base64bytes, "utf-8");
			System.out.println(base64String.substring(0, 100));
			
			new BufferedInputStream(new FileInputStream(fileLocation)).read(bytes);
      System.out.println(FileIO.toBase64(bytes).substring(0, 100));
      
      byte[] compfile=FileIO.readAll(new File("binaryfile.bin"));
      System.out.println(new String(compfile, "utf-8").substring(0, 100));
			
      System.out.println(FileIO.fromBase64(compfile).substring(0, 800));
      
      JaninoJavaCompiler compiler=new JaninoJavaCompiler();
      MemoryResourceReader rr=new MemoryResourceReader();
      
      rr.add("fred", FileIO.readAll(new File(fileLocation)));
      ResourceStore arg2=new ResourceStore(){
        public void remove(String x){
          System.out.println(x);
        }
        public void write(String x, byte[] b){
          System.out.println(x);
        }
        public byte[] read(String arg0) {
          return null;
        }
      };
      CompilationResult r=compiler.compile(new String[]{"fred"}, rr, arg2, this.getClass().getClassLoader(), new JaninoJavaCompilerSettings());
      CompilationProblem[] errors=r.getErrors();
      for (CompilationProblem e : errors) {
        System.out.println(e);
      }
      
//      DRLFileContentHandler h=new DRLFileContentHandler();
//      BRMSPackageBuilder b=new BRMSPackageBuilder();
//      AssetItem a=new AssetItem();
//      a.getRulesRepository().importRulesRepositoryFromStream(new FileInputStream(new File(fileLocation)));
//      h.compile(b, a, null);
      
      //from guvnor code
//      Properties ps = new Properties();
//      AssetItemIterator iter = pkg.listAssetsByFormat(new String[] {"properties", "conf"});
//      while(iter.hasNext()) {
//          AssetItem conf = iter.next();
//          conf.getContent();
//          Properties p = new Properties();
//          p.load(conf.getBinaryContentAttachment());
//          ps.putAll(p);
//      }
//      
//      ps.setProperty( DefaultPackageNameOption.PROPERTY_NAME, this.pkg.getName() );
//      builder = BRMSPackageBuilder.getInstance(BRMSPackageBuilder.getJars(pkg), ps);
//
//      if (compile && preparePackage()) {
//        ContentHandler h = ContentManager.getHandler(asset.getFormat());
//        IRuleAsset
//        
//        buildPackage();
//      }
      
      
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (true) return;
		RuleAgent agent=RuleAgent.newRuleAgent(getProps("package1"));
		StatelessSession s=agent.getRuleBase().newStatelessSession();
		Collection<Object> facts=new LinkedList<Object>();
		facts.add("ping");
		StatelessSessionResult result=s.executeWithResults(facts);
		
		Iterator it=result.iterateObjects();
		boolean worked=false;
		while(it.hasNext()) {
			Object o=it.next();
			if (o instanceof String){
				if (((String)o).equals("pong")){
					worked=true;
				}
			}
		}
		if (worked){
			System.out.println("SUCCESS!!!!");
		}else
			System.out.println("FAILED :-(");
	}
	
	private Properties getProps(String packageName){
		Properties r=new Properties();
		//r.put("url", System.getProperty("drools.package.url") + packageName);
		//r.put("dir", System.getProperty("drools.package.dir") + packageName);
		r.put("url", "http://");
		r.put("name", "ORS");
		r.put("poll", "30");
		r.put("localCacheDir", "/tmp");
		r.put("newInstance", "true");
		return r;
	}
}
