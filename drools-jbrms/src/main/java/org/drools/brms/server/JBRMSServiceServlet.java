package org.drools.brms.server;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import javax.jcr.LoginException;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.http.HttpSession;

import org.drools.brms.client.rpc.MetaData;
import org.drools.brms.client.rpc.RepositoryService;
import org.drools.brms.client.rpc.RuleAsset;
import org.drools.brms.client.rpc.RuleContentText;
import org.drools.brms.client.rpc.TableConfig;
import org.drools.brms.client.rpc.TableDataResult;
import org.drools.brms.client.rpc.TableDataRow;
import org.drools.repository.CategorisableItem;
import org.drools.repository.CategoryItem;
import org.drools.repository.RepositoryConfigurator;
import org.drools.repository.RuleItem;
import org.drools.repository.RulePackageItem;
import org.drools.repository.RulesRepository;
import org.drools.repository.RulesRepositoryException;
import org.drools.repository.StateItem;

import com.google.gwt.user.client.rpc.SerializableException;
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
                                 String initialPackage) throws SerializableException {        
        try {
            RulePackageItem pkg = getRulesRepository().loadRulePackage( initialPackage );
            pkg.addRule( ruleName,
                                         description, initialCategory );

        } catch (RulesRepositoryException e) {
            throw new SerializableException(e.getMessage());
        }
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
    




    public TableDataResult loadRuleListForCategories(String categoryPath) throws SerializableException {
        RulesRepository repo = getRulesRepository();

        List list = repo.findRulesByCategory( categoryPath );
        TableDisplayHandler handler = new TableDisplayHandler();
        return handler.loadRuleListTable( list );
        
    }

    public TableConfig loadTableConfig(String listName) {
        TableDisplayHandler handler = new TableDisplayHandler();
        return handler.loadTableConfig(listName);
        
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

    /**
     * This actually does the hard work of loading up an asset based
     * on its format.
     */
    public RuleAsset loadRuleAsset(String uuid) throws SerializableException {
        RulesRepository repo = getRulesRepository();
        RuleItem item = repo.loadRuleByUUID( uuid );
        RuleAsset asset = new RuleAsset();
        
        asset.metaData = popuplateMetaData( item );
        
        
        asset.dateEffective = formatDate( item.getDateEffective() );
        asset.dateExpired = formatDate( item.getDateExpired() );
        
        //TODO: this could be refactored to there are different loadXXX methods, or 
        //use polymorphism or something, in any case avoiding this dirty if statement...
        //as we know at the "client" what we should be loaded from the format string.
        if (item.getFormat().equals( "DSL" )) {
            //ok here is where we do DSLs...
            throw new SerializableException("Can't load DSL rules just yet.");

        } else if (item.getFormat().equals( "DSL" )) {
        } else if (item.getFormat().equals( "DT" )) {
            //and here we do decision tables
            throw new SerializableException("Still working on this...");
        } else {
            //default to text, goode olde texte, just like mum used to make.
            RuleContentText text = new RuleContentText();
            text.content = item.getRuleContent();
            asset.content = text;

        }
        asset.metaData.packageName = item.getPackageName();
        
        return asset;
    }
    
    /** pretty print the date. */
    String formatDate(Calendar date) {
        if (date == null) {
            return "";
        }
        DateFormat f = DateFormat.getDateTimeInstance();
        return f.format( date.getTime() );
    }

    /** 
     * read in the meta data.
     * @param item
     * @return
     */
    MetaData popuplateMetaData(CategorisableItem item) {
        MetaData meta = new MetaData();
        meta.name = item.getName();
        meta.title = item.getTitle();
        
        List cats = item.getCategories();
        meta.categories = new String[cats.size()];
        for ( int i = 0; i < meta.categories.length; i++ ) {
            CategoryItem cat = (CategoryItem) cats.get(i);
            meta.categories[i] = cat.getName();            
        }
        
        meta.state = (item.getState() != null) ? item.getState().getName() : "";
        
        meta.coverage = item.getCoverage();
        meta.creator = item.getCreator();
        meta.description = item.getDescription();
        meta.externalRelation = item.getExternalRelation();
        meta.externalSource = item.getExternalSource();
        meta.format = item.getFormat();
        meta.lastCheckinComment = item.getCheckinComment();
        meta.lastContributor = item.getLastContributor();
        meta.lastModifiedDate = formatDate(item.getLastModified());
        meta.createdDate = formatDate( item.getCreatedDate() );
        meta.versionNumber = item.getVersionNumber();
        
        
        
        return meta;
    }


    
    


}
