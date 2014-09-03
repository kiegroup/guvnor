package org.guvnor.structure.organizationalunit;

import org.guvnor.structure.repositories.Repository;
import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class RepoAddedToOrganizationaUnitEvent {

    private OrganizationalUnit organizationalUnit;
    private Repository repository;

    public RepoAddedToOrganizationaUnitEvent() {
    }

    public RepoAddedToOrganizationaUnitEvent(final OrganizationalUnit organizationalUnit, final Repository repository) {
        this.organizationalUnit = organizationalUnit;
        this.repository = repository;
    }

    public OrganizationalUnit getOrganizationalUnit() {
        return organizationalUnit;
    }

    public void setOrganizationalUnit( final OrganizationalUnit organizationalUnit ) {
        this.organizationalUnit = organizationalUnit;
    }

    public Repository getRepository() {
        return repository;
    }

    public void setRepository(Repository repository) {
        this.repository = repository;
    }
}
