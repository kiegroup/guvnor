package org.guvnor.common.services.backend.metadata;

import java.util.Date;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.thoughtworks.xstream.XStream;
import org.guvnor.common.services.backend.config.SafeSessionInfo;
import org.guvnor.common.services.backend.exceptions.ExceptionUtilities;
import org.guvnor.common.services.shared.metadata.CategoriesService;
import org.guvnor.common.services.shared.metadata.MetadataService;
import org.guvnor.common.services.shared.metadata.model.Categories;
import org.guvnor.common.services.shared.metadata.model.CategoriesModelContent;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.jboss.errai.bus.server.annotations.Service;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.base.options.CommentedOption;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.security.Identity;

@Service
@ApplicationScoped
public class CategoryServiceImpl
        implements CategoriesService {

    private final XStream xt = new XStream();

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    private MetadataService metadataService;

    @Inject
    private Identity identity;

    @Inject
    private SessionInfo sessionInfo;

    @Override
    public Path save(Path path, Categories content, Metadata metadata, String comment) {
        return Paths.convert(saveWithServerSidePath(Paths.convert(path),
                content, metadata, comment));
    }

    private org.uberfire.java.nio.file.Path saveWithServerSidePath(org.uberfire.java.nio.file.Path path,
            Categories content) {
        return saveWithServerSidePath(path, content, null, "");
    }

    private org.uberfire.java.nio.file.Path saveWithServerSidePath(
            org.uberfire.java.nio.file.Path path,
            Categories content,
            Metadata metadata,
            String comment) {
        try {

            ioService.write(path,
                    xt.toXML(content),
                    metadataService.setUpAttributes(Paths.convert(path),
                            metadata),
                    makeCommentedOption(comment));

            return path;
        } catch (Exception e) {
            throw ExceptionUtilities.handleException(e);
        }
    }

    private CommentedOption makeCommentedOption( final String commitMessage ) {
        final String name = identity.getName();
        final Date when = new Date();
        return new CommentedOption( getSessionInfo().getId(),
                name,
                null,
                commitMessage,
                when );
    }

    @Override
    public Categories load(Path path) {
        try {
            final String content = ioService.readAllString(Paths.convert(path));
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

        org.uberfire.java.nio.file.Path path = Paths.convert(pathToRoot).resolve("categories.xml");
        if (!ioService.exists(path)) {
            saveWithServerSidePath(path, new Categories());
        }

        Path categoriesPath = Paths.convert(path);

        categoriesModelContent.setPath(categoriesPath);
        categoriesModelContent.setCategories(load(categoriesPath));

        return categoriesModelContent;
    }

    @Override
    public Categories getCategoriesFromResource(final Path resource) {
        try {
            final org.uberfire.java.nio.file.Path categoriesPath = Paths.convert(resource).getRoot().resolve("categories.xml");

            return load(Paths.convert(categoriesPath));

        } catch (Exception e) {
            throw ExceptionUtilities.handleException(e);
        }
    }
    protected SessionInfo getSessionInfo() {
        return new SafeSessionInfo(sessionInfo);
    }
}
