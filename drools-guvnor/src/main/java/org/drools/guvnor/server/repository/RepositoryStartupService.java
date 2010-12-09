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



import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.jcr.LoginException;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.drools.repository.*;
import org.drools.repository.events.StorageEventManager;
import org.drools.repository.events.CheckinEvent;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This startup class manages the JCR repository, sets it up if necessary.
 * @author Michael Neale
 */
@Scope(ScopeType.APPLICATION)
@Startup
@Name("repositoryConfiguration")
public class RepositoryStartupService {

	private static final Logger log = LoggerFactory.getLogger(RepositoryStartupService.class);
	private static final String ADMIN                     = "admin";
	private static final String ADMIN_USER_PROPERTY       = "org.drools.repository.admin.username";
	private static final String ADMIN_PASSWORD_PROPERTY   = "org.drools.repository.admin.password";
	private static final String MAILMAN                   = "mailman";
	private static final String MAILMAN_USER_PROPERTY     = "org.drools.repository.mailman.username";
	private static final String MAILMAN_PASSWORD_PROPERTY = "org.drools.repository.mailman.password";
	private static final String SECURE_PASSWORDS_PROPERTY = "org.drools.repository.secure.passwords";
	
	
	private RulesRepositoryConfigurator configurator;
    Map<String,String> properties = new HashMap<String,String>();

    Repository repository;
	private Session sessionForSetup;
    private RulesRepository mailmanSession;

    public Repository getRepositoryInstance() {
    	try {
    		Properties properties = new Properties();
    		properties.putAll(this.properties);
			configurator = RulesRepositoryConfigurator.getInstance(properties);
			repository = configurator.getJCRRepository();
		} catch (RepositoryException e) {
			log.error(e.getMessage(),e);
		}
		return repository;
    }
    
    @Create
    public void create() {
    	repository = getRepositoryInstance();
    	String username = ADMIN;
    	if (properties.containsKey(ADMIN_USER_PROPERTY)) {
    		username = properties.get(ADMIN_USER_PROPERTY);
    	}
    	String password = "password";
    	if (properties.containsKey(ADMIN_PASSWORD_PROPERTY)) {
    		password = properties.get(ADMIN_PASSWORD_PROPERTY);
    		if ("true".equalsIgnoreCase(properties.get(SECURE_PASSWORDS_PROPERTY))) {
    			password = decode(password);
    		}
    	} else {
    		log.debug("Could not find property " + ADMIN_PASSWORD_PROPERTY + " for user " + ADMIN);
    	}
        sessionForSetup = newSession(username,password);
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
    	String username = MAILMAN;
    	if (properties.containsKey(MAILMAN_USER_PROPERTY)) {
    		username = properties.get(MAILMAN_USER_PROPERTY);
    	}
    	String password = "password";
    	if (properties.containsKey(MAILMAN_PASSWORD_PROPERTY)) {
    		password = properties.get(MAILMAN_PASSWORD_PROPERTY);
    		if ("true".equalsIgnoreCase(properties.get(SECURE_PASSWORDS_PROPERTY))) {
    			password = decode(password);
    		}
    	} else {
    		log.debug("Could not find property " + MAILMAN_PASSWORD_PROPERTY + " for user " + MAILMAN);
    	}
        mailmanSession = new RulesRepository(newSession(username, password));
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
    		properties.put(JCRRepositoryConfigurator.REPOSITORY_ROOT_DIRECTORY, home);
    	}
    }

    public void setRepositoryConfigurator(String clazz) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
    	if (clazz!=null) {
    		properties.put(RulesRepositoryConfigurator.CONFIGURATOR_CLASS, clazz);
    	}
    }
    
    /**
     * This will create a new Session, based on the current user.
     * @return
     */
    public Session newSession(String userName) {

        try {
        	return configurator.login(userName);
        } catch ( LoginException e ) {
            throw new RulesRepositoryException( "Unable to login to JCR backend." );
        } catch ( RepositoryException e ) {
            throw new RulesRepositoryException( e );
        }
    }


    /**
     * This will create a new Session, based on the current user.
     * @return
     */
    public Session newSession(String userName, String password) {

        try {
        	return configurator.login(userName, password);
        } catch ( LoginException e ) {
            throw new RulesRepositoryException("UserName: [ " + userName + "] Unable to login to JCR backend." ,e);
        } catch ( RepositoryException e ) {
            throw new RulesRepositoryException( e );
        }
    }
    
   
    
    
    
    private static String decode(String secret)
    {
    	String decodedPassword = secret;
    	try {
	    	byte[] kbytes = "jaas is the way".getBytes();
	    	SecretKeySpec key = new SecretKeySpec(kbytes, "Blowfish");
	
	    	BigInteger n = new BigInteger(secret, 16);
	    	byte[] encoding = n.toByteArray();
	
	    	//SECURITY-344: fix leading zeros
	    	if (encoding.length % 8 != 0)
	    	{
	    		int length = encoding.length;
	    		int newLength = ((length / 8) + 1) * 8;
	    		int pad = newLength - length; //number of leading zeros
	    		byte[] old = encoding;
	    		encoding = new byte[newLength];
	    		for (int i = old.length - 1; i >= 0; i--)
	    		{
	    			encoding[i + pad] = old[i];
	    		}
	    	}
	
	    	Cipher cipher = Cipher.getInstance("Blowfish");
	    	cipher.init(Cipher.DECRYPT_MODE, key);
	    	byte[] decode = cipher.doFinal(encoding);
	    	decodedPassword =  new String(decode);
    	} catch (Exception e) {
    		log.error(e.getMessage(),e);
    	}
    	return decodedPassword;
    }

}