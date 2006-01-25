package org.drools.repository.db;

import java.io.Serializable;
import java.sql.Connection;

import org.hibernate.EmptyInterceptor;
import org.hibernate.LockMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.type.Type;

/**
 * This event listener processes save history events. When an asset is updated
 * that requires a history record, it asks the asset if a history record should
 * be created. If it does then it will clone it, and save it as a history item
 * (using an seperate session that is not related to the current session).
 * 
 * @author <a href="mailto:michael.neale@gmail.com"> Michael Neale</a>
 */
public class StoreEventListener extends EmptyInterceptor {

    private static final long serialVersionUID = -5634072610999632779L;
    
    //use a threadlocal to get the currentConnection, 
    //as we may not always use currentSession semantics.
    private static ThreadLocal currentConnection = new ThreadLocal();

    public boolean onFlushDirty(Object entity,
                                Serializable id,
                                Object[] currentState,
                                Object[] previousState,
                                String[] propertyNames,
                                Type[] types) {
        if ( entity instanceof ISaveHistory ) {
            handleSaveHistory( entity );
        }
        return false;
    }

    /**
     * This will load up the old copy, and save it as a history record
     * (with a different identity).
     * Filters stop the history records from popping up in unwanted places .
     */
    private void handleSaveHistory(Object entity) {
        ISaveHistory versionable = (ISaveHistory) entity;
        
        Session session = getSessionFactory().openSession( (Connection) currentConnection.get() );
        ISaveHistory prev = (ISaveHistory) session.load( entity.getClass(),
                                                         versionable.getId(),
                                                         LockMode.NONE );
        ISaveHistory copy = (ISaveHistory) prev.copy();
        copy.setHistoricalId( versionable.getId() );
        copy.setHistoricalRecord( true );
        //session.beginTransaction();
        session.save( copy );
        //session.getTransaction().commit();
        session.flush();                        
        session.close();
    }
    
    /**
     * Used to set the current session so the interceptor can access it.
     * The idea is to share the same connection that any current transactions 
     * are using.
     */
    public static void setCurrentConnection(Connection conn) {
        currentConnection.set(conn);
    }

    private SessionFactory getSessionFactory() {
        return HibernateUtil.getSessionFactory();
    }

}
