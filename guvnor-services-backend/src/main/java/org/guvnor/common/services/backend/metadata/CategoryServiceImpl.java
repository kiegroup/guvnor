package org.guvnor.common.services.backend.metadata;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.thoughtworks.xstream.XStream;
import org.guvnor.common.services.backend.exceptions.ExceptionUtilities;
import org.guvnor.common.services.shared.metadata.CategoriesService;
import org.guvnor.common.services.shared.metadata.model.Categories;
import org.guvnor.common.services.shared.metadata.model.CategoriesModelContent;
import org.jboss.errai.bus.server.annotations.Service;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;

@Service
@ApplicationScoped
public class CategoryServiceImpl implements CategoriesService {

    private final XStream xt = new XStream();

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    private Paths paths;

    @Override
    public void save(final Path path,
            final Categories content) {
        saveWithServerSidePath(paths.convert(path),
                content);
    }

    private void saveWithServerSidePath(org.uberfire.java.nio.file.Path path,
            Categories content) {
        try {
            ioService.write(path,
                    xt.toXML(content));

        } catch (Exception e) {
            throw ExceptionUtilities.handleException(e);
        }
    }

    @Override
    public Categories getContent(final Path path) {
        try {
            final String content = ioService.readAllString(paths.convert(path));
            final Categories categories;

            if (content.trim().equals("")) {
                categories = new Categories();
            } else {
                categories = (Categories) xt.fromXML(content);
            }

            return categories;

        } catch (Exception e) {
            throw ExceptionUtilities.handleException(e);
        }
    }

    @Override
    public CategoriesModelContent getContentByRoot(Path pathToRoot) {
        CategoriesModelContent categoriesModelContent = new CategoriesModelContent();

        org.uberfire.java.nio.file.Path path = paths.convert(pathToRoot).resolve("categories.xml");
        if (!ioService.exists(path)) {
            saveWithServerSidePath(path, new Categories());
        }

        Path categoriesPath = paths.convert(path);

        categoriesModelContent.setPath(categoriesPath);
        categoriesModelContent.setCategories(getContent(categoriesPath));

        return categoriesModelContent;
    }

    @Override
    public Categories getCategoriesFromResource(final Path resource) {
        try {
            final org.uberfire.java.nio.file.Path categoriesPath = paths.convert(resource).getRoot().resolve("categories.xml");

            return getContent(paths.convert(categoriesPath));

        } catch (Exception e) {
            throw ExceptionUtilities.handleException(e);
        }
    }
}
