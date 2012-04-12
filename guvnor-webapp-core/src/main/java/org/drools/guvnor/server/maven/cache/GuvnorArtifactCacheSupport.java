/*
 * Copyright 2012 JBoss Inc
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

package org.drools.guvnor.server.maven.cache;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.drools.guvnor.client.rpc.MavenArtifact;
import org.drools.guvnor.server.maven.ArtifactDependencySupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.*;
import static org.apache.commons.io.FileUtils.*;
import static org.drools.guvnor.server.maven.ArtifactDependencySupport.*;
import static org.drools.guvnor.server.maven.cache.FileDownloadUtil.*;

public class GuvnorArtifactCacheSupport {

    public static final String CLASSPATH_MAVEN_REPOSITORIES = "org/drools/guvnor/server/maven/maven-respositories.txt";
    public static final String SYSTEM_FILE_SEPARATOR = System.getProperty("file.separator");

    private static final Logger log = LoggerFactory.getLogger(GuvnorArtifactCacheSupport.class);

    private static Collection<String> mavenRepositories = null;
    private static String guvnorTempDir = null;
    private static boolean hasAlreadyBuildLocalCache = false;

    public static Collection<File> resolveArtifacts(final Collection<MavenArtifact> artifactsToBeExcluded) {

        final List<MavenArtifact> dependenciesToBeResolved = new ArrayList<MavenArtifact>(getDependencyList());
        if (artifactsToBeExcluded != null) {
            dependenciesToBeResolved.removeAll(artifactsToBeExcluded);
        }

        return resolveDependencies(dependenciesToBeResolved);
    }

    public synchronized static Collection<File> resolveDependencies(Collection<MavenArtifact> dependenciesToBeResolved) {
        checkNotNull(dependenciesToBeResolved);
        if (!hasAlreadyBuildLocalCache) {
            buildCache(getMavenRepositoryList(), getDependencyList());
        }

        final Collection<File> result = new ArrayList<File>(dependenciesToBeResolved.size());

        for (final MavenArtifact activeArtifact : dependenciesToBeResolved) {
            final File file = new File(getGuvnorTempDir() + activeArtifact.toFileName());
            if (file.exists() && file.isFile()) {
                result.add(file);
            } else {
                log.error("Can't resolve following dependency: " + activeArtifact.toFileName());
            }
        }

        return result;
    }

    public static void buildCache(final Collection<String> repositories, Collection<MavenArtifact> dependencies) {
        Set<MavenArtifact> dependenciesToBeResolved = new HashSet<MavenArtifact>(dependencies);
        for (final String activeRepository : repositories) {
            final List<MavenArtifact> missingDependencies = new ArrayList<MavenArtifact>();
            for (final MavenArtifact mavenArtifact : dependenciesToBeResolved) {
                final File file = resolveFile(mavenArtifact.toURL(activeRepository, SYSTEM_FILE_SEPARATOR), mavenArtifact.toFileName());
                if (file == null) {
                    missingDependencies.add(mavenArtifact);
                }
            }
            if (missingDependencies.size() == 0) {
                dependenciesToBeResolved.clear();
                break;
            }
            dependenciesToBeResolved = new HashSet<MavenArtifact>(missingDependencies);
        }

        for (final MavenArtifact artifact : dependenciesToBeResolved) {
            log.error("Can't resolve following artifact: " + artifact.toFileName());
        }
        hasAlreadyBuildLocalCache = true;
    }

    private static File resolveFile(final String url, final String fileName) {
        final File localFile = new File(getGuvnorTempDir() + fileName);
        if (localFile.exists() && localFile.isFile()) {
            return localFile;
        }
        return downloadFile(url, getGuvnorTempDir() + fileName);
    }

    public static synchronized Collection<String> getMavenRepositoryList() {
        if (mavenRepositories == null) {
            mavenRepositories = new LinkedHashSet<String>();
            mavenRepositories.add(getURLtoLocalUserMavenRepo());
            final BufferedReader buffer = new BufferedReader(new InputStreamReader(ArtifactDependencySupport.class.getClassLoader().getResourceAsStream(CLASSPATH_MAVEN_REPOSITORIES)));

            String strLine;
            try {
                while ((strLine = buffer.readLine()) != null) {
                    mavenRepositories.add(strLine);
                }
            } catch (IOException e) {
                log.error("Can't read following classpath resource: " + CLASSPATH_MAVEN_REPOSITORIES);
            }

        }

        return mavenRepositories;
    }

    public static String getURLtoLocalUserMavenRepo() {
        return new StringBuilder("file://")
                .append(getUserHomeDir())
                .append(SYSTEM_FILE_SEPARATOR)
                .append(".m2")
                .append(SYSTEM_FILE_SEPARATOR)
                .append("repository")
                .append(SYSTEM_FILE_SEPARATOR).toString();
    }

    public static String getUserHomeDir() {
        return System.getProperty("user.home");
    }

    public static void cleanTempDir() {
        try {
            deleteDirectory(new File(getGuvnorTempDir()));
            hasAlreadyBuildLocalCache = false;
            new File(getGuvnorTempDir()).mkdirs();
        } catch (IOException e) {
        }
    }

    public static String getGuvnorTempDir() {
        if (guvnorTempDir == null) {
            String tempdir = System.getProperty("java.io.tmpdir");

            if (!(tempdir.endsWith("/") || tempdir.endsWith("\\"))) {
                tempdir = tempdir + SYSTEM_FILE_SEPARATOR;
            }

            guvnorTempDir = tempdir + "guvnor" + SYSTEM_FILE_SEPARATOR;
            new File(guvnorTempDir).mkdirs();
        }
        return guvnorTempDir;
    }

}
