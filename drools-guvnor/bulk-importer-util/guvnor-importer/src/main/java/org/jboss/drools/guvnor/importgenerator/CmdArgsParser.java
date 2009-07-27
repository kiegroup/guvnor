package org.jboss.drools.guvnor.importgenerator;

import java.util.HashMap;
import java.util.Map;

/**
 * Command line argument parser
 * 
 * @author <a href="mailto:mallen@redhat.com">Mat Allen</a>
 */
public class CmdArgsParser{
  private static Map<Parameters, String> options=new HashMap<Parameters, String>();
  private static Map<String, Parameters> map=new HashMap<String, Parameters>();
  public enum Parameters{
    OPTIONS_PATH,
    OPTIONS_PACKAGE_START,
    OPTIONS_PACKAGE_EXCLUDE,
    OPTIONS_RECURSIVE,
    OPTIONS_CREATOR,
    OPTIONS_EXTENSIONS,
    OPTIONS_OUTPUT_FILE,
    OPTIONS_SNAPSHOT_NAME,
    OPTIONS_FUNCTIONS_FILE,
    OPTIONS_KAGENT_CHANGE_SET_SERVER,
    OPTIONS_KAGENT_CHANGE_SET_FILE,
    OPTIONS_BASE_DIR,
    OPTIONS_VERBOSE,
    OPTIONS_VERY_VERBOSE
  }
  
  public CmdArgsParser(){
    map.put("-p", Parameters.OPTIONS_PATH);
    map.put("-e", Parameters.OPTIONS_PACKAGE_EXCLUDE);
    map.put("-s", Parameters.OPTIONS_PACKAGE_START);
    map.put("-r", Parameters.OPTIONS_RECURSIVE);
    map.put("-u", Parameters.OPTIONS_CREATOR);
    map.put("-f", Parameters.OPTIONS_EXTENSIONS);
    map.put("-o", Parameters.OPTIONS_OUTPUT_FILE);
    map.put("-n", Parameters.OPTIONS_SNAPSHOT_NAME);
    map.put("-c", Parameters.OPTIONS_FUNCTIONS_FILE);
    map.put("-k", Parameters.OPTIONS_KAGENT_CHANGE_SET_SERVER);
    map.put("-w", Parameters.OPTIONS_KAGENT_CHANGE_SET_FILE);
    map.put("-b", Parameters.OPTIONS_BASE_DIR);
    map.put("-v", Parameters.OPTIONS_VERBOSE);
    map.put("-vv", Parameters.OPTIONS_VERY_VERBOSE);
  }
  
  public String getOption(Parameters parameterName){
    return options.get(parameterName);
  }
  
  public Map<Parameters, String> parse(String[] args){
    if (args.length==0){
      args=new String[]{ //default arguments
          "-classpath",
          "-p", "/home/mallen/workspace/guvnor-importer/my_rules",
          "-s", "rules",
          "-e", "[0-9|.]*[.|-]+[SNAPSHOT]+[.|-]*[09|.]*",
          "-r", "true",
          "-u","admin",
          "-f","drl,xls",
          "-o","generated.xml",
          "-n","1.0.0-SNAPSHOT",
          "-c","functions.drl",
          "-k", "http://localhost:8080/brms/org.drools.guvnor.Guvnor/package/",
          "-b", "/home/mallen/workspace/guvnor-importer",
          "-w", "kagentChangeSet.xml",
          "-V"};
    }
    for (int i=0;i<args.length;i++) {
      String arg = args[i];
      if (arg.equalsIgnoreCase("-classpath"))
        continue;
      if (arg.equalsIgnoreCase("-v")){
        if (arg.equals("-V")) //if its uppercase then set the very verbose flag
          options.put(Parameters.OPTIONS_VERY_VERBOSE, "true");
        options.put(Parameters.OPTIONS_VERBOSE, "true");
        continue;
      }
      if (arg.startsWith("-") && map.get(arg)!=null){ //it is a - param and is mapped
        options.put(map.get(arg), args[++i]);
      }
    }
    //display them so the user knows what the options values are
    for (Parameters key : options.keySet()) {
      System.out.println("   "+key.name()+"="+options.get(key));
    }
    return options;
  }
}