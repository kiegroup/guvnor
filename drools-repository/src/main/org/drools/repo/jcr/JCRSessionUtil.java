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

    public static final String REPO_JNDI_NAME = "repo";

    /**
     * At the moment this is all hard coded to be un-authenticated and local
     * It is looking for a jaas.config, as well as the repository.xml to setup the repository.
     * @return
     * @throws NamingException
     * @throws RepositoryException
     */
    public Session getSession() throws NamingException, RepositoryException {
        
        
        System.setProperty("java.security.auth.login.config", "c:/jaas.config");                 
        String configFile = "conf/repository.xml";
        String repHomeDir = "drools-repository";

        Hashtable env = new Hashtable( );
        env.put( Context.INITIAL_CONTEXT_FACTORY,
                 "org.apache.jackrabbit.core.jndi.provider.DummyInitialContextFactory" );
        env.put( Context.PROVIDER_URL,
                 "localhost" );
        
        InitialContext ctx = new InitialContext( env );

        registerNewRepoInJNDI( configFile,
                               repHomeDir,
                               ctx );
        Repository r = (Repository) ctx.lookup( REPO_JNDI_NAME );
        
        Session session = r.login( new SimpleCredentials( "userid",
                                                          "".toCharArray( ) ),
                                   null );
        return session;        
        
    }

    private void registerNewRepoInJNDI(String configFile,
                                       String repHomeDir,
                                       InitialContext ctx) throws NamingException,
                                                          RepositoryException
    {
        //configure repo, if its not already
        RegistryHelper.registerRepository( ctx,
                                           REPO_JNDI_NAME,
                                           configFile,
                                           repHomeDir,
                                           true );
    }
    
}
