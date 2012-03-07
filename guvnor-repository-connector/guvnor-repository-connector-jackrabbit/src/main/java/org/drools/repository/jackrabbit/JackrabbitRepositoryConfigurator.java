/*
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

package org.drools.repository.jackrabbit;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Properties;

import javax.jcr.Credentials;
import javax.jcr.LoginException;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.Workspace;

import org.apache.jackrabbit.commons.cnd.CndImporter;
import org.apache.jackrabbit.core.RepositoryFactoryImpl;
import org.apache.jackrabbit.core.TransientRepository;
import org.drools.repository.JCRRepositoryConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** 
 * This contains code to initialise the repository for jackrabbit.
 * This is mostly a collection of utilities. 
 * Any jackrabbit specific code needs to go in here.
 */
public class JackrabbitRepositoryConfigurator extends JCRRepositoryConfigurator {


    private static final Logger log = LoggerFactory.getLogger(JackrabbitRepositoryConfigurator.class);
    
    public JackrabbitRepositoryConfigurator() {
        defaultJCRImplClass = RepositoryFactoryImpl.class.getName();
    }

    @Override
    public Repository getJCRRepository(Properties properties) throws RepositoryException {

        String repoRootDir = properties.getProperty(REPOSITORY_ROOT_DIRECTORY);
        if (repoRootDir == null) {
            repository = new TransientRepository();
        } else {
            repository =  new TransientRepository(repoRootDir + "/repository.xml", repoRootDir);
        }
        return repository;

    }

    public Session login (String userName) throws RepositoryException {
        Credentials credentials = new SimpleCredentials(userName, "password".toCharArray());
        return repository.login( credentials );
    }
    
    public void registerNodeTypesFromCndFile(String cndFileName, Session session, Workspace workspace) throws RepositoryException {
        try {
            //Read in the CND file
            Reader in = new InputStreamReader(this.getClass().getResourceAsStream( cndFileName ), "UTF-8");
            CndImporter.registerNodeTypes(in, session);
        } catch (Exception e) {
            throw new RepositoryException("Registering node types for repository failed.", e);
        }
    }
    
    /**
     * {@inheritDoc}
     * 
     * @see org.drools.repository.JCRRepositoryConfigurator#shutdown()
     */
    public void shutdown() {
        if (repository instanceof TransientRepository) {
            ((TransientRepository)repository).shutdown();
        }
    }
    
}
