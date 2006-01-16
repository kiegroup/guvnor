package org.drools.repository.db;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 * Dynamic proxy handler for all persistence operations.
 * Keeps the hibernate session and transaction handling away from the repository implementation.
 * 
 * This is the glue between the actual implementation and the interface.
 * Kind of like poor mans aspects. But I couldn't justify AOP for this little thing.
 * 
 * @author <a href="mailto:michael.neale@gmail.com"> Michael Neale</a>
 */
public class RepoProxyHandler
    implements
    InvocationHandler {
    

    private RepositoryImpl repoImpl = new RepositoryImpl();
    private Session session = null;
    private boolean stateful = false;
    
    
    /** 
     * This is essentially for stateless repository access.
     * Default implementation uses hibernates getCurrentSession to 
     * work with the current session. 
     */
    public RepoProxyHandler() {
    }
    
    /**
     * Allows stateful access of the repository.
     * @param stateful True if stateful operation is desired.
     */
    public RepoProxyHandler(boolean stateful) {
        if (stateful) {
            this.session = HibernateUtil.getSessionFactory().openSession();
            this.stateful = true;
        }
    }
    
    /**
     * This will initialise the session to the correct state.
     * Allows both stateless and stateful repository options.
     */
    public Object invoke(Object proxy,
                         Method method,
                         Object[] args) throws Throwable {

        
        Session session = getCurrentSession();

        if (this.stateful && method.getName().equals("close")) {
            session.close();
            return null;
        }
        
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            configureSession( session );            
            Object result = method.invoke(repoImpl, args);
            session.flush();
            tx.commit();
            return result;
        }
        catch (InvocationTargetException e) {
            rollback( tx );
            throw e.getTargetException();
        }
        catch (Exception e) {
            rollback( tx );
            throw e;
        }
    }

    private void rollback(Transaction tx) {
        if (tx !=null) {
            tx.rollback();
        }
    }


    /**
     * Set the connection for the listeners to use (they use their own session).
     * Enable the default filters for historical stuff
     * and then provide the session to the repo implementation.
     */
    private void configureSession(Session session) {
        StoreEventListener.setCurrentConnection( session.connection() );
        repoImpl.enableHistoryFilter( session );
        repoImpl.injectSession( session );        
    }
    

    /**
     * Uses a different session depending on if it is stateful or not.
     */
    private Session getCurrentSession() {
        if (stateful) {
            return session;
        } else {
            return HibernateUtil.getSessionFactory().getCurrentSession();
        }
    }
        

}
