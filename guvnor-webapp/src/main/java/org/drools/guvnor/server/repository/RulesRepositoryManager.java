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
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.enterprise.inject.Produces;
import org.jboss.seam.security.Credentials;
import org.jboss.seam.solder.beanManager.BeanManagerLocator;
import org.jboss.seam.security.Identity;

/**
 * This enhances the BRMS repository for lifecycle management.
 */
@RequestScoped // TODO Shouldn't this be @ApplicationScoped?
public class RulesRepositoryManager {

    @Inject
    RepositoryStartupService repositoryConfiguration;

    @Inject
    private Credentials credentials;

    private RulesRepository repository;

    @PostConstruct
    public void create() {
        //Do not use user name "anonymous" as this user is configured in JackRabbit SimpleLoginModule
        //with limited privileges. In Guvnor, access control is done in a higher level. 
        String DEFAULT_USER = "guest";
        //String READ_ONLY_USER = "anonymous";
        String userName = DEFAULT_USER;
        BeanManagerLocator beanManagerLocator = new BeanManagerLocator();
        if (beanManagerLocator.isBeanManagerAvailable()) {
            userName = credentials.getUsername();
        }
        if (userName == null) {
            userName = DEFAULT_USER;
        }
        repository = new RulesRepository(repositoryConfiguration.newSession(userName));
    }

    @Produces @Named("repository") // TODO shouldn't this be @RequestScoped?
    public RulesRepository getRepository() {
        return repository;
    }

}
