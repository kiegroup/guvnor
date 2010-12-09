/**
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



import java.util.Properties;

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

	private RulesRepositoryConfigurator configurator;
    Properties properties = new Properties();

    Repository repository;
	private Session sessionForSetup;
    private RulesRepository mailmanSession;

    public Repository getRepositoryInstance() {
    	try {
			configurator = RulesRepositoryConfigurator.getInstance(properties);
			repository = configurator.getJCRRepository();
		} catch (RepositoryException e) {
			// TODO Kurt Auto-generated catch block
			e.printStackTrace();
		}
		return repository;
    }
    
    @Create
    public void create() {
    	repository = getRepositoryInstance();
        sessionForSetup = newSession("admin");
        create( sessionForSetup );
        startMailboxService();
        registerCheckinListener();
    }


    /** Listen for changes to the repository - for inbox purposes */
    public static void registerCheckinListener() {
    	System.out.println("Registering check-in listener");
        StorageEventManager.registerCheckinEvent(new CheckinEvent() {
            public void afterCheckin(AssetItem item) {
                UserInbox.recordUserEditEvent(item);  //to register that she edited...
                MailboxService.getInstance().recordItemUpdated(item);   //for outgoing...
                MailboxService.getInstance().wakeUp();
            }
        });
        System.out.println("Check-in listener up");
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
            try {
				configurator.setupRepository( sessionForSetup );
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
        MailboxService.getInstance().stop();
        mailmanSession.logout();
        
    }


    public void setHomeDirectory(String home) {
    	if (home!=null) {
    		properties.setProperty(JCRRepositoryConfigurator.REPOSITORY_ROOT_DIRECTORY, home);
    	}
    }

    public void setRepositoryConfigurator(String clazz) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
    	if (clazz!=null) {
    		properties.setProperty(RulesRepositoryConfigurator.CONFIGURATOR_CLASS, clazz);
    	}
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