package org.guvnor.common.services.backend.exceptions;

import org.guvnor.common.services.shared.exceptions.GenericPortableException;
import org.jboss.errai.config.rebind.EnvUtil;

/**
 * Utilities for exception handling.
 */
public class ExceptionUtilities {

    /**
     * Helper to return a @Portable RuntimeException.
     * @param e
     * @return
     */
    public static RuntimeException handleException( final Exception e ) {
        if ( EnvUtil.isPortableType( e.getClass() ) ) {
            if ( e instanceof RuntimeException ) {
                return (RuntimeException) e;
            } else {
                return new GenericPortableException( e.getMessage() );
            }
        }
        return new GenericPortableException( e.getMessage() );
    }

}
