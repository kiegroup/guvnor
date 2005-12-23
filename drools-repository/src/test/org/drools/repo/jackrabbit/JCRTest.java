package org.drools.repo.jackrabbit;

import junit.framework.TestCase;

import java.util.Hashtable;

import javax.jcr.*;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.jackrabbit.core.jndi.RegistryHelper;

public class JCRTest extends TestCase
{

    public void testInitial() throws Exception
    {
        setupJAAS( );
        Session session = getSession( );
        String[] uris = session.getWorkspace().getNamespaceRegistry().getURIs(); // .registerNamespace("drools", "https://www.drools.org/repo");
        basicCase( session );
        
        addSomething( session);
    }
    
    private static void addSomething(Session session) throws Exception {
        
        Node node = session.getRootNode();
        
        node.setProperty("drools:rules", "yeah");
        node.save();
        
        session.save();
        
    }

    private static void basicCase(Session session)
    {
        try
        {

            
            
            Node rn = session.getRootNode( );

            System.out.println( rn.getPrimaryNodeType( ).getName( ) );

        }
        catch ( Exception e )
        {
            System.err.println( e );
        }
    }

    private static Session getSession() throws NamingException,
                                       RepositoryException,
                                       LoginException,
                                       NoSuchWorkspaceException
    {
        String configFile = "repotest/repository.xml";
        String repHomeDir = "repotest";

        Hashtable env = new Hashtable( );
        env.put( Context.INITIAL_CONTEXT_FACTORY,
                 "org.apache.jackrabbit.core.jndi.provider.DummyInitialContextFactory" );
        env.put( Context.PROVIDER_URL,
                 "localhost" );
        InitialContext ctx = new InitialContext( env );

        registerRepo( configFile,
                      repHomeDir,
                      ctx );
        Repository r = (Repository) ctx.lookup( "repo" );
        
        Session session = r.login( new SimpleCredentials( "userid",
                                                          "".toCharArray( ) ),
                                   null );
        return session;
    }

    private static void registerRepo(String configFile,
                                     String repHomeDir,
                                     InitialContext ctx) throws NamingException,
                                                        RepositoryException
    {
        RegistryHelper.registerRepository( ctx,
                                           "repo",
                                           configFile,
                                           repHomeDir,
                                           true );
    }

    private static void setupJAAS()
    {
        System.setProperty("java.security.auth.login.config", "c:/jaas.config");
    }



}
