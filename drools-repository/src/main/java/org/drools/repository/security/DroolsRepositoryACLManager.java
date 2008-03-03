package org.drools.repository.security;

import java.util.ArrayList;
import java.util.Collection;

import javax.jcr.ItemNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.jackrabbit.core.ItemId;
import org.apache.jackrabbit.core.NodeId;
import org.apache.jackrabbit.core.security.AccessManager;
import org.apache.jackrabbit.uuid.UUID;
import org.apache.log4j.Logger;
import org.jboss.security.acl.ACL;
import org.jboss.security.acl.ACLEntry;
import org.jboss.security.acl.ACLEntryImpl;
import org.jboss.security.acl.ACLPersistenceStrategy;
import org.jboss.security.acl.ACLProvider;
import org.jboss.security.acl.ACLProviderImpl;
import org.jboss.security.acl.BasicACLPermission;
import org.jboss.security.acl.BitMaskPermission;
import org.jboss.security.acl.CompositeACLPermission;
import org.jboss.security.acl.JPAPersistenceStrategy;
import org.jboss.security.authorization.AuthorizationException;
import org.jboss.security.identity.Identity;

public class DroolsRepositoryACLManager {

    private Identity               identity;

    private ACLPersistenceStrategy strategy;
    private DroolsACLRegistration  registration;
    private ACLProvider            provider;

    private static Logger          log = Logger.getLogger( DroolsRepositoryAccessManager.class );

    public DroolsRepositoryACLManager(final Identity identity) {

        this.strategy = new JPAPersistenceStrategy();
        this.registration = new DroolsACLRegistration( strategy );
        this.provider = new ACLProviderImpl();
        this.provider.setPersistenceStrategy( strategy );
        this.identity = identity;

        log.debug( "ACL manager for user:" + identity.getName() );

    }

    public void setPermission(final String uuid,
                              final int permission) {

        UUIDResource localresource = new UUIDResource( uuid );

        Collection<ACLEntry> entries = new ArrayList<ACLEntry>();

        ACLEntry entry = new ACLEntryImpl( toSecurityByteMaskPermission( permission ),
                                           identity );

        entries.add( entry );

        registration.registerACL( localresource,
                                  entries );
    }

    public void removePermission(final UUID uuid) {
        ACL acl = this.strategy.getACL( new UUIDResource( uuid.toString() ) );
        this.strategy.removeACL( acl );
    }

    public boolean checkPermission(final ItemId id,
                                   int permissions) throws AuthorizationException,
                                                   ItemNotFoundException,
                                                   RepositoryException {

        Session session = DroolsRepositoryAccessManager.adminThreadlocal.get();
        UUID nodeUUID = getNodeUUIDFromItemId( id );

        if ( session != null && nodeUUID != null ) {
            UUIDResource localresource = new UUIDResource( nodeUUID.toString() );

            //            try {
            return this.provider.isAccessGranted( localresource,
                                                  identity,
                                                  toSecurityByteMaskPermission( permissions ) );

            // jcr bug see https://issues.apache.org/jira/browse/JCR-1359
            
            //            } catch ( AuthorizationException e ) {
            //
            //                Node nodeByUUID = session.getNodeByUUID( nodeUUID.toString() );
            //                boolean accessGranted = true;
            //
            //                while ( nodeByUUID != null || accessGranted ) {
            //                    session.getNodeByUUID( nodeUUID.toString() );
            //                    accessGranted = this.provider.isAccessGranted( localresource,
            //                                                                   identity,
            //                                                                   toSecurityByteMaskPermission( permissions ) );
            //                    nodeByUUID = nodeByUUID.getParent();
            //                }
            //            }

        }

        //log.debug( "Unable to find an ACL entry for asset " + nodeUUID );

        return true;
    }

    private UUID getNodeUUIDFromItemId(final ItemId id) {
        if ( id.denotesNode() ) {
            return ((NodeId) id).getUUID();
        } else {
            return null;
        }
    }

    private BitMaskPermission toSecurityByteMaskPermission(final int permission) {
        switch ( permission ) {
            case AccessManager.READ :
                return BasicACLPermission.READ;

            case AccessManager.WRITE :
                return new CompositeACLPermission( BasicACLPermission.UPDATE,
                                                   BasicACLPermission.CREATE );
            case AccessManager.REMOVE :
                return BasicACLPermission.DELETE;
        }
        return new CompositeACLPermission( permission );
    }
}
