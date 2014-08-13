/*
 * Copyright 2014 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.guvnor.common.services.shared.metadata.model;

import org.uberfire.backend.vfs.Path;
import org.uberfire.commons.validation.PortablePreconditions;

public class CategoriesModelContent {

    private Path path;

    private Categories categories;

    private Overview overview;

    public CategoriesModelContent() {
    }

    public CategoriesModelContent(Path path, Categories categories, Overview overview) {
        this.path = PortablePreconditions.checkNotNull("path",path);
        this.categories = PortablePreconditions.checkNotNull("categories",categories);
        this.overview = PortablePreconditions.checkNotNull("overview", overview);
    }

    public Path getPath() {
        return path;
    }

    public Categories getCategories() {
        return categories;
    }

    public Overview getOverview() {
        return overview;
    }
}
