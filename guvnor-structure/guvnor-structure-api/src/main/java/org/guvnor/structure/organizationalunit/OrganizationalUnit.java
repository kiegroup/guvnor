package org.guvnor.structure.organizationalunit;

import java.util.Collection;

import org.guvnor.structure.repositories.Repository;
import org.uberfire.commons.data.Cacheable;
import org.uberfire.security.authz.RuntimeContentResource;

public interface OrganizationalUnit extends RuntimeContentResource, Cacheable {

    String getName();

    String getOwner();

    String getDefaultGroupId();

    Collection<Repository> getRepositories();

}
