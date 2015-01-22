package org.guvnor.structure.organizationalunit;

import org.guvnor.structure.repositories.Repository;
import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class RepoAddedToOrganizationaUnitEvent extends OrganizationalUnitEventBase {

    private Repository repository;

    public RepoAddedToOrganizationaUnitEvent() {
    }

    public RepoAddedToOrganizationaUnitEvent(final OrganizationalUnit organizationalUnit, final Repository repository, final String userName ) {
        super( organizationalUnit, userName );
        this.repository = repository;
    }

    public Repository getRepository() {
        return repository;
    }

    public void setRepository(Repository repository) {
        this.repository = repository;
    }
}
