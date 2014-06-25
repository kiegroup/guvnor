package org.guvnor.structure.server.repositories;

import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.server.config.ConfigGroup;

public interface RepositoryFactoryHelper {

    boolean accept( ConfigGroup repoConfig );

    Repository newRepository( ConfigGroup repoConfig );
}
