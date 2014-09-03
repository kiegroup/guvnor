package org.guvnor.structure.organizationalunit;

import org.guvnor.structure.repositories.Repository;
import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class RepoRemovedFromOrganizationalUnitEvent {

    private OrganizationalUnit organizationalUnit;
    private Repository repository;

    public RepoRemovedFromOrganizationalUnitEvent() {
    }

    public RepoRemovedFromOrganizationalUnitEvent(final OrganizationalUnit organizationalUnit, final Repository repository) {
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
