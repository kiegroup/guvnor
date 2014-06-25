package org.guvnor.structure.navigator;

import java.util.List;

import org.guvnor.structure.repositories.Repository;
import org.jboss.errai.bus.server.annotations.Remote;
import org.uberfire.backend.vfs.Path;

@Remote
public interface FileNavigatorService {

    NavigatorContent listContent( final Path path );

    List<Repository> listRepositories();

}
