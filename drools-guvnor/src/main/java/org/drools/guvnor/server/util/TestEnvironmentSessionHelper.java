package org.drools.guvnor.server.util;
/*
 * Copyright 2005 JBoss Inc
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



import java.io.File;

import javax.jcr.Repository;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.drools.repository.JCRRepositoryConfigurator;
import org.drools.repository.JackrabbitRepositoryConfigurator;
import org.drools.repository.RepositorySessionUtil;
import org.drools.repository.RulesRepositoryAdministrator;

/**
 * This is only to be used for testing, eg in hosted mode, or unit tests.
 * 
 * @author Michael Neale
 */
public class TestEnvironmentSessionHelper {


    public static Repository repository;
    
    
    public static Session getSession() throws Exception {
        return getSession(true);
    }
    
    public static synchronized Session getSession(boolean erase) throws Exception {
        if (repository == null) {
            
            if (erase) {
                File repoDir = new File("repository");
                System.out.println("DELETE test repo dir: " + repoDir.getAbsolutePath());
                RepositorySessionUtil.deleteDir( repoDir );
                System.out.println("TEST repo dir deleted.");
            }
            
            JCRRepositoryConfigurator config = new JackrabbitRepositoryConfigurator();
            repository = config.getJCRRepository(null);;
        
            Session testSession = repository.login(
                                                                     new SimpleCredentials("alan_parsons", "password".toCharArray()));

            RulesRepositoryAdministrator admin = new RulesRepositoryAdministrator(testSession);
            if (erase && admin.isRepositoryInitialized()) {
                
                admin.clearRulesRepository( );
            } 
            config.setupRulesRepository( testSession );
            return testSession;
        } else {
            return repository.login(
                             new SimpleCredentials("alan_parsons", "password".toCharArray()));            
        }
        
    }
        
    
}