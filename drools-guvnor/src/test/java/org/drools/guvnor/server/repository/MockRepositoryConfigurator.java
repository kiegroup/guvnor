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

/**
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

package org.drools.guvnor.server.repository;

import java.util.Properties;

import javax.jcr.LoginException;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Workspace;

import org.drools.repository.JCRRepositoryConfigurator;
import org.drools.repository.RulesRepositoryException;

public class MockRepositoryConfigurator extends JCRRepositoryConfigurator {
    
    @Override
    public Repository getJCRRepository(Properties properties) {
        if (repository == null) {
            repository = new MockRepo();
        }
        return repository;
    }

    @Override
    public void registerNodeTypesFromCndFile(String cndFileName, Session session, Workspace workspace)
            throws RepositoryException {
        // Do nothing
    }

    @Override
    public void shutdown() {
        // Do nothing
    }

    @Override
    public Session login(String userName) throws RepositoryException {
        // Do nothing
        return null;
    }
    
}
