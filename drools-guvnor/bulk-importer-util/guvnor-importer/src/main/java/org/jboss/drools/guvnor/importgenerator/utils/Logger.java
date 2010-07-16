/**
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
