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

package org.drools.guvnor.server.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Properties;

import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.drools.repository.JCRRepositoryConfigurator;
import org.drools.repository.RepositorySessionUtil;
import org.drools.repository.RulesRepositoryAdministrator;
import org.drools.repository.RulesRepositoryConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is only to be used for testing, eg in hosted mode, or unit tests.
 * This is deliberately in the src/main path. 
 *
 * @author Michael Neale
 */
public class TestEnvironmentSessionHelper {


	public static final Logger log = LoggerFactory.getLogger(TestEnvironmentSessionHelper.class);
    public static Repository repository;


    public static synchronized Session getSession() throws Exception {
        return getSession(true);
    }

    public static synchronized Session getSession(boolean erase) {
    	try {
	        if (repository == null) {

	            if (erase) {
	                File repoDir = new File("repository");
	                log.info("DELETE test repo dir: " + repoDir.getAbsolutePath());
	                RepositorySessionUtil.deleteDir( repoDir );
	                log.info("TEST repo dir deleted.");
	            }

	            RulesRepositoryConfigurator config = RulesRepositoryConfigurator.getInstance(null);
                String home = System.getProperty("guvnor.repository.dir");
                Properties properties = new Properties();
                if (home!=null) {
                	properties.setProperty(JCRRepositoryConfigurator.REPOSITORY_ROOT_DIRECTORY, home);
                }
	            repository = config.getJCRRepository();

	            Session testSession = repository.login(new SimpleCredentials("alan_parsons", "password".toCharArray()));

	            RulesRepositoryAdministrator admin = new RulesRepositoryAdministrator(testSession);
	            if (erase && admin.isRepositoryInitialized()) {
	                admin.clearRulesRepository( );
	            }
	            config.setupRepository( testSession );
	            File file = File.createTempFile( "pete", "txt" );
	            file.deleteOnExit();
	            PrintWriter out = new PrintWriter(new FileOutputStream(file));
				//dump(testSession.getRootNode(), out);
//	            OutputStream out = new FileOutputStream("/tmp/pepe.txt");
//	            testSession.exportSystemView("/", out, true, false);
				out.close();
	            return testSession;
	        } else {
	            return repository.login(new SimpleCredentials("alan_parsons", "password".toCharArray()));
	        }
    	} catch (Exception e) {
    		throw new IllegalStateException(e);
    	}

    }

	/** Recursively outputs the contents of the given node. */
//	private static void dump(Node node, PrintWriter out) throws Exception {
//		
//		// First output the node path
//		out.println(node.getPath());
//		// Skip the virtual (and large!) jcr:system subtree
//		if (node.getName().equals("jcr:system")) {
//			return;
//		}
//
//		// Then output the properties
//		PropertyIterator properties = node.getProperties();
//		while (properties.hasNext()) {
//			Property property = properties.nextProperty();
//			if (property.getDefinition().isMultiple()) {
//				// A multi-valued property, print all values
//				Value[] values = property.getValues();
//				for (int i = 0; i < values.length; i++) {
//					out.println(property.getPath() + " = "
//							+ values[i].getString());
//				}
//			} else {
//				// A single-valued property
//				out.println(property.getPath() + " = "
//						+ property.getString());
//			}
//		}
//
//		// Finally output all the child nodes recursively
//		NodeIterator nodes = node.getNodes();
//		while (nodes.hasNext()) {
//			dump(nodes.nextNode(), out);
//		}
//	}
    
    /**
     * Uses the given user name.
     */
    public static synchronized Session getSessionFor(String userName) throws RepositoryException {
        return repository.login(
                         new SimpleCredentials(userName, "password".toCharArray()));

    }
    
    public static synchronized void shutdown() throws RepositoryException {
    	RulesRepositoryConfigurator.getInstance(null).shutdown();
    	
        repository = null;
    }

    


}
