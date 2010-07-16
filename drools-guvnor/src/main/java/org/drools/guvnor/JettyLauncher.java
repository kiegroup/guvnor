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

package org.drools.guvnor;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Method;

/**
 *
 */
public class JettyLauncher {

    public void launch(String[] args) throws Exception {
        File jettyConf;
        if (args.length == 1) {
            jettyConf = new File(args[0]);
            if (!jettyConf.exists()) {
                throw new FileNotFoundException("Jetty config file " + args[1] + " not found!");
            }
        } else {
            jettyConf = new File("jetty.xml");
            if (!jettyConf.exists()) {
                throw new FileNotFoundException("jetty.xml must present in working directory!");
            }
        }

        try {
            Method mainMethod = getJettyServerClassName().getMethod("main", new Class[]{String[].class});
            mainMethod.invoke(null, new Object[]{new String[]{jettyConf.getAbsolutePath()}});
        }
        catch (ClassNotFoundException e) {
            System.err.println("You don't have Jetty in the classpath, cannot proceed!");
        }
    }

    private Class getJettyServerClassName() throws ClassNotFoundException {
        try {
            System.out.println("starting jetty5...");
            return Class.forName("org.mortbay.jetty.Server"); // jetty 5
        }
        catch (ClassNotFoundException e) {
            System.err.println("jetty5 failed: " + e.getMessage());
            System.out.println("starting jetty4...");
            return Class.forName("org.mortbay.start.Main");// jetty 4 and early versions
        }

    }

    public static void main(String[] args) throws Exception {
        new JettyLauncher().launch(args);
    }

}
