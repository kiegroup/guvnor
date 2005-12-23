package org.drools.repo.jcr;

import java.util.Hashtable;

import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.jackrabbit.core.jndi.RegistryHelper;

/**
 * This is a dirty hack of a class while I get the hang of using JCR in and 
 * outside of containers.
 * @author <a href="mailto:michael.neale@gmail.com"> Michael Neale</a>
 *
 */
public class JCRSessionUtil
{

    public Session getSession() throws NamingException, RepositoryException {
        
        System.setProperty("java.security.auth.login.config", "c:/jaas.config");                 
        String configFile = "repotest/repository.xml";
        String repHomeDir = "repotest";

        Hashtable env = new Hashtable( );
        env.put( Context.INITIAL_CONTEXT_FACTORY,
                 "org.apache.jackrabbit.core.jndi.provider.DummyInitialContextFactory" );
        env.put( Context.PROVIDER_URL,
                 "localhost" );
        
        InitialContext ctx = new InitialContext( env );

        //configure repo, if its not already
        RegistryHelper.registerRepository( ctx,
                                           "repo",
                                           configFile,
                                           repHomeDir,
                                           true );
        Repository r = (Repository) ctx.lookup( "repo" );
        
        Session session = r.login( new SimpleCredentials( "userid",
                                                          "".toCharArray( ) ),
                                   null );
        return session;        
        
    }
    
}
