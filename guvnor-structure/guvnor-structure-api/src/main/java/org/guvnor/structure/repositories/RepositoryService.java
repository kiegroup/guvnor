package org.guvnor.structure.repositories;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.jboss.errai.bus.server.annotations.Remote;
import org.uberfire.backend.vfs.Path;
import org.uberfire.java.nio.base.version.VersionRecord;

@Remote
public interface RepositoryService {

    RepositoryInfo getRepositoryInfo( final String alias );

    List<VersionRecord> getRepositoryHistory( final String alias,
                                              final int startIndex );

    List<VersionRecord> getRepositoryHistory( final String alias,
                                              final int startIndex,
                                              final int endIndex );

    List<VersionRecord> getRepositoryHistoryAll( final String alias );

    Repository getRepository( final String alias );

    Repository getRepository( final Path root );

    Collection<Repository> getRepositories();

    Repository createRepository( final OrganizationalUnit organizationalUnit,
                                 final String scheme,
                                 final String alias,
                                 final Map<String, Object> env ) throws RepositoryAlreadyExistsException;

    Repository createRepository( final String scheme,
                                 final String alias,
                                 final Map<String, Object> env ) throws RepositoryAlreadyExistsException;

    String normalizeRepositoryName( final String name );

    void addGroup( final Repository repository,
                   final String group );

    void removeGroup( final Repository repository,
                      final String group );

    void removeRepository( final String alias );

    Repository updateRepository( Repository repository, Map<String, Object> config );

}
