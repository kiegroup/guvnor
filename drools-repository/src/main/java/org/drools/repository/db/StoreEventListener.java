package org.drools.repository.db;

import java.io.Serializable;

import org.drools.repository.ISaveHistory;
import org.drools.repository.IVersionable;
import org.drools.repository.RuleDef;
import org.hibernate.EmptyInterceptor;
import org.hibernate.LockMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.type.Type;

public class StoreEventListener extends EmptyInterceptor {

    private static final long serialVersionUID = -5634072610999632779L;

    
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

    private void handleSaveHistory(Object entity) {
        ISaveHistory versionable = (ISaveHistory) entity;
        
        Session current = getSessionFactory().getCurrentSession();
        Session session = getSessionFactory().openSession( current.connection() );

        System.out.println( "POSSIBLY SAVING COPY" );

        ISaveHistory prev = (ISaveHistory) session.load( entity.getClass(),
                                               versionable.getId(),
                                               LockMode.NONE );
        if ( versionable.isStateChanged(prev) )  {
            ISaveHistory copy = (ISaveHistory) versionable.copy();
            copy.setHistoricalId( prev.getId() );
            copy.setHistoricalRecord( true );
            session.save( copy );
            session.flush();
            System.out.println( "SAVING HISTORY COPY" );
        }
        else {
            System.out.println( "NOPE, not saving." );
        }
        
    }

 

//    public boolean onFlushDirty(Object entity,
//                                Serializable id,
//                                Object[] currentState,
//                                Object[] previousState,
//                                String[] propertyNames,
//                                Type[] types) {
//        if (entity instanceof IVersionable) {
//            handleVersionable( entity );            
//        }
//        return false;
//    }
//    
//
//    private void handleVersionable(Object entity) {
//        IVersionable versionable = (IVersionable) entity;        
//        if (versionable instanceof RuleDef) {
//            handleRuleDef( versionable );
//        }
//    }
//
//
//    private void handleRuleDef(IVersionable versionable) {
//        RuleDef def = (RuleDef) versionable;
//        Session current = getSessionFactory().getCurrentSession();
//        Session session = getSessionFactory().openSession(current.connection());
//        
//        System.out.println("POSSIBLY SAVING COPY");
//        
//        RuleDef prev = (RuleDef) session.load(RuleDef.class, def.getId(), LockMode.NONE);
//        if (!prev.getContent().equals(def.getContent()) ){
//            RuleDef copy = (RuleDef) def.copy();
//            copy.setHistoricalId(prev.getId());
//            copy.setHistoricalRecord(true);
//            session.save(copy);
//            session.flush();
//            System.out.println("SAVING RULE HISTORY COPY");
//        } else {
//            System.out.println("NOPE, not saving.");
//        }
//    }
//
    
    
    
    private SessionFactory getSessionFactory() {
        return HibernateUtil.getSessionFactory();
    }


    
    
}
