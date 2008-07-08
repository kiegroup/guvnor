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
