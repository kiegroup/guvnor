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

import org.drools.repository.RulesRepository;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import org.jboss.seam.security.Identity;
import org.picketlink.idm.api.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Request scoped bean that produces the RulesRepository
 */
@RequestScoped
public class RulesRepositoryManager {
	private static final String DEFAULT_USERNAME = "guest";
    
    private static final Logger log = LoggerFactory.getLogger(RulesRepositoryManager.class);

    public void logout(@Disposes @Preferred RulesRepository rulesRepository) {
     	rulesRepository.logout();
    }

    private void doSecurityContextAssociation() {
        // TODO seam3upgrade seam3 uses PicketLink, not JAAS. If JaasAuthenticator is configured, we can extract the jaasConfigName there
//        // Also set the JBoss security context if the JAAS realm is found.
//        try {
//            String configName = jaasAuthenticator.getJaasConfigName();
//            boolean isJBoss = true;
//            try {
//                ClassUtil.forName("org.jboss.security.SecurityContext", this.getClass());
//            } catch (ClassNotFoundException e) {
//                isJBoss = false;
//            }
//            if (isJBoss && configName != null) {
//                Subject subject = identity.getSubject();
//                Principal principal = subject.getPrincipals().iterator().next();
//                SecurityContext sc = SecurityContextFactory.createSecurityContext(principal, null, subject, configName);
//                SecurityContextAssociation.setSecurityContext(sc);
//            }
//        } catch (Exception e1) {
//            log.error("Not able to set the JAAS security context", e1.getMessage(),e1);
//        }
    }

    @Produces @Preferred @RequestScoped 
    public RulesRepository getRulesRepository(RepositoryStartupService repositoryStartupService, Identity identity) {
       	 User user = identity.getUser();
         String username;
         // TODO user should never be null, weld messes up the identity proxy?
         if (user == null) {
             log.warn("Creating RulesRepository with default username.");
             // Do not use user name "anonymous" as this user is configured in JackRabbit SimpleLoginModule
             // with limited privileges. In Guvnor, access control is done in a higher level.
             username = DEFAULT_USERNAME;
         } else {
             username = user.getId();
         }
         doSecurityContextAssociation();
         RulesRepository rulesRepository = new RulesRepository(repositoryStartupService.newSession(username));
         return rulesRepository;      
    }

}
