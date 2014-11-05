package org.guvnor.structure.backend.repositories.git;

import org.guvnor.structure.backend.repositories.EnvironmentParameters;
import org.guvnor.structure.repositories.PublicURI;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.impl.DefaultPublicURI;
import org.guvnor.structure.repositories.impl.git.GitRepository;
import org.guvnor.structure.server.config.ConfigGroup;
import org.guvnor.structure.server.config.ConfigItem;
import org.guvnor.structure.server.config.PasswordService;
import org.guvnor.structure.server.config.SecureConfigItem;
import org.guvnor.structure.server.repositories.RepositoryFactoryHelper;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.FileSystemAlreadyExistsException;
import org.uberfire.java.nio.file.Path;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.net.URI;
import java.util.*;

import static org.guvnor.structure.repositories.impl.git.GitRepository.SCHEME;
import static org.uberfire.backend.server.util.Paths.convert;
import static org.uberfire.commons.validation.Preconditions.checkNotNull;

@ApplicationScoped
public class GitRepositoryFactoryHelper implements RepositoryFactoryHelper {

    private IOService ioService;

    @Inject
    private PasswordService secureService;


    public GitRepositoryFactoryHelper() {
    }

    @Inject
    public GitRepositoryFactoryHelper(@Named("ioStrategy") IOService ioService) {
        this.ioService = ioService;
    }

    @Override
    public boolean accept(final ConfigGroup repoConfig) {
        checkNotNull("repoConfig", repoConfig);
        final ConfigItem<String> schemeConfigItem = repoConfig.getConfigItem(EnvironmentParameters.SCHEME);
        checkNotNull("schemeConfigItem", schemeConfigItem);
        return SCHEME.equals(schemeConfigItem.getValue());
    }

    @Override
    public Repository newRepository(final ConfigGroup repoConfig) {
        validate(repoConfig);

        String branch = repoConfig.getConfigItemValue(EnvironmentParameters.BRANCH);
        if (branch == null) {
            branch = "master";
        }

        return newRepository(repoConfig, branch);
    }

    public Repository newRepository(ConfigGroup repoConfig, String branch) {
        validate(repoConfig);
        checkNotNull("branch", branch);

        final GitRepository repo = new GitRepository(repoConfig.getName());

        for (final ConfigItem item : repoConfig.getItems()) {
            if (item instanceof SecureConfigItem) {
                repo.addEnvironmentParameter(item.getName(), secureService.decrypt(item.getValue().toString()));
            } else {
                repo.addEnvironmentParameter(item.getName(), item.getValue());
            }
        }

        if (!repo.isValid()) {
            throw new IllegalStateException("Repository " + repoConfig.getName() + " not valid");
        }

        FileSystem fs = null;
        URI uri = null;
        try {
            uri = URI.create(repo.getUri());
            fs = ioService.newFileSystem(uri, new HashMap<String, Object>(repo.getEnvironment()) {{
                if (!repo.getEnvironment().containsKey("origin")) {
                    put("init", true);
                }
            }});
        } catch (final FileSystemAlreadyExistsException e) {
            fs = ioService.getFileSystem(uri);
        } catch (final Throwable ex) {
            throw new RuntimeException(ex.getCause().getMessage(), ex);
        }

        org.uberfire.backend.vfs.Path defaultRoot = convert(fs.getRootDirectories().iterator().next());
        Map<String, org.uberfire.backend.vfs.Path> branches = getBranches(fs);
        if (branches.containsKey(branch)) {
            defaultRoot = branches.get(branch);
        }
        repo.setBranches(branches);

        repo.setRoot(defaultRoot);

        repo.changeBranch(branch);

        final String[] uris = fs.toString().split("\\r?\\n");
        final List<PublicURI> publicURIs = new ArrayList<PublicURI>(uris.length);

        for (final String s : uris) {
            final int protocolStart = s.indexOf("://");
            final PublicURI publicURI;
            if (protocolStart > 0) {
                publicURI = new DefaultPublicURI(s.substring(0, protocolStart), s);
            } else {
                publicURI = new DefaultPublicURI(s);
            }
            publicURIs.add(publicURI);
        }
        repo.setPublicURIs(publicURIs);

        return repo;
    }

    /**
     * collect all branches
     *
     * @param fs
     * @return
     */
    private Map<String, org.uberfire.backend.vfs.Path> getBranches(FileSystem fs) {
        Map<String, org.uberfire.backend.vfs.Path> branches = new HashMap<String, org.uberfire.backend.vfs.Path>();
        for (final Path path : fs.getRootDirectories()) {
            String gitBranch = getBranchName(path);
            branches.put(gitBranch, convert(path));
        }
        return branches;
    }

    protected String getBranchName(final Path path) {
        URI uri = path.toUri();
        String gitBranch = uri.getAuthority();

        if (gitBranch.indexOf("@") != -1) {
            return gitBranch.split("@")[0];
        }

        return gitBranch;
    }

    private void validate(ConfigGroup repoConfig) {
        checkNotNull("repoConfig", repoConfig);
        final ConfigItem<String> schemeConfigItem = repoConfig.getConfigItem(EnvironmentParameters.SCHEME);
        checkNotNull("schemeConfigItem", schemeConfigItem);
    }
}
