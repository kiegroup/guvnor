package org.drools.repository.db;



import org.drools.repository.ApplicationDataDef;
import org.drools.repository.FunctionDef;
import org.drools.repository.ImportDef;
import org.drools.repository.RuleDef;
import org.drools.repository.RuleSetAttachment;
import org.drools.repository.RuleSetDef;
import org.drools.repository.RuleSetVersionInfo;
import org.drools.repository.Tag;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * The usual hibernate helper, with a few tweaks.
 * @author <a href="mailto:michael.neale@gmail.com"> Michael Neale</a>
 *
 */
public class HibernateUtil {

    private static final SessionFactory sessionFactory;

    static {
        try {
            
            Configuration cfg = new Configuration();            
            cfg.setInterceptor( new StoreEventListener() );
            registerPersistentClasses( cfg );
            cfg.configure("drools-repository-db.cfg.xml");
            
            sessionFactory = cfg.buildSessionFactory();
        }
        catch ( Throwable ex ) {
            // Make sure you log the exception, as it might be swallowed
            System.err.println( "Initial SessionFactory creation failed." + ex );
            throw new ExceptionInInitializerError( ex );
        }
    }

    /**
     * Use class based registration for refactor-friendly goodness.
     */
    private static void registerPersistentClasses(Configuration cfg) {
        cfg
            .addClass(ApplicationDataDef.class)
            .addClass(FunctionDef.class)
            .addClass(RuleDef.class)
            .addClass(Tag.class)
            .addClass(RuleSetDef.class)
            .addClass(RuleSetAttachment.class)
            .addClass(RuleSetVersionInfo.class)
            .addClass(ImportDef.class);
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

}
