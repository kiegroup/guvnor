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


import org.drools.repository.*;
import org.drools.repository.events.CheckinEvent;
import org.drools.repository.events.StorageEventManager;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.jcr.LoginException;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.ServletContextEvent;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


/**
 * This startup class manages the JCR repository, sets it up if necessary.
 */
@ApplicationScoped
public class RepositoryStartupService {

    private transient final Logger log = LoggerFactory.getLogger(getClass());

    @Inject
    private GuvnorBootstrapConfiguration guvnorBootstrapConfiguration;

    private RulesRepositoryConfigurator configurator;

    private Repository repository;
    private Session sessionForSetup;
    private RulesRepository mailmanRulesRepository;

    public Repository getRepositoryInstance() {
        try {
            // Convert Map to Properties object
            Properties properties = new Properties();
            properties.putAll(guvnorBootstrapConfiguration.getProperties());
            configurator = RulesRepositoryConfigurator.getInstance(properties);
            repository = configurator.getJCRRepository();
        } catch (RepositoryException e) {
            throw new RulesRepositoryException(e);
        }
        return repository;
    }

    @PostConstruct
    public void create() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        repository = getRepositoryInstance();
        String adminUsername = guvnorBootstrapConfiguration.extractAdminUsername();
        String adminPassword = guvnorBootstrapConfiguration.extractAdminPassword();
        sessionForSetup = newSession(adminUsername, adminPassword);
        create(sessionForSetup);
        startMailboxService();
        registerCheckinListener();
    }

    void create(Session sessionForSetup) {

        RulesRepositoryAdministrator admin = new RulesRepositoryAdministrator(sessionForSetup);
        if (!admin.isRepositoryInitialized()) {
            try {
                configurator.setupRepository(sessionForSetup);
            } catch (RepositoryException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        //
        //Migrate v4 ruleflows to v5
        //This section checks if the repository contains drools v4
        //ruleflows that need to be migrated to drools v5
        //
        RulesRepository repo = new RulesRepository(sessionForSetup);
        try {
            if (MigrateRepository.needsRuleflowMigration(repo)) {
                MigrateRepository.migrateRuleflows(repo);
            }
        } catch (RepositoryException e) {
            e.printStackTrace();
            throw new RulesRepositoryException(e);
        }
    }

    /**
     * Start up the mailbox, flush out any messages that were left
     */
    private void startMailboxService() {
        String mailmanUsername = guvnorBootstrapConfiguration.extractMailmanUsername();
        String mailmanPassword = guvnorBootstrapConfiguration.extractMailmanPassword();
        mailmanRulesRepository = new RulesRepository(newSession(mailmanUsername, mailmanPassword));
        MailboxService.getInstance().init(mailmanRulesRepository);
        MailboxService.getInstance().wakeUp();
        registerCheckinListener();
    }

    /**
     * Listen for changes to the repository - for inbox purposes
     */
    // TODO seam3upgrade Maybe this was only done during testing?
    public void registerCheckinListener() {
        StorageEventManager.registerCheckinEvent(new CheckinEvent() {
            public void afterCheckin(AssetItem item) {
                UserInbox.recordUserEditEvent(item);  //to register that she edited...
                MailboxService.getInstance().recordItemUpdated(item);   //for outgoing...
                MailboxService.getInstance().wakeUp();
            }
        });
        log.info("CheckinListener registered");
    }

    @PreDestroy
    public void close() {
        sessionForSetup.logout();
        MailboxService.getInstance().stop();
        mailmanRulesRepository.logout();

        log.info( "Removing listeners...." );
        StorageEventManager.removeListeners();
        log.info( "Shutting down repository..." );
        configurator.shutdown();
    }

    /**
     * This will create a new Session, based on the current user.
     *
     * @return
     */
    public Session newSession(String userName) {
        try {
            return configurator.login(userName);
        } catch (LoginException e) {
            throw new RulesRepositoryException("Unable to login to JCR backend.", e);
        } catch (RepositoryException e) {
            throw new RulesRepositoryException(e);
        }
    }

    /**
     * This will create a new Session, based on the current user.
     *
     * @return
     */
    public Session newSession(String userName, String password) {
        try {
            return configurator.login(userName, password);
        } catch (LoginException e) {
            throw new RulesRepositoryException("UserName: [ " + userName + "] Unable to login to JCR backend.", e);
        } catch (RepositoryException e) {
            throw new RulesRepositoryException(e);
        }
    }

}
