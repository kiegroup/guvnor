package org.jboss.drools.guvnor.importgenerator.test;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import org.drools.RuleBase;
import org.drools.StatelessSession;
import org.drools.StatelessSessionResult;
import org.drools.agent.HttpClientImpl;
import org.drools.agent.RuleAgent;
import org.drools.common.DroolsObjectInputStream;
import org.drools.definition.KnowledgePackage;
import org.drools.definitions.impl.KnowledgePackageImp;
import org.drools.rule.Package;

/**
 * Test class to check that rules have been deployed correctly.
 * 
 * @author <a href="mailto:mallen@redhat.com">Mat Allen</a>
 */
public class TestRuleDeployment {
  public enum RuleAgentType{ HTTP, FILE_DRL, FILE_PKG }
	private static final String ruleServer="http://localhost:8080/brms";
	private static final String packageName="ping";
	private static final String snapshotName="1.0.0-SNAPSHOT";
	
	public void run(){
	  boolean executeRules=false;
	  Properties props=getProps(packageName, snapshotName);
	  String packSnap=packageName +"/"+ snapshotName;
	  String urlstr=(String)props.get("url");
	  try{
	    System.out.println("1: Drools method - looking up ["+packSnap+"]");
	    HttpClientImpl c=new HttpClientImpl();
	    Package pa=c.fetchPackage(new URL(urlstr));
	    System.out.println("1: found ["+ pa.getName() +"; clazz="+ pa.getClass().getName() +"]");
	    executeRules=true;
	  }catch(Exception e){
	    System.out.println("ERROR "+ e.getClass().getName() +" - "+ e.getMessage());
	  }
	  
	  try{
      System.out.println("2: My method - looking up ["+packSnap+"]");
      URLConnection cnn = new URL(urlstr).openConnection();
      InputStreamReader in=new InputStreamReader(cnn.getInputStream());
      byte[] b=new byte[cnn.getInputStream().available()];
      cnn.getInputStream().read(b);
      in.close();
      ByteArrayInputStream bais=new ByteArrayInputStream(b);
      //ObjectInputStream oin=new ObjectInputStream(bais); //dont use the ObjectInputSteam because the DroolsObject... does some specific ops
      DroolsObjectInputStream dois=new DroolsObjectInputStream(bais);
      
      Object o=dois.readObject();
      if (o instanceof List){
        List<KnowledgePackage> pl=((List<KnowledgePackage>)o);
        System.out.println("2: Found Package in a List ["+pl.get(0).getName()+"]");
      }else if (o instanceof KnowledgePackageImp){
        Package p=((KnowledgePackageImp)o).pkg;
        System.out.println("2: Found a KnowledgePackageImp ["+p.getName()+"]");
      }else if (o instanceof Package){
        Package p=((Package)o);
        System.out.println("2: Found a Package ["+p.getName()+"]");
      }
	  }catch(Exception e){
      System.err.println("ERROR "+ e.getClass().getName() +" - "+ e.getMessage());
	  }
	  
	  if (!executeRules) return; //exit if we've not found the package in the brms to execute
	  
	  RuleAgentFactory factory=new RuleAgentFactory(props);
		RuleAgent agent=factory.get(RuleAgentType.HTTP);
		RuleBase rb=agent.getRuleBase();
		
		StatelessSession s=rb.newStatelessSession();
		Collection<Object> facts=new LinkedList<Object>();
		facts.add(new String("ping"));
		System.out.print("ping...");
		StatelessSessionResult result=s.executeWithResults(facts);
		Iterator it=result.iterateObjects();
		while(it.hasNext()) {
			Object o=it.next();
			if (o instanceof String){
			  System.out.println(((String)o));
			}
		}
	}
	
	private Properties getProps(String packageName, String snapshotName){
		Properties r=new Properties();
		r.put("url", ruleServer+"/org.drools.guvnor.Guvnor/package/"+packageName+"/"+snapshotName);
		r.put("file", "my_rules/permissions/zone1/1.0.0-SNAPSHOT/permissions.");
		r.put("name", "RuleAgent for "+ packageName); //optional
		r.put("poll", "30");
		r.put("localCacheDir", "/tmp");
		r.put("newInstance", "true");
		return r;
	}
	
	 public static void main(String[] args){
	    new TestRuleDeployment().run();
	  }
	
	class RuleAgentFactory{
    Properties props;
    public RuleAgentFactory(Properties props) {
      this.props=props;
    }
    public RuleAgent get(RuleAgentType type){
      Properties p=(Properties)props.clone();
      switch (type){
      case HTTP:
        p.remove("file");
        break;
      case FILE_DRL:
        p.remove("url");
        p.put("file", p.get("file")+".drl");
        break;
      case FILE_PKG:
        p.remove("url");
        p.put("file", p.get("file")+".pkg");
        break;
      }
      return RuleAgent.newRuleAgent(p);
    }
  }
}

