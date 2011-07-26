package org.drools.guvnor.server;

import org.drools.repository.RepositoryFilter;
import org.jboss.seam.solder.beanManager.BeanManagerLocator;

public abstract class AbstractFilter<T>
    implements
    RepositoryFilter {
    private final Class<T> clazz;

    public AbstractFilter(Class<T> clazz) {
        this.clazz = clazz;

    }

    @SuppressWarnings("unchecked")
    public boolean accept(Object artifact,
                          String action) {
        if ( artifact == null || !clazz.isAssignableFrom( artifact.getClass() ) ) {
            return false;
        }
        // for GWT hosted mode - debug only
        BeanManagerLocator beanManagerLocator = new BeanManagerLocator();
        if ( !beanManagerLocator.isBeanManagerAvailable() ) {
            return true;
        }
        return checkPermission( (T) artifact,
                                action );
    }

    protected abstract boolean checkPermission(T t,
                                               String action);
}
