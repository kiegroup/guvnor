package org.drools.repository.db;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.event.SaveOrUpdateEventListener;

public class HibernateUtil {

    private static final SessionFactory sessionFactory;

    static {
        try {
            // Create the SessionFactory from hibernate.cfg.xml

            Configuration cfg = new Configuration();

            SaveOrUpdateEventListener[] listeners = new SaveOrUpdateEventListener[]{new StoreEventListener()};
            
            cfg.setInterceptor( new StoreEventListener() );
            cfg.getEventListeners().setUpdateEventListeners( listeners );
            cfg.configure();
            sessionFactory = cfg.buildSessionFactory();
        }
        catch ( Throwable ex ) {
            // Make sure you log the exception, as it might be swallowed
            System.err.println( "Initial SessionFactory creation failed." + ex );
            throw new ExceptionInInitializerError( ex );
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

}
