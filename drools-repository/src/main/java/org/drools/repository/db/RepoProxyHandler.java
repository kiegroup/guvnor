package org.drools.repository.db;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.drools.repository.RepositoryException;
import org.drools.repository.RepositoryManagerImpl;
import org.hibernate.Session;
import org.hibernate.StaleObjectStateException;
import org.hibernate.Transaction;

/**
 * Dynamic proxy handler for all persistence operations.
 * Keeps the hibernate session and transaction handling away from the repository implementation.
 * 
 * It will enable the history filter before invoking any methods.
 * 
 * This is the glue between the actual implementation and the interface.
 * 
 * It also provides the stateful and stateless behaviour.
 * Stateful simple means that a session instance is created the first time, and 
 * kept around (long running sessions).
 * 
 * 
 * It can also be extended to provide user context to the implementation class 
 * (for auditing, access control and locking for instance).
 * 
 * @author <a href="mailto:michael.neale@gmail.com"> Michael Neale</a>
 */
public class RepoProxyHandler
    implements
    InvocationHandler {
    

    private RepositoryManagerImpl repoImpl = new RepositoryManagerImpl();
    private Session session = null;
    private boolean stateful = false;
    
    
    /** 
     * This is essentially for stateless repository access.
     * Default implementation uses hibernates getCurrentSession to 
     * work with the current session. 
     */
    public RepoProxyHandler() {
        this(false);
    }
    
    /**
     * Allows stateful access of the repository.
     * @param stateful True if stateful operation is desired.
     */
    public RepoProxyHandler(boolean stateful) {
        this.stateful = stateful;
        if (stateful) {
            this.session = HibernateUtil.getSessionFactory().openSession();
        }
    }
    
    /**
     * This will initialise the session to the correct state.
     * Allows both stateless and stateful repository options.
     * 
     * If an exception occurs in the Repo Impl, it will rollback
     * the transaction.
     * If the exception is of type RepositoryException, the session will be left
     * alone.
     * If the exception is of any other type then the session will be closed.
     * 
     */
    public Object invoke(Object proxy,
                         Method method,
                         Object[] args) throws Throwable {

        
        Session session = getCurrentSession();

        if (this.stateful && method.getName().equals("close")) {
            return handleCloseSession( session );
        }
        
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            configureSession( session );            
            Object result = method.invoke(repoImpl, args);            
            tx.commit();
            
            if (!stateful) {
                this.repoImpl.injectSession(null); //not really needed, but to prove it is stateless !
            }
            return result;
        }
        catch (InvocationTargetException e) {
            rollback( tx );
            checkForRepositoryException( session, e );
            throw e.getTargetException();
        }

    }

    /**
     * If its an instance of RepositoryException, we don't want to close the session.
     * It may just be a validation message being thrown.
     */
    private void checkForRepositoryException(Session session,
                                             InvocationTargetException e) {
        if (! (e.getTargetException() instanceof RepositoryException)) {
            try { 
                repoImpl.injectSession(null);
                session.close(); 
            } catch (Exception e2) { /*ignore*/ }
        }
    }

    /**
     * Should really only be called for stateful repository instances.
     */
    private Object handleCloseSession(Session session) {
        session.close();
        StoreEventListener.setCurrentConnection(null);
        return null;
    }

    private void rollback(Transaction tx) {
        if (tx != null) {
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
