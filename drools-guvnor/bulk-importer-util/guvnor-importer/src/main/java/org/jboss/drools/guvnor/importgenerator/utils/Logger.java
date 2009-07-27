package org.jboss.drools.guvnor.importgenerator.utils;

import org.jboss.drools.guvnor.importgenerator.CmdArgsParser;
import org.jboss.drools.guvnor.importgenerator.CmdArgsParser.Parameters;

/**
 * Simple std io logger
 * 
 * @author <a href="mailto:mallen@redhat.com">Mat Allen</a>
 */
public class Logger {
  public boolean debugEnabled=true;
  
  public static Logger getLogger(Class c, CmdArgsParser options){
    Logger l=new Logger();
    l.debugEnabled="true".equals(options.getOption(Parameters.OPTIONS_VERBOSE));
    return l;
  }
  public void debugln(String msg){
    if (debugEnabled){
      System.out.println(msg);
    }
  }
  public void debug(String msg){
    if (debugEnabled){
      System.out.print(msg);
    }
  }

}
