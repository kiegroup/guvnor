package org.guvnor.common.services.shared.metadata;

import org.guvnor.common.services.shared.file.SupportsCreate;
import org.guvnor.common.services.shared.file.SupportsRead;
import org.guvnor.common.services.shared.file.SupportsUpdate;
import org.guvnor.common.services.shared.metadata.model.Categories;
import org.guvnor.common.services.shared.metadata.model.CategoriesModelContent;
import org.jboss.errai.bus.server.annotations.Remote;
import org.uberfire.backend.vfs.Path;

@Remote
public interface CategoriesService
    extends
        SupportsRead<Categories>,
        SupportsUpdate<Categories> {

    Categories getCategoriesFromResource( final Path resource );

    CategoriesModelContent getContentByRoot(Path pathToProjectRoot);
}
