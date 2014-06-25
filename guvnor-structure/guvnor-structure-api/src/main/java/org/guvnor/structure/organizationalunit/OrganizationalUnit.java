package org.guvnor.structure.organizationalunit;

import java.util.Collection;

import org.guvnor.structure.repositories.Repository;
import org.uberfire.security.authz.RuntimeResource;
import org.uberfire.commons.data.Cacheable;

public interface OrganizationalUnit extends RuntimeResource, Cacheable {

    String getName();

    String getOwner();

    Collection<Repository> getRepositories();

}
