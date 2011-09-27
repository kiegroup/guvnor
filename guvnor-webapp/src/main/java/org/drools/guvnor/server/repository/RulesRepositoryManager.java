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

import java.security.Principal;

import javax.security.auth.Subject;

import org.drools.repository.ClassUtil;
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
import org.jboss.security.SecurityContext;
import org.jboss.security.SecurityContextAssociation;
import org.jboss.security.SecurityContextFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This enhances the BRMS repository for lifecycle management.
 */
@Scope(ScopeType.EVENT)
@AutoCreate
@Name("repository")
public class RulesRepositoryManager {

	private static final Logger log = LoggerFactory.getLogger(RulesRepositoryManager.class);
	
    @In
    RepositoryStartupService repositoryConfiguration;

    private RulesRepository repository;

    @Create
    public void create() {
        //Do not use user name "anonymous" as this user is configured in JackRabbit SimpleLoginModule
        //with limited privileges. In Guvnor, access control is done in a higher level. 
        String DEFAULT_USER = "guest";
        //String READ_ONLY_USER = "anonymous";
        String userName = DEFAULT_USER;
        if (Contexts.isApplicationContextActive()) {
            userName = Identity.instance().getCredentials().getUsername();
            
            // Also set the JBoss security context if the JAAS realm is found.
            try {
                String configName = Identity.instance().getJaasConfigName();
                boolean isJBoss=true;
                try {
                    ClassUtil.forName("org.jboss.security.SecurityContext", this.getClass());
                } catch (ClassNotFoundException e) {
                    isJBoss=false;
                }
                if (configName!=null && isJBoss==true) {
                    Subject subject = Identity.instance().getSubject();
                    Principal principal = subject.getPrincipals().iterator().next();
                    SecurityContext sc = SecurityContextFactory.createSecurityContext(principal, null, subject, configName); 
                    SecurityContextAssociation.setSecurityContext(sc);
                }
            } catch (Exception e1) {
            	log.error("Not able to set the JAAS security context", e1.getMessage(),e1);
            }
        }
        if (userName == null) {
            userName = DEFAULT_USER;
        }
        repository = new RulesRepository(repositoryConfiguration.newSession(userName));
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
