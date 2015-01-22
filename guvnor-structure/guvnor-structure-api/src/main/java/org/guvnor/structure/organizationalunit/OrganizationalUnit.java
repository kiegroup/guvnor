package org.guvnor.structure.organizationalunit;

import java.util.Collection;

import org.guvnor.structure.repositories.Repository;
import org.uberfire.commons.data.Cacheable;
import org.uberfire.security.authz.RuntimeResource;

public interface OrganizationalUnit extends RuntimeResource, Cacheable {

    String getName();

    String getOwner();

    String getDefaultGroupId();

    Collection<Repository> getRepositories();

}
