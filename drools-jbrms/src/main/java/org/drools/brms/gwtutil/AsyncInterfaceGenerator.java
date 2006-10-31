package org.drools.brms.gwtutil;

import java.lang.reflect.Method;
import java.util.Iterator;

import org.drools.brms.client.rpc.RepositoryService;

/**
 * This utility uses reflection to generate the async interface from the 
 * Service interface as per GWT standard.
 * 
 * @author Michael Neale
 */
public class AsyncInterfaceGenerator {

    public static void main(String[] args) throws Exception {
        Class cls = RepositoryService.class;
        String line = "";
        Method[] methods = cls.getMethods();
        for ( int i = 0; i < methods.length; i++ ) {
            Method meth = methods[i];
            if (meth.getDeclaringClass() == cls) {
                line += "public void " + meth.getName() + "(";
                Class params[] = meth.getParameterTypes();
                for ( int j = 0; j < params.length; j++ ) {
                    line += params[j].getName();
                    line += " p" + j;
                    if (j < params.length -1) {
                        line += ", ";
                    }
                }
                if (line.endsWith( "(" )) {
                    line += "AsyncCallback cb";
                } else {
                    line += ", AsyncCallback cb";
                }
                line += ");\n";
            }
        }
        
        System.out.println(line);
    }

}
