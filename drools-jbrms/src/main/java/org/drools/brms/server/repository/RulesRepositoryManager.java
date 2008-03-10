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



import org.drools.repository.RulesRepository;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Unwrap;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.security.Identity;

/**
 * This enhances the BRMS repository for lifecycle management.
 * @author Michael Neale
 */
@Scope(ScopeType.EVENT)
@AutoCreate
@Name("repository")
public class RulesRepositoryManager {

    private static String READ_ONLY_USER = "anonymous";
    
    @In 
    BRMSRepositoryConfiguration repositoryConfiguration;
    
    private RulesRepository repository;
    
    
    @Create
    public void create() {
        String userName = READ_ONLY_USER;
        if (Contexts.isApplicationContextActive()) {
            userName = Identity.instance().getUsername();
        }
        if (userName == null) {
            userName = READ_ONLY_USER;
        }        
        repository = new RulesRepository(repositoryConfiguration.newSession(userName) );
    }
    
    @Unwrap
    public RulesRepository getRepository() {
        return repository;
    }
    
    @Destroy
    public void close() {
        repository.logout();
    }
    

    
}