package org.jboss.drools.guvnor.importgenerator.example.domain;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.drools.FactHandle;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.agent.KnowledgeAgent;
import org.drools.agent.KnowledgeAgentConfiguration;
import org.drools.agent.KnowledgeAgentFactory;
import org.drools.command.Command;
import org.drools.command.CommandFactory;
import org.drools.definition.KnowledgePackage;
import org.drools.io.ResourceChangeScannerConfiguration;
import org.drools.io.ResourceFactory;
import org.drools.io.impl.ResourceChangeScannerImpl;
import org.drools.runtime.ExecutionResults;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.StatelessKnowledgeSession;

public class KagentTest {
  private boolean changeScannerInitialised = false;
  private Map<String, KnowledgeAgent> cache = new HashMap<String, KnowledgeAgent>();

  public enum KnowledgeResourceType {
    DRL, PKG
  }

  public static void main(String[] args) {
    new KagentTest().run();
  }

  public void run() {
    initChangeScanner();
    Set<String> packageNames = new HashSet<String>();
    packageNames.add("ping.test/1.0.0-SNAPSHOT");
    if (true){
      KnowledgeAgent kagent=getKnowledgeAgent("ping.test/1.0.0-SNAPSHOT");
      StatelessKnowledgeSession s=kagent.newStatelessKnowledgeSession();
//      List facts=new ArrayList();
//      facts.add("ping");
      s.execute(Arrays.asList(CommandFactory.newGetObjects(), CommandFactory.newFireAllRules()));
//      for (Object x : facts) {
//        System.out.println("fact-"+x);
//      }
    }
    if (false) {
      StatefulKnowledgeSession s = createStatefulSession(packageNames);
//      List facts=new ArrayList();
//      facts.add("ping");
      org.drools.runtime.rule.FactHandle fact1=s.insert("ping");
      s.fireAllRules();
      System.out.println(fact1.toString());
//      for (Object x : facts) {
//        System.out.println(x);
//      }
      s.dispose();
    }
    ResourceFactory.getResourceChangeNotifierService().stop();
    ResourceFactory.getResourceChangeScannerService().stop();
  }

  public void initChangeScanner() {
//    if (!changeScannerInitialised) {
//      ResourceChangeScannerConfiguration c = ResourceFactory.getResourceChangeScannerService().newResourceChangeScannerConfiguration();
//      c.setProperty("drools.resource.scanner.interval", "10");
//      ((ResourceChangeScannerImpl) ResourceFactory.getResourceChangeScannerService()).reset();
//      ResourceFactory.getResourceChangeNotifierService().start();
//      ResourceFactory.getResourceChangeScannerService().start();
//      ResourceFactory.getResourceChangeScannerService().configure(c);
//    }
//    changeScannerInitialised = true;
  }

  public KnowledgeAgentConfiguration getConfig() {
    KnowledgeAgentConfiguration c = KnowledgeAgentFactory.newKnowledgeAgentConfiguration();
    c.setProperty("drools.agent.scanDirectories", "true");
    c.setProperty("drools.agent.scanResources", "true");
    c.setProperty("drools.agent.newInstance", "true");
    return c;
  }

  public StatelessKnowledgeSession createSession(Set<String> packageNames) {
    return getKnowledgeBase(packageNames).newStatelessKnowledgeSession();
  }

  public StatefulKnowledgeSession createStatefulSession(Set<String> packageNames) {
    return getKnowledgeBase(packageNames).newStatefulKnowledgeSession();
  }

  private KnowledgeBase getKnowledgeBase(Set<String> packageNames, boolean ignorMissing) {
    KnowledgeBase kb = KnowledgeBaseFactory.newKnowledgeBase();
    Collection<KnowledgePackage> kpl = new ArrayList<KnowledgePackage>();
    for (String packageName : packageNames) {
      try {
        KnowledgeAgent kagent = getKnowledgeAgent(packageName);
        kb.addKnowledgePackages(kagent.getKnowledgeBase().getKnowledgePackages());
      } catch (java.lang.IllegalArgumentException e) {
        // boolean packageDoesntExist=e.getMessage().startsWith("The directory")
        // && e.getMessage().endsWith("is not valid");
        if (ignorMissing) {
          continue;
        } else
          throw new RuntimeException(e.getMessage(), e);
      }
    }
    return kb;
  }

  private KnowledgeBase getKnowledgeBase(Set<String> packageNames) {
    return getKnowledgeBase(packageNames, false);
  }

  private KnowledgeAgent getKnowledgeAgent(String packageName) {
    if (cache.get(packageName) == null) {
      cache.put(packageName, createKnowledgeAgent(packageName));
    }
    return cache.get(packageName);
  }

  private KnowledgeAgent createKnowledgeAgent(String packageName) {
    initChangeScanner();
    KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
    KnowledgeAgent kagent = KnowledgeAgentFactory.newKnowledgeAgent("KAgent for " + packageName, kbase, getConfig());
    kagent.applyChangeSet(ResourceFactory.newReaderResource(new StringReader(getChangeSet(packageName))));
    return kagent;
  }

  private String getChangeSet(String packageName) {
    // packageName=toCustomPackage(packageName, ".");
    String url = "http://localhost:8080/brms/org.drools.guvnor.Guvnor/package/";
    KnowledgeResourceType type = KnowledgeResourceType.PKG;// valueOf(System.getProperty("PKG"));
    StringBuffer xml = new StringBuffer();
    xml.append("<change-set xmlns='http://drools.org/drools-5.0/change-set'");
    xml.append("    xmlns:xs='http://www.w3.org/2001/XMLSchema-instance'");
    xml.append("    xs:schemaLocation='http://drools.org/drools-5.0/change-set drools-change-set-5.0.xsd' >");
    xml.append("    <add> ");
    xml.append("        <resource source='" + url + packageName + "' type='" + type.name() + "' />");
    xml.append("    </add> ");
    xml.append("</change-set>");
    return xml.toString();
  }
}
