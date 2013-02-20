package org.kie.guvnor.services.backend.file;

import org.jboss.errai.bus.server.annotations.Service;
import org.kie.commons.io.IOService;
import org.kie.commons.java.nio.base.options.CommentedOption;
import org.kie.guvnor.services.file.GenericFileService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.security.Identity;

import javax.inject.Inject;
import javax.inject.Named;

@Service
public class GenericFileServiceImpl
        implements GenericFileService {

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    private Paths paths;

    @Inject
    private Identity identity;

    @Override
    public void delete(Path path, String comment) {
        ioService.delete(paths.convert(path));
    }

    @Override
    public Path rename(Path path, String newName, String comment) {
        String targetName = path.getFileName().substring(0, path.getFileName().lastIndexOf("/") + 1) + newName;
        String targetURI = path.toURI().substring(0, path.toURI().lastIndexOf("/") + 1) + newName;
        Path targetPath = PathFactory.newPath(path.getFileSystem(), targetName, targetURI);
        ioService.move(paths.convert(path), paths.convert(targetPath), new CommentedOption(identity.getName(), comment));

        return targetPath;
    }

    @Override
    public Path copy(Path path, String newName, String comment) {
        String targetName = path.getFileName().substring(0, path.getFileName().lastIndexOf("/") + 1) + newName;
        String targetURI = path.toURI().substring(0, path.toURI().lastIndexOf("/") + 1) + newName;
        Path targetPath = PathFactory.newPath(path.getFileSystem(), targetName, targetURI);
        ioService.copy(paths.convert(path), paths.convert(targetPath), new CommentedOption(identity.getName(), comment));

        return targetPath;
    }
}
