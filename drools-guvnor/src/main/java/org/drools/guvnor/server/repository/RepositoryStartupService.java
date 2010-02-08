package org.drools.guvnor.server.repository;
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



import javax.jcr.LoginException;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.drools.repository.*;
import org.drools.repository.events.StorageEventManager;
import org.drools.repository.events.CheckinEvent;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
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
public class RepositoryStartupService {

    JCRRepositoryConfigurator configurator = new JackrabbitRepositoryConfigurator();
    String repositoryHomeDirectory = null;

    Repository repository;
	private Session sessionForSetup;
    private RulesRepository mailmanSession;

    @Create
    public void create() {
        repository = configurator.getJCRRepository( repositoryHomeDirectory );
        sessionForSetup = newSession("admin");
        create( sessionForSetup );
        startMailboxService();
        registerCheckinListener();
    }


    /** Listen for changes to the repository - for inbox purposes */
    public static void registerCheckinListener() {
        StorageEventManager.registerCheckinEvent(new CheckinEvent() {
            public void afterCheckin(AssetItem item) {
                UserInbox.recordUserEditEvent(item);  //to register that she edited...
                MailboxService.getInstance().recordItemUpdated(item);   //for outgoing...
                MailboxService.getInstance().wakeUp();
            }
        });
    }

    /** Start up the mailbox, flush out any messages that were left */
    private void startMailboxService() {
        mailmanSession = new RulesRepository(newSession(MailboxService.MAILMAN));
        MailboxService.getInstance().init(mailmanSession);
        MailboxService.getInstance().wakeUp();
    }




    void create(Session sessionForSetup) {
    	
    	RulesRepositoryAdministrator admin = new RulesRepositoryAdministrator(sessionForSetup);
        if (!admin.isRepositoryInitialized()) {
            configurator.setupRulesRepository( sessionForSetup );
        }
        
        //
        //Migrate v4 ruleflows to v5
        //This section checks if the repository contains drools v4
        //ruleflows that need to be migrated to drools v5
        //
        RulesRepository repo = new RulesRepository(sessionForSetup);
        try
        {
        	if ( MigrateRepository.needsRuleflowMigration(repo) ) 
        	{
        		MigrateRepository.migrateRuleflows( repo );
        	}
        }
        catch ( RepositoryException e ) 
        {
        	e.printStackTrace();
        	throw new RulesRepositoryException(e);
        }
    }

    @Destroy
    public void close() {
        sessionForSetup.logout();
        mailmanSession.logout();
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