package org.drools.repository.db;

import java.io.Serializable;
import java.security.Principal;
import java.sql.Connection;
import java.util.Date;

import org.drools.repository.Asset;
import org.hibernate.EmptyInterceptor;
import org.hibernate.LockMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.type.Type;

/**
 * This event listener processes database events using an interceptor. 
 * 
 * When an asset is updated that requires a history record, a history record will be created by 
 * loading and then copying the old copy of the data. 
 * (using an seperate session that is not related to the current session, but sharing the same connection).
 * 
 * Note that it will also save audit information about whom saved the data.
 * 
 * This can be extended in future to provide additional audit trail information, or instance based security
 * (to enforce ACLs on save etc).
 * 
 * @author <a href="mailto:michael.neale@gmail.com"> Michael Neale</a>
 */
public class StoreInterceptor extends EmptyInterceptor {

    private static final long serialVersionUID = -5634072610999632779L;
    
    //use a threadlocal to get the currentConnection, 
    //as we may not always use currentSession semantics.
    private static ThreadLocal currentConnection = new ThreadLocal();

    //we also need the current user if it has been set.
    private static ThreadLocal currentUser = new ThreadLocal();
    
    /**
     * Create historical records, and log events.
     */
    public boolean onFlushDirty(Object entity,
                                Serializable id,
                                Object[] currentState,
                                Object[] previousState,
                                String[] propertyNames,
                                Type[] types) {
        if ( entity instanceof ISaveHistory ) {
            handleSaveHistory( entity );
        }
        
        return handleUserSaveInfo( entity,
                                   currentState,
                                   propertyNames );        
    }
    
    /** record who and when */
    public boolean onSave(Object entity,
                          Serializable id,
                          Object[] currentState,
                          String[] propertyNames,
                          Type[] types) {

        return handleUserSaveInfo( entity,
                                   currentState,
                                   propertyNames );
        
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
    
    /**
     * Set the current user for auditing purposes.
     * This is backed by a threadlocal.
     */
    public static void setCurrentUser(Principal user) {
        currentUser.set(user);
    }

    private SessionFactory getSessionFactory() {
        return HibernateUtil.getSessionFactory();
    }



    /** Log who saved, what, when */
    private boolean handleUserSaveInfo(Object entity,
                                       Object[] currentState,
                                       String[] propertyNames) {
        if (entity instanceof Asset) {
            Principal user = (Principal) currentUser.get();
            boolean changed = false;
                
                for ( int i=0; i < propertyNames.length; i++ ) {
                    if ( "lastSavedDate".equals( propertyNames[i] ) ) {
                        currentState[i] = new Date();
                        changed = true;
                    } else if (user != null && "lastSavedByUser".equals( propertyNames[i]) ) {
                        currentState[i] = user.getName();
                        changed = true;
                    }
                }
            return changed;
        } else {
            return false;
        }
        
    }

}
