package org.drools.repository.security;

import java.security.Principal;

import javax.jcr.AccessDeniedException;
import javax.jcr.ItemNotFoundException;
import javax.jcr.NoSuchWorkspaceException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.jackrabbit.core.ItemId;
import org.apache.jackrabbit.core.security.AMContext;
import org.apache.jackrabbit.core.security.AccessManager;
import org.apache.log4j.Logger;
import org.jboss.security.authorization.AuthorizationException;
import org.jboss.security.identity.Identity;
import org.jboss.security.identity.plugins.IdentityFactory;

/**
 * @author Fernando Meyer
 */

public class DroolsRepositoryAccessManager
    implements
    AccessManager {

    private static Logger                log              = Logger.getLogger( DroolsRepositoryAccessManager.class );

    public static ThreadLocal<Session>   adminThreadlocal = new ThreadLocal<Session>();
    private String                       defaultpermission;

    /**
     * Identity whose access rights this AccessManager should reflect
     */
    protected Identity                   identity;

    /**
     * hierarchy manager used for ACL-based access control model
     */
    protected DroolsRepositoryACLManager aclManager;

    private boolean                      initialized;

    public DroolsRepositoryAccessManager() {
        initialized = false;
    }

    /**
     * {@inheritDoc}
     */

    public void init(final AMContext context) throws AccessDeniedException,
                                             Exception {

        if ( initialized ) {
            throw new IllegalStateException( "already initialized" );
        }

        for ( Principal principal : context.getSubject().getPrincipals() ) {
            identity = IdentityFactory.createIdentity( principal.getName() );
        }

        aclManager = new DroolsRepositoryACLManager( identity );

        initialized = true;

        log.debug( "Repository Access Manager initialized" );
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void close() throws Exception {
        if ( !initialized ) {
            throw new IllegalStateException( "not initialized" );
        }

        Session session = adminThreadlocal.get();
        session.logout();

        initialized = false;
    }

    /**
     * {@inheritDoc}
     */
    public void checkPermission(final ItemId id,
                                final int permissions) throws AccessDeniedException,
                                                      ItemNotFoundException,
                                                      RepositoryException {

        if ( !initialized ) {
            throw new IllegalStateException( "not initialized" );
        }

        try {
            if ( aclManager.checkPermission( id,
                                             permissions ) == false ) {
                throw new AccessDeniedException( "User doesn't have enough permission" );
            }
        } catch ( AuthorizationException e ) {
        }
    }

    public boolean isGranted(final ItemId id,
                             final int permissions) throws ItemNotFoundException,
                                                   RepositoryException {

        if ( !initialized ) {
            throw new IllegalStateException( "not initialized" );
        }

        if ( identity.getName() == "ADMINISTRATOR" ) {
            return true;
        }

        try {
            return aclManager.checkPermission( id,
                                               permissions );
        } catch ( Exception e ) {
            // if there isn't any access config then should return the default value
            // RESTRICT or GRANT
            // log.debug( "Unable to find an ACL entry for asset " + nodeUUID );
            return true;
        }
    }

    public boolean canAccess(final String workspaceName) throws NoSuchWorkspaceException,
                                                        RepositoryException {
        System.out.println( "canAccess: " + workspaceName );
        return true;
    }

    public String getDefaultpermission() {
        return defaultpermission;
    }

    public void setDefaultpermission(final String defaultpermission) {
        this.defaultpermission = defaultpermission;
    }
}
