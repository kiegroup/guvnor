package org.drools.brms.server;

import javax.jcr.LoginException;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.http.HttpSession;

import org.drools.brms.client.rpc.RepositoryService;
import org.drools.brms.client.rpc.TableConfig;
import org.drools.repository.RepositoryConfigurator;
import org.drools.repository.RulesRepository;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/** 
 * This is the implementation of the repository service to drive the GWT based front end.
 * 
 * @author Michael Neale
 */
public class JBRMSServiceServlet extends RemoteServiceServlet
    implements
    RepositoryService {

    private static final long serialVersionUID = 3150768417428383474L;
    
    /**
     * The shared repository instance. This could be bound to JNDI eventually.
     */
    public static Repository repository;
    
    public String[] loadChildCategories(String categoryPath) {   
        ServiceImpl handler = new ServiceImpl(getRulesRepository());
        return handler.loadChildCategories( categoryPath );
    }



    public String[][] loadRuleListForCategories(String categoryPath,
                                                String status) {
        log( "loading rule list",
             "for cat path: " + categoryPath );
        String[][] data = {{"Rule 1", "Production", "mark", "2"}, {"Rule 2", "Production", "mark", "2"}, {"Rule 3", "Production", "mark", "2"}};
        return data;
    }

    public TableConfig loadTableConfig(String listName) {
        log( "loading table config",
             listName );
       
        final TableConfig config = new TableConfig();

        config.headers = new String[]{"name", "status", "last updated by", "version"};
        config.rowsPerPage = 30;
        return config;
    }



    private void log(String serviceName,
                     String message) {
        System.out.println( "[" + serviceName + "] " + message );
    }
    
    /** Get the rule repository for the "current" user */
    private RulesRepository getRulesRepository() {
        return this.getRepositoryFrom( getSession() );
    }

    private HttpSession getSession() {
        return this.getThreadLocalRequest().getSession();
    }

    /**
     * Pull or create the repository from session.
     * If it is not found, it will create one and then bind it to the session.
     */
    RulesRepository getRepositoryFrom(HttpSession session) {
        Object obj = session.getAttribute( "drools.repository" );
        if ( obj == null ) {
            obj = createRuleRepositoryInstance();
            session.setAttribute( "drools.repository",
                                  obj );
        }
        return (RulesRepository) obj;
    }

    /** Initialse the repository, set it up if it is brand new */
    RulesRepository createRuleRepositoryInstance() {
        
        RepositoryConfigurator config = new RepositoryConfigurator();

        try {
            
            Session session;
            if (repository == null) {
                long start = System.currentTimeMillis();
                session = initialiseRepo( config );
                System.out.println("initialise repo time: " + (System.currentTimeMillis() - start));
            }  else {
                long start = System.currentTimeMillis();
                session = config.login( repository );
                System.out.println("login repo time: " + (System.currentTimeMillis() - start));
                
            }
            
            return new RulesRepository( session );
        } catch ( LoginException e ) {
            throw new RuntimeException( e );
        } catch ( RepositoryException e ) {
            throw new RuntimeException( "Unable to get a repository: " + e.getMessage() );
        }
    }
    
    

    /** This will create a new repository instance (should only happen once after startup) */
    private Session initialiseRepo(RepositoryConfigurator config) throws LoginException,
                                                                 RepositoryException {
        Session session;
        if (repository == null) {
            repository = config.createRepository();
        }
        
        session = config.login( repository );
        
        config.setupRulesRepository( session );
        return session;
    }
    
    

    public Boolean createCategory(String path,
                                  String name,
                                  String description) {
        ServiceImpl serv = new ServiceImpl(getRulesRepository());
        return serv.createCategory( path, name, description );
        
    }



}
