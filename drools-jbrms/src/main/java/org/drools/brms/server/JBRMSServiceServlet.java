package org.drools.brms.server;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.jcr.LoginException;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.http.HttpSession;

import org.drools.brms.client.rpc.RepositoryService;
import org.drools.brms.client.rpc.TableConfig;
import org.drools.repository.CategoryItem;
import org.drools.repository.RepositoryConfigurator;
import org.drools.repository.RuleItem;
import org.drools.repository.RulePackageItem;
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

        CategoryItem item = getRulesRepository().loadCategory( categoryPath );
        List children = item.getChildTags();
        String[] list = new String[children.size()];
        for ( int i = 0; i < list.length; i++ ) {
            list[i] = ((CategoryItem) children.get( i )).getName();
        }
        return list;

    }

    public Boolean createCategory(String path,
                                  String name,
                                  String description) {
        
        if (path == null || "".equals(path)) {
            path = "/";
        }
        CategoryItem item = getRulesRepository().loadCategory( path );
        item.addCategory( name, description );
        return Boolean.TRUE;
    }
    
    
    public Boolean createNewRule(String ruleName,
                                 String description,
                                 String initialCategory,
                                 String initialPackage) {
        RulePackageItem pkg = getRulesRepository().loadRulePackage( initialPackage );
        RuleItem rule = pkg.addRule( ruleName, description );
        rule.addCategory( initialCategory );
        
        return Boolean.TRUE;
    }

    public String[] listRulePackages() {
        Iterator pkgs = getRulesRepository().listPackages();
        List result = new ArrayList();
        while(pkgs.hasNext()) {
            RulePackageItem pkg = (RulePackageItem) pkgs.next();
            result.add( pkg.getName() );
        }
        return (String[]) result.toArray( new String[result.size()] );
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
    RulesRepository getRulesRepository() {
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
        Session session = config.login( getJCRRepository( config ) );
        
        config.setupRulesRepository( session );
        return session;
    }



    synchronized static Repository getJCRRepository(RepositoryConfigurator config) {
        if (repository == null) {
            repository = config.createRepository();
        }
        return repository;
    }
    
    


}
