package org.drools.guvnor.server;

import org.drools.repository.RepositoryFilter;
import org.jboss.seam.security.Identity;

public abstract class AbstractFilter<T>
    implements
    RepositoryFilter {
    
    private final Class<T> clazz;
    protected final Identity identity;

    public AbstractFilter(Class<T> clazz, Identity identity) {
        this.clazz = clazz;
        this.identity = identity;
    }

    @SuppressWarnings("unchecked")
    public boolean accept(Object artifact,
                          String action) {
        if ( artifact == null || !clazz.isAssignableFrom( artifact.getClass() ) ) {
            return false;
        }
        if (identity == null) {
            // TODO for tests only ... Behaves like the pre-seam3 code: tests should be fixed to not require this!
            return true;
        }
        return checkPermission( (T) artifact,
                                action );
    }

    protected abstract boolean checkPermission(T t,
                                               String action);
}
