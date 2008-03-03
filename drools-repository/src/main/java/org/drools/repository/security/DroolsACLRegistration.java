package org.drools.repository.security;

import java.util.Collection;

import org.jboss.security.acl.ACLEntry;
import org.jboss.security.acl.ACLPersistenceStrategy;
import org.jboss.security.acl.ACLRegistration;
import org.jboss.security.authorization.Resource;

public class DroolsACLRegistration
    implements
    ACLRegistration {

    private final ACLPersistenceStrategy strategy;

    public DroolsACLRegistration(ACLPersistenceStrategy strategy) {
        this.strategy = strategy;
    }

    public void deRegisterACL(Resource resource) {
        this.strategy.removeACL( resource );
    }

    public void registerACL(Resource resource) {
        this.strategy.createACL( resource );
    }

    public void registerACL(Resource resource,
                            Collection<ACLEntry> entries) {
        this.strategy.createACL( resource,
                                 entries );
    }

}
