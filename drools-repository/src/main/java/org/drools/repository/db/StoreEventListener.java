package org.drools.repository.db;

import java.io.Serializable;

import org.drools.repository.IVersionable;
import org.hibernate.EmptyInterceptor;
import org.hibernate.HibernateException;
import org.hibernate.event.SaveOrUpdateEvent;
import org.hibernate.event.SaveOrUpdateEventListener;
import org.hibernate.type.Type;

public class StoreEventListener extends EmptyInterceptor implements SaveOrUpdateEventListener {

    private static final long serialVersionUID = -5634072610999632779L;

    public boolean onSave(Object entity,
                          Serializable id,
                          Object[] state,
                          String[] propertyNames,
                          Type[] types) {
        if (entity instanceof IVersionable) {
           // System.out.println("VERSIONABLE INTERCEPT: " + id);
            IVersionable versionable = (IVersionable) entity;
            if (versionable.getId() != null) {
                System.out.println("ITS A NEW VERSION");
            }
        }       
        
        return false;
    }

    public void onSaveOrUpdate(SaveOrUpdateEvent event) throws HibernateException {
        Object ent = event.getEntity();     
        System.out.println("OnSaveOrUpdate");
        if (ent instanceof IVersionable) {
            IVersionable versionable = (IVersionable) ent;
            if (versionable.getId() != null) {
                System.out.println("!!!! WE have an update");
            }
        }
        
    }


    
    
}
