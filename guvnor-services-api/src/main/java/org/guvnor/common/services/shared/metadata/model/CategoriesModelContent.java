package org.guvnor.common.services.shared.metadata.model;

import org.uberfire.backend.vfs.Path;

public class CategoriesModelContent {

    private Path path;

    private Categories categories;

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public Categories getCategories() {
        return categories;
    }

    public void setCategories(Categories categories) {
        this.categories = categories;
    }
}
