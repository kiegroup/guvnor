package org.drools.guvnor.server;

import org.jboss.errai.bus.server.annotations.Service;
import org.uberfire.backend.FileExplorerRootService;
import org.uberfire.backend.Root;
import org.uberfire.backend.vfs.ActiveFileSystems;
import org.uberfire.backend.vfs.Path;
import org.uberfire.shared.mvp.impl.DefaultPlaceRequest;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static java.util.Collections.unmodifiableSet;

@Service
@ApplicationScoped
public class FileExplorerRootServiceImpl implements FileExplorerRootService {

    protected final Set<Root> roots = new HashSet<Root>();

    @Inject
    private ActiveFileSystems fileSystems;

    @PostConstruct
    protected void init() {
        setupGitRepos();
    }

    private void setupGitRepos() {
        final Path rootPath = fileSystems.getBootstrapFileSystem().getRootDirectories().get(0);
        final Root root = new Root(rootPath, new DefaultPlaceRequest("RepositoryEditor"));

        roots.add(root);
    }

    @Override
    public Collection<Root> listRoots() {
        return unmodifiableSet(roots);
    }

    @Override
    public void addRoot(final Root root) {
        roots.add(root);
    }

    @Override
    public void removeRoot(final Root root) {
        roots.remove(root);
    }

    @Override
    public void clear() {
        roots.clear();
    }

}
