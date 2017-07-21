/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
    */
package org.guvnor.structure.backend.repositories.git;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.guvnor.structure.repositories.PublicURI;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.impl.DefaultPublicURI;
import org.guvnor.structure.repositories.impl.git.GitRepository;
import org.guvnor.structure.server.config.ConfigGroup;
import org.guvnor.structure.server.config.ConfigItem;
import org.guvnor.structure.server.config.PasswordService;
import org.guvnor.structure.server.config.SecureConfigItem;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.FileSystemAlreadyExistsException;

import static org.uberfire.backend.server.util.Paths.convert;

public class GitRepositoryBuilder {

    private final IOService ioService;
    private final PasswordService secureService;
    private GitRepository repo;

    public GitRepositoryBuilder(final IOService ioService,
                                final PasswordService secureService) {
        this.ioService = ioService;
        this.secureService = secureService;
    }

    public Repository build(final ConfigGroup repoConfig) {

        repo = new GitRepository(repoConfig.getName());

        if (!repo.isValid()) {
            throw new IllegalStateException("Repository " + repoConfig.getName() + " not valid");
        } else {

            addEnvironmentParameters(repoConfig.getItems());

            FileSystem fileSystem = createFileSystem(repo);

            setBranches(fileSystem);

            setPublicURIs(fileSystem);

            return repo;
        }
    }

    private void setPublicURIs(final FileSystem fileSystem) {
        final String[] uris = fileSystem.toString().split("\\r?\\n");
        final List<PublicURI> publicURIs = new ArrayList<PublicURI>(uris.length);

        for (final String s : uris) {
            final int protocolStart = s.indexOf("://");
            publicURIs.add(getPublicURI(s,
                                        protocolStart));
        }
        repo.setPublicURIs(publicURIs);
    }

    private PublicURI getPublicURI(final String s,
                                   final int protocolStart) {
        if (protocolStart > 0) {
            return new DefaultPublicURI(s.substring(0,
                                                    protocolStart),
                                        s);
        } else {
            return new DefaultPublicURI(s);
        }
    }

    private void setBranches(final FileSystem fileSystem) {
        final Map<String, Path> branches = getBranches(fileSystem);

        repo.setBranches(branches);

        repo.setRoot(getDefaultRoot(fileSystem,
                                    branches));
    }

    private void addEnvironmentParameters(final Collection<ConfigItem> items) {
        for (final ConfigItem item : items) {
            if (item instanceof SecureConfigItem) {
                repo.addEnvironmentParameter(item.getName(),
                                             secureService.decrypt(item.getValue().toString()));
            } else {
                repo.addEnvironmentParameter(item.getName(),
                                             item.getValue());
            }
        }
    }

    private org.uberfire.backend.vfs.Path getDefaultRoot(final FileSystem fileSystem,
                                                         final Map<String, org.uberfire.backend.vfs.Path> branches) {
        org.uberfire.backend.vfs.Path defaultRoot;
        if (branches.containsKey("master")) {
            defaultRoot = branches.get("master");
        } else {
            defaultRoot = convert(fileSystem.getRootDirectories().iterator().next());
        }
        return defaultRoot;
    }

    private FileSystem createFileSystem(final GitRepository repo) {
        FileSystem fs = null;
        URI uri = null;
        try {
            uri = URI.create(repo.getUri());
            fs = newFileSystem(uri);
        } catch (final FileSystemAlreadyExistsException e) {
            fs = ioService.getFileSystem(uri);
            Object replaceIfExists = repo.getEnvironment().get("replaceIfExists");
            if (replaceIfExists != null && Boolean.valueOf(replaceIfExists.toString())) {
                org.uberfire.java.nio.file.Path root = fs.getPath(null);
                ioService.delete(root);
                fs = newFileSystem(uri);
            }
        } catch (final Throwable ex) {
            throw new RuntimeException(ex.getCause().getMessage(),
                                       ex);
        }
        return fs;
    }

    private FileSystem newFileSystem(URI uri) {
        return ioService.newFileSystem(uri,
                                       new HashMap<String, Object>(repo.getEnvironment()) {{
                                           if (!repo.getEnvironment().containsKey("origin")) {
                                               put("init",
                                                   true);
                                           }
                                       }});
    }

    /**
     * collect all branches
     * @param fs
     * @return
     */
    private Map<String, org.uberfire.backend.vfs.Path> getBranches(final FileSystem fs) {
        Map<String, org.uberfire.backend.vfs.Path> branches = new HashMap<String, org.uberfire.backend.vfs.Path>();
        for (final org.uberfire.java.nio.file.Path path : fs.getRootDirectories()) {
            String gitBranch = getBranchName(path);
            branches.put(gitBranch,
                         convert(path));
        }
        return branches;
    }

    protected String getBranchName(final org.uberfire.java.nio.file.Path path) {
        URI uri = path.toUri();
        String gitBranch = uri.getAuthority();

        if (gitBranch.indexOf("@") != -1) {
            return gitBranch.split("@")[0];
        }

        return gitBranch;
    }
}
