package org.drools.brms.server.repository;
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



import java.util.Collection;

import javax.jcr.LoginException;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.drools.repository.JCRRepositoryConfigurator;
import org.drools.repository.JackrabbitRepositoryConfigurator;
import org.drools.repository.RulesRepositoryAdministrator;
import org.drools.repository.RulesRepositoryException;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;

/**
 * This startup class manages the JCR repository, sets it up if necessary.
 * @author Michael Neale
 */
@Scope(ScopeType.APPLICATION)
@Startup
@Name("repositoryConfiguration")
public class BRMSRepositoryConfiguration {

    JCRRepositoryConfigurator configurator = new JackrabbitRepositoryConfigurator();
    String repositoryHomeDirectory = null;
    
    Repository repository;
    
    @Create
    public void create() {      
        repository = configurator.getJCRRepository( repositoryHomeDirectory );
        Session sessionForSetup = newSession("admin");
        create( sessionForSetup );
    }


    void create(Session sessionForSetup) {
                
        RulesRepositoryAdministrator admin = new RulesRepositoryAdministrator(sessionForSetup);
        if (!admin.isRepositoryInitialized()) {
            configurator.setupRulesRepository( sessionForSetup );
        }
        sessionForSetup.logout();
    }


    public void setHomeDirectory(String home) {
        this.repositoryHomeDirectory = home;
    }
    
    public void setRepositoryConfigurator(String clazz) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
            Class cls = Class.forName( clazz );
            this.configurator = (JCRRepositoryConfigurator) cls.newInstance();
    }
    
     
    /**
     * This will create a new Session, based on the current user.
     * @return
     */
    public Session newSession(String userName) {
        
        try {
            return repository.login( new SimpleCredentials(userName, "password".toCharArray()) );
        } catch ( LoginException e ) {
            throw new RulesRepositoryException( "Unable to login to JCR backend." );
        } catch ( RepositoryException e ) {
            throw new RulesRepositoryException( e );
        }
    }
    
    
}