package org.guvnor.structure.organizationalunit;

import org.guvnor.structure.repositories.Repository;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.rpc.SessionInfo;

@Portable
public class RepoRemovedFromOrganizationalUnitEvent extends OrganizationalUnitEventBase {

    private Repository repository;

    public RepoRemovedFromOrganizationalUnitEvent() {
    }

    public RepoRemovedFromOrganizationalUnitEvent(final OrganizationalUnit organizationalUnit, final Repository repository, final SessionInfo sessionInfo) {
        super( organizationalUnit, sessionInfo );
        this.repository = repository;
    }

    public Repository getRepository() {
        return repository;
    }

    public void setRepository(Repository repository) {
        this.repository = repository;
    }
}
