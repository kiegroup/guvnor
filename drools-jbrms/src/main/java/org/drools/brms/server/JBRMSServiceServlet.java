package org.drools.brms.server;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.jcr.LoginException;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.http.HttpSession;

import org.drools.brms.client.common.AssetFormats;
import org.drools.brms.client.rpc.MetaData;
import org.drools.brms.client.rpc.RepositoryService;
import org.drools.brms.client.rpc.RuleAsset;
import org.drools.brms.client.rpc.RuleContentText;
import org.drools.brms.client.rpc.TableConfig;
import org.drools.brms.client.rpc.TableDataResult;
import org.drools.brms.server.util.MetaDataMapper;
import org.drools.repository.AssetItem;
import org.drools.repository.CategoryItem;
import org.drools.repository.PackageItem;
import org.drools.repository.RepositoryConfigurator;
import org.drools.repository.RulesRepository;
import org.drools.repository.RulesRepositoryException;

import com.google.gwt.user.client.rpc.IsSerializable;
import com.google.gwt.user.client.rpc.SerializableException;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/** 
 * This is the implementation of the repository service to drive the GWT based front end.
 * TODO: refactor this to use "Action" pattern or SSB pattern for 
 * transaction demarcation.
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

    private MetaDataMapper metaDataMapper;

    
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
    
    
    /**
     * This will create a new asset. It will be saved, but not checked in.
     * The initial state will be the draft state.
     */
    public String createNewRule(String ruleName,
                                 String description,
                                 String initialCategory,
                                 String initialPackage,
                                 String format) throws SerializableException {        
        try {
            PackageItem pkg = getRulesRepository().loadPackage( initialPackage );
            AssetItem asset = pkg.addAsset( ruleName, description, initialCategory, format );            
            getRulesRepository().save();
            return asset.getUUID();
        } catch (RulesRepositoryException e) {
            throw new SerializableException(e.getMessage());
        }

    }

    public String[] listRulePackages() {
        Iterator pkgs = getRulesRepository().listPackages();
        List result = new ArrayList();
        while(pkgs.hasNext()) {
            PackageItem pkg = (PackageItem) pkgs.next();
            result.add( pkg.getName() );
        }
        return (String[]) result.toArray( new String[result.size()] );
    }
    




    public TableDataResult loadRuleListForCategories(String categoryPath) throws SerializableException {
        RulesRepository repo = getRulesRepository();

        List list = repo.findAssetsByCategory( categoryPath );
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
        AssetItem item = repo.loadAssetByUUID( uuid );
        RuleAsset asset = new RuleAsset();
        
        asset.metaData = popuplateMetaData( item );
        
        //TODO: possibly move this to a different structure, perhaps even into the drools-repository itself.
        if (item.getFormat().equals( AssetFormats.DSL_TEMPLATE_RULE)) {
            //ok here is where we do DSLs...
            throw new SerializableException("Can't load DSL rules just yet.");

        } else if (item.getFormat().equals( "DT" )) {
            //and here we do decision tables
            throw new SerializableException("Still working on this...");
        } else {
            //default to text, goode olde texte, just like mum used to make.
            RuleContentText text = new RuleContentText();
            text.content = item.getContent();
            asset.content = text;

        }
        asset.metaData.packageName = item.getPackageName();
        asset.uuid = uuid;
        
        return asset;
    }
    

    /** 
     * read in the meta data.
     * @param item
     * @return
     */
    MetaData popuplateMetaData(AssetItem item) {
        MetaData meta = new MetaData();

        
        List cats = item.getCategories();
        meta.categories = new String[cats.size()];
        for ( int i = 0; i < meta.categories.length; i++ ) {
            CategoryItem cat = (CategoryItem) cats.get(i);
            meta.categories[i] = cat.getFullPath();          
        }
        
        meta.state = (item.getState() != null) ? item.getState().getName() : "";

        getMetaDataMapper().copyToMetaData( meta, item );
        
        meta.createdDate = calendarToDate(item.getCreatedDate());
        meta.dateEffective = calendarToDate( item.getDateEffective() );
        meta.dateExpired = calendarToDate( item.getDateExpired() );
        

        
        
        return meta;
    }

    private Date calendarToDate(Calendar createdDate) {
        if (createdDate == null) return null;
        return createdDate.getTime();
    }
    
    private Calendar dateToCalendar(Date date) {
        if (date == null) return null;
        Calendar cal = Calendar.getInstance();
        cal.setTime( date );
        return cal;
    }    

    private MetaDataMapper getMetaDataMapper() {
        if (this.metaDataMapper == null)  this.metaDataMapper = new MetaDataMapper();
        return this.metaDataMapper;
    }

    public String checkinVersion(RuleAsset asset) throws SerializableException {        
        RulesRepository repo = getRulesRepository();
        
        AssetItem rule = repo.loadAssetByUUID( asset.uuid );
        
        MetaData meta = asset.metaData;
        
        getMetaDataMapper().copyFromMetaData( meta, rule );
        
        rule.updateDateEffective( dateToCalendar( meta.dateEffective ) );
        rule.updateDateExpired( dateToCalendar( meta.dateExpired ) );        
        
        rule.updateCategoryList( meta.categories );
        updateContentToAsset( rule, asset.content );
        
        
        
        rule.checkin( meta.checkinComment );
        
        return rule.getUUID();
    }
    

    private void updateContentToAsset(AssetItem repoAsset, IsSerializable content) throws SerializableException {
        if (content instanceof RuleContentText) {
            repoAsset.updateContent( ((RuleContentText)content).content );        
        } else {
            throw new SerializableException("Not able to handle that type of content just yet...");
        }
    }


    
    


}
